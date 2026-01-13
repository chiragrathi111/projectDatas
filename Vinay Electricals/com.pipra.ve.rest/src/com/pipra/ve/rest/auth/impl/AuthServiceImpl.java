package com.pipra.ve.rest.auth.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.adempiere.util.LogAuthFailure;
import org.compiere.model.I_AD_Preference;
import org.compiere.model.MClient;
import org.compiere.model.MOrg;
import org.compiere.model.MPreference;
import org.compiere.model.MRole;
import org.compiere.model.MSession;
import org.compiere.model.MTable;
import org.compiere.model.MUser;
import org.compiere.model.Query;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;
import org.compiere.util.Language;
import org.compiere.util.Login;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.pipra.ve.rest.filter.RequestFilter;
import com.pipra.ve.rest.model.request.LoginCredential;
import com.pipra.ve.rest.model.request.LoginParameters;
import com.pipra.ve.rest.model.request.RefreshParameters;
import com.pipra.ve.rest.model.response.Client;
import com.pipra.ve.rest.model.response.LoginResponse;
import com.pipra.ve.rest.model.response.Organization;
import com.pipra.ve.rest.model.response.Role;
import com.pipra.ve.rest.model.response.Warehouse;
import com.pipra.ve.rest.util.ErrorBuilder;
import com.pipra.ve.rest.util.LoginClaims;
import com.pipra.ve.rest.util.TokenUtils;

/**
 * @author hengsin
 *
 */
public class AuthServiceImpl implements AuthService {

	private static LogAuthFailure logAuthFailure = new LogAuthFailure();

	public static final String ROLE_TYPES_WEBSERVICE = "NULL,WS"; // webservice+null

	private @Context HttpServletRequest request = null;

	public AuthServiceImpl() {
	}

	@Override
	public Response loginApi(LoginCredential credential) {
		LoginResponse response = new LoginResponse();
		Login login = new Login(Env.getCtx());

		KeyNamePair[] clients = login.getClients(credential.getUserName(), credential.getPassword(),
				ROLE_TYPES_WEBSERVICE);
		if (clients == null)
			return Response
					.status(Status.UNAUTHORIZED).entity(new ErrorBuilder().status(Status.UNAUTHORIZED)
							.title("Authenticate error").append("Invalid Login Credentials").build().toString())
					.build();

		List<Client> clientList = new ArrayList<Client>();
		for (KeyNamePair client : clients) {

			Client clientName = new Client();
			KeyNamePair[] roles = login.getRoles(credential.getUserName(), client, ROLE_TYPES_WEBSERVICE);

			if (roles != null) {
				List<Role> roleList = new ArrayList<Role>();
				for (KeyNamePair role : roles) {
					Role roleName = new Role();
					roleName.setRoleId(role.getID());
					roleName.setRole(role.getName());

					KeyNamePair[] orgs = login.getOrgs(new KeyNamePair(role.getKey(), ""));
					if (orgs != null) {
						List<Organization> orgList = new ArrayList<Organization>();
						for (KeyNamePair org : orgs) {
							Organization orgName = new Organization();
							orgName.setOrgId(org.getID());
							orgName.setOrg(org.getName());

							KeyNamePair[] warehouses = login.getWarehouses(new KeyNamePair(org.getKey(), ""));
							if (warehouses != null) {
								List<Warehouse> warehouseList = new ArrayList<Warehouse>();
								for (KeyNamePair warehouse : warehouses) {
									Warehouse wName = new Warehouse();
									wName.setWarehouseId(warehouse.getID());
									wName.setWarehouse(warehouse.getName());
									warehouseList.add(wName);
								}
								orgName.setWarehouseList(warehouseList);
							}
							orgList.add(orgName);
						}
						roleName.setOrgList(orgList);
					}
					roleList.add(roleName);
				}
				clientName.setRoleList(roleList);
			}
			clientName.setClientId(client.getID());
			clientName.setClient(client.getName());
			clientList.add(clientName);
		}
		response.setClientList(clientList);
		return Response.ok(response).build();
	}

