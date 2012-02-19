package org.texaslinuxfest.txlf;

import java.util.ArrayList;
import java.util.List;

import org.texaslinuxfest.txlf.Guide.Session;

import android.app.Activity;
import android.os.Bundle;
import android.view.*;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnTouchListener;
import android.view.animation.*;
import android.widget.*;

public class Sessions extends Activity {

	// choose between day one and day 2
	private TextView sessionDayTextButton1; // Friday
	private TextView sessionDayTextButton2; // Saturday
	
	//private TextView sessionTrackTextButton;
	
	private ViewFlipper viewFlipper;
	private GestureDetector gestureDetector;
	final private ListView lv1;

	//http://pareshnmayani.wordpress.com/tag/android-custom-listview-example/
	//private ArrayList<Object> sessionList;
	//private Session session;
	
	private ArrayList<Session> sessionList;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.session_track, null);
        addViewToFlipper(view);
        this.lv1 = (ListView) view.findViewById(R.id.SessionListView);
        //http://pareshnmayani.wordpress.com/tag/android-custom-listview-example/
        //prepare track list - prepareLists();
        
        // get sessions
        this.sessionList = Guide.getSessionsByTrack(0); 
        final SessionListAdapter lv1adapter = new SessionListAdapter(this, this.sessionList);
        lv1.setAdapter(lv1adapter);
        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
			    // item click listener for listview
				Session session = (Session) lv1adapter.getItem(position);
				Toast.makeText(getApplicationContext(), "Title => "+session.getTitle()+" \n Time => "+session.getTimeSpan(), Toast.LENGTH_SHORT).show();
			}
        	
		});
        	
        //lv1adapter = new SessionViewCustomAdapter(this, title, time);
        //http://www.xtensivearts.com/2009/11/15/quick-tip-2-sorting-lists/
        //adapter.sort(new Comparator<String>() {
    	//public int compare(String object1, String object2) {
    	//	return object1.compareTo(object2);
    	//};
        // });
    };
    
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