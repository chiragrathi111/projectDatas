package com.pipra.ve.rest.util;

import java.sql.Timestamp;

import org.compiere.model.MSysConfig;
import org.compiere.util.Env;
import org.compiere.util.TimeUtil;

/**
 * 
 * @author Mahendhar Reddy
 *
 */
public class TokenUtils {

	public static final String REST_TOKEN_SECRET = "REST_TOKEN_SECRET";
	public static final String REST_TOKEN_KEY_ID = "REST_TOKEN_KEY_ID";
	public static final String REST_TOKEN_Issuer = "REST_TOKEN_ISSUER";
	public static final String REST_TOKEN_EXPIRE_IN_MINUTES = "REST_TOKEN_EXPIRE_IN_MINUTES";

	private TokenUtils() {
	}

	/**
	 * 
	 * @return token secret
	 */
	public static String getTokenSecret() {
		String secret = MSysConfig.getValue(REST_TOKEN_SECRET);
		if(secret == null)
			secret = "Vinay";
		return secret;
	}

	/**
	 *
	 * @return token key id
	 */
	public static String getTokenKeyId() {
		String secret = MSysConfig.getValue(REST_TOKEN_KEY_ID);
		;
		return secret;
	}

	/**
	 * 
	 * @return issuer of token
	 */
	public static String getTokenIssuer() {
		String secret = MSysConfig.getValue(REST_TOKEN_Issuer);
		;
		return secret;
	}

	/**
	 * 
	 * @return token expire time stamp
	 */
	public static Timestamp getTokenExpiresAt() {
		Timestamp expiresAt = new Timestamp(System.currentTimeMillis());
		int expMinutes = MSysConfig.getIntValue(REST_TOKEN_EXPIRE_IN_MINUTES, 1440, Env.getAD_Client_ID(Env.getCtx()));
		expiresAt = TimeUtil.addMinutess(expiresAt, expMinutes);
		return expiresAt;
	}

}
