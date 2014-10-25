package com.mauriciogiordano.commit;

import java.util.Calendar;
import java.util.TimeZone;

public class CommitHelper
{

	public static Long getToday()
	{
		Calendar today = Calendar.getInstance();
		today.setTimeZone(TimeZone.getDefault());
		
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.HOUR_OF_DAY, 1);
		today.set(Calendar.MILLISECOND, 0);
		
		// FOR DEBUG
		//today.set(Calendar.DAY_OF_YEAR, today.get(Calendar.DAY_OF_YEAR) + 1);
		
		return today.getTimeInMillis();
	}
	
	public static Long getYesterday()
	{
		Calendar yesterday = Calendar.getInstance();
		yesterday.setTimeZone(TimeZone.getDefault());
		
		yesterday.set(Calendar.SECOND, 0);
		yesterday.set(Calendar.MINUTE, 0);
		yesterday.set(Calendar.HOUR_OF_DAY, 1);
		yesterday.set(Calendar.MILLISECOND, 0);
		
		int day = yesterday.get(Calendar.DAY_OF_YEAR);
		
		yesterday.set(Calendar.DAY_OF_YEAR, day - 1);
		
		return yesterday.getTimeInMillis();
	}
	
}
