package com.bgdev.nushuttleplanner;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class FavoritesDbHelper extends SQLiteOpenHelper 
{
	public static final String DATABASE_NAME = "favorites_data";
	public static final String TABLE_NAME = "favorites_table";
	public static final String C_ID = "_id";
	public static final String FIRST_LOCATION = "first_location";
	public static final String FIRST_TIME = "first_time";
	public static final String SECOND_LOCATION = "second_location";
	public static final String SECOND_TIME = "second_time";
	public static final String SHUTTLE = "shuttle";
	public static final String IMAGE = "image";
	
	private final String createDb = "create table if not exists "+ TABLE_NAME + " ( "
			+ C_ID + " integer primary key autoincrement, "
			+ SHUTTLE + " text, "
			+ FIRST_LOCATION + " text, "
			+ FIRST_TIME + " text, "
			+ SECOND_LOCATION + " text, "
			+ SECOND_TIME + " text, "
			+ IMAGE + " integer) ";
	
	public FavoritesDbHelper(Context context, String name, CursorFactory factory,
			int version) 
	{
		super(context, DATABASE_NAME, factory, version);
	}
	
	public FavoritesDbHelper(Context context)
	{
		super(context, DATABASE_NAME, null,1);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(createDb);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL("drop table " + TABLE_NAME);
		onCreate(db);
	}
	

}
