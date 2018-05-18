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
import java.util.concurrent.ConcurrentMap;

import javax.xml.ws.Endpoint;
import javax.xml.ws.handler.Handler;

import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.ext.logging.LoggingInInterceptor;
import org.apache.cxf.ext.logging.LoggingOutInterceptor;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.metrics.MetricsFeature;
import org.apache.cxf.spring.boot.jaxws.annotation.JaxwsEndpoint;
import org.apache.cxf.spring.boot.jaxws.endpoint.EndpointCallback;
import org.apache.cxf.validation.BeanValidationFeature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ObjectUtils;

/**
 * TODO
 * 
 * @author ： <a href="https://github.com/vindell">vindell</a>
 */
@SuppressWarnings("rawtypes")
public class DefaultEndpointCallback implements EndpointCallback {

	private ConcurrentMap<String, Feature> features = null;
	private ConcurrentMap<String, Handler> handlers = null;
	private ConcurrentMap<String, Interceptor> interceptors = null;
	private LoggingFeature loggingFeature;
	private MetricsFeature metricsFeature;
	private BeanValidationFeature validationFeature;

	/**
	 * TODO
	 * 
	 * @author : <a href="https://github.com/vindell">vindell</a>
	 * @param features
	 * @param handlers
	 * @param interceptors
	 */
	public DefaultEndpointCallback(ConcurrentMap<String, Feature> features, ConcurrentMap<String, Handler> handlers,
			ConcurrentMap<String, Interceptor> interceptors, LoggingFeature loggingFeature,
			MetricsFeature metricsFeature, BeanValidationFeature validationFeature) {
		this.features = features;
		this.handlers = handlers;
		this.interceptors = interceptors;
		this.loggingFeature = loggingFeature;
		this.metricsFeature = metricsFeature;
		this.validationFeature = validationFeature;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Endpoint doCallback(Object implementor, EndpointImpl endpoint) {

		// 查找该实现上的自定义注解
		JaxwsEndpoint annotationType = AnnotationUtils.findAnnotation(implementor.getClass(), JaxwsEndpoint.class);
		if (annotationType != null) {
			// 数据上行拦截器
			for (String name : annotationType.inInterceptors()) {
				Interceptor interceptor = interceptors.get(name);
				if (!ObjectUtils.isEmpty(interceptor)) {
					endpoint.getInInterceptors().add(interceptor);
				}
			}
			// 数据下行拦截器
			for (String name : annotationType.outInterceptors()) {
				Interceptor interceptor = interceptors.get(name);
				if (!ObjectUtils.isEmpty(interceptor)) {
					endpoint.getOutInterceptors().add(interceptor);
				}
			}
			// 数据上行Fault拦截器
			for (String name : annotationType.inFaults()) {
				Interceptor interceptor = interceptors.get(name);
				if (!ObjectUtils.isEmpty(interceptor)) {
					endpoint.getInFaultInterceptors().add(interceptor);
				}
			}
			// 数据下行Fault拦截器
			for (String name : annotationType.outFaults()) {
				Interceptor interceptor = interceptors.get(name);
				if (!ObjectUtils.isEmpty(interceptor)) {
					endpoint.getOutFaultInterceptors().add(interceptor);
				}
			}

			// Feature
			for (String name : annotationType.features()) {
				Feature feature = features.get(name);
				if (!ObjectUtils.isEmpty(feature)) {
					endpoint.getFeatures().add(feature);
				}
			}

			// Handler
			for (String name : annotationType.features()) {
				Handler handler = handlers.get(name);
				if (!ObjectUtils.isEmpty(handler)) {
					endpoint.getHandlers().add(handler);
				}
			}
		} else {

			endpoint.getInInterceptors().add(new LoggingInInterceptor());
			endpoint.getOutInterceptors().add(new LoggingOutInterceptor());
			
			endpoint.getFeatures().addAll(Arrays.asList(metricsFeature, loggingFeature, validationFeature));

		}

		return endpoint;
	}

}
