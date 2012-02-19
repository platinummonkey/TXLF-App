package org.texaslinuxfest.txlf;

import org.texaslinuxfest.txlf.Guide;
import org.texaslinuxfest.txlf.Guide.Session;
import static org.texaslinuxfest.txlf.Constants.GUIDEFILE;
import static org.texaslinuxfest.txlf.Constants.GUIDETYPE;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Date;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnTouchListener;
import android.view.animation.*;
import android.widget.*;

public class Sessions extends Activity {

	private Guide guide;
	private String LOG_TAG = "Sessions Activity";
	
	// choose between day one and day 2
	private TextView sessionDayTextButton1; // Friday
	private TextView sessionDayTextButton2; // Saturday
	
	//private TextView sessionTrackTextButton;
	
	private ViewFlipper viewFlipper;
	private GestureDetector gestureDetector;
	private ListView lv_day1;
	private ListView lv_day2;

	//http://pareshnmayani.wordpress.com/tag/android-custom-listview-example/
	//private ArrayList<Object> sessionList;
	//private Session session;
	
	private ArrayList<Session> sessionList_day1;
	private ArrayList<Session> sessionList_day2;
	
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
        
        // declare buttons
        this.sessionDayTextButton1 = (TextView)this.findViewById(R.id.session_day_short1);
        this.sessionDayTextButton2 = (TextView)this.findViewById(R.id.session_day_short2);
        //this.sessionTrackTextButton = (TextView)this.findViewById(R.id.session_track_tv);
        
        // setup view flipper and gesture
        this.viewFlipper = (ViewFlipper) this.findViewById(R.id.session_day_viewflipper);
        this.gestureDetector = new GestureDetector(new SessionGestureDetector());
        
