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
package org.apache.cxf.spring.boot.jaxws.feature;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;

public class EndpointPauseInterceptor extends AbstractSoapInterceptor {
	
	private String cause = null;
	
	public EndpointPauseInterceptor(String cause) {
		// 一定要指定这个拦截器放在拦截器链的哪个阶段，这里放在最开始阶段。
		super(Phase.RECEIVE);
		this.cause = cause;
	}

	@Override
	public void handleMessage(SoapMessage message) throws Fault {
		throw new RuntimeException("Service Suspending : " + cause);  
	}
	
}