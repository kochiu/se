package com.kochiu.se.rpc.dubbo.listener;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.Exporter;
import com.alibaba.dubbo.rpc.ExporterListener;
import com.alibaba.dubbo.rpc.RpcException;

@Activate(group = Constants.EXPORTER_LISTENER_KEY)
public class DubboServiceExporterListener implements ExporterListener {

	public static final String EXTENSION_NAME = "export";
	
	@Override
	public void exported(Exporter<?> exporter) throws RpcException {
	}

	@Override
	public void unexported(Exporter<?> exporter) {
		
	}
}
