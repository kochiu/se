package com.kochiu.se.rpc.dubbo.extension;

import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.alibaba.dubbo.rpc.ExporterListener;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.ProxyFactory;
import com.alibaba.dubbo.rpc.cluster.RouterFactory;
import com.kochiu.se.rpc.dubbo.client.DubboClientFilter;
import com.kochiu.se.rpc.dubbo.exception.DubboServiceExceptionFilter;
import com.kochiu.se.rpc.dubbo.listener.DubboServiceExporterListener;
import com.kochiu.se.rpc.dubbo.provider.DubboServiceFactory;
import com.kochiu.se.rpc.dubbo.route.SeConditionRouterFactory;

public class DubboExtensionLoader {

	@SuppressWarnings({ "unchecked", "rawtypes", "deprecation" })
	public static void loadExtension() {
		ExtensionLoader proxyFactoryExtensionLoader = ExtensionLoader.getExtensionLoader(ProxyFactory.class);
		proxyFactoryExtensionLoader.replaceExtension(DubboServiceFactory.EXTENSION_NAME, DubboServiceFactory.class);

		ExtensionLoader filterExtensionLoader = ExtensionLoader.getExtensionLoader(Filter.class);
		filterExtensionLoader.replaceExtension(DubboClientFilter.EXTENSION_NAME, DubboClientFilter.class);
		filterExtensionLoader.replaceExtension(DubboServiceExceptionFilter.EXTENSION_NAME, DubboServiceExceptionFilter.class);

		ExtensionLoader routerExtensionLoader = ExtensionLoader.getExtensionLoader(RouterFactory.class);
		routerExtensionLoader.replaceExtension(SeConditionRouterFactory.EXTENSION_NAME, SeConditionRouterFactory.class);
	
//		ExtensionLoader exporterExtensionLoader = ExtensionLoader.getExtensionLoader(ExporterListener.class);
//		exporterExtensionLoader.replaceExtension(DubboServiceExporterListener.EXTENSION_NAME, DubboServiceExporterListener.class);
	}

}
