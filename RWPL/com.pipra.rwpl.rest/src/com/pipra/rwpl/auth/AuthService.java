package com.pipra.rwpl.auth;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.pipra.rwpl.mode.request.LoginCredential;
import com.pipra.rwpl.mode.request.RefreshParameters;

@Path("auth")
public interface AuthService {

	@POST
	@Path("login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response loginApi(LoginCredential credential);
	
	@POST
	@Path("authenticate")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response authenticate(LoginCredential credential);
	
	@Path("refresh")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response tokenRefresh(RefreshParameters refresh);

}
