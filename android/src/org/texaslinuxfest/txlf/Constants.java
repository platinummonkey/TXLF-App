package org.texaslinuxfest.txlf;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public interface Constants {
	// App Constants
	Date CONFERENCE_END = new Date(2012, 04, 20); // prevents guide updates after April 20, 2012
	
	// Program Guide Constants
	String GUIDEURL = "http://platinummonkey.com/test.json";
	String GUIDEFILE = "guide.json";
	int GUIDE_UPDATE_HOUR = 1; // 1 am
	int GUIDE_UPDATE_MIN = 0; // 0 minutes
	
	String GUIDETYPE = "Guide";
	String SESSIONTYPE = "Session";
	String VENUETYPE = "Venue";
	String SPONSORTYPE = "Sponsor";
	
	// Resource Constants ( Images, Videos, etc..)
	String MEDIAURL = "http://platinummonkey.com/"; // requires trailing "/"
	
	// Sponsors - Only used by sponsors
	String SPONSORUNLOCKCODE = "SP0NS0RS";
	
	// Volunteers - Only used by volunteers
	String VOLUNTEERUNLOCKCODE = "V0LUNT33RS";
	
	// Admins - Only used by Event Organizers to keep everyone in sync 24 hrs of the day.
	String ADMINUNLOCKCODE = "42";
	
	int NUMDAYS = 2; // friday + saturday = 2
	List<Integer> NUMTRACKSPERDAY = Arrays.asList(2,3); // 2,3 day0-2 tracks, day1-3 tracks
	List<String> DAYTITLES = Arrays.asList("Friday", "Saturday"); // day0, day1
	List<String> TRACKTITLES = Arrays.asList("Track A", "Track B", "Track C"); 
	
}
