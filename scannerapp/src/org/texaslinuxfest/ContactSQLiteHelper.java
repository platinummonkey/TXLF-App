package org.texaslinuxfest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ContactSQLiteHelper extends SQLiteOpenHelper {
	
	// Database Constants
	private static final String DATABASE_NAME = "contacts.db";
	private static final int DATABASE_VERSION = 1;
	public static final String TABLE_CONTACTS = "contacts";
	public static final String COL_ID = "_id";
	public static final String COL_NAME = "name";
	public static final String COL_EMAIL = "email";
	public static final String COL_WORK_PHONE = "wphone";
	public static final String COL_MOBILE_PHONE = "mphone";
	public static final String COL_JOB_TITLE = "title";
	public static final String COL_COMPANY = "company";
	public static final String COL_WEBSITE = "www";
	public static final String COL_ADDRESS = "address";
	
	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table " + TABLE_CONTACTS + "(" + COL_ID
				+ " integer primary key autoincrement, "
				+ COL_NAME + " text not null, "
				+ COL_EMAIL + " text, "
				+ COL_WORK_PHONE + " text, "
				+ COL_MOBILE_PHONE + " text, "
				+ COL_JOB_TITLE + " text, "
				+ COL_COMPANY + " text, "
				+ COL_WEBSITE + " text, "
				+ COL_ADDRESS + " text"
				+ ");";
	

	public ContactSQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(ContactSQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
		onCreate(database);
	}

}
