package org.realmeds.tissue.custom;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.compiere.model.I_AD_User;
import org.compiere.model.MMessage;
import org.compiere.model.MNote;
import org.compiere.model.MUserRoles;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.idempiere.webservices.model.MWebService;
import org.idempiere.webservices.model.X_WS_WebServiceType;
import org.pipra.custom.fcm.FCMService;
import org.pipra.model.custom.PiUserToken;
import org.realmeds.tissue.model.TCVisit;
import org.realmeds.tissue.moduller.I_TC_VisitType;
import org.realmeds.tissue.moduller.X_TC_VisitType;
import org.springframework.scheduling.annotation.Async;

public class TCUtills {

//	private static final ExecutorService executor = Executors.newCachedThreadPool();
	private static final String TABLE_PRODUCT = "m_product";
	private static final String TABLE_USER = "ad_user";
	private static final String TABLE_DOC = "c_doctype";
	private static final String TABLE_PLANT_DETAIL = "tc_plantdetails";
	private static final String TABLE_EXPLANT = "tc_explantlabel";
	private static final String TABLE = "ad_table";
	private static final String TABLE_FARMER = "tc_farmer";
	private static final String TABLE_CULTURELABEL = "tc_cultureLabel";
	private static final String TABLE_CULTURESTAGE = "tc_culturestage";
	
	public static String generateUUID() {
		UUID uuid = UUID.randomUUID();
		String uuidAsString = uuid.toString();
		return uuidAsString;
	}

