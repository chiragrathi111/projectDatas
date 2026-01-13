package com.pipra.rwpl.filter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.Properties;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.adempiere.util.ServerContext;
import org.compiere.util.Env;
import org.compiere.util.Util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.pipra.rwpl.util.LoginClaims;
import com.pipra.rwpl.util.RwplUtils;
import com.pipra.rwpl.util.TokenUtils;

@Provider
public class RequestFilter implements ContainerRequestFilter {
	public static final String LOGIN_NAME = "#LoginName";
	public static final String LOGIN_CLIENTS = "#LoginClients";
	private static String webServiceName = new String("RwplWebservice");

	public RequestFilter() {
	}

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		Properties ctx = new Properties();
		ServerContext.setCurrentInstance(ctx);

		if (HttpMethod.OPTIONS.equals(requestContext.getMethod())
				|| (HttpMethod.POST.equals(requestContext.getMethod())
						&& requestContext.getUriInfo().getPath().endsWith("auth/authenticate"))
				|| (HttpMethod.POST.equals(requestContext.getMethod())
						&& requestContext.getUriInfo().getPath().endsWith("auth/refresh"))
				|| (HttpMethod.POST.equals(requestContext.getMethod())
						&& requestContext.getUriInfo().getPath().endsWith("auth/login"))
				|| (HttpMethod.GET.equals(requestContext.getMethod())
						&& requestContext.getUriInfo().getPath().endsWith("auth/forgot/password"))
				|| (HttpMethod.GET.equals(requestContext.getMethod())
						&& requestContext.getUriInfo().getPath().endsWith("auth/update/password"))) {
			return;
		}
		String authHeaderVal = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

		// consume JWT i.e. execute signature validation
		if (authHeaderVal != null && authHeaderVal.startsWith("Bearer")) {
			try {
				validate(authHeaderVal.split(" ")[1], requestContext);
				if (Util.isEmpty(Env.getContext(Env.getCtx(), Env.AD_USER_ID))
						|| Util.isEmpty(Env.getContext(Env.getCtx(), Env.AD_ROLE_ID))) {
					if (!requestContext.getUriInfo().getPath().startsWith("v1/auth/")) {
						requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
					}
				}
			} catch (JWTVerificationException ex) {
				requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
			} catch (Exception ex) {
				requestContext.abortWith(Response.status(Response.Status.INTERNAL_SERVER_ERROR).build());
			}
		} else {
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
		}

		String serviceType = requestContext.getHeaderString("serviceType");
		if (serviceType == null)
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());

		String err = RwplUtils.authenticate(webServiceName, serviceType, Env.getCtx());
		if (err != null && err.length() > 0) {
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
		}

	}

	private void validate(String token, ContainerRequestContext requestContext)
			throws IllegalArgumentException, UnsupportedEncodingException {

		Algorithm algorithm = Algorithm.HMAC512(TokenUtils.getTokenSecret());
		JWTVerifier verifier = JWT.require(algorithm).withIssuer(TokenUtils.getTokenIssuer()).build(); // Reusable
																										// verifier
																										// instance
		DecodedJWT jwt = verifier.verify(token);

		String userName = jwt.getSubject();
		ServerContext.setCurrentInstance(new Properties());
		Env.setContext(Env.getCtx(), LOGIN_NAME, userName);
		Claim claim = jwt.getClaim(LoginClaims.Clients.name());
		if (!claim.isNull() && !claim.isMissing()) {
			String clients = claim.asString();
			Env.setContext(Env.getCtx(), LOGIN_CLIENTS, clients);
		}
		claim = jwt.getClaim(LoginClaims.AD_Client_ID.name());
		int AD_Client_ID = 0;
		if (!claim.isNull() && !claim.isMissing()) {
			AD_Client_ID = claim.asInt();
			Env.setContext(Env.getCtx(), Env.AD_CLIENT_ID, AD_Client_ID);
		}
		claim = jwt.getClaim(LoginClaims.AD_User_ID.name());
		if (!claim.isNull() && !claim.isMissing()) {
			Env.setContext(Env.getCtx(), Env.AD_USER_ID, claim.asInt());
		}
		claim = jwt.getClaim(LoginClaims.AD_Role_ID.name());
		int AD_Role_ID = 0;
		if (!claim.isNull() && !claim.isMissing()) {
			AD_Role_ID = claim.asInt();
			Env.setContext(Env.getCtx(), Env.AD_ROLE_ID, AD_Role_ID);
		}
		claim = jwt.getClaim(LoginClaims.AD_Org_ID.name());
		int AD_Org_ID = 0;
		if (!claim.isNull() && !claim.isMissing()) {
			AD_Org_ID = claim.asInt();
			Env.setContext(Env.getCtx(), Env.AD_ORG_ID, AD_Org_ID);
		}
		claim = jwt.getClaim(LoginClaims.M_Warehouse_ID.name());
		if (!claim.isNull() && !claim.isMissing()) {
			Env.setContext(Env.getCtx(), Env.M_WAREHOUSE_ID, claim.asInt());
		}
		claim = jwt.getClaim(LoginClaims.AD_Language.name());
		if (!claim.isNull() && !claim.isMissing()) {
			String AD_Language = claim.asString();
			Env.setContext(Env.getCtx(), Env.LANGUAGE, AD_Language);
		}
		claim = jwt.getClaim(LoginClaims.AD_Session_ID.name());

		Env.setContext(Env.getCtx(), "#Date", new Timestamp(System.currentTimeMillis()));
	}

}
