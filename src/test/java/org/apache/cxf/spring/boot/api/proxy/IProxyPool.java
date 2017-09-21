/**
 * <p>Coyright (R) 2014 正方软件股份有限公司。<p>
 */
package org.apache.cxf.spring.boot.api.proxy;


public interface IProxyPool {

	public <T> T getProxy(Object target);
	
}