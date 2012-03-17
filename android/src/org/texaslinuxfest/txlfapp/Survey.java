package org.texaslinuxfest.txlfapp;

import static org.texaslinuxfest.txlfapp.Constants.SURVEYURL;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Survey extends Activity {
	WebView webview;
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    getWindow().requestFeature(Window.FEATURE_PROGRESS);
	    getWindow().setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);
	    setContentView(R.layout.register);

	    webview = (WebView) findViewById(R.id.register_webview);
	    webview.getSettings().setJavaScriptEnabled(true);
	    webview.getSettings().setBuiltInZoomControls(true);
	    
	    final Activity activity = this;
	    webview.setWebChromeClient(new WebChromeClient() {
	    	public void onProgressChanged(WebView view, int progress) {
	    		activity.setTitle("Loading...");
	    		activity.setProgress(progress * 100);
	    		if (progress == 100) {
	    			activity.setTitle(R.string.app_name);
	    		}
	    	}
	    });
	    webview.setWebViewClient(new WebViewClient());
	    
	    webview.loadUrl(SURVEYURL);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
	        webview.goBack();
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	// Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.register_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_backToApp:
                // Exit activity
        		finish();
                break;
        }
        return true;
    }
}
