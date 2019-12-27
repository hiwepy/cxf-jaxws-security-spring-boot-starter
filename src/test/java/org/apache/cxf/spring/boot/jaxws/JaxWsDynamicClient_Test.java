/*
 * Copyright (c) 2018, hiwepy (https://github.com/hiwepy).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.cxf.spring.boot.jaxws;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.cxf.spring.boot.jaxws.security.ClientLoginInterceptor;
import org.junit.Test;

public class JaxWsDynamicClient_Test {

	/**
	 * 动态调用方式
	 */
	@Test
	public void testDynamicClient() {
		
		// 创建动态客户端
		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
		Client client = dcf.createClient("http://localhost:8081/services/stu/info/v1?wsdl");
		// 需要密码的情况需要加上用户名和密码
		// client.getOutInterceptors().add(new ClientLoginInterceptor(USER_NAME,
		// PASS_WORD));
		client.getOutInterceptors().add(new ClientLoginInterceptor("admin", "pass"));
		
		Object[] objects = new Object[0];
		try {
			// invoke("方法名",参数1,参数2,参数3....);
			objects = client.invoke("sayHello", "Leftso");
			System.out.println("返回数据:" + objects[0]);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
	}

}
