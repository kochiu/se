package com.kochiu.se.rpc.dubbo.provider;

import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.bytecode.Proxy;
import com.alibaba.dubbo.common.bytecode.Wrapper;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.proxy.AbstractProxyFactory;
import com.alibaba.dubbo.rpc.proxy.AbstractProxyInvoker;
import com.alibaba.dubbo.rpc.proxy.InvokerInvocationHandler;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.kochiu.se.common.domain.ContextConstants;
import com.kochiu.se.common.util.date.DateUtil;

public class DubboServiceFactory extends AbstractProxyFactory {

	public static final String EXTENSION_NAME = "javassist";

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static boolean openLog;

	private static int logLength;

	public static void setOpenLog(boolean openLog) {
		DubboServiceFactory.openLog = openLog;
	}

	public static void setLogLength(int logLength) {
		DubboServiceFactory.logLength = logLength;
	}

	@SuppressWarnings("unchecked")
	public <T> T getProxy(Invoker<T> invoker, Class<?>[] interfaces) {
		return (T) Proxy.getProxy(interfaces).newInstance(new InvokerInvocationHandler(invoker));
	}

	public <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url) {
		// TODO Wrapper类不能正确处理带$的类名
		final Wrapper wrapper = Wrapper.getWrapper(proxy.getClass().getName().indexOf('$') < 0 ? proxy.getClass() : type);
		return new AbstractProxyInvoker<T>(proxy, type, url) {
			@Override
			protected Object doInvoke(T proxy, String methodName, Class<?>[] parameterTypes, Object[] arguments) throws Throwable {
				if (openLog) {
					long startTime = System.currentTimeMillis();
					long endTime = 0;
					Object obj = null;
					String className = this.getInterface().getCanonicalName();

					try {
						obj = wrapper.invokeMethod(proxy, methodName, parameterTypes, arguments);
						return obj;
					} catch (Throwable t) {
						if (t instanceof InvocationTargetException) {
							InvocationTargetException ite = (InvocationTargetException) t;
							Throwable e = ite.getTargetException();
							obj = e.getClass().getCanonicalName() + ":" + e.getMessage();
						} else {
							obj = t.getClass().getCanonicalName() + ":" + t.getMessage();
						}
						
						throw t;
					} finally {
						try {
							String inputParams = "";
							String rspResult = "";

							if (arguments != null) {
								inputParams = JSON.toJSONStringWithDateFormat(arguments, DateUtil.MAX_LONG_DATE_FORMAT_STR,
										SerializerFeature.DisableCircularReferenceDetect);
							}

							if (obj != null) {
								rspResult = JSON.toJSONStringWithDateFormat(obj, DateUtil.MAX_LONG_DATE_FORMAT_STR,
										SerializerFeature.DisableCircularReferenceDetect);
							}

							endTime = (endTime == 0 ? System.currentTimeMillis() : endTime);
							// 打印日志
							String rpcLog = getRpcLog(className, methodName, inputParams, rspResult, startTime, endTime);
							int logLength = DubboServiceFactory.logLength != 0 ? DubboServiceFactory.logLength : ContextConstants.LOG_MAX_LENGTH;

							if (logLength != -1 && rpcLog.length() > logLength) {
								rpcLog = rpcLog.substring(0, logLength);
							}

							logger.info(rpcLog);
						} catch (Exception e) {
							logger.error("DubboServiceFactory error", e);
						}
					}
				} else {
					return wrapper.invokeMethod(proxy, methodName, parameterTypes, arguments);
				}
			}

			private String getRpcLog(String className, String methodName, String inputParams, String rspResult, long startTime, long endTime) {
				String localAddress = RpcContext.getContext().getLocalAddressString();
				String remoteAddress = RpcContext.getContext().getRemoteAddressString();
				long cost = endTime - startTime;
				String startTimeStr = DateUtil.formatDate(startTime, DateUtil.MAX_LONG_DATE_FORMAT_STR);
				String endTimeStr = DateUtil.formatDate(endTime, DateUtil.MAX_LONG_DATE_FORMAT_STR);
				return String.format("[Provider] %s -> %s - %s|%s|IN:%s|OUT:%s|[start:%s, end:%s, cost:%dms]", remoteAddress, localAddress, className, methodName, inputParams, rspResult, startTimeStr, endTimeStr, cost);
			}
		};
	}
}