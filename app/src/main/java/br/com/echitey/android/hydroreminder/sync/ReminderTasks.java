package br.com.echitey.android.hydroreminder.sync;

import android.content.Context;

import br.com.echitey.android.hydroreminder.utilities.NotificationUtils;
import br.com.echitey.android.hydroreminder.utilities.PreferenceUtilities;

public class ReminderTasks {

    public static final String ACTION_INCREMENT_WATER_COUNT = "increment-water-count";

    public static final String ACTION_DISMISS_NOTIFICATION = "dismiss-notification";

    public static final String ACTION_CHARGING_REMINDER = "charging-reminder";


    public static void executeTask(Context context, String action){
        if(ACTION_INCREMENT_WATER_COUNT.equals(action)){
            incrementWaterCount(context);
        } else if(ACTION_DISMISS_NOTIFICATION.equals(action)){
            dismissNotification(context);
        } else if(ACTION_CHARGING_REMINDER.equals(action)){
            issueChargingReminder(context);
        }
    }

    private static void incrementWaterCount(Context context){
        PreferenceUtilities.incrementWaterCount(context);

        dismissNotification(context);
    }

    private static void dismissNotification(Context context){
        NotificationUtils.clearAllNotifications(context);
    }

    private static void issueChargingReminder(Context context){
        PreferenceUtilities.incrementChargingReminderCount(context);
        NotificationUtils.remindUserBecauseCharging(context);
    }
}
