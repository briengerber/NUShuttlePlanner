package com.bgdev.nushuttleplanner;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class FavoritesProvider extends ContentProvider {


	private static final String AUTH = "com.bgdev.nushuttleplanner.FavoritesProvider";
	
	//Similar to web address for database
	public static final Uri FAVORITES_URI = Uri.parse("content://"+AUTH+"/"+ FavoritesDbHelper.TABLE_NAME);
	
	final static int FAVORITES =1;
	
	SQLiteDatabase db;
	FavoritesDbHelper dbHelper;
	
	//Add Uri's to the matcher. When you want to insert data using each URI you pass it in through the matcher
	private static final UriMatcher uriMatcher;
	
	static
	{
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTH, FavoritesDbHelper.TABLE_NAME, FAVORITES);
	}
	
	@Override
	public boolean onCreate() 
	{
		dbHelper = new FavoritesDbHelper(getContext());
		return true;
	}
	
	@Override
	//If the Uri matches the one we want, add teh content values
	public Uri insert(Uri uri, ContentValues values) 
	{
		db = dbHelper.getWritableDatabase();
		
		//If the uri passed in matches the uri helper up top, then return 1
		if(uriMatcher.match(uri) == FAVORITES)
		{
			db.insert(FavoritesDbHelper.TABLE_NAME, null, values);
		}
		db.close();
		
		//New data has been inserted into the database, so we must noitify the content resolver to reload
		getContext().getContentResolver().notifyChange(uri, null);
		return null;
	}
	
	@Override
	//Takes in a uri and return the corresponding Cursor
	public Cursor query(Uri uri, String[] columns, String selection, String[] selectionArgs, String sortOrder) 
	{
		db = dbHelper.getWritableDatabase();
		Cursor cursor = db.query(FavoritesDbHelper.TABLE_NAME, columns, selection, selectionArgs, null,null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);		
		return cursor;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