	@Override
	public Response authenticate(LoginCredential credential) {
		Login login = new Login(Env.getCtx());
		KeyNamePair[] clients = login.getClients(credential.getUserName(), credential.getPassword(),
				ROLE_TYPES_WEBSERVICE);
		if (clients == null || clients.length == 0) {
			String loginErrMsg = login.getLoginErrMsg();
			return unauthorized(loginErrMsg, credential.getUserName());
		} else {
			JsonArray clientNodes = new JsonArray();
			StringBuilder clientsSB = new StringBuilder();
			for (KeyNamePair client : clients) {
				JsonObject node = new JsonObject();
				node.addProperty("id", client.getKey());
				node.addProperty("name", client.getName());
				clientNodes.add(node);
				if (clientsSB.length() > 0)
					clientsSB.append(",");
				clientsSB.append(client.getKey());
			}
			if (credential.getParameters() != null) {
				LoginParameters parameters = credential.getParameters();
				String userName = credential.getUserName();
				Env.setContext(Env.getCtx(), RequestFilter.LOGIN_NAME, userName);
				return processLoginParameters(parameters, userName, clientsSB.toString());
			} else {
				return Response
						.status(Status.UNAUTHORIZED).entity(new ErrorBuilder().status(Status.UNAUTHORIZED)
								.title("Authenticate error").append("Invalid Login Parameters").build().toString())
						.build();
			}
		}
	}

	/**
	 * @param loginErrMsg
	 * @return
	 */
	private Response unauthorized(String loginErrMsg, String userName) {
		if (Util.isEmpty(loginErrMsg))
			loginErrMsg = Msg.getMsg(Env.getCtx(), "FailedLogin", true);
		String x_Forward_IP = request.getHeader("X-Forwarded-For");
		if (x_Forward_IP == null) {
			x_Forward_IP = request.getRemoteAddr();
		}
		logAuthFailure.log(x_Forward_IP, "/api", userName, loginErrMsg);

		return Response.status(Status.UNAUTHORIZED).entity(new ErrorBuilder().status(Status.UNAUTHORIZED)
				.title("Authenticate error").append(loginErrMsg).build().toString()).build();
	}

	@Override
	public Response changeLoginParameters(LoginParameters parameters) {
		String userName = Env.getContext(Env.getCtx(), RequestFilter.LOGIN_NAME);
		String clients = Env.getContext(Env.getCtx(), RequestFilter.LOGIN_CLIENTS);
		return processLoginParameters(parameters, userName, clients);
	}

	/**
	 * @param parameters
	 * @param userName
	 * @param clients
	 * @return
	 */
	private Response processLoginParameters(LoginParameters parameters, String userName, String clients) {
		JsonObject responseNode = new JsonObject();
		Builder builder = JWT.create().withSubject(userName);
		String defaultLanguage = Language.getBaseAD_Language();
		int clientId = parameters.getClientId();
		boolean isValidClient = isValidClient(clientId, clients);

		if (isValidClient) {
			builder.withClaim(LoginClaims.AD_Client_ID.name(), clientId);
			Env.setContext(Env.getCtx(), Env.AD_CLIENT_ID, clientId);
			MUser user = MUser.get(Env.getCtx(), userName);
			builder.withClaim(LoginClaims.AD_User_ID.name(), user.getAD_User_ID());
			responseNode.addProperty("userId", user.getAD_User_ID());
			defaultLanguage = getPreferenceUserLanguage(user.getAD_User_ID());

			int roleId = parameters.getRoleId();
			int orgId = parameters.getOrganizationId();
			int warehouseId = parameters.getWarehouseId();
			String errorMessage = validateLoginParameters(userName, clientId, roleId, orgId, warehouseId);

			if (Util.isEmpty(errorMessage)) {
				builder.withClaim(LoginClaims.AD_Role_ID.name(), roleId);
				builder.withClaim(LoginClaims.AD_Org_ID.name(), orgId);
				if (orgId > 0 && warehouseId > 0)
					builder.withClaim(LoginClaims.M_Warehouse_ID.name(), warehouseId);
			} else {
				return unauthorized(errorMessage, userName);
			}
		} else {
			return unauthorized("Invalid clientId", userName);
		}
		if (parameters.getLanguage() != null) {
			for (String langAllowed : Env.getLoginLanguages()) {
				if (parameters.getLanguage().equals(langAllowed)) {
					defaultLanguage = parameters.getLanguage();
					break;
				}
			}
		}

		builder.withClaim(LoginClaims.AD_Language.name(), defaultLanguage);
		responseNode.addProperty("language", defaultLanguage);

		// Create AD_Session here and set the session in the token as another parameter
		MSession session = MSession.get(Env.getCtx());
		if (session == null) {
			session = MSession.create(Env.getCtx());
			session.setWebSession("idempiere-rest");
			session.saveEx();
		}
		builder.withClaim(LoginClaims.AD_Session_ID.name(), session.getAD_Session_ID());

		Timestamp expiresAt = TokenUtils.getTokenExpiresAt();
		builder.withIssuer(TokenUtils.getTokenIssuer()).withExpiresAt(expiresAt).withKeyId(TokenUtils.getTokenKeyId());
		try {
			String token = builder.sign(Algorithm.HMAC512(TokenUtils.getTokenSecret()));
			responseNode.addProperty("token", token);
		} catch (IllegalArgumentException | JWTCreationException e) {
			e.printStackTrace();
			return Response.status(Status.BAD_REQUEST).build();
		}
		return Response.ok(responseNode.toString()).build();
	}

