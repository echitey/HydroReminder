package br.com.echitey.android.hydroreminder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import br.com.echitey.android.hydroreminder.sync.ReminderTasks;
import br.com.echitey.android.hydroreminder.sync.ReminderUtilities;
import br.com.echitey.android.hydroreminder.sync.WaterReminderIntentService;
import br.com.echitey.android.hydroreminder.utilities.NotificationUtils;
import br.com.echitey.android.hydroreminder.utilities.PreferenceUtilities;

public class MainActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private TextView mWaterCountDisplay;
    private TextView mChargingCountDisplay;
    private ImageView mChargingImageView;

    private Toast mToast;
    private IntentFilter chargingIntentFilter;
    private ChargingBroadcastReceiver chargingBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** Get the views **/
        mWaterCountDisplay = (TextView) findViewById(R.id.tv_water_count);
        mChargingCountDisplay = (TextView) findViewById(R.id.tv_charging_reminder_count);
        mChargingImageView = (ImageView) findViewById(R.id.iv_power_increment);

        /** Set the original values in the UI **/
        updateWaterCount();
        updateChargingReminderCount();

        ReminderUtilities.scheduleChargingReminder(this);

        /** Setup the shared preference listener **/
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

        chargingIntentFilter = new IntentFilter();
        chargingIntentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        chargingIntentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);

        chargingBroadcastReceiver = new ChargingBroadcastReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // COMPLETED (2) Get a BatteryManager instance using getSystemService()
            BatteryManager batteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
            // COMPLETED (3) Call isCharging on the battery manager and pass the result on to your show
            // charging method
            showCharging(batteryManager.isCharging());
        } else {
            // sticky broadcast that contains a lot of information about the battery state.
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            // COMPLETED (6) Set a new Intent object equal to what is returned by registerReceiver, passing in null
            // for the receiver. Pass in your intent filter as well. Passing in null means that you're
            // getting the current state of a sticky broadcast - the intent returned will contain the
            // battery information you need.
            Intent currentBatteryStatusIntent = registerReceiver(null, ifilter);
            // COMPLETED (7) Get the integer extra BatteryManager.EXTRA_STATUS. Check if it matches
            // BatteryManager.BATTERY_STATUS_CHARGING or BatteryManager.BATTERY_STATUS_FULL. This means
            // the battery is currently charging.
            int batteryStatus = currentBatteryStatusIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = batteryStatus == BatteryManager.BATTERY_STATUS_CHARGING ||
                    batteryStatus == BatteryManager.BATTERY_STATUS_FULL;
            // COMPLETED (8) Update the UI using your showCharging method
            showCharging(isCharging);
        }

        registerReceiver(chargingBroadcastReceiver, chargingIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(chargingBroadcastReceiver);
    }

    /**
     * Updates the TextView to display the new water count from SharedPreferences
     */
    private void updateWaterCount() {
        int waterCount = PreferenceUtilities.getWaterCount(this);
        mWaterCountDisplay.setText(waterCount+"");
    }

    /**
     * Updates the TextView to display the new charging reminder count from SharedPreferences
     */
    private void updateChargingReminderCount() {
        int chargingReminders = PreferenceUtilities.getChargingReminderCount(this);
        String formattedChargingReminders = getResources().getQuantityString(
                R.plurals.charge_notification_count, chargingReminders, chargingReminders);
        mChargingCountDisplay.setText(formattedChargingReminders);

    }

    /**
     * Adds one to the water count and shows a toast
     */
    public void incrementWater(View view) {
        if (mToast != null) mToast.cancel();
        mToast = Toast.makeText(this, R.string.water_chug_toast, Toast.LENGTH_SHORT);
        mToast.show();

        Intent intent = new Intent(this, WaterReminderIntentService.class);
        intent.setAction(ReminderTasks.ACTION_INCREMENT_WATER_COUNT);
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /** Cleanup the shared preference listener **/
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * This is a listener that will update the UI when the water count or charging reminder counts
     * change
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (PreferenceUtilities.KEY_WATER_COUNT.equals(key)) {
            updateWaterCount();
        } else if (PreferenceUtilities.KEY_CHARGING_REMINDER_COUNT.equals(key)) {
            updateChargingReminderCount();
        }
    }

    public void testNotification(View view) {
        NotificationUtils.remindUserBecauseCharging(this);
    }

    private void showCharging(boolean isCharging){
        if(isCharging){
            mChargingImageView.setImageResource(R.drawable.ic_power_pink_80px);
        } else {
            mChargingImageView.setImageResource(R.drawable.ic_power_grey_80px);
        }
    }

    private class ChargingBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            boolean isCharging = action.equals(Intent.ACTION_POWER_CONNECTED);
            showCharging(isCharging);
        }
    }
}