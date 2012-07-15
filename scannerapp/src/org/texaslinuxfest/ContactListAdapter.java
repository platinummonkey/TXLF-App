package org.texaslinuxfest;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ContactListAdapter extends BaseAdapter {
	private String LOG_TAG = "ContactListAdapter";
	private ArrayList<Contact> contacts;
	private LayoutInflater mInflater;
	
	public ContactListAdapter(Context context, ArrayList<Contact> cl) {
		contacts = cl;
		mInflater = LayoutInflater.from(context);

	}
	public int getCount() {
		return contacts.size();
	}
	public Object getItem(int position) {
		return contacts.get(position);
	}
	public long getItemId(int position) {
		return position;
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.contact_twolinelist, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.contact_name);
			holder.email = (TextView) convertView.findViewById(R.id.contact_email);
			holder.titlecompany = (TextView) convertView.findViewById(R.id.contact_titlecompany);
			convertView.setTag(holder);
		} else {
			Log.d(LOG_TAG, "New Holder - getTag()");
			holder = (ViewHolder) convertView.getTag();
		}
		Contact contact = contacts.get(position);
		holder.name.setText(contact.getName());
		holder.email.setText(contact.getEmail());
		holder.titlecompany.setText(contact.getJobTitle()+" - "+contact.getCompany());
		Log.d(LOG_TAG,"Returning View from ContactListAdapter");
		return convertView;
	}
	
	static class ViewHolder {
		TextView name;
		TextView email;
		TextView titlecompany;
	}

}