	private boolean isValidClient(int clientID, String clients) {
		if (clientID >= 0 && !Util.isEmpty(clients)) {
			for (String allowedClient : clients.split(",")) {
				if (clientID == Integer.valueOf(allowedClient)) {
					return true;
				}
			}
		}
		return false;
	}

	private String validateLoginParameters(String userName, int clientId, int roleId, int orgId, int warehouseId) {
		MClient client = MClient.get(Env.getCtx(), clientId);
		KeyNamePair clientKeyNamePair = new KeyNamePair(client.getAD_Client_ID(), client.getName());
		Login login = new Login(Env.getCtx());
		KeyNamePair[] roles = login.getRoles(userName, clientKeyNamePair, ROLE_TYPES_WEBSERVICE);
		boolean isValidRole = isValidRole(roleId, roles);

		if (isValidRole) {
			boolean isValidOrg = isValidOrg(orgId, roleId, login);
			if (isValidOrg) {
				if (orgId > 0 && warehouseId > 0) {
					boolean warehouseValid = isValidWarehouse(orgId, warehouseId, login);
					if (!warehouseValid)
						return "Invalid warehouseId";
				}
			} else {
				return "Invalid organizationId";
			}
		} else {
			return "Invalid roleId";
		}

		return login.validateLogin(new KeyNamePair(orgId, ""));
	}

