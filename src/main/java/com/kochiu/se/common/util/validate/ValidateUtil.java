package com.kochiu.se.common.util.validate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import com.kochiu.se.common.exception.BusinessException;
import com.kochiu.se.common.exception.SystemException;
import org.apache.commons.lang.StringUtils;

import com.kochiu.se.common.domain.ResultCodeEnum;

public class ValidateUtil {

	/**
	 * 判断对象是否为空，是则抛出Bussiness异常
	 * 
	 * @param object
	 * @param message
	 */
	public static void notNull(Object object, String message) {
		if (object == null) {
			throw new BusinessException(message);
		}
	}

	/**
	 * 判断对象是否为空，是则抛出Bussiness异常
	 * 
	 * @param object
	 * @param resultCode
	 */
	public static void notNull(Object object, ResultCodeEnum resultCode) {
		if (object == null) {
			throw new BusinessException(resultCode.getCode(), resultCode.getDescription());
		}
	}

	/**
	 * 判断是否为空字符串，是则抛出Bussiness异常(包括null, "")
	 * 
	 * @param str
	 * @param message
	 */
	public static void notEmpty(String str, String message) {
		if (str == null || str == "") {
			throw new BusinessException(message);
		}
	}

	/**
	 * 判断是否为空字符串，是则抛出Bussiness异常(包括null, "")
	 * 
	 * @param str
	 * @param message
	 */
	public static void notEmpty(String str, ResultCodeEnum resultCode) {
		if (str == null || str == "") {
			throw new BusinessException(resultCode.getCode(), resultCode.getDescription());
		}
	}

	/**
	 * 判断是否为空字符串，是则抛出Bussiness异常(包括null, "", " ")
	 * 
	 * @param str
	 * @param message
	 */
	public static void isNotBlank(String str, String message) {
		if (StringUtils.isBlank(str)) {
			throw new BusinessException(message);
		}
	}

	/**
	 * 判断是否为空字符串，是则抛出Bussiness异常(包括null, "", " ")
	 * 
	 * @param str
	 * @param resultCode
	 */
	public static void isNotBlank(String str, ResultCodeEnum resultCode) {
		if (StringUtils.isBlank(str)) {
			throw new BusinessException(resultCode.getCode(), resultCode.getDescription());
		}
	}

	/**
	 * 判断集合是否为空，是则抛出Bussiness异常
	 * 
	 * @param object
	 * @param message
	 */
	public static void notEmpty(Collection<?> object, String message) {
		if (object == null || object.isEmpty()) {
			throw new BusinessException(message);
		}
	}

	/**
	 * 判断集合是否为空，是则抛出Bussiness异常
	 * 
	 * @param object
	 * @param resultCode
	 */
	public static void notEmpty(Collection<?> object, ResultCodeEnum resultCode) {
		if (object == null || object.isEmpty()) {
			throw new BusinessException(resultCode.getCode(), resultCode.getDescription());
		}
	}

	/**
	 * 判断集合是否为true，否则抛出Bussiness异常
	 * 
	 * @param expression
	 * @param message
	 */
	public static void isTrue(boolean expression, String message) {
		if (!expression) {
			throw new BusinessException(message);
		}
	}

	/**
	 * 判断集合是否为true，否则抛出Bussiness异常
	 * 
	 * @param expression
	 * @param resultCode
	 */
	public static void isTrue(boolean expression, ResultCodeEnum resultCode) {
		if (!expression) {
			throw new BusinessException(resultCode.getCode(), resultCode.getDescription());
		}
	}

	/**
	 * 判断集合是否不为true，否则抛出Bussiness异常
	 * 
	 * @param expression
	 * @param message
	 */
	public static void isNotTrue(boolean expression, String message) {
		if (expression) {
			throw new BusinessException(message);
		}
	}

	/**
	 * 判断集合是否不为true，否则抛出Bussiness异常
	 * 
	 * @param expression
	 * @param resultCode
	 */
	public static void isNotTrue(boolean expression, ResultCodeEnum resultCode) {
		if (expression) {
			throw new BusinessException(resultCode.getCode(), resultCode.getDescription());
		}
	}

