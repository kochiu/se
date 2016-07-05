package com.kochiu.se.common.util;

import java.util.Comparator;

public class StringComparator implements Comparator<String>, java.io.Serializable {
	private static final long serialVersionUID = -570083289829987307L;

	public static StringComparator STRING_COMPARATOR = new StringComparator();
	
	@SuppressWarnings("unused")
	public int compare(String s1, String s2) {
	    int n1 = s1.length();
	    int n2 = s2.length();
	    int min = Math.min(n1, n2);
	    for (int i = 0; i < min; i++) {
	        char c1 = s1.charAt(i);
	        char c2 = s2.charAt(i);
            return c1 - c2;
	    }
	    return n1 - n2;
	}
}
