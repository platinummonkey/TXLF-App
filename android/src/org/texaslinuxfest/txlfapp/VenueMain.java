package org.texaslinuxfest.txlfapp;

import java.util.ArrayList;
import java.util.List;

import org.texaslinuxfest.txlfapp.Guide;
import org.texaslinuxfest.txlfapp.Guide.Venue;

import static org.texaslinuxfest.txlfapp.Constants.*;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class VenueMain extends Activity {
	private Guide guide;
	private Venue venue;
	private String LOG_TAG = "Venue Activity";
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = this.getIntent().getExtras();
        if (b!=null) {
        	guide = (Guide) b.getSerializable(GUIDETYPE);
        	Log.d(LOG_TAG,"Got guide through intent Serializable");
        } else {
        	Log.e(LOG_TAG,"Unable to get guide through Intent");
        }
        setContentView(R.layout.venue);
        
        venue = guide.getVenue();
        
        TextView venueName  = (TextView) this.findViewById(R.id.venueName);
        venueName.setText(venue.getName());
        ArrayList<String> address = venue.getAddress();
        TextView venueAddress_l1  = (TextView) this.findViewById(R.id.venueAddress_l1);
        venueAddress_l1.setText(address.get(0));
        TextView venueAddress_l2  = (TextView) this.findViewById(R.id.venueAddress_l2);
        venueAddress_l2.setText(address.get(1));
        TextView venueMap  = (TextView) this.findViewById(R.id.venueMap);
        venueMap.setText(venue.getMap().toString());
        
        Context context = getApplicationContext();
        final VenueListAdapter vl_adapter = new VenueListAdapter(context, venue.getVenueMapsSeq());
        ListView lv = (ListView) this.findViewById(R.id.venueTrackMapsListView);
        lv.setAdapter(vl_adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long id) {
			    // item click listener for listview
				@SuppressWarnings("unchecked")
				List<String> map = (List<String>) vl_adapter.getItem(pos);
				viewMap(map);
			}
        	
		});
        
        
        Log.d(LOG_TAG,"Finished?");
    };
    
    public void viewMap(List<String> mapset) {
    	Intent intent = new Intent();
		intent.putExtra("TRACK", mapset.get(0));
		intent.putExtra("MAP", mapset.get(1));
		intent.setClass(VenueMain.this, VenueMapView.class);
        startActivity(intent);
    }
    public void onVenueMapClick(View v) {
    	// Launch web browser view of google map (may default to google maps app)
    	Intent i = new Intent(Intent.ACTION_VIEW);
    	i.setData(Uri.parse(VENUEMAPURL));
    	startActivity(i);
    }
    public void onVenueTitleClick(View v) {
    	// launch web browswer view of venue website
    	Intent i = new Intent(Intent.ACTION_VIEW);
    	i.setData(Uri.parse(VENUEURL));
    	startActivity(i);
    }
}
