package com.ambow.contact;

import android.provider.BaseColumns;

public class ContactColumn implements BaseColumns {
	public ContactColumn(){
	}

	public static final String NAME = "name";
	public static final String MOBILE = "mobileNumber";
	public static final String EMAIL = "email";
	public static final String CREATED = "createdDate";
	public static final String MODIFIED = "modifiedDate";
	public static final String COMPANY = "company";
	public static final String QQ="qq";
	
	public static final int _ID_COLUMN = 0;
	public static final int NAME_COLUMN = 1;
	public static final int MOBILE_COLUMN = 2;
	public static final int EMAIL_COLUMN = 3;
	public static final int CREATED_COLUMN = 4;
	public static final int MODIFIED_COLUMN = 5;
	public static final int COMPANY_COLUMN = 6;
	public static final int QQ_COLUMN=4;

	public static final String[] PROJECTION ={
		_ID,//0
		NAME,//1
		MOBILE,//2
		EMAIL,//3
		COMPANY,//4
			QQ//5
	};

}
