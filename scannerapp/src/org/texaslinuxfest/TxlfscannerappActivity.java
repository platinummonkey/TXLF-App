package org.texaslinuxfest;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.json.*;
import android.content.*;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.util.Log;
import au.com.bytecode.opencsv.CSVWriter;

import static org.texaslinuxfest.Constants.*;

public class TxlfscannerappActivity extends Activity {

	// buttons
	private Button scanButton;
	private Button registerButton;
	private Button surveyButton;
	private static final String LOG_TAG = "txlf";
	
	private boolean boothExhibitorMode;
	private final static int SCANCODE = 0;
	
	private CSVWriter csvwriter = null;
	private ContactDataSource datasource = null;
	
	private SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boothExhibitorMode = pref.getBoolean("BOOTHEXHIBITORMODE", false);
        datasource = new ContactDataSource(this);
        datasource.open();
        
        
        // build interface
        setContentView(R.layout.main);
        
        //declare buttons
        this.scanButton = (Button)this.findViewById(R.id.button_scan);
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
		        startActivityForResult(intent, SCANCODE);				
			}
		});
        //Register Button
        registerButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// browser intent to register page
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(REGISTERURL));
		        startActivity(intent);
			}
		});
       //Survey Button
        surveyButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// browser intent to survey page
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(SURVEYURL));
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
    }
    
    public void addContact(String name, String w_phone, String m_phone, 
    		String email, String title, String company, String www,
    		String address) {
    	// This launches the add contact UI, allows people to preview and edit.
    	Log.v(LOG_TAG, "Starting AddContact Activity from TxlfActivity");
        Intent intent = new Intent(ContactsContract.Intents.SHOW_OR_CREATE_CONTACT);
        if (email != "") {
        	intent.setData(Uri.fromParts("mailto", email, null));
        	intent.putExtra(ContactsContract.Intents.EXTRA_FORCE_CREATE, true); //skips the dialog box that asks the user to confirm creation of contacts
        } else {
        	intent.putExtra(ContactsContract.Intents.EXTRA_FORCE_CREATE, true); //skips the dialog box that asks the user to confirm creation of contacts
        	intent.putExtra(ContactsContract.Intents.Insert.EMAIL, email);
        }
        // stack in values
        if (name != "") intent.putExtra(ContactsContract.Intents.Insert.NAME, name);
        if (w_phone != "") {
        	intent.putExtra(ContactsContract.Intents.Insert.PHONE, w_phone);
        	intent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK);
        }
        if (m_phone != "") {
        	intent.putExtra(ContactsContract.Intents.Insert.PHONE, m_phone);
        	intent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        }
        if (title != "") intent.putExtra(ContactsContract.Intents.Insert.JOB_TITLE, title);
        if (company != "") intent.putExtra(ContactsContract.Intents.Insert.COMPANY, company);
        if (address != "") intent.putExtra(ContactsContract.Intents.Insert.POSTAL, address);
        if (www != "") intent.putExtra(ContactsContract.Intents.Insert.NOTES, www);
        startActivity(intent);
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	datasource.open();
    	boothExhibitorMode = pref.getBoolean("BOOTHEXHIBITORMODE", false);
        if (requestCode == SCANCODE) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                //String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                // Handle successful scan and import contact (JSON) - See qrtest.txt
                try {
                	String x = "";
                	JSONObject post = new JSONObject(contents);
                	Log.v(LOG_TAG, "There are " + post.length()+" contact components");
            		Log.v(LOG_TAG, post.toString());

            		String name = post.getString("n");
            		String w_phone = post.getString("pw");
            		String m_phone = post.getString("pm");
            		String email = post.getString("e");
            		String web = post.getString("www");
            		String title = post.getString("t");
            		String company = post.getString("c");
            		String address = post.getString("adr");
            		
                    x += "Name:" + name + "\n";
                    x += "Work Phone: " + w_phone + "\n\n";
                    x += "Mobile Phone: " + m_phone + "\n\n";
                    x += "Email: " + email + "\n\n";
                    x += "Website: " + web + "\n\n";
                    x += "Work Site: " + title + " @ " + company + "\n\n";
                    x += "Address: " + address + "\n\n";
                    Log.v(LOG_TAG, x);
                    
                    // check if attendee or booth exhibitor mode
                    if (boothExhibitorMode) {
                    	// add to database
                    	Contact newContact = datasource.createContact(name, email, w_phone, m_phone, title, company, web, address);
                    	Context context = getApplicationContext();
                    	CharSequence text = newContact.getName() + " added to Contacts";
                    	int duration = Toast.LENGTH_LONG;
                    	Toast.makeText(context, text, duration).show();
                    } else {
                    	@SuppressWarnings("unused")
						Contact newContact = datasource.createContact(name, email, w_phone, m_phone, title, company, web, address);
                    	// add the contact - display
                    	addContact(name, w_phone, m_phone, email, web, title, company, address);
                    }
                } catch (Exception je) {
                	Log.e(LOG_TAG, "error loading JSON");
                	CharSequence text = "Failed to read Badge, please try again";
                	Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                	je.printStackTrace();
                	Log.e(LOG_TAG, "Could not add contact");
                }
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            	Log.v(LOG_TAG, "Badge Scan Cancelled");
            }
        }
    }
    
    public void saveToCSV() {
    	try 
    	{
    	    csvwriter = new CSVWriter(new FileWriter(Environment.getExternalStorageDirectory()+"/TexasLinuxFestival.csv"), ',');
    	    List<Contact> contacts = datasource.getAllContacts();
    	    String[] header = {"name","email","phone_work", "phone_mobile", "title",
    	    		"company", "www", "address"};
    	    csvwriter.writeNext(header);
    	    for (Contact contact : contacts) {
    	    	csvwriter.writeNext(contact.getValues());
    	    }
    	    csvwriter.close();
    	    Toast.makeText(getApplicationContext(), "Saved to CSV", Toast.LENGTH_SHORT).show();
    	} 
    	catch (IOException e)
    	{
    		Toast.makeText(getApplicationContext(), "Could not save to CSV", Toast.LENGTH_SHORT).show();
    	    e.printStackTrace();
    	}
    }
    
    public void viewContacts() {
    	Intent intent = new Intent();
		intent.setClass(TxlfscannerappActivity.this, ViewTXLFContacts.class);
		startActivity(intent);
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
    public boolean onPrepareOptionsMenu(Menu menu) {
    	MenuItem toggle = menu.findItem(R.id.menu_Booth);
    	//MenuItem csvgroup = menu.findItem(R.id.CSVgroup);
    	if (boothExhibitorMode) {
    		Log.d(LOG_TAG, "going into booth mode - onPrepareOptionsMenu");
    		toggle.setTitle("Attendee Mode");
    		menu.setGroupVisible(R.id.CSVgroup, true);
    		//if (menu.findItem(writeCSV) == null) menu.add(2, writeCSV, Menu.NONE, "Write CSV");
    		//if (menu.findItem(emailCSV) == null ) menu.add(3, emailCSV, Menu.NONE, "Email CSV");
    	} else {
    		Log.d(LOG_TAG, "going into attendee mode - onPrepareOptionsMenu");
    		toggle.setTitle("Booth Exhibitor Mode");
    		menu.setGroupVisible(R.id.CSVgroup, false);
    		//if (menu.findItem(writeCSV) != null) menu.removeItem(writeCSV);
    		//if (menu.findItem(emailCSV) != null ) menu.removeItem(emailCSV);
    	}
    	return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        	case R.id.menu_Booth:
        		if (boothExhibitorMode) {
        			boothExhibitorMode = false;
        			//datasource.close();
        			//datasource = null;
        			Log.d(LOG_TAG,"Attendee Mode");
        		} else {
        			boothExhibitorMode = true;
        			//datasource = new ContactDataSource(this);
                	//datasource.open();
        			Log.d(LOG_TAG,"Booth Exhibitor Mode");
        		}
        		SharedPreferences.Editor editor = pref.edit();
    			editor.putBoolean("BOOTHEXHIBITORMODE", boothExhibitorMode);
    			editor.commit();
        		break;
        	case R.id.menu_ViewContacts:
        		Log.d(LOG_TAG,"Chose viewContacts");
        		viewContacts();
        		break;
        	case R.id.menu_writeCSV:
        		Log.d(LOG_TAG,"Saving to CSV on SDcard");
        		saveToCSV();
        		break;
        	case R.id.menu_emailCSV:
        		Log.d(LOG_TAG,"Emailing CSV");
        		saveToCSV();
        		Intent intent = new Intent(Intent.ACTION_SEND);
        		intent.setType("text/plain");
        		intent.putExtra(Intent.EXTRA_SUBJECT, "TXLF 2012 CSV");
        		intent.putExtra(Intent.EXTRA_TEXT, "Texas Linux Fest 2012 Contact csv");
        		File file = new File(Environment.getExternalStorageDirectory(),"TexasLinuxFestival.csv");
        		if (!file.exists() || !file.canRead()) {
        			Log.e(LOG_TAG,"Could not load CSV");
        			Toast.makeText(getApplicationContext(), "Could not load CSV", Toast.LENGTH_SHORT).show();
        			break;
        		}
        		Uri uri = Uri.fromFile(file);
        		intent.putExtra(Intent.EXTRA_STREAM, uri);
        		startActivity(Intent.createChooser(intent, "Send email..."));
        		break;
        	default:
        		return false;
        }
        return true;
    }
    @Override
    protected void onResume() {
    	datasource.open();
    	super.onResume();
    }
    protected void onPause() {
    	datasource.close();
    	super.onPause();
    }
}