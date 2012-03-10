package org.texaslinuxfest.txlf;

import java.io.*;
import java.text.*;
import java.util.*;
import org.json.*;
import android.app.*;
import android.content.*;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
	private Button surveyButton;
	private static final String LOG_TAG = "txlf";
	
	// GUIDE
	private Guide guide;
	
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
        this.surveyButton = (Button)this.findViewById(R.id.button_survey);
        
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
				// force update guide - no idea why intents aren't updating guide...
				forceReloadGuide();
				Intent intent = new Intent();
				Bundle b = new Bundle();
				b.putSerializable(GUIDETYPE, guide);
				intent.putExtras(b);
				intent.setClass(TxlfActivity.this, Sessions.class);
		        startActivity(intent);
			}
		});
        //Venue Button
        venueButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// force update guide - no idea why intents aren't updating guide...
				forceReloadGuide();
				Intent intent = new Intent();
				Bundle b = new Bundle();
				b.putSerializable(GUIDETYPE, guide);
				intent.putExtras(b);
				intent.setClass(TxlfActivity.this, VenueMain.class);
		        startActivity(intent);
			}
		});
        //Sponsors Button
        sponsorsButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// force update guide - no idea why intents aren't updating guide...
				forceReloadGuide();
				Intent intent = new Intent();
				Bundle b = new Bundle();
				b.putSerializable(GUIDETYPE, guide);
				intent.putExtras(b);
				intent.setClass(TxlfActivity.this, Sponsors.class);
		        startActivity(intent);
			}
		});
        //Register Button
        registerButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// webview to register page
				Intent intent = new Intent();
				intent.setClass(TxlfActivity.this, Register.class);
		        startActivity(intent);
			}
		});
       //Survey Button
        surveyButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// webview to survey page
				Intent intent = new Intent();
				intent.setClass(TxlfActivity.this, Survey.class);
		        startActivity(intent);
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
        	Intent goToMarket = new Intent(Intent.ACTION_VIEW)
        		.setData(Uri.parse("market://details?id=com.google.zxing.client.android.SCAN"));
        	startActivity(goToMarket);
        }

        boolean guideReady = checkGuide();
        if (guideReady) {
        	//Guide is ready and session information is available
        	Log.v(LOG_TAG, "Guide is ready - Sessions available");
        	sessionsButton.setEnabled(true);
        	venueButton.setEnabled(true);
        	sponsorsButton.setEnabled(true);
        } else {
        	// Guide is not available - .:. session information not available
        	Log.v(LOG_TAG, "Guide is NOT ready - Sessions unavailable");
        	sessionsButton.setEnabled(false);
        	venueButton.setEnabled(false);
        	sponsorsButton.setEnabled(false);
        	// Toast Message
        	Context context = getApplicationContext();
        	CharSequence text = "Guide is unavailabe!\nPlease allow time to download and restart TXLF App";
        	int duration = Toast.LENGTH_LONG;
        	Toast toast = Toast.makeText(context, text, duration);
        	toast.show();
        }

    }
    
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
    	public void onReceive(Context context, Intent intent) {
    		Bundle b = intent.getExtras();
            if (b!=null) {
            	TxlfActivity.this.guide = (Guide) b.getSerializable(GUIDETYPE);
            	Log.d(LOG_TAG,"Got guide through intent Serializable");
            } else {
            	Log.e(LOG_TAG,"Unable to get guide through Intent");
            }
    		// start service to download images in background
            Context context2 = getApplicationContext();
            context2.startService(new Intent(TxlfActivity.this, ImageDownloaderService.class));
			Log.d(LOG_TAG,"Started ImageDownloader Service");
	        // update UI eventhough images haven't been fully downloaded.
    		updateUI(intent);
    	}
    };
    public void onResume() {
    	super.onResume();		
    	registerReceiver(broadcastReceiver, new IntentFilter(GuideDownloaderService.BROADCAST_ACTION));
    }
    public void onPause() {
    	super.onPause();
    	unregisterReceiver(broadcastReceiver);	
    }
    
    
    private void updateUI(Intent intent) {
    	String guideStatus = intent.getStringExtra("GuideDownloadStatus");
    	// Toast Message
    	Context context = getApplicationContext();
    	int duration = Toast.LENGTH_LONG;
    	Toast toast = Toast.makeText(context, guideStatus, duration);
    	toast.show();
    	sessionsButton.setEnabled(true);
    	venueButton.setEnabled(true);
    	sponsorsButton.setEnabled(true);
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
    
    public boolean checkGuide() {
    	// Check if TXLF guide has been downloaded
        File file = getBaseContext().getFileStreamPath(GUIDEFILE);
        if(file.exists()) {
        	// Check if it has expired and needs updating
        	if(checkGuideExpiration()) {
        		// Guide is still good and stored in this.guide
        		Log.d(LOG_TAG, "Guide hasn't expired");
        		return true;
        	} else {
        		// Guide has expired lets download a new one and save it.
        		Log.d(LOG_TAG, "Guide has expired - need to update");

                // start service to download and update guide
        		Context context = getApplicationContext();
                context.startService(new Intent(this, GuideDownloaderService.class));
                return false;
        	}
        } else {
        	Log.e(LOG_TAG, "Guide doesn't exist, attempting to download");
        	// set alarm to update guide then update guide
        	Context context = getApplicationContext();
            //setRecurringAlarm(context);sd
        	AlarmReceiver.setRecurringAlarm(context);
            
            // start service to download and update guide
            context.startService(new Intent(this, GuideDownloaderService.class));
            return false;
        }
    }
    
    public Boolean checkGuideExpiration() {
    	// try to open guide on disk
    	try {
    		FileInputStream fis = openFileInput(GUIDEFILE);
    		ObjectInputStream in = new ObjectInputStream(fis);
    		Guide g = (Guide) in.readObject();
    		in.close();
    		// Check if guide has expired
    		Date now = new Date();
    		if (now.after(g.expires)) {
    			// guide has expired - need to update
    			return false;
    		}
    		// else guide is up-to-date
    		this.guide = g;
    		return true;
    	} catch (IOException e) {
    		Log.e(LOG_TAG, "Coudln't find guide - IOerror");
    		e.printStackTrace();
    		return false;
    	} catch (Exception e) {
    		Log.e(LOG_TAG, "Couldn't find guide -other exception");
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
    		@SuppressWarnings("unused")
			ApplicationInfo info = getPackageManager().getApplicationInfo(uri, 0);
    		// application exists
    		app_installed = true;
    	} catch( PackageManager.NameNotFoundException e) {
    		// application doesn't exist
    		app_installed = false;
    	}
    	return app_installed;
    	}
    
    // Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_Download:
            	sessionsButton.setEnabled(false);
            	sponsorsButton.setEnabled(false);
            	venueButton.setEnabled(false);
            	Toast.makeText(this, "Forcing Guide Update", Toast.LENGTH_LONG).show();
            	Log.d(LOG_TAG, "Forcing Guide update");

                // start service to download and update guide
        		Context context = getApplicationContext();
        		Intent gds = new Intent(this, GuideDownloaderService.class);
        		gds.putExtra("force", true);
                context.startService(gds);
                break;
            //case R.id.menu_Itinerary:
            	//Context c = getApplicationContext();
        		//Intent i = new Intent(this, Itinerary.class);
        		//Bundle b = new Bundle();
        		//b.putSerializable(GUIDETYPE, guide)
        		//i.putExtra(b);
                //c.startActivity(i);
            	//break;
        }
        return true;
    }
    public void forceReloadGuide() {
    	// try to open guide on disk
    	try {
    		FileInputStream fis = openFileInput(GUIDEFILE);
    		ObjectInputStream in = new ObjectInputStream(fis);
    		guide = (Guide) in.readObject();
    		in.close();
    		Log.d(LOG_TAG,"Got guide");
    	} catch (IOException e) {
    		Log.e(LOG_TAG, "Coudln't find guide - IOerror");
    		e.printStackTrace();
    	} catch (Exception e) {
    		Log.e(LOG_TAG, "Couldn't find guide -other exception");
    		e.printStackTrace();
    	}
    }
}