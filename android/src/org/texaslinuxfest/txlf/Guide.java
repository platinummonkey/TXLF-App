package org.texaslinuxfest.txlf;

import java.io.Serializable;
import java.net.URI;
import java.util.*;
import android.app.Application;

@SuppressWarnings("serial")
public class Guide extends Application implements Serializable {
	// Defines the guide Object for storage and reading.
	
	private String year;
	private Date expires;
	private List<Session> sessions = new ArrayList<Session>();
	private List<Sponsor> sponsors = new ArrayList<Sponsor>();
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
	
	class Session implements Comparable<Session> {
		private int track;
		private Date time;
		private Date endTime;
		private String speaker;
		private String title;
		private String summary;
		
		public Session (int track, Date time, Date endTime, String speaker, String title, String summary) {
			// defines the session object(s) that are a part of the overall guide.
			this.track = track;
			this.time = time;
			this.endTime = endTime;
			this.speaker = speaker;
			this.title = title;
			this.summary = summary;
		}
		
		public String toString() {
			return this.title.toString();
		}
		public String getTitle() {
			return this.title;
		}
		public int getTrack() {
			return this.track;
		}
		public String getTimeSpan() {
			return this.time.toString()+"-"+this.endTime.toString();
		}
		public String getSpeaker() {
			return this.speaker;
		}
		public String getSummary() {
			return this.summary;
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
	
	class Sponsor implements Comparable<Sponsor> {
		private String organization;
		private int level;
		private int order;
		private String levelCommonName;
		private String summary;
		private URI imagePath;
		
		public Sponsor (String organization, int level, int order, String levelCommonName, String summary, URI imagePath) {
			this.organization = organization;
			this.level = level;
			this.order = order;
			this.levelCommonName = levelCommonName;
			this.summary = summary;
			this.imagePath = imagePath;
		}
		public String getOrganizationName() {
			return this.organization.toString();
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
		public URI getImage() {
			return this.imagePath;
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

	class Venue {
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
		private List<String> tracks = new ArrayList<String>();
		
		public Venue (String name, String address, int zipcode, String cityState, URI map) {
			this.name = name;
			this.address = address;
			this.zipcode = zipcode;
			this.cityState = cityState;
			this.map = map;
		}
		
		public void addTrack(String track) {
			tracks.add(track);
		}
	}
	
	class Afterparty {
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
	public void addSession(int track, Date time, Date endTime, String speaker, String title, String summary) {
		sessions.add(new Session(track, time, endTime, speaker, title, summary));
	}
	
	public void addSponsor(String organization, int level, int order, String levelCommonName, String summary, URI imagePath) {
		sponsors.add(new Sponsor(organization, level, order, levelCommonName, summary, imagePath));
	}
	
	public void addVenue(String name, String address, int zipcode, String cityState, URI map) {
		venue = new Venue(name, address, zipcode, cityState, map);
	}
	
	public void addAfterparty(String name, String address, int zipcode, String cityState, URI map) {
		afterparty = new Afterparty(name, address, zipcode, cityState, map);
	}
	
	// set up filtering methods
	public ArrayList<Session> getSessionsByTrack(int n) {
		ArrayList<Session> tracks = new ArrayList<Session>();
		for (Session session : this.sessions) {
			if (session.track == n) {
				tracks.add(session);
			}
		}
		Collections.sort(tracks); // sort by start and end time.
		return tracks;
	}
	public List<Sponsor> getSponsors() {
		Collections.sort(this.sponsors);
		return this.sponsors;
	}
	public Venue getVenue() {
		return this.venue;
	}
	public Afterparty getAfterParty() {
		return this.afterparty;
	}
}
