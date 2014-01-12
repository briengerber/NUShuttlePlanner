package com.bgdev.nushuttleplanner;

import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MergeCursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bgdev.nushuttleplanner.UpcomingFragment.OnUpcomingItemClickListener;

public class ResultsFragment extends ListFragment implements OnClickListener
{

	LayoutInflater layoutInflater;
	String[] tables;
	ListView list;
	Spinner startLocationSpinner, endLocationSpinner;
	
	ShuttleDbHelper shuttleDbHelper;
	SQLiteDatabase shuttleDb, favoriteDb;
	FavoritesDbHelper favoriteDbHelper;
	
	Cursor spinnerCursor;
	MergeCursor mergeSecondLocationCursor, startMergeCursor, endMergeCursor;
	
	ArrayList<Cursor> listTableCursors = new ArrayList<Cursor>();
	ArrayList<Cursor> listSecondLocCursors = new ArrayList<Cursor>();
	ArrayList<String> listLocations = new ArrayList<String>();
	ArrayList<String> listSecondLocations = new ArrayList<String>();
	ArrayList<String> listTables = new ArrayList<String>();
	
	ArrayList<Cursor> listStartCursor = new ArrayList<Cursor>();
	ArrayList<Cursor> listEndCursor = new ArrayList<Cursor>();
	ArrayList<String> startTimeList = new ArrayList<String>();
	ArrayList<String> endTimeList = new ArrayList<String>();
	ArrayList<String> shuttleList = new ArrayList<String>();
	ArrayList<String> startTimeArrayList = new ArrayList<String>();
	ArrayList<String> endTimeArrayList = new ArrayList<String>();

	OnUpcomingItemClickListener listener;
	String startLocationSelected, endLocationSelected,endLoc, startLoc;
	TextView emptyText;
	long maxDifference = 3;
//	boolean bCreated = false;
//	boolean bSwitched = false;
	boolean bCreated, bSwitched;
	int endToStartPosition, restoredStart, restoredEnd;
	
