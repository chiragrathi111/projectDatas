package org.realmeds.tissue.custom;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

	@Override
	public Response toResponse(Throwable exception) {
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				.entity("{\"statusCode\":500,\"error\":\"Internal Server Error\"}")
				.type("application/json")
				.build();
	}
}
