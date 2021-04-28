package com.pulmuone.mrtina.utils;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtil {

	public final static String DEFUALT_DATE_FORMAT1		= "yyyyMMdd";
	public final static String DEFUALT_DATE_FORMAT2		= "yyyy.MM.dd";
	public final static String DEFUALT_DATE_FORMAT3		= "yyyy-MM-dd";
	public final static String DEFUALT_DATE_FORMAT4		= "yyyy-MM-dd HH:mm:ss";
	public final static String DEFUALT_DATE_FORMAT5		= "HHmmss";
	public final static String DEFUALT_DATE_FORMAT7		= "yyyyMMddHHmmss";
	
	public final static String DATE_FORMAT_PICTURE		= "MM월dd일";
	public final static String DATE_FORMAT_PICTURE_ITEM	= "MM/dd HH:mm";
	public final static String DATE_FORMAT_GAME_DATE	= "HH:mm";

	public static final int TYPE_DAYS		= 24;
	public static final int TYPE_HOURS		= 60;
	public static final int TYPE_MINUTES	= 60;
	public static final int TYPE_SECONDS	= 1000;

	public static String date(String format) {

		try {

			java.util.Date date = new java.util.Date();
			SimpleDateFormat simple_date_format = new SimpleDateFormat(format);

			return simple_date_format.format(date);

		} catch (Exception e) {
			return "";
		}
	}

	public static String date(Date date, String format) {

		if (date == null)
			return "";

		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(format);
		String dateString = formatter.format(date);

		return dateString;
	}

	public static String date(String date, String format) {

		if (date == null || date.length() != 8)
			return "";

		return date.substring(0, 4) + format + date.substring(4, 6) + format
				+ date.substring(6, 8);
	}

	public static String dateParsing(String yyyyMMdd) {

		if (yyyyMMdd == null || yyyyMMdd.length() != 8)
			return "";

		return yyyyMMdd.substring(4, 6) + "월" + yyyyMMdd.substring(6, 8) + "일";
	}

    public static String addDotDate(String date)
    {
    	if(date == null)
    	{
    		return "";
    	}
    	if(date.length() != 8)
    	{
    		return date;
    	}
    	
    	return date.substring(0, 4) + "." + date.substring(4, 6) + "." + date.substring(6, 8);
    }

    public static String getTargetDay(int day) {
    	
    	return getTargetDay(day, DEFUALT_DATE_FORMAT2);
    }
    
    public static String getTargetDay(int day, String dateFormat) {
    	
    	Calendar cal = Calendar.getInstance();

    	cal.add(Calendar.DATE, day);	// 현재 날짜에서 해당일(day) 전 or 후의 날짜 가져오기

    	Date currentTime=cal.getTime();

    	SimpleDateFormat formatter=new SimpleDateFormat(dateFormat);

    	String timeResult	= formatter.format(currentTime);

    	return timeResult;
    	
    }
    
    public static Date getTargetDate(int day) {
    	
    	Calendar cal = Calendar.getInstance();

    	cal.add(Calendar.DATE, day);	// 현재 날짜에서 해당일(day) 전 or 후의 날짜 가져오기

    	Date currentTime=cal.getTime();

    	return currentTime;
    	
    }

    
    public static String getTargetDate(String yyyymmdd, int elapsedDay, String format) {

		int yyyy	= 0;
		int mm		= 0;
		int dd		= 0;

		if (yyyymmdd.length() > 8) 
		{
			yyyy	= Integer.parseInt(yyyymmdd.substring(0, 4));
			mm		= Integer.parseInt(yyyymmdd.substring(5, 7));
			dd		= Integer.parseInt(yyyymmdd.substring(8, 10));

		} else if (yyyymmdd.length() == 8) 
		{
			yyyy	= Integer.parseInt(yyyymmdd.substring(0, 4));
			mm		= Integer.parseInt(yyyymmdd.substring(4, 6));
			dd		= Integer.parseInt(yyyymmdd.substring(6, 8));

		} else 
		{
			return "";
		}

    	return getTargetDate(yyyy, mm, dd, elapsedDay, format);
	}
    

    public static String getTargetDate(int year, int month, int day, int elapsedDay, String format) {

		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month - 1, day);

		calendar.add(Calendar.DATE, elapsedDay);			// 파라미터 날짜에서 해당일(day) 전 or 후의 날짜 가져오기

    	Date currentTime	= calendar.getTime();

    	SimpleDateFormat formatter=new SimpleDateFormat(format);

    	String timeResult	= formatter.format(currentTime);

    	return timeResult;
	}

    
    public static Date getTargetDate(int year, int month, int day, int elapsedDay) {

		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month - 1, day);

		calendar.add(Calendar.DATE, elapsedDay);			// 파라미터 날짜에서 해당일(day) 전 or 후의 날짜 가져오기

    	Date currentTime	= calendar.getTime();

    	return currentTime;
	}

    
    public static String addDateToFormatedString(String pattern, int day) {

		Calendar calendar = Calendar.getInstance(); // 현재 날짜/시간 등의 각종 정보 얻기

		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + day);

		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(pattern);
		String dateString = formatter.format(calendar.getTime());
		return dateString;
	}


	public static String addDateToFormatedString(String pattern, String date, int day) {

		Calendar calendar = Calendar.getInstance(); // 현재 날짜/시간 등의 각종 정보 얻기
		calendar.set(Calendar.YEAR, Integer.parseInt(date.substring(0, 4)));
		calendar.set(Calendar.MONTH, Integer.parseInt(date.substring(4, 6)) - 2);
		calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date.substring(6, 8)));
		calendar.add(Calendar.DAY_OF_MONTH, day);

		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(pattern);
		String dateString = formatter.format(calendar.getTime());

		return dateString;
	}

	public static String getLastDayString(String yyyy, String mm) {

		if (yyyy == null || yyyy.length() < 4 || mm == null || "".equals(mm)) 
		{
			return "";

		}else{
			return String.valueOf(getLastDay(Integer.parseInt(yyyy), Integer.parseInt(mm)));
		}
	}

	public static int getLastDay(String yyyy, String mm) {

		if (yyyy == null || yyyy.length() < 4 || mm == null || "".equals(mm)) 
		{
			return 0;

		} else 
		{
			return getLastDay(Integer.parseInt(yyyy), Integer.parseInt(mm));
		}
	}

	public static int getLastDay(int yyyy, int mm) {

		Calendar Cal = Calendar.getInstance();
		Cal.set(yyyy, mm - 1, 1);
		int Max = Cal.getActualMaximum(Calendar.DAY_OF_MONTH);

		return Max;
	}

	public static String getLastDayString(int yyyy, int mm) {

		return String.valueOf(getLastDay(yyyy, mm));
	}


	public static String toDateTime(String strDate) {

		SimpleDateFormat formatter_one = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss", Locale.ENGLISH);
		SimpleDateFormat formatter_two = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

		ParsePosition pos	= new ParsePosition(0);
		Date frmTime		= formatter_one.parse(strDate, pos);

		return formatter_two.format(frmTime);
	}

	public static String toDateNa(String strDate,String fm) { // fm : D,T
		String rtn = "";
		SimpleDateFormat formatter_one = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss", Locale.ENGLISH);
		SimpleDateFormat formatter_two = new SimpleDateFormat("MM-dd-yyyy");

		if(fm.equals("T")) {
			rtn =  formatter_one.format(strDate);
		}else{
			rtn =  formatter_two.format(strDate);
		}
		return rtn;
	}


	public static String getWeekString(String yyyymmdd) {

		int yyyy	= 0;
		int mm		= 0;
		int dd		= 0;

		if (yyyymmdd.length() > 8) 
		{
			yyyy	= Integer.parseInt(yyyymmdd.substring(0, 4));
			mm		= Integer.parseInt(yyyymmdd.substring(5, 7));
			dd		= Integer.parseInt(yyyymmdd.substring(8, 10));

		} else if (yyyymmdd.length() == 8) 
		{
			yyyy	= Integer.parseInt(yyyymmdd.substring(0, 4));
			mm		= Integer.parseInt(yyyymmdd.substring(4, 6));
			dd		= Integer.parseInt(yyyymmdd.substring(6, 8));

		} else 
		{
			return "";
		}

		return getWeekString(yyyy, mm, dd);
	}


	public static String getWeekString(int year, int month, int day) {

		String week = "";

		int i = getWeek(year, month, day);

		if (i == 1) 
		{
			week = "(일)";
		
		} else if (i == 2) 
		{
			week = "(월)";
		
		} else if (i == 3) 
		{
			week = "(화)";
		
		} else if (i == 4) 
		{
			week = "(수)";
		
		} else if (i == 5) 
		{
			week = "(목)";
		
		} else if (i == 6) 
		{
			week = "(금)";
		
		} else if (i == 7) 
		{
			week = "(토)";
		}

		return week;
	}


	public static String getWeekString(String year, String month, String day) {

		return getWeekString(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
	}


	public static String getFirstWeekString(int year, int month) {

		return getWeekString(year, month, 1);
	}


	public static String getFirstWeekString(String year, String month) {

		return getWeekString(Integer.parseInt(year), Integer.parseInt(month), 1);
	}


	public static int getWeek(String yyyymmdd) {

		int yyyy	= 0;
		int mm		= 0;
		int dd		= 0;

		if (yyyymmdd.length() > 8) 
		{
			yyyy	= Integer.parseInt(yyyymmdd.substring(0, 4));
			mm		= Integer.parseInt(yyyymmdd.substring(5, 7));
			dd		= Integer.parseInt(yyyymmdd.substring(8, 10));

		} else if (yyyymmdd.length() == 8) 
		{
			yyyy	= Integer.parseInt(yyyymmdd.substring(0, 4));
			mm		= Integer.parseInt(yyyymmdd.substring(4, 6));
			dd		= Integer.parseInt(yyyymmdd.substring(6, 8));
		
		} else 
		{
			return 0;
		}

		return getWeek(yyyy, mm, dd);
	}


	public static int getWeek(int year, int month, int day) {

		Calendar calendar = Calendar.getInstance(); // 현재 날킈/시간 등의 각종 정보 얻기
		calendar.set(year, month - 1, day);

		return calendar.get(Calendar.DAY_OF_WEEK);
	}


	public static int getWeek(String year, String month, String day) {

		return getWeek(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
	}


	public static int getFirstWeek(int year, int month) {

		return getWeek(year, month, 1);
	}


	public static int getFirstWeek(String year, String month) {

		return getWeek(Integer.parseInt(year), Integer.parseInt(month), 1);
	}


	public static int getMonthWeek(int year, int month, int day) {

		Calendar calendar = Calendar.getInstance(); // 현재 날짜/시간 등의 각종 정보 얻기
		calendar.set(year, month - 1, day);

		return calendar.get(Calendar.WEEK_OF_MONTH);
	}


	public static int getMonthLastWeek(int year, int month) {

		return getMonthWeek(year, month, getLastDay(year, month));
	}


	public static int getMonthLastWeek(String yyyy, String mm) {

		if (yyyy == null || yyyy.length() < 4 || mm == null || "".equals(mm)) 
		{
			return 0;
		
		} else 
		{
			return getMonthLastWeek(Integer.parseInt(yyyy), Integer.parseInt(mm));
		}
	}


	public static Date toDate() {

		return toDate(date(DEFUALT_DATE_FORMAT1), DEFUALT_DATE_FORMAT1);
	}


	public static Date toDate(String date) {

		return toDate(date, DEFUALT_DATE_FORMAT1);
	}


	public static Date toDate(String dateTime, String format) {

		java.util.Date date = null;

		try {

			java.text.SimpleDateFormat simpleDateFormat = new java.text.SimpleDateFormat(format);
			date = simpleDateFormat.parse(dateTime);

		} catch (Exception e) 
		{

		}

		return date;
	}



	public static String[] split(String date, String delim) {

		if(null == date || null == delim || date.length()<10)
			return null;

		String[] result = new String[3];

		String sYear	= date.substring(0, 4);
		String sMonth	= date.substring(5, 7);
		String sDay		= date.substring(8, 10);

		result[0]	= sYear;
		result[1]	= sMonth;
		result[2]	= sDay;
		
		return result;

	}


	public static long diffOfDate(Date begin, Date end) {

		return diffOfDate(begin, end, DEFUALT_DATE_FORMAT1);
	}


	public static long diffOfDate(Date begin, Date end, String format) {

		return diffOfDate(date(begin, format), date(end, format));
	}


	public static long diffOfDate(String begin, String end) {

		return diffOfDate(begin, end, DEFUALT_DATE_FORMAT1);
	}


	public static long diffOfDate(String begin, String end, String format) {

		SimpleDateFormat formatter = new SimpleDateFormat(format);

		long diffDays = 0;

		try {

			Date beginDate	= formatter.parse(begin);
			Date endDate	= formatter.parse(end);

			long diff	= endDate.getTime() - beginDate.getTime();
			diffDays	= diff / (24 * 60 * 60 * 1000);

		} catch (Exception e) 
		{
			e.printStackTrace();
		}

		return diffDays;
	}


	public static boolean isDateValidate(String date) {

		boolean isCheck = false;

		try {

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DEFUALT_DATE_FORMAT1);
			simpleDateFormat.setLenient(false);
			simpleDateFormat.parse(date);
			isCheck = true;

		} catch (ParseException e) 
		{
			isCheck = false;
		
		} catch (IllegalArgumentException e) 
		{
			isCheck = false;
		
		} catch (Exception e) 
		{
			isCheck = false;
		}

		return isCheck;
	}

	public static Calendar toCalendar(String dateStr, String format) throws ParseException {
		if (dateStr == null || dateStr.equals("")) {
			return null;
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		dateFormat.parse(dateStr);
		return dateFormat.getCalendar();
	}

	public static String toString(Date date, String format) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		String dateTime = dateFormat.format(date);
		return dateTime;
	}

}

