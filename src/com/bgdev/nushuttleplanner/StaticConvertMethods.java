package com.bgdev.nushuttleplanner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.google.android.gms.maps.model.LatLng;

public class StaticConvertMethods
{
	public static String convertTimeToAmPm(String time)
	{
		String replaceTime;
		if (time.contains("0:"))
		{
			if(time.contains("10:"))
			{
				replaceTime = time + " AM";
			}
			else if(time.contains("20:"))
			{
				replaceTime = time.replace("20:", "8:") + " PM";
			}
			else
			{
				replaceTime = time.replace("0:", "12:") + " AM";
			}
		}
		else if(time.contains("1:"))
		{
			if(time.contains("21:"))
			{
				replaceTime = time.replace("21:", "9:") + " PM";
			}
			else
			{
				replaceTime = time + " AM";
			}
		}
		else if (time.contains("2:"))
		{
			if(time.contains("12:"))
			{
				replaceTime = time + " PM";
			}
			else if (time.contains("22:"))
			{
				replaceTime = time.replace("22:", "10:") + " PM";
			}
			else
			{
				replaceTime = time + " AM";
			}
		}
		else if (time.contains("3:"))
		{
			if (time.contains("23:"))
			{
				replaceTime = time.replace("23:", "11:") + " PM";
			}
			else if (time.contains("13:"))
			{
				replaceTime = time.replace("13:", "1:") + " PM";
			}
			else 
			{
				replaceTime = time + " AM";
			}
		}
		else if (time.contains("4:"))
		{
			if (time.contains("14:"))
			{
				replaceTime = time.replace("14:", "2:") + " PM";
			}
			else 
			{
				replaceTime = time + " AM";
			}
		}
		else if (time.contains("5:"))
		{
			if (time.contains("15:"))
			{
				replaceTime = time.replace("15:", "3:") + " PM";
			}
			else 
			{
				replaceTime = time + " AM";
			}
		}
		else if (time.contains("6:"))
		{
			if (time.contains("16:"))
			{
				replaceTime = time.replace("16:", "4:") + " PM";
			}
			else
			{
				replaceTime = time + " AM";
			}
		}
		else if (time.contains("7:"))
		{
			if (time.contains("17:"))
			{
				replaceTime = time.replace("17:", "5:") + " PM";
			}
			else
			{
				replaceTime = time + " AM";
			}
		}
		else if (time.contains("8:"))
		{
			if (time.contains("18:"))
			{
				replaceTime = time.replace("18:", "6:") + " PM";
			}
			else
			{
				replaceTime = time + " AM";
			}
		}
		else if (time.contains("9:"))
		{
			if (time.contains("19:"))
			{
				replaceTime = time.replace("19:", "7:") + " PM";
			}
			else
			{
				replaceTime = time + " AM";
			}
		}
		else
		{
			replaceTime = "Time Error";
		}
		
		return replaceTime;
	}
	
	public static ArrayList<String> convertArrayListTime(ArrayList<String> list)
	{
		ArrayList<String> sorted = new ArrayList<String>();
		for(String listEntries: list)
		{
			String converted = StaticConvertMethods.convertTimeToAmPm(listEntries);
			sorted.add(converted);
		}
		return sorted;
	}
	
	public static String[] convertArrayTime(String[] list)
	{
		String[] sortedList = new String[list.length];
		for(int i =0; i<list.length; i++)
		{
			String converted = StaticConvertMethods.convertTimeToAmPm(list[i]);
			sortedList[i] = converted;
		}
		return sortedList;
	}
	 
