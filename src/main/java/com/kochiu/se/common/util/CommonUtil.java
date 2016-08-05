package com.kochiu.se.common.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.kochiu.se.common.exception.BusinessException;

/**
 * 系统通用工具类
 * 
 * @author zhihong.pzh
 * 
 */
public class CommonUtil {

	public static String OS = System.getProperty("os.name").toLowerCase();

	public static boolean isTrue(Boolean o) {
		if (o == null) {
			return false;
		} else {
			return o;
		}
	}

	/**
	 * List是否为空
	 * 
	 * @param list
	 * @return
	 */
	public static <E> boolean isListEmpty(List<E> list) {
		return list == null || list.isEmpty();
	}

	public static void assertNotBlank(String text, String message) {
		if (text == null || text.trim().isEmpty())
			throw new BusinessException(message);
	}

	public static <T> void assertNotNull(T t, String message) {
		if (t == null)
			throw new BusinessException(message);
	}

	public static <T> void assertListNotNull(List<T> list, String message) {
		if (isListEmpty(list))
			throw new BusinessException(message);
	}

	public static String formatNum(Object num) {
		DecimalFormat df = new DecimalFormat("#0.##########");
		return df.format(num);
	}

	public static <T> void assertNotEq(String arg1, String arg2, String message) {
		if (!arg1.equalsIgnoreCase(arg2)) {
			throw new BusinessException(message);
		}
	}

	/**
	 * List内部去重后还保持顺序
	 * 
	 * @param list
	 */
	public static void removeDuplicateWithOrder(List<String> list) {
		Set<String> set = new HashSet<String>();
		List<String> newList = new ArrayList<String>();
		for (Iterator<String> iter = list.iterator(); iter.hasNext();) {
			String element = iter.next();
			if (set.add(element))
				newList.add(element);
		}
		list.clear();
		list.addAll(newList);
	}

	/**
	 * 判断当前操作系统是否是windows
	 * 
	 * @return
	 */
	public static boolean isWindows() {
		return (OS.indexOf("win") != -1);
	}

