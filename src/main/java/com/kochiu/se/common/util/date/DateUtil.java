package com.kochiu.se.common.util.date;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 日期处理工具类
 */
public class DateUtil {

	public static final String SHORT_DATE_FORMAT_STR = "yyyy-MM-dd";
	public static final String LONG_DATE_FORMAT_STR = "yyyy-MM-dd HH:mm:ss";
	public static final String MAX_LONG_DATE_FORMAT_STR = "yyyy-MM-dd HH:mm:ss.SSS";
	public static final String EARLY_TIME = "00:00:00 000";
	public static final String LATE_TIME = "23:59:59";
	public static final String EARER_IN_THE_DAY = "yyyy-MM-dd 00:00:00.000";
	public static final String LATE_IN_THE_DAY = "yyyy-MM-dd 23:59:59.999";
	public static final long DAY_LONG = 24 * 60 * 60 * 1000;
	public static final String YYYY_MM_DD = "yyyy-MM-dd";

	/**
	 * 得到某个日期在这一天中时间最早的日期对象
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static Date getEarlyInTheDay(Date date) throws ParseException {
		String dateString = new SimpleDateFormat(SHORT_DATE_FORMAT_STR).format(date) + " " + EARLY_TIME;
		return new SimpleDateFormat(LONG_DATE_FORMAT_STR).parse(dateString);
	}
	
	/**
	 * 得到某天最早的时间
	 * @param date
	 * @return
	 */
	public static Date getFirstOfDay(Date date) {
		String dateString = new SimpleDateFormat(EARER_IN_THE_DAY).format(date);
		try {
			return new SimpleDateFormat(LONG_DATE_FORMAT_STR).parse(dateString);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 得到某个日期在这一天中时间最晚的日期对象
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static Date getLateInTheDay(Date date) throws ParseException {
		String dateString = new SimpleDateFormat(SHORT_DATE_FORMAT_STR).format(date) + " " + LATE_TIME;
		return new SimpleDateFormat(LONG_DATE_FORMAT_STR).parse(dateString);
	}

	/**
	 * 增加时间的秒数
	 * @param date 要增加的日期
	 * @param second 增加的时间（以秒为单位）
	 * @return 增加时间后的日期
	 */

	public static Date addSecond(Date date, int second) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.SECOND, second);
		return cal.getTime();
	}

	/**
	 * 增加时间的分钟数
	 * @param date 要增加的日期
	 * @param second 增加的时间（以分钟为单位）
	 * @return 增加时间后的日期  
	 */
	public static Date addMinute(Date date, int minute) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, minute);
		return cal.getTime();
	}
	
	
	/**
	 * 根据传入日期计算和当前日期的相差天数
	 * 
	 * @param date
	 * @return
	 */
	public static long subtractNowDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		long dateTimeInMillis = calendar.getTimeInMillis();
		Calendar nowCalendar = Calendar.getInstance();
		nowCalendar.setTime(new Date());
		long nowTimeInMillis = nowCalendar.getTimeInMillis();
		return (nowTimeInMillis - dateTimeInMillis) / (24 * 60 * 60 * 1000);
	}

	/**
	 * 获取结束日期与开始日期相差的秒数
	 * 
	 * @param startDate 开始日期
	 * @param endDate 结束日期
	 * @return
	 */
	public static long subtractSecond(Date startDate, Date endDate) {
		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTime(startDate);
		long startTimeInMillis = startCalendar.getTimeInMillis();
		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(endDate);
		long endTimeInMillis = endCalendar.getTimeInMillis();
		return (endTimeInMillis - startTimeInMillis) / 1000;

	}

	/**
	 * 字符串按自定格式更新
	 * 
	 * @param dateString
	 * @param format
	 * @return
	 * @throws ParseException
	 */
	public static Date parserStringToDate(String dateString, String format) throws ParseException {
		DateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.parse(dateString);
	}
	
	public static Date parse(String dateString, String format){
		Date date = null;
		try{
			date = parserStringToDate(dateString, format);
		}catch(Exception e){}
		return date;
	}

	/**
	 * 日期加减
	 * 
	 * @param date
	 * @param dateInterval
	 * @return
	 */
	public static Date dateInterval(Date date, int dateInterval) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, dateInterval);
		return cal.getTime();
	}
	

	public static Date getBeginOfDay(Date date) {
		String beginDay = new SimpleDateFormat(EARER_IN_THE_DAY).format(date);
		try {
			return parserStringToDate(beginDay, LONG_DATE_FORMAT_STR);
		} catch (ParseException e) {
		}
		return null;
	}

	public static Date getEndOfDay(Date date) {
		String endDay = new SimpleDateFormat(LATE_IN_THE_DAY).format(date);
		try {
			return parserStringToDate(endDay, MAX_LONG_DATE_FORMAT_STR);
		} catch (ParseException e) {
		}
		return null;
	}

	public static String formatDate(Date date, String format) {
		DateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(date);
	}

	public static String formatDate(long millisecond, String format) {
		DateFormat dateFormat = new SimpleDateFormat(format);
		Date date = new Date(millisecond);
		return dateFormat.format(date);
	}
	
	public static Calendar getCalendar(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

	public static long truncateDate(Date beginDate, Date endDate) {
		if (endDate != null && beginDate != null) {
			GregorianCalendar end = new GregorianCalendar();
			end.setTime(endDate);

			GregorianCalendar begin = new GregorianCalendar();
			begin.setTime(beginDate);

			return (end.getTimeInMillis() - begin.getTimeInMillis()) / DAY_LONG;
		}
		return 0;
	}

	public static void main(String[] args) throws Exception {
		Date date1 = parserStringToDate("2014-07-02", SHORT_DATE_FORMAT_STR);
		Date date2 = parserStringToDate("2014-07-01", SHORT_DATE_FORMAT_STR);
		System.out.println(truncateDate(date1, date2));
		
		System.out.println(formatDate(getFirstOfDay(new Date()),"yyyy-MM-dd HH:mm:ss SSS"));
		System.out.println(formatDate(getEndOfDay(new Date()),"yyyy-MM-dd HH:mm:ss SSS"));
	}
}
