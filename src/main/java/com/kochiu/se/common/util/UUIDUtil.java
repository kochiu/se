package com.kochiu.se.common.util;

import java.util.UUID;

/**
 * 生成UUID
 * 
 * @author zhihongp
 * 
 */
public class UUIDUtil {

	/**
	 * 获得一个去掉“-”符号的UUID
	 * 
	 * @return StringUUID
	 */
	public static String getUUID() {
		String s = UUID.randomUUID().toString().replaceAll("-", "");
		return s;
	}
	
	/**
	 * 获得一个只包含数字的UUID(19位)
	 * 
	 * @return StringUUID
	 */
	public static String getNumUUID() {
		String s = String.valueOf(UUID.randomUUID().getLeastSignificantBits());
		return s;
	}
	
	/**
	 * 获得指定数目标UUID
	 * 
	 * @param number int 必需获得的UUID数量
	 * @return String[] UUID数组
	 */
	public static String[] getUUID(int number) {
		if (number < 1) {
			return null;
		}
		String[] ss = new String[number];
		for (int i = 0; i < number; i++) {
			ss[i] = getUUID();
		}

		return ss;
	}
	
	public static void main(String[] args) {
		String id1 = UUIDUtil.getUUID();
		String id2 = UUIDUtil.getNumUUID();
		
		System.out.println(id1);
		System.out.println(id2);
	}

}