	public static int getId(String tableName, String uuid, int client_Id) {
		int recordId = 0;
		String sql = "select " + tableName + "_id AS id from adempiere." + tableName
				+ " where c_uuid = ? AND AD_Client_ID = ? ;";
		try (PreparedStatement pstm = DB.prepareStatement(sql, null)) {
			pstm.setString(1, uuid);
			pstm.setInt(2, client_Id);
			try (ResultSet rs = pstm.executeQuery()) {
				if (rs.next())
					recordId = rs.getInt("id");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return recordId;
	}
	
	public static int getIds(String tableName, String uuid) {
		int recordId = 0;
		String sql = "select " + tableName + "_id AS id from adempiere." + tableName
				+ " where c_uuid = ?  ;";
		try (PreparedStatement pstm = DB.prepareStatement(sql, null)) {
			pstm.setString(1, uuid);
			try (ResultSet rs = pstm.executeQuery()) {
				if (rs.next())
					recordId = rs.getInt("id");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return recordId;
	}
	
	public static int getValidCultureId(String uuid, int client_Id) {
		int recordId = 0;
		String sql = "select  cl." + TABLE_CULTURELABEL + "_id AS id from adempiere." + TABLE_CULTURELABEL + " cl "
				+ "JOIN adempiere." + TABLE_CULTURESTAGE + " cs ON cs." + TABLE_CULTURESTAGE + "_id = cl."
				+ TABLE_CULTURESTAGE + "_id "
				+ "where cl.c_uuid = ? AND cl.AD_Client_ID = ? AND cl.issold = 'N' AND cl.isdiscarded = 'N'  AND cs.name = 'Rooting';";
		try (PreparedStatement pstm = DB.prepareStatement(sql, null)) {
			pstm.setString(1, uuid);
			pstm.setInt(2, client_Id);
			try (ResultSet rs = pstm.executeQuery()) {
				if (rs.next())
					recordId = rs.getInt("id");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return recordId;
	}
	
	public static int getIdss(String tableName, int id, int client_Id) {
		int recordId = 0;
		String sql = "select " + tableName + "_id AS id from adempiere." + tableName + "" + " where " + tableName
				+ "_id = ? AND AD_Client_ID = ? ;";
		try (PreparedStatement pstm = DB.prepareStatement(sql, null)) {
			pstm.setInt(1, id);
			pstm.setInt(2, client_Id);
			try (ResultSet rs = pstm.executeQuery()) {
				if (rs.next())
					recordId = rs.getInt("id");

			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return recordId;
	}
	
	public static int getParentId(String tableName, String uuid, int client_Id, String parentTable) {
		int recordId = 0;
		String sql = "select o." + tableName + "_id AS id from adempiere." + tableName + " AS o " + "JOIN adempiere."
				+ parentTable + " cl ON cl.tc_out_id = o.tc_out_id "
				+ "where o.c_uuid = ? AND o.AD_Client_ID = ? AND cl.isdiscarded = 'N';";
		try (PreparedStatement pstm = DB.prepareStatement(sql, null)) {
			pstm.setString(1, uuid);
			pstm.setInt(2, client_Id);
			try (ResultSet rs = pstm.executeQuery()) {
				if (rs.next())
					recordId = rs.getInt("id");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return recordId;
	}
	
	public static int getMediaId(String tableName, String uuid, int client_Id) {
		int recordId = 0;
		String sql = "select " + tableName + "_id AS id from adempiere." + tableName + " "
				+ "where c_uuid = ? AND AD_Client_ID = ? AND isdiscarded = 'N';";
		try (PreparedStatement pstm = DB.prepareStatement(sql, null)) {
			pstm.setString(1, uuid);
			pstm.setInt(2, client_Id);

			try (ResultSet rs = pstm.executeQuery()) {
				if (rs.next())
					recordId = rs.getInt("id");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return recordId;
	}
	
	public static boolean canCreateVisit(int farmerId,int cycleNo,int visittypeID) {
		String visitType = getVisitType(visittypeID);
		if ("Intermediate Visit".equalsIgnoreCase(visitType)) {
	        return true; // Intermediate visits are allowed multiple times
	    }
		int existingCount = new Query(Env.getCtx(), TCVisit.Table_Name, "tc_farmer_id = ? AND cycleNo = ? AND tc_visittype_id = ?",null)
				.setParameters(farmerId,cycleNo,visittypeID)
				.count();
		return existingCount ==0;
	}
	
	public static String getVisitType(int visitTypeId) {
		I_TC_VisitType visit = new X_TC_VisitType(Env.getCtx(), visitTypeId, null);
		return visit.getName();
	}

	public static String getUserName(int client_Id, String personalCode) {
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String userName = "";
		String sql = "SELECT name AS UserName FROM adempiere."+ TABLE_USER +"\n" + "WHERE AD_Client_ID = " + client_Id
				+ " AND personalCode = '" + personalCode + "' limit 1;";
		pstm = DB.prepareStatement(sql, null);
		try {
			rs = pstm.executeQuery();
			while (rs.next()) {
				userName = rs.getString("UserName");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DB.close(rs, pstm);
		}

		return userName;
	}

	public static int getRecordId(int clientId, String tableName, String name) {
		int recordId = 0;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String sql = "select " + tableName + "_id AS id from adempiere." + tableName + " where ad_client_id = "
				+ clientId + " AND name = '" + name + "';";
		pstm = DB.prepareStatement(sql, null);
		try {
			rs = pstm.executeQuery();
			while (rs.next()) {
				recordId = rs.getInt("id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DB.close(rs, pstm);
		}
		return recordId;
	}

	public static int getLocatorId(int clientId, String tableName, String searchKey) {
		int recordId = 0;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String sql = "select " + tableName + "_id AS id from adempiere." + tableName + " where ad_client_id = "
				+ clientId + " AND value = '" + searchKey + "';";
		pstm = DB.prepareStatement(sql, null);
		try {
			rs = pstm.executeQuery();
			if (rs.next())
				recordId = rs.getInt("id");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DB.close(rs, pstm);
		}
		return recordId;
	}

	public static int getId(String uuid,int clientId) {
		int recordId = 0;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String sql = "select tc_plantdetails_id AS id from adempiere."+ TABLE_PLANT_DETAIL +" where planttaguuid = '" + uuid
				+ "' AND AD_Client_ID = " + clientId + " ;";
		pstm = DB.prepareStatement(sql, null);
		try {
			rs = pstm.executeQuery();
			if (rs.next())
				recordId = rs.getInt("id");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DB.close(rs, pstm);
		}
		return recordId;
	}
	
	public static int getIds(String uuid,int clientId) {
		int recordId = 0;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String sql = "select tc_explantlabel_id AS id from adempiere."+ TABLE_EXPLANT +" where parentuuid = '" + uuid
				+ "' AND AD_Client_ID = " + clientId + " ;";
		pstm = DB.prepareStatement(sql, null);
		try {
			rs = pstm.executeQuery();
			if (rs.next())
				recordId = rs.getInt("id");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DB.close(rs, pstm);
		}
		return recordId;
	}

	public static int getTableId(String tableName) {
		int recordId = 0;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String sql = "select ad_table_id AS id from adempiere."+ TABLE +" where name = '" + tableName + "';";
		pstm = DB.prepareStatement(sql, null);
		try {
			rs = pstm.executeQuery();
			if (rs.next())
				recordId = rs.getInt("id");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DB.close(rs, pstm);
		}
		return recordId;
	}

	public static int getDocTypeId(int clientId, String documentName) {
		int docTypeId = 0;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String sql = "SELECT c_doctype_id As id FROM adempiere."+ TABLE_DOC +" WHERE name = '" + documentName
				+ "' AND ad_client_id = " + clientId + ";";
		pstm = DB.prepareStatement(sql, null);
		try {
			rs = pstm.executeQuery();
			if (rs.next())
				docTypeId = rs.getInt("id");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DB.close(rs, pstm);
		}
		return docTypeId;
	}

	public static int getProductIdthroughdescription(int clientId, String description) {
		int docTypeId = 0;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String sql = "SELECT m_product_id As id FROM adempiere."+ TABLE_PRODUCT +" WHERE description = '" + description
				+ "' AND ad_client_id = " + clientId + ";";
		pstm = DB.prepareStatement(sql, null);
		try {
			rs = pstm.executeQuery();
			if (rs.next())
				docTypeId = rs.getInt("id");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DB.close(rs, pstm);
		}
		return docTypeId;
	}

	public static int getUserId(int clientId, String name) {
		int docTypeId = 0;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String sql = "SELECT ad_user_id As id FROM adempiere."+ TABLE_USER +" WHERE name = '" + name + "' AND ad_client_id = "
				+ clientId + ";";
		pstm = DB.prepareStatement(sql, null);
		try {
			rs = pstm.executeQuery();
			if (rs.next())
				docTypeId = rs.getInt("id");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DB.close(rs, pstm);
		}
		return docTypeId;
	}
	
	public static String getFarmerName(int clientId, int farmerId) {
		String name = "";
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String sql = "SELECT tc_farmer_id As id,name FROM adempiere."+ TABLE_FARMER +" WHERE tc_farmer_id = '" + farmerId + "' AND ad_client_id = "
				+ clientId + ";";
		pstm = DB.prepareStatement(sql, null);
		try {
			rs = pstm.executeQuery();
			if (rs.next())
				name = rs.getString("name");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DB.close(rs, pstm);
		}
		return name;
	}

	public static int getProductIdByName(int clientId, String productName) {
		int productId = 0;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String sql = "SELECT m_product_id As id FROM adempiere."+ TABLE_PRODUCT +" WHERE name = '" + productName
				+ "' AND ad_client_id = " + clientId + ";";
		pstm = DB.prepareStatement(sql, null);
		try {
			rs = pstm.executeQuery();
			if (rs.next())
				productId = rs.getInt("id");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DB.close(rs, pstm);
		}
		return productId;
	}

	public static List<MUserRoles> getUsersByClientAndRole(Properties ctx, int clientId, int roleId) {
		String sql = " AD_Client_ID = ? AND AD_Role_ID = ?";
		List<MUserRoles> muserList = new Query(ctx, MUserRoles.Table_Name, sql, null).setParameters(clientId, roleId)
				.setOrderBy(MUserRoles.COLUMNNAME_AD_User_ID).list();
		return muserList;
	}
	
	 public static boolean isValidMobileNumber(String mobileNo) {
	        return mobileNo != null && Pattern.matches("^\\d{10}$", mobileNo);
	    }
	    
	    public static boolean isValidPinCode(String pinCode) {
	        return pinCode != null && Pattern.matches("^\\d{6}$", pinCode);
	    }
	    
	    public static boolean isValidName(String name) {
	        return name != null && Pattern.matches("^[a-zA-Z\\s]+$", name);
	    }
	    
	    public static boolean isValidNameNumber(String name) {
	        return name != null && Pattern.matches("^[a-zA-Z0-9\\s\\-.]+$", name);
	    }
	    
	    public static boolean isValidLocationName(String location) {
	    	if (location == null || location.trim().isEmpty()) {
	            return false; // Reject null, empty, or whitespace-only strings
	        }
	        return location != null && Pattern.matches("^[a-zA-Z0-9\\s\\-,.+/]+$", location);
	    }
	    
	    public static boolean isValidNumber(String Number) {
	        return Number != null && Pattern.matches("^\\d{2}$", Number);
	    }
	    
	    public static boolean isValidBooleanString(String input) {
	        if (input == null) {
	            return false;
	        }
	        String trimmedInput = input.trim();
	        
	        return "true".equalsIgnoreCase(trimmedInput) || "false".equalsIgnoreCase(trimmedInput);
	    }
	    

	@Async
//	public static void sendNotificationAsync(int roleId, int tableId, int recordId, Properties ctx, String trxName,
//			String title, String body, String tableName, Map<String, String> data, int clientId, String messageTitle) {
//
//		executor.submit(() -> {
//			sendNotification(roleId, tableId, recordId, ctx, trxName, title, body, tableName, data, clientId,
//					messageTitle);
//		});
//
//	}

	private static void sendNotification(int roleId, int tableId, int recordId, Properties ctx, String trxName,
			String title, String body, String tableName, Map<String, String> data, int clientId, String messageTitle) {

		List<MUserRoles> mUserRoleList = getUsersByClientAndRole(ctx, clientId, roleId);
		if (mUserRoleList.size() == 0)
			return;

		for (MUserRoles mUserRole : mUserRoleList) {

			I_AD_User user = mUserRole.getAD_User();
			List<PO> userTokenList = PiUserToken.getTokensForUser(Env.getAD_Client_ID(Env.getCtx()),
					user.getAD_User_ID(), ctx, null);

			if (userTokenList.size() != 0) {

				int batchSize = 50;
				IntStream.range(0, userTokenList.size()).boxed().collect(Collectors.groupingBy(i -> i / batchSize))
						.values().parallelStream().forEach(batchIndexes -> {
							List<PO> batch = batchIndexes.stream().map(userTokenList::get).collect(Collectors.toList());

							batch.forEach(uToken -> {
								PiUserToken piUserToken = new PiUserToken(ctx, uToken.get_ID(), null);
								FCMService fcm = new FCMService();

								fcm.sendFCMMessage(piUserToken.getdevicetoken(), title, body, null, data);

								MNote mNoteS = new MNote(ctx, 0, null);
								mNoteS.setRecord(tableId, recordId);
								mNoteS.setAD_Message_ID(messageTitle);
								mNoteS.setTextMsg(body);
								mNoteS.setDescription(title + " " + body);
								mNoteS.setAD_User_ID(piUserToken.getAD_User_ID());

								if (mNoteS.getAD_Message_ID() == 240) {
									MMessage mMessage = new MMessage(ctx, 0, null);
									mMessage.setValue(messageTitle);
									mMessage.setMsgText(messageTitle);
									mMessage.setMsgType("M");
									mMessage.saveEx();

									mNoteS.setAD_Message_ID(mMessage.getAD_Message_ID());
								}

								mNoteS.saveEx();

							});
						});
			}

		}
	}
	
	public static String authenticate(String webServiceValue, String methodValue, Properties ctx) {
		
		int clientId = Env.getAD_Client_ID(ctx);

		MWebService m_webservice = new Query(ctx, MWebService.Table_Name, "Value=?", null)
	            .setParameters(webServiceValue)
	            .setOnlyActiveRecords(true)
	            .first();

        if (m_webservice == null) {
            return "Web Service '" + webServiceValue + "' not registered or inactive.";
        }

        X_WS_WebServiceType m_webservicetype = new Query(ctx, X_WS_WebServiceType.Table_Name, 
                "WS_WebService_ID=? AND Value=?", null)
                .setParameters(m_webservice.getWS_WebService_ID(), methodValue)
                .setOnlyActiveRecords(true)
                .first();

        if (m_webservicetype == null) {
            return "Method '" + methodValue + "' not registered or inactive under service '" + webServiceValue + "'.";
        }

        int AD_Role_ID = Env.getAD_Role_ID(ctx);

        String sql = "SELECT COUNT(*) FROM WS_WebServiceTypeAccess " +
                "WHERE AD_Role_ID=? AND WS_WebServiceType_ID=? AND AD_Client_ID=?";

        int accessCount = DB.getSQLValue(null, sql, AD_Role_ID, m_webservicetype.getWS_WebServiceType_ID(),clientId);

        if (accessCount <= 0) {
            return "Web Service Error: Login role (" + AD_Role_ID + ") does not have access to method '" + methodValue + "'.";
        }

        return null;
    }
}