        sessionDayTextButton1.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				// update viewflipper to correct tracks for day
				//dialog http://developer.android.com/guide/topics/ui/dialogs.html
			}
        	
        });
        sessionDayTextButton2.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				// update viewflipper to correct tracks for day
				//dialog http://developer.android.com/guide/topics/ui/dialogs.html
			}
        	
        });
        
        // set up gesture swiping of tracks with animations (left/right only)
        //    up/down events and those which are too diagonal or squigly are ignored
        viewFlipper.setOnTouchListener(new OnTouchListener()
        {
        	public boolean onTouch(View v, MotionEvent event) {
        	    if (gestureDetector.onTouchEvent(event)) {
        	     return true;
        	    } else {
        	     return false;
        	    }
        	}
        });
        
        // load views into viewflipper
        View view_day1 = LayoutInflater.from(getApplicationContext()).inflate(R.layout.session_track, null);
        //View view_day2 = LayoutInflater.from(getApplicationContext()).inflate(R.layout.session_track, null);
        addViewToFlipper(view_day1);
        //addViewToFlipper(view_day2);
        this.lv_day1 = (ListView) view_day1.findViewById(R.id.SessionListView);
        //this.lv_day2 = (ListView) view_day2.findViewById(R.id.SessionListView);
        //http://pareshnmayani.wordpress.com/tag/android-custom-listview-example/
        
        // get sessions
        //this.guide = this.getGuide();
        this.sessionList_day1 = guide.getSessionsByTrack(0);
        //this.sessionList_day2 = guide.getSessionsByTrack(1);
        final SessionListAdapter lv_day1_adapter = new SessionListAdapter(this, this.sessionList_day1);
        //final SessionListAdapter lv_day2_adapter = new SessionListAdapter(this, this.sessionList_day2);
        lv_day1.setAdapter(lv_day1_adapter);
        //lv_day2.setAdapter(lv_day2_adapter);
        lv_day1.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
			    // item click listener for listview
				Session session = (Session) lv_day1_adapter.getItem(position);
				Toast.makeText(getApplicationContext(), "Title => "+session.getTitle()+" \n Time => "+session.getTimeSpan(), Toast.LENGTH_SHORT).show();
			}
        	
		});
        /*lv_day2.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
			    // item click listener for listview
				Session session = (Session) lv_day2_adapter.getItem(position);
				Toast.makeText(getApplicationContext(), "Title => "+session.getTitle()+" \n Time => "+session.getTimeSpan(), Toast.LENGTH_SHORT).show();
			}
        	
		});*/
        	
        //lv1adapter = new SessionViewCustomAdapter(this, title, time);
        //http://www.xtensivearts.com/2009/11/15/quick-tip-2-sorting-lists/
        //adapter.sort(new Comparator<String>() {
    	//public int compare(String object1, String object2) {
    	//	return object1.compareTo(object2);
    	//};
        // });
    };
    
    private Guide getGuide() {
    	try {
    		// open file
    		InputStream is = openFileInput(GUIDEFILE);
    		byte [] buffer = new byte[is.available()];
    		while (is.read(buffer) != -1);
    		//String istext = new String(buffer);
    		ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buffer));
    		Guide guide = (Guide) in.readObject();
    		in.close();
    		
    		return guide; 
    	} catch (Exception e) {
    		// invalid json file, or some badjuju happened
    		Log.e(LOG_TAG, "Error loading guide file");
    		e.printStackTrace();
    		File file = getBaseContext().getFileStreamPath(GUIDEFILE);
    		if (file.exists()) {
    			Log.e(LOG_TAG, "file exists");
    		} else {
    			Log.e(LOG_TAG, "file really doesn't exist");
    		}
    		// set alarm to update guide then update guide
        	Context context = getApplicationContext();
            //setRecurringAlarm(context);
        	AlarmReceiver.setRecurringAlarm(context);
            
            // start service to download and update guide
            context.startService(new Intent(this, GuideDownloaderService.class));
    		return new Guide("2012", new Date());
    	}
    }
    
    // Add view to viewflipper
    public void addViewToFlipper(View view) {
    	viewFlipper.addView(view);
    }
    
    // Custom Gesture Adapter
    public class SessionGestureDetector extends SimpleOnGestureListener {

		  private static final int SWIPE_MIN_DISTANCE = 120;
		  private static final int SWIPE_MAX_OFF_PATH = 250;
		  private static final int SWIPE_THRESHOLD_VELOCITY = 200;

		  public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		   System.out.println(" in onFling() :: ");
		   if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
		    return false;
		   if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
		     && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
		    viewFlipper.setInAnimation(inFromRightAnimation());
		    viewFlipper.setOutAnimation(outToLeftAnimation());
		    viewFlipper.showNext();
		   } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
		     && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
		    viewFlipper.setInAnimation(inFromLeftAnimation());
		    viewFlipper.setOutAnimation(outToRightAnimation());
		    viewFlipper.showPrevious();
		   }
		   return super.onFling(e1, e2, velocityX, velocityY);
		  }
		}
    
    private Animation inFromRightAnimation() {
    	Animation inFromRight = new TranslateAnimation(
    			Animation.RELATIVE_TO_PARENT, +1.0f,
    			Animation.RELATIVE_TO_PARENT, 0.0f,
    			Animation.RELATIVE_TO_PARENT, 0.0f,
    			Animation.RELATIVE_TO_PARENT, 0.0f);
    	inFromRight.setDuration(500);
    	inFromRight.setInterpolator(new AccelerateInterpolator());
    	return inFromRight;
    }

    private Animation outToLeftAnimation() {
    	Animation outtoLeft = new TranslateAnimation(
    			Animation.RELATIVE_TO_PARENT, 0.0f,
    			Animation.RELATIVE_TO_PARENT, -1.0f,
    			Animation.RELATIVE_TO_PARENT, 0.0f,
    			Animation.RELATIVE_TO_PARENT, 0.0f);
    	outtoLeft.setDuration(500);
    	outtoLeft.setInterpolator(new AccelerateInterpolator());
    	return outtoLeft;
    }

    private Animation inFromLeftAnimation() {
    	Animation inFromLeft = new TranslateAnimation(
    			Animation.RELATIVE_TO_PARENT, -1.0f,
    			Animation.RELATIVE_TO_PARENT, 0.0f,
    			Animation.RELATIVE_TO_PARENT, 0.0f,
    			Animation.RELATIVE_TO_PARENT, 0.0f);
    	inFromLeft.setDuration(500);
    	inFromLeft.setInterpolator(new AccelerateInterpolator());
    	return inFromLeft;
    }

    private Animation outToRightAnimation() {
    	Animation outtoRight = new TranslateAnimation(
    			Animation.RELATIVE_TO_PARENT, 0.0f,
    			Animation.RELATIVE_TO_PARENT, +1.0f,
    			Animation.RELATIVE_TO_PARENT, 0.0f,
    			Animation.RELATIVE_TO_PARENT, 0.0f);
    	outtoRight.setDuration(500);
    	outtoRight.setInterpolator(new AccelerateInterpolator());
    	return outtoRight;
    }
}