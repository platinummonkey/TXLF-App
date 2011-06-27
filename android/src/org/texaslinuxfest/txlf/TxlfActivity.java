package org.texaslinuxfest.txlf;

import java.io.InputStream;

//import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
//import android.content.ContentUris;
//import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
//import android.net.Uri;
import android.os.Bundle;
//import android.provider.Contacts;
//import android.view.Gravity;
import android.view.View;
import android.widget.*;
import android.util.Log;

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
        	
        	//enable testing
        	//testJSON();
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