	TextView headerText;
	public static ResultsFragment newInstance(String[] tables)
	{
		ResultsFragment frag = new ResultsFragment();
		Bundle b = new Bundle();
		b.putStringArray("tables", tables);
		frag.setArguments(b);
		return frag;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View rootView = inflater.inflate(R.layout.results_fragment_layout, container, false);
		layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		tables = getArguments().getStringArray("tables");

		emptyText = (TextView) rootView.findViewById(android.R.id.empty);
		headerText = (TextView) rootView.findViewById(R.id.results_header);
		headerText.setVisibility(View.INVISIBLE);
		
		favoriteDbHelper = new FavoritesDbHelper(getActivity());
		favoriteDb = favoriteDbHelper.getWritableDatabase();
		
		startLocationSpinner = (Spinner) rootView.findViewById(R.id.spin_start_location);
		endLocationSpinner = (Spinner) rootView.findViewById(R.id.spin_end_location);
		
		Button searchButton = (Button) rootView.findViewById(R.id.search_button);
		searchButton.setOnClickListener(this);
		
		ImageButton switchButton = (ImageButton) rootView.findViewById(R.id.switch_button);
		switchButton.setOnClickListener(this);
		
		shuttleDbHelper = new ShuttleDbHelper(getActivity());
		shuttleDb = shuttleDbHelper.getReadableDatabase();
		
		list = (ListView) rootView.findViewById(android.R.id.list);
		list.setVisibility(View.INVISIBLE);
				
		CreateStartLocationDataBaseTask task = new CreateStartLocationDataBaseTask();
		task.execute();
					
		return rootView;
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
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		//setRetainInstance(true);
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onClick(View v) 
	{
		if (v.getId()==R.id.search_button)
		{
			shuttleList.clear();
			startTimeArrayList.clear();
			endTimeArrayList.clear();
			listStartCursor.clear();
			listEndCursor.clear();
			startTimeList.clear();
			endTimeList.clear();
			
			ShuttlePlannerTask searchTask = new ShuttlePlannerTask();
			searchTask.execute();
			
		}
		else
		{
			ArrayAdapter<String> startAdap = (ArrayAdapter) startLocationSpinner.getAdapter();
			ArrayAdapter<String> endAdap = (ArrayAdapter) endLocationSpinner.getAdapter();
			
			startLoc = (String) startLocationSpinner.getSelectedItem();
			endLoc = (String) endLocationSpinner.getSelectedItem();
			startLocationSpinner.setSelection(startAdap.getPosition(endLoc));
			endLocationSpinner.setSelection(endAdap.getPosition(startLoc));
			bSwitched = true;
		}
	}
	
	private class CreateStartLocationDataBaseTask extends AsyncTask<Void, Void, MergeCursor>
	{
		@Override
		protected MergeCursor doInBackground(Void... params) 
		{
			String[] columns = {ShuttleDbHelper.COLUMN_LOCATION, ShuttleDbHelper.COLUMN_TIME, ShuttleDbHelper.COLUMN_SHUTTLES};
			for(int i = 0; i <tables.length; i++)
			{
				spinnerCursor = shuttleDb.query(tables[i], columns, null, null, null, null, null);
				listTableCursors.add(spinnerCursor);
			}
			
			return new MergeCursor(listTableCursors.toArray(new Cursor[listTableCursors.size()]));
		}

		@Override
		protected void onPostExecute(MergeCursor result) 
		{
			result.moveToFirst();
			while(result.moveToNext())
			{
				String location = result.getString(result.getColumnIndexOrThrow("Location"));
				
				if(!listLocations.contains(location) && !location.contains("Arrive"))
				{
					listLocations.add(location);
				}
			}
			Collections.sort(listLocations);
			ArrayAdapter<String> spinnerDataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, listLocations);
			spinnerDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			startLocationSpinner.setAdapter(spinnerDataAdapter);
			startLocationSpinner.setOnItemSelectedListener(new OnItemSelectedListener()
			{
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) 
				{
					listSecondLocCursors.clear();
					listSecondLocations.clear();
					listTables.clear();
					startLocationSelected = (String) parent.getItemAtPosition(position);
					for(Cursor secondCursor: listTableCursors)
					{
						secondCursor.moveToFirst();
						while(secondCursor.moveToNext())
						{
							if(startLocationSelected.equals(secondCursor.getString(secondCursor.getColumnIndexOrThrow("Location"))))
							{
								if(!listSecondLocCursors.contains(secondCursor))
								{
									listSecondLocCursors.add(secondCursor);
									listTables.add(StaticConvertMethods.convertShuttleToTable(secondCursor.getString(secondCursor.getColumnIndexOrThrow("Shuttle"))));
								}
							}
						}
					}
					
					mergeSecondLocationCursor = new MergeCursor(listSecondLocCursors.toArray(new Cursor[listSecondLocCursors.size()]));
					mergeSecondLocationCursor.moveToFirst();
					while(mergeSecondLocationCursor.moveToNext())
					{
						String secondLocation = mergeSecondLocationCursor.getString(mergeSecondLocationCursor.getColumnIndexOrThrow("Location"));
						
						if(!listSecondLocations.contains(secondLocation))
						{
							listSecondLocations.add(secondLocation);
						}
					}
					Collections.sort(listSecondLocations);
					ArrayAdapter<String> secondSpinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, listSecondLocations);
					secondSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					endLocationSpinner.setAdapter(secondSpinnerAdapter);
					if (bSwitched)
					{
						endLocationSpinner.setSelection(secondSpinnerAdapter.getPosition(startLoc));
					}
					secondSpinnerAdapter.notifyDataSetChanged();
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) 
				{
					
				}
				
			});
			
