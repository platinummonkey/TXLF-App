package org.texaslinuxfest.txlf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.texaslinuxfest.txlf.Guide.Sponsor;
import static org.texaslinuxfest.txlf.Constants.SESSIONTYPE;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class SponsorView extends Activity {
	
	private String LOG_TAG = "SponsorView Activity";
	private Sponsor sponsor;
	private File cacheDir;
	
	private TextView sponsorName;
	private TextView sponsorStatus;
	private TextView sponsorWebsite;
	private TextView sponsorBoothBool;
	private ImageView sponsorImage;
	private TextView sponsorSummary;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = this.getIntent().getExtras();
        if (b!=null) {
        	sponsor = (Sponsor) b.getSerializable(SESSIONTYPE);
        	Log.d(LOG_TAG,"Got guide through intent Serializable");
        } else {
        	Log.e(LOG_TAG,"Unable to get guide through Intent");
        }
        cacheDir = this.getCacheDir();
        
        setContentView(R.layout.sponsor_view);
        
        this.sponsorName = (TextView) this.findViewById(R.id.sponsorName);
        this.sponsorName.setText(sponsor.getOrganizationName(21)); // get first 21 characters
        
        this.sponsorStatus = (TextView) this.findViewById(R.id.sponsorStatus);
        this.sponsorStatus.setText(sponsor.getSponsorStatus());
        
        this.sponsorWebsite = (TextView) this.findViewById(R.id.sponsorWebsite);
        this.sponsorWebsite.setText(sponsor.getWebsite());
        
        this.sponsorBoothBool = (TextView) this.findViewById(R.id.sponsorBoothBool);
        if (sponsor.hasBooth()) {
        	this.sponsorBoothBool.setText("Booth Available");
        } else {
        	this.sponsorBoothBool.setText("");
        }
        
        
        this.sponsorImage = (ImageView) this.findViewById(R.id.sponsorImage);
        if (sponsor.getSponsorImage()!= null && sponsor.getSponsorImage().length()>0) {
        	Log.d(LOG_TAG,"Sponsor image exists, attempting to load");
        	File file = new File(this.cacheDir, sponsor.getSponsorImage());
			try {
				Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
				this.sponsorImage.setImageBitmap(bitmap);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
        } else {
        	Log.d(LOG_TAG,"Sponsor image does not exist, defaulting to resource image.");
        }
        
        
        this.sponsorSummary = (TextView) this.findViewById(R.id.sponsorSummary);
        this.sponsorSummary.setText(sponsor.getSummary());
    }
}

