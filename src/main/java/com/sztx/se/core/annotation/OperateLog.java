package com.sztx.se.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface OperateLog {
	/**
	 * 模块
	 * @return
	 */
	String module();
	/**
	 * 动作
	 * @return
	 */
	String action();
}