	public static String getCurrentTime()
	{
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("CST"));
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		return sdf.format(cal.getTime());
	}
	
	public static String convertShuttleToTable(String strShuttle)
	{
        Calendar currentdate = Calendar.getInstance();
        
		if (strShuttle.contains("Chicago Express"))
		{
			return ShuttleDbHelper.TABLE_CHICAGO_EXPRESS_SATURDAY_SHUTTLE;
		}
		else if (strShuttle.equals("Chicago Intercampus"))
		{
			return "chicago_to_evanston";
		}
		else if (strShuttle.contains("Evanston Loop"))
		{
			if (currentdate.get(Calendar.DAY_OF_WEEK)<=4)
			{
				return ShuttleDbHelper.TABLE_EVANSTON_LOOP_SUN_THROUGH_WED;
			}
			else
			{
				return ShuttleDbHelper.TABLE_EVANSON_LOOP_THURS_THROUGH_SAT;
			}
		}
		else if (strShuttle.equals("Evanston Intercampus"))
		{
			return "evanston_to_chicago";
		}
		else if (strShuttle.equals("Frostbite Express"))
		{
			return ShuttleDbHelper.TABLE_FROSTBITE_EXPRESS;
		}
		else if (strShuttle.equals("Frostbite Sheridan"))
		{
			return ShuttleDbHelper.TABLE_FROSTBITE_SHERIDAN;
		}
		else if (strShuttle.equals("Ryan Field"))
		{
			return ShuttleDbHelper.TABLE_RYAN_FIELD;
		}
		else if (strShuttle.contains("Shop"))
		{
			return ShuttleDbHelper.TABLE_SHOP_N_RIDE;
		}
		else if (strShuttle.equals("Campus Loop"))
		{
			if (TimeZone.getTimeZone("CST").inDaylightTime(new Date()))
			{
				return ShuttleDbHelper.TABLE_CAMPUS_LOOP_DAYLIGHTSAVINGS;
			}
			else
			{
				return ShuttleDbHelper.TABLE_CAMPUS_LOOP;
			}	
		}
		else
		{
			return "Table not found";
		}
	}
	
	public static String convertTableToShuttle(String strTable)
	{
		if (strTable.equals(ShuttleDbHelper.TABLE_CAMPUS_LOOP) || strTable.equals(ShuttleDbHelper.TABLE_CAMPUS_LOOP_DAYLIGHTSAVINGS))
		{
			return "Campus Loop";
		}
		else if (strTable.equals(ShuttleDbHelper.TABLE_CHICAGO_EXPRESS_SATURDAY_SHUTTLE))
		{
			return "Chicago Express";
		}
		else if (strTable.equals(ShuttleDbHelper.TABLE_CHICAGO_TO_EVANSTON))
		{
			return "Chicago Intercampus";
		}
		else if (strTable.equals(ShuttleDbHelper.TABLE_EVANSON_LOOP_THURS_THROUGH_SAT)|| strTable.equals(ShuttleDbHelper.TABLE_EVANSTON_LOOP_SUN_THROUGH_WED))
		{
			return "Evanston Loop";
		}
		else if (strTable.equals(ShuttleDbHelper.TABLE_EVANSON_TO_CHICAGO))
		{
			return "Evanston Intercampus";
		}
		else if (strTable.equals(ShuttleDbHelper.TABLE_FROSTBITE_EXPRESS))
		{
			return "Frostbite Express";
		}
		else if (strTable.equals(ShuttleDbHelper.TABLE_FROSTBITE_SHERIDAN))
		{
			return "Frostbite Sheridan";
		}
		else if (strTable.equals(ShuttleDbHelper.TABLE_RYAN_FIELD))
		{
			return "Ryan Field";
		}
		else if (strTable.equals(ShuttleDbHelper.TABLE_SHOP_N_RIDE))
		{
			return "Shop-N-Ride";
		}
		else
		{
			return null;
		}
	}
	
	public static ArrayList<String> convertEachTableToShuttle(ArrayList<String> list)
	{
		ArrayList<String> results = new ArrayList<String>();
		for(String name : list)
		{
			String converted = convertTableToShuttle(name);
			results.add(converted);	
		}
		return results;
	}
	
	public static LatLng convertLocationToLatLng(String location)
	{
		if (location.contains("Best Buy"))
		{
			return new LatLng(42.020115, -87.708134);
		}
		else if (location.contains("Central L"))
		{
			return new LatLng(42.064229, -87.685544);
		}
		else if (location.contains("Central/Jackson"))
		{
			return new LatLng(42.064052, -87.692088);
		}		
		else if (location.contains("Chicago/Davis"))
		{
			return new LatLng(42.046301, -87.679842);
		}
		else if (location.contains("Chicago/Greenleaf"))
		{
			return new LatLng(42.033815, -87.680405);
		}
		else if (location.contains("Chicago/Sheridan"))
		{
			return new LatLng(42.051037, -87.67735);
		}
		else if (location.contains("Clark/Burger King"))
		{
			return new LatLng(42.049876, -87.680236);
		}		
		else if (location.contains("Columbus/Illinois"))
		{
			return new LatLng(41.891065, -87.620307);
		}
		else if (location.contains("Columbus/Monroe"))
		{
			return new LatLng(41.880783, -87.620827);
		}
		else if (location.contains("Davis/Oak"))
		{
			return new LatLng(42.047134, -87.686542);
		}
		else if (location.contains("Davis/Sherman"))
		{
			return new LatLng(42.047118, -87.682063);
		}		
		else if (location.contains("Emerson/Maple"))
		{
			return new LatLng(42.052236, -87.684777);
		}
		else if (location.contains("Fisk Hall"))
		{
			return new LatLng(42.050504, -87.673863);
		}
		else if (location.contains("Green Bay/Emerson"))
		{
			return new LatLng(42.052487, -87.689562);
		}
		else if (location.contains("Green Bay/Noyes"))
		{
			return new LatLng(42.058263, -87.693746);
		}		
		else if (location.contains("Green Bay/Simpson"))
		{
			return new LatLng(42.055771, -87.691662);
		}
		else if (location.contains("Hinman/Sheridan"))
		{
			return new LatLng(42.050531, -87.675595);
		}
		else if (location.contains("Jacobs Center"))
		{
			return new LatLng(42.053865, -87.677173);
		}
		else if (location.contains("Lake/Maple"))
		{
			return new LatLng(42.044096, -87.684989);
		}		
		else if (location.contains("Library/Norris"))
		{
			return new LatLng(42.053015, -87.673935);
		}
		else if (location.contains("Lincolnwood Town Center"))
		{
			return new LatLng(42.009812, -87.712806);
		}
		else if (location.contains("Maple/Clark"))
		{
			return new LatLng(42.050304, -87.684691);
		}
		else if (location.contains("Metra/Green Bay/Harrison"))
		{
			return new LatLng(42.063811, -87.698923);
		}		
		else if (location.contains("Noyes/Sherman"))
		{
			return new LatLng(42.058364, -87.681681);
		}
		else if (location.contains("Orrington/Clark"))
		{
			return new LatLng(42.049548, -87.679884);
		}
		else if (location.contains("Chicago/Main"))
		{
			return new LatLng(42.034178, -87.679874);
		}
		else if (location.contains("Columbus/Roosevelt"))
		{
			return new LatLng(41.867591, -87.620503);
		}		
		else if (location.contains("Patten Gym"))
		{
			return new LatLng(42.06119, -87.677063);
		}
		else if (location.contains("Pearson"))
		{
			return new LatLng(41.897657, -87.620058);
		}
		else if (location.contains("Ridge/Davis"))
		{
			return new LatLng(42.047118, -87.688964);
		}
		else if (location.contains("Ridge/Garnett"))
		{
			return new LatLng(42.053362, -87.687539);
		}
		else if (location.contains("Ridge/Noyes"))
		{
			return new LatLng(42.058424, -87.685833);
		}
		else if (location.contains("Ridge/Simpson"))
		{
			return new LatLng(42.055996, -87.686786);
		}
		else if (location.contains("Ryan Field"))
		{
			return new LatLng(42.065575, -87.694112);
		 }
		else if (location.contains("SPAC"))
		{
			return new LatLng(42.059458, -87.67399);
		}
		else if (location.contains("Sheridan/Foster"))
		{
			return new LatLng(42.053734, -87.677266);
		}
		else if (location.contains("Sheridan/Lincoln"))
		{
			return new LatLng(42.061218, -87.677154);
		}
		else if (location.contains("Sheridan/Loyola"))
		{
			return new LatLng(42.000261, -87.660803);
		}
		else if (location.contains("Sheridan/Noyes"))
		{
			return new LatLng(42.058173, -87.677197);
		}
		else if (location.contains("Sherman/Simpson"))
		{
			return new LatLng(42.055863, -87.681735);
		}
		else if (location.contains("Target"))
		{
			return new LatLng(42.01926, -87.704762);
		}
		else if (location.contains("Tech Institute"))
		{
			return new LatLng(42.05788, -87.677089);
		}
		else if (location.contains("Ward"))
		{
			return new LatLng(41.896751, -87.61924);
		}
		else if (location.contains("Weber Arch"))
		{
			return new LatLng(42.051282, -87.677226);
		}
		else if (location.contains("Sherman/Clark"))
		{
			return new LatLng(42.050026, -87.681966);
		}
		else if (location.contains("Sherman/Emerson"))
		{
			return new LatLng(42.052246, -87.681813);
		}
		else if (location.contains("Sherman/Noyes"))
		{
			return new LatLng(42.058528, -87.681665);
		}
		else
		{
			return new LatLng(0,0);
		}
	}
	
	public static int setImageBasedOnShuttle(String shuttle)
	{
		if(shuttle.contains("Chicago Express"))
		{
			return R.drawable.chicago_express_image;
		}
		else if (shuttle.contains("Chicago Intercampus") || shuttle.contains("Evanston Intercampus"))
		{
			return R.drawable.intercampus_image;
		}
		else if (shuttle.contains("Evanston Loop"))
		{
			return R.drawable.evanston_loop_image;
		}
		else if (shuttle.contains("Frostbite Express"))
		{
			return R.drawable.evanston_loop_image;
		}
		else if (shuttle.contains("Frostbite Sheridan")|| shuttle.contains("Campus"))
		{
			return R.drawable.frostbite_sheridan_image;
		}
		else if (shuttle.contains("Ryan Field"))
		{
			return R.drawable.ryan_field_image;
		}
		else if (shuttle.contains("Shop"))
		{
			return R.drawable.shop_n_ride_image;
		}
		else
		{
			return 0;
		}
	}
	
	public static int setSquareImageBasedOnShuttle(String shuttle)
	{
		if(shuttle.contains("Chicago Express"))
		{
			return R.drawable.ic_drawer_gray;
		}
		else if (shuttle.contains("Chicago Intercampus") || shuttle.contains("Evanston Intercampus"))
		{
			return R.drawable.ic_drawer_red;
		}
		else if (shuttle.contains("Evanston Loop"))
		{
			return R.drawable.ic_drawer_purple;
		}
		else if (shuttle.contains("Frostbite Express"))
		{
			return R.drawable.ic_drawer_purple;
		}
		else if (shuttle.contains("Frostbite Sheridan")|| shuttle.contains("Campus"))
		{
			return R.drawable.ic_drawer_green;
		}
		else if (shuttle.contains("Ryan Field"))
		{
			return R.drawable.ic_drawer_blue;
		}
		else if (shuttle.contains("Shop"))
		{
			return R.drawable.ic_drawer_orange;
		}
		else
		{
			return 0;
		}
	}
	
	public static boolean isFirstTimeBeforeSecondTime(String firstTime, String secondTime)
	{
		long difference;
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.US);
		
		Date dtFirstTime = null;
		Date dtSecondTime = null;
		try 
		{
			dtFirstTime = dateFormat.parse(firstTime);
			dtSecondTime = dateFormat.parse(secondTime);
		} 
		catch (ParseException e) 
		{
	
		}
		
		difference = dtFirstTime.getTime() - dtSecondTime.getTime();
		
		return difference<0;
	}

	public static long differenceBetweenTimes(String firstTime, String secondTime)
	{
		long difference;
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.US);
		
		Date dtFirstTime = null;
		Date dtSecondTime = null;
		try 
		{
			dtFirstTime = dateFormat.parse(firstTime);
			dtSecondTime = dateFormat.parse(secondTime);
		} 
		catch (ParseException e) 
		{
			
		}
		 
		difference = dtFirstTime.getTime() - dtSecondTime.getTime();
		
		long absdiff = Math.abs(difference);
		return absdiff / (60 * 60 * 1000) % 24;
	}
	
	public static long differenceBetweenTimesNoBefore(String firstTime, String secondTime)
	{
		long difference;
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.US);
		
		Date dtFirstTime = null;
		Date dtSecondTime = null;
		try 
		{
			dtFirstTime = dateFormat.parse(firstTime);
			dtSecondTime = dateFormat.parse(secondTime);
		} 
		catch (ParseException e) 
		{
			
		}
		return difference = dtSecondTime.getTime() - dtFirstTime.getTime();
		
	}
	
	public static String convertShuttleToDescription(String strShuttle)
	{
		if (strShuttle.contains("Intercampus"))
		{
			return "Operates year round, M-F";
		}
		else if (strShuttle.contains("Loop") || strShuttle.contains("Ryan Field"))
		{
			return "Operates during the academic year, M-F";
		}
		else if (strShuttle.contains("Shop"))
		{
			return "Operates on Sundays during the academic year";
		}
		else if (strShuttle.contains("Frostbite"))
		{
			return "Operates when temperature reads single digits or below";
		}
		else
		{
			return "Operates on Saturdays during the academic year";
		}
	}
}
