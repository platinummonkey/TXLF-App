package org.texaslinuxfest.txlf;

import java.io.Serializable;
import java.util.*;

@SuppressWarnings("serial")
public class Guide implements Serializable {
	// Defines the guide Object for storage and reading.
	
	private String year;
	@SuppressWarnings("unused")
	private Date expires;
	List<Session> sessions = new ArrayList<Session>();
	
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
	
	public void addSession(int track, Date time, Date endTime, String speaker, String title, String summary) {
		sessions.add(new Session(track, time, endTime, speaker, title, summary));
	}
}
