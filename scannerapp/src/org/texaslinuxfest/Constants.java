package org.texaslinuxfest;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public interface Constants {
	// App Constants
	
	// Resource Constants ( Images, Videos, etc..)
	String REGISTERURL = "http://register.texaslinuxfest.org";
	String SURVEYURL = "http://texaslinuxfest.org/survey/";
	
	// Database Constants
	String DATABASE_NAME = "contacts.db";
	int DATABASE_VERSION = 1;
	String TABLE_CONTACTS = "contacts";
	String COL_ID = "_id";
	String COL_NAME = "name";
	String COL_WORK_PHONE = "wphone";
	String COL_MOBILE_PHONE = "mphone";
	String COL_JOB_TITLE = "title";
	String COL_COMPANY = "company";
	String COL_ADDRESS = "address";
	String COL_WEBSITE = "www";
	
	// Database creation sql statement
	String DATABASE_CREATE = "create table " + TABLE_CONTACTS + "(" + COL_ID
				+ " integer primary key autoincrement, "
				+ COL_NAME + " text not null, "
				+ COL_WORK_PHONE + " text, "
				+ COL_MOBILE_PHONE + " text, "
				+ COL_JOB_TITLE + " text, "
				+ COL_COMPANY + " text, "
				+ COL_ADDRESS + " text, "
				+ COL_WEBSITE + " text"
				+ ");";
}
