package provision.util;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtil{
	
	public static Date getDateXDaysFromNow(int daysFromStartDate){
		return getDateXDaysFromY(System.currentTimeMillis(), daysFromStartDate);
	}
	
	public static Timestamp getTimestampXDaysFromNow(int daysFromStartDate){
		return getTimestampXDaysFromY(System.currentTimeMillis(), daysFromStartDate);
	}
	
	public static Date getDateXDaysFromY(long startTimeInMillis, int daysFromStartDate){
		Calendar cal = getCalendarXDaysFromY(startTimeInMillis, daysFromStartDate);
		return new Date(cal.getTimeInMillis());
	}
	
	public static Timestamp getTimestampXDaysFromY(long startTimeInMillis, int daysFromStartDate){
	    Calendar cal = getCalendarXDaysFromY(startTimeInMillis, daysFromStartDate);
		return new Timestamp(cal.getTimeInMillis());
	}
	
	private static Calendar getCalendarXDaysFromY(long startTimeInMillis, int daysFromStartDate){
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(startTimeInMillis);
		cal.add(Calendar.DAY_OF_YEAR, daysFromStartDate);
		
		return cal;
	}

}
