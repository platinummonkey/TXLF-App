package org.texaslinuxfest;

import static org.texaslinuxfest.Constants.CONTACTTYPE;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

public class ViewTXLFContacts extends Activity {
	
	private ArrayList<Contact> contacts;
	private ContactDataSource datasource = null;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // open database and get contacts
        datasource = new ContactDataSource(this);
        datasource.open();
        contacts = (ArrayList<Contact>) datasource.getAllContacts();
        // build interface
        setContentView(R.layout.recentcontacts);
        LinearLayout linlayout = (LinearLayout) this.findViewById(R.id.recentContactsLinearLayout);
        if (contacts.isEmpty()) {
        	// no contacts - make them feel bad and encourage badge scanning.
        	ImageView imageview = new ImageView(this);
        	LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        	imageview.setLayoutParams(lp);
        	imageview.setImageResource(R.drawable.foreveralone);
        	linlayout.addView(imageview);
        }
        
        Context context = getApplicationContext();
        final ContactListAdapter cl_adapter = new ContactListAdapter(context, contacts);
        ListView lv = (ListView) this.findViewById(R.id.contact_listview);
        lv.setAdapter(cl_adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long id) {
			    // item click listener for listview
				Contact contact = (Contact) cl_adapter.getItem(pos);
				viewContact(contact);
			}
		});
    }
    public void viewContact(Contact contact) {
    	Intent intent = new Intent();
    	Bundle b = new Bundle();
		b.putSerializable(CONTACTTYPE, contact);
		intent.putExtras(b);
		intent.setClass(ViewTXLFContacts.this, ViewContact.class);
        startActivity(intent);
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
