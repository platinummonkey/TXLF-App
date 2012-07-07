package org.texaslinuxfest;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ContactDataSource {
	// Database fields
	private SQLiteDatabase database;
	private ContactSQLiteHelper dbHelper;
	private String[] allColumns = { ContactSQLiteHelper.COL_ID,
			ContactSQLiteHelper.COL_NAME,
			ContactSQLiteHelper.COL_EMAIL,
			ContactSQLiteHelper.COL_WORK_PHONE,
			ContactSQLiteHelper.COL_MOBILE_PHONE,
			ContactSQLiteHelper.COL_JOB_TITLE,
			ContactSQLiteHelper.COL_COMPANY,
			ContactSQLiteHelper.COL_ADDRESS,
			ContactSQLiteHelper.COL_WEBSITE};
	
	public ContactDataSource(Context context) {
		dbHelper = new ContactSQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Contact createContact(String name, String email, String wphone,
			String mphone, String title, String company, String www,
			String address) {
		ContentValues values = new ContentValues();
		values.put(ContactSQLiteHelper.COL_NAME, name);
		values.put(ContactSQLiteHelper.COL_EMAIL, email);
		values.put(ContactSQLiteHelper.COL_WORK_PHONE, wphone);
		values.put(ContactSQLiteHelper.COL_MOBILE_PHONE, mphone);
		values.put(ContactSQLiteHelper.COL_JOB_TITLE, title);
		values.put(ContactSQLiteHelper.COL_COMPANY, company);
		values.put(ContactSQLiteHelper.COL_WEBSITE, www);
		values.put(ContactSQLiteHelper.COL_ADDRESS, address);
		long insertId = database.insert(ContactSQLiteHelper.TABLE_CONTACTS, null,
				values);
		Cursor cursor = database.query(ContactSQLiteHelper.TABLE_CONTACTS,
				allColumns, ContactSQLiteHelper.COL_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		Contact newContact = cursorToContact(cursor);
		cursor.close();
		return newContact;
	}

	public void deleteContact(Contact contact) {
		long id = contact.getId();
		System.out.println("Contact deleted with id: " + id);
		database.delete(ContactSQLiteHelper.TABLE_CONTACTS, ContactSQLiteHelper.COL_ID
				+ " = " + id, null);
	}

	public List<Contact> getAllContacts() {
		List<Contact> contacts = new ArrayList<Contact>();

		Cursor cursor = database.query(ContactSQLiteHelper.TABLE_CONTACTS,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Contact contact = cursorToContact(cursor);
			contacts.add(contact);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return contacts;
	}

	private Contact cursorToContact(Cursor cursor) {
		Contact contact = new Contact();
		contact.setId(cursor.getLong(0));
		contact.setValues(cursor.getString(1),
				cursor.getString(2),
				cursor.getString(3),
				cursor.getString(4),
				cursor.getString(5),
				cursor.getString(6),
				cursor.getString(7),
				cursor.getString(8));
		return contact;
	}

}
