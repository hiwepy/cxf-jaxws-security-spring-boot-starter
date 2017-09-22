package org.apache.cxf.spring.boot.endpoint;

import java.util.List;

public interface APIEndpointRepository {

	List<APIEndpoint> getEndpoints();
	
}
