package org.texaslinuxfest.txlf;

import android.content.*;
import android.net.Uri;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
	 
    private static final String LOG_TAG = "txlf AlarmReceiver";
 
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "Recurring alarm; requesting download service.");
        // start the download
        Intent downloader = new Intent(context, GuideDownloaderService.class);
        downloader.setData(Uri
                .parse("http://feeds.feedburner.com/MobileTuts?format=xml"));
        context.startService(downloader);
    }
 
}
