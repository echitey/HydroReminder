package br.com.echitey.android.hydroreminder.sync;

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

public class ReminderUtilities {

    private static final int REMINDER_INTERVAL_MINUTES = 120;
    private static final int REMINDER_INTERVAL_SECONDS = (int) (TimeUnit.MINUTES.toSeconds(REMINDER_INTERVAL_MINUTES));
    public static final int SYNC_FLEXTIME_SECONDS = REMINDER_INTERVAL_SECONDS;

    private static final String REMINDER_JOB_TAG = "hydration_reminder_tag";

    private static boolean initialized;

    synchronized public static void scheduleChargingReminder(@NonNull final Context context){
        if(initialized) return;

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Job reminderJob = dispatcher.newJobBuilder()
                .setService(WaterReminderFirebaseJobService.class)
                .setTag(REMINDER_JOB_TAG)
                .setConstraints(Constraint.DEVICE_CHARGING)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        REMINDER_INTERVAL_SECONDS,
                        REMINDER_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS
                ))
                .setReplaceCurrent(true)
                .build();

        dispatcher.schedule(reminderJob);
        initialized = true;
    }
}
