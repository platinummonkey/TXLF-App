package org.texaslinuxfest.txlf;

import static org.texaslinuxfest.txlf.Constants.*;
import java.io.Serializable;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;
import android.app.Application;
import android.util.Log;

@SuppressWarnings("serial")
public class Guide extends Application implements Serializable {
	// Defines the guide Object for storage and reading.
	
	
	private String LOG_TAG = "Guide Object";
	String year;
	Date expires;
	private ArrayList<Session> sessions = new ArrayList<Session>();
	private ArrayList<Sponsor> sponsors = new ArrayList<Sponsor>();
	private Venue venue = null;
	private Afterparty afterparty = null;
	
	public Guide (String year, Date expires) {
		this.year = year;
		this.expires = expires;
	}
	
	public String getYear() {
		return this.year;
	}
	public Date getExpiration() {
		return this.expires;
	}
	
	class Session implements Comparable<Session>, Serializable {
		private int day;
		private int track;
		private Date time;
		private Date endTime;
		private String speaker;
		private String speakerImage;
		private String title;
		private String summary;
		
		public Session (int day, int track, Date time, Date endTime, String speaker, String speakerImage, String title, String summary) {
			// defines the session object(s) that are a part of the overall guide.
			this.day = day;
			this.track = track;
			this.time = time;
			this.endTime = endTime;
			this.speaker = speaker;
			this.speakerImage = speakerImage;
			this.title = title;
			this.summary = summary;
		}
		
		public String toString() {
			return this.title.toString();
		}
		public String getTitle() {
			return this.title;
		}
		public String getTitle(int maxLength) {
			// Returns portion of title, if less than max length it
			//    just returns title, else appends "..."
			Log.d(LOG_TAG,"getTitle: " + new Integer(maxLength).toString() );
			if (this.title.length() > maxLength) {
				Log.d(LOG_TAG,"string too large");
				return this.title.substring(0,maxLength-1) +"...";
			} else {
				Log.d(LOG_TAG,"string small enough");
				return this.title;
			}
				
		}
		public int getTrack() {
			return this.track;
		}
		public String getDayTrack() {
			return DAYTITLES.get(this.day) + " " + 
					TRACKTITLES.get(this.day).get(this.track);
		}
		public String getTimeSpan() {
			Format formatter = new SimpleDateFormat("h:mm a");
			String stime = formatter.format(this.time);
			String etime = formatter.format(this.endTime);
			return stime+"-"+etime;
		}
		public String getSpeaker() {
			return this.speaker;
		}
		public String getSpeakerImage() {
			return this.speakerImage;
		}
		public String getSummary() {
			return this.summary;
		}
		public String getHash() {
			return '0'+md5(this.title);
		}
		public boolean equals(Object o) {
			// check if two are the same thing (only check title)
			if (!(o instanceof Session))
				return false;
			Session s = (Session)o;
			return s.title.equals(title);
		}
		public int compareTo(Session s) {
			// needed to sort by dates for ListView Adapter
			int timeCmp = time.compareTo(s.time);
			return (timeCmp != 0 ? timeCmp :
					endTime.compareTo(s.endTime));
		}
	}
	
	class Sponsor implements Comparable<Sponsor>, Serializable {
		private String organization;
		private int level;
		private int order;
		private String levelCommonName;
		private String summary;
		private String sponsorImage;
		private String website;
		private Boolean booth;
		
		public Sponsor (String organization, int level, int order, String summary, String sponsorImage, String website, Boolean booth) {
			this.organization = organization;
			this.level = level;
			this.order = order;
			this.levelCommonName = SPONSORSTATUSES.get(level);
			this.summary = summary;
			this.sponsorImage = sponsorImage;
			this.website = website;
			this.booth = booth;
		}
		public String getOrganizationName() {
			return this.organization.toString();
		}
		public String getOrganizationName(int maxLength) {
			// Returns portion of title, if less than max length it
			//    just returns title, else appends "..."
			Log.d(LOG_TAG,"getOrganizationName: " + new Integer(maxLength).toString() );
			if (this.organization.length() > maxLength) {
				Log.d(LOG_TAG,"string too large");
				return this.organization.substring(0,maxLength-1) +"...";
			} else {
				Log.d(LOG_TAG,"string small enough");
				return this.organization;
			}
		}
		public int getLevel() {
			return this.level;
		}
		public int getOrder() {
			return this.order;
		}
		public String getLevelName() {
			return this.levelCommonName;
		}
		public String getSummary() {
			return this.summary;
		}
		public String getWebsite() {
			return this.website;
		}
		public Boolean hasBooth() {
			if (this.booth) {
				return true;
			}
			return false;
		}
		public String getSponsorImage() {
			return this.sponsorImage;
		}
		public String getSponsorStatus() {
			return SPONSORSTATUSES.get(this.level);
		}
		public String getHash() {
			return '1'+md5(this.organization);
		}
		public boolean equals(Object o) {
			// check if two are the same thing (only check title)
			if (!(o instanceof Sponsor))
				return false;
			Sponsor s = (Sponsor)o;
			return s.organization.equals(organization);
		}
		public int compareTo(Sponsor s) {
			// sort by level, order, and organization
			int levelCmp = new Integer(level).compareTo(new Integer(s.level));
			int orderCmp = new Integer(order).compareTo(new Integer(s.order));
			return (levelCmp != 0 ? levelCmp :
					(orderCmp != 0 ? orderCmp :
						organization.compareTo(s.organization)));
		}
	}

	class Venue implements Serializable {
		private String name;
		private String address;
		private int zipcode;
		private String cityState;
		private URI map;
		private List<String> vmaps;
		
