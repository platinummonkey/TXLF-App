package org.texaslinuxfest.txlfapp.services;

import java.util.Timer;
import java.util.TimerTask;

import org.texaslinuxfest.txlfapp.util.RESTClient;
import org.texaslinuxfest.txlfapp.util.RequestMethod;

import static org.texaslinuxfest.txlfapp.util.RequestMethod.*;
import static org.texaslinuxfest.txlfapp.Constants.REST_SURVEYSUBMIT;
import static org.texaslinuxfest.txlfapp.Constants.REST_SURVEYUPDATE;
import static org.texaslinuxfest.txlfapp.Constants.REST_SURVEYCONNECT;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class RESTTimer extends Service {
	private static final String LOG_TAG = "RESTTimer";
	TimerTask resttask;
	final Handler handler = new Handler();
	Timer t = new Timer();
	int numTries = 1;
	
	RESTClient restclient;
	
	public void submitSurvey(String jsondata) {
		resttask = new TimerTask() {
			public void run() {
				handler.post(new Runnable() {
					public void run() {
						String response = checkResponse(REST_SURVEYSUBMIT, PUT);
					}
				});
			};
		};
	}

	public void updateSurvey(String jsondata) {
		resttask = new TimerTask() {
			public void run() {
				handler.post(new Runnable() {
					public void run() {
						String response = checkResponse(REST_SURVEYUPDATE, PUT);
					}
				});
			};
		};
	}
	
	public void stopAll() {
		resttask.cancel();
		t.cancel();
		t.purge();
	}
	
	public String checkResponse(String url, RequestMethod method) {
		try {
			restclient = new RESTClient(url);
			restclient.execute(method);
			if ( restclient.getResponseCode() == 200 ) {
				// success
				String response = restclient.getResponse();
				Log.d(LOG_TAG,response);
				return response;
			} else {
				// failure, setup to try again later
				if (numTries < 20) {
					Log.e(LOG_TAG,"Failure code, trying again");
					numTries += 1;
					t.schedule(resttask, 30*1000); // 30 second delay
				} else {
					Log.e(LOG_TAG, "continuous fail, aborting");
					stopAll();
				}
				return null;
			}
		} catch (Exception e) {
			// restclient error
			if (numTries < 20) {
				Log.e(LOG_TAG,"Failure Exception");
				numTries += 1;
				t.schedule(resttask, 30*1000);
			} else {
				Log.e(LOG_TAG, "epic fail, aborting");
				stopAll();
			}
			e.printStackTrace();
			return null;
		}
	}
	
	public void testConnect(String test) {
		resttask = new TimerTask() {
			public void run() {
				handler.post(new Runnable() {
					public void run() {
						String response = checkResponse(REST_SURVEYCONNECT, POST);
					}
				});
			};
		};
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	};
	
	
}
