package org.apache.cxf.spring.boot.endpoint;

import org.apache.cxf.spring.boot.repository.APIEndpoint;

public interface APIEndpointHandler {

	public boolean preHandle(APIEndpoint endpoint);
	
	public byte[] postHandle(APIEndpoint endpoint);
	
	public void afterCompletion(APIEndpoint endpoint, Exception exception) throws Exception;
	
}
