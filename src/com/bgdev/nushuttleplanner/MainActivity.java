package com.bgdev.nushuttleplanner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.SQLException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bgdev.nushuttleplanner.HomeFragment.OnHomePageSelectedListener;
import com.bgdev.nushuttleplanner.UpcomingFragment.OnUpcomingItemClickListener;

public class MainActivity extends ActionBarActivity implements OnHomePageSelectedListener, OnUpcomingItemClickListener
{
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerLeftList, mDrawerRightList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mDrawerTitles, mDrawerRightTitles;
    private int[] mDrawerImagesResource = {R.drawable.drawer_icon_planner, R.drawable.drawer_icon_upcoming,R.drawable.drawer_icon_favorites, R.drawable.drawer_icon_map, R.drawable.drawer_icon_google_map};
    private int[] mDrawerImagesRightResource = {R.drawable.ic_drawer_green, R.drawable.ic_drawer_red, R.drawable.ic_drawer_purple,R.drawable.ic_drawer_blue,R.drawable.ic_drawer_red,
    		R.drawable.ic_drawer_green, R.drawable.ic_drawer_purple,  R.drawable.ic_drawer_gray, R.drawable.ic_drawer_orange};
    
    private static String fragId = "";
    FragmentManager fragMan; 
    
    public static boolean bTimePreference, bNotAllDay, bDefaultHome;
    Context context; 
    LayoutInflater inflater;
    ShuttleDbHelper shuttleDb;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_activity_drawer_layout);
		context = this;
		shuttleDb = new ShuttleDbHelper(context);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);		
		
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        bTimePreference = sharedPreferences.getBoolean("AMPMChoice", true);
        bNotAllDay = sharedPreferences.getBoolean("all_day", true);
        bDefaultHome = sharedPreferences.getBoolean("home_screen",true);
        		
        mTitle = mDrawerTitle = getTitle();
        mDrawerTitles = getResources().getStringArray(R.array.drawer_array);
        mDrawerRightTitles = getResources().getStringArray(R.array.drawer_array_right);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLeftList = (ListView) findViewById(R.id.left_drawer);
        mDrawerRightList = (ListView) findViewById(R.id.right_drawer);
        
        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerLeftList.setAdapter(new NavigationLeftBarAdapter(this, R.layout.drawer_list_item, mDrawerTitles));
        mDrawerLeftList.setOnItemClickListener(new DrawerItemClickListener());
        
        mDrawerRightList.setAdapter(new NavigationRightBarAdapter(this, R.layout.drawer_right_list_item, mDrawerRightTitles));
        mDrawerRightList.setOnItemClickListener(new RightDrawerItemClickListener());
        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        
        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

			public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()             
           }
        };
        
        mDrawerLayout.setDrawerListener(mDrawerToggle);
		
		//Controls all fragments on screen
        if (savedInstanceState!=null)
        {
        	fragMan = this.getSupportFragmentManager();
        }
        else
        {
	        fragMan = getSupportFragmentManager();
			FragmentTransaction fragTrans = fragMan.beginTransaction();
			Fragment frag = null;
			if (bDefaultHome)
			{
				frag = HomeFragment.newInstance();
			}
			else
			{
				frag = new UpcomingFragment();
			}
			fragTrans.replace(R.id.content_frame, frag, "HOME");
			fragTrans.commit();
        }
        
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        if (item.getItemId()==R.id.action_settings)
        {
        	startActivity(new Intent(this, EditSettings.class));
        	return true;
        }
        else return false;
    }
    	
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		//User MenuItemCopact static class if adding an actionbar option
		return true;
	}
	
    //Selecting the item for the left navigation bar
    private void selectItem(int position) 
    {
    	Fragment fragment = null;
    	if (position==0)
    	{
    		fragment = HomeFragment.newInstance();
    	}
    	else if(position==1)
    	{
    		fragment = new UpcomingFragment();
    		fragId = "UPCOMING";
    	}
    	else if(position==2)
    	{
    		fragment = new FavoritesFragment();
    		fragId = "FAVORITES";
    	}
    	else if (position==3)
    	{
    		fragment = new NuMapFragment();
    		fragId = "MAP";
    	}
    	else
    	{
    		fragment = new NuGoogleMapFragment();
    		fragId = "GOOGLE MAP";
    	}
        
    	mDrawerLeftList.setItemChecked(position, true);
        setTitle(mDrawerTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerLeftList);
        
		FragmentTransaction fragmentTransaction = fragMan.beginTransaction();
		fragmentTransaction.setCustomAnimations(R.anim.abc_fade_in,R.anim.abc_fade_out);
		fragmentTransaction.replace(R.id.content_frame, fragment,fragId);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit(); 
    }
    
    //Sets the title to the title selected in the navigation bar
	@Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }
    
    
    /* The click listener for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }
    
    //OnClickListener for the right drawer list view
    private class RightDrawerItemClickListener implements ListView.OnItemClickListener
    {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position ,long id) 
		{
	    	mDrawerRightList.setItemChecked(position, true);
	        setTitle(mDrawerRightTitles[position]);
	        mDrawerLayout.closeDrawer(mDrawerRightList);
			
			String shuttleName = mDrawerRightTitles[position];
			Fragment shuttleScheduleFrag = FullScheduleFragment.newInstance(shuttleName);
			
			FragmentTransaction fragmentTransaction = fragMan.beginTransaction();
			fragmentTransaction.setCustomAnimations(R.anim.abc_fade_in,R.anim.abc_fade_out);
			fragmentTransaction.replace(R.id.content_frame, shuttleScheduleFrag, "shuttle schedule");
			fragmentTransaction.addToBackStack(null);
			fragmentTransaction.commit(); 
		}
    	
    }
    
    //Adapter for the left navigation bar
    private class NavigationLeftBarAdapter extends ArrayAdapter<String>
    {
    	String[] drawerTextArray;
		public NavigationLeftBarAdapter(Context context, int resource, String[] drawerText) 
		{
			super(context, resource, drawerText);
			drawerTextArray = drawerText;

		}
		public View getView(int position, View view, ViewGroup parent) 
		{
			View drawerView = inflater.inflate(R.layout.drawer_list_item, null);
			
 			TextView drawerText = (TextView) drawerView.findViewById(R.id.drawer_text);
 			drawerText.setText(drawerTextArray[position]);
 			
			ImageView drawerImage = (ImageView) drawerView.findViewById(R.id.drawer_image);
			drawerImage.setImageResource(mDrawerImagesResource[position]);
			
			return drawerView;
		}
    	
    }
    //Adapter for the right navigation bar
    private class NavigationRightBarAdapter extends ArrayAdapter<String>
    {
    	String[] drawerTextArray;
		public NavigationRightBarAdapter(Context context, int resource, String[] drawerText) 
		{
			super(context, resource, drawerText);
			drawerTextArray = drawerText;

		}
		public View getView(int position, View view, ViewGroup parent) 
		{
			View drawerView = inflater.inflate(R.layout.drawer_right_list_item, null);
			
 			TextView drawerText = (TextView) drawerView.findViewById(R.id.drawer_text_right);
 			drawerText.setText(drawerTextArray[position]);
 			
			ImageView drawerImage = (ImageView) drawerView.findViewById(R.id.drawer_image_right);
			drawerImage.setImageResource(mDrawerImagesRightResource[position]);
 			
			return drawerView;
		}
    	
    }
    
    @Override
    public void onResume()
    {
    	super.onResume();
		try
		{
			shuttleDb.openDataBase();
		}
		catch (SQLException e)
		{
			
		}
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        bTimePreference = sharedPreferences.getBoolean("AMPMChoice", true);
        bNotAllDay = sharedPreferences.getBoolean("all_day", true);
        bDefaultHome = sharedPreferences.getBoolean("default_home",true);
    }
    @Override
    public void onStop()
    {
    	super.onStop();
    	mDrawerLayout.closeDrawers();	
    }

	@Override
	protected void onDestroy()
	{
		ShuttleDbHelper.NuShuttleDataBase.close();
		NuShuttleApplication.bmp.recycle();
		super.onDestroy();
	}
	
	//Call for the continue button to the results fragment
	@Override
	public void onContinueButtonSelected() 
	{
		if(HomeFragment.listDbNames.isEmpty()==false)
		{ 
			Fragment fragment = ResultsFragment.newInstance(HomeFragment.listDbNames.toArray(new String[HomeFragment.listDbNames.size()]));
			FragmentTransaction fragmentTransaction = fragMan.beginTransaction();
			fragmentTransaction.setCustomAnimations(R.anim.abc_fade_in,R.anim.abc_fade_out);
			fragmentTransaction.replace(R.id.content_frame, fragment,"RESULTS");
			fragmentTransaction.addToBackStack(null);
			fragmentTransaction.commit(); 
		}
		else
		{
			Toast.makeText(this, "Please select a shuttle", Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	public void onUpcomingItemClicked(String strShuttle, String strTime) 
	{	
		if (strShuttle.contains("Thurs")|| strShuttle.contains("Wed"))
		{
			strShuttle = "Evanston Loop";
		}
		setTitle("Search");
		Fragment fragment = FullScheduleFragment.newInstance(strShuttle, strTime);
		FragmentTransaction fragmentTransaction = fragMan.beginTransaction();
		fragmentTransaction.setCustomAnimations(R.anim.abc_fade_in,R.anim.abc_fade_out);
		fragmentTransaction.replace(R.id.content_frame, fragment,"SHUTTLE");
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit(); 
	}
	
	
}
