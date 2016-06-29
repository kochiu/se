package com.sztx.se.common.util.validate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.sztx.se.common.exception.BusinessException;
import com.sztx.se.common.exception.SystemException;

public class ValidateUtil {
	
	public static void notNull(Object object, String message) {
		if (object == null) {
			throw new BusinessException(message);
		}
	}
	
	public static void isNotBlank(String str, String message){
		if(StringUtils.isBlank(str)){
			throw new BusinessException(message);
		}
	}
	
	
	public static void isTrue(boolean expression, String message) {
		if (!expression) {
			throw new BusinessException(message);
		}
	}
	
	public static void isNotTrue(boolean expression, String message) {
		if (expression) {
			throw new BusinessException(message);
		}
	}
	
	/**
	 * 判断集合是否为空
	 * @param object
	 * @param message
	 */
	public static void notEmpty(Collection<?> object, String message) {
		if (object == null || object.isEmpty()) {
			throw new BusinessException(message);
		}
	}
	
	/**
	 * 判断是否是空字符串
	 * @param str
	 * @param message
	 */
	public static void notEmpty(String str,String message){
		if (str == null || str == "") {
			throw new BusinessException(message);
		}
	}
	
	/**
	 * 断言是否是正确的日期格式，否在抛出Bussiness异常
	 * @param theNum
	 * @param diff
	 * @param message
	 */
	public static Date isDate(String date,String format,String message){
		if (date == null || date == "") {
			throw new BusinessException(message);
		}
		try {
			Date d = new SimpleDateFormat(format).parse(date);
			if(d == null){
				throw new BusinessException(message);
			}
			return d;
		} catch (ParseException e) {
			throw new BusinessException(message);
		}
	}
	
	/**
	 * 断言是否小于指定数字，否在抛出Bussiness异常
	 * @param theNum
	 * @param diff
	 * @param message
	 */
	public static void lt(Integer theNum,Integer diff,String message){
		if (theNum == null || diff == null) {
			throw new BusinessException(message);
		}
		if(theNum >= diff){
			throw new BusinessException(message);
		}
	}
	
	/**
	 * 断言是否小于等于指定数字，否在抛出Bussiness异常
	 * @param theNum
	 * @param diff
	 * @param message
	 */
	public static void le(Integer theNum,Integer diff,String message){
		if (theNum == null || diff == null) {
			throw new BusinessException(message);
		}
		if(theNum > diff){
			throw new BusinessException(message);
		}
	}
	
	/**
	 * 断言是否大于指定数字，否在抛出Bussiness异常
	 * @param theNum
	 * @param diff
	 * @param message
	 */
	public static void gt(Integer theNum,Integer diff,String message){
		if (theNum == null || diff == null) {
			throw new BusinessException(message);
		}
		if(theNum <= diff){
			throw new BusinessException(message);
		}
	}
	
	/**
	 * 断言是否大于等于指定数字，否在抛出Bussiness异常
	 * @param theNum
	 * @param diff
	 * @param message
	 */
	public static void ge(Integer theNum,Integer diff,String message){
		if (theNum == null || diff == null) {
			throw new BusinessException(message);
		}
		if(theNum < diff){
			throw new BusinessException(message);
		}
	}
	
	/**
	 * 断言字符串是否超长
	 * @param str
	 * @param maxLength
	 * @param message
	 */
	public static void strMaxLen(String str, int maxLength, String message) {
		if (str == null) {
			throw new SystemException("string is null");
		}
		if(str.length() > maxLength) {
			throw new BusinessException(message);
		}
	}
	
	/**
	 * 断言字符串是否是固定长度
	 * @param str
	 * @param equalLength
	 * @param message
	 */
	public static void strEqualLen(String str, int equalLength, String message) {
		if (str == null) {
			throw new SystemException("string is null");
		}
		if(str.length() != equalLength) {
			throw new BusinessException(message);
		}
	}
	
}
