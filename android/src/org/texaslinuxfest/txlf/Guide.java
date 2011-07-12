package org.texaslinuxfest.txlf;

import java.io.Serializable;
import java.net.URI;
import java.util.*;

@SuppressWarnings("serial")
public class Guide implements Serializable {
	// Defines the guide Object for storage and reading.
	
	private String year;
	@SuppressWarnings("unused")
	private Date expires;
	List<Session> sessions = new ArrayList<Session>();
	List<Sponsor> sponsors = new ArrayList<Sponsor>();
	Venue venue = null;
	Afterparty afterparty = null;
	
	public Guide (String year, Date expires) {
		this.year = year;
		this.expires = expires;
	}
	
	class Session {
		@SuppressWarnings("unused")
		private int track;
		@SuppressWarnings("unused")
		private Date time;
		@SuppressWarnings("unused")
		private Date endTime;
		@SuppressWarnings("unused")
		private String speaker;
		@SuppressWarnings("unused")
		private String title;
		@SuppressWarnings("unused")
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
			return "year: " + year + "";
		}
	}
	
	class Sponsor {
		@SuppressWarnings("unused")
		private String organization;
		@SuppressWarnings("unused")
		private int level;
		@SuppressWarnings("unused")
		private int order;
		@SuppressWarnings("unused")
		private String levelCommonName;
		@SuppressWarnings("unused")
		private String summary;
		@SuppressWarnings("unused")
		private URI imagePath;
		
		public Sponsor (String organization, int level, int order, String levelCommonName, String summary, URI imagePath) {
			this.organization = organization;
			this.level = level;
			this.order = order;
			this.levelCommonName = levelCommonName;
			this.summary = summary;
			this.imagePath = imagePath;
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
}
