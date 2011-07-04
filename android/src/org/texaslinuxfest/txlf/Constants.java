package org.texaslinuxfest.txlf;

public interface Constants {
	// Program Guide Constants
	String GUIDEURL = "http://platinummonkey.com/test.json";
	String GUIDEFILE = "guide.json";
	// Resource Constants ( Images, Videos, etc..)
	String MEDIAURL = "http://platinummonkey.com/"; // requires trailing "/"
	
	// Sponsors - Only used by sponsors
	String SPONSORUNLOCKCODE = "SP0NS0RS";
	
	// Volunteers - Only used by volunteers
	String VOLUNTEERUNLOCKCODE = "V0LUNT33RS";
	
	// Admins - Only used by Event Organizers to keep everyone in sync 24 hrs of the day.
	String ADMINUNLOCKCODE = "42";
}
