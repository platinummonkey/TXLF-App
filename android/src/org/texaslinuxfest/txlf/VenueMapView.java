package org.texaslinuxfest.txlf;

import static org.texaslinuxfest.txlf.Constants.SPONSORTYPE;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.texaslinuxfest.txlf.Guide.Sponsor;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class VenueMapView extends Activity {
	private String LOG_TAG = "VenueMapView Activity";
	private String track;
	private String map;
	private File cacheDir;
	
	private ImageView venueMapImage;
	private TextView venueMapTrack;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = this.getIntent().getExtras();
        if (b!=null) {
        	track = (String) b.get("TRACK");
        	map = (String) b.getString("MAP");
        	Log.d(LOG_TAG,"Got VenueMap through intent Serializable");
        } else {
        	Log.e(LOG_TAG,"Unable to get sponsor through Intent");
        }
        cacheDir = this.getCacheDir();
        
        setContentView(R.layout.venue_map_view);
        
        this.venueMapTrack = (TextView) this.findViewById(R.id.venueMapViewTrack);
        this.venueMapTrack.setText(track);
        
        
        this.venueMapImage = (ImageView) this.findViewById(R.id.venueMapViewImage);
        if (map!= null && map.length()>0) {
        	Log.d(LOG_TAG,"Venue Map image exists, attempting to load");
        	File file = new File(this.cacheDir, map);
			try {
				Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
				this.venueMapImage.setImageBitmap(bitmap);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
        } else {
        	Log.d(LOG_TAG,"Venue Map image does not exist, defaulting to resource image.");
        }
    }
}
