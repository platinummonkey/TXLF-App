package org.texaslinuxfest.txlf;

import android.app.Activity;
import android.os.Bundle;

public class TxlfActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}