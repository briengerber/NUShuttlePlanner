package com.bgdev.nushuttleplanner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.polites.android.GestureImageView;

public class NuMapFragment extends Fragment 
{
	public static final String _MAP_FRAGMENT_TITLE = "Overview";
	public GestureImageView imageView;
	View rootView;
	boolean bMapLoaded = false;
	BitmapDrawable d;
	Bitmap bmp;
	
    public NuMapFragment() 
    {
        // Empty constructor required for fragment subclasses
    }

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView  = inflater.inflate(R.layout.map_fragment_layout, container, false);
		imageView = (GestureImageView) rootView.findViewById(R.id.map_image);
        getActivity().setTitle(_MAP_FRAGMENT_TITLE);

        if (savedInstanceState==null)
        {
        	SaveMapTask load = new SaveMapTask();
        	load.execute();
        }
        else
        {
        	bmp = (Bitmap) savedInstanceState.getParcelable("bitmap");
			d = new BitmapDrawable(getResources(), bmp);
			imageView.setImageDrawable(d);
        }
        return rootView;
    }

	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onResume()
	{
		getActivity().setTitle(_MAP_FRAGMENT_TITLE);
		super.onResume();
	}

	private class SaveMapTask extends AsyncTask<Void,Void,Bitmap>
	{
		@Override
		protected Bitmap doInBackground(Void... arg0) 
		{
	        BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inSampleSize=2;
	        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.shuttle_overview, options);
			return bmp;
		}
		protected void onPostExecute(Bitmap result) 
		{
			d = new BitmapDrawable(getResources(), result);
			imageView.setImageDrawable(d);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) 
	{
		outState.putParcelable("bitmap", bmp);
	}

}
