package kulkarni.aditya.materialnews.network;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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

import kulkarni.aditya.materialnews.R;
import kulkarni.aditya.materialnews.data.NewsSQLite;
import kulkarni.aditya.materialnews.model.NewsArticle;
import kulkarni.aditya.materialnews.model.NewsResponse;
import kulkarni.aditya.materialnews.model.Sources;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by maverick on 5/16/18.
 */

public class BackgroundSyncJobService extends JobService {
    private NewsSQLite newsSQLite;
    private ArrayList<Sources> sourceArrayList;
    private ArrayList<NewsArticle> newsArticleArrayList = new ArrayList<>();
    StringBuilder sourcesString = new StringBuilder();
    private AsyncTask mGetSourcesTask;
    private AsyncTask mGetTopNewsTask;
    public static final String PRIMARY_CHANNEL = "default";
    public static final String SOURCES = "sources";
    public static final String COUNTS = "counts";
    private FirebaseAnalytics mFirebaseAnalytics;
    Bundle bundle;
    @Override
    public boolean onStartJob(JobParameters job) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        initNotification();
        newsSQLite = new NewsSQLite(this);
        mGetSourcesTask = new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] objects) {
                sourceArrayList = new ArrayList<>();
                sourceArrayList = newsSQLite.getAllSources();
                for (int i = 0; i < sourceArrayList.size(); i++) {
                    sourcesString.append(sourceArrayList.get(i).getSource());
                    sourcesString.append(",");
                }
                bundle = new Bundle();
                bundle.putString("sources_string",sourcesString.toString());
                mFirebaseAnalytics.logEvent(SOURCES, bundle);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                networkCall();
            }
        };
        mGetSourcesTask.execute();
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
        if (mGetSourcesTask != null) {
            mGetSourcesTask.cancel(true);
        }
        return true;
    }

    private void networkCall() {
        APIClient apiClient = RetrofitInstance.getRetrofitInstance().create(APIClient.class);

        Call<NewsResponse> call = apiClient.getTopHeadlines(sourcesString.toString());

        call.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(@NonNull Call<NewsResponse> call, @NonNull retrofit2.Response<NewsResponse> response) {

                NewsResponse newsResponse = response.body();
                if (newsResponse != null) {
                    int previousCount = newsSQLite.getNewsCount();
                    newsSQLite.addAllNews(newsResponse.getNewsArticleList());
                    getUnread(previousCount);
                }
            }

            @Override
            public void onFailure(@NonNull Call<NewsResponse> call, @NonNull Throwable t) {
                Log.d("failure", t.toString() + "");
            }
        });
    }

    private void getUnread(final int previousCount) {
        int currentCount = newsSQLite.getNewsCount();
        final int difference = currentCount - previousCount;
        bundle = new Bundle();
        bundle.putString("currentCount", String.valueOf(currentCount));
        bundle.putString("previousCount", String.valueOf(previousCount));
        bundle.putString("difference", String.valueOf(difference));
        mFirebaseAnalytics.logEvent(COUNTS,bundle);
        if (difference > 0) {
            mGetTopNewsTask = new AsyncTask() {

                @Override
                protected Object doInBackground(Object[] objects) {
                    newsArticleArrayList = new ArrayList<>();
                    newsArticleArrayList = newsSQLite.getTopNRows(difference);
                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    sendNotification(difference);
                }
            };
            mGetTopNewsTask.execute();
        }
    }

    private void sendNotification(int count){
        bundle = new Bundle();
        bundle.putString("notif_count", String.valueOf(count));
        bundle.putString("notif_list_size", String.valueOf(newsArticleArrayList.size()));
        mFirebaseAnalytics.logEvent(COUNTS,bundle);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(BackgroundSyncJobService.this, PRIMARY_CHANNEL)
                .setSmallIcon(R.drawable.ic_icons8_google_news)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentTitle(count + " new notifications")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        for (int i = 0; i < count; i++){
            inboxStyle.addLine(newsArticleArrayList.get(i).getTitle());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mBuilder.setColor(getColor(R.color.blue_500));
        }
        mBuilder.setStyle(inboxStyle);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(BackgroundSyncJobService.this);
        notificationManager.notify(5468, mBuilder.build());
    }
}
