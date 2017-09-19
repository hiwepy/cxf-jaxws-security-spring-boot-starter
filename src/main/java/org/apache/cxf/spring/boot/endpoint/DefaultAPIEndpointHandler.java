package org.apache.cxf.spring.boot.endpoint;

import org.apache.cxf.spring.boot.repository.APIEndpoint;

public class DefaultAPIEndpointHandler implements APIEndpointHandler {

	@Override
	public boolean preHandle(APIEndpoint endpoint) {
		return true;
	}

	@Override
	public byte[] postHandle(APIEndpoint endpoint) {
		return endpoint.getBytes();
	}

	@Override
	public void afterCompletion(APIEndpoint endpoint, Exception exception) throws Exception {
		
	}

}
