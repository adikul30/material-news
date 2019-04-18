package kulkarni.aditya.materialnews.network;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import kulkarni.aditya.materialnews.R;
import kulkarni.aditya.materialnews.activities.Home;
import kulkarni.aditya.materialnews.data.AppExecutor;
import kulkarni.aditya.materialnews.data.DatabaseRoom;
import kulkarni.aditya.materialnews.model.NewsArticle;
import kulkarni.aditya.materialnews.model.NewsResponse;
import kulkarni.aditya.materialnews.model.Sources;
import kulkarni.aditya.materialnews.util.Constants;
import retrofit2.Call;
import retrofit2.Callback;

import static androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC;


/**
 * Created by maverick on 5/16/18.
 */

public class BackgroundSyncJobService extends JobService {

    public static final String PRIMARY_CHANNEL = "default";
    public static final String SOURCES = "sources";
    public static final String COUNTS = "counts";
    StringBuilder sourcesString = new StringBuilder();
    Bundle bundle;
    Context mContext;
    int currentCount;
    private List<Sources> sourceArrayList;
    private List<NewsArticle> newsArticleArrayList = new ArrayList<>();
    private List<NewsArticle> tempList;
    private DatabaseRoom mDb;

    @Override
    public boolean onStartJob(JobParameters job) {
        mContext = this;
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

                final NewsResponse newsResponse = response.body();
                tempList = new ArrayList<>();

                if (newsResponse != null) {
                    AppExecutor.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
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
                    });
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
        for (int i = 0; i < count && i < 5; i++) {
            loadImage(newsArticleArrayList.get(i));
        }
    }

    private void loadImage(NewsArticle newsArticle) {

        //Notification setup

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(BackgroundSyncJobService.this, PRIMARY_CHANNEL)
                .setSmallIcon(R.drawable.ic_icons8_google_news)
                .setDefaults(Notification.DEFAULT_ALL)
                .setVisibility(VISIBILITY_PUBLIC)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        String miscTitle = newsArticle.getTitle();
        String miscContent = newsArticle.getDescription();
        String trimmedMsg;
        if (miscContent.length() > 50) {
            trimmedMsg = miscContent.substring(0, 50) + "...";
        } else {
            trimmedMsg = miscContent;
        }

        mBuilder.setContentTitle(Html.fromHtml(miscTitle))
                .setContentText(trimmedMsg);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mBuilder.setColor(getColor(R.color.blue_500));
        }

        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = () -> Glide.with(getApplicationContext())
                .asBitmap()
                .load(newsArticle.getUrlToImage())
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        e.printStackTrace();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        generateNotification(
                                mBuilder.setStyle(
                                        new NotificationCompat.BigPictureStyle()
                                                .bigLargeIcon(null)
                                                .bigPicture(resource)
                                ).setLargeIcon(resource), newsArticle.getUrl()
                        );
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        generateNotification(
                                mBuilder.setStyle(
                                        new NotificationCompat.InboxStyle().addLine(newsArticle.getTitle())
                                ), newsArticle.getUrl());
                    }
                });

        handler.post(runnable);
    }

    private void generateNotification(NotificationCompat.Builder mBuilder, String url) {

        //PendingIntent setup
        Intent activityFromNotification = new Intent(BackgroundSyncJobService.this, Home.class);
        activityFromNotification.putExtra(Constants.URL, url);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(BackgroundSyncJobService.this);
        stackBuilder.addNextIntentWithParentStack(activityFromNotification);
        int pendingIntentId = new Random().nextInt(543254);
        PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(pendingIntentId, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(notificationPendingIntent);

        int notificationId = new Random().nextInt(543254);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(BackgroundSyncJobService.this);
        notificationManager.notify(getResources().getString(R.string.package_name), notificationId, mBuilder.build());
    }
}
