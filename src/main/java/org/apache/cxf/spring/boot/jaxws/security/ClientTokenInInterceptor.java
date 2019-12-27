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
package org.apache.cxf.spring.boot.jaxws.security;

import java.util.List;
import java.util.Map;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;

public class ClientTokenInInterceptor extends AbstractPhaseInterceptor<SoapMessage> {

	/**
	 * Instantiates the interceptor with a specified id or with a system determined
	 * unique id. The specified id will be used unless <code>uniqueId</code> is set
	 * to true.
	 * @param i the interceptor's id
	 * @param p the interceptor's phase
	 * @param uniqueId
	 */
	public ClientTokenInInterceptor(String i, String p, boolean uniqueId) {
		super(i, p, uniqueId);
	}

	@Override
	public void handleMessage(SoapMessage message) {

		Message request = message.getExchange().getInMessage();
		Map<String, List<String>> headers = CastUtils.cast((Map<?, ?>) request.get(Message.PROTOCOL_HEADERS)); // 获取header参数

		if (headers.get("token") == null || headers.get("token").size() == 0) {
			// logger.error("没有token参数");
			message.getInterceptorChain().abort();
			return;
		}
		
		String token = headers.get("token").get(0);

		if (token == null || token.length() == 0) {
			// logger.error("没有token值");
			message.getInterceptorChain().abort(); // token没有，阻止拦截链执行下去
			return;
		}
	}

}
