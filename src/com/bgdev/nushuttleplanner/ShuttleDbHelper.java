package com.bgdev.nushuttleplanner;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class ShuttleDbHelper extends SQLiteOpenHelper 
{
	
	//Android default system path of application database
	private static final String SHUTTLE_DB_PATH = "/data/data/com.bgdev.nushuttleplanner/databases/";
	private static final String SHUTTLE_DB_NAME = "nu_shuttles.sqlite";
	private final Context myContext;
	private static String SHUTTLE_FULL_PATH = SHUTTLE_DB_PATH + SHUTTLE_DB_NAME;
	
	public final static String TABLE_CHICAGO_EXPRESS_SATURDAY_SHUTTLE = "chicago_express_saturday_shuttle";
	public final static String TABLE_EVANSON_LOOP_THURS_THROUGH_SAT = "evanston_loop_thurs_through_sat";
	public final static String TABLE_EVANSON_TO_CHICAGO ="evanston_to_chicago";
	public final static String TABLE_FROSTBITE_EXPRESS ="frostbite_express";
	public final static String TABLE_FROSTBITE_SHERIDAN ="frostbite_sheridan";
	public final static String TABLE_RYAN_FIELD ="ryan_field";
	public final static String TABLE_SHOP_N_RIDE ="shop_n_ride_sunday";
	public final static String TABLE_CHICAGO_TO_EVANSTON ="chicago_to_evanston";
	public final static String TABLE_EVANSTON_LOOP_SUN_THROUGH_WED ="evanston_loop_sun_through_wed";
	public final static String TABLE_CAMPUS_LOOP ="campus_loop";
	public final static String TABLE_CAMPUS_LOOP_DAYLIGHTSAVINGS ="campus_loop_daylight_savings";
	
	public final static String COLUMN_ID = "_id";
	public final static String COLUMN_LOCATION = "location";
	public final static String COLUMN_TIME = "time";
	public final static String COLUMN_SHUTTLES = "shuttle";
	
	public static String[] COLUMNS = {COLUMN_ID, COLUMN_LOCATION, COLUMN_TIME, COLUMN_SHUTTLES};
	public static SQLiteDatabase NuShuttleDataBase;
	
	public static boolean bCreated = false;
	public ShuttleDbHelper(Context context)
	{
		super(context, SHUTTLE_DB_NAME, null,1);
		myContext = context;
	}

	/**
	 * Creates an empty database in the system and then fills it with our data
	 */
	public void createDataBase() throws IOException
	{
		boolean bDbExist = _checkDataBase();
		
		if(bDbExist && bCreated)
		{
			return;
		}
		else
		{
			bCreated=true;
			getReadableDatabase();
			try
			{
				_copyDataBase();
			}
			catch (Exception e)
			{
			
			}
			
		}
	}
	
	/**
	 * Checks to see if the database is currently existing
	 */
	private boolean _checkDataBase() 
	{
		SQLiteDatabase checkDb = null;
		try
		{
			checkDb = SQLiteDatabase.openDatabase(SHUTTLE_FULL_PATH, null, SQLiteDatabase.OPEN_READONLY);
		}
		catch (SQLiteException e)
		{
			
		}
		
		if(checkDb!=null)
		{
			checkDb.close();
		}
		
		return checkDb !=null ? true:false;
	}
	
	private void _copyDataBase() throws IOException
	{
		//Opens the local db as the input stream
		InputStream myDbInput = myContext.getAssets().open(SHUTTLE_DB_NAME);
		
		//Path to the newly created db
		String outFileName = SHUTTLE_FULL_PATH;
		
		//open the empty db as the output stream
		OutputStream myDbOutput = new FileOutputStream(outFileName);
		
		//byte transfer
		byte[] buffer = new byte[1024];
		int length;
		while((length = myDbInput.read(buffer))>0)
		{
			myDbOutput.write(buffer, 0, length);
		}
		
		//close the streams
		myDbOutput.flush();
		myDbOutput.close();
		myDbInput.close();
	}
	
	public void openDataBase() throws SQLException
	{
		//Open the database
		NuShuttleDataBase = SQLiteDatabase.openDatabase(SHUTTLE_FULL_PATH, null, SQLiteDatabase.OPEN_READONLY);
	}

	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{

	}
}
