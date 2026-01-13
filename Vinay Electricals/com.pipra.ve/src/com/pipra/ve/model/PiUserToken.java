package com.pipra.ve.model;

import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

import org.compiere.model.PO;
import org.compiere.model.Query;

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
	
	public static boolean checkTokenExistForuserAndRole(String devieToken, int userId, int roleId, Properties ctx, String trxName) {
		boolean flag = false;
		List<PO> list  = getPiUserToken(devieToken, userId, ctx, trxName);
		if(list.size() > 0)
			flag = true;
		return flag;
	}
	
	public static List<PO> getUserTokensForUserAndRole(int userId, int roleId, Properties ctx, String trxName) {
		List<PO> list = new Query(ctx, Table_Name, "ad_user_ID = ? AND ad_role_ID = ?", trxName)
					.setParameters(userId, roleId).setOrderBy(PiUserToken.COLUMNNAME_pi_userToken_ID).list();
		return list;
	}
	
	public static void deleteTokensForUserAndRole(int userId, int roleId, Properties ctx, String trxName) {
		List<PO> list = new Query(ctx, Table_Name, "ad_user_ID = ? AND ad_role_ID = ?", trxName)
				.setParameters(userId, roleId).setOrderBy(PiUserToken.COLUMNNAME_pi_userToken_ID).list();
		if (list != null && list.size() != 0) {
			list.forEach(it -> new PiUserToken(ctx, it.get_ID(), trxName).delete(false));
		}
	}
	
	public static List<PO> getTokensForUser(int clientId, int userId, Properties ctx, String trxName) {
		List<PO> list = new Query(ctx, Table_Name, "ad_client_ID = ? AND ad_user_ID = ?", trxName)
					.setParameters(clientId, userId).setOrderBy(PiUserToken.COLUMNNAME_pi_userToken_ID).list();
		return list;
	}
	
	public static List<PO> getTokensForRole(int clientId, int roleId,  int departmentId, Properties ctx, String trxName) {
		
		List<PO> list = null;
		if(departmentId != 0)
		list = new Query(ctx, Table_Name, ""+Table_Name+".ad_client_ID = ? AND "+Table_Name+".ad_role_ID = ? AND au.pi_deptartment_ID = ?", trxName)
				.addJoinClause(" JOIN Ad_User au ON ("+Table_Name+".ad_user_ID =au.ad_user_ID ) ")
					.setParameters(clientId, roleId, departmentId).setOrderBy(PiUserToken.COLUMNNAME_pi_userToken_ID).list();
		else
			list = new Query(ctx, Table_Name, ""+Table_Name+".ad_client_ID = ? AND "+Table_Name+".ad_role_ID = ?", trxName)
				.setParameters(clientId, roleId).setOrderBy(PiUserToken.COLUMNNAME_pi_userToken_ID).list();
			
		return list;
	}
}
