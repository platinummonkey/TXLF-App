package org.texaslinuxfest.txlf;

import org.texaslinuxfest.txlf.Guide;
import org.texaslinuxfest.txlf.Guide.Sponsor;

import static org.texaslinuxfest.txlf.Constants.*;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.*;

public class Sponsors extends Activity {
	private Guide guide;
	private String LOG_TAG = "Sponsors Activity";
	
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
        
        LevelPagerAdapter adapter_levels = new LevelPagerAdapter();
        ViewPager vpager = (ViewPager) this.findViewById(R.id.sponsor_viewpager);
        vpager.setAdapter(adapter_levels);
        
        Log.d(LOG_TAG,"Finished?");
    };
    
    private void viewSponsor(Sponsor s) {
    	Intent intent = new Intent();
		Bundle b = new Bundle();
		b.putSerializable(SPONSORTYPE, s);
		intent.putExtras(b);
		intent.setClass(Sponsors.this, SponsorView.class);
        startActivity(intent);
    }
    
    // Custom PagerAdapter
    private class LevelPagerAdapter extends PagerAdapter {
    	
		@Override
		public int getCount() {
			return SPONSORSTATUSES.size(); //indicates number of sponsor levels
		}

		@Override
		public Object instantiateItem(View collection, int position) {
			LayoutInflater inflater = (LayoutInflater) collection.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			ViewGroup view = (ViewGroup) inflater.inflate(R.layout.sponsor_pager,null);
			TextView tv  = (TextView) view.findViewById(R.id.sponsor_level_desc);
	        tv.setText(SPONSORSTATUSES.get(position));
			ListView lv = (ListView) view.findViewById(R.id.SponsorListView);
			ArrayList<Sponsor> sponsors = guide.getSponsorsByLevel(position);
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