	private boolean isValidRole(int roleId, KeyNamePair[] roles) {
		if (roleId >= 0 && roles != null) {
			for (KeyNamePair roleAllowed : roles) {
				if (roleId == roleAllowed.getKey()) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isValidOrg(int orgId, int roleId, Login login) {
		if (orgId >= 0) {
			String userName = Env.getContext(Env.getCtx(), RequestFilter.LOGIN_NAME);
			MUser user = MUser.get(Env.getCtx(), userName);
			Env.setContext(Env.getCtx(), Env.AD_USER_ID, user.getAD_User_ID());
			MRole role = MRole.get(Env.getCtx(), roleId);
			KeyNamePair rolesKeyNamePair = new KeyNamePair(role.getAD_Role_ID(), role.getName());
			KeyNamePair[] orgs = login.getOrgs(rolesKeyNamePair);
			if (orgs != null) {
				for (KeyNamePair orgAllowed : orgs) {
					if (orgId == orgAllowed.getKey()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean isValidWarehouse(int orgId, int warehouseId, Login login) {
		MOrg org = MOrg.get(orgId);
		KeyNamePair orgKeyNamePair = new KeyNamePair(org.getAD_Org_ID(), org.getName());
		KeyNamePair[] warehouses = login.getWarehouses(orgKeyNamePair);
		if (warehouses != null) {
			for (KeyNamePair allowedWarehouse : warehouses) {
				if (warehouseId == allowedWarehouse.getKey()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns the user preference language if non exist - returns the client
	 * language
	 */
	private String getPreferenceUserLanguage(int AD_User_ID) {
		Query query = new Query(Env.getCtx(), MTable.get(Env.getCtx(), I_AD_Preference.Table_ID),
				" Attribute=? AND AD_User_ID=? AND PreferenceFor = 'W'", null);

		MPreference preference = query.setOnlyActiveRecords(true).setParameters("Language", AD_User_ID).first();

		return preference != null ? Language.getAD_Language(preference.getValue())
				: MClient.get(Env.getCtx()).getAD_Language();
	}

	/**
	 * Refresh a token, returns a new auth token and a new refresh token
	 */
	@Override
	public Response tokenRefresh(RefreshParameters refresh) {
		String refreshToken = refresh.getRefresh_token();
		Algorithm algorithm = Algorithm.HMAC512(TokenUtils.getTokenSecret());
		JWTVerifier verifier = JWT.require(algorithm).withIssuer(TokenUtils.getTokenIssuer()).build(); 
		
		// Verify the refresh token (expiration, signature)
		DecodedJWT jwt;
		try {
			jwt = verifier.verify(refreshToken);
		} catch (JWTVerificationException e) {
			return Response.status(Status.UNAUTHORIZED).entity(new ErrorBuilder().status(Status.UNAUTHORIZED)
					.title("Authenticate error").append(e.getLocalizedMessage()).build().toString()).build();
		}

		String userName = jwt.getSubject();

		Claim claim = jwt.getClaim(LoginClaims.AD_Client_ID.name());
		int clientId = -1;
		if (!claim.isNull() && !claim.isMissing())
			clientId = claim.asInt();

		claim = jwt.getClaim(LoginClaims.AD_User_ID.name());
		int userId = -1;
		if (!claim.isNull() && !claim.isMissing())
			userId = claim.asInt();

		claim = jwt.getClaim(LoginClaims.AD_Role_ID.name());
		int roleId = -1;
		if (!claim.isNull() && !claim.isMissing())
			roleId = claim.asInt();

		claim = jwt.getClaim(LoginClaims.AD_Org_ID.name());
		int orgId = -1;
		if (!claim.isNull() && !claim.isMissing())
			orgId = claim.asInt();

		claim = jwt.getClaim(LoginClaims.M_Warehouse_ID.name());
		int warehouseId = -1;
		if (!claim.isNull() && !claim.isMissing())
			warehouseId = claim.asInt();

		claim = jwt.getClaim(LoginClaims.AD_Language.name());
		String defaultLanguage = null;
		if (!claim.isNull() && !claim.isMissing())
			defaultLanguage = claim.asString();

		claim = jwt.getClaim(LoginClaims.AD_Session_ID.name());
		int sessionId = -1;
		if (!claim.isNull() && !claim.isMissing())
			sessionId = claim.asInt();

		JsonObject responseNode = new JsonObject();
		Builder builder = JWT.create().withSubject(userName);
		builder.withClaim(LoginClaims.AD_Client_ID.name(), clientId);
		Env.setContext(Env.getCtx(), Env.AD_CLIENT_ID, clientId);
		builder.withClaim(LoginClaims.AD_User_ID.name(), userId);
		builder.withClaim(LoginClaims.AD_Role_ID.name(), roleId);
		builder.withClaim(LoginClaims.AD_Org_ID.name(), orgId);
		if (orgId > 0 && warehouseId > 0)
			builder.withClaim(LoginClaims.M_Warehouse_ID.name(), warehouseId);
		builder.withClaim(LoginClaims.AD_Language.name(), defaultLanguage);
		builder.withClaim(LoginClaims.AD_Session_ID.name(), sessionId);

		Timestamp expiresAt = TokenUtils.getTokenExpiresAt();
		builder.withIssuer(TokenUtils.getTokenIssuer()).withExpiresAt(expiresAt).withKeyId(TokenUtils.getTokenKeyId());
		try {
			String token = builder.sign(Algorithm.HMAC512(TokenUtils.getTokenSecret()));
			responseNode.addProperty("token", token);
		} catch (IllegalArgumentException | JWTCreationException e) {
			e.printStackTrace();
			return Response.status(Status.BAD_REQUEST).build();
		}
		return Response.ok(responseNode.toString()).build();
	}
}