	/**
	 * 判断日期是否是正确的格式，否则抛出Bussiness异常
	 * 
	 * @param date
	 * @param format
	 * @param message
	 */
	public static Date isDate(String date, String format, String message) {
		if (date == null || date == "") {
			throw new BusinessException(message);
		}

		try {
			Date d = new SimpleDateFormat(format).parse(date);

			if (d == null) {
				throw new BusinessException(message);
			}

			return d;
		} catch (ParseException e) {
			throw new BusinessException(message);
		}
	}

	/**
	 * 判断日期是否是正确的格式，否则抛出Bussiness异常
	 * 
	 * @param date
	 * @param format
	 * @param resultCode
	 */
	public static Date isDate(String date, String format, ResultCodeEnum resultCode) {
		if (date == null || date == "") {
			throw new BusinessException(resultCode.getCode(), resultCode.getDescription());
		}
		try {
			Date d = new SimpleDateFormat(format).parse(date);
			if (d == null) {
				throw new BusinessException(resultCode.getCode(), resultCode.getDescription());
			}
			return d;
		} catch (ParseException e) {
			throw new BusinessException(resultCode.getCode(), resultCode.getDescription());
		}
	}

	/**
	 * 判断是否小于指定数字，否则抛出Bussiness异常
	 * 
	 * @param theNum
	 * @param diff
	 * @param message
	 */
	public static void lt(Integer theNum, Integer diff, String message) {
		if (theNum == null || diff == null) {
			throw new BusinessException(message);
		}

		if (theNum >= diff) {
			throw new BusinessException(message);
		}
	}

	/**
	 * 判断是否小于指定数字，否则抛出Bussiness异常
	 * 
	 * @param theNum
	 * @param diff
	 * @param resultCode
	 */
	public static void lt(Integer theNum, Integer diff, ResultCodeEnum resultCode) {
		if (theNum == null || diff == null) {
			throw new BusinessException(resultCode.getCode(), resultCode.getDescription());
		}

		if (theNum >= diff) {
			throw new BusinessException(resultCode.getCode(), resultCode.getDescription());
		}
	}

	/**
	 * 判断是否小于指定数字，否则抛出Bussiness异常
	 * 
	 * @param theNum
	 * @param diff
	 * @param message
	 */
	public static void le(Integer theNum, Integer diff, String message) {
		if (theNum == null || diff == null) {
			throw new BusinessException(message);
		}

		if (theNum > diff) {
			throw new BusinessException(message);
		}
	}

	/**
	 * 判断是否小于指定数字，否则抛出Bussiness异常
	 * 
	 * @param theNum
	 * @param diff
	 * @param resultCode
	 */
	public static void le(Integer theNum, Integer diff, ResultCodeEnum resultCode) {
		if (theNum == null || diff == null) {
			throw new BusinessException(resultCode.getCode(), resultCode.getDescription());
		}

		if (theNum > diff) {
			throw new BusinessException(resultCode.getCode(), resultCode.getDescription());
		}
	}

	/**
	 * 判断是否大于指定数字，否则抛出Bussiness异常
	 * 
	 * @param theNum
	 * @param diff
	 * @param message
	 */
	public static void gt(Integer theNum, Integer diff, String message) {
		if (theNum == null || diff == null) {
			throw new BusinessException(message);
		}

		if (theNum <= diff) {
			throw new BusinessException(message);
		}
	}

	/**
	 * 判断是否大于指定数字，否则抛出Bussiness异常
	 * 
	 * @param theNum
	 * @param diff
	 * @param resultCode
	 */
	public static void gt(Integer theNum, Integer diff, ResultCodeEnum resultCode) {
		if (theNum == null || diff == null) {
			throw new BusinessException(resultCode.getCode(), resultCode.getDescription());
		}

		if (theNum <= diff) {
			throw new BusinessException(resultCode.getCode(), resultCode.getDescription());
		}
	}

	/**
	 * 判断是否大于等于指定数字，否则抛出Bussiness异常
	 * 
	 * @param theNum
	 * @param diff
	 * @param message
	 */
	public static void ge(Integer theNum, Integer diff, String message) {
		if (theNum == null || diff == null) {
			throw new BusinessException(message);
		}

		if (theNum < diff) {
			throw new BusinessException(message);
		}
	}

