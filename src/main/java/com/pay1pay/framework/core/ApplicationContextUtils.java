/**
 * 
 */
package com.pay1pay.framework.core;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author jetdu
 * 
 */
public class ApplicationContextUtils implements ApplicationContextAware {
	public static ApplicationContext applicationContext;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.context.ApplicationContextAware#setApplicationContext
	 * (org.springframework.context.ApplicationContext)
	 */
	@Override
	public void setApplicationContext(ApplicationContext appContext) throws BeansException {
		applicationContext = appContext;

	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

}
