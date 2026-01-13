package com.pipra.rwpl.entity.model;

import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

import org.compiere.model.PO;
import org.compiere.model.Query;

import com.pipra.rwpl.entity.X_pi_userToken;

public class PiUserToken extends X_pi_userToken {

	private static final long serialVersionUID = 1L;

	public PiUserToken(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	public PiUserToken(Properties ctx, int piUserTokenId, String trxName) {
		this(ctx, piUserTokenId, trxName, (String[]) null);
	}

	public PiUserToken(Properties ctx, int piUserTokenId, String trxName, String... virtualColumns) {
		super(ctx, piUserTokenId, trxName, virtualColumns);
	}
	
	public static List<PO> getPiUserToken(String devieToken, int userId, Properties ctx, String trxName) {
		List<PO> list = new Query(ctx, Table_Name, "deviceToken = ? AND ad_user_ID = ?", trxName)
					.setParameters(devieToken, userId).setOrderBy(PiUserToken.COLUMNNAME_pi_userToken_ID).list();
		return list;
	}

	public static boolean checkTokenExistForuser(String devieToken, int userId, Properties ctx, String trxName) {
		boolean flag = false;
		List<PO> list  = getPiUserToken(devieToken, userId, ctx, trxName);
		if(list.size() > 0)
			flag = true;
		return flag;
	}
	
	public static List<PO> getTokensForUser(int clientId, int userId, Properties ctx, String trxName) {
		List<PO> list = new Query(ctx, Table_Name, "ad_client_ID = ? AND ad_user_ID = ?", trxName)
					.setParameters(clientId, userId).setOrderBy(PiUserToken.COLUMNNAME_pi_userToken_ID).list();
		return list;
	}
}
