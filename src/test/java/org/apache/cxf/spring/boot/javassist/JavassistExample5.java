/*
 * Copyright (c) 2018, vindell (https://github.com/vindell).
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
package org.apache.cxf.spring.boot.javassist;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;

public class JavassistExample5 {
	
	public static void main(String[] args) throws Exception {
		JavassistWebserviceGenerator javassistLearn = new JavassistWebserviceGenerator();
		Class<?> webservice = javassistLearn.createDynamicClazz();

		JaxWsServerFactoryBean factoryBean = new JaxWsServerFactoryBean();

		// Web服务的地址
		factoryBean.setAddress("http://localhost:8081/dynamicHello");

		// Web服务对象调用接口
		factoryBean.setServiceClass(webservice);
		Server server = factoryBean.create();
		server.start();
	}
}