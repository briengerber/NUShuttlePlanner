package com.bgdev.nushuttleplanner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

public class HomeFragment extends Fragment implements OnClickListener
{
	OnHomePageSelectedListener activityListener;
	public static ArrayList<String> listDbNames = new ArrayList<String>();
	private int[] homeFragmentButtonIds = {R.id.cb_all, R.id.cb_chicago_express_saturday, R.id.cb_chicago_intercampus,
			R.id.cb_evanston_intercampus, R.id.cb_evanston_loop, R.id.cb_campus_loop,
			R.id.cb_frostbite_express, R.id.cb_frostbite_sheridan,R.id.cb_ryan_field, R.id.cb_shop_n_ride};
	
	Calendar currentdate;
    public HomeFragment() 
    {
        // Empty constructor required for fragment subclasses
    }
    
    public static HomeFragment newInstance()
    {
    	HomeFragment frag = new HomeFragment();
    	listDbNames.clear();
		return frag;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_main, container, false);
        for (int i = 0; i< homeFragmentButtonIds.length; i++)
        {
        	CheckBox box = (CheckBox) rootView.findViewById(homeFragmentButtonIds[i]);
        	box.setOnClickListener(this);
        }
        Button continueButton = (Button) rootView.findViewById(R.id.button_select_shuttles);
        continueButton.setOnClickListener(this);
       
