package com.example.roomieturn.library;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class DatabaseHandler extends SQLiteOpenHelper {
	// All Static variables
	private static final int DATABASE_VERSION = 4;
	private static final String DATABASE_NAME = "roomie_db";
	private static final String TABLE_LOGIN = "login";

	// Login Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_EMAIL = "email";
	private static final String KEY_USERNAME = "uname";
	private static final String KEY_HOUSENAME = "house_name";
	private static final String KEY_HOUSECODE = "house_code";
	private static final String KEY_HOUSEADMIN = "house_admin";
	private static final String KEY_UID = "uid";
	private static final String KEY_CREATED_AT = "created_at";
	public static final String TAG = "DatabaseHandler";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_LOGIN + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_EMAIL
				+ " TEXT UNIQUE," + KEY_USERNAME + " TEXT," + KEY_UID
				+ " TEXT," + KEY_HOUSENAME + " TEXT," + KEY_HOUSECODE
				+ " TEXT," + KEY_HOUSEADMIN + " TEXT," + KEY_CREATED_AT
				+ " TEXT" + ")";
		db.execSQL(CREATE_LOGIN_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);
		onCreate(db);
	}

	/**
	 * Storing user details in database
	 * */
	public void addUser(String email, String uname, String uid, String hname,
			String hcode, String admin, String created_at) {
		Log.i(TAG, "email: " + email + " uname: " + uname + " uid: "
				+ uid + " hname: " + hname + " hcode: " + hcode + " admin: "
				+ admin + " created_at: " + created_at);
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_EMAIL, email); // Email
		values.put(KEY_USERNAME, uname); // UserName
		values.put(KEY_UID, uid); // User Id
		values.put(KEY_HOUSENAME, hname); // House Name
		values.put(KEY_HOUSECODE, hcode); // House Code
		values.put(KEY_HOUSEADMIN, admin); // House Code
		values.put(KEY_CREATED_AT, created_at); // Created At
		db.insert(TABLE_LOGIN, null, values);
		db.close(); // Closing database connection
	}

	/**
	 * Storing house details in database
	 * */
	public void addHouse(String uid, String housename, String housecode,
			String houseAdmin) {
		Log.i(TAG, "uid: " + uid + " housename: " + housename + " housecode: "
				+ housecode + " houseadmin: " + houseAdmin);
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues newValues = new ContentValues();
		newValues.put(KEY_HOUSENAME, housename);
		newValues.put(KEY_HOUSECODE, housecode);
		newValues.put(KEY_HOUSEADMIN, houseAdmin);
		db.update(TABLE_LOGIN, newValues, KEY_UID + "=?", new String[] { uid });
		db.close(); // Closing database connection
	}

	/**
	 * Removing house details in database
	 * */
	public void removeHouse(String uid, String housename, String housecode, String houseAdmin) {
		Log.i(TAG, "housecode: " + housecode);
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues newValues = new ContentValues();
		newValues.put(KEY_HOUSENAME, housename);
		newValues.put(KEY_HOUSECODE, housecode);
		newValues.put(KEY_HOUSEADMIN, houseAdmin);
		db.update(TABLE_LOGIN, newValues, KEY_UID + "=?", new String[] { uid });
		db.close(); // Closing database connection
	}

	/**
	 * Getting user data from database
	 * */
	public HashMap<String, String> getUserDetails() {
		Log.i(TAG, "getUserDetails");
		HashMap<String, String> user = new HashMap<String, String>();
		String selectQuery = "SELECT  * FROM " + TABLE_LOGIN;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// Move to first row
		cursor.moveToFirst();
		// Log.i(TAG, "Cursor count: " + cursor.getCount());
		if (cursor.getCount() > 0) {
			user.put("email", cursor.getString(1));
			user.put("uname", cursor.getString(2));
			user.put("uid", cursor.getString(3));
			user.put("house_name", cursor.getString(4));
			user.put("house_code", cursor.getString(5));
			user.put("house_admin", cursor.getString(6));
			user.put("created_at", cursor.getString(7));
		}
		cursor.close();
		db.close();
		return user;
	}

	/**
	 * Getting user login status return true if rows are there in table
	 * */
	public int getRowCount() {
		String countQuery = "SELECT  * FROM " + TABLE_LOGIN;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int rowCount = cursor.getCount();
		db.close();
		cursor.close();
		return rowCount;
	}

	/**
	 * Re create database Delete all tables and create them again
	 * */
	public void resetTables() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_LOGIN, null, null);
		db.close();
	}
}