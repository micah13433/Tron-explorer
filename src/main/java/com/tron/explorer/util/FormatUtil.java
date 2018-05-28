package com.tron.explorer.util;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FormatUtil {
		
	public static String formatTimeInMillis(long timeInMillis) {  
	    Calendar cal = Calendar.getInstance();  
	    cal.setTimeInMillis(timeInMillis);  
	    Date date = cal.getTime();  
	    SimpleDateFormat dateFormat = new SimpleDateFormat(  
	            "yyyy-MM-dd HH:mm:ss");  
	    String fmt = dateFormat.format(date);  
	  
	    return fmt;  
	}  	
	
	public static String getShowNumber(String number) throws ParseException{
		DecimalFormat df = new DecimalFormat("####.##"); 
		return df.format((Float.valueOf(number) / 100000000));
	}

}