        currentdate = Calendar.getInstance();
        return rootView;
    }

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		//If i want more options menu do this
		//setHasOptionsMenu(mAdded);
		
		//Then override onCreateOptionsMenu() and onPrepareOptionsMenu() and inflate just like an activity
	}
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		try
		{
			activityListener = (OnHomePageSelectedListener) activity;
		}
		catch (ClassCastException c)
		{

		}
	}
    
	@Override
	public void onResume()
	{
		getActivity().setTitle("Planner");
		super.onResume();
	}

	public interface OnHomePageSelectedListener
	{
		public void onContinueButtonSelected();
	}

	@Override
	public void onClick(View view) 
	{
		boolean checked = false;
		if (view.getId()!=R.id.button_select_shuttles)
		{
			checked = ((CheckBox) view).isChecked();
		}
		switch(view.getId())
		{
		case R.id.cb_all:
			if(checked)
			{
				listDbNames.clear();
				listDbNames.add(ShuttleDbHelper.TABLE_CHICAGO_EXPRESS_SATURDAY_SHUTTLE);
				listDbNames.add(ShuttleDbHelper.TABLE_EVANSON_TO_CHICAGO);
				listDbNames.add(ShuttleDbHelper.TABLE_FROSTBITE_EXPRESS);
				listDbNames.add(ShuttleDbHelper.TABLE_FROSTBITE_SHERIDAN);
				listDbNames.add(ShuttleDbHelper.TABLE_RYAN_FIELD);
				listDbNames.add(ShuttleDbHelper.TABLE_SHOP_N_RIDE);
				listDbNames.add(ShuttleDbHelper.TABLE_CHICAGO_TO_EVANSTON);
				if (TimeZone.getTimeZone("CST").inDaylightTime(new Date()))
				{
					listDbNames.add(ShuttleDbHelper.TABLE_CAMPUS_LOOP_DAYLIGHTSAVINGS);
				}
				else 
				{
					listDbNames.add(ShuttleDbHelper.TABLE_CAMPUS_LOOP);
				}
				if (currentdate.get(Calendar.DAY_OF_WEEK)<=4)
				{
					listDbNames.add(ShuttleDbHelper.TABLE_EVANSTON_LOOP_SUN_THROUGH_WED);
				}
				else
				{
					listDbNames.add(ShuttleDbHelper.TABLE_EVANSON_LOOP_THURS_THROUGH_SAT);
				}
			}
			else
			{
				listDbNames.clear();
			}
			break;
		case R.id.cb_chicago_express_saturday:
			if(checked)
			{
				if (listDbNames.contains(ShuttleDbHelper.TABLE_CHICAGO_EXPRESS_SATURDAY_SHUTTLE))
				{
					listDbNames.remove(ShuttleDbHelper.TABLE_CHICAGO_EXPRESS_SATURDAY_SHUTTLE);
				}
				listDbNames.add(ShuttleDbHelper.TABLE_CHICAGO_EXPRESS_SATURDAY_SHUTTLE);
			}
			else
			{
				listDbNames.remove(ShuttleDbHelper.TABLE_CHICAGO_EXPRESS_SATURDAY_SHUTTLE);
			}
			break;
		case R.id.cb_chicago_intercampus:
			if(checked)
			{
				if (listDbNames.contains(ShuttleDbHelper.TABLE_CHICAGO_TO_EVANSTON))
				{
					listDbNames.remove(ShuttleDbHelper.TABLE_CHICAGO_TO_EVANSTON);
				}
				listDbNames.add(ShuttleDbHelper.TABLE_CHICAGO_TO_EVANSTON);
			}
			else
			{
				listDbNames.remove(ShuttleDbHelper.TABLE_CHICAGO_TO_EVANSTON);
			}
			break;
		case R.id.cb_evanston_intercampus:
			if(checked)
			{
				if (listDbNames.contains(ShuttleDbHelper.TABLE_EVANSON_TO_CHICAGO))
				{
					listDbNames.remove(ShuttleDbHelper.TABLE_EVANSON_TO_CHICAGO);
				}
				listDbNames.add(ShuttleDbHelper.TABLE_EVANSON_TO_CHICAGO);
			}
			else
			{
				listDbNames.remove(ShuttleDbHelper.TABLE_EVANSON_TO_CHICAGO);
			}
			break;
		case R.id.cb_evanston_loop:
			if(checked)
			{
				if (currentdate.get(Calendar.DAY_OF_WEEK)<=4)
				{
					if (listDbNames.contains(ShuttleDbHelper.TABLE_EVANSTON_LOOP_SUN_THROUGH_WED))
					{
						listDbNames.remove(ShuttleDbHelper.TABLE_EVANSTON_LOOP_SUN_THROUGH_WED);
					}
					listDbNames.add(ShuttleDbHelper.TABLE_EVANSTON_LOOP_SUN_THROUGH_WED);
				}
				else
				{
					if (listDbNames.contains(ShuttleDbHelper.TABLE_EVANSON_LOOP_THURS_THROUGH_SAT))
					{
						listDbNames.remove(ShuttleDbHelper.TABLE_EVANSON_LOOP_THURS_THROUGH_SAT);
					}
					listDbNames.add(ShuttleDbHelper.TABLE_EVANSON_LOOP_THURS_THROUGH_SAT);
				}
			}
			else
			{
				if (currentdate.get(Calendar.DAY_OF_WEEK)<=4)
				{
					listDbNames.remove(ShuttleDbHelper.TABLE_EVANSTON_LOOP_SUN_THROUGH_WED);
				}
				else
				{
					listDbNames.remove(ShuttleDbHelper.TABLE_EVANSON_LOOP_THURS_THROUGH_SAT);
				}
			}
			break;
		case R.id.cb_campus_loop:
			if(checked)
			{
				if (TimeZone.getTimeZone("CST").inDaylightTime(new Date()))
				{
					if (listDbNames.contains(ShuttleDbHelper.TABLE_CAMPUS_LOOP_DAYLIGHTSAVINGS))
					{
						listDbNames.remove(ShuttleDbHelper.TABLE_CAMPUS_LOOP_DAYLIGHTSAVINGS);
					}
					listDbNames.add(ShuttleDbHelper.TABLE_CAMPUS_LOOP_DAYLIGHTSAVINGS);
				}
				else
				{
					if (listDbNames.contains(ShuttleDbHelper.TABLE_CAMPUS_LOOP))
					{
						listDbNames.remove(ShuttleDbHelper.TABLE_CAMPUS_LOOP);
					}
					listDbNames.add(ShuttleDbHelper.TABLE_CAMPUS_LOOP);
				}
			}
			else
			{
				if (TimeZone.getTimeZone("CST").inDaylightTime(new Date()))
				{
					listDbNames.remove(ShuttleDbHelper.TABLE_CAMPUS_LOOP_DAYLIGHTSAVINGS);
				}
				else
				{
					listDbNames.remove(ShuttleDbHelper.TABLE_CAMPUS_LOOP);
				}
			}
			break;
		case R.id.cb_frostbite_express:
			if(checked)
			{
				if (listDbNames.contains(ShuttleDbHelper.TABLE_FROSTBITE_EXPRESS))
				{
					listDbNames.remove(ShuttleDbHelper.TABLE_FROSTBITE_EXPRESS);
				}
				listDbNames.add(ShuttleDbHelper.TABLE_FROSTBITE_EXPRESS);
			}
			else
			{
				listDbNames.remove(ShuttleDbHelper.TABLE_FROSTBITE_EXPRESS);
			}
			break;
		case R.id.cb_frostbite_sheridan:
			if(checked)
			{
				if (listDbNames.contains(ShuttleDbHelper.TABLE_FROSTBITE_SHERIDAN))
				{
					listDbNames.remove(ShuttleDbHelper.TABLE_FROSTBITE_SHERIDAN);
				}
				listDbNames.add(ShuttleDbHelper.TABLE_FROSTBITE_SHERIDAN);
			}
			else
			{
				listDbNames.remove(ShuttleDbHelper.TABLE_FROSTBITE_SHERIDAN);
			}
			break;
		case R.id.cb_ryan_field:
			if(checked)
			{
				if (listDbNames.contains(ShuttleDbHelper.TABLE_RYAN_FIELD))
				{
					listDbNames.remove(ShuttleDbHelper.TABLE_RYAN_FIELD);
				}
				listDbNames.add(ShuttleDbHelper.TABLE_RYAN_FIELD);
			}
			else
			{
				listDbNames.remove(ShuttleDbHelper.TABLE_RYAN_FIELD);
			}
			break;
		case R.id.cb_shop_n_ride:
			if(checked)
			{
				if (listDbNames.contains(ShuttleDbHelper.TABLE_SHOP_N_RIDE))
				{
					listDbNames.remove(ShuttleDbHelper.TABLE_SHOP_N_RIDE);
				}
				listDbNames.add(ShuttleDbHelper.TABLE_SHOP_N_RIDE);
			}
			else
			{
				listDbNames.remove(ShuttleDbHelper.TABLE_SHOP_N_RIDE);
			}
			break;
			
		case R.id.button_select_shuttles:
			activityListener.onContinueButtonSelected();
			break;
		}	
	}
	

}
