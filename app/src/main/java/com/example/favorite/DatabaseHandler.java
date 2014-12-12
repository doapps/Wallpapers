package com.example.favorite;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper{
	
private static final int DATABASE_VERSION = 1;
	
	private static final String DATABASE_NAME = "AddtoFav";
	
	private static final String TABLE_NAME = "Favorite";
	private static final String KEY_ID = "id";
	private static final String KEY_IMAGE_CATNAME = "imagecatname";
	private static final String KEY_IMAGE_URL = "imageurl";
	
	

	public DatabaseHandler(Context context) 
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
		@Override
		public void onCreate(SQLiteDatabase db) 
		{
			String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NAME + "("
					+ KEY_ID + " INTEGER PRIMARY KEY," 
					+ KEY_IMAGE_CATNAME + " TEXT,"
					+ KEY_IMAGE_URL + " TEXT"
					+ ")";
			db.execSQL(CREATE_CONTACTS_TABLE);		
			
		}
		
	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		// Drop older table if existed
				db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

				// Create tables again
				onCreate(db);
	}
	
	//Adding Record in Database
	
	public	void AddtoFavorite(Pojo pj)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		
		values.put(KEY_IMAGE_CATNAME, pj.getCategoryName());
		values.put(KEY_IMAGE_URL, pj.getImageurl());
		
		// Inserting Row
		db.insert(TABLE_NAME, null, values);
		db.close(); // Closing database connection
		
	}
	
	// Getting All Data
		public List<Pojo> getAllData() 
		{
			List<Pojo> dataList = new ArrayList<Pojo>();
			// Select All Query
			String selectQuery = "SELECT  * FROM " + TABLE_NAME;

			SQLiteDatabase db = this.getWritableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			// looping through all rows and adding to list
			if (cursor.moveToFirst()) 
			{
				do {
					Pojo contact = new Pojo();
					contact.setId(Integer.parseInt(cursor.getString(0)));
					contact.setCategoryName(cursor.getString(1));
					contact.setImageurl(cursor.getString(2));
				 
					// Adding contact to list
					dataList.add(contact);
				} while (cursor.moveToNext());
			}

			// return contact list
			return dataList;
		}
		
	//getting single row
		
		public List<Pojo> getFavRow(String id) 
		{
			List<Pojo> dataList = new ArrayList<Pojo>();
			// Select All Query
			String selectQuery = "SELECT  * FROM " + TABLE_NAME +" WHERE imageurl="+"'"+id+"'";

			SQLiteDatabase db = this.getWritableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			// looping through all rows and adding to list
			if (cursor.moveToFirst()) 
			{
				do {
					Pojo contact = new Pojo();
					contact.setId(Integer.parseInt(cursor.getString(0)));
					contact.setCategoryName(cursor.getString(1));
					contact.setImageurl(cursor.getString(2));
				 
					// Adding contact to list
					dataList.add(contact);
				} while (cursor.moveToNext());
			}

			// return contact list
			return dataList;
		}
		
	//for remove favorite
		
		public void RemoveFav(Pojo contact)
		{
		    SQLiteDatabase db = this.getWritableDatabase();
		    db.delete(TABLE_NAME, KEY_IMAGE_URL + " = ?",
		            new String[] { String.valueOf(contact.getImageurl()) });
		    db.close();
		}
		
		public enum DatabaseManager {
			INSTANCE;
			private SQLiteDatabase db;
			private boolean isDbClosed =true;
			DatabaseHandler dbHelper;
			public void init(Context context) {
				dbHelper = new DatabaseHandler(context);
			if(isDbClosed){
			isDbClosed =false;
			this.db = dbHelper.getWritableDatabase();
			}

			}


			public boolean isDatabaseClosed(){
			return isDbClosed;
			}

			public void closeDatabase(){
			if(!isDbClosed && db != null){
			isDbClosed =true;
			db.close();
			dbHelper.close();
			}
			}
		}
}