		public Venue (String name, String address, int zipcode, String cityState, URI map, ArrayList<String> vmaps) {
			this.name = name;
			this.address = address;
			this.zipcode = zipcode;
			this.cityState = cityState;
			this.map = map;
			this.vmaps = vmaps;
		}
		public String getName() {
			return this.name;
		}
		public ArrayList<String> getAddress() {
			ArrayList<String> alist = new ArrayList<String>();
			alist.add(this.address);
			alist.add(this.cityState + ", " + Integer.toString(this.zipcode));
			return alist;
		}
		public URI getMap() {
			return this.map;
		}
		public List<String> getVenueMaps(){
			return this.vmaps;
		}
		public String getVenueMap(int mapNumber) {
			return this.vmaps.get(mapNumber);
		}
		public ArrayList<List<String>> getVenueMapsSeq(){
			ArrayList<List<String>> vmaps_list = new ArrayList<List<String>>();
			ArrayList<String> ttitles = new ArrayList<String>();
			for (List<String> day : TRACKTITLES) {
				for (String track : day) {
					ttitles.add(track);
				}
			}
			int i;
			for (i=0; i<this.vmaps.size(); i++) {
				vmaps_list.add(Arrays.asList(ttitles.get(i),this.vmaps.get(i)));
			}
			return vmaps_list;
		}
	}
	
	class Afterparty implements Serializable {
		@SuppressWarnings("unused")
		private String name;
		@SuppressWarnings("unused")
		private String address;
		@SuppressWarnings("unused")
		private int zipcode;
		@SuppressWarnings("unused")
		private String cityState;
		@SuppressWarnings("unused")
		private URI map;
		
		public Afterparty (String name, String address, int zipcode, String cityState, URI map) {
			this.name = name;
			this.address = address;
			this.zipcode = zipcode;
			this.cityState = cityState;
			this.map = map;
		}
	}
	
	
	// Set up adding methods
	public void addSession(int day, int track, Date time, Date endTime, String speaker, String speakerImage, String title, String summary) {
		sessions.add(new Session(day, track, time, endTime, speaker, speakerImage, title, summary));
	}
	
	public void addSponsor(String organization, int level, int order, String summary, String sponsorImage, String website, Boolean booth) {
		sponsors.add(new Sponsor(organization, level, order, summary, sponsorImage, website, booth));
	}
	
	public void setVenue(String name, String address, int zipcode, String cityState, URI map, ArrayList<String> vmaps) {
		venue = new Venue(name, address, zipcode, cityState, map, vmaps);
	}
	
	public void setAfterparty(String name, String address, int zipcode, String cityState, URI map) {
		afterparty = new Afterparty(name, address, zipcode, cityState, map);
	}
	
	// set up filtering methods
	public ArrayList<Session> getSessionsByTrack(int d, int n) {
		ArrayList<Session> tracks = new ArrayList<Session>();
		for (Session session : this.sessions) {
			if ((session.track == n) && (session.day == d)) {
				Log.d(LOG_TAG,"Adding Session for Track " + Integer.toString(n) +": Title: " + session.getTitle());
				tracks.add(session);
			}
		}
		Collections.sort(tracks); // sort by start and end time.
		return tracks;
	}
	public ArrayList<Sponsor> getSponsorsByLevel(int n) {
		Log.d(LOG_TAG,"in getSponsorsByLevel()");
		ArrayList<Sponsor> s = new ArrayList<Sponsor>();
		for (Sponsor sponsor : this.sponsors) {
			if (sponsor.level == n) {
				Log.d(LOG_TAG,"Adding Sponsors to SponsorSet "+sponsor.getOrganizationName());
				s.add(sponsor);
			}
		}
		Log.d(LOG_TAG,"Sorting sponsors");
		Collections.sort(s);
		return s;
	}
	public int getNumSponsorLevels() {
		return sponsors.size();
	}
	public int getNumSponsorsByLevel(int n) {
		Log.d(LOG_TAG,"In getNumSponsorsByLevel");
		return getSponsorsByLevel(n).size();
	}
	public Venue getVenue() {
		return this.venue;
	}
	public Afterparty getAfterParty() {
		return this.afterparty;
	}
	public List<String> getImagesToDownload() {
		// sessions, sponsors, venue
		List<String> images = new ArrayList<String>();
		// sessions
		for (int i = 0; i < this.sessions.size(); i++) {
			images.add(this.sessions.get(i).getSpeakerImage());
		}
		// sponsors
		for (int i = 0; i < this.sponsors.size(); i++) {
			images.add(this.sponsors.get(i).getSponsorImage());
		}
		// venue
		//for (int i = 0; i < this.venue.getVenueMaps().size(); i++) {
		//	images.add(this.venue.getVenueMap(i));
		//}
		return images;
	}
	public String md5(String s) {
	    try {
	        // Create MD5 Hash
	        MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
	        digest.update(s.getBytes());
	        byte messageDigest[] = digest.digest();
	        
	        // Create Hex String
	        StringBuffer hexString = new StringBuffer();
	        for (int i=0; i<messageDigest.length; i++)
	            hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
	        return hexString.toString();
	        
	    } catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    }
	    return "";
	}
	public Object getObjectFromHash(String mhash) {
		Integer type = Integer.parseInt(mhash.substring(0, 1));
		String hash = mhash.substring(1,mhash.length()-1);
		Object o = null;
		switch(type) {
			case 0: // Session
				for (Session session : this.sessions) {
					if (md5(session.getTitle()) == hash) {
						o = session;
						break;
					}
				}
				break;
			case 1: // Sponsor
				for (Sponsor sponsor : this.sponsors) {
					if (md5(sponsor.getOrganizationName()) == hash) {
						o = sponsor;
						break;
					}
				}
				break;
			default:
				break;
		};
		return o;
	}
}
