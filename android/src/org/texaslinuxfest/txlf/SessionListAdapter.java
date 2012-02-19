package org.texaslinuxfest.txlf;

import org.texaslinuxfest.txlf.Guide.Session;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SessionListAdapter extends BaseAdapter {
	private ArrayList<Session> sessions;
	//private Session[] sl;
	private LayoutInflater mInflater;
	
	public SessionListAdapter(Context context, ArrayList<Session> sl) {
		sessions = sl;
		mInflater = LayoutInflater.from(context);
	}
	public int getCount() {
		return sessions.size();
	}
	public Object getItem(int position) {
		return sessions.get(position);
	}
	public long getItemId(int position) {
		return position;
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.session_two_line_list, null);
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.session_title);
			holder.time = (TextView) convertView.findViewById(R.id.session_time);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.title.setText(sessions.get(position).getTitle());
		holder.time.setText(sessions.get(position).getTimeSpan());
		return convertView;
	}
	
	static class ViewHolder {
		TextView title;
		TextView time;
	}
}
