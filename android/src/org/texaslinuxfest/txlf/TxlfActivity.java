package org.texaslinuxfest.txlf;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class TxlfActivity extends Activity {
	
	// buttons
	private Button scanButton;
	private Button sessionsButton;
	private Button venueButton;
	private Button sponsorsButton;
	private Button registerButton;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //check if BarcodeScanner is installed...
        boolean installed = appInstalledOrNot("com.google.zxing.client.android.SCAN");
        if(installed) {
        	System.out.println("App already installed om your phone");
        } else {
            System.out.println("App is not installed om your phone");
        }

        
        
        //Button listeners
        //Scan Button - Requires Barcode Scanner
        this.scanButton = (Button)this.findViewById(R.id.button_scan);
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
        this.sessionsButton = (Button)this.findViewById(R.id.button_sessions);
        sessionsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
        //Venue Button
        this.venueButton = (Button)this.findViewById(R.id.button_venue);
        venueButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
        //Sponsors Button
        this.sponsorsButton = (Button)this.findViewById(R.id.button_sponsors);
        sponsorsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
        //Register Button
        this.registerButton = (Button)this.findViewById(R.id.button_register);
        registerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
        //End Buttons
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                // Handle successful scan
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