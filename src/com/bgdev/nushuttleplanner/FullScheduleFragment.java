package com.bgdev.nushuttleplanner;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class FullScheduleFragment extends ListFragment 
{
	ListView list;
	String strTableName, strShuttleName, strDescription, strBundleTime;
	SQLiteDatabase shuttleDb;
	ShuttleDbHelper shuttleDbHelper;
	LayoutInflater layoutInflater;
	Calendar currentTime;
	
	ArrayList<String> listLocations = new ArrayList<String>();
	ArrayList<String> listTimes = new ArrayList<String>();
	
	public static FullScheduleFragment newInstance(String strShuttle)
	{
		FullScheduleFragment frag = new FullScheduleFragment();
		Bundle b = new Bundle();
		b.putString("shuttle name", strShuttle);
		frag.setArguments(b);
		return frag;
	}
	
	public static FullScheduleFragment newInstance(String strShuttle, String strTime)
	{
		FullScheduleFragment frag = new FullScheduleFragment();
		Bundle b = new Bundle();
		b.putString("shuttle name", strShuttle);
		b.putString("shuttle time", strTime);
		frag.setArguments(b);
		return frag;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View rootView = inflater.inflate(R.layout.full_schedule_layout, container, false);
		layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		strShuttleName = getArguments().getString("shuttle name");
		strTableName = StaticConvertMethods.convertShuttleToTable(strShuttleName);
		
		strBundleTime = getArguments().getString("shuttle time");
		shuttleDbHelper = new ShuttleDbHelper(getActivity());
		shuttleDb = shuttleDbHelper.getReadableDatabase();
		currentTime = Calendar.getInstance();
		list = (ListView) rootView.findViewById(android.R.id.list);
		
		TextView shuttleName =  (TextView) rootView.findViewById(R.id.full_schedule_name);
		shuttleName.setText(strShuttleName);

		ImageView shuttleImage = (ImageView) rootView.findViewById(R.id.full_schedule_image);
		shuttleImage.setImageResource(StaticConvertMethods.setImageBasedOnShuttle(strShuttleName));
		
		TextView description = (TextView) rootView.findViewById(R.id.full_schedule_description);
		description.setText(StaticConvertMethods.convertShuttleToDescription(strShuttleName));
		
		TextView time = (TextView) rootView.findViewById(R.id.full_schedule_time);
		time.setTypeface(Typeface.DEFAULT_BOLD);
		
		TextView extras = (TextView) rootView.findViewById(R.id.full_schedule_extra);
		if (strShuttleName.contains("Evanston Loop") && currentTime.get(Calendar.DAY_OF_WEEK)>4)
		{
			extras.setText("*Daylight savings specific times not shown*");
		}
		TextView stop = (TextView) rootView.findViewById(R.id.full_schedule_stop);
		stop.setTypeface(Typeface.DEFAULT_BOLD);
		
		CreateFullScheduleTask task = new CreateFullScheduleTask();
		task.execute();
		
		return rootView;	
	}
	
	private class CreateFullScheduleTask extends AsyncTask<Void, Void, Cursor>
	{
		String[] columns = {ShuttleDbHelper.COLUMN_LOCATION, ShuttleDbHelper.COLUMN_TIME};
		@Override
		protected Cursor doInBackground(Void... params) 
		{
			Cursor c = shuttleDb.query(strTableName, columns, null, null, null, null, null);
			return c;
		}
		@Override
		protected void onPostExecute(Cursor result) 
		{
			String location;
			String time;
			while(result.moveToNext())
			{
				location = result.getString(result.getColumnIndexOrThrow("Location"));
				time = result.getString(result.getColumnIndexOrThrow("Time"));
				listLocations.add(location);
				if (MainActivity.bTimePreference)
				{
					listTimes.add(StaticConvertMethods.convertTimeToAmPm(time));
				}
				else
				{
					listTimes.add(time);
				}
			}
			ArrayAdapter<String> adapter = new FullScheduleAdapter(getActivity(), R.layout.full_schedule_row_layout, listLocations);
			list.setAdapter(adapter);
			if (strBundleTime!=null)
			{
				int position = listTimes.indexOf(strBundleTime);
				getListView().setSelection(position);
			}
		}
	}
	
	private class FullScheduleAdapter extends ArrayAdapter<String>
	{
		ArrayList<String> locations;
		public FullScheduleAdapter(Context context, int resource, ArrayList<String> objects) 
		{
			super(context, resource, objects);
			locations = objects;
		}
		
		public View getView(int position, View view, ViewGroup parent) 
		{
			View drawerView = layoutInflater.inflate(R.layout.full_schedule_row_layout, null);
			
 			TextView locationText = (TextView) drawerView.findViewById(R.id.full_schedule_location);
 			locationText.setText(locations.get(position));
 			
 			TextView timeText = (TextView) drawerView.findViewById(R.id.full_schedule_time_left);
 			timeText.setText(listTimes.get(position));
			
			return drawerView;
		}
	}
	
	public void onResume()
	{
		getActivity().setTitle(strShuttleName);
		super.onResume();
	}

}
