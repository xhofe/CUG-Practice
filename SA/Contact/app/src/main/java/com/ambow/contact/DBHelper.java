package com.ambow.contact;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "contacts.db";
	public static final String CONTACTS_TABLE = "contacts";
	public static final int DATABASE_VERSION = 2;

	private static final String DATABASE_CREATE = "CREATE TABLE " + CONTACTS_TABLE +" ("
												+ ContactColumn._ID+" integer primary key autoincrement,"
												+ ContactColumn.NAME+" text,"
												+ ContactColumn.MOBILE+" text,"
												+ ContactColumn.EMAIL+" text,"
												+ ContactColumn.CREATED+" long,"
												+ ContactColumn.MODIFIED+" long,"
	                                            + ContactColumn.COMPANY+" text,"
												+ ContactColumn.QQ+" text);";
		
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {		
		db.execSQL("DROP TABLE IF EXISTS " + CONTACTS_TABLE);
		onCreate(db);
	}

}
