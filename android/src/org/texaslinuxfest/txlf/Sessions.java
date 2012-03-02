package org.texaslinuxfest.txlf;

import org.texaslinuxfest.txlf.Guide;
import org.texaslinuxfest.txlf.Guide.Session;
import static org.texaslinuxfest.txlf.Constants.*;

import java.util.ArrayList;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.*;

public class Sessions extends TabActivity {

	private Guide guide;
	private String LOG_TAG = "Sessions Activity";
	
	// TabHost
	private TabHost tabhost;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = this.getIntent().getExtras();
        if (b!=null) {
        	this.guide = (Guide) b.getSerializable(GUIDETYPE);
        	Log.d(LOG_TAG,"Got guide through intent Serializable");
        } else {
        	Log.e(LOG_TAG,"Unable to get guide through Intent");
        }
        setContentView(R.layout.sessions);
        
        TrackPagerAdapter adapter_day0 = new TrackPagerAdapter(0);
        TrackPagerAdapter adapter_day1 = new TrackPagerAdapter(1);
        ViewPager tpager_day0 = (ViewPager) this.findViewById(R.id.session_viewpager_day0);
        ViewPager tpager_day1 = (ViewPager) this.findViewById(R.id.session_viewpager_day1);
        tpager_day0.setAdapter(adapter_day0);
        tpager_day1.setAdapter(adapter_day1);
        
        
        
        tabhost = getTabHost();
        tabhost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);
        tabhost.addTab(tabhost.newTabSpec("day0").setIndicator(createTabView(tabhost.getContext(), "Friday")).setContent(R.id.session_tab_layout_day0));
        tabhost.addTab(tabhost.newTabSpec("day1").setIndicator(createTabView(tabhost.getContext(), "Saturday")).setContent(R.id.session_tab_layout_day1));
        tabhost.setCurrentTab(0);
        
        
        
        Log.d(LOG_TAG,"Finished?");
        //lv1adapter = new SessionViewCustomAdapter(this, title, time);
        //http://www.xtensivearts.com/2009/11/15/quick-tip-2-sorting-lists/
        //adapter.sort(new Comparator<String>() {
    	//public int compare(String object1, String object2) {
    	//	return object1.compareTo(object2);
    	//};
        // });
    };
    
    private void viewSession(Session session) {
    	Intent intent = new Intent();
		Bundle b = new Bundle();
		b.putSerializable(SESSIONTYPE, session);
		intent.putExtras(b);
		intent.setClass(Sessions.this, SessionView.class);
        startActivity(intent);
    }
    
    private static View createTabView(final Context context, final String text) {
    	View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
    	TextView tv = (TextView) view.findViewById(R.id.tabsText);
    	tv.setText(text);
    	return view;
    }
    
    // Custom PagerAdapter
    private class TrackPagerAdapter extends PagerAdapter {

    	private int dayNumber;
    	
    	public TrackPagerAdapter(int dn) {
    		// subclass pager adapter so we know which day to load.
    		super();
    		this.dayNumber = dn;
    	}
    	
		@Override
		public int getCount() {
			return NUMTRACKSPERDAY.get(dayNumber);
		}

		@Override
		public Object instantiateItem(View collection, int position) {
			LayoutInflater inflater = (LayoutInflater) collection.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			ViewGroup view = (ViewGroup) inflater.inflate(R.layout.session_pager,null);
			TextView tv  = (TextView) view.findViewById(R.id.session_day_track_desc);
	        tv.setText(DAYTITLES.get(this.dayNumber) + " - " + TRACKTITLES.get(this.dayNumber).get(position));
			ListView lv = (ListView) view.findViewById(R.id.SessionListView);
			ArrayList<Session> sessions = guide.getSessionsByTrack(this.dayNumber,position);
			final SessionListAdapter lv_adapter = new SessionListAdapter(collection.getContext(), sessions);
			lv.setAdapter(lv_adapter);
			lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long id) {
				    // item click listener for listview
					Session session = (Session) lv_adapter.getItem(pos);
					viewSession(session);
				}
	        	
			});
			((ViewPager) collection).addView(view,0);
			return view;
		}
		
		@Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
                ((ViewPager) arg0).removeView((View) arg2);

        }
		
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == ((View) arg1);
		}

    	
    }
    
}