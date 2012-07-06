package org.texaslinuxfest.txlfapp;

import static org.texaslinuxfest.txlfapp.Constants.GUIDEFILE;
import static org.texaslinuxfest.txlfapp.Constants.GUIDETYPE;
import static org.texaslinuxfest.txlfapp.Constants.GUIDEURL;

import java.io.*;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.json.*;
import android.app.Service;
import android.content.*;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class GuideDownloaderService extends Service {

	private static final String LOG_TAG = "GuideDownloaderService";
	public static final String BROADCAST_ACTION = "org.texaslinuxfest.txlf.GuideUpdate";
	private final Handler handler = new Handler();
	Intent intent;
	private boolean guideUpdated = false;
	Guide guide;
	
	@Override
	public IBinder onBind(Intent intent) {
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
		Bundle b = this.intent.getExtras();
		if (b!=null) {
			Thread thr = new Thread(null, dTask2, "GuideDownloaderService");
			thr.start();
		} else {
		Toast.makeText(this, "Checking Guide", Toast.LENGTH_LONG).show();
		// Start up the thread running the service.  Note that we create a
		// separate thread because the service normally runs in the process's
		// main thread, which we don't want to block.
		Thread thr = new Thread(null, dTask, "GuideDownloaderService");
		thr.start();
		}
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
		Bundle b = new Bundle();
		b.putSerializable(GUIDETYPE, this.guide);
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
    Runnable dTask2 = new Runnable() {
        public void run() {
        	Log.d(LOG_TAG,"Starting dTask2 runnable -- attempting to force update guide");
            // perform the download and update the internal guide
        	updateGuide();
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
    		this.guide = (Guide) in.readObject();
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
    		this.guide = new Guide(year, expires);
    		
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
    			String speakerImage = jsession.getString("speakerImage");
    			String title = jsession.getString("title");
    			String summary = jsession.getString("summary");
    			guide.addSession(day, track, time, endTime, speaker, speakerImage, title, summary);
    		}
    		
    		// go through sponsors
    		String sponsorstext = jguide.getString("sponsors");
    		JSONArray jsponsors = new JSONArray(sponsorstext);
    		for (i=0;i<jsponsors.length();i++) {
    			JSONObject jsponsor = jsponsors.getJSONObject(i);
    			String organization = jsponsor.getString("organization");
    			Integer sponsorLevel = Integer.parseInt(jsponsor.getString("sponsorLevel"));
    			String website = jsponsor.getString("website");
    			String logo = jsponsor.getString("logo");
    			Integer order = Integer.parseInt(jsponsor.getString("order"));
    			String summary = jsponsor.getString("summary");
    			Boolean booth = Boolean.parseBoolean(jsponsor.getString("boothAvail"));
    			guide.addSponsor(organization, sponsorLevel, order, summary, logo, website, booth);
    		}
    		
    		// venue
    		JSONObject jvenue = jguide.getJSONObject("venue");
    		String venueName = jvenue.getString("name");
    		String venueAddress = jvenue.getString("address");
    		Integer venueZipcode = Integer.parseInt(jvenue.getString("zipcode"));
    		String venueCity = jvenue.getString("city");
    		URI venuemap = URI.create(jvenue.getString("map"));
    		String vmapstext = jvenue.getString("vmaps");
    		JSONArray jvmaps = new JSONArray(vmapstext);
    		ArrayList<String> vmaps = new ArrayList<String>();
    		for (i=0;i<jvmaps.length();i++) {
    			vmaps.add(jvmaps.getString(i));
    		}
    		guide.setVenue(venueName, venueAddress, venueZipcode, venueCity, venuemap, vmaps);
    		
    		// afterparty
    		JSONObject jap = jguide.getJSONObject("afterparty");
    		String apName = jap.getString("name");
    		String apAddress = jap.getString("address");
    		Integer apZipcode = Integer.parseInt(jap.getString("zipcode"));
    		String apCity = jap.getString("city");
    		URI apMap = URI.create(jap.getString("map"));
    		guide.setAfterparty(apName, apAddress, apZipcode, apCity, apMap);
    		
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
