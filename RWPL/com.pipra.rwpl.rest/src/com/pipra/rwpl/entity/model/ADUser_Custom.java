package com.pipra.rwpl.entity.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.compiere.model.MUser;
import org.compiere.model.PO;
import org.compiere.model.Query;

public class ADUser_Custom extends MUser {

	private static final long serialVersionUID = 1L;

	public ADUser_Custom(Properties ctx, int AD_User_ID, String trxName) {
		super(ctx, AD_User_ID, trxName);
	}

	/** Column name token */
	public static final String COLUMNNAME_token = "token";

	/** Set token */
	public void settoken(String token) {
		set_Value(COLUMNNAME_token, token);
	}

	/** Get token */
	public String gettoken() {
		String token = (String) get_Value(COLUMNNAME_token);
		return token;
	}

	/** Column name lastname */
	public static final String COLUMNNAME_firstname = "firstname";

	/** Set lastname */
	public void setfirstname(String firstname) {
		set_Value(COLUMNNAME_firstname, firstname);
	}

	/** Get lastname */
	public String getfirstname() {
		String firstname = (String) get_Value(COLUMNNAME_firstname);
		return firstname;
	}

	/** Column name lastname */
	public static final String COLUMNNAME_lastname = "lastname";

	/** Set lastname */
	public void setlastname(String lastname) {
		set_Value(COLUMNNAME_lastname, lastname);
	}

	/** Get lastname */
	public String getlastname() {
		String token = (String) get_Value(COLUMNNAME_lastname);
		return token;
	}

	public static List<ADUser_Custom> fetchUsers(Properties ctx, String trxName, int clientId, String searchKey,
			int limit, int offset, List<String> filterRoles, Boolean isActive) {

		StringBuilder whereClause = new StringBuilder();
		StringBuilder joinClause = new StringBuilder();
		
		ArrayList<Object> parameters = new ArrayList<Object>();

		whereClause.append(""+Table_Name+".ad_client_id = ? ");
		parameters.add(clientId);

		if (searchKey != null && !searchKey.isEmpty()) {
			whereClause.append("  AND  ("+Table_Name+".firstname ILIKE '%' || COALESCE(?, "+Table_Name+".firstname) || '%' OR "+Table_Name+".lastname ILIKE '%' ||"
					+ " COALESCE(?, "+Table_Name+".lastname) || '%' OR "+Table_Name+".EMail ILIKE '%' || COALESCE(?, "+Table_Name+".EMail) || '%')\n ");
			parameters.add(searchKey);
			parameters.add(searchKey);
			parameters.add(searchKey);
		}
		
		if (isActive != null) {

			whereClause.append("  AND "+Table_Name+".IsActive = ?");

			if (isActive)
				parameters.add("Y");
			else if (!isActive)
				parameters.add("N");
		}
		
		if(filterRoles !=  null && filterRoles.size() != 0) {
			String roleIds = filterRoles.stream().map(Object::toString).collect(Collectors.joining(", "));
			joinClause.append("JOIN ad_user_roles ur ON "+Table_Name+".ad_user_id  = ur.ad_user_id");
			whereClause.append("AND  ur.ad_role_id IN (").append(roleIds).append(") ");
		}

		String orderBy = ""+Table_Name+".created desc , "+Table_Name+".ad_user_id desc ";

		if (limit == 0)
			limit = 1000;

		List<ADUser_Custom> users = new ArrayList<ADUser_Custom>();
		List<PO> list = new Query(ctx, MUser.Table_Name, whereClause.toString(), trxName).addJoinClause(joinClause.toString()).setParameters(parameters)
				.setPageSize(limit).setRecordstoSkip(offset).setOrderBy(orderBy).list();

		if (list != null && list.size() != 0)
			list.forEach(it -> users.add(new ADUser_Custom(ctx, it.get_ID(), trxName)));

		return users;

	}

}
