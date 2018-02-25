/*
 * Copyright (c) 2010-2020, vindell (https://github.com/vindell).
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
package org.apache.cxf.spring.boot.api;

import java.lang.reflect.InvocationHandler;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import org.apache.cxf.spring.boot.jaxws.JaxwsApi;

@WebService(serviceName = "sample", // 与接口中指定的name一致
		targetNamespace = "http://ws.cxf.com/"// , // 与接口中的命名空间一致,一般是接口的包名倒
// endpointInterface = "org.apache.cxf.spring.boot.api.JaxwsSample"// 接口地址
)
public class JaxwsApiSample extends JaxwsApi {

	public JaxwsApiSample() {
	}

	public JaxwsApiSample(InvocationHandler handler) {
		super(handler);
	}

	@WebMethod
	@WebResult(name = "String", targetNamespace = "")
	public String sayHello(@WebParam(name = "userName") String name) {
		
		//getHandler().invoke(this, method, args);
		//Method method = this.getClass().getDeclaredMethod(name, $sig);
		//getHandler().invoke($0, method, $args);
		
		return "Hello ," + name;
	}

	@WebMethod
	@WebResult(name = "String", targetNamespace = "")
	public String sayHello2(@WebParam(name = "userName") String name) {
		return "Hello ," + name;
	}
	
	@WebMethod
	@WebResult(name = "String", targetNamespace = "")
	public String invoke(@WebParam(name = "userName") String name) {
		
		//this.getHandler().invoke(this, method, args)
		
		return "Hello ," + name;
	}
	
}