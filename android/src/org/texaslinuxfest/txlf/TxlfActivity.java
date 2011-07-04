package org.texaslinuxfest.txlf;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.json.JSONArray;
import org.json.JSONObject;
import android.app.Activity;
import android.content.*;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.util.Log;
import static org.texaslinuxfest.txlf.Constants.*;

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
			@Override
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
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Intent i = new Intent(this, ContactAdder.class);
		        //startActivity(i);
			}
		});
        //Venue Button
        venueButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Intent i = new Intent(this, ContactAdder.class);
		        //startActivity(i);
			}
		});
        //Sponsors Button
        sponsorsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Intent i = new Intent(this, ContactAdder.class);
		        //startActivity(i);
			}
		});
        //Register Button
        registerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Intent i = new Intent(this, ContactAdder.class);
		        //startActivity(i);
			}
		});
        //End Buttons
        
        //check if BarcodeScanner is installed...
        boolean installed = appInstalledOrNot("com.google.zxing.client.android.SCAN");
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
        	// Forward user to market to install Barcode Scanner
        	//Intent goToMarket = new Intent(Intent.ACTION_VIEW)
        	//	.setData(Uri.parse("market://details?id=come.google.zxing.client.android.SCAN"));
        	//startActivity(goToMarket);
        }

        // Check if TXLF guide has been downloaded
        File file = getBaseContext().getFileStreamPath(GUIDEFILE);
        if(file.exists()) {
        	// Check if it has expired and needs updating
        	if(checkGuideExpiration()) {
        		// Guide is still good
        		Log.i(LOG_TAG, "Guide hasn't expired");
        	} else {
        		// Guide has expired lets download a new one and save it.
        		Log.i(LOG_TAG, "Guide has expired - need to update");
        		updateGuide();
        	}
        } else {
        	// We need to download the guide.
        	updateGuide();
        }

        //enable testing
        //testJSON();
        //testWriteFile();
        //testViewGuide();
        //testDateExpiration();
    }
    
    public void updateGuide() {
    	// Call HTTP Get on file from web, then write to internal
    	//     storage. - minimize web hits
    	String guidetext = getProgramGuide(); // get String
    	Log.i(LOG_TAG, "Retrieved Guide");
    	FileOutputStream fos;
    	try {
    		Log.i(LOG_TAG, "Writing guide file to internal storage");
    		fos = openFileOutput(GUIDEFILE, Context.MODE_PRIVATE);
    		fos.write(guidetext.getBytes());
    		fos.close();
    		Log.v(LOG_TAG, "Wrote guide file to internal storage");
    	} catch (IOException e) {
    		Log.e(LOG_TAG, "Error Writing guide file to internal storage");
    		e.printStackTrace();
    	}
    }
    
    public Boolean checkGuideExpiration() {
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
    
    public void testDateExpiration() {
    	String olddate = "2011-07-03 13:00:00";
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Date now = new Date();
    	Date oldtime;
		try {
			oldtime = format.parse(olddate);
		} catch (ParseException e) {
			Log.e(LOG_TAG, "Error parsing date");
			e.printStackTrace();
			oldtime = new Date();
		}
    	Log.v(LOG_TAG, "old time: " + oldtime.getTime() + " new time: " + new Date().getTime());
    	if (now.after(oldtime)) {
    		Log.v(LOG_TAG, "time has passed");
    	} else {
    		Log.v(LOG_TAG, "time has NOT passed");
    	}
    }
    
    public void testWriteFile() {
    	String FILENAME = "hello_file";
    	String string = "hello world!";
    	
    	FileOutputStream fos;
		try {
			String fd = getFilesDir().toString();
			Log.v(LOG_TAG, "files directory: " + fd);
			Log.v(LOG_TAG, "writing file");
			fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
			fos.write(string.getBytes());
			fos.close();
			Log.v(LOG_TAG, "wrote file");
			Log.v(LOG_TAG, "reading file");
			InputStream is = openFileInput(FILENAME);
            byte [] buffer = new byte[is.available()];
            while (is.read(buffer) != -1);
            String istext = new String(buffer);
			Log.v(LOG_TAG, "file text: " + istext);			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void testViewGuide() {
    	try {
    		String jsontext = getProgramGuide();
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
    
    public String getProgramGuide() {
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet("http://platinummonkey.com/test.json");
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
    
    public void testJSON() {
    	// this is just a test!
    	try {
        	String x = "";
            InputStream is = this.getResources().openRawResource(R.raw.qrtest);
            byte [] buffer = new byte[is.available()];
            while (is.read(buffer) != -1);
            String jsontext = new String(buffer);
            JSONObject post = new JSONObject(jsontext);
            
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

            x += "------------\n";
            x += "Name:" + name + "\n";
            x += "Work Phone:" + w_phone + "\n\n";
            x += "Home Phone:" + h_phone + "\n\n";
            x += "Mobile Phone:" + m_phone + "\n\n";
            x += "Email:" + email + "\n\n";
            x += "Website:" + web + "\n\n";
            x += "Organization:" + organization + "\n\n";
            x += "Work Address:" + w_address + "\n\n";
            Log.v(LOG_TAG, x);
            // start activity and pass values
            addContact(name, w_phone, h_phone, m_phone, email, web, organization, w_address);
    	} catch (Exception je) {
    		Log.v(LOG_TAG, "error loading JSON");
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
    	PackageManager pm = getPackageManager();
        boolean app_installed = false;
        try {
        	pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed ;
    }
}