package kulkarni.aditya.materialnews.network;

import android.content.Context;
import androidx.annotation.NonNull;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

/**
 * Created by maverick on 5/16/18.
 */

public class ScheduleServiceHelper {

    private static final int SYNC_INTERVAL_MINUTES = 120;
    private static final int SYNC_INTERVAL_SECONDS = (int) (TimeUnit.MINUTES.toSeconds(SYNC_INTERVAL_MINUTES));
    private static final int SYNC_FLEXTIME_MINUTES = 10;
    private static final int SYNC_FLEXTIME_SECONDS = (int) (TimeUnit.MINUTES.toSeconds(SYNC_FLEXTIME_MINUTES));

    private static final String REMINDER_JOB_TAG = "background_sync";

    private static boolean sInitialized;

    synchronized public static void scheduleBackgroundSync(@NonNull final Context context) {

        if (sInitialized) return;

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Job constraintReminderJob = dispatcher.newJobBuilder()
                .setService(BackgroundSyncJobService.class)
                .setTag(REMINDER_JOB_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();

        dispatcher.schedule(constraintReminderJob);
        sInitialized = true;
    }

}
