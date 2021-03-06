package com.kochiu.se.core.container;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DefaultContainer {

	private static ClassPathXmlApplicationContext context;

	private static String configPath = "classpath*:spring-config.xml";

	public static void main(String[] args) {
		context = new ClassPathXmlApplicationContext(new String[] { configPath });
		context.start();
	}
	
}
