package org.texaslinuxfest.txlf;

import java.util.Calendar;
import java.util.TimeZone;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.*;
import android.util.Log;
import static org.texaslinuxfest.txlf.Constants.GUIDE_UPDATE_HOUR;
import static org.texaslinuxfest.txlf.Constants.GUIDE_UPDATE_MIN;

public class AlarmReceiver extends BroadcastReceiver {
	 
    private static final String LOG_TAG = "txlf AlarmReceiver";
 
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "Recurring alarm; requesting download service.");
        // start the download
        Intent downloader = new Intent(context, GuideDownloaderService.class);
        context.startService(downloader);
    }
    
    public static void cancelRecurringAlarm(Context context) {
    	// Cancel alarms pointing to this receiver
        Intent downloader = new Intent(context, AlarmReceiver.class);
        PendingIntent recurringDownload = PendingIntent.getBroadcast(context,
                0, downloader, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarms = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        alarms.cancel(recurringDownload);
    }
    
    public static void setRecurringAlarm(Context context) {
    	// sets alarm to be used by this receiver for daily updates
    	Calendar updateTime = Calendar.getInstance();
        updateTime.setTimeZone(TimeZone.getTimeZone("GMT"));
        updateTime.set(Calendar.HOUR_OF_DAY, GUIDE_UPDATE_HOUR);
        updateTime.set(Calendar.MINUTE, GUIDE_UPDATE_MIN);
     
        Intent downloader = new Intent(context, AlarmReceiver.class);
        PendingIntent recurringDownload = PendingIntent.getBroadcast(context,
                0, downloader, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarms.setInexactRepeating(AlarmManager.RTC,
                updateTime.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, recurringDownload);
    }
 
}
