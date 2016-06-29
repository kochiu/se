package com.sztx.se.common.util.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import com.sztx.se.common.exception.BusinessException;

public class DayTimeUtil {
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH");
	private SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");

	
	private Date beginTime;//查询开始时间
	private Date endTime;//查询结束
	private String shortBeginDay;//展示段日期
	private String shortEndDay;//展示段日期
	private String beginHour;//查询开始小时数
	private String endHour;//查询结束小时数
	
	private boolean includeToday;//包含今天
	private boolean includeReport;//包含报表库
	
	private Date beginTodayTime;//业务库查询开始时间
	private Date endTodayTime;//业务库查询结束时间
	
	private String beginDay;//报表库查询开始日期
	private String endDay;//报表库查询结束日期
	private String beginDayHour;//报表库查询开始日期时间（上架、收货明细）2014-08-11 10:00:00.0
	private String endDayHour;//报表库查询结束日期时间（上架收货明细）2014-08-11 15:59:59.999
	
	public DayTimeUtil(String begin, String end, Integer interval) {
		if ((StringUtils.isBlank(begin)) || (StringUtils.isBlank(end))) {
			throw new BusinessException("请选择时间区间");
		}
		try {
			beginTime = DateUtil.parserStringToDate(begin+":00:00", "yyyy-MM-dd HH:mm:ss");
		} catch (ParseException e) {
			throw new BusinessException("开始时间格式不对");
		}
		Calendar calendarBegin = Calendar.getInstance();
		calendarBegin.setTime(beginTime);
		calendarBegin.set(calendarBegin.get(Calendar.YEAR), calendarBegin.get(Calendar.MONTH), calendarBegin.get(Calendar.DAY_OF_MONTH), calendarBegin.get(Calendar.HOUR_OF_DAY), 0, 0);
		try {
			endTime = DateUtil.parserStringToDate(end+":59:59.999", "yyyy-MM-dd HH:mm:ss.SSS");
		} catch (ParseException e) {
			throw new BusinessException("结束时间格式不对");
		}
		
		Calendar calendarEnd = Calendar.getInstance();
		calendarEnd.setTime(endTime);
		calendarEnd.set(calendarEnd.get(Calendar.YEAR), calendarEnd.get(Calendar.MONTH), calendarEnd.get(Calendar.DAY_OF_MONTH), calendarEnd.get(Calendar.HOUR_OF_DAY), 59, 59);
		
		if (interval != null) {
			if (((calendarEnd.getTimeInMillis() - calendarBegin.getTimeInMillis()) / (24 * 60 * 60 * 1000)+1) > interval) {
				throw new BusinessException(String.format("查询不能超过%s天", interval));
			}
		}
		if(beginTime.after(endTime)){
			throw new BusinessException("开始时间不能晚于结束时间");
		}
		//查询条件中的时间
		shortBeginDay = DateUtil.formatDate(beginTime,DateUtil.SHORT_DATE_FORMAT_STR);
		shortEndDay = DateUtil.formatDate(endTime, DateUtil.SHORT_DATE_FORMAT_STR);
		beginHour = String.valueOf(calendarBegin.get(Calendar.HOUR_OF_DAY));
		endHour = String.valueOf(calendarEnd.get(Calendar.HOUR_OF_DAY));
		
		//今天最早时间
		Date now = new Date();
		Calendar calendarTodayBegin = Calendar.getInstance();
		calendarTodayBegin.setTime(now);
		calendarTodayBegin.set(calendarTodayBegin.get(Calendar.YEAR), calendarTodayBegin.get(Calendar.MONTH), calendarTodayBegin.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		calendarTodayBegin.set(Calendar.MILLISECOND, 0);
		//今天最晚时间
		Calendar calendarTodayEnd = Calendar.getInstance();
		calendarTodayEnd.setTime(now);
		calendarTodayEnd.set(calendarTodayEnd.get(Calendar.YEAR), calendarTodayEnd.get(Calendar.MONTH), calendarTodayEnd.get(Calendar.DAY_OF_MONTH), calendarTodayEnd.get(Calendar.HOUR_OF_DAY), 59, 59);
		calendarTodayEnd.set(Calendar.MILLISECOND, 999);
		Date todayBeginTime = calendarTodayBegin.getTime();
		Date todayEndTime = calendarTodayEnd.getTime();
		Date yesterdayEndTime = new Date(todayBeginTime.getTime() - 1);
		
		//如果查询开始时间大于今天的最早时间,且小于今天的最晚时间
		if(beginTime.getTime() >= todayBeginTime.getTime() && beginTime.getTime() <= todayEndTime.getTime()){
			includeToday = true;
			includeReport = false;
			beginTodayTime = this.beginTime;
			endTodayTime = this.endTime;
		}
		//如果结束时间大于今天的最早时间，且小于今天的最晚时间 ,且查询开始时间小于今天的最早时间
		else if(endTime.getTime() >= todayBeginTime.getTime() && endTime.getTime() <= todayEndTime.getTime() && beginTime.getTime() < todayBeginTime.getTime()){
			includeToday = true;
			includeReport = true;
			beginTodayTime = todayBeginTime;
			endTodayTime = this.endTime;
			beginDay = DateUtil.formatDate(beginTime, DateUtil.SHORT_DATE_FORMAT_STR);
			endDay = DateUtil.formatDate(yesterdayEndTime, DateUtil.SHORT_DATE_FORMAT_STR);
			beginDayHour = DateUtil.formatDate(beginTime, "yyyy-MM-dd HH:00:00");
			endDayHour = DateUtil.formatDate(yesterdayEndTime, "yyyy-MM-dd HH:59:59");
		}
		//如果开始时间小于今天的最早时间，结束时间大于今天的最晚时间
		else if(endTime.getTime() > todayEndTime.getTime() && beginTime.getTime() < todayBeginTime.getTime()){
			includeToday = true;
			includeReport = true;
			beginTodayTime = todayBeginTime;
			endTodayTime = this.endTime;
			beginDay = DateUtil.formatDate(beginTime, DateUtil.SHORT_DATE_FORMAT_STR);
			endDay = DateUtil.formatDate(yesterdayEndTime, DateUtil.SHORT_DATE_FORMAT_STR);
			beginDayHour = DateUtil.formatDate(beginTime, "yyyy-MM-dd HH:00:00");
			endDayHour = DateUtil.formatDate(yesterdayEndTime, "yyyy-MM-dd HH:59:59");
		}
		//如果查询结束时间小于今天的最早时间
		else if(endTime.before(todayBeginTime)){
			includeToday = false;
			includeReport = true;
			beginDay = DateUtil.formatDate(beginTime, DateUtil.SHORT_DATE_FORMAT_STR);
			endDay = DateUtil.formatDate(endTime, DateUtil.SHORT_DATE_FORMAT_STR);
			beginDayHour = DateUtil.formatDate(beginTime, "yyyy-MM-dd HH:00:00");
			endDayHour = DateUtil.formatDate(endTime, "yyyy-MM-dd HH:59:59");
		}
	}
	public DayTimeUtil(String searchTime) {
		if (StringUtils.isBlank(searchTime)) {
			throw new BusinessException("查询时间不能为空");
		}
		try {
			beginTime = dateFormat2.parse(searchTime);
			endTime = dateFormat2.parse(searchTime);
		} catch (ParseException e) {
			throw new BusinessException("查询时间格式不对");
		}
	}
	/**
	 * 将日期字符串转换成毫秒
	 */
	public static Long StringToSeconds(String dateTime){
		if(StringUtils.isNotEmpty(dateTime)){
			try {
				return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTime).getTime();
			} catch (ParseException e) {
				throw new BusinessException("时间转换格式为yyyy-MM-dd HH:mm:ss");
			}
		}else{
			throw new BusinessException("时间不能为空");
		}
	}
	/**
	 * 获取某天最早的时间，精确到秒
	 * @param date
	 * @return
	 */
	public static Date getBeginOfDay(Date date) {
		String beginDayHour = DateFormatUtils.format(date, "yyyy-MM-dd 00:00:00");
		return parseDate(beginDayHour, "yyyy-MM-dd HH:mm:ss");
	}
	
