package org.texaslinuxfest.txlf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.texaslinuxfest.txlf.Guide.Session;
import static org.texaslinuxfest.txlf.Constants.SESSIONTYPE;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class SessionView extends Activity {
	
	private String LOG_TAG = "SessionView Activity";
	private Session session;
	private File cacheDir;
	
	private TextView sessionTitle;
	private TextView sessionTime;
	private TextView sessionDayTrack;
	private TextView sessionSpeaker;
	private ImageView sessionSpeakerImage;
	private TextView sessionSummary;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = this.getIntent().getExtras();
        if (b!=null) {
        	this.session = (Session) b.getSerializable(SESSIONTYPE);
        	Log.d(LOG_TAG,"Got guide through intent Serializable");
        } else {
        	Log.e(LOG_TAG,"Unable to get guide through Intent");
        }
        cacheDir = this.getCacheDir();
        
        setContentView(R.layout.session_view);
        
        this.sessionTitle = (TextView) this.findViewById(R.id.sessionTitle);
        this.sessionTitle.setText(this.session.getTitle(21)); // get first 21 characters
        
        this.sessionDayTrack = (TextView) this.findViewById(R.id.sessionDayTrack);
        this.sessionDayTrack.setText(this.session.getDayTrack());
        
        this.sessionTime = (TextView) this.findViewById(R.id.sessionTime);
        this.sessionTime.setText(this.session.getTimeSpan());
        
        this.sessionSpeaker = (TextView) this.findViewById(R.id.sessionSpeaker);
        this.sessionSpeaker.setText(this.session.getSpeaker());
        
        this.sessionSpeakerImage = (ImageView) this.findViewById(R.id.sessionImage);
        if (session.getSpeakerImage()!= null && session.getSpeakerImage().length()>0) {
        	Log.d(LOG_TAG,"Speaker image exists, attempting to load");
        	File file = new File(this.cacheDir, session.getSpeakerImage());
			try {
				Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
				this.sessionSpeakerImage.setImageBitmap(bitmap);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } else {
        	Log.d(LOG_TAG,"Speaker image does not exist, defaulting to resource image.");
        }
        
        
        this.sessionSummary = (TextView) this.findViewById(R.id.sessionSummary);
        this.sessionSummary.setText(this.session.getSummary());
        
        final RatingBar ratingbar = (RatingBar) this.findViewById(R.id.sessionRating);
        ratingbar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
        	public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        		Toast.makeText(SessionView.this, "New Rating: "+rating, Toast.LENGTH_SHORT).show();
        	}
        });
        
        
    }
}
