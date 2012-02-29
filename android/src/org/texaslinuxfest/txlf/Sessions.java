package org.texaslinuxfest.txlf;

import org.texaslinuxfest.txlf.Guide;
import org.texaslinuxfest.txlf.Guide.Session;
import static org.texaslinuxfest.txlf.Constants.GUIDEFILE;
import static org.texaslinuxfest.txlf.Constants.GUIDETYPE;
import static org.texaslinuxfest.txlf.Constants.SESSIONTYPE;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Date;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

public class Sessions extends TabActivity implements OnGestureListener {

	private Guide guide;
	private String LOG_TAG = "Sessions Activity";
	
	// TabHost
	private TabHost tabhost;
	private ViewFlipper viewFlipperDay0;
	private ViewFlipper viewFlipperDay1;
	private TextView trackTitle_day0;
	private TextView trackTitle_day1;
	private GestureDetector gestureDetectorDay0;
	private GestureDetector gestureDetectorDay1;
	private ListView lv_day0_trackA;
	private ListView lv_day0_trackB;
	private ListView lv_day0_trackC;
	private ListView lv_day1_trackA;
	private ListView lv_day1_trackB;
	private ListView lv_day1_trackC;
	

	//http://pareshnmayani.wordpress.com/tag/android-custom-listview-example/
	//private ArrayList<Object> sessionList;
	//private Session session;
	
	private ArrayList<Session> sessionList_day0_trackA;
	private ArrayList<Session> sessionList_day0_trackB;
	private ArrayList<Session> sessionList_day0_trackC;
	private ArrayList<Session> sessionList_day1_trackA;
	private ArrayList<Session> sessionList_day1_trackB;
	private ArrayList<Session> sessionList_day1_trackC;
	
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
        
        this.trackTitle_day0 = (TextView) this.findViewById(R.id.session_track_desc_day0);
        this.trackTitle_day0.setText("Track A");
        this.viewFlipperDay0 = (ViewFlipper) this.findViewById(R.id.session_viewflipper_day0);
        this.trackTitle_day1 = (TextView) this.findViewById(R.id.session_track_desc_day1);
        this.trackTitle_day1.setText("Track A");
        this.viewFlipperDay1 = (ViewFlipper) this.findViewById(R.id.session_viewflipper_day1);
        
        this.gestureDetectorDay0 = new GestureDetector(new SessionGestureDetector(this.viewFlipperDay0));
        this.gestureDetectorDay1 = new GestureDetector(new SessionGestureDetector(this.viewFlipperDay1));
                
        // set up gesture swiping of tracks with animations (left/right only)
        //    up/down events and those which are too diagonal or squigly are ignored
        viewFlipperDay0.setOnTouchListener(new OnTouchListener()
        {
        	public boolean onTouch(View v, MotionEvent event) {
        	    if (gestureDetectorDay0.onTouchEvent(event)) {
        	     return true;
        	    } else {
        	     return false;
        	    }
        	}
        });
        viewFlipperDay1.setOnTouchListener(new OnTouchListener()
        {
        	public boolean onTouch(View v, MotionEvent event) {
        	    if (gestureDetectorDay1.onTouchEvent(event)) {
        	     return true;
        	    } else {
        	     return false;
        	    }
        	}
        });
        
        // load views into viewflipper
        /// Add Tracks
        Log.d(LOG_TAG,"Creating Track Views");
        View view_day0_trackA = LayoutInflater.from(getApplicationContext()).inflate(R.layout.session_track, null);
        View view_day0_trackB = LayoutInflater.from(getApplicationContext()).inflate(R.layout.session_track, null);
        View view_day0_trackC = LayoutInflater.from(getApplicationContext()).inflate(R.layout.session_track, null);
        View view_day1_trackA = LayoutInflater.from(getApplicationContext()).inflate(R.layout.session_track, null);
        View view_day1_trackB = LayoutInflater.from(getApplicationContext()).inflate(R.layout.session_track, null);
        View view_day1_trackC = LayoutInflater.from(getApplicationContext()).inflate(R.layout.session_track, null);
        
        Log.d(LOG_TAG,"Adding Track Views to ViewFlippers");
        addViewToDayFlipper0(view_day0_trackA);
        addViewToDayFlipper0(view_day0_trackB);
        addViewToDayFlipper0(view_day0_trackC);
        addViewToDayFlipper1(view_day1_trackA);
        addViewToDayFlipper1(view_day1_trackB);
        addViewToDayFlipper1(view_day1_trackC);
        
        Log.d(LOG_TAG,"Assigning ListViews");
        this.lv_day0_trackA = (ListView) view_day0_trackA.findViewById(R.id.SessionListView);
        this.lv_day0_trackB = (ListView) view_day0_trackB.findViewById(R.id.SessionListView);
        this.lv_day0_trackC = (ListView) view_day0_trackC.findViewById(R.id.SessionListView);
        this.lv_day1_trackA = (ListView) view_day1_trackA.findViewById(R.id.SessionListView);
        this.lv_day1_trackB = (ListView) view_day1_trackB.findViewById(R.id.SessionListView);
        this.lv_day1_trackC = (ListView) view_day1_trackC.findViewById(R.id.SessionListView);
        //http://pareshnmayani.wordpress.com/tag/android-custom-listview-example/
        
