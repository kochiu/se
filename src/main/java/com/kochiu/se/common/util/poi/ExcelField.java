package com.kochiu.se.common.util.poi;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Documented
public @interface ExcelField {

	/**
	 * 序号(仅支持String/Double/Integer/Float/Short/Boolean/Date类型)
	 * 
	 * @return
	 */
	int order() default 0;

	/**
	 * 报头名字
	 * 
	 * @return
	 */
	String header() default "";
}