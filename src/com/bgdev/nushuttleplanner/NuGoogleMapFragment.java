package com.bgdev.nushuttleplanner;

import java.util.ArrayList;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class NuGoogleMapFragment extends SupportMapFragment 
{
	NuGoogleMapFragment googleMapFragment;
	GoogleMap mMap;
	ShuttleDbHelper shuttleDbHelper;
	SQLiteDatabase shuttleDb;
	String[] listOfLocations;
	LayoutInflater layoutInflater;
	public NuGoogleMapFragment()
	{
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		setHasOptionsMenu(true);
		getActivity().setTitle("Map");
		layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		shuttleDbHelper = new ShuttleDbHelper(getActivity());
		listOfLocations = getActivity().getResources().getStringArray(R.array.locations_array);
		shuttleDb = shuttleDbHelper.getReadableDatabase();
		View v = super.onCreateView(inflater, container, savedInstanceState);
		return v;
	}

	

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) 
	{
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		mMap = getMap();
		setUp();
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onResume()
	{
		getActivity().setTitle("Map");
		super.onResume();
	}
	
	public void setUp()
	{
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
		if(status == ConnectionResult.SUCCESS) 
		{
			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(42.054073,-87.674939), 14.5f));
			mMap.setIndoorEnabled(true);
			mMap.setMyLocationEnabled(true);
			addAllMarkers(mMap);
		}
	}
	
	private void addAllMarkers(GoogleMap map)
	{
		for (String location : listOfLocations)
		{
			mMap.addMarker(new MarkerOptions().position(StaticConvertMethods.convertLocationToLatLng(location)).title(location)
					.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
			
			mMap.setInfoWindowAdapter(new InfoWindowAdapter(){
				@Override
				public View getInfoContents(Marker marker) 
				{
					View v = layoutInflater.inflate(R.layout.info_window_layout, null);

		            // Getting reference to the TextView to set title
		            TextView title = (TextView) v.findViewById(R.id.info_window_title);
		            title.setText(marker.getTitle());
		            title.setTypeface(Typeface.DEFAULT_BOLD);
		            
		            ArrayList<String> snippet = NuShuttleApplication.markerHash.get(marker.getTitle());
		            ListView list = (ListView) v.findViewById(android.R.id.list); 
		            list.setAdapter(new CustomInfoWindowAdapter(getActivity(), R.id.info_window_row_info,StaticConvertMethods.convertEachTableToShuttle(snippet)));
		            return v;
				}

				@Override
				public View getInfoWindow(Marker arg0) {
					// TODO Auto-generated method stub
					return null;
				}
			});
		}
	}	
	
	
	
	private class CustomInfoWindowAdapter extends ArrayAdapter<String>
	{
		ArrayList<String> infoTables = new ArrayList<String>();
		public CustomInfoWindowAdapter(Context context, int resource, ArrayList<String> tables) 
		{
			super(context, resource, tables);
			infoTables = tables;
		}

		@Override
		//Gets view is called each time its loads
		public View getView(int position, View view, ViewGroup parent) 
		{	
 			View infoView = layoutInflater.inflate(R.layout.info_window_row_layout, null);
 			
 			TextView info = (TextView) infoView.findViewById(R.id.info_window_row_info);
 			info.setText(infoTables.get(position));
 			
 			ImageView image = (ImageView) infoView.findViewById(R.id.info_window_image);
 			image.setImageResource(StaticConvertMethods.setSquareImageBasedOnShuttle(infoTables.get(position)));
 			
			return infoView;
		}
	}
	
	
}
