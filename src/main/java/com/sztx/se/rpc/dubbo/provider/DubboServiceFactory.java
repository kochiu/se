package com.sztx.se.rpc.dubbo.provider;

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
import com.sztx.se.common.domain.ContextConstants;
import com.sztx.se.common.util.date.DateUtil;

public class DubboServiceFactory extends AbstractProxyFactory {

	public static final String EXTENSION_NAME = "javassist";

	private static final Logger logger = LoggerFactory.getLogger(DubboServiceFactory.class);

	private static boolean openLog;
	
	public static void setOpenLog(boolean openLog) {
		DubboServiceFactory.openLog = openLog;
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

					try {
						obj = wrapper.invokeMethod(proxy, methodName, parameterTypes, arguments);
						return obj;
					} catch (Throwable t) {
						obj = t.getClass().getCanonicalName() + ":" + t.getMessage();
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
							String rpcLog = getRpcLog(proxy, methodName, inputParams, rspResult, startTime, endTime);

							if (rpcLog.length() > ContextConstants.LOG_MAX_LENGTH) {
								rpcLog = rpcLog.substring(0, ContextConstants.LOG_MAX_LENGTH);
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

			private String getRpcLog(T proxy, String methodName, String inputParams, String rspResult, long startTime, long endTime) {
				String localAddress = RpcContext.getContext().getLocalAddressString();
				String remoteAddress = RpcContext.getContext().getRemoteAddressString();
				long cost = endTime - startTime;
				String startTimeStr = DateUtil.formatDate(startTime, DateUtil.MAX_LONG_DATE_FORMAT_STR);
				String endTimeStr = DateUtil.formatDate(endTime, DateUtil.MAX_LONG_DATE_FORMAT_STR);
				return String.format("[Provider] %s -> %s - %s|%s|IN:%s|OUT:%s|[start:%s, end:%s, cost:%dms]", remoteAddress, localAddress, proxy.getClass()
						.getCanonicalName(), methodName, inputParams, rspResult, startTimeStr, endTimeStr, cost);
			}
		};
	}
}