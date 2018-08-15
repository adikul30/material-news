package kulkarni.aditya.materialnews.network;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import kulkarni.aditya.materialnews.R;
import kulkarni.aditya.materialnews.activities.Home;
import kulkarni.aditya.materialnews.data.AppExecutor;
import kulkarni.aditya.materialnews.data.DatabaseRoom;
import kulkarni.aditya.materialnews.model.NewsArticle;
import kulkarni.aditya.materialnews.model.NewsResponse;
import kulkarni.aditya.materialnews.model.Sources;
import retrofit2.Call;
import retrofit2.Callback;

import static android.support.v4.app.NotificationCompat.VISIBILITY_PUBLIC;

/**
 * Created by maverick on 5/16/18.
 */

public class BackgroundSyncJobService extends JobService {

    private List<Sources> sourceArrayList;
    private List<NewsArticle> newsArticleArrayList = new ArrayList<>();
    private List<NewsArticle> tempList;
    StringBuilder sourcesString = new StringBuilder();
    private AsyncTask mGetSourcesTask;
    public static final String PRIMARY_CHANNEL = "default";
    public static final String SOURCES = "sources";
    public static final String COUNTS = "counts";
    private DatabaseRoom mDb;
    private FirebaseAnalytics mFirebaseAnalytics;
    Bundle bundle;
    int currentCount;

    @Override
    public boolean onStartJob(JobParameters job) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        initNotification();
        mDb = DatabaseRoom.getsInstance(this);

        AppExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                sourceArrayList = new ArrayList<>();
                sourceArrayList = mDb.sourcesDao().getSourcesList();
                for (int i = 0; i < sourceArrayList.size(); i++) {
                    sourcesString.append(sourceArrayList.get(i).getSource());
                    sourcesString.append(",");
                }
                bundle = new Bundle();
                bundle.putString("sources_string", sourcesString.toString());
                mFirebaseAnalytics.logEvent(SOURCES, bundle);
                fetchNews();
            }
        });

        return true;
    }

    private void initNotification() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Primary Channel";
            String description = "News";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(PRIMARY_CHANNEL, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public boolean onStopJob(JobParameters job) {
//        if (mGetSourcesTask != null) {
//            mGetSourcesTask.cancel(true);
//        }
        return true;
    }

    private void fetchNews() {
        APIClient apiClient = RetrofitInstance.getRetrofitInstance().create(APIClient.class);

        Call<NewsResponse> call = apiClient.getTopHeadlines(sourcesString.toString());

        call.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(@NonNull Call<NewsResponse> call, @NonNull retrofit2.Response<NewsResponse> response) {

                NewsResponse newsResponse = response.body();
                tempList = new ArrayList<>();

                if (newsResponse != null) {

                    int previousCount = mDb.newsDao().getNewsCount();
                    tempList = newsResponse.getNewsArticleList();

                    if (tempList.size() != 0) {
                        for (NewsArticle news : tempList) {
                            mDb.newsDao().addNews(news);
                        }
                        currentCount = mDb.newsDao().getNewsCount();
                        getUnread(previousCount);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<NewsResponse> call, @NonNull Throwable t) {
                Log.d("failure", t.toString() + "");
            }
        });
    }

    private void getUnread(final int previousCount) {

        final int difference = currentCount - previousCount;
        bundle = new Bundle();
        bundle.putString("currentCount", String.valueOf(currentCount));
        bundle.putString("previousCount", String.valueOf(previousCount));
        bundle.putString("difference", String.valueOf(difference));
        mFirebaseAnalytics.logEvent(COUNTS, bundle);

        if (difference > 0) {

            AppExecutor.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    newsArticleArrayList = new ArrayList<>();
                    newsArticleArrayList = mDb.newsDao().getTopNRows(difference);
                    sendNotification(difference);
                }
            });

        }
    }

    private void sendNotification(int count) {
        bundle = new Bundle();
        bundle.putString("notif_count", String.valueOf(count));
        bundle.putString("notif_list_size", String.valueOf(newsArticleArrayList.size()));
        mFirebaseAnalytics.logEvent(COUNTS, bundle);

        //Notification setup
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(BackgroundSyncJobService.this, PRIMARY_CHANNEL)
                .setSmallIcon(R.drawable.ic_icons8_google_news)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentTitle(count + " new notifications")
                .setVisibility(VISIBILITY_PUBLIC)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        for (int i = 0; i < count; i++) {
            inboxStyle.addLine(newsArticleArrayList.get(i).getTitle());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mBuilder.setColor(getColor(R.color.blue_500));
        }
        mBuilder.setStyle(inboxStyle);

        //PendingIntent setup
        Intent activityFromNotification = new Intent(BackgroundSyncJobService.this, Home.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(BackgroundSyncJobService.this);
        stackBuilder.addNextIntentWithParentStack(activityFromNotification);
        PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(notificationPendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(BackgroundSyncJobService.this);
        notificationManager.notify(5468, mBuilder.build());
    }
}
