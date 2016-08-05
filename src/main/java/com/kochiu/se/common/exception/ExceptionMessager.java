package com.kochiu.se.common.exception;

/**
 * 
 * @author zhihongp
 *
 */
public class ExceptionMessager {
	
	private final static String BE_INDEX = "BusinessException:";
	
	private final static String SE_INDEX = "SystemException:";
	
	public static String getExceptionMessage(Throwable ex) {
		String message = ex.getMessage();
		int index1 = message.indexOf("\n");

		if (index1 != -1) {
			message = message.substring(0, index1);
		}

		int index2 = message.indexOf("\r");

		if (index2 != -1) {
			message = message.substring(0, index2);
		}

		int index3 = -1;
		
		if (BusinessException.class.isAssignableFrom(ex.getClass()) || ex.getClass().isInstance(new BusinessException())) {
			index3 = message.lastIndexOf(BE_INDEX);
			
			if (index3 != -1) {
				index3 = index3 + BE_INDEX.length() - 1;
			} else {
				index3 = message.lastIndexOf(":");
			}
		} else if (SystemException.class.isAssignableFrom(ex.getClass()) || ex.getClass().isInstance(new SystemException())) {
			index3 = message.lastIndexOf(SE_INDEX) + SE_INDEX.length();
			
			if (index3 != -1) {
				index3 = index3 + SE_INDEX.length() - 1;
			} else {
				index3 = message.lastIndexOf(":");
			}
		} 
		
		if (index3 != -1) {
			message = message.substring(index3 + 1).trim();
		}

		return message;
	}
	
}
