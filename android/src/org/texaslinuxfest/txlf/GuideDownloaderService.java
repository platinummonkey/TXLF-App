package org.texaslinuxfest.txlf;

import static org.texaslinuxfest.txlf.Constants.GUIDEFILE;
import static org.texaslinuxfest.txlf.Constants.GUIDEURL;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.json.*;
import android.app.Service;
import android.content.*;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class GuideDownloaderService extends Service {

	private static final String LOG_TAG = "txlf_GuideDownloaderService";
	public static final String BROADCAST_ACTION = "org.texaslinuxfest.txlf.GuideUpdate";
	private final Handler handler = new Handler();
	Intent intent;
	private boolean guideUpdated = false;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(LOG_TAG,"GuideDownloaderService created");
		intent = new Intent(BROADCAST_ACTION);
	}
	
	@Override
	public void onStart(Intent intent, int startid) {
		Toast.makeText(this, "Checking Guide", Toast.LENGTH_LONG).show();
		// Start up the thread running the service.  Note that we create a
		// separate thread because the service normally runs in the process's
		// main thread, which we don't want to block.
		Thread thr = new Thread(null, dTask, "GuideDownloaderService");
		thr.start();
		handler.removeCallbacks(sendUpdatesToUI);
		handler.post(sendUpdatesToUI);
	}
	
	Runnable sendUpdatesToUI = new Runnable() {
		public void run() {
			if (guideUpdated) {
				updateUI();
			} else {
				handler.postDelayed(this, 1000);
			}
		}
	};
	private void updateUI() {
		Log.d(LOG_TAG,"Broadcasting Intent to update display");
		intent.putExtra("GuideDownloadStatus","Guide has been updated");
		sendBroadcast(intent);
	}
	
	Runnable dTask = new Runnable() {
        public void run() {
        	Log.d(LOG_TAG,"Starting dTask runnable -- attempting to update guide");
            // perform the download and update the internal guide
        	checkAndUpdateGuide();
        	guideUpdated = true;
            // Done with our work...  stop the service!
            GuideDownloaderService.this.stopSelf();
        }
    };
    
    public void checkAndUpdateGuide() {
    	// try to open guide on disk
    	try {
    		FileInputStream fis = openFileInput(GUIDEFILE);
    		ObjectInputStream in = new ObjectInputStream(fis);
    		Guide guide = (Guide) in.readObject();
    		in.close();
    		// Check if guide has expired
    		Date now = new Date();
    		if (now.after(guide.expires)) {
    			// guide has expired - need to update
    			updateGuide();
    		}
    	} catch (IOException e) {
    		Log.e(LOG_TAG, "Coudln't find guide - IOerror");
    		e.printStackTrace();
    		updateGuide(); // try to update guide
    	} catch (Exception e) {
    		Log.e(LOG_TAG, "Couldn't find guide -other exception");
    		e.printStackTrace();
    		updateGuide(); // try to update guide
    	}
    }
    
    public void updateGuide() {
    	// Call HTTP Get on file from web, then write to internal
    	//     storage. - minimize web hits
    	String guidetext = getProgramGuide(); // get String
    	Log.i(LOG_TAG, "Retrieved Guide");
    	FileOutputStream fos;
    	FileOutputStream foso; 
    	try {
    		Log.i(LOG_TAG, "Writing guide JSON file to internal storage");
    		fos = openFileOutput(GUIDEFILE+".json", Context.MODE_PRIVATE);
    		fos.write(guidetext.getBytes());
    		fos.close();
    		Log.v(LOG_TAG, "Wrote guide JSON file to internal storage, now trying Object Serialization Write");
    		
    		foso = openFileOutput(GUIDEFILE, Context.MODE_PRIVATE);
    		ObjectOutput out = new ObjectOutputStream(foso);
    		
    		JSONObject jguide = new JSONObject(guidetext);
    		String year = jguide.getString("year");
    		Date expires = convertStringToDate(jguide.getString("expires"));
    		
    		// create object
    		Guide guide = new Guide(year, expires);
    		
    		// go through sessions
    		String sessionstext = jguide.getString("sessions");
    		JSONArray jsessions = new JSONArray(sessionstext);
    		int i;
    		for (i=0;i<jsessions.length();i++) {
    			JSONObject jsession = jsessions.getJSONObject(i);
    			Integer day = Integer.parseInt(jsession.getString("day"));
    			Integer track = Integer.parseInt(jsession.getString("track"));
    			Date time = convertStringToDate(jsession.getString("time"));
    			Date endTime = convertStringToDate(jsession.getString("end_time"));
    			String speaker = jsession.getString("speaker");
    			String title = jsession.getString("title");
    			String summary = jsession.getString("summary");
    			guide.addSession(day, track, time, endTime, speaker, title, summary);
    		}
    		
    		// write object to storage
    		Log.v(LOG_TAG, "Attempting to write serialized object to file");
    		out.writeObject(guide);
    		out.flush();
    		out.close();
    		Log.v(LOG_TAG, "Wrote serialized object to file");
    		
    	} catch (IOException e) {
    		Log.e(LOG_TAG, "Error Writing guide file to internal storage");
    		e.printStackTrace();
    	} catch (Exception e) {
    		Log.e(LOG_TAG, "Other error");
    		e.printStackTrace();
    	}
    }
    
    public Date convertStringToDate(String dateString) {
    	// check json expiry date against today
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Date date;
		try {
			date = format.parse(dateString);
		} catch (ParseException e) {
			Log.e(LOG_TAG, "Error parsing expire date");
			e.printStackTrace();
			date = new Date();
		}
		return date;
    }
    
    /*public Boolean checkGuideExpiration() {
    	try {
    		// open file
    		InputStream is = openFileInput(GUIDEFILE+".json");
    		byte [] buffer = new byte[is.available()];
    		while (is.read(buffer) != -1);
    		String istext = new String(buffer);
    		// parse json
    		JSONObject guide = new JSONObject(istext);
    		String expires = guide.getString("expires");
    		// check json expiry date against today
    		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        	Date now = new Date();
        	Date expireTime;
    		try {
    			expireTime = format.parse(expires);
    		} catch (ParseException e) {
    			Log.e(LOG_TAG, "Error parsing expire date");
    			e.printStackTrace();
    			expireTime = new Date();
    		}
    		// check if its expired
        	if (now.after(expireTime)) {
        		Log.i(LOG_TAG, "guide has expired");
        		return false;
        	} else {
        		Log.i(LOG_TAG, "guide has NOT expired");
        		return true;
        	}
    	} catch (Exception e) {
    		// invalid json file, or some badjuju happened
    		Log.e(LOG_TAG, "Error loading guide file");
    		e.printStackTrace();
    		return false;
    	}
    }*/
    
    public String getProgramGuide() {
    	Log.d(LOG_TAG, "Attempting to download GUIDE");
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(GUIDEURL);
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			} else {
				Log.e(LOG_TAG, "Failed to download file");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return builder.toString();
	}
    
}
