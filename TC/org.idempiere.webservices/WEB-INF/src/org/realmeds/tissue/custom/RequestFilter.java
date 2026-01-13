package org.realmeds.tissue.custom;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.adempiere.util.ServerContext;
import org.compiere.model.MSysConfig;
import org.compiere.util.Env;
import org.compiere.util.Util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

@Provider
public class RequestFilter implements ContainerRequestFilter {
//    private static final Response.Status UNAUTHORIZED_STATUS = Response.Status.UNAUTHORIZED;

	public static final String LOGIN_NAME = "#LoginName";
	public static final String LOGIN_CLIENTS = "#LoginClients";
	private static String webServiceName = new String("RealMedsTissueWebservice");

	private static final Pattern[] MALICIOUS_PATTERNS = {
		Pattern.compile("<script[^>]*>.*?</script>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
		Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
		Pattern.compile("on\\w+\\s*=\\s*['\"].*?['\"]?", Pattern.CASE_INSENSITIVE),
		Pattern.compile("&#\\d+;|&#x[0-9a-f]+;", Pattern.CASE_INSENSITIVE),
		Pattern.compile("&[a-z]+;", Pattern.CASE_INSENSITIVE),
		Pattern.compile("prompt\\s*\\(", Pattern.CASE_INSENSITIVE),
		Pattern.compile("alert\\s*\\(", Pattern.CASE_INSENSITIVE),
		Pattern.compile("eval\\s*\\(", Pattern.CASE_INSENSITIVE),
		Pattern.compile("(union|select|insert|update|delete|drop|create|alter|exec|execute)\\s+(.*\\s+)?(from|into|table|database)", Pattern.CASE_INSENSITIVE)
	};

	public RequestFilter() {
	}
	
	private static final List<String> openApi = List.of(
            "gettracedataforqr",
            "generatetokenandsendmailnew",
            "updateuserpassword",
            "gettokenvalidation"
    );
	
    private static final List<String> APP_SIGNATURES = List.of(
            "8e534997a9759ee363a2ed1cbe0e0ca19f6837bfe541332fef090f906d049f66",
            "e7bc3f9c60808844015148e32ec7bcdf60b943ed764c382937dc5d2dbc7e4bbd"
    );

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		Properties ctx = new Properties();
		ServerContext.setCurrentInstance(ctx);
		
		// Handle CORS
		String origin = requestContext.getHeaderString("Origin");
		if (origin != null) {
			Set<String> allowedOrigins = getAllowedOrigins();
			if (!allowedOrigins.contains(origin)) {
				requestContext.abortWith(Response.status(Response.Status.FORBIDDEN)
						.header("Access-Control-Allow-Origin", String.join(",", allowedOrigins))
						.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
						.header("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With, appSignature, serviceType")
						.entity("{\"error\":\"Origin not allowed\"}")
						.type("application/json")
						.build());
				return;
			}
		}
		
		// Sanitize request body for POST/PUT requests
		if ((HttpMethod.POST.equals(requestContext.getMethod()) || HttpMethod.PUT.equals(requestContext.getMethod())) 
				&& requestContext.hasEntity()) {
			String contentType = requestContext.getHeaderString("Content-Type");
			if (contentType != null && !contentType.toLowerCase().startsWith("multipart/")) {

				String requestBody = getRequestBody(requestContext);
				if (containsMaliciousContent(requestBody)) {
					String errorResponse = "{\"statusCode\":400,\"error\":\"Invalid input detected. Request contains potentially malicious content.\"}";
					requestContext.abortWith(Response.status(Response.Status.BAD_REQUEST).entity(errorResponse)
							.type("application/json").build());
					return;
				}
			}
		}
		
		String fullPath = requestContext.getUriInfo().getPath();
		String path = fullPath.substring(fullPath.lastIndexOf('/') + 1);

        String appSignature = requestContext.getHeaderString("appSignature");
        
        if (openApi.contains(path)) {
            return;
        }

        if (appSignature == null || !APP_SIGNATURES.contains(appSignature)) {
            requestContext.abortWith(Response.status(Response.Status.NOT_ACCEPTABLE).build());
        }
        
		if (HttpMethod.OPTIONS.equals(requestContext.getMethod())
				|| (HttpMethod.POST.equals(requestContext.getMethod())
						&& requestContext.getUriInfo().getPath().endsWith("gettracedataforqr"))
				|| (HttpMethod.POST.equals(requestContext.getMethod())
						&& requestContext.getUriInfo().getPath().endsWith("token/refresh"))
				|| (HttpMethod.POST.equals(requestContext.getMethod())
						&& requestContext.getUriInfo().getPath().endsWith("login_api"))
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

		String err = TCUtills.authenticate(webServiceName, serviceType, Env.getCtx());
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
	
	private String getRequestBody(ContainerRequestContext requestContext) throws IOException {
		InputStream inputStream = requestContext.getEntityStream();
		String body = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
				.lines().collect(Collectors.joining("\n"));
		// Reset the input stream for further processing
		requestContext.setEntityStream(new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8)));
		return body;
	}
	
	private boolean containsMaliciousContent(String input) {
		if (input == null || input.isEmpty()) {
			return false;
		}
		
		for (Pattern pattern : MALICIOUS_PATTERNS) {
			if (pattern.matcher(input).find()) {
				return true;
			}
		}
		return false;
	}
	
	private Set<String> getAllowedOrigins() {
		Set<String> origins = new HashSet<>();
		
		String configOrigins = MSysConfig.getValue("CORS_ALLOWED_ORIGINS", "", 0);
		if (!configOrigins.isEmpty()) {
			origins.addAll(Arrays.asList(configOrigins.split(",")));
		}
		
		return origins;
	}

}
