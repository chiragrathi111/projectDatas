package com.pipra.ve.rest.auth.impl;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.pipra.ve.rest.model.request.LoginCredential;
import com.pipra.ve.rest.model.request.LoginParameters;
import com.pipra.ve.rest.model.request.RefreshParameters;

@Path("auth")
/**
 * authentication service
 * 
 * @author Mahendhar Reddy
 *
 */
public interface AuthService {

	@POST
	@Path("login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response loginApi(LoginCredential credential);

	
	@Path("authenticate")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	/**
	 * Get auth token with username and password
	 * 
	 * @return new auth token and list of clients available
	 */
	public Response authenticate(LoginCredential credential);

	@Path("changeLoginParameters")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	/**
	 * Set/Modify login role, organization and warehouse
	 * 
	 * @param token     from /users/login
	 * @param loginRole
	 * @return new auth token
	 */
	public Response changeLoginParameters(LoginParameters loginRole);

	@Path("refresh")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	/**
	 * Get auth token with a refresh token
	 * 
	 * @return new auth token
	 */
	public Response tokenRefresh(RefreshParameters refresh);

}
