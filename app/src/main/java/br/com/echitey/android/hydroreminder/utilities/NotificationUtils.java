package br.com.echitey.android.hydroreminder.utilities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import br.com.echitey.android.hydroreminder.MainActivity;
import br.com.echitey.android.hydroreminder.R;

public class NotificationUtils {

    /*
     * This notification ID can be used to access our notification after we've displayed it. This
     * can be handy when we need to cancel the notification, or perhaps update it. This number is
     * arbitrary and can be set to whatever you like. 1138 is in no way significant.
     */
    private static final int WATER_REMINDER_NOTIFICATION_ID = 1138;
    /**
     * This pending intent id is used to uniquely reference the pending intent
     */
    private static final int WATER_REMINDER_PENDING_INTENT_ID = 3417;
    /**
     * This notification channel id is used to link notifications to this channel
     */
    private static final String WATER_REMINDER_NOTIFICATION_CHANNEL_ID = "reminder_notification_channel";


    private static PendingIntent contentIntent(Context context){
        Intent intent = new Intent(context, MainActivity.class);

        return PendingIntent.getActivity(context,
                                        WATER_REMINDER_PENDING_INTENT_ID,
                                        intent,
                                        PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static Bitmap largeIcon(Context context){
        Resources res = context.getResources();
        Bitmap largeIcon = BitmapFactory.decodeResource(res, R.drawable.ic_local_drink_black_24px);
        return largeIcon;
    }

    public static void remindUserBecauseCharging(Context context){
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(
                    WATER_REMINDER_NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.main_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH
            );

            notificationManager.createNotificationChannel(channel);
        }

        // - has a color of R.color.colorPrimary - use ContextCompat.getColor to get a compatible color
        // - has ic_drink_notification as the small icon
        // - uses icon returned by the largeIcon helper method as the large icon
        // - sets the title to the charging_reminder_notification_title String resource
        // - sets the text to the charging_reminder_notification_body String resource
        // - sets the style to NotificationCompat.BigTextStyle().bigText(text)
        // - sets the notification defaults to vibrate
        // - uses the content intent returned by the contentIntent helper method for the contentIntent
        // - automatically cancels the notification when the notification is clicked
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, WATER_REMINDER_NOTIFICATION_CHANNEL_ID)
                        .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                        .setSmallIcon(R.drawable.ic_local_drink_grey_120px)
                        .setLargeIcon(largeIcon(context))
                        .setContentTitle(context.getString(R.string.charging_reminder_notification_title))
                        .setContentText(context.getString(R.string.charging_reminder_notification_body))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(
                                context.getString(R.string.charging_reminder_notification_body)))
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .setContentIntent(contentIntent(context))
                        .setAutoCancel(true);

        // set the notification's priority to PRIORITY_HIGH.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        // Pass in a unique ID of your choosing for the notification and notificationBuilder.build()
        notificationManager.notify(WATER_REMINDER_NOTIFICATION_ID, builder.build());

    }
}
