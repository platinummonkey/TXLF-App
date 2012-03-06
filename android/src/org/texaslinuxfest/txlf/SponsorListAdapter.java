package org.texaslinuxfest.txlf;

import org.texaslinuxfest.txlf.Guide.Sponsor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SponsorListAdapter extends BaseAdapter {
	private String LOG_TAG = "SponsorListAdapter";
	private ArrayList<Sponsor> sponsors;
	private LayoutInflater mInflater;
	private File cacheDir;
	
	public SponsorListAdapter(Context context, ArrayList<Sponsor> sl) {
		sponsors = sl;
		mInflater = LayoutInflater.from(context);
		cacheDir = context.getCacheDir();
	}
	public int getCount() {
		return sponsors.size();
	}
	public Object getItem(int position) {
		return sponsors.get(position);
	}
	public long getItemId(int position) {
		return position;
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.sponsor_list, null);
			holder = new ViewHolder();
			holder.organization = (TextView) convertView.findViewById(R.id.sponsorOrganization);
			holder.image = (ImageView) convertView.findViewById(R.id.sponsorImage);
			Log.d(LOG_TAG,"New Holder: Organization: " + sponsors.get(position).getOrganizationName());
			convertView.setTag(holder);
		} else {
			Log.d(LOG_TAG, "New Holder - getTag()");
			holder = (ViewHolder) convertView.getTag();
		}
		holder.organization.setText(sponsors.get(position).getOrganizationName());
		if (sponsors.get(position).getSponsorImage()!= null && sponsors.get(position).getSponsorImage().length()>0) {
        	Log.d(LOG_TAG,"Sponsor image exists, attempting to load");
        	File file = new File(this.cacheDir, sponsors.get(position).getSponsorImage());
			try {
				Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
				holder.image.setImageBitmap(bitmap);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
        } else {
        	Log.d(LOG_TAG,"Speaker image does not exist, defaulting to resource image.");
        }
		Log.d(LOG_TAG,"Returning View from SponsorListAdapter");
		return convertView;
	}
	
	static class ViewHolder {
		TextView organization;
		ImageView image;
	}
}

