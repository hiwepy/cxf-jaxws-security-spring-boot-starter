package org.apache.cxf.spring.boot.javassist.proxy;

import org.apache.cxf.spring.boot.javassist.JavassistWebserviceGenerator;
import org.apache.cxf.spring.boot.jaxws.proxy.JavassistProxy;
import org.junit.Test;

/**
 * https://www.cnblogs.com/coshaho/p/5105545.html
 */
public class JavassistProxyExample3 {

	@Test
	public void testDynamicInterface() throws Exception {
		
		JavassistWebserviceGenerator javassistLearn = new JavassistWebserviceGenerator();
		
		Class<?> webservice = javassistLearn.createDynamicInterface();

		// Javassist Proxy
		Object obj = JavassistProxy.getProxy(webservice);
				
		obj.toString();
		
	}
 
}