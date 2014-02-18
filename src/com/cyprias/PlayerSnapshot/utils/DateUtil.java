package com.cyprias.PlayerSnapshot.utils;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateUtil {
	
	public static int translateTimeStringToSeconds( String arg_value ){
		int seconds = 0;
		
		Pattern p = Pattern.compile("([0-9]+)(s|h|m|d|w)");

		String[] matches = preg_match_all( p, arg_value );
		if(matches.length > 0){
			for(String match : matches){
	
				Matcher m = p.matcher( match );
				if(m.matches()){
					
					if( m.groupCount() == 2 ){
						
						int tfValue = Integer.parseInt( m.group(1) );
						String tfFormat = m.group(2);

						if(tfFormat.equals("w")){
							seconds += 60*60*24*7 * tfValue;
						}
						else if(tfFormat.equals("d")){
							seconds += 60*60*24 * tfValue;
						}
						else if(tfFormat.equals("h")){
							seconds += 60*60 * tfValue;
						}
						else if(tfFormat.equals("m")){
							seconds += 60 * tfValue;
						}
						else if(tfFormat.equals("s")){
							seconds += tfValue;
						} else {
							return 0;
						}
					}
				}
			}
		}
		
		return seconds;
	}
	
	public static Long translateTimeStringToDate( String arg_value ){
		
		Long dateFrom = 0L;

		Pattern p = Pattern.compile("([0-9]+)(s|h|m|d|w)");
		Calendar cal = Calendar.getInstance();

		String[] matches = preg_match_all( p, arg_value );
		if(matches.length > 0){
			for(String match : matches){
	
				Matcher m = p.matcher( match );
				if(m.matches()){
					
					if( m.groupCount() == 2 ){
						
						int tfValue = Integer.parseInt( m.group(1) );
						String tfFormat = m.group(2);

						if(tfFormat.equals("w")){
							cal.add(Calendar.WEEK_OF_YEAR, -1 * tfValue);
						}
						else if(tfFormat.equals("d")){
							cal.add(Calendar.DAY_OF_MONTH, -1 * tfValue);
						}
						else if(tfFormat.equals("h")){
							cal.add(Calendar.HOUR, -1 * tfValue);
						}
						else if(tfFormat.equals("m")){
							cal.add(Calendar.MINUTE, -1 * tfValue);
						}
						else if(tfFormat.equals("s")){
							cal.add(Calendar.SECOND, -1 * tfValue);
						} else {
							return null;
						}
					}
				}
			}
			dateFrom = cal.getTime().getTime();
		}

		return dateFrom;
		
	}
	
	  public static String[] preg_match_all(Pattern p, String subject)
	  {
	    Matcher m = p.matcher(subject);
	    StringBuilder out = new StringBuilder();
	    boolean split = false;
	    while (m.find()) {
	      out.append(m.group());
	      out.append("~");
	      split = true;
	    }
	    return split ? out.toString().split("~") : new String[0];
	  }
}
