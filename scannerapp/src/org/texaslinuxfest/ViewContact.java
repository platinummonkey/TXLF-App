package org.texaslinuxfest;

import static org.texaslinuxfest.Constants.CONTACTTYPE;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class ViewContact extends Activity {

	private String LOG_TAG = "ViewContact Activity";
	private Contact contact;
	
	private TextView cname;
	private TextView cemail;
	private TextView cwphone;
	private TextView cmphone;
	private TextView cwww;
	private TextView ctitle;
	private TextView ccompany;
	private TextView caddress;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = this.getIntent().getExtras();
        if (b!=null) {
        	contact = (Contact) b.get(CONTACTTYPE);
        	Log.d(LOG_TAG,"Got Contact through intent Serializable");
        } else {
        	Log.e(LOG_TAG,"Unable to get contact through Intent");
        }
        
        setContentView(R.layout.view_contact);
        
        this.cname = (TextView) this.findViewById(R.id.viewContact_name);
        this.cname.setText(contact.getName());
        
        this.cemail = (TextView) this.findViewById(R.id.viewContact_email);
        this.cemail.setText(contact.getEmail());
        
        this.cwphone = (TextView) this.findViewById(R.id.viewContact_workphone);
        this.cwphone.setText(contact.getWorkPhone());
        
        this.cmphone = (TextView) this.findViewById(R.id.viewContact_mobilephone);
        this.cmphone.setText(contact.getMobilePhone());
        
        this.ctitle = (TextView) this.findViewById(R.id.viewContact_title);
        this.ctitle.setText(contact.getJobTitle());
        
        this.ccompany = (TextView) this.findViewById(R.id.viewContact_company);
        this.ccompany.setText(contact.getCompany());
        
        this.cwww = (TextView) this.findViewById(R.id.viewContact_website);
        this.cwww.setText(contact.getWebsite());
        
        this.caddress = (TextView) this.findViewById(R.id.viewContact_address);
        this.caddress.setText(contact.getAddress());
    }
    public void onWWWClick(View v) {
    	// launch web browswer view of venue website
    	Intent i = new Intent(Intent.ACTION_VIEW);
    	i.setData(Uri.parse(contact.getWebsite()));
    	startActivity(i);
    }
    public void onEmailClick(View v) {
    	// launch web browswer view of venue website
    	Intent i = new Intent(Intent.ACTION_VIEW);
    	i.setData(Uri.parse("mailto://"+contact.getEmail()));
    	startActivity(i);
    }
    public void onMPhoneClick(View v) {
    	// launch web browswer view of venue website
    	Intent i = new Intent(Intent.ACTION_CALL);
    	i.setData(Uri.parse(contact.getMobilePhone()));
    	startActivity(i);
    }
    public void onWPhoneClick(View v) {
    	// launch web browswer view of venue website
    	Intent i = new Intent(Intent.ACTION_CALL);
    	i.setData(Uri.parse(contact.getWorkPhone()));
    	startActivity(i);
    }
    public void onAddressClick(View v) {
    	// launch web browswer view of venue website
    	Intent i = new Intent(Intent.ACTION_VIEW);
    	i.setData(Uri.parse("maps.google.com/maps?q="+contact.getAddress()));
    	startActivity(i);
    }
}