			endLocationSpinner.setOnItemSelectedListener(new OnItemSelectedListener()
			{
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) 
				{
					endLocationSelected = (String) parent.getItemAtPosition(position);
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) 
				{
					
				}
				
			});
		}	
	}

	private class ShuttleAdapter extends ArrayAdapter<String>
	{
		ArrayList<String> shuttleTables = new ArrayList<String>();
		public ShuttleAdapter(Context context, int resource, ArrayList<String> tables) 
		{
			super(context, resource, tables);
			shuttleTables = tables;
		}

		@Override
		//Gets view is called each time its loads
		public View getView(int position, View view, ViewGroup parent) 
		{	
 			View shuttleView = layoutInflater.inflate(R.layout.shuttle_row, null);
 			
 			TextView shuttleText = (TextView) shuttleView.findViewById(R.id.which_shuttle);
 			shuttleText.setText(shuttleTables.get(position));

			TextView startLocationText =  (TextView) shuttleView.findViewById(R.id.starting_shuttle_location);
			startLocationText.setText(startLocationSelected);
			
			TextView endLocationText =  (TextView) shuttleView.findViewById(R.id.ending_shuttle_location);
			endLocationText.setText(endLocationSelected);

			TextView startTimeText =  (TextView) shuttleView.findViewById(R.id.departing_time);
			startTimeText.setText(startTimeArrayList.get(position));
			
			TextView endTimeText =  (TextView) shuttleView.findViewById(R.id.arriving_time);
			endTimeText.setText(endTimeArrayList.get(position));	
			
			ImageView pictureImage = (ImageView) shuttleView.findViewById(R.id.shuttle_image);
			pictureImage.setImageResource(StaticConvertMethods.setImageBasedOnShuttle(shuttleTables.get(position)));
			
			return shuttleView;
		}

	}

	private class ListClickHandler implements OnItemClickListener
	{
		@Override
		public void onItemClick(AdapterView<?> Adaptor, View View, int position, long nArg3) 
		{
			listener.onUpcomingItemClicked(shuttleList.get(position), startTimeArrayList.get(position));
		}
	}
	
	private class ListLongPressListener implements OnItemLongClickListener
	{
		@Override
		public boolean onItemLongClick(AdapterView<?> adapter, View view, int position, long arg3) 
		{
			Toast.makeText(getActivity(), "Added to favorites", Toast.LENGTH_SHORT).show();
			WriteToFavoritesDbTask writeToDbTask = new WriteToFavoritesDbTask();
			writeToDbTask.execute(position);
			return true;
		}
		
	}

	private class ShuttlePlannerTask extends AsyncTask<Void, Void, Boolean>
	{
		@Override
		protected void onPostExecute(Boolean results)
		{
			ArrayAdapter<String> adapter = new ShuttleAdapter(getActivity(), R.layout.shuttle_row, shuttleList);
			if(!results.booleanValue())
			{	
				headerText.setVisibility(View.VISIBLE);	
			}
			list.setAdapter(adapter);
			list.setOnItemClickListener(new ListClickHandler());
			list.setOnItemLongClickListener(new ListLongPressListener());
			adapter.notifyDataSetChanged();
			list.setVisibility(View.VISIBLE);
			if (results.booleanValue())
			{
				headerText.setVisibility(View.INVISIBLE);
				emptyText.setText("No shuttles match the search at this time. Swipe from the right to see the full schedule.");
			}
		}
		
		@Override
		protected Boolean doInBackground(Void... params) 
		{
			String startSelection = "location = '" + startLocationSelected + "'";
			String endSelection = "location = '" + endLocationSelected + "'";
			for(int i=0; i<listTables.size(); i++)
			{
				Cursor startShuttleCursor = shuttleDb.query(listTables.get(i), null, startSelection, null, null, null, null);
				Cursor endShuttleCursor = shuttleDb.query(listTables.get(i), null, endSelection, null, null, null, null);
				if (startShuttleCursor.moveToFirst() && endShuttleCursor.moveToFirst())
				{
					if (startShuttleCursor.getCount()>0 && endShuttleCursor.getCount()>0)
					{
						listStartCursor.add(startShuttleCursor);
						listEndCursor.add(endShuttleCursor);
					}
				}
			}
			startMergeCursor = new MergeCursor(listStartCursor.toArray(new Cursor[listStartCursor.size()]));
			endMergeCursor = new MergeCursor(listEndCursor.toArray(new Cursor[listEndCursor.size()]));
			boolean isStartBeforeEnd = false;
			
			String startShuttle = startMergeCursor.getString(3);
			isStartBeforeEnd = StaticConvertMethods.isFirstTimeBeforeSecondTime(startMergeCursor.getString(2) ,endMergeCursor.getString(2));
			if(isStartBeforeEnd)
			{
				String firstTime = startMergeCursor.getString(2);
				String secondTime = endMergeCursor.getString(2);
				while(startMergeCursor.moveToNext() && endMergeCursor.moveToNext())
				{
					if (MainActivity.bNotAllDay)
					{
						if(!StaticConvertMethods.isFirstTimeBeforeSecondTime(firstTime,StaticConvertMethods.getCurrentTime()) && startShuttle.equals(startShuttle))
						{
							startTimeList.add(firstTime);
							endTimeList.add(secondTime);
							shuttleList.add(startShuttle);
						}
						startShuttle = startMergeCursor.getString(3);
						firstTime = startMergeCursor.getString(2);
						secondTime = endMergeCursor.getString(2);
						if (StaticConvertMethods.isFirstTimeBeforeSecondTime(firstTime, "4:00"))
						{
							startTimeList.add(firstTime);
							endTimeList.add(secondTime);
							shuttleList.add(startShuttle);
						}
					}
					
					else
					{
						startTimeList.add(startMergeCursor.getString(2));
						endTimeList.add(endMergeCursor.getString(2));
						shuttleList.add(startMergeCursor.getString(3));
					}
				}
			}
			else
			{
				endMergeCursor.moveToNext();
				String firstTime = startMergeCursor.getString(2);
				while(endMergeCursor.moveToNext() && startMergeCursor.moveToNext())
				{
					if (!startMergeCursor.getString(3).contains("Inter"))
					{
						if (MainActivity.bNotAllDay)
						{
							if(!StaticConvertMethods.isFirstTimeBeforeSecondTime(startMergeCursor.getString(2),StaticConvertMethods.getCurrentTime()) &&StaticConvertMethods.isFirstTimeBeforeSecondTime(startMergeCursor.getString(2),endMergeCursor.getString(2)))
							{
								startTimeList.add(startMergeCursor.getString(2));
								endTimeList.add(endMergeCursor.getString(2));
								shuttleList.add(startMergeCursor.getString(3));
							}
							startShuttle = endMergeCursor.getString(3);
							firstTime = startMergeCursor.getString(2);
							
							if (StaticConvertMethods.isFirstTimeBeforeSecondTime(firstTime, "4:00") && StaticConvertMethods.differenceBetweenTimes(startMergeCursor.getString(2), endMergeCursor.getString(2)) < maxDifference)
							{
								startTimeList.add(startMergeCursor.getString(2));
								endTimeList.add(endMergeCursor.getString(2));
								shuttleList.add(startMergeCursor.getString(3));
							}
						}
						else
						{
							if (StaticConvertMethods.isFirstTimeBeforeSecondTime(startMergeCursor.getString(2),endMergeCursor.getString(2)) && StaticConvertMethods.differenceBetweenTimes(startMergeCursor.getString(2), endMergeCursor.getString(2)) < maxDifference)
							{
								startTimeList.add(startMergeCursor.getString(2));
								endTimeList.add(endMergeCursor.getString(2));
								shuttleList.add(startMergeCursor.getString(3));
							}
						}
					}	
				}
			}
			if(startTimeList.isEmpty())
			{
				return Boolean.TRUE;
			}
			
			if (MainActivity.bTimePreference)
			{
				startTimeArrayList = StaticConvertMethods.convertArrayListTime(startTimeList);
				endTimeArrayList = StaticConvertMethods.convertArrayListTime(endTimeList);
			}
			else
			{
				startTimeArrayList = startTimeList;
				endTimeArrayList = endTimeList;
			}
			
			return Boolean.FALSE;
		}
	}
	
	private class WriteToFavoritesDbTask extends AsyncTask<Integer, Void, Void>
	{
		@Override
		protected Void doInBackground(Integer... params) 
		{
			ContentValues cv = new ContentValues();
			int position = params[0];
			cv.put(FavoritesDbHelper.SHUTTLE, shuttleList.get(position));
			cv.put(FavoritesDbHelper.FIRST_LOCATION, startLocationSelected);
			cv.put(FavoritesDbHelper.FIRST_TIME, startTimeArrayList.get(position));
			cv.put(FavoritesDbHelper.SECOND_LOCATION, endLocationSelected);
			cv.put(FavoritesDbHelper.SECOND_TIME, endTimeArrayList.get(position));
			cv.put(FavoritesDbHelper.IMAGE, StaticConvertMethods.setImageBasedOnShuttle(shuttleList.get(position)));
	
			ContentResolver content = getActivity().getContentResolver();
			content.insert(FavoritesProvider.FAVORITES_URI, cv);
			return null;
		}	
	}
	
	@Override
	public void onResume()
	{
		getActivity().setTitle("Search");
		super.onResume();
	}

}
