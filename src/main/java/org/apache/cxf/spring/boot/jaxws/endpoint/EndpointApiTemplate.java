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
package org.apache.cxf.spring.boot.jaxws.endpoint;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.xml.ws.Endpoint;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.ServerImpl;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.metrics.MetricsFeature;
import org.apache.cxf.spring.boot.jaxws.MediatorInInterceptor;
import org.apache.cxf.spring.boot.jaxws.callback.DefaultEndpointCallback;
import org.apache.cxf.validation.BeanValidationFeature;

/**
 * TODO
 * @author ： <a href="https://github.com/vindell">vindell</a>
 */
public class EndpointApiTemplate {

	private ConcurrentMap<String, Endpoint> endpoints = new ConcurrentHashMap<String, Endpoint>();
	private Bus bus;
	private EndpointCallback callback;
	private LoggingFeature loggingFeature;
	private MetricsFeature metricsFeature;
	private BeanValidationFeature validationFeature;

	public EndpointApiTemplate(Bus bus) {
		this.bus = bus;
		this.callback = new DefaultEndpointCallback(loggingFeature, metricsFeature, validationFeature);
	}

	/**
	 * 为指定的addr发布Endpoint
	 * @author ： <a href="https://github.com/vindell">vindell</a>
	 * @param addr
	 * @param api
	 * @return
	 */
	public Endpoint publish(String addr, Object implementor) {
		return this.publish(addr, implementor, callback);
	}

	/**
	 * 为指定的addr发布Endpoint
	 * @author 		： <a href="https://github.com/vindell">vindell</a>
	 * @param addr
	 * @param implementor
	 * @param callback
	 * @return
	 */
	public Endpoint publish(String addr, Object implementor, EndpointCallback callback) {

		EndpointImpl endpoint = new EndpointImpl(bus, implementor);
		
		callback.doCallback(implementor, endpoint);

		endpoint.getInInterceptors().add(new MediatorInInterceptor());
		// 接口发布在 addr 目录下
		endpoint.publish(addr);
		
		endpoints.put(addr, endpoint);

		return endpoint;
	}

	/**
	 * 销毁指定路径匹配的Endpoint
	 * 
	 * @author ： <a href="https://github.com/vindell">vindell</a>
	 * @param addr
	 */
	public void destroy(String addr) {
		EndpointImpl endpoint = (EndpointImpl) endpoints.get(addr);
		if (endpoint != null) {
			ServerImpl server = endpoint.getServer(addr);
			server.destroy();
		}
	}

	public Bus getBus() {
		return bus;
	}

	public void setBus(Bus bus) {
		this.bus = bus;
	}

	public ConcurrentMap<String, Endpoint> getEndpoints() {
		return endpoints;
	}
	
	public void setEndpoints(Map<String, Endpoint> endpoints) {
		this.endpoints.putAll(endpoints);
	}

	public LoggingFeature getLoggingFeature() {
		return loggingFeature;
	}

	public void setLoggingFeature(LoggingFeature loggingFeature) {
		this.loggingFeature = loggingFeature;
	}

	public MetricsFeature getMetricsFeature() {
		return metricsFeature;
	}

	public void setMetricsFeature(MetricsFeature metricsFeature) {
		this.metricsFeature = metricsFeature;
	}

	public BeanValidationFeature getValidationFeature() {
		return validationFeature;
	}

	public void setValidationFeature(BeanValidationFeature validationFeature) {
		this.validationFeature = validationFeature;
	}
	
}
