package org.texaslinuxfest.txlf;

import org.texaslinuxfest.txlf.Guide.Session;
import static org.texaslinuxfest.txlf.Constants.SESSIONTYPE;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class SessionView extends Activity {
	
	private String LOG_TAG = "SessionView Activity";
	private Session session;
	
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
        setContentView(R.layout.sessions);
    }
}
