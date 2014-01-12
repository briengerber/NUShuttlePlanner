package com.bgdev.nushuttleplanner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class UpcomingFragment extends ListFragment 
{
	
	ShuttleDbHelper shuttleDbHelper;
	SQLiteDatabase shuttleDb;
	ListView list;
	Spinner upcomingSpinner;
	String[] locations;
	String locationSelected;
	Calendar date;
	ArrayList<String> shuttles = new ArrayList<String>();
	ArrayList<String> selectedShuttles = new ArrayList<String>();
	ArrayList<String> selectedShuttleTimes = new ArrayList<String>();
	ArrayList<String> sortSelectedShuttles = new ArrayList<String>();
	ArrayList<String> sortSelectedShuttleTimes = new ArrayList<String>();
	LayoutInflater layoutInflater;
	OnUpcomingItemClickListener listener;
	
	UpcomingAdapter upcomeAdap;
	public UpcomingFragment()
	{
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View rootView = inflater.inflate(R.layout.upcoming_fragment_layout, container, false);
		layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		shuttleDbHelper = new ShuttleDbHelper(getActivity());
		shuttleDb = shuttleDbHelper.getReadableDatabase();
		
		list = (ListView) rootView.findViewById(android.R.id.list);
		list.setVisibility(View.INVISIBLE);

		date = Calendar.getInstance();
		
		if (shuttles.isEmpty())
		{
			if (TimeZone.getTimeZone("CST").inDaylightTime(new Date()))
			{
				if ((date.get(Calendar.DAY_OF_WEEK)>1 && date.get(Calendar.DAY_OF_WEEK)<=6))
				{
					shuttles.add(ShuttleDbHelper.TABLE_CAMPUS_LOOP_DAYLIGHTSAVINGS);
				}
			}
			else
			{
				if ((date.get(Calendar.DAY_OF_WEEK)>1 && date.get(Calendar.DAY_OF_WEEK)<=6))
				{
					shuttles.add(ShuttleDbHelper.TABLE_CAMPUS_LOOP);
				}
			}
			if (date.get(Calendar.DAY_OF_WEEK)<=4)
			{
				shuttles.add(ShuttleDbHelper.TABLE_EVANSTON_LOOP_SUN_THROUGH_WED);
			}
			else
			{
				shuttles.add(ShuttleDbHelper.TABLE_EVANSON_LOOP_THURS_THROUGH_SAT);
			}
			if (date.get(Calendar.DAY_OF_WEEK)== Calendar.SATURDAY)
			{
				shuttles.add(ShuttleDbHelper.TABLE_CHICAGO_EXPRESS_SATURDAY_SHUTTLE);
			}
			if (date.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY)
			{
				shuttles.add(ShuttleDbHelper.TABLE_SHOP_N_RIDE);
			}
			if (date.get(Calendar.DAY_OF_WEEK)>1 && date.get(Calendar.DAY_OF_WEEK)<=6)
			{
				shuttles.add(ShuttleDbHelper.TABLE_CHICAGO_TO_EVANSTON);
				shuttles.add(ShuttleDbHelper.TABLE_EVANSON_TO_CHICAGO);
				shuttles.add(ShuttleDbHelper.TABLE_FROSTBITE_EXPRESS);
				shuttles.add(ShuttleDbHelper.TABLE_FROSTBITE_SHERIDAN);
				shuttles.add(ShuttleDbHelper.TABLE_RYAN_FIELD);
			}
		}
		upcomingSpinner = (Spinner) rootView.findViewById(R.id.upcoming_spinner);
		locations = getActivity().getResources().getStringArray(R.array.upcoming_array);
		
		ArrayAdapter<String> upcomingAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, locations);
		upcomingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		upcomingSpinner.setAdapter(upcomingAdapter);
		upcomingSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id)  
			{
				selectedShuttles.clear();
				selectedShuttleTimes.clear();
				locationSelected = (String) parent.getItemAtPosition(position);
				String selection = "location = " + "'" + locationSelected + "'";
				String[] columns = {ShuttleDbHelper.COLUMN_LOCATION, ShuttleDbHelper.COLUMN_TIME, ShuttleDbHelper.COLUMN_SHUTTLES};
				for (String shuttle: shuttles)
				{
					Cursor upcomingCursor = shuttleDb.query(shuttle, columns, selection, null, null, null, null);
					
					while(upcomingCursor.moveToNext())
					{	
						String shuttleTime = upcomingCursor.getString(1);
						if (upcomingCursor.getCount()>0)
						{
							if (StaticConvertMethods.differenceBetweenTimesNoBefore(StaticConvertMethods.getCurrentTime(), shuttleTime)>=0 && StaticConvertMethods.differenceBetweenTimesNoBefore(StaticConvertMethods.getCurrentTime(), shuttleTime)<=3600000)
							{
								selectedShuttles.add(StaticConvertMethods.convertTableToShuttle(shuttle));
								selectedShuttleTimes.add(shuttleTime);
							}
						}
					}
				}
				sortShuttlesByTime();
				if (MainActivity.bTimePreference)
				{
					selectedShuttleTimes = StaticConvertMethods.convertArrayListTime(selectedShuttleTimes);
				}
				upcomeAdap = new UpcomingAdapter(getActivity(), R.layout.upcoming_row_layout, selectedShuttles);
				list.setAdapter(upcomeAdap);
				list.setVisibility(View.VISIBLE);
				upcomeAdap.notifyDataSetChanged();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {	
			}});
		
		return rootView;
	}
	
	private void sortShuttlesByTime()
	{
		if (selectedShuttleTimes.size()>1)
		{
			int j=1;
			while (j>0)
			{
				j=0;
				for(int i=0; i<selectedShuttleTimes.size()-1; i++)
				{
					if(!StaticConvertMethods.isFirstTimeBeforeSecondTime(selectedShuttleTimes.get(i), selectedShuttleTimes.get(i+1)))
					{
						String firstTime = selectedShuttleTimes.get(i);
						String secondTime = selectedShuttleTimes.get(i+1);
						selectedShuttleTimes.set(i, secondTime);
						selectedShuttleTimes.set(i+1, firstTime);
						String firstShuttle = selectedShuttles.get(i);
						String secondShuttle = selectedShuttles.get(i+1);
						selectedShuttles.set(i, secondShuttle);
						selectedShuttles.set(i+1, firstShuttle);
						j++;
					}
					else
					{
						if (i==(selectedShuttleTimes.size()-2))
						{
							if (j==0)
							{
								break;
							}
							else
							{
								j++;
							}
						}
					}
				}
			}
		}
	}
	
	public void onResume()
	{
		selectedShuttles.clear();
		selectedShuttleTimes.clear();
		getActivity().setTitle("Upcoming");
		super.onResume();
	}
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		try
		{
			listener = (OnUpcomingItemClickListener) activity;
		}
		catch (ClassCastException c)
		{

		}
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) 
	{
		listener.onUpcomingItemClicked(selectedShuttles.get(position), selectedShuttleTimes.get(position));
	}
	
	private class UpcomingAdapter extends ArrayAdapter<String>
	{
		ArrayList<String> shuttleTables = new ArrayList<String>();
		public UpcomingAdapter(Context context, int resource, ArrayList<String> tables) 
		{
			super(context, resource, tables);
			shuttleTables = tables;
		}

		@Override
		//Gets view is called each time its loads
		public View getView(int position, View view, ViewGroup parent) 
		{	
 			View upcomingView = layoutInflater.inflate(R.layout.upcoming_row_layout, null);
 			
 			TextView txtShuttle = (TextView) upcomingView.findViewById(R.id.upcoming_shuttle);
 			txtShuttle.setText(shuttleTables.get(position));
 			
 			TextView txtTime = (TextView) upcomingView.findViewById(R.id.upcoming_time);
 			txtTime.setText(selectedShuttleTimes.get(position));
			
 			ImageView upcomingImage = (ImageView) upcomingView.findViewById(R.id.upcoming_image);
 			upcomingImage.setImageResource(StaticConvertMethods.setSquareImageBasedOnShuttle(shuttleTables.get(position)));
 			
			return upcomingView;
		}

	} 
	
	public interface OnUpcomingItemClickListener
	{
		public void onUpcomingItemClicked(String strShuttle, String strTime);
	}
	
}
