package org.texaslinuxfest.txlf;

import java.util.Date;

public interface Constants {
	// App Constants
	Date CONFERENCE_END = new Date(2012, 04, 20); // prevents guide updates after April 20, 2012
	
	// Program Guide Constants
	String GUIDEURL = "http://platinummonkey.com/test.json";
	String GUIDEFILE = "guide.json";
	int GUIDE_UPDATE_HOUR = 1; // 1 am
	int GUIDE_UPDATE_MIN = 0; // 0 minutes
	
	String GUIDETYPE = "Guide";
	
	// Resource Constants ( Images, Videos, etc..)
	String MEDIAURL = "http://platinummonkey.com/"; // requires trailing "/"
	
	// Sponsors - Only used by sponsors
	String SPONSORUNLOCKCODE = "SP0NS0RS";
	
	// Volunteers - Only used by volunteers
	String VOLUNTEERUNLOCKCODE = "V0LUNT33RS";
	
	// Admins - Only used by Event Organizers to keep everyone in sync 24 hrs of the day.
	String ADMINUNLOCKCODE = "42";
}