	/**
	 * 判断是否大于等于指定数字，否则抛出Bussiness异常
	 * 
	 * @param theNum
	 * @param diff
	 * @param resultCode
	 */
	public static void ge(Integer theNum, Integer diff, ResultCodeEnum resultCode) {
		if (theNum == null || diff == null) {
			throw new BusinessException(resultCode.getCode(), resultCode.getDescription());
		}

		if (theNum < diff) {
			throw new BusinessException(resultCode.getCode(), resultCode.getDescription());
		}
	}

	/**
	 * 判断字符串是否大于最大长度，是则抛出Bussiness异常
	 * 
	 * @param str
	 * @param maxLength
	 * @param message
	 */
	public static void strMaxLen(String str, int maxLength, String message) {
		if (str == null) {
			throw new SystemException(message);
		}

		if (str.length() > maxLength) {
			throw new BusinessException(message);
		}
	}

	/**
	 * 判断字符串是否超过指定长度，是则抛出Bussiness异常
	 * 
	 * @param str
	 * @param maxLength
	 * @param resultCode
	 */
	public static void strMaxLen(String str, int maxLength, ResultCodeEnum resultCode) {
		if (str == null) {
			throw new BusinessException(resultCode.getCode(), resultCode.getDescription());
		}

		if (str.length() > maxLength) {
			throw new BusinessException(resultCode.getCode(), resultCode.getDescription());
		}
	}

	/**
	 * 判断字符串是否小于指定长度，是则抛出Bussiness异常
	 * 
	 * @param str
	 * @param minLength
	 * @param message
	 */
	public static void strMinLen(String str, int minLength, String message) {
		if (str == null) {
			throw new SystemException("string is null");
		}

		if (str.length() < minLength) {
			throw new BusinessException(message);
		}
	}

	/**
	 * 判断字符串是否小于指定长度，是则抛出Bussiness异常
	 * 
	 * @param str
	 * @param minLength
	 * @param message
	 */
	public static void strMinLen(String str, int minLength, ResultCodeEnum resultCode) {
		if (str == null) {
			throw new SystemException("string is null");
		}

		if (str.length() < minLength) {
			throw new BusinessException(resultCode.getCode(), resultCode.getDescription());
		}
	}

	/**
	 * 判断字符串是否大于等于指定最小长度，小于等于指定最大长度，否则抛出Bussiness异常
	 * 
	 * @param str
	 * @param minLength
	 * @param message
	 */
	public static void strMinMaxLen(String str, int minLength, int maxLength, String message) {
		if (str == null) {
			throw new SystemException(message);
		}

		if (str.length() < minLength || str.length() > maxLength) {
			throw new BusinessException(message);
		}
	}

	/**
	 * 判断字符串是否大于等于指定最小长度，小于等于指定最大长度，否则抛出Bussiness异常
	 * 
	 * @param str
	 * @param minLength
	 * @param resultCode
	 */
	public static void strMinMaxLen(String str, int minLength, int maxLength, ResultCodeEnum resultCode) {
		if (str == null) {
			throw new BusinessException(resultCode.getCode(), resultCode.getDescription());
		}

		if (str.length() < minLength || str.length() > maxLength) {
			throw new BusinessException(resultCode.getCode(), resultCode.getDescription());
		}
	}

	/**
	 * 判断字符串是否是固定长度，否则抛出Bussiness异常
	 * 
	 * @param str
	 * @param equalLength
	 * @param message
	 */
	public static void strEqualLen(String str, int equalLength, String message) {
		if (str == null) {
			throw new SystemException(message);
		}

		if (str.length() != equalLength) {
			throw new BusinessException(message);
		}
	}

	/**
	 * 判断字符串是否是固定长度，否则抛出Bussiness异常
	 * 
	 * @param str
	 * @param equalLength
	 * @param message
	 */
	public static void strEqualLen(String str, int equalLength, ResultCodeEnum resultCode) {
		if (str == null) {
			throw new BusinessException(resultCode.getCode(), resultCode.getDescription());
		}

		if (str.length() != equalLength) {
			throw new BusinessException(resultCode.getCode(), resultCode.getDescription());
		}
	}
}
