package org.texaslinuxfest.txlfapp;

import static org.texaslinuxfest.txlfapp.Constants.GUIDEFILE;
import static org.texaslinuxfest.txlfapp.Constants.MEDIAURL;
import org.texaslinuxfest.txlfapp.Guide;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.app.Service;
import android.content.*;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.util.Log;

public class ImageDownloaderService extends Service {

	private static final String LOG_TAG = "ImageDownloaderService";
	
	private Guide guide;
	
	
	// cache dir
	private File cacheDir;
	private List<String> imagesRequired;
	private List<String> imagesToDownload = new ArrayList<String>();
	private List<String> imagesDownloaded;
	
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(LOG_TAG,"ImageDownloaderService created");
	}
	
	@Override
	public void onStart(Intent intent, int startid) {
		// try to open guide on disk
    	try {
    		FileInputStream fis = openFileInput(GUIDEFILE);
    		ObjectInputStream in = new ObjectInputStream(fis);
    		this.guide = (Guide) in.readObject();
    		in.close();
    		Log.d(LOG_TAG,"Got guide");
			Log.d(LOG_TAG,"Got guide: " + guide.getYear());
			cacheDir = this.getCacheDir();
			Thread thr = new Thread(null, dTask, "ImageDownloaderService");
			thr.start();
    	} catch (IOException e) {
    		Log.e(LOG_TAG, "Coudln't find guide - IOerror");
    		e.printStackTrace();
    	} catch (Exception e) {
    		Log.e(LOG_TAG, "Couldn't find guide -other exception");
    		e.printStackTrace();
    	}
	}
	
	
    Runnable dTask = new Runnable() {
        public void run() {
        	Log.d(LOG_TAG,"Starting dTask runnable -- attempting to download images");
            // perform the download and update the internal guide
        	checkAndUpdateImages();
            // Done with our work...  stop the service!
            ImageDownloaderService.this.stopSelf();
        }
    };
    
    public void checkAndUpdateImages() {
    	Log.d(LOG_TAG,"checking and updating images: " + guide.getYear());
    	imagesRequired = guide.getImagesToDownload();
    	Log.d(LOG_TAG,"Got images to download");
    	imagesDownloaded = Arrays.asList(cacheDir.list());
    	Log.d(LOG_TAG,"Got cached image list");
    	String imreq;
    	for (int i = 0; i < imagesRequired.size(); i++) {
    		imreq = imagesRequired.get(i);
    		if (imagesDownloaded.contains(imreq)) {
    			// already downloaded
    			Log.d(LOG_TAG,"Already have cached image: " + imreq);
    		} else {
    			// contains images needed to be downloaded
    			Log.d(LOG_TAG, "Need to download image: " + imreq);
    			imagesToDownload.add(imreq);
    		}
    	}
    	// contains leftover cached files that need to be deleted.
    	try {
    		imagesDownloaded.removeAll(imagesRequired);
    		// delete leftover cache files
        	for (int i = 0; i < imagesDownloaded.size(); i++) {
        		File file = new File(cacheDir, imagesDownloaded.get(i));
        		if (file.delete()) {
        			Log.d(LOG_TAG, "File deleted: " + imagesDownloaded.get(i));
        		} else {
        			Log.d(LOG_TAG, "File NOT deleted: " + imagesDownloaded.get(i));
        		}
        	}
    	} catch (Exception e) {
    		// there are no images to remove
    		e.printStackTrace();
    	}
    	// download images
    	for (int i = 0; i < imagesToDownload.size(); i++) {
    		Log.d(LOG_TAG, "Attempting to download: " + imagesToDownload.get(i));
    		downloadAndSave(imagesToDownload.get(i));
    	}
    }
    
    public void downloadAndSave(String image) {
    	try {
    		URL url = new URL(MEDIAURL+image);
    		File file = new File(this.cacheDir, image);
    		Bitmap bitmap = BitmapFactory.decodeStream(url.openStream());
    		bitmap.compress(CompressFormat.PNG, 90, new FileOutputStream(file));
    		Log.d(LOG_TAG, "Successfully downloaded: " + MEDIAURL+image);
    	} catch (Exception e) {
    		e.printStackTrace();
    	};
    }    
}

