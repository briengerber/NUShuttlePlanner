package com.bgdev.nushuttleplanner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;

public class NuShuttleApplication extends Application
{
	ShuttleDbHelper shuttleDbHelper;
	SQLiteDatabase shuttleDb;
	Context context;
    public static GoogleMap googleMap; 
    public static Bitmap bmp;
    Calendar currentdate;
    public static ArrayList<String> tablesList = new ArrayList<String>();
    String[] listOfLocations;
    public static HashMap<String, ArrayList<String>> markerHash = new HashMap<String, ArrayList<String>>();
    
	@Override
	public void onCreate()
	{
		super.onCreate();
		context=this;
		shuttleDbHelper = new ShuttleDbHelper(context);
		shuttleDb = shuttleDbHelper.getReadableDatabase();
		currentdate = Calendar.getInstance();
		DownloadShuttleDbTask shuttleDbTask = new DownloadShuttleDbTask();
		shuttleDbTask.execute();	
		listOfLocations = getResources().getStringArray(R.array.locations_array);
	}
	
	@Override
	public void onLowMemory()
	{
		ShuttleDbHelper.NuShuttleDataBase.close();
	}
	
	public class DownloadShuttleDbTask extends AsyncTask<Void, Void, Void>
	{
		@Override
		protected Void doInBackground(Void... params) 
		{
			shuttleDbHelper = new ShuttleDbHelper(context);
			try
			{
				shuttleDbHelper.createDataBase();
			}
			catch (IOException e)
			{
				
			}
			
			try
			{
				shuttleDbHelper.openDataBase();
			}
			catch (SQLException e)
			{
				
			}
			
	        BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inSampleSize=2;
	        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.shuttle_overview, options);
	        addAllToArrayList();
	        setupHashMap();
			return null;
		}
		
		public void addAllToArrayList()
		{
			if (tablesList.isEmpty())
			{
				tablesList.add(ShuttleDbHelper.TABLE_SHOP_N_RIDE);
				tablesList.add(ShuttleDbHelper.TABLE_RYAN_FIELD);
				tablesList.add(ShuttleDbHelper.TABLE_FROSTBITE_SHERIDAN);
				tablesList.add(ShuttleDbHelper.TABLE_FROSTBITE_EXPRESS);
				tablesList.add(ShuttleDbHelper.TABLE_EVANSON_TO_CHICAGO);
				tablesList.add(ShuttleDbHelper.TABLE_CHICAGO_TO_EVANSTON);
				tablesList.add(ShuttleDbHelper.TABLE_CHICAGO_EXPRESS_SATURDAY_SHUTTLE);
				if (currentdate.get(Calendar.DAY_OF_WEEK)<=4)
				{
					tablesList.add(ShuttleDbHelper.TABLE_EVANSTON_LOOP_SUN_THROUGH_WED);
				}
				else
				{
					tablesList.add(ShuttleDbHelper.TABLE_EVANSON_LOOP_THURS_THROUGH_SAT);
				}
				if (TimeZone.getTimeZone("CST").inDaylightTime(new Date()))
				{
					tablesList.add(ShuttleDbHelper.TABLE_CAMPUS_LOOP_DAYLIGHTSAVINGS);
				}
				else
				{
					tablesList.add(ShuttleDbHelper.TABLE_CAMPUS_LOOP);
				}
			}
		}
		
		public void setupHashMap()
		{
			for(int i=0; i<listOfLocations.length;i++)
			{
				String location = listOfLocations[i];
				ArrayList<String> tableLocations = new ArrayList<String>();
				for(String table : tablesList)
				{
					String sql = "select distinct location from " + table + " where location like " + "'%" + location + "%'";
					Cursor c = shuttleDb.rawQuery(sql, null);
					c.moveToFirst();
					if(c.getCount()>0)
					{
						tableLocations.add(table);
					}
				}
				ArrayList<String> locationList = new ArrayList<String>();
				for(int j = 0; j < tableLocations.size(); j++)
				{
					locationList.add(tableLocations.get(j));
				}
				markerHash.put(location, locationList);
				tableLocations.clear();
			}
		}
	
	}
	
	
}
