package com.kochiu.se.rpc.dubbo.provider;

import com.kochiu.se.rpc.dubbo.config.DubboConfigServer;

/**
 * Dubbo服务端
 * 
 * @author zhihongp
 *
 */
public class DubboService {

	/**
	 * 接口名
	 */
	private String interfaceName;

	/**
	 * 接口实现
	 */
	private Object interfaceRef;

	/**
	 * 接口版本号
	 */
	private String version;

	/**
	 * 接口负责人
	 */
	private String owner;

	/**
	 * 协议名
	 */
	private String protocols = "dubbo:20880";

	/**
	 * 服务提供超时时间，默认3秒
	 */
	private int timeout = 30000;

	/**
	 * 注册配置中心
	 */
	private DubboConfigServer dubboConfigServer;

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public Class<?> getInterfaceClass() {
		Class<?> interfaceClass = null;

		try {
			interfaceClass = Class.forName(interfaceName, true, Thread.currentThread().getContextClassLoader());
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}

		return interfaceClass;
	}

	public Object getInterfaceRef() {
		return interfaceRef;
	}

	public void setInterfaceRef(Object interfaceRef) {
		this.interfaceRef = interfaceRef;
	}

	public DubboConfigServer getDubboConfigServer() {
		return dubboConfigServer;
	}

	public void setDubboConfigServer(DubboConfigServer dubboConfigServer) {
		this.dubboConfigServer = dubboConfigServer;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getProtocols() {
		return protocols;
	}

	public void setProtocols(String protocols) {
		this.protocols = protocols;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	@Override
	public String toString() {
		return "DubboService [interfaceName=" + interfaceName + ", interfaceRef=" + interfaceRef + ", version=" + version + ", owner=" + owner + ", protocols="
				+ protocols + ", timeout=" + timeout + ", dubboConfigServer=" + dubboConfigServer + "]";
	}

}
