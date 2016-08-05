package com.kochiu.se.rpc.dubbo.client;

import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.common.utils.NetUtils;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.RpcInvocation;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.kochiu.se.common.domain.ContextConstants;
import com.kochiu.se.common.util.date.DateUtil;

@Activate(group = Constants.CONSUMER, order = -10000)
public class DubboClientFilter implements Filter {

	public static final String EXTENSION_NAME = "consumercontext";

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static boolean openLog;

	private static int logLength;

	public static void setOpenLog(boolean openLog) {
		DubboClientFilter.openLog = openLog;
	}

	public static void setLogLength(int logLength) {
		DubboClientFilter.logLength = logLength;
	}

	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		if (openLog) {
			long startTime = System.currentTimeMillis();
			long endTime = 0;
			RpcContext.getContext().setInvoker(invoker).setInvocation(invocation).setLocalAddress(NetUtils.getLocalHost(), 0)
					.setRemoteAddress(invoker.getUrl().getHost(), invoker.getUrl().getPort());

			if (invocation instanceof RpcInvocation) {
				((RpcInvocation) invocation).setInvoker(invoker);
			}

			Object obj = null;
			String className = invoker.getInterface().getName();
			String methodName = invocation.getMethodName();
			Object[] arguments = invocation.getArguments();

			try {
				obj = invoker.invoke(invocation);
				Result result = getResult(obj, invoker);
				return result;
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
						rspResult = JSON.toJSONStringWithDateFormat(obj, DateUtil.MAX_LONG_DATE_FORMAT_STR, SerializerFeature.DisableCircularReferenceDetect);
					}

					endTime = (endTime == 0 ? System.currentTimeMillis() : endTime);
					// 打印日志
					String rpcLog = getRpcLog(className, methodName, inputParams, rspResult, startTime, endTime);
					int logLength = DubboClientFilter.logLength != 0 ? DubboClientFilter.logLength : ContextConstants.LOG_MAX_LENGTH;

					if (logLength != -1 && rpcLog.length() > logLength) {
						rpcLog = rpcLog.substring(0, logLength);
					}

					logger.info(rpcLog);
				} catch (Exception e) {
					logger.error("DubboClientFilter error", e);
				}

				RpcContext.getContext().clearAttachments();
			}
		} else {
			try {
				Object obj = invoker.invoke(invocation);
				Result result = getResult(obj, invoker);
				return result;
			} finally {
				RpcContext.getContext().clearAttachments();
			}
		}
	}

	private Result getResult(Object obj, Invoker<?> invoker) {
		Result result = (Result) obj;
		return result;
	}

	private String getRpcLog(String className, String methodName, String inputParams, String rspResult, long startTime, long endTime) {
		String localAddress = RpcContext.getContext().getLocalAddressString();
		String remoteAddress = RpcContext.getContext().getRemoteAddressString();
		long cost = endTime - startTime;
		String startTimeStr = DateUtil.formatDate(startTime, DateUtil.MAX_LONG_DATE_FORMAT_STR);
		String endTimeStr = DateUtil.formatDate(endTime, DateUtil.MAX_LONG_DATE_FORMAT_STR);
		return String.format("[Client] %s -> %s - %s|%s|IN:%s|OUT:%s|[start:%s, end:%s, cost:%dms]", remoteAddress, localAddress, className, methodName,
				inputParams, rspResult, startTimeStr, endTimeStr, cost);
	}

}
