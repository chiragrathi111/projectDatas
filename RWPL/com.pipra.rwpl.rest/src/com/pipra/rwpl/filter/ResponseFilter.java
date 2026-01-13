package com.pipra.rwpl.filter;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

@Provider
public class ResponseFilter implements ContainerResponseFilter {

	public ResponseFilter() {
	}

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext cres) throws IOException {
		
		MultivaluedMap<String, Object> headers = cres.getHeaders();
		
		headers.add("Access-Control-Allow-Origin", "*");
		headers.add("Access-Control-Allow-Headers", "Origin, Authorization, X-Requested-With, Content-Type, Accept, Token, token,Content-Type, Accept, X-Requested-With, remember-me");
		headers.add("Access-Control-Allow-Credentials", "true");
		headers.add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
		headers.add("Access-Control-Max-Age", "1209600");
//		ServerContext.dispose();
	}
}
