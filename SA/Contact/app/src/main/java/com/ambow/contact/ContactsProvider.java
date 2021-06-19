package com.ambow.contact;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class ContactsProvider extends ContentProvider {
	private static final String TAG= "ContactsProvider"; 

	private DBHelper dbHelper;
	private SQLiteDatabase contactsDB;
	
	public static final String AUTHORITY = "com.ambow.provider.contact";
	public static final String CONTACTS_TABLE = "contacts";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/contacts");
	
	public static final int CONTACTS = 1;
	public static final int CONTACT_ID = 2;
	private static final UriMatcher uriMatcher;	
	
	static{
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY,"contacts",CONTACTS);
		uriMatcher.addURI(AUTHORITY,"contacts/#",CONTACT_ID);
	}

	@Override
	public boolean onCreate() {
		dbHelper = new DBHelper(getContext());
		contactsDB = dbHelper.getWritableDatabase();
		return (contactsDB == null)? false : true;
	}
	
	@Override
	public int delete(Uri uri, String where, String[] selectionArgs) {
		int count;
		switch (uriMatcher.match(uri)) {
			case CONTACTS:
				count = contactsDB.delete(CONTACTS_TABLE, where, selectionArgs);
				break;
			case CONTACT_ID:
				String contactID = uri.getPathSegments().get(1);
				count = contactsDB.delete(CONTACTS_TABLE, 
						ContactColumn._ID + "=" + contactID,
						selectionArgs);
				break;
			default: throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		return count;
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		if (uriMatcher.match(uri) != CONTACTS) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
			Log.d(TAG+"insert","initialValues is not null");
		} else {
			values = new ContentValues();
		}
		
		Long now = Long.valueOf(System.currentTimeMillis());
		if (values.containsKey(ContactColumn.CREATED) == false) {
			values.put(ContactColumn.CREATED, now);
		}
		if (values.containsKey(ContactColumn.MODIFIED) == false) {
			values.put(ContactColumn.MODIFIED, now);
		}
		if (values.containsKey(ContactColumn.NAME) == false) {
			values.put(ContactColumn.NAME, "");
		}
		if (values.containsKey(ContactColumn.MOBILE) == false) {
			values.put(ContactColumn.MOBILE, "");
		}
		if (values.containsKey(ContactColumn.EMAIL) == false) {
			values.put(ContactColumn.EMAIL, "");
		}
		if (values.containsKey(ContactColumn.COMPANY) == false) {
			values.put(ContactColumn.COMPANY, "");
		}
		if (values.containsKey(ContactColumn.QQ)==false){
			values.put(ContactColumn.QQ,"");
		}
		Log.d(TAG+"insert",values.toString());
		
		long rowId = contactsDB.insert(CONTACTS_TABLE, null, values);
		if (rowId > 0) {
			Uri noteUri = ContentUris.withAppendedId(CONTENT_URI,rowId);
			getContext().getContentResolver().notifyChange(noteUri, null);
			Log.d(TAG+"insert",noteUri.toString());
			return noteUri;
		}else{
		    throw new SQLException("Failed to insert row into " + uri);
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(CONTACTS_TABLE);
		
		switch (uriMatcher.match(uri)) {
			case CONTACT_ID:
				qb.appendWhere(ContactColumn._ID + "=" + uri.getPathSegments().get(1));
				break;
		}
		String orderBy;
		if (TextUtils.isEmpty(sortOrder)) {
			orderBy = ContactColumn._ID;
		} else {
			orderBy = sortOrder;
		}
		Cursor c = qb.query(contactsDB,projection,
							selection, selectionArgs,
							null, null,orderBy);
		return c;		
	}

	@Override
	public int update(Uri uri, ContentValues values, String where, String[] selectionArgs) {

		Log.d(TAG+"update",values.toString());
		Log.d(TAG+"update",uri.toString());
		
		int count;		
		switch (uriMatcher.match(uri)) {
			case CONTACTS:
				Log.d(TAG+"update",CONTACTS+"");
				count = contactsDB.update(CONTACTS_TABLE, values, where, selectionArgs);
				break;
			case CONTACT_ID:
				String contactID = uri.getPathSegments().get(1);
				Log.d(TAG+"update",contactID+"");
				count = contactsDB.update(CONTACTS_TABLE,values,
						ContactColumn._ID + "=" + contactID, 
						selectionArgs);
				break;
			default: throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		return count;
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
			case CONTACTS:
			return "vnd.android.cursor.dir/vnd.ambow.contacts";
			case CONTACT_ID:
			return "vnd.android.cursor.item/vnd.ambow.contacts";
			default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}

}