	//
	// public static SimpleDateFormat DataFormatter = new
	// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	//
	// /**
	// * 返回固定长度的正整数(不以0和1开头)
	// *
	// * @param length 固定长度
	// * @return
	// */
	// public static int getLengthRandomNum(int length) {
	// Random rm = new Random();
	// int strLength = 8;
	// int firstNum = 0;
	//
	// while (firstNum == 0 || firstNum == 1) {
	// firstNum = rm.nextInt(9);
	// }
	//
	// // 获得随机数
	// int pross = (int) ((firstNum + rm.nextDouble()) * Math.pow(10,
	// strLength));
	//
	// // 将获得的获得随机数转化为字符串
	// String fixLenthString = String.valueOf(pross);
	//
	// // 返回固定的长度的随机数
	// String numberStr = fixLenthString.substring(0, strLength);
	//
	// return Integer.valueOf(numberStr);
	// }
	//
	// /**
	// * 获取当前时间(yyyy-MM-dd HH:mm:ss)
	// *
	// * @return
	// */
	// public static String currentTimeStr() {
	// String dateStr = DataFormatter.format(new Date());
	//
	// return dateStr;
	// }
	//
	// /**
	// * 时间字符串(yyyy-MM-dd HH:mm:ss)
	// *
	// * @param format yyyyMMdd
	// * @return
	// */
	// public static String current(String format) {
	//
	// SimpleDateFormat formatter = DataFormatter;
	// if(StringUtils.isNotBlank(format)){
	// formatter = new SimpleDateFormat(format);
	// }
	//
	// return formatter.format(currentTime());
	// }
	//
	// /**
	// * 获取当前时间(Date)
	// *
	// * @return Date
	// */
	// public static Date currentTime() {
	// return new Date();
	// }
	//
	// /**
	// * 时间字符串(yyyy-MM-dd HH:mm:ss)转换为Data
	// *
	// * @return
	// */
	// public static Date Str2Data(String str) {
	// Date date;
	// try {
	// date = DataFormatter.parse(str);
	// } catch (ParseException e) {
	// date = new Date();
	// }
	//
	// return date;
	// }
	//
	// /**
	// * Data转换为时间字符串(yyyy-MM-dd HH:mm:ss)
	// *
	// * @return
	// */
	// public static String Data2Str(Date date) {
	// if (date == null)
	// return "";
	// else
	// return DataFormatter.format(date);
	// }
	//
	// /**
	// * 字符串转整形
	// *
	// * @param str
	// * @param defualt
	// * @return
	// */
	// public static int Str2Int(String str, int defualt) {
	// try {
	// if (StringUtils.isNotBlank(str))
	// return Integer.parseInt(str);
	// } catch (Exception e) {
	// }
	//
	// return defualt;
	// }
	//
	// /**
	// * 字符串转long
	// *
	// * @param str
	// * @param defualt
	// * @return
	// */
	// public static long Str2Long(String str, int defualt) {
	// try {
	// if (StringUtils.isNotBlank(str))
	// return Long.parseLong(str);
	// } catch (Exception e) {
	// }
	//
	// return defualt;
	// }
	//
	// public static int StringToInt(String str, int defualt) {
	// try {
	// if (StringUtils.isNotBlank(str))
	// return Integer.parseInt(str);
	// } catch (Exception e) {
	// }
	//
	// return defualt;
	// }
	//
	//
	//
	// /**
	// * 列表分页(当请求的页数大于列表长度时，返回空集合)
	// *
	// * @param list 分页的列表
	// * @param pageNo 页号
	// * @param pageSize 每页数据条数
	// * @return
	// */
	// public static <E> List<E> pageList(List<E> list, int pageNo, int
	// pageSize) {
	// List<E> pList = new ArrayList<E>();
	//
	// if (list != null && !list.isEmpty()) {
	// int size = list.size();
	// int start = (pageNo - 1) * pageSize;
	// int end = start + pageSize - 1;
	//
	// for (int i = 0; i < size; i++) {
	// if (i >= start && i <= end) {
	// pList.add(list.get(i));
	// }
	// }
	// }
	//
	// return pList;
	// }
	//
	/**
	 * 获取list对应的字符串(中间以指定分隔符链接)
	 *
	 * @param list 需要转换的列表
	 * @param splitSign 指定分隔符
	 * @return list对应的字符串
	 */
	public static String getListStr(List<String> list, String splitSign) {
		StringBuffer listSb = new StringBuffer();

		if (list != null && !list.isEmpty()) {
			int size = list.size();

			for (int i = 0; i < size; i++) {
				listSb.append(list.get(i));
				int end = size - 1;

				if (i != end) {
					listSb.append(splitSign);
				}
			}
		}

		return listSb.toString();
	}

	/**
	 * 获取stringArray对应的字符串(中间以指定分隔符链接)
	 *
	 * @param list 需要转换的列表
	 * @param splitSign 指定分隔符
	 * @return list对应的字符串
	 */
	public static String getStringArrayStr(String[] strArray, String splitSign) {
		StringBuffer stringArraySb = new StringBuffer();

		if (strArray != null && strArray.length > 0) {
			int size = strArray.length;

			for (int i = 0; i < size; i++) {
				stringArraySb.append(strArray[i]);
				int end = size - 1;

				if (i != end) {
					stringArraySb.append(splitSign);
				}
			}
		}

		return stringArraySb.toString();
	}
	
	/**
	 * 获取字符串对应的list
	 *
	 * @param list 需要转换的列表
	 * @param splitSign 指定分隔符
	 * @return list对应的字符串
	 */
	public static List<String> getList(String listStr, String splitSign) {
		List<String> list = new ArrayList<String>();

		if (StringUtil.isNotBlank(listStr)) {
			String[] array = listStr.split(splitSign);

			for (String e : array) {
				list.add(e);
			}
		}

		return list;
	}

}