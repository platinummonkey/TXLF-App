package org.texaslinuxfest.txlfapp;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public interface Constants {
	// App Constants
	Date CONFERENCE_END = new Date(2012, 8, 15); // prevents guide updates after Aug 15, 2012
	
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
	String REGISTERURL = "http://register.texaslinuxfest.org";
	String SURVEYURL = "http://texaslinuxfest.org/survey/";
	String RATINGSURL = "http://texaslinuxfest.org/ratings/submit/";
	String VENUEURL = "http://norriscenters.com/SanAntonio/";
	String VENUEMAPURL = "https://maps.google.com/maps?q=4522+Fredericksburg+Road+San+Antonio,+TX+78201&hl=en&geocode=+&hnear=4522+Fredericksburg+Rd,+Balcones+Heights,+Bexar,+Texas+78201&t=m&z=16";
	
	// REST Services
	String REST_SURVEY = "http://2012.texaslinuxfest.org/?q=txlfapp/";
	String REST_SURVEYSUBMIT = REST_SURVEY+"node.json";
	String REST_SURVEYUPDATE = REST_SURVEY+"node/";
	String REST_SURVEYCONNECT= REST_SURVEY+"system/connect.json";
	
	// Sponsors - Only used by sponsors
	String SPONSORUNLOCKCODE = "SP0NS0RS";
	
	// Volunteers - Only used by volunteers
	String VOLUNTEERUNLOCKCODE = "V0LUNT33RS";
	
	// Admins - Only used by Event Organizers to keep everyone in sync 24 hrs of the day.
	String ADMINUNLOCKCODE = "42";
	
	int NUMDAYS = 2; // friday + saturday = 2
	List<Integer> NUMTRACKSPERDAY = Arrays.asList(2,3); // 2,3 day0-2 tracks, day1-3 tracks
	List<String> DAYTITLES = Arrays.asList("Friday", "Saturday"); // day0, day1
	@SuppressWarnings("unchecked")
	List<List<String>> TRACKTITLES = Arrays.asList(
			Arrays.asList("Track A", "Track B"), // Day 0 track titles
			Arrays.asList("Track A", "Track B", "Track C") // Day 1 track titles
			);
	List<String> SPONSORSTATUSES = Arrays.asList("Platinum","Gold","Silver","Bronze"); // 0,1,2,3
}
