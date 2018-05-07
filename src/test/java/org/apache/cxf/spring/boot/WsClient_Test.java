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
package org.apache.cxf.spring.boot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.xml.namespace.QName;

import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.handler.WSHandlerConstants;

public class WsClient_Test {

	public static void main(String[] args) {
		
		// 这个是用cxf 客户端访问cxf部署的webservice服务
		// 千万记住，访问cxf的webservice必须加上namespace ,否则通不过
		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();

		org.apache.cxf.endpoint.Client client = dcf
				.createClient("http://192.168.4.35:8080/monitor/ws/wsMailTrack?wsdl");
		// url为调用webService的wsdl地址
		QName name = new QName("http://webservice.business.monitor.com/", "batchAddMailTracks");
		Map<String, Object> props = new HashMap<String, Object>();
		props.put(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
		// 密码类型 明文:PasswordText密文：PasswordDigest
		props.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
		// 用户名
		props.put(WSHandlerConstants.USER, "huwei");
		// 将PasswordHandler 的类名传递给服务器，相当于传递了密码给服务器
		props.put(WSHandlerConstants.PW_CALLBACK_CLASS, PasswordHandler.class.getName());
		WSS4JOutInterceptor wssOut = new WSS4JOutInterceptor(props);
		client.getOutInterceptors().add(wssOut);
		// paramvalue为参数值
		Object[] objects;
		try {
			String mailTrackId = UUID.randomUUID().toString().replace("-", "");
			objects = client.invoke(name, mailTrackId, "123@qq.com", "2016-09-25", "我要发邮件", "huwei@ftsafe.com",
					"love you hi");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
