package org.texaslinuxfest.txlf;

import org.texaslinuxfest.txlf.Guide;
import org.texaslinuxfest.txlf.Guide.Sponsor;

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

public class Sponsors extends TabActivity {
	private Guide guide;
	private String LOG_TAG = "Sponsors Activity";
	
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
        setContentView(R.layout.sponsors);
        
        LevelPagerAdapter adapter_level0 = new LevelPagerAdapter(0);
        LevelPagerAdapter adapter_level1 = new LevelPagerAdapter(1);
        LevelPagerAdapter adapter_level2 = new LevelPagerAdapter(2);
        LevelPagerAdapter adapter_level3 = new LevelPagerAdapter(3);
        ViewPager lpager_level0 = (ViewPager) this.findViewById(R.id.sponsor_viewpager_level0);
        ViewPager lpager_level1 = (ViewPager) this.findViewById(R.id.sponsor_viewpager_level1);
        ViewPager lpager_level2 = (ViewPager) this.findViewById(R.id.sponsor_viewpager_level2);
        ViewPager lpager_level3 = (ViewPager) this.findViewById(R.id.sponsor_viewpager_level3);
        lpager_level0.setAdapter(adapter_level0);
        lpager_level1.setAdapter(adapter_level1);
        lpager_level2.setAdapter(adapter_level2);
        lpager_level3.setAdapter(adapter_level3);
        
        
        
        tabhost = getTabHost();
        tabhost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);
        tabhost.addTab(tabhost.newTabSpec("level0").setIndicator(createTabView(tabhost.getContext(), "Platinum")).setContent(R.id.sponsor_tab_layout_level0));
        tabhost.addTab(tabhost.newTabSpec("level1").setIndicator(createTabView(tabhost.getContext(), "Gold")).setContent(R.id.sponsor_tab_layout_level1));
        tabhost.addTab(tabhost.newTabSpec("level2").setIndicator(createTabView(tabhost.getContext(), "Silver")).setContent(R.id.sponsor_tab_layout_level2));
        tabhost.addTab(tabhost.newTabSpec("level3").setIndicator(createTabView(tabhost.getContext(), "Bronze")).setContent(R.id.sponsor_tab_layout_level3));
        tabhost.setCurrentTab(0);
        
        
        
        Log.d(LOG_TAG,"Finished?");
    };
    
    private void viewSponsor(Sponsor sponsor) {
    	Intent intent = new Intent();
		Bundle b = new Bundle();
		b.putSerializable(SPONSORTYPE, sponsor);
		intent.putExtras(b);
		intent.setClass(Sponsors.this, SponsorView.class); //TODO
        startActivity(intent);
    }
    
    private static View createTabView(final Context context, final String text) {
    	View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
    	TextView tv = (TextView) view.findViewById(R.id.tabsText);
    	tv.setText(text);
    	return view;
    }
    
    // Custom PagerAdapter
    private class LevelPagerAdapter extends PagerAdapter {

    	private int levelNumber;
    	
    	public LevelPagerAdapter(int ln) {
    		// subclass pager adapter so we know which day to load.
    		super();
    		this.levelNumber = ln;
    	}
    	
		@Override
		public int getCount() {
			return guide.getNumSponsorsByLevel(levelNumber);
		}

		@Override
		public Object instantiateItem(View collection, int position) {
			LayoutInflater inflater = (LayoutInflater) collection.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			ViewGroup view = (ViewGroup) inflater.inflate(R.layout.sponsor_pager,null);
			ListView lv = (ListView) view.findViewById(R.id.SponsorListView);
			ArrayList<Sponsor> sponsors = guide.getSponsorsByLevel(this.levelNumber);
			final SponsorListAdapter lv_adapter = new SponsorListAdapter(collection.getContext(), sponsors);
			lv.setAdapter(lv_adapter);
			lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long id) {
				    // item click listener for listview
					Sponsor sponsor = (Sponsor) lv_adapter.getItem(pos);
					viewSponsor(sponsor);
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
