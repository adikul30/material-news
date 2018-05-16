package kulkarni.aditya.materialnews.Util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.util.ArrayList;

import kulkarni.aditya.materialnews.Model.NewsResponse;
import kulkarni.aditya.materialnews.Model.Sources;
import kulkarni.aditya.materialnews.R;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by maverick on 5/16/18.
 */

public class BackgroundSyncJobService extends JobService {
    private NewsSQLite newsSQLite;
    private ArrayList<Sources> sourceArrayList;
    StringBuilder sourcesString = new StringBuilder();
    private AsyncTask mBackgroundTask;
    public static final String PRIMARY_CHANNEL = "default";

    @Override
    public boolean onStartJob(JobParameters job) {
        initNotification();
        newsSQLite = new NewsSQLite(this);
        mBackgroundTask = new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] objects) {
                sourceArrayList = new ArrayList<>();
                sourceArrayList = newsSQLite.getAllSources();
                for (int i = 0; i < sourceArrayList.size(); i++) {
                    sourcesString.append(sourceArrayList.get(i).getSource());
                    sourcesString.append(",");
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                networkCall();
            }
        };
        mBackgroundTask.execute();
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
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (mBackgroundTask != null) mBackgroundTask.cancel(true);
        return true;
    }

    private void networkCall(){
        APIClient apiClient = RetrofitInstance.getRetrofitInstance().create(APIClient.class);

        Call<NewsResponse> call = apiClient.getTopHeadlines(sourcesString.toString());

        call.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(@NonNull Call<NewsResponse> call, @NonNull retrofit2.Response<NewsResponse> response) {
                Log.d("Service","hi there");
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(BackgroundSyncJobService.this, PRIMARY_CHANNEL)
                        .setSmallIcon(R.drawable.newspaper_icon)
                        .setContentTitle("hi")
                        .setContentText("hi")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(BackgroundSyncJobService.this);

                notificationManager.notify(34653, mBuilder.build());
                NewsResponse newsResponse = response.body();
                newsSQLite.addAllNews(newsResponse.getNewsArticleList());
            }

            @Override
            public void onFailure(@NonNull Call<NewsResponse> call, @NonNull Throwable t) {
                Log.d("failure", t.toString()+"");
            }
        });
    }
}
