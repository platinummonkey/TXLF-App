package org.texaslinuxfest.txlf;

import java.io.*;
import java.text.*;
import java.util.*;
import org.json.*;
import android.app.*;
import android.content.*;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.util.Log;
import static org.texaslinuxfest.txlf.Constants.*;
import org.texaslinuxfest.txlf.Guide;
import org.texaslinuxfest.txlf.AlarmReceiver;

public class TxlfActivity extends Activity {

	// buttons
	private Button scanButton;
	private Button sessionsButton;
	private Button venueButton;
	private Button sponsorsButton;
	private Button registerButton;
	private static final String LOG_TAG = "txlf";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //declare buttons
        this.scanButton = (Button)this.findViewById(R.id.button_scan);
        this.sessionsButton = (Button)this.findViewById(R.id.button_sessions);
        this.venueButton = (Button)this.findViewById(R.id.button_venue);
        this.sponsorsButton = (Button)this.findViewById(R.id.button_sponsors);
        this.registerButton = (Button)this.findViewById(R.id.button_register);
        
        //Button listeners
        //Scan Button - Requires Barcode Scanner
        scanButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Launch ZXing Barcode Scanner - Get the Result and launch new view
				Intent intent = new Intent("com.google.zxing.client.android.SCAN");
		        intent.setPackage("com.google.zxing.client.android");
		        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
		        startActivityForResult(intent, 0);				
			}
		});
        //Sessions Button
        sessionsButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(TxlfActivity.this, Sessions.class);
		        startActivity(intent);
			}
		});
        //Venue Button
        venueButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Intent i = new Intent(this, ContactAdder.class);
		        //startActivity(i);
			}
		});
        //Sponsors Button
        sponsorsButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Intent i = new Intent(this, ContactAdder.class);
		        //startActivity(i);
			}
		});
        //Register Button
        registerButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Intent i = new Intent(this, ContactAdder.class);
		        //startActivity(i);
			}
		});
        //End Buttons
        
        //check if BarcodeScanner is installed...
        boolean installed = appInstalledOrNot("com.google.zxing.client.android");
        if(installed) {
        	// Barcode Scanner IS installed and we can scan!
        	Log.v(LOG_TAG, "App already installed on your phone");
        	scanButton.setEnabled(true);
        } else {
        	// Barcode Scanner is NOT installed, notify the user to install it for this functionality        	
        	Log.v(LOG_TAG, "App is not installed on your phone");
        	scanButton.setEnabled(false);
        	// Toast message
        	Context context = getApplicationContext();
        	CharSequence text = "Install Barcode Scanner from the Android Market to enable QR Code Scanning!";
        	int duration = Toast.LENGTH_LONG;
        	Toast toast = Toast.makeText(context, text, duration);
        	toast.show();
        	// Forward user to market to install Barcode Scanner - this has been disabled for the emulator
        	//Intent goToMarket = new Intent(Intent.ACTION_VIEW)
        	//	.setData(Uri.parse("market://details?id=com.google.zxing.client.android.SCAN"));
        	//startActivity(goToMarket);
        }

        // Check if TXLF guide has been downloaded
        File file = getBaseContext().getFileStreamPath(GUIDEFILE);
        if(file.exists()) {
        	// Check if it has expired and needs updating
        	if(checkGuideExpiration()) {
        		// Guide is still good
        		Log.d(LOG_TAG, "Guide hasn't expired");
        	} else {
        		// Guide has expired lets download a new one and save it.
        		Log.d(LOG_TAG, "Guide has expired - need to update");
        		//updateGuide(); - instead start service to download

                // start service to download and update guide
        		Context context = getApplicationContext();
                context.startService(new Intent(this, GuideDownloaderService.class));
        	}
        } else {
        	Log.e(LOG_TAG, "Guide doesn't exist, attempting to download");
        	// set alarm to update guide then update guide
        	Context context = getApplicationContext();
            //setRecurringAlarm(context);
        	AlarmReceiver.setRecurringAlarm(context);
            
            // start service to download and update guide
            context.startService(new Intent(this, GuideDownloaderService.class));
        }

    }
    
    private void setRecurringAlarm(Context context) {
    	// Sets an alarm for daily updates
    	// -  service doesn't actually update if file expiration date hasn't expired
        // set it to start at around 1 AM every day, 
    	// but we don't want to hit all at once
        Calendar updateTime = Calendar.getInstance();
        updateTime.setTimeZone(TimeZone.getTimeZone("GMT"));
        updateTime.set(Calendar.HOUR_OF_DAY, 1);
        updateTime.set(Calendar.MINUTE, 0);
     
        Intent downloader = new Intent(context, AlarmReceiver.class);
        PendingIntent recurringDownload = PendingIntent.getBroadcast(context,
                0, downloader, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarms = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarms.setInexactRepeating(AlarmManager.RTC,
                updateTime.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, recurringDownload);
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
    
    public Boolean checkGuideExpiration() {
    	// TODO update to check object file
    	try {
    		// open file
    		InputStream is = openFileInput(GUIDEFILE);
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
    }
    
    public void addContact(String name, String w_phone, String h_phone, String m_phone, String email, String organ, String web, String w_address) {
    	// This launches the add contact UI, allows people to preview and edit.
    	Log.v(LOG_TAG, "Starting AddContact Activity from TxlfActivity");
        Intent i = new Intent(this, AddContact.class);
        i.putExtra("name", name);
        i.putExtra("w_phone", w_phone);
        i.putExtra("h_phone", h_phone);
        i.putExtra("m_phone", m_phone);
        i.putExtra("email", email);
        i.putExtra("web", web);
        i.putExtra("organization", organ);
        i.putExtra("w_address", w_address);
        startActivity(i);
    }
        
    public void testViewGuide() {
    	try {
    		//String jsontext = getProgramGuide();
    		String jsontext = "TODO FIX";
    		Log.v(LOG_TAG, "Guide json: " + jsontext);
    		JSONObject guide = new JSONObject(jsontext);
    		
    		Log.v(LOG_TAG, "There are " + guide.length()+" guide components");
    		
    		String year = guide.getString("year");
    		Log.v(LOG_TAG, "year: " + year);
    		String expires = guide.getString("expires");
    		Log.v(LOG_TAG, "expires: " + expires);
    		String sessionstext = guide.getString("sessions");
    		Log.v(LOG_TAG, "sessions: " + sessionstext);
    		
    		JSONArray sessions = new JSONArray(sessionstext);
    		Log.v(LOG_TAG, "There are " + sessions.length()+" session entries");
    		int i;
    		for (i=0;i<sessions.length();i++) {
    			JSONObject session = sessions.getJSONObject(i);
    			String track = session.getString("track");
    			String time = session.getString("time");
    			String endTime = session.getString("end_time");
    			String speaker = session.getString("speaker");
    			String title = session.getString("title");
    			String summary = session.getString("summary");
    			Log.v(LOG_TAG, "-----Session " + Integer.toString(i) + "-----" );
    			Log.v(LOG_TAG, "track: " + track);
    			Log.v(LOG_TAG, "time: " + time);
    			Log.v(LOG_TAG, "end time: " + endTime);
    			Log.v(LOG_TAG, "speaker: " + speaker);
    			Log.v(LOG_TAG, "title: " + title);
    			Log.v(LOG_TAG, "summary: " + summary);
    		}
    	} catch(Exception e) {
    		Log.v(LOG_TAG, "error loading blog JSON");
    		e.printStackTrace();
    	}
    	
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                //String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                // Handle successful scan and import contact (JSON) - See qrtest.txt
                try {
                	String x = "";
                	JSONObject post = new JSONObject(contents);
                	Log.v(LOG_TAG, "There are " + post.length()+" contact components");
            		Log.v(LOG_TAG, post.toString());

            		String name = post.getString("name");
            		String w_phone = post.getString("p_work");
            		String h_phone = post.getString("p_home");
            		String m_phone = post.getString("p_mobile");
            		String email = post.getString("email");
            		String web = post.getString("web");
            		String organization = post.getString("name");
            		String w_address = post.getString("w_address");
            		
                    x += "Name:" + name + "\n";
                    x += "Work Phone: " + w_phone + "\n\n";
                    x += "Home Phone: " + h_phone + "\n\n";
                    x += "Mobile Phone: " + m_phone + "\n\n";
                    x += "Email: " + email + "\n\n";
                    x += "Website: " + web + "\n\n";
                    x += "Organization: " + organization + "\n\n";
                    x += "Work Address: " + w_address + "\n\n";
                    Log.v(LOG_TAG, x);
                    
                    // add the contact - display
                    addContact(name, w_phone, h_phone, m_phone, email, web, organization, w_address);
                    
                } catch (Exception je) {
                	Log.v(LOG_TAG, "error loading JSON");
                	Log.v(LOG_TAG, "Could not add contact");
                }
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            }
        }
    }
    
    private boolean appInstalledOrNot(String uri) {
    	// This function checks whether or not an application is installed. This does not work for intents, only the root package and does no version checks either!
    	boolean app_installed = false;
    	
    	try {
    		ApplicationInfo info = getPackageManager().getApplicationInfo(uri, 0);
    		// application exists
    		app_installed = true;
    	} catch( PackageManager.NameNotFoundException e) {
    		// application doesn't exist
    		app_installed = false;
    	}
    	return app_installed;
    	}
}