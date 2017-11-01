package org.apache.cxf.spring.boot.api.proxy;


public interface IProxyPool {

	public <T> T getProxy(Object target);
	
}