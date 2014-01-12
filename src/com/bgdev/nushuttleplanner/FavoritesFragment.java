package com.bgdev.nushuttleplanner;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.bgdev.nushuttleplanner.UpcomingFragment.OnUpcomingItemClickListener;

public class FavoritesFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>
{	
	OnUpcomingItemClickListener listener;
	LayoutInflater fragmentInflater;
	ListView list;
	
	FavoritesDbHelper favoriteDbHelper;
	SQLiteDatabase favoriteDb;
	
	Cursor favoriteCursor;
	SimpleCursorAdapter mAdapter;

    public FavoritesFragment() 
    {
        // Empty constructor required for fragment subclasses
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View rootView = inflater.inflate(R.layout.favorite_shuttles_layout, container, false);
		favoriteDbHelper = new FavoritesDbHelper(getActivity());
		favoriteDb = favoriteDbHelper.getWritableDatabase();	
		favoriteCursor = favoriteDb.query(FavoritesDbHelper.TABLE_NAME, null, null, null, null, null, null);
		list = (ListView) rootView.findViewById(android.R.id.list);

		String[] from = {FavoritesDbHelper.SHUTTLE, FavoritesDbHelper.FIRST_LOCATION, FavoritesDbHelper.FIRST_TIME,
				FavoritesDbHelper.SECOND_LOCATION, FavoritesDbHelper.SECOND_TIME, FavoritesDbHelper.IMAGE};
		
		int[] to = {R.id.which_shuttle, R.id.starting_shuttle_location, R.id.departing_time, R.id.ending_shuttle_location,
				R.id.arriving_time, R.id.shuttle_image};
		
		mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.shuttle_row, favoriteCursor, from, to, 0);
		View header = inflater.inflate(R.layout.favorites_header, null);
		if(list.getHeaderViewsCount()==0)
		{
			list.addHeaderView(header, "header", false);
		}
		setListAdapter(mAdapter);
		list.setOnItemLongClickListener(new ListLongPressListener());
		return rootView;	
	}
	
	public void onResume()
	{
		getActivity().setTitle("Favorites");
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
		if (position>0)
		{
			Cursor search = mAdapter.getCursor();
			search.moveToPosition(position-1);
			listener.onUpcomingItemClicked(search.getString(search.getColumnIndex("shuttle")), search.getString(search.getColumnIndex("first_time")));
			search.close();
		}
	}
	
	private class ListLongPressListener implements OnItemLongClickListener
	{
		@Override
		public boolean onItemLongClick(AdapterView<?> adapter, View view, int position, long arg3) 
		{
			Toast.makeText(getActivity(), "Removed from favorites", Toast.LENGTH_SHORT).show();
			deleteFavoriteRow(position-1);
			mAdapter.changeCursor(favoriteDb.query(FavoritesDbHelper.TABLE_NAME, null, null, null, null, null, null));
			mAdapter.notifyDataSetChanged();
			list.invalidateViews();
			return true;
		}
		
		private boolean deleteFavoriteRow(int rowPosition)
		{
			Cursor clickedCursor = (Cursor) mAdapter.getItem(rowPosition);
			String iD = clickedCursor.getString(0);
			clickedCursor.close();
			return favoriteDb.delete(FavoritesDbHelper.TABLE_NAME, FavoritesDbHelper.C_ID + "=" + iD, null)>0;
		}
	}

	@Override
	public void onDetach() 
	{
		super.onDetach();
		favoriteCursor.close();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) 
	{
		String[] columns = {FavoritesDbHelper.C_ID,FavoritesDbHelper.SHUTTLE, FavoritesDbHelper.FIRST_LOCATION, FavoritesDbHelper.FIRST_TIME,
				FavoritesDbHelper.SECOND_LOCATION, FavoritesDbHelper.SECOND_TIME, FavoritesDbHelper.IMAGE};
		
		return new CursorLoader(getActivity(), FavoritesProvider.FAVORITES_URI, columns, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) 
	{
		mAdapter.swapCursor(cursor);
	}
	@Override
	public void onLoaderReset(Loader<Cursor> arg0) 
	{
		mAdapter.swapCursor(null);
	}
	
}
