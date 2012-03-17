package org.texaslinuxfest.txlfapp;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class VenueListAdapter extends BaseAdapter {
	private String LOG_TAG = "SessionListAdapter";
	private ArrayList<List<String>> venuemaps;
	private LayoutInflater mInflater;
	
	public VenueListAdapter(Context context, ArrayList<List<String>> vl) {
		venuemaps = vl;
		// [ String desc, String mapfile ]
		// ['Friday Track A', 'tmap_day0_A.jpg']
		mInflater = LayoutInflater.from(context);

	}
	public int getCount() {
		return venuemaps.size();
	}
	public Object getItem(int position) {
		return venuemaps.get(position);
	}
	public long getItemId(int position) {
		return position;
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.venue_list, null);
			holder = new ViewHolder();
			holder.track = (TextView) convertView.findViewById(R.id.venueListTrackItem);
			Log.d(LOG_TAG,"New Holder: Track: " + venuemaps.get(position).get(0) + " | Map file: " + venuemaps.get(position).get(1));
			convertView.setTag(holder);
		} else {
			Log.d(LOG_TAG, "New Holder - getTag()");
			holder = (ViewHolder) convertView.getTag();
		}
		holder.track.setText(venuemaps.get(position).get(0));
		Log.d(LOG_TAG,"Returning View from SessionListAdapter");
		return convertView;
	}
	
	static class ViewHolder {
		TextView track;
	}
}