	//得到日期区间所有的日期（传入的日期格式必须是"yyyy-MM-dd"）
	public List<String> getAllDates(String startTime,String endTime){
		List <String> list = new ArrayList<String>();
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
			Date begin=sdf.parse(startTime);      
			Date end=sdf.parse(endTime);      
			double between=(end.getTime()-begin.getTime())/1000;//除以1000是为了转换成秒      
			double day=between/(24*3600);
			for(int i = 0;i<=day;i++){
				Calendar cd = Calendar.getInstance();   
				cd.setTime(sdf.parse(startTime));   
				cd.add(Calendar.DATE, i);//增加一天   
				list.add(sdf.format(cd.getTime()));
			}
		}catch(Exception e){
				throw new BusinessException("日期转化失败");
		}
		return list;
	}
	
	public static Date parseDate(String date,String format){
		try {
			return org.apache.commons.lang.time.DateUtils.parseDate(date, new String[]{format});
		} catch (ParseException e) {}
		return null;
	}
	
	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public boolean isIncludeToday() {
		return includeToday;
	}

	public void setIncludeToday(boolean includeToday) {
		this.includeToday = includeToday;
	}

	public boolean isIncludeReport() {
		return includeReport;
	}

	public void setIncludeReport(boolean includeReport) {
		this.includeReport = includeReport;
	}

	public SimpleDateFormat getDateFormat() {
		return dateFormat;
	}

	public String getBeginHour() {
		return beginHour;
	}

	public String getBeginDay() {
		return beginDay;
	}

	public String getEndHour() {
		return endHour;
	}

	public String getEndDay() {
		return endDay;
	}

	public String getShortBeginDay() {
		return shortBeginDay;
	}

	public String getShortEndDay() {
		return shortEndDay;
	}

	public Date getBeginTodayTime() {
		return beginTodayTime;
	}

	public Date getEndTodayTime() {
		return endTodayTime;
	}

	public String getEarerBeginDay() {
		return DateUtil.formatDate(beginTime,"yyyy-MM-dd HH:00:00");
	}

	public String getLateEndDay() {
		return DateUtil.formatDate(endTime,"yyyy-MM-dd HH:59:59");
	}
	
	public String getBeginDayHour() {
		return beginDayHour;
	}

	public String getEndDayHour() {
		return endDayHour;
	}

}