        // get sessions
        //this.guide = this.getGuide();
        Log.d(LOG_TAG,"Getting Session info for each track");
        this.sessionList_day0_trackA = guide.getSessionsByTrack(0,0);
        this.sessionList_day0_trackB = guide.getSessionsByTrack(0,1);
        this.sessionList_day0_trackC = guide.getSessionsByTrack(0,2);
        this.sessionList_day1_trackA = guide.getSessionsByTrack(1,0);
        this.sessionList_day1_trackB = guide.getSessionsByTrack(1,1);
        this.sessionList_day1_trackC = guide.getSessionsByTrack(1,2);
        
        Log.d(LOG_TAG,"Assigning SessionAdapters");
        final SessionListAdapter lv_day0_trackA_adapter = new SessionListAdapter(this, this.sessionList_day0_trackA);
        final SessionListAdapter lv_day0_trackB_adapter = new SessionListAdapter(this, this.sessionList_day0_trackB);
        final SessionListAdapter lv_day0_trackC_adapter = new SessionListAdapter(this, this.sessionList_day0_trackC);
        final SessionListAdapter lv_day1_trackA_adapter = new SessionListAdapter(this, this.sessionList_day1_trackA);
        final SessionListAdapter lv_day1_trackB_adapter = new SessionListAdapter(this, this.sessionList_day1_trackB);
        final SessionListAdapter lv_day1_trackC_adapter = new SessionListAdapter(this, this.sessionList_day1_trackC);
        
        Log.d(LOG_TAG,"Setting Adapters");
        lv_day0_trackA.setAdapter(lv_day0_trackA_adapter);
        lv_day0_trackB.setAdapter(lv_day0_trackB_adapter);
        lv_day0_trackC.setAdapter(lv_day0_trackC_adapter);
        lv_day1_trackA.setAdapter(lv_day1_trackA_adapter);
        lv_day1_trackB.setAdapter(lv_day1_trackB_adapter);
        lv_day1_trackC.setAdapter(lv_day1_trackC_adapter);
        
        Log.d(LOG_TAG,"ListView OnClickListenters being assigned");
        lv_day0_trackA.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
			    // item click listener for listview
				Session session = (Session) lv_day0_trackA_adapter.getItem(position);
				Toast.makeText(getApplicationContext(), "Title => "+session.getTitle()+" \n Time => "+session.getTimeSpan(), Toast.LENGTH_SHORT).show();
				viewSession(session);
			}
        	
		});
        lv_day0_trackB.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
			    // item click listener for listview
				Session session = (Session) lv_day0_trackB_adapter.getItem(position);
				Toast.makeText(getApplicationContext(), "Title => "+session.getTitle()+" \n Time => "+session.getTimeSpan(), Toast.LENGTH_SHORT).show();
				viewSession(session);
			}
        	
		});
        lv_day0_trackC.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
			    // item click listener for listview
				Session session = (Session) lv_day0_trackC_adapter.getItem(position);
				Toast.makeText(getApplicationContext(), "Title => "+session.getTitle()+" \n Time => "+session.getTimeSpan(), Toast.LENGTH_SHORT).show();
				viewSession(session);
			}
        	
		});
        lv_day1_trackA.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
			    // item click listener for listview
				Session session = (Session) lv_day1_trackA_adapter.getItem(position);
				Toast.makeText(getApplicationContext(), "Title => "+session.getTitle()+" \n Time => "+session.getTimeSpan(), Toast.LENGTH_SHORT).show();
				viewSession(session);
			}
        	
		});
        lv_day1_trackB.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
			    // item click listener for listview
				Session session = (Session) lv_day1_trackB_adapter.getItem(position);
				Toast.makeText(getApplicationContext(), "Title => "+session.getTitle()+" \n Time => "+session.getTimeSpan(), Toast.LENGTH_SHORT).show();
				viewSession(session);
			}
        	
		});
        lv_day1_trackC.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
			    // item click listener for listview
				Session session = (Session) lv_day1_trackC_adapter.getItem(position);
				Toast.makeText(getApplicationContext(), "Title => "+session.getTitle()+" \n Time => "+session.getTimeSpan(), Toast.LENGTH_SHORT).show();
				viewSession(session);
			}
        	
		});
        
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
    public void addViewToDayFlipper0(View view) {
    	viewFlipperDay0.addView(view);
    }
    public void addViewToDayFlipper1(View view) {
    	viewFlipperDay1.addView(view);
    }
    
    // Custom Gesture Adapter
    public class SessionGestureDetector extends SimpleOnGestureListener {

		  private static final int SWIPE_MIN_DISTANCE = 120;
		  private static final int SWIPE_MAX_OFF_PATH = 250;
		  private static final int SWIPE_THRESHOLD_VELOCITY = 200;
		  private ViewFlipper vf;
		  
		  public SessionGestureDetector(final ViewFlipper vf) {
			  super();
			  this.vf = vf;
		  }
		  
		  public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		   System.out.println(" in onFling() :: ");
		   if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
		    return false;
		   if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
		     && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
		    vf.setInAnimation(inFromRightAnimation());
		    vf.setOutAnimation(outToLeftAnimation());
		    vf.showNext();
		   } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
		     && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
		    vf.setInAnimation(inFromLeftAnimation());
		    vf.setOutAnimation(outToRightAnimation());
		    vf.showPrevious();
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

	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}

	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
}