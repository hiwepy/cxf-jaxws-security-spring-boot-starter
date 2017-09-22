package org.apache.cxf.spring.boot.jaxrs.security;


import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;

/**
 * This is a simple optional CORS filter used for this demo to make the resources accessible
 * from other origins. You may omit using this filter or use CXF's advanced CORS filter
 * org.apache.cxf.rs.security.cors.CrossOriginResourceSharingFilter
 * included in cxf-rt-rs-security-cors if you need a more comprehensive accessibility rules.
 */
public class ApiOriginFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
        throws IOException {
        MultivaluedMap<String, Object> headers = responseContext.getHeaders();
        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
        headers.add("Access-Control-Allow-Headers", "Content-Type");
    }
}