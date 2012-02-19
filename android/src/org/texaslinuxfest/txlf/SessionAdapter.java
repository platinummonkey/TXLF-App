package org.texaslinuxfest.txlf;

import org.texaslinuxfest.txlf.Guide.Session;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SessionAdapter extends ArrayAdapter<Session> {
	private final Context context;
	private final Session[] sessions;

	public SessionAdapter(Context context, Session[] sessions) {
		super(context, R.layout.session_two_line_list, sessions);
		this.context = context;
		this.sessions = sessions;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.session_two_line_list, parent, false);
		TextView textView_title = (TextView) rowView.findViewById(R.id.session_title);
		TextView textView_time = (TextView) rowView.findViewById(R.id.session_time);
		textView_title.setText(sessions[position].getTitle());
		textView_time.setText(sessions[position].getTimeSpan());

		return rowView;
	}

}
