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
package org.apache.cxf.spring.boot.jaxws.callback;

import java.util.Arrays;

import javax.xml.ws.Endpoint;

import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.ext.logging.LoggingInInterceptor;
import org.apache.cxf.ext.logging.LoggingOutInterceptor;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.metrics.MetricsFeature;
import org.apache.cxf.spring.boot.jaxws.endpoint.EndpointCallback;
import org.apache.cxf.validation.BeanValidationFeature;

/**
 * TODO
 * 
 * @author ： <a href="https://github.com/vindell">vindell</a>
 */
@SuppressWarnings("rawtypes")
public class DefaultEndpointCallback implements EndpointCallback {

	private LoggingFeature loggingFeature;
	private MetricsFeature metricsFeature;
	private BeanValidationFeature validationFeature;

	public DefaultEndpointCallback( LoggingFeature loggingFeature,
			MetricsFeature metricsFeature, BeanValidationFeature validationFeature) {
		this.loggingFeature = loggingFeature;
		this.metricsFeature = metricsFeature;
		this.validationFeature = validationFeature;
	}

	@Override
	public Endpoint doCallback(Object implementor, EndpointImpl endpoint) {

		endpoint.getInInterceptors().add(new LoggingInInterceptor());
		endpoint.getOutInterceptors().add(new LoggingOutInterceptor());
		
		endpoint.getFeatures().addAll(Arrays.asList(metricsFeature, loggingFeature, validationFeature));

		return endpoint;
	}

}
