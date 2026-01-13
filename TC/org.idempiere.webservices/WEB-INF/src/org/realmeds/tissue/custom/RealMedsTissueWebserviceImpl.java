package org.realmeds.tissue.custom;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.adempiere.exceptions.AdempiereException;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.codehaus.jettison.json.JSONObject;
import org.compiere.model.I_AD_Preference;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MAttachment;
import org.compiere.model.MAttachmentEntry;
import org.compiere.model.MClient;
import org.compiere.model.MElementValue;
import org.compiere.model.MFactAcct;
import org.compiere.model.MGLCategory;
import org.compiere.model.MLocator;
import org.compiere.model.MLocatorType;
import org.compiere.model.MMovement;
import org.compiere.model.MMovementLine;
import org.compiere.model.MOrg;
import org.compiere.model.MPeriod;
import org.compiere.model.MPreference;
import org.compiere.model.MRole;
import org.compiere.model.MSession;
import org.compiere.model.MTable;
import org.compiere.model.MTransaction;
import org.compiere.model.MUser;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.process.DocAction;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;
import org.compiere.util.Language;
import org.compiere.util.Login;
import org.compiere.util.Trx;
import org.compiere.util.Util;
import org.idempiere.adInterface.x10.*;
import org.idempiere.adinterface.CompiereService;
import org.idempiere.webservices.AbstractService;
import org.pipra.model.custom.PiUserToken;
import org.pipra.webservices.custom.MInOut_Custom;
import org.realmeds.tissue.model.ADUser_Custom;
import org.realmeds.tissue.model.TCCollectionJoinPlantDetails;
import org.realmeds.tissue.model.TCCollectionVisit;
import org.realmeds.tissue.model.TCCultureLabel;
import org.realmeds.tissue.model.TCCultureStage;
import org.realmeds.tissue.model.TCExplantLabel;
import org.realmeds.tissue.model.TCFarmer;
import org.realmeds.tissue.model.TCFirstVisit;
import org.realmeds.tissue.model.TCFirstVisitJoinPlantDetails;
import org.realmeds.tissue.model.TCIOTRecord;
import org.realmeds.tissue.model.TCIOTdeviceData;
import org.realmeds.tissue.model.TCIn;
import org.realmeds.tissue.model.TCIntermediateJoinPlantDetails;
import org.realmeds.tissue.model.TCIntermediateVisit;
import org.realmeds.tissue.model.TCMediaLabelQr;
import org.realmeds.tissue.model.TCMediaLine;
import org.realmeds.tissue.model.TCMediaOrder;
import org.realmeds.tissue.model.TCMediaOutLine;
import org.realmeds.tissue.model.TCMediaType;
import org.realmeds.tissue.model.TCOrder;
import org.realmeds.tissue.model.TCOut;
import org.realmeds.tissue.model.TCPlantDetail;
import org.realmeds.tissue.model.TCPrimaryHardeningLabel;
import org.realmeds.tissue.model.TCQualityCheck;
import org.realmeds.tissue.model.TCSecondaryHardeningLabel;
import org.realmeds.tissue.model.TCVisit;
import org.realmeds.tissue.moduller.I_TC_PlantDetails;
import org.realmeds.tissue.moduller.X_TC_Farmer;
import org.realmeds.tissue.moduller.X_TC_MediaType;
import org.realmeds.tissue.moduller.X_TC_PlantDetails;
import org.realmeds.tissue.moduller.X_TC_PrimaryHardeningLabel;
import org.realmeds.tissue.moduller.X_TC_SecondaryHardeningLabel;
import org.realmeds.tissue.moduller.X_TC_VisitType;
import org.realmeds.tissue.moduller.X_TC_collectionjoinplant;
import org.realmeds.tissue.moduller.X_TC_temperatureposition;
import org.realmeds.tissue.moduller.X_tc_light;
import org.realmeds.tissue.moduller.X_tc_primaryHardeningcultureS;
import org.realmeds.tissue.moduller.X_tc_tempstatus;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

@WebService(endpointInterface = "org.realmeds.tissue.custom.RealMedsTissueWebservice", serviceName = "RealMedsTissueWebservice", targetNamespace = "http://idempiere.org/ADInterface/1_0")
public class RealMedsTissueWebserviceImpl extends AbstractService implements RealMedsTissueWebservice {

	public static final String ROLE_TYPES_WEBSERVICE = "NULL,WS,SS";
	private static CLogger log = CLogger.getCLogger(RealMedsTissueWebserviceImpl.class);
	private boolean manageTrx = true;
	private static final String STATUS_IN_PROGRESS = "In Progress";
	private static final String STATUS_CANCELLED = "Cancelled";
	private static final String STATUS_COMPLETED = "Completed";
	
	private static final String FIRST_VISIT = "First Visit";
	private static final String INTERMEDIATE_VISIT = "Intermediate Visit";
	private static final String COLLECTION_VISIT = "Collection Visit";
	
	private static final String DEFAULT_PRODUCT_EXPLANT = "Explant";
	private static final String DEFAULT_PRODUCT_PLANT = "Plant Tag";
	private static final String DEFAULT_PRODUCT_PH = "H01";
	private static final String DEFAULT_PRODUCT_SH = "H02";
	
	private static final String DEFAULT_LOCATOR_PLANT = "P1-1-1";
	private static final String DEFAULT_LOCATOR_EXPLANT = "E1-1-1";
	private static final String DEFAULT_LOCATOR_CULTURE = "C1-10-02";
	private static final String DEFAULT_LOCATOR_MEDIA = "C1-20-02";
	private static final String DEFAULT_LOCATOR_HD = "HD-1-1";
	private static final String DEFAULT_LOCATOR_PH = "PH1-1-1";
	private static final String DEFAULT_LOCATOR_SH = "SH1-1-1";
	private static final String DEFAULT_DISCARD_CULTURE_LABEL = "Discard Culture Label";
	private static final String DEFAULT_DISCARD_MEDIA_LABEL = "Discard Media Label";
	
	private static final int UOM_ID = 100;
	private static final String DOCUMENT_MATERIAL_MOVEMENT = "Material Movement";
	private static final String DOCUMENT_MEDIA_ORDER = "TC Media Order";
	private static final String DOCUMENT_ORDERED = "TC Ordered";
	
	private static final String TEMPERATURE_STATUS_OVERCOOL = "OverCool";
	private static final String TEMPERATURE_STATUS_NORMAL = "Normal";
	private static final String TEMPERATURE_STATUS_OVERHEAT = "OverHeat";
	
	private static final String TABLE_LIGHT_STATUS = "TC_LightStatus";
	private static final String TABLE_TC_OUT = "tc_out";
	private static final String TABLE_TC_CULTURE = "tc_cultureLabel";
	private static final String TABLE_LOCATOR = "m_locator";
	private static final String TABLE_PLANT_DETAILS = "tc_plantdetails";
	private static final String TABLE_FARMER = "tc_farmer";
	private static final String TABLE_MEDIA = "tc_mediaLabelQr";
	private static final String TABLE_STATUS = "tc_status";
	private static final String TABLE_PLANT_STATUS = "tc_plantstatus";
	private static final String TABLE_VISIT_TYPE = "tc_visittype";
	private static final String TABLE_SH = "tc_secondaryhardeningLabel";
	private static final String TABLE_PH = "tc_primaryhardeningLabel";
	private static final String TABLE_EXPLANT = "tc_explantLabel";
	private static final String TABLE_PLANT = "tc_planttag";
	private static final String TABLE_MEDIA_TYPE = "TC_MediaType";
	private static final String TABLE_PLANT_DETAILS2 = "TC_PlantDetails";
	private static final String TABLE_FIRST_VISIT = "tc_firstvisit";
	private static final String TABLE_VISIT = "TC_VisitType";
	private static final String TABLE_MEDIATYPE = "TC_MediaType";
	
	private static final String TABLE_PLANT_STATUS_NAME = "Rejected";

	private static final String INTERNAL_SERVER_ERROR = "Internal Server Error";

	private @Context HttpServletRequest httpServletRequest;

	public boolean isManageTrx() {
		return manageTrx;
	}

	public void setManageTrx(boolean manageTrx) {
		this.manageTrx = manageTrx;
	}

	@Override
	public LoginApiResponseDocument loginApi(LoginApiRequestDocument req) {
		Trx trx = null;
		LoginApiResponseDocument loginApiResponseDocument = LoginApiResponseDocument.Factory.newInstance();
		LoginApiResponse loginApiResponse = loginApiResponseDocument.addNewLoginApiResponse();
		LoginApiRequest loginRequest = req.getLoginApiRequest();
		String deviceToken = loginRequest.getDeviceToken();
		try {
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
//			setCtxProp(ctx);
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			Login login = new Login(ctx);
			
			if (containsMaliciousPattern(loginRequest.getUser())) {
				loginApiResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
			    loginApiResponse.setIsError(true);
			    return loginApiResponseDocument;
			}
	        
	        if (containsMaliciousPattern(loginRequest.getPassword())) {
	        	loginApiResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
			    loginApiResponse.setIsError(true);
			    return loginApiResponseDocument;
			}

			String userType = null;
			ADUser_Custom mUser = null;
			List<PO> poList = MInOut_Custom.getUserByName(loginRequest.getUser(), ctx, trxName);
			if (poList.size() != 0 && poList != null) {
				mUser = new ADUser_Custom(ctx, poList.get(0).get_ID(), trxName);
				userType = mUser.getC_Job().getName();
			} else {
				loginApiResponse.setIsError(true);
				loginApiResponse.setError("Invalid username or password.");
				loginApiResponseDocument.setLoginApiResponse(loginApiResponse);
				return loginApiResponseDocument;
			}

			Timestamp blockedUpto = mUser.getBlockedupto();
			Timestamp currentTime = new Timestamp(System.currentTimeMillis());

			if (blockedUpto != null && blockedUpto.after(currentTime)) {
				loginApiResponse.setIsError(true);
				loginApiResponse.setError("Invalid username or password.");
				loginApiResponseDocument.setLoginApiResponse(loginApiResponse);
				return loginApiResponseDocument;
			}

			final int MAX_FAILED_ATTEMPTS = 5;

			int failedLogins = mUser.getFailedLoginCount();
			if (failedLogins > MAX_FAILED_ATTEMPTS) {
				loginApiResponse.setIsError(true);
				loginApiResponse.setError("Invalid username or password.");
				loginApiResponseDocument.setLoginApiResponse(loginApiResponse);
				return loginApiResponseDocument;
			}

			final long LOCKOUT_DURATION_MS = 24 * 60 * 60 * 1000L;

			KeyNamePair[] clients = login.getClients(loginRequest.getUser(), loginRequest.getPassword(),
					ROLE_TYPES_WEBSERVICE);

			if (clients == null) {
				failedLogins++;
				mUser.setFailedLoginCount(failedLogins);

				if (failedLogins > MAX_FAILED_ATTEMPTS) {
					long unlockTime = System.currentTimeMillis() + LOCKOUT_DURATION_MS;

					mUser.setBlockedupto(new java.sql.Timestamp(unlockTime));
				}

				mUser.saveEx();

				loginApiResponse.setIsError(true);
				loginApiResponse.setError("Invalid User ID or Password");
				loginApiResponseDocument.setLoginApiResponse(loginApiResponse);
				return loginApiResponseDocument;

			} else {
				mUser.setFailedLoginCount(0);
				mUser.setBlockedupto(null);
				mUser.saveEx();
			}

			if (userType == null)
				loginApiResponse.setUserType("");
			else
				loginApiResponse.setUserType(userType);
			
			int clientId = 0;
			int roleId = 0;
			int orgId = 0;
			int warehouseId = 0;
			String userName = loginRequest.getUser();

			for (KeyNamePair client : clients) {

				if (Integer.parseInt(client.getID()) != 0) {
					clientId = Integer.parseInt(client.getID());
					Client clientName = loginApiResponse.addNewClient();
					KeyNamePair[] roles = login.getRoles(loginRequest.getUser(), client, ROLE_TYPES_WEBSERVICE);

					if (roles != null) {
						for (KeyNamePair role : roles) {
							if (Integer.parseInt(role.getID()) != 0) {
								roleId = Integer.parseInt(role.getID());

								Role roleName = clientName.addNewRoleList();
								roleName.setRoleId(role.getID());
								roleName.setRole(role.getName());

								KeyNamePair[] orgs = login.getOrgs(new KeyNamePair(role.getKey(), ""));
								if (orgs != null) {

									for (KeyNamePair org : orgs) {
										if (Integer.parseInt(org.getID()) != 0) {
											orgId = Integer.parseInt(org.getID());

											Organization orgName = roleName.addNewOrgList();
											orgName.setOrgId(org.getID());
											orgName.setOrg(org.getName());

											KeyNamePair[] warehouses = login
													.getWarehouses(new KeyNamePair(org.getKey(), ""));
											if (warehouses != null) {
												for (KeyNamePair warehouse : warehouses) {
													if (Integer.parseInt(warehouse.getID()) != 0) {
														warehouseId = Integer.parseInt(warehouse.getID());

														Warehouse wName = orgName.addNewWarehouse();
														wName.setWarehouseId(warehouse.getID());
														wName.setWarehouse(warehouse.getName());
													}
													break;
												}
											}
											break;
										}
									}
								}

							}
						}
					}
					clientName.setClientId(client.getID());
					clientName.setClient(client.getName());

					break;
				}
			}
			
			setCtxProp(ctx);
			trx.commit();
			boolean flag = true;
			if (deviceToken != null)
				flag = PiUserToken.checkTokenExistForuser(deviceToken, Env.getAD_User_ID(ctx), ctx, trxName);
			if (!flag) {
				PiUserToken piUserToken = new PiUserToken(ctx, 0, trxName);
				piUserToken.setAD_User_ID(Env.getAD_User_ID(ctx));
				piUserToken.setdevicetoken(deviceToken);
				piUserToken.saveEx();
			}
			
			String token = processLoginParameters(clientId, roleId, orgId, warehouseId, userName, false);

			if (token == null) {
				loginApiResponse.setIsError(true);
				loginApiResponse.setError("Invalid User ID or Password");
				loginApiResponseDocument.setLoginApiResponse(loginApiResponse);
				return loginApiResponseDocument;
			} else {
				loginApiResponse.setToken(token);
				
				String refreshToken = processLoginParameters(clientId, roleId, orgId, warehouseId, userName, true);

				loginApiResponse.setRefreshToken(refreshToken);	
				
			}
			
			trx.commit();
		}catch (Exception e) {
			if (trx != null) trx.rollback();
		} finally {
			if (trx != null)
				trx.close();
				getCompiereService().disconnect();
		}
		return loginApiResponseDocument;
	}
	

	@Override
	public TokenRefreshDocument tokenRefresh(TokenRefreshDocument requst) {
		TokenRefreshDocument response = TokenRefreshDocument.Factory.newInstance();
		TokenRefresh resp = response.addNewTokenRefresh();
		TokenRefresh tokenRefreshRequest = requst.getTokenRefresh();
		String refreshToken = tokenRefreshRequest.getToken();
		Algorithm algorithm = Algorithm.HMAC512(TokenUtils.getTokenSecret());
		JWTVerifier verifier = JWT.require(algorithm).withIssuer(TokenUtils.getTokenIssuer()).build();

		// Verify the refresh token (expiration, signature)
		DecodedJWT jwt;
		try {
			jwt = verifier.verify(refreshToken);
		} catch (JWTVerificationException e) {

			resp.setIsError(true);
			resp.setError("Authenticate error");
			response.setTokenRefresh(resp);
			return response;
		}

		String userName = jwt.getSubject();

		Claim claim = jwt.getClaim(LoginClaims.AD_Client_ID.name());
		int clientId = -1;
		if (!claim.isNull() && !claim.isMissing())
			clientId = claim.asInt();

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

		Env.setContext(Env.getCtx(), Env.AD_CLIENT_ID, clientId);

		String token = processLoginParameters(clientId, roleId, orgId, warehouseId, userName, false);
		if (token == null) {
			resp.setIsError(true);
			resp.setError("Authenticate error");
			response.setTokenRefresh(resp);
			return response;

		} else {
			resp.setToken(token);

		}
		resp.setIsError(false);
		resp.setToken(token);
		return response;

	}
	
	private void closeDbCon(PreparedStatement pstm, ResultSet rs) {
		try {
			if (pstm != null)
				pstm.close();
			if (rs != null)
				rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
			log.saveError("Error : " , e);
		}
	}

	@Override
	public GetListResponseDocument getList(GetListRequestDocument req) {
		Trx trx = null;
		GetListResponseDocument getListResponseDocument = GetListResponseDocument.Factory.newInstance();
		GetListResponse getListResponse = getListResponseDocument.addNewGetListResponse();
		GetListRequest loginRequest = req.getGetListRequest();
		String table = safeTrim(loginRequest.getTableName());
		String tableName = table.toString();
		String tableId = tableName + "_id";
		int tableIds = MTable.getTable_ID(tableName);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
			int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			
			if (containsMaliciousPattern(table)) {
				getListResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				getListResponse.setIsError(true);
			    return getListResponseDocument;
			}
			
			String sql = "select name," + tableId + " from " + table + " where ad_client_id = ? ORDER BY " + tableId;
			pstmt = DB.prepareStatement(sql, null);
			pstmt.setInt(1, clientId);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				getListResponse.setTableId(tableIds);
				ListOfName listName = getListResponse.addNewListOfName();
				String names = rs.getString("name");
				int id = rs.getInt(tableId);
				listName.setName(names);
				listName.setId(id);
			}
			trx.commit();
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			getListResponse.setError("Table Name is not correct: " + table);
			getListResponse.setIsError(true);
			log.saveError("Error : " , e);
			return getListResponseDocument;
		} finally {
			closeDbCon(pstmt, rs);
			if (trx != null) {
				trx.close();
			}
			getCompiereService().disconnect();
		}
		return getListResponseDocument;
	}

	@Override
	public GetCodeListResponseDocument getCodeList(GetCodeListRequestDocument req) {
		Trx trx = null;
		GetCodeListResponseDocument getCodeListResponseDocument = GetCodeListResponseDocument.Factory.newInstance();
		GetCodeListResponse getCodeListResponse = getCodeListResponseDocument.addNewGetCodeListResponse();
		GetCodeListRequest loginRequest = req.getGetCodeListRequest();
		boolean isReference = loginRequest.getIsReference();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
			int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			
			if(isReference == true) {
				String referenceName = safeTrim(loginRequest.getReferenceName());
				String sql = "select l.name AS name from adempiere.AD_Ref_List l JOIN adempiere.AD_Reference r ON l.AD_Reference_id = r.AD_Reference_id "
						+ "where r.name = ?";

				pstmt = DB.prepareStatement(sql, null);
				pstmt.setString(1, referenceName);
				rs = pstmt.executeQuery();

				while (rs.next()) {
					ListOfNameAndCode list = getCodeListResponse.addNewListOfNameAndCode();
					String names = rs.getString("name");
					list.setName(names);
				}
				trx.commit();
			} else {
				String table = safeTrim(loginRequest.getTableName());
				String tableName = table.toString();
				String tableId = tableName + "_id";
				
				if (containsMaliciousPattern(table)) {
					getCodeListResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
					getCodeListResponse.setIsError(true);
				    return getCodeListResponseDocument;
				}
				
				String sql = "select name,codeNo," + tableId + " from " + table + " where ad_client_id = ? order by codeNo";
				pstmt = DB.prepareStatement(sql, null);
				pstmt.setInt(1, clientId);
				rs = pstmt.executeQuery();

				while (rs.next()) {
					ListOfNameAndCode list = getCodeListResponse.addNewListOfNameAndCode();
					String names = rs.getString("name");
					String codeNo = rs.getString("codeNo");
					int id = rs.getInt(tableId);
					list.setName(names);
					list.setCodeNo(codeNo);
					list.setId(id);
				}
				trx.commit();
			}
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			getCodeListResponse.setError("Table Name is not correct");
			getCodeListResponse.setIsError(true);
			log.saveError("Error : " , e);
			return getCodeListResponseDocument;
		} finally {
			closeDbCon(pstmt, rs);
			if (trx != null) {
				trx.close();
			}
			getCompiereService().disconnect();
		}
		return getCodeListResponseDocument;
	}

	public static boolean isBase64(String input) {
	    if (input == null || input.length() < 12) {
	        return false;
	    }
	    
	    String base64Pattern = "^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$";
	    
	    return input.matches(base64Pattern);
	}
	
	public static boolean isPlusCode(String input) {
	    if (input == null) return false;
	    return input.matches("^[A-Z0-9]{4}\\\\+[A-Z0-9]{2,4}$");
	}
	
	public boolean containsMaliciousPattern(String value) {
	    if (value == null || value.trim().isEmpty()) {
	        return false;
	    }
	    String xssPattern = "(?i).*(" +
	                        "<script|" +      // <script
	                        "</script|" +     // </script
	                        "alert\\s*\\(|" + // alert( (detects the 'alert' function)
	                        "alert|" +
	                        "onerror=|" +     // onerror=
	                        "onload=|" +      // onload=
	                        "<iframe|" +     // <iframe>
	                        "javascript:|" +  // javascript:
	                        "<svg|" +         // <svg
	                        "eval\\s*\\(|" +  // eval(
	                        "prompt\\s*\\(" + // prompt(
	                        ").*";

	    if (isPlusCode(value))
	        return false; 
	    
	    if (value.matches(xssPattern)) 
	        return true;
	    
	    if (value.matches(".*%[0-9A-Fa-f]{2}.*")) 
	         return true;
	    
	    if (isBase64(value))
	         return true;
	    
	    return false;
	}
	
	@Override
	public FarmerRegisterResponseDocument addFarmer(FarmerRegisterRequestDocument req) {
		Trx trx = null;
		FarmerRegisterResponseDocument farmerRegisterResponseDocument = FarmerRegisterResponseDocument.Factory
				.newInstance();
		FarmerRegisterResponse farmerRegisterResponse = farmerRegisterResponseDocument.addNewFarmerRegisterResponse();
		FarmerRegisterRequest loginRequest = req.getFarmerRegisterRequest();
		AddFarmer addFarmer = loginRequest.getAddFarmer();
		
	    String name = safeTrim(addFarmer.getName());
	    String latitude = safeTrim(addFarmer.getLatitude());
	    String longitude = safeTrim(addFarmer.getLongitude());
	    String mobileNo = safeTrim(addFarmer.getMobileNo());
	    String villageName = safeTrim(addFarmer.getVillageName());
	    String landmark = safeTrim(addFarmer.getLandmark());
	    String talukName = safeTrim(addFarmer.getTalukName());
	    String cityName = safeTrim(addFarmer.getCityName());
	    String district = safeTrim(addFarmer.getDistrict());
	    String state = safeTrim(addFarmer.getState());
	    String pinCode = safeTrim(addFarmer.getPinCode());


		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
	        int orgId = Env.getAD_Org_ID(ctx);
	        
	        if (containsMaliciousPattern(landmark)) {
			    farmerRegisterResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
			    farmerRegisterResponse.setStatus("Registration Failed");
			    farmerRegisterResponse.setIsError(true);
			    return farmerRegisterResponseDocument;
			}
	        
	        if (containsMaliciousPattern(name)) {
			    farmerRegisterResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
			    farmerRegisterResponse.setStatus("Registration Failed");
			    farmerRegisterResponse.setIsError(true);
			    return farmerRegisterResponseDocument;
			}
			
			if (containsMaliciousPattern(state)) {
			    farmerRegisterResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
			    farmerRegisterResponse.setStatus("Registration Failed");
			    farmerRegisterResponse.setIsError(true);
			    return farmerRegisterResponseDocument;
			}
			
			if (containsMaliciousPattern(villageName)) {
			    farmerRegisterResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
			    farmerRegisterResponse.setStatus("Registration Failed");
			    farmerRegisterResponse.setIsError(true);
			    return farmerRegisterResponseDocument;
			}
			
			if (containsMaliciousPattern(district)) {
			    farmerRegisterResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
			    farmerRegisterResponse.setStatus("Registration Failed");
			    farmerRegisterResponse.setIsError(true);
			    return farmerRegisterResponseDocument;
			}
			
			if (containsMaliciousPattern(cityName)) {
			    farmerRegisterResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
			    farmerRegisterResponse.setStatus("Registration Failed");
			    farmerRegisterResponse.setIsError(true);
			    return farmerRegisterResponseDocument;
			}
			
			if (containsMaliciousPattern(talukName)) {
			    farmerRegisterResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
			    farmerRegisterResponse.setStatus("Registration Failed");
			    farmerRegisterResponse.setIsError(true);
			    return farmerRegisterResponseDocument;
			}
			
			if (containsMaliciousPattern(latitude)) {
				farmerRegisterResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				farmerRegisterResponse.setIsError(true);
			    return farmerRegisterResponseDocument;
			}
			
			if (containsMaliciousPattern(longitude)) {
				farmerRegisterResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				farmerRegisterResponse.setIsError(true);
			    return farmerRegisterResponseDocument;
			}
			
			if (!TCUtills.isValidPinCode(pinCode)) {
	            farmerRegisterResponse.setError("Invalid PIN code: must be exactly 6 digits");
	            farmerRegisterResponse.setStatus("Registration Failed");
	            farmerRegisterResponse.setIsError(true);
	            return farmerRegisterResponseDocument;
	        }
			
			if (!TCUtills.isValidMobileNumber(mobileNo)) {
	            farmerRegisterResponse.setError("Invalid Mobile number: must be exactly 10 digits");
	            farmerRegisterResponse.setStatus("Registration Failed");
	            farmerRegisterResponse.setIsError(true);
	            return farmerRegisterResponseDocument;
	        }

	        if (!TCUtills.isValidNameNumber(name)) {
	            farmerRegisterResponse.setError("Invalid Farmer Name format");
	            farmerRegisterResponse.setStatus("Registration Failed");
	            farmerRegisterResponse.setIsError(true);
	            return farmerRegisterResponseDocument;
	        }

	        if (!TCUtills.isValidName(district)) {
	            farmerRegisterResponse.setError("Invalid District Name format");
	            farmerRegisterResponse.setStatus("Registration Failed");
	            farmerRegisterResponse.setIsError(true);
	            return farmerRegisterResponseDocument;
	        }

	        if (!TCUtills.isValidName(cityName)) {
	            farmerRegisterResponse.setError("Invalid City Name format");
	            farmerRegisterResponse.setStatus("Registration Failed");
	            farmerRegisterResponse.setIsError(true);
	            return farmerRegisterResponseDocument;
	        }

	        if (!TCUtills.isValidName(talukName)) {
	            farmerRegisterResponse.setError("Invalid Taluk Name format");
	            farmerRegisterResponse.setStatus("Registration Failed");
	            farmerRegisterResponse.setIsError(true);
	            return farmerRegisterResponseDocument;
	        }

	        if (!TCUtills.isValidLocationName(state)) {
	            farmerRegisterResponse.setError("Invalid State Name format");
	            farmerRegisterResponse.setStatus("Registration Failed");
	            farmerRegisterResponse.setIsError(true);
	            return farmerRegisterResponseDocument;
	        }

	        if (!TCUtills.isValidLocationName(villageName)) {
	            farmerRegisterResponse.setError("Invalid Village Name format");
	            farmerRegisterResponse.setStatus("Registration Failed");
	            farmerRegisterResponse.setIsError(true);
	            return farmerRegisterResponseDocument;
	        }

	        if (!TCUtills.isValidLocationName(landmark)) {
	            farmerRegisterResponse.setError("Invalid Landmark format");
	            farmerRegisterResponse.setStatus("Registration Failed");
	            farmerRegisterResponse.setIsError(true);
	            return farmerRegisterResponseDocument;
	        }
	        
	        if (!TCUtills.isValidLocationName(latitude)) {
	            farmerRegisterResponse.setError("Invalid Latitude format");
	            farmerRegisterResponse.setStatus("Registration Failed");
	            farmerRegisterResponse.setIsError(true);
	            return farmerRegisterResponseDocument;
	        }
	        
	        if (!TCUtills.isValidLocationName(longitude)) {
	            farmerRegisterResponse.setError("Invalid Longitude format");
	            farmerRegisterResponse.setStatus("Registration Failed");
	            farmerRegisterResponse.setIsError(true);
	            return farmerRegisterResponseDocument;
	        }
	        
	        String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			
			TCFarmer farmer = new TCFarmer(ctx, 0, null);
			farmer.setAD_Org_ID(orgId);
			farmer.setName(name);
			farmer.setlatitude(latitude);
			farmer.setlongitude(longitude);
			farmer.setmobileno(mobileNo);
			farmer.setvillagename2(villageName);
			farmer.setlandmark(landmark);
			farmer.settalukname(talukName);
			farmer.setCity(cityName);
			farmer.setdistrict(district);
			farmer.setstate(state);
			farmer.setpincode(pinCode);
			if (!farmer.save()) {
				throw new Exception("Failed to save Farmer Registration: " + farmer);
			}
			trx.commit();
			farmerRegisterResponse.setStatus("Farmer Registration Successfull");
			int farmerId = farmer.get_ID();
			farmerRegisterResponse.setFarmerId(farmerId);
			farmerRegisterResponse.setIsError(false);

			Map<String, String> data = new HashMap<>();
			data.put("recordId", String.valueOf(farmerId));
			data.put("farmerName", farmer.getName());

//			TCUtills.sendNotificationAsync(login.getRoleID(), tableId, farmerId, ctx, trxName,
//					"Farmer Registered - " + farmer.getName() + "",
//					"Farmer details :- Name - " + farmer.getName() + "\n" + "FarmerId - " + farmerId + "", tableName,
//					data, login.getClientID(), "Farmer Registration Successfully");

		} catch (Exception e) {
			if (trx != null) trx.rollback();
			farmerRegisterResponse.setStatus("Registration Failed");
			farmerRegisterResponse.setError(INTERNAL_SERVER_ERROR);
			farmerRegisterResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return farmerRegisterResponseDocument;
	}
	
	private String safeTrim(String value) {
	    return value != null ? value.trim() : "";
	}

	@Override
	public GetVisitResponseDocument getVisit(GetVisitRequestDocument req) {
		GetVisitResponseDocument getVisitResponseDocument = GetVisitResponseDocument.Factory.newInstance();
		GetVisitResponse getVisitResponse = getVisitResponseDocument.addNewGetVisitResponse();
		GetVisitRequest loginRequest = req.getGetVisitRequest();
		int count = 0;
		int tableId = MTable.getTable_ID(TABLE_FARMER);
		MAttachment attachment = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String searchKey = loginRequest.getSearchKey();
		int pageSize = loginRequest.getPageSize(); // Number of records per page
		int pageNumber = loginRequest.getPageNumber(); // Current page number
		int offset = (pageNumber - 1) * pageSize; // Calculate offset
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
			int clientId = Env.getAD_Client_ID(ctx);
			String user = safeTrim(new MUser(ctx, Env.getAD_User_ID(ctx), null).getName());

			
			List<String> farmerList = new ArrayList<>();
			List<String> statusList = new ArrayList<>();
			List<String> mobilenoList = new ArrayList<>();
			List<String> dateList = new ArrayList<>();
			List<String> visitTypeList = new ArrayList<>();

			for (Filter filter : loginRequest.getFiltersArray()) {
				switch (filter.getKey().toLowerCase()) {
				case "farmername":
					farmerList.add(filter.getValue());
					break;
				case "status":
					statusList.add(filter.getValue());
					break;
				case "mobileno":
					mobilenoList.add(filter.getValue());
					break;
				case "date":
					dateList.add(filter.getValue());
					break;
				case "visittypename":
					visitTypeList.add(filter.getValue());
					break;
				}
			}
			StringBuilder sql = new StringBuilder(
					"SELECT v.cycleNo AS cycleNo,f.name AS farmerName, vt.name AS VisitType, v.date AS Date, f.mobileno AS MobileNo,f.tc_farmer_id As farmerId, ")
					.append("v.tc_visit_id AS ID, s.name AS status,COUNT(*) OVER () AS totalCount,v.visitdone As visitdone FROM adempiere.tc_visit v ")
					.append("JOIN adempiere.tc_farmer f ON f.tc_farmer_id = v.tc_farmer_id ")
					.append("JOIN adempiere.tc_status s ON s.tc_status_id = v.tc_status_id ")
					.append("JOIN adempiere.tc_visittype vt ON vt.tc_visittype_id = v.tc_visittype_id ")
					.append("JOIN adempiere.ad_user u ON u.ad_user_id = v.createdby ").append("WHERE v.ad_client_id = ?"
							+ " AND s.name <> 'Cancelled' AND u.name = ? ");
			
			// Dynamic filters with placeholders
	        if (!farmerList.isEmpty()) {
	            sql.append(" AND f.name IN (")
	               .append(farmerList.stream().map(x -> "?").collect(Collectors.joining(",")))
	               .append(")");
	        }
	        if (!statusList.isEmpty()) {
	            sql.append(" AND s.name IN (")
	               .append(statusList.stream().map(x -> "?").collect(Collectors.joining(",")))
	               .append(")");
	        }
	        if (!mobilenoList.isEmpty()) {
	            sql.append(" AND f.mobileno IN (")
	               .append(mobilenoList.stream().map(x -> "?").collect(Collectors.joining(",")))
	               .append(")");
	        }
	        if (!dateList.isEmpty()) {
	            sql.append(" AND CAST(v.date AS date) IN (")
	               .append(dateList.stream().map(x -> "?").collect(Collectors.joining(",")))
	               .append(")");
	        }
	        if (!visitTypeList.isEmpty()) {
	            sql.append(" AND vt.name IN (")
	               .append(visitTypeList.stream().map(x -> "?").collect(Collectors.joining(",")))
	               .append(")");
	        }
	        
			if (searchKey != null && !searchKey.trim().isEmpty()) {
				sql.append(" AND (f.name ILIKE ? OR s.name ILIKE ?  OR v.mobileno ILIKE ? OR vt.name ILIKE ?)");
			}
			sql.append(" order by v.tc_visit_id DESC LIMIT ? OFFSET ?");

			pstm = DB.prepareStatement(sql.toString(), null);
			
//			int userId = Env.getAD_User_ID(ctx);
//			MUser user = new MUser(ctx, userId, null);
			int parameterIndex = 1;
			pstm.setInt(parameterIndex++, clientId);
	        pstm.setString(parameterIndex++, user);
	        System.out.println(user);
	        
	        for (String f : farmerList) pstm.setString(parameterIndex++, f);
	        for (String s : statusList) pstm.setString(parameterIndex++, s);
	        for (String m : mobilenoList) pstm.setString(parameterIndex++, m);
	        for (String d : dateList) pstm.setDate(parameterIndex++, java.sql.Date.valueOf(d));
	        for (String vt : visitTypeList) pstm.setString(parameterIndex++, vt);
	        
			if (searchKey != null && !searchKey.trim().isEmpty()) {
				for (int i = 0; i < 4; i++) {
					pstm.setString(parameterIndex++, "%" + searchKey + "%");
				}
			}
			
	        pstm.setInt(parameterIndex++, pageSize);
	        pstm.setInt(parameterIndex++, offset);
	        
			rs = pstm.executeQuery();

			if (!rs.isBeforeFirst()) {
				getVisitResponse.setIsError(false);
				getVisitResponse.setCount(0);
				getVisitResponse.addNewListOfVisit();
				return getVisitResponseDocument;
			}

			while (rs.next()) {
				ListOfVisit listOfVisits = getVisitResponse.addNewListOfVisit();
				int farmerId = rs.getInt("farmerId");
				
				listOfVisits.setVisitId(rs.getInt("ID"));
	            listOfVisits.setFarmerId(rs.getInt("farmerId"));
	            listOfVisits.setName(rs.getString("farmerName"));
	            listOfVisits.setVisitType(rs.getString("VisitType"));
	            listOfVisits.setDate(rs.getString("Date"));
	            listOfVisits.setMobileNo(rs.getString("MobileNo"));
	            listOfVisits.setStatus(rs.getString("status"));
	            listOfVisits.setVisitDone(rs.getBoolean("visitdone"));
	            listOfVisits.setCycleNo(rs.getInt("cycleNo"));
	            count = rs.getInt("totalCount");

				attachment = MAttachment.get(ctx, tableId, farmerId);
				if (attachment != null) {
					MAttachmentEntry[] entries = attachment.getEntries();
					if (entries.length > 0) {
						int i = entries.length - 1; // latest
	                    ImageArray imageArray = listOfVisits.addNewImageArray1();
	                    imageArray.setImageIndexId(i);
					} else {
						listOfVisits.addNewImageArray1();
					}
				} else {
					listOfVisits.addNewImageArray1();
				}
			}
			getVisitResponse.setCount(count);
			getVisitResponse.setTableId(tableId);
		} catch (Exception e) {
//			if (trx != null) trx.rollback();
			getVisitResponse.setError(INTERNAL_SERVER_ERROR);
			getVisitResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);

			getCompiereService().disconnect();
		}
		return getVisitResponseDocument;
	}

	@Override
	public AddVisitResponseDocument addVisit(AddVisitRequestDocument req) {
		AddVisitResponseDocument addVisitResponseDocument = AddVisitResponseDocument.Factory.newInstance();
		AddVisitResponse addVisitResponse = addVisitResponseDocument.addNewAddVisitResponse();
		AddVisitRequest loginRequest = req.getAddVisitRequest();
		AddVisit addVisit = loginRequest.getAddVisit();
		String name = "";
		String mobileNo = "";
		String dates = safeTrim(addVisit.getDate());
		int farmerId = 0;
		int visitTypeId = 0;
		int cycleNo = 0;
		Trx trx = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
			int clientId = Env.getAD_Client_ID(ctx);
			int orgId = Env.getAD_Org_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			try {
				farmerId = addVisit.getFarmerId();
			}catch (Exception e) {
				addVisitResponse.setError("Invalid FarmerId: must be an integer");
	            addVisitResponse.setIsError(true);
	            log.saveError("Error : " , e);
	            return addVisitResponseDocument;
			}
			
			try {
				visitTypeId = addVisit.getVisitTypeId();
			}catch (Exception e) {
				addVisitResponse.setError("Invalid VisitTypeId: must be an integer");
	            addVisitResponse.setIsError(true);
	            log.saveError("Error : " , e);
	            return addVisitResponseDocument;
			}
			
			try {
				cycleNo = addVisit.getCycleNo();
			}catch (Exception e) {
				addVisitResponse.setError("Invalid CycleNo: must be an integer");
	            addVisitResponse.setIsError(true);
	            log.saveError("Error : " , e);
	            return addVisitResponseDocument;
			}
			
			 if (containsMaliciousPattern(dates)) {
				 addVisitResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				 addVisitResponse.setIsError(true);
				    return addVisitResponseDocument;
				}
			
			int statusId = TCUtills.getRecordId(clientId, TABLE_STATUS, STATUS_IN_PROGRESS);
			if (statusId == 0) {
				addVisitResponse.setError("Status id is not in table record " + STATUS_IN_PROGRESS + "");
				addVisitResponse.setIsError(true);
				return addVisitResponseDocument;
			}

			int firstVisitId = TCUtills.getRecordId(clientId, TABLE_VISIT_TYPE, FIRST_VISIT);
			if (firstVisitId == 0) {
				addVisitResponse.setError("First visit type is not in table record " + FIRST_VISIT + "");
				addVisitResponse.setIsError(true);
				return addVisitResponseDocument;
			}

			int intermediateVisitId = TCUtills.getRecordId(clientId, TABLE_VISIT_TYPE, INTERMEDIATE_VISIT);
			if (intermediateVisitId == 0) {
				addVisitResponse
						.setError("Intermediate visit type is not in table record " + INTERMEDIATE_VISIT + "");
				addVisitResponse.setIsError(true);
				return addVisitResponseDocument;
			}

			int collectionVisitId = TCUtills.getRecordId(clientId, TABLE_VISIT_TYPE, COLLECTION_VISIT);
			if (collectionVisitId == 0) {
				addVisitResponse.setError("Collection visit type is not in table record " + COLLECTION_VISIT + "");
				addVisitResponse.setIsError(true);
				return addVisitResponseDocument;
			}
	        java.util.Date date = dateFormat.parse(dates);
	        Timestamp timestamp = new Timestamp(date.getTime());
	        String formattedDate = dateFormat.format(date);

			TCFarmer farmer = new TCFarmer(ctx, farmerId, trx.getTrxName());
			name = farmer.getName();
			mobileNo = farmer.getmobileno();

			if (!TCUtills.canCreateVisit(farmerId, cycleNo, visitTypeId)) {
				addVisitResponse.setError("only one first and collection visit allowed per cycle");
				addVisitResponse.setIsError(true);
				return addVisitResponseDocument;
			}

			TCVisit visit = new TCVisit(ctx, 0, trx.getTrxName());
			visit.setAD_Org_ID(orgId);
			visit.setName(name);
			visit.setmobileno(mobileNo);
			visit.setdate(timestamp);
			visit.setTC_Farmer_ID(farmerId);
			visit.setTC_VisitType_ID(visitTypeId);
			visit.setTC_Status_ID(statusId);
			visit.setcycleno(cycleNo);

			if (!visit.save()) {
				throw new Exception("Failed to save Add Visit : " + visit);
			}
			visit.setName(name + "_" + visit.get_ID() + "_" + dates);
			visit.saveEx();
			trx.commit();
			
			int visitId = visit.get_ID();
			addVisitResponse.setFarmerName(name);
			addVisitResponse.setVisitId(visitId);
			addVisitResponse.setIsError(false);

			if (visitTypeId == firstVisitId) {
				TCFirstVisit firstVisit = new TCFirstVisit(ctx, 0, trx.getTrxName());
				firstVisit.setAD_Org_ID(orgId);
				firstVisit.setTC_Farmer_ID(farmerId);
				firstVisit.setTC_Visit_ID(visitId);
				firstVisit.setvisitdate(timestamp);
				if (!firstVisit.save()) {
					throw new Exception("Failed to save Add First Visit : " + firstVisit);
				}
				firstVisit.setName("FirstVisit_" + firstVisit.get_ID() + "_" + formattedDate);
				firstVisit.saveEx();
				trx.commit();
				addVisitResponse.setFirstVisitId(firstVisit.get_ID());

			} else if (visitTypeId == intermediateVisitId) {
				TCIntermediateVisit intermediateVisit = new TCIntermediateVisit(ctx, 0, trx.getTrxName());
				intermediateVisit.setAD_Org_ID(orgId);
				intermediateVisit.setTC_Farmer_ID(farmerId);
				intermediateVisit.setTC_Visit_ID(visitId);
				if (!intermediateVisit.save()) {
					throw new Exception("Failed to save Add Intermediate Visit : " + intermediateVisit);
				}
				intermediateVisit.setName("IntermediateVisit_" + intermediateVisit.get_ID() + "_" + formattedDate);
				intermediateVisit.saveEx();
				trx.commit();
				addVisitResponse.setIntermediateVisitId(intermediateVisit.get_ID());

			} else if (visitTypeId == collectionVisitId) {
				TCCollectionVisit collectionVisit = new TCCollectionVisit(ctx, 0, trx.getTrxName());
				collectionVisit.setAD_Org_ID(orgId);
				collectionVisit.setTC_Farmer_ID(farmerId);
				collectionVisit.setTC_Visit_ID(visitId);
				if (!collectionVisit.save()) {
					throw new Exception("Failed to save Add Collection Visit : " + collectionVisit);
				}
				collectionVisit.setName("CollectionVisit_" + collectionVisit.get_ID() + "_" + formattedDate);
				collectionVisit.saveEx();
				trx.commit();
				addVisitResponse.setCollectionVisitId(collectionVisit.get_ID());
			}
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			addVisitResponse.setError(INTERNAL_SERVER_ERROR);
			addVisitResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return addVisitResponseDocument;
	}

	@Override
	public UpdateFirstVisitResponseDocument updateFirstVisit(UpdateFirstVisitRequestDocument req) {
		UpdateFirstVisitResponseDocument updateFirstVisitResponseDocument = UpdateFirstVisitResponseDocument.Factory
				.newInstance();
		UpdateFirstVisitResponse updateFirstVisitResponse = updateFirstVisitResponseDocument
				.addNewUpdateFirstVisitResponse();
		UpdateFirstVisitRequest loginRequest = req.getUpdateFirstVisitRequest();
		UpdateFirstVisit updateFirstVisit = loginRequest.getUpdateFirstVisit();
		String nameOfPest = updateFirstVisit.getNameOfPest();
		int firstVisitId = 0;
		int fieldConditionId = 0;
		int soilTypeId = 0;
		int wateringTypeId = 0;
		int fieldManagementId = 0;
		int plantNo = 0;
		String enterDetailsOfInfestation = updateFirstVisit.getEnterDetailsOfInfestation();
		String date = safeTrim(updateFirstVisit.getDate());
		boolean visitDone = updateFirstVisit.getVisitDone();
		Trx trx = null;
		Timestamp timestamp = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
		    int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			firstVisitId = updateFirstVisit.getFirstVisitId();
			fieldConditionId = updateFirstVisit.getFieldConditionId();
			soilTypeId = updateFirstVisit.getSoilTypeId();
			wateringTypeId = updateFirstVisit.getWaterringTypeId();
			fieldManagementId = updateFirstVisit.getFieldManagementId();
			plantNo = updateFirstVisit.getPlantNo();
			int visitCompletedId = TCUtills.getRecordId(clientId, TABLE_STATUS, STATUS_COMPLETED);
			if (visitCompletedId == 0) {
				updateFirstVisitResponse.setError("Status id is not in table record " + STATUS_COMPLETED + "");
				updateFirstVisitResponse.setIsError(true);
				return updateFirstVisitResponseDocument;
			}
			
			if (containsMaliciousPattern(date)) {
				updateFirstVisitResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				updateFirstVisitResponse.setIsError(true);
				    return updateFirstVisitResponseDocument;
				}
			
			if (date != null && !date.isEmpty()) {
				Date dates = (Date) dateFormat.parse(date);
				long timestampValue = dates.getTime();
				timestamp = new Timestamp(timestampValue);
			}

			TCFirstVisit firstVisit = new TCFirstVisit(ctx, firstVisitId, trx.getTrxName());
			firstVisit.setTC_FieldSelection_ID(fieldConditionId);
			firstVisit.setTC_SoilType_ID(soilTypeId);
			firstVisit.setTC_WateringType_ID(wateringTypeId);
			firstVisit.setTC_FieldManagement_ID(fieldManagementId);
			firstVisit.setplantno(plantNo);
			firstVisit.setpesthistory(nameOfPest);
			firstVisit.setenterdetailsofinfestation(enterDetailsOfInfestation);

			if (date != null && !date.isEmpty()) {
				firstVisit.setvisitdate(timestamp);
			}

			if (!firstVisit.save()) {
				throw new Exception("Failed to save Add First Visit : " + firstVisit);
			}
			int visitId = firstVisit.getTC_Visit_ID();
			TCVisit visit = new TCVisit(ctx, visitId, trx.getTrxName());
			visit.setTC_Status_ID(visitCompletedId);
			visit.setvisitdone(visitDone);
			visit.saveEx();
			trx.commit();
			String firstVisitUUId = firstVisit.getc_uuid();
			updateFirstVisitResponse.setFirstVisitId(firstVisitId);
			updateFirstVisitResponse.setFirstVisitUUId(firstVisitUUId);
			updateFirstVisitResponse.setIsError(false);

		} catch (Exception e) {
			if (trx != null) trx.rollback();
			updateFirstVisitResponse.setError(INTERNAL_SERVER_ERROR);
			updateFirstVisitResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return updateFirstVisitResponseDocument;
	}

	@Override
	public AddPlantDetailResponseDocument addPlantDetail(AddPlantDetailRequestDocument req) {
		AddPlantDetailResponseDocument addPlantDetailResponseDocument = AddPlantDetailResponseDocument.Factory
				.newInstance();
		AddPlantDetailResponse addPlantDetailResponse = addPlantDetailResponseDocument.addNewAddPlantDetailResponse();
		AddPlantDetailRequest loginRequest = req.getAddPlantDetailRequest();
		int firstVisitId = 0;
		int tableId = 0;
		int i = 1;
		Trx trx = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
		int clientId = Env.getAD_Client_ID(ctx);
		int orgId = Env.getAD_Org_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			firstVisitId = loginRequest.getFirstVisitId();
			tableId = TCUtills.getTableId(TABLE_PLANT_DETAILS2);
			if (tableId == 0) {
				throw new Error("Table Id is not available");
			}
			TCFirstVisit firstVisit = new TCFirstVisit(ctx, firstVisitId, trx.getTrxName());
			int farmerId = firstVisit.getTC_Farmer_ID();

			AddPlantDetail[] addPlantDetail = loginRequest.getAddPlantDetailArray();
			for (AddPlantDetail details : addPlantDetail) {
				String plantTagUUId = details.getPlantTagUUId();
				String dates = details.getDateOfCollection();
				String parentCultureLine = details.getParentCultureLine();
				String diseaseName = details.getDiseaseName();
				String medicineDetail = details.getMedicineDetail();
				String height = details.getHeight();
				String stature = details.getStature();
//				int leavesNo = (details == null || details.getLeavesNo() == null) ? 0 : details.getLeavesNo();
				Integer leavesNoWrapper = details.getLeavesNo();
				int leavesNo = (leavesNoWrapper != null) ? leavesNoWrapper : 0;
				String bunchWeight = details.getBunchWeight();
				String weight = details.getWeight();
				String bunchesNo = details.getBunchesNo();
				String row = details.getRow();
				String column = details.getColumn();
				int specie_Id = details.getSpecieId();
				int variety_Id = details.getVarietyId();
				AddImageBase64[] addImageBase64 = details.getAddImageBase64Array();

				if (plantTagUUId == "") {
					addPlantDetailResponse.setError("Plant Tag is empty, so the Plant Detail record was not created");
					addPlantDetailResponse.setIsError(true);
					return addPlantDetailResponseDocument;
				}

				int planttagId = TCUtills.getId(plantTagUUId, clientId);
				if (planttagId != 0) {
					addPlantDetailResponse.setError("This plant tag is already attached to a visit");
					addPlantDetailResponse.setIsError(true);
					return addPlantDetailResponseDocument;
				}
				
				if (containsMaliciousPattern(parentCultureLine)) {
					addPlantDetailResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
					addPlantDetailResponse.setIsError(true);
				    return addPlantDetailResponseDocument;
				}
				
				if (containsMaliciousPattern(bunchWeight)) {
					addPlantDetailResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
					addPlantDetailResponse.setIsError(true);
				    return addPlantDetailResponseDocument;
				}
				
				if (containsMaliciousPattern(weight)) {
					addPlantDetailResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
					addPlantDetailResponse.setIsError(true);
				    return addPlantDetailResponseDocument;
				}
				
				if (containsMaliciousPattern(row)) {
					addPlantDetailResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
					addPlantDetailResponse.setIsError(true);
				    return addPlantDetailResponseDocument;
				}
				
				if (containsMaliciousPattern(column)) {
					addPlantDetailResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
					addPlantDetailResponse.setIsError(true);
				    return addPlantDetailResponseDocument;
				}
				
				if (!TCUtills.isValidNameNumber(parentCultureLine)) {
					addPlantDetailResponse.setError("Invalid Parent Culture Line format");
					addPlantDetailResponse.setIsError(true);
					return addPlantDetailResponseDocument;
		        }
				
				if (!TCUtills.isValidLocationName(bunchWeight)) {
					addPlantDetailResponse.setError("Invalid Bunch Weight format");
					addPlantDetailResponse.setIsError(true);
					return addPlantDetailResponseDocument;
		        }
				
				if (!TCUtills.isValidLocationName(weight)) {
					addPlantDetailResponse.setError("Invalid Weight format");
					addPlantDetailResponse.setIsError(true);
					return addPlantDetailResponseDocument;
		        }
				
				if (!TCUtills.isValidLocationName(row)) {
					addPlantDetailResponse.setError("Invalid Row format");
					addPlantDetailResponse.setIsError(true);
					return addPlantDetailResponseDocument;
		        }
				
				if (!TCUtills.isValidLocationName(column)) {
					addPlantDetailResponse.setError("Invalid Column format");
					addPlantDetailResponse.setIsError(true);
					return addPlantDetailResponseDocument;
		        }

				Date date = (Date) dateFormat.parse(dates);
				long timestampValue = date.getTime();
				Timestamp timestamp = new Timestamp(timestampValue);

				TCPlantDetail plant = new TCPlantDetail(ctx, 0, trx.getTrxName());
				plant.setdate(timestamp);
				plant.setparentcultureline(parentCultureLine);
				plant.setdiseasename(diseaseName);
				plant.setmedicinedetails(medicineDetail);
				plant.setHeight(height);
				plant.setleavesno(leavesNo);
				plant.setstature(stature);
				plant.setbunceweight(bunchWeight);
				plant.setWeight(weight);
				plant.setbunchesno(bunchesNo);
				plant.setraw(row);
				plant.setColumns(column);
				plant.setAD_Org_ID(orgId);
				plant.setTC_Farmer_ID(farmerId);
				plant.settc_species_id(specie_Id);
				plant.setTC_Variety_ID(variety_Id);
				plant.setplanttaguuid(plantTagUUId);
				if (!plant.save()) {
					throw new Exception("Failed to save Plant detail: " + plant);
				}
				trx.commit();
				plant.setName("Plant_" + plant.get_ID());
				plant.saveEx();
				trx.commit();
				int plantId = plant.get_ID();

				TCFirstVisitJoinPlantDetails firstJoinPlant = new TCFirstVisitJoinPlantDetails(ctx, 0,
						trx.getTrxName());
				firstJoinPlant.setAD_Org_ID(orgId);
				firstJoinPlant.setTC_FirstVisit_ID(firstVisitId);
				firstJoinPlant.setTC_PlantDetails_ID(plantId);
				if (!firstJoinPlant.save()) {
					throw new Exception("Failed to save First Visit Join Plant detail: " + firstJoinPlant);
				}
				trx.commit();
				PlantDetails plantDetails = addPlantDetailResponse.addNewPlantDetails();
				plantDetails.setPlantId(plantId);

				if (addImageBase64 != null && addImageBase64.length > 0) {
					boolean hasValidImage = false;
					MAttachment attachment = new MAttachment(ctx, tableId, plantId, trx.getTrxName());
					for (AddImageBase64 images : addImageBase64) {
						if (images != null && images.getImage64() != null && !images.getImage64().isEmpty()) {
							hasValidImage = true;
							String timestamps = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss").format(new Date());
							String imageName = "image" + i + "-" + timestamps + ".png";
							byte[] imagedata = Base64.getDecoder().decode(images.getImage64());
							Boolean success = attachment.addEntry(imageName, imagedata);
							i++;
							if (!success) {
								addPlantDetailResponse.setIsError(true);
								addPlantDetailResponse.setError("Failed to save image: " + imageName);
								return addPlantDetailResponseDocument;
							}
						}
					}
					if (hasValidImage) {
						attachment.saveEx(trx.getTrxName());
						trx.commit();
						plantDetails.setRecordId(attachment.get_ID());
					}
				}
				int retryCount = 0;
				String plantUUId = null;
				while (plantUUId == null && retryCount < 10) {
					plantUUId = DB.getSQLValueString(null,
							"SELECT c_uuid FROM " + TABLE_PLANT_DETAILS + " WHERE " + TABLE_PLANT_DETAILS + "_id = ?", plantId);
					if (plantUUId == null) {
						Thread.sleep(700);
						retryCount++;
					}
				}
				if (plantUUId == null) {
					throw new Exception("Failed to get UUID for Plant Details ID: " + plantId);
				}
				trx.commit();
				plantDetails.setPlantUUId(plantUUId);
			}
			addPlantDetailResponse.setIsError(false);
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			addPlantDetailResponse.setError(INTERNAL_SERVER_ERROR);
			addPlantDetailResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return addPlantDetailResponseDocument;
	}

	@Override
	public GetPlantDetailResponseDocument getPlantDetail(GetPlantDetailRequestDocument req) {
		GetPlantDetailResponseDocument getPlantDetailResponseDocument = GetPlantDetailResponseDocument.Factory
				.newInstance();
		GetPlantDetailResponse getPlantDetailResponse = getPlantDetailResponseDocument.addNewGetPlantDetailResponse();
		GetPlantDetailRequest loginRequest = req.getGetPlantDetailRequest();
		int plantid = 0;
		Trx trx = null;
//		String base64 = "";
		int tableId = MTable.getTable_ID(TABLE_PLANT_DETAILS);
		MAttachment attachment = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
			int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			plantid = loginRequest.getPlantDetailsId();

			String sql = "SELECT pd.planttaguuid AS plantTagUUid,pd.raw as raw,pd.columns as column,pd.tc_plantdetails_id AS ID,ps.codeno AS PlantSpecieCodeNo,v.codeno AS VarietyCodeNO,pd.date AS Date,pd.c_uuid As UUid,pd.isrejected AS rejected,\n"
					+ "pd.bunceweight AS BunchWeight,pd.parentcultureline AS parentcultureline,pd.weight AS Weight,pd.bunchesno AS BunchesNo,pd.diseasename AS DiseaseName,pd.medicinedetails AS MedicineDetails,pd.height AS Height,pd.stature AS Stature,pd.leavesno AS LeavesNo FROM adempiere.tc_plantdetails pd\n"
					+ "JOIN adempiere.tc_plantspecies ps ON ps.tc_plantspecies_id = pd.tc_species_id\n"
					+ "JOIN adempiere.tc_variety v ON v.tc_variety_id = pd.tc_variety_id\n" + "WHERE pd.ad_client_id = ?"
					+ " AND pd.tc_plantdetails_id = ?";
			pstmt = DB.prepareStatement(sql, null);
			pstmt.setInt(1, clientId);
			pstmt.setInt(2, plantid);
			rs = pstmt.executeQuery();

			if (!rs.isBeforeFirst()) {
				getPlantDetailResponse.setIsError(false);
				getPlantDetailResponse.addNewListOfPlantDetails();
				return getPlantDetailResponseDocument;
			}

			while (rs.next()) {
				ListOfPlantDetails listOfPlantDetails = getPlantDetailResponse.addNewListOfPlantDetails();
				String parentcultureline = rs.getString("parentcultureline");
				String cropType = rs.getString("PlantSpecieCodeNo");
				String variety = rs.getString("VarietyCodeNO");
				String date = rs.getString("Date");
				int plantId = rs.getInt("ID");
				String diseaseName = rs.getString("DiseaseName");
				String medicineDetails = rs.getString("MedicineDetails");
				String height = rs.getString("Height");
				String stature = rs.getString("Stature");
				String bunchWeight = rs.getString("BunchWeight");
				String weight = rs.getString("Weight");
				String raw = rs.getString("raw");
				String column = rs.getString("column");
				int leavesNo = rs.getInt("LeavesNo");
				String bunchesNo = rs.getString("BunchesNo");
				String plantTagUUid = rs.getString("plantTagUUid");
				String plantDetailsUUid = rs.getString("UUid");
				boolean rejected = rs.getBoolean("rejected");

				listOfPlantDetails.setCropType(cropType);
				listOfPlantDetails.setVariety(variety);
				listOfPlantDetails.setExpectedDate(date);
				listOfPlantDetails.setDiseaseName(diseaseName != null ? diseaseName : "");
				listOfPlantDetails.setMedicineDetails(medicineDetails != null ? medicineDetails : "");
				listOfPlantDetails.setHeight(height);
				listOfPlantDetails.setStature(stature);
				listOfPlantDetails.setLeavesNo(leavesNo);
				listOfPlantDetails.setBunchWeight(bunchWeight);
				listOfPlantDetails.setWeight(weight);
				listOfPlantDetails.setBunchesNo(bunchesNo);
				listOfPlantDetails.setRow(raw != null ? raw : "");
				listOfPlantDetails.setColumn(column != null ? column : "");
				listOfPlantDetails.setPlantUUId(plantDetailsUUid);
				listOfPlantDetails.setPlantId(plantId);
				listOfPlantDetails.setRejectStatus(rejected);
				listOfPlantDetails.setPlantTagUUId(plantTagUUid != null ? plantTagUUid : "");
				listOfPlantDetails.setParentCultureLine(parentcultureline != null ? parentcultureline : "");

				attachment = MAttachment.get(ctx, tableId, plantId);
				if (attachment != null) {
					MAttachmentEntry[] entries = attachment.getEntries();
//					if (entries.length > 0) {
					for (int i = entries.length - 1; i >= 0; i--) {
//						MAttachmentEntry entry = entries[i];
//						byte[] data = entry.getData();
//						base64 = Base64.getEncoder().encodeToString(data);
						ImageArray imageArray = listOfPlantDetails.addNewImageArray1();
//						imageArray.setImage64(base64);
						imageArray.setImageIndexId(i);
					}
//					} else {
//						listOfPlantDetails.addNewImageArray1();
//					}
				} else
					listOfPlantDetails.addNewImageArray1();
			}
			trx.commit();
			getPlantDetailResponse.setTableId(tableId);
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			getPlantDetailResponse.setError(INTERNAL_SERVER_ERROR);
			getPlantDetailResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstmt, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return getPlantDetailResponseDocument;
	}

	@Override
	public AddCultureOperationResponseDocument addCultureOperation(AddCultureOperationRequestDocument req) {
		AddCultureOperationResponseDocument addCultureOperationResponseDocument = AddCultureOperationResponseDocument.Factory
				.newInstance();
		AddCultureOperationResponse addCultureOperationResponse = addCultureOperationResponseDocument
				.addNewAddCultureOperationResponse();
		AddCultureOperationRequest loginRequest = req.getAddCultureOperationRequest();
		AddCultureDetails addCultureDetails = loginRequest.getAddCultureDetails();
		AddCultureOperation addCultureOperation = loginRequest.getAddCultureOperation();
		String outUUId = safeTrim(loginRequest.getOutUUId());
		String mediaUUId = safeTrim(loginRequest.getMediaLabelUUId());
		int mediaTypeId = 0;
		int cropTypeId = 0;
		int varietyId = 0;
		String parentCultureLine = safeTrim(addCultureDetails.getParentCultureLine());
		String cultureDetailDate = safeTrim(addCultureDetails.getCultureDetailDate());
		int naturesampleId = 0;
		int cultureStageId = 0;
		int cycle = 0;
		int virusResult = 0;
		String tcpf = safeTrim(addCultureOperation.getTCPF());
		String machineCode = safeTrim(addCultureOperation.getMachineCode());
		int machineTypeId = 0;
		String personalCode = safeTrim(addCultureOperation.getPersonalCode());
		String Date = safeTrim(addCultureOperation.getDate());
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		int inId = 0;
		int productId = 0;
		int qty = 1;
		String dates = null;
		String toSubCultureCheck = "";
		int orderId = 0;
		String parentUuid = "";
		BigDecimal quantity = new BigDecimal(qty);
		Trx trx = null;
		PreparedStatement pstmt = null, pstmt1 = null, pstmt2 = null, pstmt3 = null;
		ResultSet rs = null, rs1 = null, rs2 = null, rs3 = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
			int orgId = Env.getAD_Org_ID(ctx);
			int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			mediaTypeId = loginRequest.getMediaTypeId();
			cropTypeId = addCultureDetails.getPlantSpeciesId();
			varietyId = addCultureDetails.getVarietyId();
			naturesampleId = addCultureDetails.getNatureSampleId();
			cultureStageId = addCultureDetails.getCultureStageId();
			cycle = addCultureDetails.getCycle();
			virusResult = addCultureDetails.getVirusTestingId();
			inId = loginRequest.getInId();
			String userName = TCUtills.getUserName(clientId, personalCode);
			if (userName == "") {
				addCultureOperationResponse
						.setError("Personal code is not available for any user, " + personalCode + "");
				addCultureOperationResponse.setIsError(true);
				return addCultureOperationResponseDocument;
			}
			
			if (containsMaliciousPattern(Date)) {
				addCultureOperationResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				addCultureOperationResponse.setIsError(true);
			    return addCultureOperationResponseDocument;
			}
			
			if (containsMaliciousPattern(machineCode)) {
				addCultureOperationResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				addCultureOperationResponse.setIsError(true);
			    return addCultureOperationResponseDocument;
			}
			
			if (containsMaliciousPattern(tcpf)) {
				addCultureOperationResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				addCultureOperationResponse.setIsError(true);
			    return addCultureOperationResponseDocument;
			}
			
			if (containsMaliciousPattern(cultureDetailDate)) {
				addCultureOperationResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				addCultureOperationResponse.setIsError(true);
			    return addCultureOperationResponseDocument;
			}
			
			if (containsMaliciousPattern(parentCultureLine)) {
				addCultureOperationResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				addCultureOperationResponse.setIsError(true);
			    return addCultureOperationResponseDocument;
			}

			int locatorId = TCUtills.getLocatorId(clientId, TABLE_LOCATOR, DEFAULT_LOCATOR_CULTURE);
			if (locatorId == 0) {
				addCultureOperationResponse
						.setError("locator searchKey is not in table record " + DEFAULT_LOCATOR_CULTURE + "");
				addCultureOperationResponse.setIsError(true);
				return addCultureOperationResponseDocument;
			}

			int recordId = TCUtills.getId(TABLE_TC_OUT, outUUId, clientId);
			if (recordId == 0) {
				addCultureOperationResponse.setError("Out Label UUId not found in table record " + outUUId + "");
				addCultureOperationResponse.setIsError(true);
				return addCultureOperationResponseDocument;
			}
			
			if (!TCUtills.isValidNameNumber(parentCultureLine)) {
				addCultureOperationResponse.setError("Invalid Parent Culture Line format");
				addCultureOperationResponse.setIsError(true);
				return addCultureOperationResponseDocument;
	        }
			
			TCOut parentOut = new TCOut(ctx, recordId, trxName);
			int parentLocatorId = parentOut.getM_Locator_ID();
			MLocator parentLocator = new MLocator(ctx, parentLocatorId, trxName);
			String locatorName = parentLocator.getValue();
			
			if (!locatorName.equalsIgnoreCase(DEFAULT_LOCATOR_EXPLANT)) {
				int cultureId = TCUtills.getParentId(TABLE_TC_OUT, outUUId, clientId, TABLE_TC_CULTURE);
				if (cultureId == 0) {
					addCultureOperationResponse.setError("This Culture Label discarded");
					addCultureOperationResponse.setIsError(true);
					return addCultureOperationResponseDocument;
				}
			}

			String sql = "SELECT cl.tc_culturelabel_id,cl.c_uuid AS parentUUid,o.c_uuid,o.tc_out_id FROM adempiere.tc_culturelabel cl\n"
					+ "JOIN adempiere.tc_out o ON o.tc_out_id = cl.tc_out_id\n" + "WHERE o.c_uuid = ?";
			pstmt = DB.prepareStatement(sql, null);
			pstmt.setString(1, outUUId);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				parentUuid = rs.getString("parentUUid");
			}
			DB.close(rs, pstmt);
			pstmt = null;
			rs = null;
			if (parentUuid != "") {

				String sql5 = "select Date(cl.created),cs.period,DATE(cl.created + (cs.period::int * INTERVAL '1 day')) AS expiryDate,cl.tosubculturecheck As culturecheck from adempiere.tc_culturelabel cl\n"
						+ "join adempiere.tc_culturestage cs On cs.tc_culturestage_id = cl.tc_culturestage_id\n"
						+ "where cl.ad_client_id = ? and cl.c_uuid = ?";
				pstmt = DB.prepareStatement(sql5.toString(), null);
				pstmt.setInt(1, clientId);
				pstmt.setString(2, parentUuid);
				rs = pstmt.executeQuery();
				Date date2;
				while (rs.next()) {
					dates = rs.getString("expiryDate");
					toSubCultureCheck = rs.getString("culturecheck");
				}
				Date date1 = new Date();
				SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
				date2 = dateFormat1.parse(dates);
				if (toSubCultureCheck.equalsIgnoreCase("N")) {
					if (date2.compareTo(date1) > 0) {
						addCultureOperationResponse.setError(
								"Created Date and add period not equal current date and To Sub Culture Also not checked");
						addCultureOperationResponse.setIsError(true);
						return addCultureOperationResponseDocument;
					}
				}
			}

			String sql1 = "SELECT cl.tc_explantlabel_id,cl.c_uuid AS parentUUid,o.c_uuid,o.tc_out_id FROM adempiere.tc_explantlabel cl\n"
					+ "JOIN adempiere.tc_out o ON o.tc_out_id = cl.tc_out_id\n" + "WHERE o.c_uuid = ?";
			pstmt1 = DB.prepareStatement(sql1, null);
			pstmt1.setString(1, outUUId);
			rs1 = pstmt1.executeQuery();
			while (rs1.next()) {
				parentUuid = rs1.getString("parentUUid");
			}

			int mediaRecordId = TCUtills.getId(TABLE_MEDIA, mediaUUId, clientId);
			if (mediaRecordId == 0) {
				addCultureOperationResponse.setError("Media Label UUId not found in table record " + mediaUUId + "");
				addCultureOperationResponse.setIsError(true);
				return addCultureOperationResponseDocument;
			}

			int mediaLabelId = TCUtills.getMediaId(TABLE_MEDIA, mediaUUId, clientId);
			if (mediaLabelId == 0) {
				addCultureOperationResponse.setError("This Media Label discarded");
				addCultureOperationResponse.setIsError(true);
				return addCultureOperationResponseDocument;
			}

			TCMediaLabelQr media = new TCMediaLabelQr(ctx, mediaRecordId, trx.getTrxName());
			int mediaLineId = media.getTC_MediaLine_ID();
			TCMediaLine line = new TCMediaLine(ctx, mediaLineId, trx.getTrxName());
			int lineLocator = line.getM_Locator_ID();
			int lineProduct = line.getM_Product_ID();

			Date date1 = (Date) dateFormat.parse(cultureDetailDate);
			long timestampValue1 = date1.getTime();
			Timestamp timestamp1 = new Timestamp(timestampValue1);

			String query = "SELECT tc_machinetype_id AS id,codeNo FROM adempiere.tc_machinetype\n"
					+ "WHERE ad_client_id = ? AND codeNo = ?";
			pstmt2 = DB.prepareStatement(query, null);
			pstmt2.setInt(1, clientId);
			pstmt2.setString(2, machineCode);
			rs2 = pstmt2.executeQuery();

			while (rs2.next()) {
				machineTypeId = rs2.getInt("id");
			}
			if (machineTypeId == 0) {
				addCultureOperationResponse
						.setError("Machine code is not Available in our Tc_MachineType Table records, MachineCode: "
								+ machineCode);
				addCultureOperationResponse.setIsError(true);
				return addCultureOperationResponseDocument;
			}

			MTable t2 = MTable.get(ctx, "TC_culturestage");
			PO cul = t2.getPO(cultureStageId, trx.getTrxName());
			if (cul == null) {
				addCultureOperationResponse.setError("Culture Stage not found for " + cultureStageId + "");
				addCultureOperationResponse.setIsError(true);
				return addCultureOperationResponseDocument;
			}
			TCCultureStage stage = (TCCultureStage) cul;
			String cultureName = stage.getName();

			Date date = (Date) dateFormat.parse(Date);
			long timestampValue = date.getTime();
			Timestamp timestamp = new Timestamp(timestampValue);

			String sql2 = "SELECT value,m_product_id FROM adempiere.m_product\n" + "WHERE ad_client_id = ?"
					+ "\n" + "AND value LIKE ? limit 1";

			pstmt3 = DB.prepareStatement(sql2, null);
			pstmt3.setInt(1, clientId);
			pstmt3.setString(2, "%" + cultureName + " " + cycle + "%");
			rs3 = pstmt3.executeQuery();

			while (rs3.next()) {
				productId = rs3.getInt("m_product_id");
			}
			TCIn in = new TCIn(ctx, inId, trx.getTrxName());
			orderId = in.getTC_order_ID();

			TCOut out = new TCOut(ctx, 0, trx.getTrxName());
			out.setTC_order_ID(orderId);
			out.setAD_Org_ID(orgId);
			out.setTC_in_ID(inId);
			out.setcycle(cycle);
			out.setM_Locator_ID(locatorId);
			out.setM_Product_ID(productId);
			out.setQuantity(quantity);
			out.setC_UOM_ID(UOM_ID);
			if (!out.save()) {
				throw new Exception("Failed to add out data: " + out);
			}
			trx.commit();
			int outId = out.get_ID();

			TCMediaOutLine outLine = new TCMediaOutLine(ctx, 0, trx.getTrxName());
			outLine.setTC_order_ID(orderId);
			outLine.setAD_Org_ID(orgId);
			outLine.setTC_out_ID(outId);
			outLine.setTC_MediaLine_ID(mediaLineId);
			outLine.setQuantity(quantity);
			outLine.setM_Locator_ID(lineLocator);
			outLine.setM_Product_ID(lineProduct);
			outLine.setC_UOM_ID(UOM_ID);

			if (!outLine.save()) {
				throw new Exception("Failed to add out line data: " + outLine);
			}
			trx.commit();

			TCCultureLabel label = new TCCultureLabel(ctx, 0, trx.getTrxName());
			label.setAD_Org_ID(orgId);
			label.settc_species_id(cropTypeId);
			label.setTC_Variety_ID(varietyId);
			label.setparentcultureline(parentCultureLine);
			label.setculturedate(timestamp1);
			label.setTC_NatureSample_ID(naturesampleId);
			label.setTC_CultureStage_ID(cultureStageId);
			label.setcycleno(cycle);
			label.setTC_VirusTesting_ID(virusResult);
			label.setTC_MediaType_ID(mediaTypeId);
			label.settcpf(tcpf);
			label.setcultureoperationdate(timestamp);
			label.setpersonal_code(personalCode);
			label.setTC_MachineType_ID(machineTypeId);
			label.setTC_in_ID(inId);
			label.setTC_out_ID(outId);
			label.setparentuuid(parentUuid);
			if (!label.save()) {
				throw new Exception("Failed to add Culture Label data: " + label);
			}
			trx.commit();
			int cultureLabelId = label.get_ID();

			int retryCount = 0;
			String cultureUUId = null;
			String outUUIdG = null;
			while (cultureUUId == null && retryCount < 10) {
				cultureUUId = DB.getSQLValueString(null,
						"SELECT c_uuid FROM " + TABLE_TC_CULTURE + " WHERE " + TABLE_TC_CULTURE + "_id = ?", cultureLabelId);
				outUUIdG = DB.getSQLValueString(null,
						"SELECT c_uuid FROM " + TABLE_TC_OUT + " WHERE " + TABLE_TC_OUT + "_id = ?", outId);
				if (cultureUUId == null && outUUIdG == null) {
					Thread.sleep(900); // Wait for 500ms before retrying
					retryCount++;
				}
			}
			if (cultureUUId == null) {
				throw new Exception("Failed to get UUID for TC Order ID: " + cultureUUId);
			}
			trx.commit();
			addCultureOperationResponse.setOutUUId(outUUIdG);
			addCultureOperationResponse.setCultureLabelUUId(cultureUUId);
			addCultureOperationResponse.setCultureLabelId(cultureLabelId);
			addCultureOperationResponse.setIsError(false);

		} catch (Exception e) {
			if (trx != null) trx.rollback();
			addCultureOperationResponse.setError(INTERNAL_SERVER_ERROR);
			addCultureOperationResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstmt, rs);
			closeDbCon(pstmt1, rs1);
			closeDbCon(pstmt2, rs2);
			closeDbCon(pstmt3, rs3);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return addCultureOperationResponseDocument;
	}

	@Override
	public AddOrderLabelResponseDocument addOrderlabel(AddOrderLabelRequestDocument req) {
		AddOrderLabelResponseDocument addOrderLabelResponseDocument = AddOrderLabelResponseDocument.Factory
				.newInstance();
		AddOrderLabelResponse addOrderLabelResponse = addOrderLabelResponseDocument.addNewAddOrderLabelResponse();
		Trx trx = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
			
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			
//			String sqlUpdateInvoices = "UPDATE adempiere.c_invoice SET docstatus = 'DR', docaction = 'CO', processed = 'N' WHERE AD_Client_ID = ?";
//			String sqlDeleteInvoiceLines = "DELETE FROM adempiere.c_invoiceline WHERE c_invoice_id IN (SELECT c_invoice_id FROM adempiere.c_invoice WHERE AD_Client_ID = ?)";
//			String sqlDeleteAllocationLines = "DELETE FROM adempiere.c_allocationline WHERE c_invoice_id IN (SELECT c_invoice_id FROM adempiere.c_invoice WHERE AD_Client_ID = ?)";
//			String sqlDeleteInvoiceTaxes = "DELETE FROM adempiere.c_invoicetax WHERE c_invoice_id IN (SELECT c_invoice_id FROM adempiere.c_invoice WHERE AD_Client_ID = ?)";
//			String sqlUpdatePayments = "UPDATE adempiere.c_invoice SET c_payment_id = NULL WHERE AD_Client_ID = ?";
//			String sqlDeletePayments = "DELETE FROM adempiere.c_payment WHERE c_invoice_id IN (SELECT c_invoice_id FROM adempiere.c_invoice WHERE AD_Client_ID = ?)";
//			String sqlDeleteInvoices = "DELETE FROM adempiere.c_invoice WHERE AD_Client_ID = ?";
//
//			DB.executeUpdateEx(sqlUpdateInvoices, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeleteInvoiceLines, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeleteAllocationLines, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeleteInvoiceTaxes, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlUpdatePayments, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeletePayments, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeleteInvoices, new Object[] { client_id }, trxName);
//			trx.commit();
//
//			String sqlUpdateInOut = "UPDATE adempiere.m_inout SET docstatus = 'DR', docaction = 'CO', processed = 'N' WHERE AD_Client_ID = ?";
//			String sqlDeleteTransactions = "DELETE FROM adempiere.m_transaction WHERE m_inoutline_id IN (SELECT m_inoutline_id FROM adempiere.m_inoutline WHERE m_inout_id IN (SELECT m_inout_id FROM adempiere.m_inout WHERE AD_Client_ID = ?))";
//			String sqlDeleteInOutLineMA = "DELETE FROM adempiere.m_inoutlinema WHERE m_inoutline_id IN (SELECT m_inoutline_id FROM adempiere.m_inoutline WHERE m_inout_id IN (SELECT m_inout_id FROM adempiere.m_inout WHERE AD_Client_ID = ?))";
//			String sqlDeleteInOutLine = "DELETE FROM adempiere.m_inoutline WHERE m_inout_id IN (SELECT m_inout_id FROM adempiere.m_inout WHERE AD_Client_ID = ?)";
//			String sqlDeleteInOut = "DELETE FROM adempiere.m_inout WHERE AD_Client_ID = ?";
//
//			DB.executeUpdateEx(sqlUpdateInOut, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeleteTransactions, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeleteInOutLineMA, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeleteInOutLine, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeleteInOut, new Object[] { client_id }, trxName);
//			trx.commit();
//
//			String sqlUpdateOrders = "UPDATE adempiere.c_order SET docstatus = 'DR', docaction = 'CO', processed = 'N' WHERE AD_Client_ID = ?";
//			String sqlDeleteOrderLines = "DELETE FROM adempiere.c_orderline WHERE c_order_id IN (SELECT c_order_id FROM adempiere.c_order WHERE AD_Client_ID = ?)";
//			String sqlDeleteOrderTax = "DELETE FROM adempiere.c_ordertax WHERE c_order_id IN (SELECT c_order_id FROM adempiere.c_order WHERE AD_Client_ID = ?)";
//			String sqlDeleteOrders = "DELETE FROM adempiere.c_order WHERE AD_Client_ID = ?";
//
//			DB.executeUpdateEx(sqlUpdateOrders, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeleteOrderLines, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeleteOrderTax, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeleteOrders, new Object[] { client_id }, trxName);
//			trx.commit();
//
//			String sqlDeleteSecondaryHardeningLabel = "DELETE FROM adempiere.tc_secondaryhardeningLabel WHERE AD_Client_ID = ?";
//			String sqlDeletePrimaryHardeningCultureS = "DELETE FROM adempiere.tc_primaryHardeningcultureS WHERE AD_Client_ID = ?";
//			String sqlDeletePrimaryHardeningLabel = "DELETE FROM adempiere.tc_primaryhardeningLabel WHERE AD_Client_ID = ?";
//			String sqlDeleteCultureLabel = "DELETE FROM adempiere.tc_culturelabel WHERE AD_Client_ID = ?";
//			String sqlDeleteExplantLabel = "DELETE FROM adempiere.tc_explantlabel WHERE AD_Client_ID = ?";
//			String sqlDeletePlantTag = "DELETE FROM adempiere.tc_planttag WHERE AD_Client_ID = ?";
//
//			DB.executeUpdateEx(sqlDeleteSecondaryHardeningLabel, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeletePrimaryHardeningCultureS, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeletePrimaryHardeningLabel, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeleteCultureLabel, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeleteExplantLabel, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeletePlantTag, new Object[] { client_id }, trxName);
//			trx.commit();
//
//			String sqlUpdateOrder = "UPDATE adempiere.TC_Order SET docstatus = 'DR', docaction = 'CO', processed = 'N' WHERE AD_Client_ID = ?";
//			String sqlDeleteMediaOutline = "DELETE FROM adempiere.tc_mediaoutline WHERE TC_order_id IN (SELECT TC_order_id FROM adempiere.TC_Order WHERE AD_Client_ID = ?)";
//			String sqlDeleteOut = "DELETE FROM adempiere.tc_out WHERE TC_order_id IN (SELECT TC_order_id FROM adempiere.TC_Order WHERE AD_Client_ID = ?)";
//			String sqlDeleteIn = "DELETE FROM adempiere.tc_in WHERE TC_order_id IN (SELECT TC_order_id FROM adempiere.TC_Order WHERE AD_Client_ID = ?)";
//			String sqlDeleteOrder = "DELETE FROM adempiere.TC_Order WHERE AD_Client_ID = ?";
//
//			DB.executeUpdateEx(sqlUpdateOrder, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeleteMediaOutline, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeleteOut, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeleteIn, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeleteOrder, new Object[] { client_id }, trxName);
//			trx.commit();
//
//			String sqlDeleteCollectionJoinPlant = "DELETE FROM adempiere.tc_collectionjoinplant WHERE tc_collectiondetails_id IN (SELECT tc_collectiondetails_id FROM adempiere.tc_collectiondetails WHERE AD_Client_ID = ?)";
//			String sqlDeleteCollectionDetails = "DELETE FROM adempiere.tc_collectiondetails WHERE AD_Client_ID = ?";
//
//			String sqlDeleteIntermediateJoinPlant = "DELETE FROM adempiere.tc_intermediatejoinplant WHERE tc_intermediatevisit_id IN (SELECT tc_intermediatevisit_id FROM adempiere.tc_intermediatevisit WHERE AD_Client_ID = ?)";
//			String sqlDeleteIntermediateVisit = "DELETE FROM adempiere.tc_intermediatevisit WHERE AD_Client_ID = ?";
//
//			String sqlDeleteFirstJoinPlant = "DELETE FROM adempiere.tc_firstjoinplant WHERE tc_firstvisit_id IN (SELECT tc_firstvisit_id FROM adempiere.tc_firstvisit WHERE AD_Client_ID = ?)";
//			String sqlDeleteFirstVisit = "DELETE FROM adempiere.tc_firstvisit WHERE AD_Client_ID = ?";
//
//			String sqlDeleteVisit = "DELETE FROM adempiere.TC_visit WHERE AD_Client_ID = ?";
//			String sqlDeletePlantDetails = "DELETE FROM adempiere.tc_plantdetails WHERE AD_Client_ID = ?";
//			String sqlDeleteFarmer = "DELETE FROM adempiere.TC_farmer WHERE AD_Client_ID = ?";
//
//			DB.executeUpdateEx(sqlDeleteCollectionJoinPlant, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeleteCollectionDetails, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeleteIntermediateJoinPlant, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeleteIntermediateVisit, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeleteFirstJoinPlant, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeleteFirstVisit, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeleteVisit, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeletePlantDetails, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeleteFarmer, new Object[] { client_id }, trxName);
//			trx.commit();
//
//			String sqlDeleteMediaLabelQR = "DELETE FROM adempiere.TC_medialabelqr WHERE AD_Client_ID = ?";
//			String sqlUpdateMediaOrder = "UPDATE adempiere.TC_MediaOrder SET docstatus = 'DR', docaction = 'CO', processed = 'N' WHERE AD_Client_ID = ?";
//			String sqlDeleteMediaLine = "DELETE FROM adempiere.TC_MediaLine WHERE TC_MediaOrder_id IN (SELECT TC_MediaOrder_id FROM adempiere.TC_MediaOrder WHERE AD_Client_ID = ?)";
//			String sqlDeleteMediaOrder = "DELETE FROM adempiere.TC_MediaOrder WHERE AD_Client_ID = ?";
//
//			DB.executeUpdateEx(sqlDeleteMediaLabelQR, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlUpdateMediaOrder, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeleteMediaLine, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeleteMediaOrder, new Object[] { client_id }, trxName);
//			trx.commit();
//
//			String sqlDeleteTransaction = "DELETE FROM adempiere.M_Transaction WHERE AD_Client_ID = ?";
//			String sqlDeleteStorage = "DELETE FROM adempiere.M_StorageOnHand WHERE AD_Client_ID = ?";
//
//			DB.executeUpdateEx(sqlDeleteTransaction, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeleteStorage, new Object[] { client_id }, trxName);
//			trx.commit();
//
//			String sqlDeleteMovementLine = "DELETE FROM adempiere.M_MovementLine WHERE M_Movement_ID IN (SELECT M_Movement_ID FROM adempiere.M_Movement WHERE AD_Client_ID = ?)";
//			String sqlDeleteMovement = "DELETE FROM adempiere.M_Movement WHERE AD_Client_ID = ?";
//
//			DB.executeUpdateEx(sqlDeleteMovementLine, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeleteMovement, new Object[] { client_id }, trxName);
//			trx.commit();
//
//			String sqlDeleteTemperatureStatus = "DELETE FROM adempiere.tc_temperatureStatus WHERE AD_Client_ID = ?";
//			String sqlDeleteLight = "DELETE FROM adempiere.tc_light WHERE AD_Client_ID = ?";
////			String sqlDeleteDeviceData = "DELETE FROM adempiere.TC_devicedata WHERE AD_Client_ID = ?";
//			DB.executeUpdateEx(sqlDeleteTemperatureStatus, new Object[] { client_id }, trxName);
//			DB.executeUpdateEx(sqlDeleteLight, new Object[] { client_id }, trxName);
////			DB.executeUpdateEx(sqlDeleteDeviceData, new Object[] { client_id }, trxName);
//			trx.commit();

			addOrderLabelResponse.setIsError(false);
//			addOrderLabelResponse.setError("Deleted Successfully");
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			e.printStackTrace();
			addOrderLabelResponse.setError(INTERNAL_SERVER_ERROR);
			addOrderLabelResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return addOrderLabelResponseDocument;
	}

	private static String generateToken() {
		SecureRandom random = new SecureRandom();
		int number = random.nextInt(900000) + 100000; // Generates a number between 100000 and 999999
		return String.valueOf(number);
	}

	@Override
	public GetCultureLabelResponseDocument getCultureLabel(GetCultureLabelRequestDocument req) {
		GetCultureLabelResponseDocument getCultureLabelResponseDocument = GetCultureLabelResponseDocument.Factory
				.newInstance();
		GetCultureLabelResponse getCultureLabelResponse = getCultureLabelResponseDocument
				.addNewGetCultureLabelResponse();
		GetCultureLabelRequest loginRequest = req.getGetCultureLabelRequest();
		String outLabelUUId = safeTrim(loginRequest.getOutLabelUUId());
		Trx trx = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
		int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			int recordId = TCUtills.getId(TABLE_TC_OUT, outLabelUUId, clientId);
			if (recordId == 0) {
				getCultureLabelResponse
						.setError("Culture Label Out UUId not found in table record ");
				getCultureLabelResponse.setIsError(true);
				return getCultureLabelResponseDocument;
			}

			String sql = "SELECT cl.tc_CultureLabel_id,cl.c_uuid AS UUId,cl.parentcultureline AS parentCultureLine,cl.cycleno AS cycleNo,cl.tcpf AS TCPF,cl.personal_code AS personalCode,\n"
					+ "ps.codeno AS cropType,cl.tosubculturecheck AS tosubculturecheck,v.codeno AS Variety,ns.codeno AS natureSample,cs.codeno AS cultureStage,vt.codeno AS virusResult,mt.name AS mediaType,mat.codeNo AS machineName,cl.tc_out_id AS outId,o.c_uuid AS outUUid,\n"
					+ "cl.culturedate AS cultureDate,cl.isdiscarded As discard,cl.cultureoperationdate AS cultureOperationDate FROM adempiere.tc_cultureLabel cl\n"
					+ "JOIN adempiere.tc_plantspecies ps ON ps.tc_plantspecies_id = cl.tc_species_id\n"
					+ "JOIN adempiere.tc_variety v ON v.tc_variety_id = cl.tc_variety_id\n"
					+ "JOIN adempiere.tc_naturesample ns ON ns.tc_naturesample_id = cl.tc_naturesample_id\n"
					+ "JOIN adempiere.tc_culturestage cs ON cs.tc_culturestage_id = cl.tc_culturestage_id\n"
					+ "JOIN adempiere.tc_virustesting vt ON vt.tc_virustesting_id = cl.tc_virustesting_id\n"
					+ "JOIN adempiere.tc_mediatype mt ON mt.tc_mediatype_id = cl.tc_mediatype_id\n"
					+ "JOIN adempiere.tc_out o ON o.tc_out_id = cl.tc_out_id\n"
					+ "JOIN adempiere.tc_machinetype mat ON mat.tc_machinetype_id = cl.tc_machinetype_id\n"
					+ "WHERE cl.ad_client_id = ? AND o.c_uuid = ?";
			pstmt = DB.prepareStatement(sql, null);
			pstmt.setInt(1, clientId);
			pstmt.setString(2, outLabelUUId);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				String discardValue = rs.getString("discard");
				String uuId = rs.getString("UUId");
				if ("Y".equalsIgnoreCase(discardValue)) {
					getCultureLabelResponse.setError("This Culture Label has been Discarded : " + uuId + "");
					getCultureLabelResponse.setIsError(true);
					return getCultureLabelResponseDocument; // Exit the method early if discard is "Y"
				}

				GetCultureLabelData data = getCultureLabelResponse.addNewGetCultureLabelData();
				String parentCultureLine = rs.getString("parentCultureLine");
				String cycleNo = rs.getString("cycleNo");
				String tcpf = rs.getString("TCPF");
				String personalCode = rs.getString("personalCode");
				String cropType = rs.getString("cropType");
				String variety = rs.getString("Variety");
				String natureSample = rs.getString("natureSample");
				String cultureStage = rs.getString("cultureStage");
				String virusResult = rs.getString("virusResult");
				String mediaType = rs.getString("mediaType");
				String machineName = rs.getString("machineName");
				String cultureDate = rs.getString("cultureDate");
				String cultureOperationDate = rs.getString("cultureOperationDate");
				Boolean tosubculturecheck = rs.getBoolean("tosubculturecheck");
				int outId = rs.getInt("outId");
				String outUUid = rs.getString("outUUid");
				System.out.println(tosubculturecheck);

				data.setCultureLabelUUID(uuId);
				data.setCropType(cropType);
				data.setVariety(variety);
				data.setParentCultureLine(parentCultureLine);
				data.setCultureDetailDate(cultureDate);
				data.setNatureSample(natureSample);
				data.setCultureStage(cultureStage);
				data.setCycleNo(cycleNo);
				data.setVirusTesting(virusResult);
				data.setMediaType(mediaType);
				data.setTCPF(tcpf);
				data.setCultureOperationDate(cultureOperationDate);
				data.setMachineName(machineName);
				data.setPersonalCode(personalCode);
				data.setTcOutId(outId);
				data.setOutLabelUUID(outUUid);
				data.setSubCultureCheck(tosubculturecheck);
			}
			closeDbCon(pstmt, rs);
			pstmt = null;
			rs = null;

			String sql2 = "SELECT i.tc_in_id As id,i.tc_order_id As orderId FROM adempiere.tc_culturelabel c\n"
					+ "JOIN adempiere.tc_out o ON o.tc_out_id = c.tc_out_id\n"
					+ "JOIN adempiere.tc_in i ON i.parentuuid = o.c_uuid\n" + "WHERE c.ad_client_id = ?"
					+ " AND i.parentuuid = ? ORDER BY id DESC";
			pstmt = DB.prepareStatement(sql2, null);
			pstmt.setInt(1, clientId);
			pstmt.setString(2, outLabelUUId);
			rs = pstmt.executeQuery();

			if (!rs.isBeforeFirst()) {
				getCultureLabelResponse.addNewGetInIdForCulture();
				getCultureLabelResponse.setIsError(false);
				return getCultureLabelResponseDocument;
			}

			while (rs.next()) {
				GetInIdForCulture inData = getCultureLabelResponse.addNewGetInIdForCulture();
				int inId = rs.getInt("id");
				int orderId = rs.getInt("orderId");
				inData.setInID(inId);
				inData.setOrderId(orderId);
			}
			trx.commit();
			getCultureLabelResponse.setIsError(false);
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			getCultureLabelResponse.setError(INTERNAL_SERVER_ERROR);
			getCultureLabelResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstmt, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return getCultureLabelResponseDocument;
	}

	@Override
	public InternalMoveQuantityResponseDocument internalMoveQuantity(InternalMoveQuantityRequestDocument req) {
		InternalMoveQuantityResponseDocument internalMoveQuantityResponseDocument = InternalMoveQuantityResponseDocument.Factory
				.newInstance();
		InternalMoveQuantityResponse internalMoveQuantityResponse = internalMoveQuantityResponseDocument
				.addNewInternalMoveQuantityResponse();
		InternalMoveQuantityRequest loginRequest = req.getInternalMoveQuantityRequest();
		String outLabelUUId = safeTrim(loginRequest.getOutLabelUUId());
		int room = 0;
		String rack = safeTrim(loginRequest.getRackNumber());
		String column = safeTrim(loginRequest.getColumnNumber());
		int warehouseId = 0;
		int documentType = 0;
		int toLocatorId = 0;
		Date currentDate = new Date();
		Timestamp currentTimestamp = new Timestamp(currentDate.getTime());
		PreparedStatement pstmt = null, pstmt1 = null;
		ResultSet rs = null, rs1 = null;
		Trx trx = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
		int clientId = Env.getAD_Client_ID(ctx);
		int orgId = Env.getAD_Org_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			warehouseId = Env.getContextAsInt(ctx, Env.M_WAREHOUSE_ID);
			room = loginRequest.getGrowthRoomNumber();
			documentType = TCUtills.getDocTypeId(clientId, DOCUMENT_MATERIAL_MOVEMENT);
			int recordId = TCUtills.getId(TABLE_TC_OUT, outLabelUUId, clientId);
			if (recordId == 0) {
				internalMoveQuantityResponse
						.setError("Culture Label Out UUId not found in table record " + outLabelUUId + "");
				internalMoveQuantityResponse.setIsError(true);
				return internalMoveQuantityResponseDocument;
			}
			
			if (containsMaliciousPattern(rack)) {
				internalMoveQuantityResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				internalMoveQuantityResponse.setIsError(true);
			    return internalMoveQuantityResponseDocument;
			}
			
			if (containsMaliciousPattern(column)) {
				internalMoveQuantityResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				internalMoveQuantityResponse.setIsError(true);
			    return internalMoveQuantityResponseDocument;
			}
			
			TCOut out = new TCOut(ctx, recordId, trx.getTrxName());
			int fromLocatorId = out.getM_Locator_ID();
			BigDecimal qty = out.getQuantity();
			int productId = out.getM_Product_ID();
			MTable roomTable = MTable.get(ctx, "m_locatortype");
			PO po1 = roomTable.getPO(room, trx.getTrxName());
			if (po1 == null) {
				internalMoveQuantityResponse.setError("Room not found " + room + "");
				internalMoveQuantityResponse.setIsError(true);
				return internalMoveQuantityResponseDocument;
			}
			MLocatorType type = (MLocatorType) po1;
			String roomName = type.getName();

			String sql = "SELECT *,value,m_locator_id FROM adempiere.m_locator WHERE ad_client_id = ?\n"
					+ "AND x=? AND y=? AND z=?";
			pstmt = DB.prepareStatement(sql, null);
			pstmt.setInt(1, clientId);
			pstmt.setString(2, roomName);
			pstmt.setString(3, rack);
			pstmt.setString(4, column);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				toLocatorId = rs.getInt("m_locator_id");
			}
			MMovement move = new MMovement(ctx, 0, trx.getTrxName());
			move.setAD_Org_ID(orgId);
			move.setDateReceived(currentTimestamp);
			move.setC_DocType_ID(documentType);
			move.setM_Warehouse_ID(warehouseId);
			move.setM_WarehouseTo_ID(warehouseId);
			if (!move.save()) {
				throw new Exception("Failed to add Internal move data: " + move);
			}
			trx.commit();
			int inventoryMoveId = move.get_ID();

			moveInventory(ctx, trxName, move, qty, productId, fromLocatorId, toLocatorId);

			move.saveEx();
			move.setDocStatus(DocAction.ACTION_Complete);
			move.setDocAction(DocAction.ACTION_Close);
			move.setPosted(true);
			move.setProcessed(true);
			move.setIsApproved(true);
			move.completeIt();
			move.saveEx();
			internalMoveQuantityResponse.setIsError(false);
			internalMoveQuantityResponse.setInventoryMoveId(inventoryMoveId);

			out.setM_Locator_ID(toLocatorId);
			out.saveEx();
			trx.commit();

		} catch (Exception e) {
			if (trx != null) trx.rollback();
			internalMoveQuantityResponse.setError(INTERNAL_SERVER_ERROR);
			internalMoveQuantityResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstmt, rs);
			closeDbCon(pstmt1, rs1);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return internalMoveQuantityResponseDocument;
	}

	private void moveInventory(Properties ctx, String trxName, MMovement move,
			BigDecimal quantity, int productId, int fromLocatorId, int toLocatorId) throws Exception {
		Trx trx = null;
		trx = Trx.get(trxName, true);
		trx.start();
		MMovementLine line = new MMovementLine(ctx, 0, trxName);
		line.setAD_Org_ID(Env.getAD_Org_ID(ctx));
		line.setM_Movement_ID(move.get_ID());
		line.setM_Product_ID(productId);
		line.setM_Locator_ID(fromLocatorId);
		line.setM_LocatorTo_ID(toLocatorId);
		line.setMovementQty(quantity);
		line.setLine(10);
		line.setDescription(null);
		line.setProcessed(true);

		if (!line.save()) {
			throw new Exception("Failed to add Internal move line data: " + line);
		}
		trx.commit();

		MTransaction mTransactionOut = new MTransaction(ctx, 0, trxName);
		mTransactionOut.setIsActive(true);
		mTransactionOut.setMovementType("M-");
		mTransactionOut.setM_Locator_ID(fromLocatorId);
		mTransactionOut.setM_Product_ID(productId);
		mTransactionOut.setMovementQty(quantity.negate());
		mTransactionOut.setM_MovementLine_ID(line.get_ID());
		mTransactionOut.setM_AttributeSetInstance_ID(line.getM_AttributeSetInstance_ID());
		mTransactionOut.saveEx();
		trx.commit();

		MTransaction mTransactionIn = new MTransaction(ctx, 0, trxName);
		mTransactionIn.setIsActive(true);
		mTransactionIn.setMovementType("M+");
		mTransactionIn.setM_Locator_ID(toLocatorId);
		mTransactionIn.setM_Product_ID(productId);
		mTransactionIn.setMovementQty(quantity);
		mTransactionIn.setM_MovementLine_ID(line.get_ID());
		mTransactionIn.setM_AttributeSetInstance_ID(line.getM_AttributeSetInstance_ID());
		mTransactionIn.saveEx();
		trx.commit();

		MAcctSchema[] mAcctSchemaArray = MAcctSchema.getClientAcctSchema(ctx, Env.getAD_Client_ID(ctx));
		MAcctSchema acctSchema = mAcctSchemaArray[0];

		List<PO> list = new Query(ctx, MElementValue.Table_Name, "name=? AND ad_client_id=?", trxName)
				.setParameters("Product asset", Env.getAD_Client_ID(ctx))
				.setOrderBy(MElementValue.COLUMNNAME_C_ElementValue_ID).list();
		MElementValue mElementValue = (MElementValue) list.get(0);
		int mElementValueId = mElementValue.get_ID();

		MPeriod mPeriod = MPeriod.get(ctx, move.getMovementDate(), Env.getAD_Org_ID(ctx), trxName);

		MGLCategory mGLCategory = MGLCategory.getDefault(ctx, "M");

		MFactAcct mFactAcct = new MFactAcct(ctx, 0, trxName);
		mFactAcct.setIsActive(true);
		mFactAcct.setC_AcctSchema_ID(acctSchema.get_ID());
		mFactAcct.setAccount_ID(mElementValueId);
		mFactAcct.setDateTrx(move.getMovementDate());
		mFactAcct.setDateAcct(move.getMovementDate());
		mFactAcct.setC_Period_ID(mPeriod.get_ID());
		mFactAcct.setAD_Table_ID(move.get_Table_ID());
		mFactAcct.setRecord_ID(move.get_ID());
		mFactAcct.setLine_ID(move.get_ID());
		mFactAcct.setGL_Category_ID(mGLCategory.get_ID());
		mFactAcct.setM_Locator_ID(fromLocatorId);
		mFactAcct.setPostingType("A");
		mFactAcct.setC_Currency_ID(acctSchema.getC_Currency_ID());
		mFactAcct.setAmtSourceDr(BigDecimal.valueOf(0));
		mFactAcct.setAmtSourceCr(BigDecimal.valueOf(0));
		mFactAcct.setAmtAcctCr(BigDecimal.valueOf(0));
		mFactAcct.setAmtAcctDr(BigDecimal.valueOf(0));
		mFactAcct.setC_UOM_ID(UOM_ID);
		mFactAcct.setQty(quantity);
		mFactAcct.setM_Product_ID(productId);
		mFactAcct.setDescription(move.getDocumentNo() + "#" + line.getLine());
		mFactAcct.saveEx();
		trx.commit();

		MFactAcct mFactAcctIn = new MFactAcct(ctx, 0, trxName);
		mFactAcctIn.setIsActive(true);
		mFactAcctIn.setC_AcctSchema_ID(acctSchema.get_ID());
		mFactAcctIn.setAccount_ID(mElementValueId);
		mFactAcctIn.setDateTrx(move.getMovementDate());
		mFactAcctIn.setDateAcct(move.getMovementDate());
		mFactAcctIn.setC_Period_ID(mPeriod.get_ID());
		mFactAcctIn.setAD_Table_ID(move.get_Table_ID());
		mFactAcctIn.setRecord_ID(move.get_ID());
		mFactAcctIn.setLine_ID(move.get_ID());
		mFactAcctIn.setGL_Category_ID(mGLCategory.get_ID());
		mFactAcctIn.setM_Locator_ID(toLocatorId);
		mFactAcctIn.setPostingType("A");
		mFactAcctIn.setC_Currency_ID(acctSchema.getC_Currency_ID());
		mFactAcctIn.setAmtSourceCr(BigDecimal.valueOf(0));
		mFactAcctIn.setAmtSourceDr(BigDecimal.valueOf(0));
		mFactAcctIn.setAmtAcctDr(BigDecimal.valueOf(0));
		mFactAcctIn.setAmtAcctCr(BigDecimal.valueOf(0));
		mFactAcctIn.setC_UOM_ID(UOM_ID);
		mFactAcctIn.setQty(quantity);
		mFactAcctIn.setM_Product_ID(productId);
		mFactAcctIn.setDescription(move.getDocumentNo() + "#" + line.getLine());
		mFactAcctIn.saveEx();
		trx.commit();
	}

	@Override
	public AddMediaLabelResponseDocument addMediaLabel(AddMediaLabelRequestDocument req) {
		AddMediaLabelResponseDocument addMediumLabelResponseDocument = AddMediaLabelResponseDocument.Factory
				.newInstance();
		AddMediaLabelResponse addMediumLabelResponse = addMediumLabelResponseDocument.addNewAddMediaLabelResponse();
		AddMediaLabelRequest loginRequest = req.getAddMediaLabelRequest();
		AddMediaDetails addMediaDetails = loginRequest.getAddMediaDetails();
		int mediaOrderId = 0;
		int mediaTypeId = 0;
		String tcpf = addMediaDetails.getTCPF();
		String machineCode = addMediaDetails.getMachineCode();
		int machineTypeId = 0;
		String personalCode = addMediaDetails.getPersonalCode();
		String date = addMediaDetails.getOperationDate();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		int productId = 0;
		int qty = 0;
		int mediaTotalQty = 0,mediaLineTotalQty = 0;
		Trx trx = null;
		PreparedStatement pstmt = null, pstmt1 = null;
		ResultSet rs = null, rs1 = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
			int orgId = Env.getAD_Org_ID(ctx);
			int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			String userName = TCUtills.getUserName(clientId, personalCode);
			if (userName == "") {
				addMediumLabelResponse.setError("Personal code is not available for any user, " + personalCode + "");
				addMediumLabelResponse.setIsError(true);
				return addMediumLabelResponseDocument;
			}
			mediaOrderId = loginRequest.getMediaOrderId();
			mediaTypeId = addMediaDetails.getMediaTypeId();
			qty = addMediaDetails.getLabelQuantity();
			BigDecimal quantity = new BigDecimal(qty);
			int locatorId = TCUtills.getLocatorId(clientId, TABLE_LOCATOR, DEFAULT_LOCATOR_MEDIA);
			if (locatorId == 0) {
				addMediumLabelResponse.setError("locator searchKey is not in table record " + DEFAULT_LOCATOR_MEDIA + "");
				addMediumLabelResponse.setIsError(true);
				return addMediumLabelResponseDocument;
			}
			
			if (containsMaliciousPattern(tcpf)) {
				addMediumLabelResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				addMediumLabelResponse.setIsError(true);
			    return addMediumLabelResponseDocument;
			}
			
			if (containsMaliciousPattern(machineCode)) {
				addMediumLabelResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				addMediumLabelResponse.setIsError(true);
			    return addMediumLabelResponseDocument;
			}
			
			if (containsMaliciousPattern(date)) {
				addMediumLabelResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				addMediumLabelResponse.setIsError(true);
			    return addMediumLabelResponseDocument;
			}
			
			TCMediaOrder mediaOrder = new TCMediaOrder(ctx, mediaOrderId, trxName);
			mediaTotalQty = mediaOrder.getQuantity().intValue();
			
			String sql1 = "SELECT COALESCE(SUM(Quantity), 0) FROM adempiere.tc_medialine WHERE tc_mediaorder_id = ?";
			
			BigDecimal childTotalQty = DB.getSQLValueBD(trxName, sql1, mediaOrderId);
			
			mediaLineTotalQty = childTotalQty != null ? childTotalQty.intValue() : 0;
			
			if(mediaLineTotalQty > mediaTotalQty) {
				addMediumLabelResponse.setError("Media Line Qty must not exceed Media Order Qty");
				addMediumLabelResponse.setIsError(true);
				return addMediumLabelResponseDocument;
			}

			Date date1 = (Date) dateFormat.parse(date);
			long timestampValue1 = date1.getTime();
			Timestamp timestamp1 = new Timestamp(timestampValue1);

			String query = "SELECT tc_machinetype_id AS id,codeNo FROM adempiere.tc_machinetype\n"
					+ "WHERE ad_client_id = ? AND codeNo = ?";
			pstmt1 = DB.prepareStatement(query, null);
			pstmt1.setInt(1, clientId);
			pstmt1.setString(2, machineCode);
			rs1 = pstmt1.executeQuery();

			while (rs1.next()) {
				machineTypeId = rs1.getInt("id");
			}
			if (machineTypeId == 0) {
				addMediumLabelResponse
						.setError("Machine code is not Available in our Tc_MachineType Table records, MachineCode: "
								+ machineCode);
				addMediumLabelResponse.setIsError(true);
				return addMediumLabelResponseDocument;
			}

			MTable table = MTable.get(ctx, TABLE_MEDIA_TYPE);
			PO po = table.getPO(mediaTypeId, trx.getTrxName());
			if (po == null) {
				addMediumLabelResponse.setError("Media Type not found " + mediaTypeId + "");
				addMediumLabelResponse.setIsError(true);
				return addMediumLabelResponseDocument;
			}
			TCMediaType stage = (TCMediaType) po;
			String productName = stage.getName();

			String sql = "SELECT name,description,m_product_id FROM adempiere.m_product\n" + "WHERE ad_client_id = ?"
					+ " AND description = ?";
			pstmt = DB.prepareStatement(sql, null);
			pstmt.setInt(1, clientId);
			pstmt.setString(2, productName);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				productId = rs.getInt("m_product_id");
			}
			TCMediaLine line = new TCMediaLine(ctx, 0, trx.getTrxName());
			line.setAD_Org_ID(orgId);
			line.setTC_mediaorder_ID(mediaOrderId);
			line.setM_Product_ID(productId);
			line.setM_Locator_ID(locatorId);
			line.setQuantity(quantity);
			line.setC_UOM_ID(UOM_ID);
			if (!line.save()) {
				throw new Exception("Failed to add Media Line: " + line);
			}
			trx.commit();
			int mediaLineId = line.get_ID();
			closeDbCon(pstmt, rs);
			closeDbCon(pstmt1, rs1);

			for (int i = 0; i < qty; i++) {
				MediaLabelData data = addMediumLabelResponse.addNewMediaLabelData();
				TCMediaLabelQr labelQr = new TCMediaLabelQr(ctx, 0, trx.getTrxName());
				labelQr.setAD_Org_ID(orgId);
				labelQr.setTC_MediaType_ID(mediaTypeId);
				labelQr.setTC_MachineType_ID(machineTypeId);
				labelQr.setpersonalcode(personalCode);
				labelQr.settcpf(tcpf);
				labelQr.setoperationdate(timestamp1);
				labelQr.setTC_MediaLine_ID(mediaLineId);
				if (!labelQr.save()) {
					throw new Exception("Failed to add Culture Label data: " + labelQr);
				}
				trx.commit();
				int mediaLabelId = labelQr.get_ID();

				int retryCount = 0;
				String mediaLabelUUId = null;
				while (mediaLabelUUId == null && retryCount < 10) {
					mediaLabelUUId = DB.getSQLValueString(null,
							"SELECT c_uuid FROM " + TABLE_MEDIA + " WHERE " + TABLE_MEDIA + "_id = ?", mediaLabelId);
					if (mediaLabelUUId == null) {
						Thread.sleep(500); // Wait for 500ms before retrying
						retryCount++;
					}
				}
				if (mediaLabelUUId == null) {
					throw new Exception("Failed to get UUID for Media Label ID: " + mediaLabelId);
				}
				trx.commit();
				data.setMediaLabelId(mediaLabelId);
				data.setMediaLabelUUId(mediaLabelUUId);
			}
			addMediumLabelResponse.setIsError(false);

		} catch (Exception e) {
			if (trx != null) trx.rollback();
			addMediumLabelResponse.setError(INTERNAL_SERVER_ERROR);
			addMediumLabelResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstmt, rs);
			closeDbCon(pstmt1, rs1);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return addMediumLabelResponseDocument;
	}

	@Override
	public GetMediaLabelResponseDocument getMediaLabel(GetMediaLabelRequestDocument req) {
		GetMediaLabelResponseDocument getMediaLabelResponseDocument = GetMediaLabelResponseDocument.Factory
				.newInstance();
		GetMediaLabelResponse getMediaLabelResponse = getMediaLabelResponseDocument.addNewGetMediaLabelResponse();
		GetMediaLabelRequest loginRequest = req.getGetMediaLabelRequest();
		String mediaLabelUUId = safeTrim(loginRequest.getCultureLabelUUId());
		Trx trx = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
		int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			int recordId = TCUtills.getId(TABLE_MEDIA, mediaLabelUUId, clientId);
			if (recordId == 0) {
				getMediaLabelResponse.setError("Media Label UUId not found in table record ");
				getMediaLabelResponse.setIsError(true);
				return getMediaLabelResponseDocument;
			}
			String sql = "SELECT cl.tc_mediaLabelQr_id,cl.c_uuid,cl.created,cl.tcpf,cl.personalCode,cl.isdiscarded AS discard,\n"
					+ "mt.name AS mediaType,mat.codeNo AS machineName,cl.operationDate,mt.tc_mediatype_id AS mediaId,\n"
					+ "cl.ad_client_id,cl.ad_org_id,cl.tc_medialine_id AS lineId FROM adempiere.tc_mediaLabelQr cl\n"
					+ "JOIN adempiere.tc_mediatype mt ON mt.tc_mediatype_id = cl.tc_mediatype_id\n"
					+ "JOIN adempiere.tc_machinetype mat ON mat.tc_machinetype_id = cl.tc_machinetype_id\n"
					+ "WHERE cl.ad_client_id = ? AND cl.c_uuid = ?;";
			pstmt = DB.prepareStatement(sql, null);
			pstmt.setInt(1, clientId);
			pstmt.setString(2, mediaLabelUUId);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				String discardValue = rs.getString("discard");
				if ("Y".equalsIgnoreCase(discardValue)) {
					getMediaLabelResponse.setError("This Media Label has been Discarded : " + mediaLabelUUId + "");
					getMediaLabelResponse.setIsError(true);
					return getMediaLabelResponseDocument; // Exit the method early if discard is "Y"
				}

				GetMediaLabelData data = getMediaLabelResponse.addNewGetMediaLabelData();
				String mediaType = rs.getString("mediaType");
				String tcpf = rs.getString("tcpf");
				String date = rs.getString("operationDate");
				String personalCode = rs.getString("personalCode");
				String machineCode = rs.getString("machineName");
				int mediaId = rs.getInt("mediaId");
				int lineId = rs.getInt("lineId");

				data.setMediaLabelUUID(mediaLabelUUId);
				data.setMediaType(mediaType);
				data.setTCPF(tcpf);
				data.setOperationDate(date);
				data.setPersonalCode(personalCode);
				data.setMachineName(machineCode);
				data.setMediaTypeId(mediaId);
				data.setMediaLineId(lineId);
			}
			trx.commit();
			getMediaLabelResponse.setIsError(false);
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			getMediaLabelResponse.setError(INTERNAL_SERVER_ERROR);
			getMediaLabelResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstmt, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return getMediaLabelResponseDocument;
	}

	@Override
	public AddExplantOperationResponseDocument addExplantOperation(AddExplantOperationRequestDocument req) {
		AddExplantOperationResponseDocument addExplantOperationResponseDocument = AddExplantOperationResponseDocument.Factory
				.newInstance();
		AddExplantOperationResponse addExplantOperationResponse = addExplantOperationResponseDocument
				.addNewAddExplantOperationResponse();
		AddExplantOperationRequest loginRequest = req.getAddExplantOperationRequest();
		AddExplantDetails addExplantDetails = loginRequest.getAddExplantDetails();
		AddExplantOperation addExplantOperation = loginRequest.getAddExplantOperation();
		String plantTagUUid = safeTrim(loginRequest.getPlantTagUUId());
		int cropTypeId = 0;
		int varietyId = 0;
		String parentCultureLine = safeTrim(addExplantDetails.getParentCultureLine());
		String cultureDetailDate = safeTrim(addExplantDetails.getCultureDetailDate());
		int naturesampleId = 0;
		String tcpf = safeTrim(addExplantOperation.getTCPF());
		String personalCode = safeTrim(addExplantOperation.getPersonalCode());
		String operationDate = safeTrim(addExplantOperation.getDate());
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		int explantProductId = 0;
		int inId = 0;
		int qty = 1;
		BigDecimal quantity = new BigDecimal(qty);
		Trx trx = null;
		PreparedStatement pstm1 = null, pstm2 = null;
		ResultSet rs1 = null, rs2 = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
			int orgId = Env.getAD_Org_ID(ctx);
			int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			cropTypeId = addExplantDetails.getPlantSpeciesId();
			varietyId = addExplantDetails.getVarietyId();
			naturesampleId = addExplantDetails.getNatureSampleId();
			inId = loginRequest.getInId();
			String userName = TCUtills.getUserName(clientId, personalCode);
			if (userName == "") {
				addExplantOperationResponse
						.setError("Personal code is not available for any user, " + personalCode + "");
				addExplantOperationResponse.setIsError(true);
				return addExplantOperationResponseDocument;
			}

			int planttagId = TCUtills.getIds(plantTagUUid, clientId);
			if (planttagId != 0) {
				addExplantOperationResponse.setError("This plant tag is already attached to a Explant Label");
				addExplantOperationResponse.setIsError(true);
				return addExplantOperationResponseDocument;
			}

			int explantloactorId = TCUtills.getLocatorId(clientId, TABLE_LOCATOR, DEFAULT_LOCATOR_EXPLANT);
			if (explantloactorId == 0) {
				addExplantOperationResponse
						.setError("locator searchKey is not in table record " + DEFAULT_LOCATOR_EXPLANT + "");
				addExplantOperationResponse.setIsError(true);
				return addExplantOperationResponseDocument;
			}
			
			if (containsMaliciousPattern(parentCultureLine)) {
				addExplantOperationResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				addExplantOperationResponse.setIsError(true);
			    return addExplantOperationResponseDocument;
			}
			
			if (containsMaliciousPattern(operationDate)) {
				addExplantOperationResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				addExplantOperationResponse.setIsError(true);
			    return addExplantOperationResponseDocument;
			}
			
			if (containsMaliciousPattern(cultureDetailDate)) {
				addExplantOperationResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				addExplantOperationResponse.setIsError(true);
			    return addExplantOperationResponseDocument;
			}
			
			if (containsMaliciousPattern(tcpf)) {
				addExplantOperationResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				addExplantOperationResponse.setIsError(true);
			    return addExplantOperationResponseDocument;
			}
			
			if (!TCUtills.isValidNameNumber(parentCultureLine)) {
				addExplantOperationResponse.setError("Invalid Parent Culture Line format");
				addExplantOperationResponse.setIsError(true);
				return addExplantOperationResponseDocument;
	        }

			Date date1 = (Date) dateFormat.parse(cultureDetailDate);
			long timestampValue1 = date1.getTime();
			Timestamp timestamp1 = new Timestamp(timestampValue1);

			Date date = (Date) dateFormat.parse(operationDate);
			long timestampValue = date.getTime();
			Timestamp timestamp = new Timestamp(timestampValue);

			String sql2 = "SELECT name,m_product_id AS eId FROM adempiere.m_product WHERE name = ?"
					+ " AND ad_client_id = ?;";
			pstm2 = DB.prepareStatement(sql2, null);
			pstm2.setString(1, DEFAULT_PRODUCT_EXPLANT);
			pstm2.setInt(2, clientId);
			rs2 = pstm2.executeQuery();
			while (rs2.next()) {
				explantProductId = rs2.getInt("eId");
			}

			TCIn in = new TCIn(ctx, inId, trx.getTrxName());
			int orderId = in.getTC_order_ID();

			TCOut out = new TCOut(ctx, 0, trx.getTrxName());
			out.setTC_order_ID(orderId);
			out.setAD_Org_ID(orgId);
			out.setTC_in_ID(inId);
			out.setM_Locator_ID(explantloactorId);
			out.setM_Product_ID(explantProductId);
			out.setQuantity(quantity);
			out.setC_UOM_ID(UOM_ID);
			if (!out.save()) {
				throw new Exception("Failed to add out data: " + out);
			}
			trx.commit();
			int outId = out.get_ID();

			TCExplantLabel label = new TCExplantLabel(ctx, 0, trx.getTrxName());
			label.setAD_Org_ID(orgId);
			label.settc_species_id(cropTypeId);
			label.setTC_Variety_ID(varietyId);
			label.setparentcultureline(parentCultureLine);
			label.setsourcingdate(timestamp1);
			label.setTC_NatureSample_ID(naturesampleId);
			label.settcpf(tcpf);
			label.setoperationdate(timestamp);
			label.setpersonalcode(personalCode);
			label.setTC_in_ID(inId);
			label.setTC_out_ID(outId);
			label.setparentuuid(plantTagUUid);
			if (!label.save()) {
				throw new Exception("Failed to add Explant Detail: " + label);
			}
			trx.commit();
			int explantLabelId = label.getTC_ExplantLabel_ID();

			int retryCount = 0;
			String explantLabelUUId = null;
			String outUUId = null;
			while (explantLabelUUId == null && retryCount < 10) {
				explantLabelUUId = DB.getSQLValueString(null,
						"SELECT c_uuid FROM " + TABLE_EXPLANT + " WHERE " + TABLE_EXPLANT + "_id = ?", explantLabelId);
				outUUId = DB.getSQLValueString(null,
						"SELECT c_uuid FROM " + TABLE_TC_OUT + " WHERE " + TABLE_TC_OUT + "_id = ?", outId);
				if (explantLabelUUId == null && outUUId == null) {
					Thread.sleep(900); // Wait for 500ms before retrying
					retryCount++;
				}
			}
			if (explantLabelUUId == null) {
				throw new Exception("Failed to get UUID for TC Order ID: " + explantLabelId);
			}
			trx.commit();
			addExplantOperationResponse.setOutUUId(outUUId);
			addExplantOperationResponse.setExplantLabelUUId(explantLabelUUId);
			addExplantOperationResponse.setExplantLabelId(explantLabelId);

		} catch (Exception e) {
			if (trx != null) trx.rollback();
			addExplantOperationResponse.setError(INTERNAL_SERVER_ERROR);
			addExplantOperationResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm1, rs1);
			closeDbCon(pstm2, rs2);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return addExplantOperationResponseDocument;
	}

	@Override
	public GetExplantLabelResponseDocument getExplantLabel(GetExplantLabelRequestDocument req) {
		GetExplantLabelResponseDocument getExplantLabelResponseDocument = GetExplantLabelResponseDocument.Factory
				.newInstance();
		GetExplantLabelResponse getExplantLabelResponse = getExplantLabelResponseDocument
				.addNewGetExplantLabelResponse();
		GetExplantLabelRequest loginRequest = req.getGetExplantLabelRequest();
		String outLabelUUId = safeTrim(loginRequest.getOutLabelUUId());
		Trx trx = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
		int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			int recordId = TCUtills.getId(TABLE_TC_OUT, outLabelUUId, clientId);
			if (recordId == 0) {
				getExplantLabelResponse
						.setError("Explant Label Out UUId not found in table record " + outLabelUUId + "");
				getExplantLabelResponse.setIsError(true);
				return getExplantLabelResponseDocument;
			}

			String sql = "SELECT el.tc_explantLabel_id,el.c_uuid AS eUUid,el.parentcultureline,el.tcpf,el.personalCode,\n"
					+ "ps.codeno AS cropType,v.codeno AS Variety,ns.codeno AS natureSample,el.tc_out_id As outId,o.c_uuid AS outUUid,\n"
					+ "el.sourcingDate AS explantDate,el.operationDate AS explantOperationDate\n"
					+ "FROM adempiere.tc_explantLabel el\n"
					+ "JOIN adempiere.tc_plantspecies ps ON ps.tc_plantspecies_id = el.tc_species_id\n"
					+ "JOIN adempiere.tc_variety v ON v.tc_variety_id = el.tc_variety_id\n"
					+ "JOIN adempiere.tc_out o ON o.tc_out_id = el.tc_out_id\n"
					+ "JOIN adempiere.tc_naturesample ns ON ns.tc_naturesample_id = el.tc_naturesample_id\n"
					+ "WHERE el.ad_client_id = ? AND o.c_uuid = ?;";
			pstmt = DB.prepareStatement(sql, null);
			pstmt.setInt(1, clientId);
			pstmt.setString(2, outLabelUUId);
			rs = pstmt.executeQuery();

			if (!rs.isBeforeFirst()) {
				getExplantLabelResponse.addNewGetExplantLabelData();
				getExplantLabelResponse.addNewGetInIdForExplant();
				getExplantLabelResponse.setIsError(false);
				getExplantLabelResponse.setError("OutUUId not attached in any Explant label then not show any records");
				return getExplantLabelResponseDocument;
			}

			while (rs.next()) {
				GetExplantLabelData data = getExplantLabelResponse.addNewGetExplantLabelData();

				String explantUUId = rs.getString("eUUid");
				String tcpf = rs.getString("tcpf");
				String operationDate = rs.getString("explantOperationDate");
				String personalCode = rs.getString("personalCode");
				String cropType = rs.getString("cropType");
				String variety = rs.getString("Variety");
				String natureSample = rs.getString("natureSample");
				String explantDate = rs.getString("explantDate");
				String parentCultureLine = rs.getString("parentcultureline");
				String outUUid = rs.getString("outUUid");

				data.setExplantLabelUUID(explantUUId);
				data.setCropType(cropType);
				data.setVariety(variety);
				;
				data.setParentCultureLine(parentCultureLine);
				data.setExplantDetailDate(explantDate);
				data.setNatureSample(natureSample);
				data.setTCPF(tcpf);
				data.setOperationDate(operationDate);
				data.setPersonalCode(personalCode);
				data.setOutLabelUUID(outUUid);
			}
			closeDbCon(pstmt, rs);
			pstmt = null;
			rs = null;

			String sql2 = "select i.tc_in_id As id,i.tc_order_id As orderId from adempiere.tc_explantlabel e\n"
					+ "Join adempiere.tc_out o ON o.tc_out_id = e.tc_out_id\n"
					+ "JOIN adempiere.tc_in i ON i.parentuuid = o.c_uuid	\n" + "WHERE e.ad_client_id = ?"
					+ " AND i.parentuuid = ? ORDER BY id DESC;";
			pstmt = DB.prepareStatement(sql2, null);
			pstmt.setInt(1, clientId);
			pstmt.setString(2, outLabelUUId);
			rs = pstmt.executeQuery();

			if (!rs.isBeforeFirst()) {
				getExplantLabelResponse.addNewGetInIdForExplant();
				getExplantLabelResponse.setIsError(false);
				return getExplantLabelResponseDocument;
			}

			while (rs.next()) {
				GetInIdForExplant inData = getExplantLabelResponse.addNewGetInIdForExplant();
				int inId = rs.getInt("id");
				int orderId = rs.getInt("orderId");
				inData.setInID(inId);
				inData.setOrderId(orderId);
			}
			trx.commit();
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			getExplantLabelResponse.setError(INTERNAL_SERVER_ERROR);
			getExplantLabelResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstmt, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return getExplantLabelResponseDocument;
	}

	@Override
	public GetLocatorDetailResponseDocument getLocatorDetail(GetLocatorDetailRequestDocument req) {
		GetLocatorDetailResponseDocument getLocatorDetailResponseDocument = GetLocatorDetailResponseDocument.Factory
				.newInstance();
		GetLocatorDetailResponse getLocatorDetailResponse = getLocatorDetailResponseDocument
				.addNewGetLocatorDetailResponse();
		GetLocatorDetailRequest loginRequest = req.getGetLocatorDetailRequest();
		int locatorId = 0;
		Trx trx = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			locatorId = loginRequest.getLocatorId();
			MTable table = MTable.get(ctx, TABLE_LOCATOR);
			PO po = table.getPO(locatorId, trx.getTrxName());
			if (po.get_ID() == 0) {
				getLocatorDetailResponse.setError("Locator Id not found in table record " + locatorId + "");
				getLocatorDetailResponse.setIsError(true);
				return getLocatorDetailResponseDocument;
			}
			GetLocatorDetailData data = getLocatorDetailResponse.addNewGetLocatorDetailData();
			MLocator locator = new MLocator(ctx, locatorId, trx.getTrxName());
			int locatorTypeId = locator.getM_LocatorType_ID();
			String growthRoom = locator.getX();
			String rack = locator.getY();
			String column = locator.getZ();

			data.setLocatorID(locatorId);
			data.setLocatorTypeId(locatorTypeId);
			data.setGrowthRoom(growthRoom);
			data.setRack(rack);
			data.setColumn(column);
			trx.commit();
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			getLocatorDetailResponse.setError(INTERNAL_SERVER_ERROR);
			getLocatorDetailResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return getLocatorDetailResponseDocument;
	}

	@Override
	public AddPrimaryHardeningDetailResponseDocument addPrimaryHardeningDetail(
			AddPrimaryHardeningDetailRequestDocument req) {
		AddPrimaryHardeningDetailResponseDocument addPrimaryHardeningDetailResponseDocument = AddPrimaryHardeningDetailResponseDocument.Factory
				.newInstance();
		AddPrimaryHardeningDetailResponse addPrimaryHardeningDetailResponse = addPrimaryHardeningDetailResponseDocument
				.addNewAddPrimaryHardeningDetailResponse();
		AddPrimaryHardeningDetailRequest loginRequest = req.getAddPrimaryHardeningDetailRequest();
		AddHardeningDetail addHardeningDetail = loginRequest.getAddHardeningDetail();
		String outUUId = safeTrim(loginRequest.getOutUUId());
		String cultureProcessedNumber = safeTrim(loginRequest.getCultureProcessedNo());
		String yearCode = safeTrim(addHardeningDetail.getYearCode());
		int cropTypeId = 0;
		int varietyId = 0;
		String parentCultureLine = safeTrim(addHardeningDetail.getParentCultureLine());
		String plotNumberTray = safeTrim(addHardeningDetail.getPlotNumberTray());
		String lotNumber = safeTrim(addHardeningDetail.getLotNumber());
		int cultureStageId = 0;
		String operationDate = safeTrim(addHardeningDetail.getOperationDate());
		int orderId = 0;
		int primaryQty = 0;
		int productId = 0;
		int inId = 0;
		int UOMId = 0;
		int totalQty = 0;
		int discardQty = 0;
		int oldProductId = 0;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Trx trx = null;
		int fromLocatorId = 0;
		int toLocatorId = 0;
		PreparedStatement pstm1 = null;
		ResultSet rs1 = null;
		int documentType = 0;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
			int orgId = Env.getAD_Org_ID(ctx);
			int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			cropTypeId = addHardeningDetail.getPlantSpeciesId();
			varietyId = addHardeningDetail.getVarietyId();
			cultureStageId = addHardeningDetail.getCultureStageId();
			orderId = loginRequest.getOrderId();
			primaryQty = loginRequest.getQuantity();
			BigDecimal priQty = new BigDecimal(primaryQty);
			int recordId = TCUtills.getId(TABLE_TC_OUT, outUUId, clientId);
			if (recordId == 0) {
				addPrimaryHardeningDetailResponse.setError("Out Label UUId not found in table record " + outUUId + "");
				addPrimaryHardeningDetailResponse.setIsError(true);
				return addPrimaryHardeningDetailResponseDocument;
			}
			
			if (containsMaliciousPattern(parentCultureLine)) {
				addPrimaryHardeningDetailResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				addPrimaryHardeningDetailResponse.setIsError(true);
			    return addPrimaryHardeningDetailResponseDocument;
			}
			
			if (containsMaliciousPattern(operationDate)) {
				addPrimaryHardeningDetailResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				addPrimaryHardeningDetailResponse.setIsError(true);
			    return addPrimaryHardeningDetailResponseDocument;
			}
			
			if (containsMaliciousPattern(yearCode)) {
				addPrimaryHardeningDetailResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				addPrimaryHardeningDetailResponse.setIsError(true);
			    return addPrimaryHardeningDetailResponseDocument;
			}
			
			if (containsMaliciousPattern(lotNumber)) {
				addPrimaryHardeningDetailResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				addPrimaryHardeningDetailResponse.setIsError(true);
			    return addPrimaryHardeningDetailResponseDocument;
			}
			
			if (containsMaliciousPattern(plotNumberTray)) {
				addPrimaryHardeningDetailResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				addPrimaryHardeningDetailResponse.setIsError(true);
			    return addPrimaryHardeningDetailResponseDocument;
			}
			
			if (!TCUtills.isValidNameNumber(parentCultureLine)) {
				addPrimaryHardeningDetailResponse.setError("Invalid Parent Culture Line format");
				addPrimaryHardeningDetailResponse.setIsError(true);
				return addPrimaryHardeningDetailResponseDocument;
	        }
			
			if (!TCUtills.isValidLocationName(plotNumberTray)) {
				addPrimaryHardeningDetailResponse.setError("Invalid Plot Number Tray format");
				addPrimaryHardeningDetailResponse.setIsError(true);
				return addPrimaryHardeningDetailResponseDocument;
	        }
			
			if (!TCUtills.isValidLocationName(lotNumber)) {
				addPrimaryHardeningDetailResponse.setError("Invalid Lot Number format");
				addPrimaryHardeningDetailResponse.setIsError(true);
				return addPrimaryHardeningDetailResponseDocument;
	        }
			
			if (!TCUtills.isValidLocationName(cultureProcessedNumber)) {
				addPrimaryHardeningDetailResponse.setError("Invalid Culture Processed Number format");
				addPrimaryHardeningDetailResponse.setIsError(true);
				return addPrimaryHardeningDetailResponseDocument;
	        }
			
			if (!TCUtills.isValidLocationName(yearCode)) {
				addPrimaryHardeningDetailResponse.setError("Invalid Year Code format");
				addPrimaryHardeningDetailResponse.setIsError(true);
				return addPrimaryHardeningDetailResponseDocument;
	        }
			
			TCOut outOld = new TCOut(ctx, recordId, trx.getTrxName());
			fromLocatorId = outOld.getM_Locator_ID();
			oldProductId = outOld.getM_Product_ID();
			UOMId = outOld.getC_UOM_ID();

			int primaryLocatorId = TCUtills.getLocatorId(clientId, TABLE_LOCATOR, DEFAULT_LOCATOR_PH);
			if (primaryLocatorId == 0) {
				addPrimaryHardeningDetailResponse
						.setError("Locator serachKey not found in table record " + DEFAULT_LOCATOR_PH + "");
				addPrimaryHardeningDetailResponse.setIsError(true);
				return addPrimaryHardeningDetailResponseDocument;
			}
			toLocatorId = TCUtills.getLocatorId(clientId, TABLE_LOCATOR, DEFAULT_LOCATOR_HD);
			documentType = TCUtills.getDocTypeId(clientId, DOCUMENT_MATERIAL_MOVEMENT);

			List<PO> inIdList = new Query(ctx, TCIn.Table_Name,
					"tc_order_id = ? AND ad_client_id = ? AND primarycheck = 'N' AND EXISTS ("
							+ "SELECT 1 FROM M_Product p WHERE p.M_Product_ID = tc_in.M_Product_ID AND p.Description = 'Rooting')",
					trxName).setParameters(orderId, clientId).setOrderBy("tc_in_id ASC").list();
			totalQty = inIdList.size();
			discardQty = totalQty - primaryQty;
			if (discardQty < 0) {
				addPrimaryHardeningDetailResponse
						.setError("Enter Quantity : " + primaryQty + " and available Quantity : " + totalQty + "");
				addPrimaryHardeningDetailResponse.setIsError(true);
				return addPrimaryHardeningDetailResponseDocument;
			}
			BigDecimal discardQtys = new BigDecimal(discardQty);
			for (int i = 0; i < Math.min(inIdList.size(), primaryQty); i++) {
				PO inlist = inIdList.get(i);
				TCIn in = new TCIn(ctx, inlist.get_ID(), trx.getTrxName());
				in.setprimarycheck(true);
				in.saveEx();
				trx.commit();
				inId = inlist.get_ID();
			}
			for (int i = primaryQty; i < totalQty; i++) {
				PO inlist = inIdList.get(i);
				TCIn in = new TCIn(ctx, inlist.get_ID(), trx.getTrxName());
				in.setprimarycheck(true);
				in.setDescription("Discarded");
				in.saveEx();
				trx.commit();
			}

			Date date = (Date) dateFormat.parse(operationDate);
			long timestampValue = date.getTime();
			Timestamp timestamp = new Timestamp(timestampValue);

			String sql1 = "SELECT name,m_product_id AS pId FROM adempiere.m_product WHERE name = ?"
					+ " AND ad_client_id = ?;";
			pstm1 = DB.prepareStatement(sql1, null);
			pstm1.setString(1, DEFAULT_PRODUCT_PH);
			pstm1.setInt(2, clientId);
			rs1 = pstm1.executeQuery();
			while (rs1.next()) {
				productId = rs1.getInt("pId");
			}

			TCOut out = new TCOut(ctx, 0, trx.getTrxName());
			out.setTC_order_ID(orderId);
			out.setAD_Org_ID(orgId);
			out.setTC_in_ID(inId);
			out.setM_Locator_ID(primaryLocatorId);
			out.setM_Product_ID(productId);
			out.setQuantity(priQty);
			out.setdiscardqty(discardQtys);
			out.setC_UOM_ID(UOMId);
			out.setcycle(1);
			if (!out.save()) {
				throw new Exception("Failed to add out data: " + out);
			}
			trx.commit();
			int outId = out.get_ID();

			TCPrimaryHardeningLabel hLabel = new TCPrimaryHardeningLabel(ctx, 0, trx.getTrxName());
			hLabel.setAD_Org_ID(orgId);
			hLabel.setcultureprocessednumber(cultureProcessedNumber);
			hLabel.setyearcode(yearCode);
			hLabel.settc_species_id(cropTypeId);
			hLabel.setTC_Variety_ID(varietyId);
			hLabel.setparentcultureline(parentCultureLine);
			hLabel.setlotnumber(lotNumber);
			hLabel.setplotnumbertray(plotNumberTray);
			hLabel.setTC_CultureStage_ID(cultureStageId);
			hLabel.setoperationdate(timestamp);
			hLabel.setTC_in_ID(inId);
			hLabel.setTC_out_ID(outId);
			if (!hLabel.save()) {
				throw new Exception("Failed to add Primary Hardening Label data: " + hLabel);
			}
			trx.commit();
			int pHLabelId = hLabel.get_ID();
			trx.commit();

			int retryCount = 0;
			String pHLUUid = null, newOutUUId = null;
			while (pHLUUid == null && newOutUUId == null && retryCount < 10) {
				pHLUUid = DB.getSQLValueString(null,
						"SELECT c_uuid FROM " + TABLE_PH + " WHERE " + TABLE_PH + "_id = ?", pHLabelId);
				newOutUUId = DB.getSQLValueString(null,
						"SELECT c_uuid FROM " + TABLE_TC_OUT + " WHERE " + TABLE_TC_OUT + "_id = ?", outId);
				if (pHLUUid == null && newOutUUId == null) {
					Thread.sleep(900); // Wait for 500ms before retrying
					retryCount++;
				}
			}
			if (pHLUUid == null) {
				throw new Exception("Failed to get UUID for Primary Hardening ID: " + pHLabelId);
			}
			trx.commit();
			addPrimaryHardeningDetailResponse.setTCOrderId(orderId);
			addPrimaryHardeningDetailResponse.setOutUUId(newOutUUId);
			addPrimaryHardeningDetailResponse.setPrimaryHardeningLabelId(pHLabelId);
			addPrimaryHardeningDetailResponse.setPrimaryHardeningLabelUUId(pHLUUid);

			for (int i = 0; i < Math.min(inIdList.size(), primaryQty); i++) {
				PO inlist = inIdList.get(i);
				TCIn ins = new TCIn(ctx, inlist.get_ID(), trx.getTrxName());
				List<PO> result = new Query(ctx, TABLE_TC_OUT,
						"c_uuid IN (SELECT parentuuid FROM TC_In WHERE ad_client_id = ? AND tc_in_id = ?)", trxName)
						.setParameters(clientId, ins.get_ID()).list();
				for (PO outs : result) {
					PO cultureLabel = new Query(ctx, TABLE_TC_CULTURE, "tc_out_id = ?", trxName)
							.setParameters(outs.get_ID()).first();
					if (cultureLabel != null) {
						String cUuid = (String) cultureLabel.get_Value("c_uuid");
						Integer cultureLabelId = (Integer) cultureLabel.get_Value("tc_culturelabel_id");

						X_tc_primaryHardeningcultureS joinCulture = new X_tc_primaryHardeningcultureS(ctx, 0,
								trx.getTrxName());
						joinCulture.setTC_PrimaryHardeningLabel_ID(pHLabelId);
						joinCulture.setTC_cultureLabel_ID(cultureLabelId);
						joinCulture.setcultureuuid(cUuid);
						joinCulture.saveEx();
						trx.commit();
					}
				}
			}
			if (discardQty > 0) {
				MMovement move = new MMovement(ctx, 0, trx.getTrxName());
				move.setAD_Org_ID(orgId);
				move.setDateReceived(timestamp);
				move.setC_DocType_ID(documentType);
				move.setM_Warehouse_ID(Env.getContextAsInt(ctx, Env.M_WAREHOUSE_ID));
				move.setM_WarehouseTo_ID(Env.getContextAsInt(ctx, Env.M_WAREHOUSE_ID));
				if (!move.save()) {
					throw new Exception("Failed to add Internal move data: " + move);
				}
				trx.commit();

				moveInventory(ctx, trxName, move, discardQtys, oldProductId, fromLocatorId, toLocatorId);

				move.saveEx();
				move.setDocStatus(DocAction.ACTION_Complete);
				move.setDocAction(DocAction.ACTION_Close);
				move.setPosted(true);
				move.setProcessed(true);
				move.setIsApproved(true);
				move.completeIt();
				move.saveEx();
			}
			addPrimaryHardeningDetailResponse.setIsError(false);

		} catch (Exception e) {
			if (trx != null) trx.rollback();
			addPrimaryHardeningDetailResponse.setError(INTERNAL_SERVER_ERROR);
			addPrimaryHardeningDetailResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm1, rs1);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return addPrimaryHardeningDetailResponseDocument;
	}

	@Override
	public GetPrimaryHardeningLabelResponseDocument getPrimaryHardeningLabel(
			GetPrimaryHardeningLabelRequestDocument req) {
		GetPrimaryHardeningLabelResponseDocument getPrimaryHardeningLabelResponseDocument = GetPrimaryHardeningLabelResponseDocument.Factory
				.newInstance();
		GetPrimaryHardeningLabelResponse getPrimaryHardeningLabelResponse = getPrimaryHardeningLabelResponseDocument
				.addNewGetPrimaryHardeningLabelResponse();
		GetPrimaryHardeningLabelRequest loginRequest = req.getGetPrimaryHardeningLabelRequest();
		String outLabelUUId = safeTrim(loginRequest.getOutLabelUUId());
		Trx trx = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
		int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			int RecordId = TCUtills.getId(TABLE_TC_OUT, outLabelUUId, clientId);
			if (RecordId == 0) {
				getPrimaryHardeningLabelResponse
						.setError("Out Label UUId not found in table record " + outLabelUUId + "");
				getPrimaryHardeningLabelResponse.setIsError(true);
				return getPrimaryHardeningLabelResponseDocument;
			}

			String sql = "SELECT ph.tc_primaryhardeningLabel_id,ph.c_uuId AS UUId,ph.yearCode AS yearCode,ph.parentCultureLine AS PCultureLine,\n"
					+ "ph.cultureProcessedNumber AS cultureProcessNumber,ph.plotNumberTray AS plotNumberTray,ph.lotnumber As lotNumber,\n"
					+ "ph.operationDate AS operationDate,ps.codeno AS cropType,v.codeno AS variety,cs.codeno AS cultureStage,\n"
					+ "o.c_uuid AS OutUUId FROM adempiere.tc_primaryhardeningLabel ph\n"
					+ "JOIN adempiere.tc_plantspecies ps ON ps.tc_plantspecies_id = ph.tc_species_id\n"
					+ "JOIN adempiere.tc_variety v ON v.tc_variety_id = ph.tc_variety_id\n"
					+ "JOIN adempiere.tc_culturestage cs ON cs.tc_culturestage_id = ph.tc_culturestage_id\n"
					+ "JOIN adempiere.tc_out o ON o.tc_out_id = ph.tc_out_id\n" + "WHERE ph.ad_client_id = ?"
					+ " AND o.c_uuid = ?;";
			pstm = DB.prepareStatement(sql, null);
			pstm.setInt(1, clientId);
			pstm.setString(2, outLabelUUId);
			rs = pstm.executeQuery();
			while (rs.next()) {
				GetPrimaryHardeningLabelData data = getPrimaryHardeningLabelResponse
						.addNewGetPrimaryHardeningLabelData();
				String primaryLabelUUid = rs.getString("UUId");
				String yearCode = rs.getString("yearCode");
				String pCultureLine = rs.getString("PCultureLine");
				String cultureProcessNumber = rs.getString("cultureProcessNumber");
				String lotNumber = rs.getString("lotNumber");
				String plotNumberTray = rs.getString("plotNumberTray");
				String operationDate = rs.getString("operationDate");
				String cropType = rs.getString("cropType");
				String variety = rs.getString("variety");
				String cultureStage = rs.getString("cultureStage");
				String outLabelUUid = rs.getString("OutUUId");

				data.setPrimaryHardeningLabelUUID(primaryLabelUUid);
				data.setYearCode(yearCode);
				data.setParentCultureLine(pCultureLine);
				data.setCultureProcessedNo(cultureProcessNumber);
				data.setPlotNumberTray(plotNumberTray);
				data.setLotNumber(lotNumber != null ? lotNumber : "");
				data.setOperationDate(operationDate);
				data.setCropType(cropType);
				data.setVariety(variety);
				data.setCultureStage(cultureStage);
				data.setOutLabelUUID(outLabelUUid);
			}
			closeDbCon(pstm, rs);
			pstm = null;
			rs = null;

			String sql2 = "SELECT i.tc_in_id As id,i.tc_order_id As orderId FROM adempiere.tc_primaryhardeninglabel c\n"
					+ "JOIN adempiere.tc_out o ON o.tc_out_id = c.tc_out_id\n"
					+ "JOIN adempiere.tc_in i ON i.parentuuid = o.c_uuid \n" + "WHERE c.ad_client_id = ?"
					+ "\n" + "AND i.parentuuid = ? ORDER BY id DESC;";
			pstm = DB.prepareStatement(sql2, null);
			pstm.setInt(1, clientId);
			pstm.setString(2, outLabelUUId);
			rs = pstm.executeQuery();

			if (!rs.isBeforeFirst()) {
				getPrimaryHardeningLabelResponse.addNewGetInIdForPrimaryH();
				getPrimaryHardeningLabelResponse.setIsError(false);
				return getPrimaryHardeningLabelResponseDocument;
			}

			while (rs.next()) {
				GetInIdForPrimaryH inData = getPrimaryHardeningLabelResponse.addNewGetInIdForPrimaryH();
				int inId = rs.getInt("id");
				int orderId = rs.getInt("orderId");
				inData.setInID(inId);
				inData.setOrderId(orderId);
			}
			trx.commit();
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			getPrimaryHardeningLabelResponse.setError(INTERNAL_SERVER_ERROR);
			getPrimaryHardeningLabelResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return getPrimaryHardeningLabelResponseDocument;
	}

	@Override
	public AddSecondaryHardeningDetailResponseDocument addSecondaryHardeningDetail(
			AddSecondaryHardeningDetailRequestDocument req) {
		AddSecondaryHardeningDetailResponseDocument addSecondaryHardeningDetailResponseDocument = AddSecondaryHardeningDetailResponseDocument.Factory
				.newInstance();
		AddSecondaryHardeningDetailResponse addSecondaryHardeningDetailResponse = addSecondaryHardeningDetailResponseDocument
				.addNewAddSecondaryHardeningDetailResponse();
		AddSecondaryHardeningDetailRequest loginRequest = req.getAddSecondaryHardeningDetailRequest();
		AddSecondaryHardeningDetail addSecondaryHardeningDetail = loginRequest.getAddSecondaryHardeningDetail();
		String outUUId = safeTrim(loginRequest.getOutUUId());
		String cultureProcessedNumber = safeTrim(loginRequest.getCultureProcessedNo());
		String yearCode = safeTrim(addSecondaryHardeningDetail.getYearCode());
		int cropTypeId = 0;
		int varietyId = 0;
		String parentCultureLine = safeTrim(addSecondaryHardeningDetail.getParentCultureLine());
		String serialNumber = safeTrim(addSecondaryHardeningDetail.getSerialNumber());
		String operationDate = safeTrim(addSecondaryHardeningDetail.getOperationDate());
		int cultureStageId = 0;
		int productId = 0;
		int discardQty = 0;
		int fromLocatorId = 0;
		int toLocatorId = 0;
		int documentType = 0;
		int oldProductId = 0;
		String plotNumberTray = "",parentUUid = "";
		int qty = 0;
		int inId = 0;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Trx trx = null;
		PreparedStatement pstm1 = null, pstm2 = null;
		ResultSet rs1 = null, rs2 = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
			int orgId = Env.getAD_Org_ID(ctx);
			int clientId = Env.getAD_Client_ID(ctx);

			
			qty = loginRequest.getQuantity();
			inId = loginRequest.getInId();
			cropTypeId = addSecondaryHardeningDetail.getPlantSpeciesId();
			varietyId = addSecondaryHardeningDetail.getVarietyId();
			cultureStageId = addSecondaryHardeningDetail.getCultureStageId();
			BigDecimal secQty = new BigDecimal(qty);
			int secondaryLocatorId = TCUtills.getLocatorId(clientId, TABLE_LOCATOR, DEFAULT_LOCATOR_SH);
			if (secondaryLocatorId == 0) {
				addSecondaryHardeningDetailResponse
						.setError("Locator serachKey not found in table record " + DEFAULT_LOCATOR_SH + "");
				addSecondaryHardeningDetailResponse.setIsError(true);
				return addSecondaryHardeningDetailResponseDocument;
			}
			
			if (containsMaliciousPattern(operationDate)) {
				addSecondaryHardeningDetailResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				addSecondaryHardeningDetailResponse.setIsError(true);
			    return addSecondaryHardeningDetailResponseDocument;
			}
			
			if (containsMaliciousPattern(serialNumber)) {
				addSecondaryHardeningDetailResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				addSecondaryHardeningDetailResponse.setIsError(true);
			    return addSecondaryHardeningDetailResponseDocument;
			}
			
			if (containsMaliciousPattern(parentCultureLine)) {
				addSecondaryHardeningDetailResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				addSecondaryHardeningDetailResponse.setIsError(true);
			    return addSecondaryHardeningDetailResponseDocument;
			}
			
			if (containsMaliciousPattern(cultureProcessedNumber)) {
				addSecondaryHardeningDetailResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				addSecondaryHardeningDetailResponse.setIsError(true);
			    return addSecondaryHardeningDetailResponseDocument;
			}
			
			if (containsMaliciousPattern(yearCode)) {
				addSecondaryHardeningDetailResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				addSecondaryHardeningDetailResponse.setIsError(true);
			    return addSecondaryHardeningDetailResponseDocument;
			}
			
			if (!TCUtills.isValidLocationName(yearCode)) {
				addSecondaryHardeningDetailResponse.setError("Invalid Year Code format");
				addSecondaryHardeningDetailResponse.setIsError(true);
				return addSecondaryHardeningDetailResponseDocument;
	        }
			
			if (!TCUtills.isValidLocationName(cultureProcessedNumber)) {
				addSecondaryHardeningDetailResponse.setError("Invalid Culture Processed Number format");
				addSecondaryHardeningDetailResponse.setIsError(true);
				return addSecondaryHardeningDetailResponseDocument;
	        }
			
			if (!TCUtills.isValidNameNumber(parentCultureLine)) {
				addSecondaryHardeningDetailResponse.setError("Invalid Parent Culture Line format");
				addSecondaryHardeningDetailResponse.setIsError(true);
				return addSecondaryHardeningDetailResponseDocument;
	        }
			
			if (!TCUtills.isValidLocationName(serialNumber)) {
				addSecondaryHardeningDetailResponse.setError("Invalid Serial Number format");
				addSecondaryHardeningDetailResponse.setIsError(true);
				return addSecondaryHardeningDetailResponseDocument;
	        }
			
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			
			toLocatorId = TCUtills.getLocatorId(clientId, TABLE_LOCATOR, DEFAULT_LOCATOR_HD);
			documentType = TCUtills.getDocTypeId(clientId, DOCUMENT_MATERIAL_MOVEMENT);

			Date date = (Date) dateFormat.parse(operationDate);
			long timestampValue = date.getTime();
			Timestamp timestamp = new Timestamp(timestampValue);

//			String sql1 = "SELECT name,m_product_id AS pId FROM adempiere.m_product WHERE name = '" + DEFAULT_PRODUCT_SH
//					+ "' AND ad_client_id = ?;";
//			pstm1 = DB.prepareStatement(sql1, null);
//			rs1 = pstm1.executeQuery();
//			while (rs1.next()) {
//				productId = rs1.getInt("pId");
//			}
			
			final String sqlProduct =
	                "SELECT m_product_id FROM adempiere.m_product WHERE name = ? AND ad_client_id = ?";
	        try (PreparedStatement ps = DB.prepareStatement(sqlProduct, trxName)) {
	            ps.setString(1, DEFAULT_PRODUCT_SH);
	            ps.setInt(2, clientId);
	            try (ResultSet rs = ps.executeQuery()) {
	                if (rs.next()) productId = rs.getInt(1);
	            }
	        }
	        if (productId <= 0) {
	        	addSecondaryHardeningDetailResponse.setError("Product is not available");
	        	addSecondaryHardeningDetailResponse.setIsError(true);
	        	return addSecondaryHardeningDetailResponseDocument;
	        }

			int recordId = TCUtills.getId(TABLE_TC_OUT, outUUId, clientId);
			if (recordId == 0) {
				addSecondaryHardeningDetailResponse
						.setError("Out Label UUId not found in table record " + outUUId + "");
				addSecondaryHardeningDetailResponse.setIsError(true);
				return addSecondaryHardeningDetailResponseDocument;
			}
			TCOut pOut = new TCOut(ctx, recordId, trx.getTrxName());
			oldProductId = pOut.getM_Product_ID();
			fromLocatorId = pOut.getM_Locator_ID();
			BigDecimal iQty = pOut.getQuantity();
			discardQty = iQty.intValue() - qty;
			BigDecimal discardQtys = new BigDecimal(discardQty);
			if (discardQty < 0) {
				addSecondaryHardeningDetailResponse
						.setError("Enter Quantity : " + qty + " and available Quantity : " + iQty.intValue() + "");
				addSecondaryHardeningDetailResponse.setIsError(true);
				return addSecondaryHardeningDetailResponseDocument;
			}

//			String sql2 = "SELECT ph.tc_primaryhardeningLabel_id AS id,ph.plotNumberTray AS plotNumberTray,ph.c_uuid AS parentuuid,\n"
//					+ "o.c_uuid AS OutUUId FROM adempiere.tc_primaryhardeningLabel ph\n"
//					+ "JOIN adempiere.tc_out o ON o.tc_out_id = ph.tc_out_id\n" + "WHERE ph.ad_client_id = ?"
//					+ " AND o.c_uuid = '" + outUUId + "';";
//			pstm2 = DB.prepareStatement(sql2, null);
//			rs2 = pstm2.executeQuery();
//			while (rs2.next()) {
//				plotNumberTray = rs2.getString("plotNumberTray");
//				parentUUid = rs2.getString("parentuuid");
//			}
			
			final String sqlPh =
	                "SELECT ph.plotNumberTray, ph.c_uuid AS parentuuid " +
	                "FROM adempiere.tc_primaryhardeningLabel ph " +
	                "JOIN adempiere.tc_out o ON o.tc_out_id = ph.tc_out_id " +
	                "WHERE ph.ad_client_id = ? AND o.c_uuid = ?";

	        try (PreparedStatement ps = DB.prepareStatement(sqlPh, trxName)) {
	            ps.setInt(1, clientId);
	            ps.setString(2, outUUId);
	            try (ResultSet rs = ps.executeQuery()) {
	                if (rs.next()) {
	                    plotNumberTray = rs.getString("plotNumberTray");
	                    parentUUid = rs.getString("parentuuid");
	                }
	            }
	        }
			
			TCIn in = new TCIn(ctx, inId, trx.getTrxName());
			int orderId = in.getTC_order_ID();
			int uomId = in.getC_UOM_ID();

			TCOut out = new TCOut(ctx, 0, trx.getTrxName());
			out.setTC_order_ID(orderId);
			out.setAD_Org_ID(orgId);
			out.setTC_in_ID(inId);
			out.setM_Locator_ID(secondaryLocatorId);
			out.setM_Product_ID(productId);
			out.setQuantity(secQty);
			out.setdiscardqty(discardQtys);
			out.setC_UOM_ID(uomId);
			out.setcycle(2);
			if (!out.save()) {
				throw new Exception("Failed to add out data: " + out);
			}
			trx.commit();
			int outId = out.get_ID();

			TCSecondaryHardeningLabel hLabel = new TCSecondaryHardeningLabel(ctx, 0, trx.getTrxName());
			hLabel.setAD_Org_ID(orgId);
			hLabel.setcultureprocessednumber(cultureProcessedNumber);
			hLabel.setyearcode(yearCode);
			hLabel.settc_species_id(cropTypeId);
			hLabel.setTC_Variety_ID(varietyId);
			hLabel.setparentcultureline(parentCultureLine);
			hLabel.setserialnumber(serialNumber);
			hLabel.setplotnumbertray(plotNumberTray);
			hLabel.setTC_CultureStage_ID(cultureStageId);
			hLabel.setoperationdate(timestamp);
			hLabel.setTC_in_ID(inId);
			hLabel.setTC_out_ID(outId);
			hLabel.setparentuuid(parentUUid);
			if (!hLabel.save()) {
				throw new Exception("Failed to add Secondary Hardening Label data: " + hLabel);
			}
			trx.commit();
			int sHLabelId = hLabel.get_ID();
			addSecondaryHardeningDetailResponse.setSecondaryHardeningLabelId(sHLabelId);
			addSecondaryHardeningDetailResponse.setTCOrderId(orderId);
			trx.commit();

			int retryCount = 0;
			String sHLUUid = null, newOutUUId = null;
			while (newOutUUId == null && sHLUUid == null && retryCount < 10) {
				sHLUUid = DB.getSQLValueString(null,
						"SELECT c_uuid FROM " + TABLE_SH + " WHERE " + TABLE_SH + "_id = ?", sHLabelId);
				newOutUUId = DB.getSQLValueString(null,
						"SELECT c_uuid FROM " + TABLE_TC_OUT + " WHERE " + TABLE_TC_OUT + "_id = ?", outId);
				if (sHLUUid == null && newOutUUId == null) {
					Thread.sleep(900); // Wait for 500ms before retrying
					retryCount++;
				}
			}
			if (sHLUUid == null) {
				throw new Exception("Failed to get UUID for Primary Hardening ID: " + sHLabelId);
			}
			trx.commit();
			addSecondaryHardeningDetailResponse.setOutUUId(newOutUUId);
			addSecondaryHardeningDetailResponse.setSecondaryHardeningLabelUUId(sHLUUid);

			if (discardQty > 0) {
				MMovement move = new MMovement(ctx, 0, trx.getTrxName());
				move.setAD_Org_ID(orgId);
				move.setDateReceived(timestamp);
				move.setC_DocType_ID(documentType);
				move.setM_Warehouse_ID(Env.getContextAsInt(ctx, Env.M_WAREHOUSE_ID));
				move.setM_WarehouseTo_ID(Env.getContextAsInt(ctx, Env.M_WAREHOUSE_ID));
				if (!move.save()) {
					throw new Exception("Failed to add Internal move data: " + move);
				}
				trx.commit();

				moveInventory(ctx, trxName, move, discardQtys, oldProductId, fromLocatorId, toLocatorId);

				move.saveEx();
				move.setDocStatus(DocAction.ACTION_Complete);
				move.setDocAction(DocAction.ACTION_Close);
				move.setPosted(true);
				move.setProcessed(true);
				move.setIsApproved(true);
				move.completeIt();
				move.saveEx();
			}
			addSecondaryHardeningDetailResponse.setIsError(false);
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			addSecondaryHardeningDetailResponse.setError(INTERNAL_SERVER_ERROR);
			addSecondaryHardeningDetailResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm1, rs1);
			closeDbCon(pstm2, rs2);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return addSecondaryHardeningDetailResponseDocument;
	}

	@Override
	public GetUpcomingVisitResponseDocument getUpcomingVisit(GetUpcomingVisitRequestDocument req) {
		GetUpcomingVisitResponseDocument getUpcomingVisitResponseDocument = GetUpcomingVisitResponseDocument.Factory
				.newInstance();
		GetUpcomingVisitResponse getUpcomingVisitResponse = getUpcomingVisitResponseDocument
				.addNewGetUpcomingVisitResponse();
		GetUpcomingVisitRequest loginRequest = req.getGetUpcomingVisitRequest();
		Trx trx = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String searchKey = loginRequest.getSearchKey();
		int count = 0;
		int pageSize = loginRequest.getPageSize(); // Number of records per page
		int pageNumber = loginRequest.getPageNumber(); // Current page number
		int offset = (pageNumber - 1) * pageSize; 
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
		int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			List<String> farmerList = new ArrayList<>();
			List<String> mobilenoList = new ArrayList<>();
			List<String> dateList = new ArrayList<>();
			List<String> visitTypeList = new ArrayList<>();
			List<String> villageName2List = new ArrayList<>();
			List<String> talukNameList = new ArrayList<>();
			List<String> cityNameList = new ArrayList<>();
			List<String> districtList = new ArrayList<>();
			List<String> stateList = new ArrayList<>();
			List<String> pinCodeList = new ArrayList<>();

			for (Filter filter : loginRequest.getFiltersArray()) {
				switch (filter.getKey().toLowerCase()) {
				case "farmername":
					farmerList.add("'" + filter.getValue() + "'");
					break;
				case "mobileno":
					mobilenoList.add("'" + filter.getValue() + "'");
					break;
				case "date":
					dateList.add("'" + filter.getValue() + "'");
					break;
				case "visittypename":
					visitTypeList.add("'" + filter.getValue() + "'");
					break;
				case "villagename":
					villageName2List.add("'" + filter.getValue() + "'");
					break;
				case "talukname":
					talukNameList.add("'" + filter.getValue() + "'");
					break;
				case "cityname":
					cityNameList.add("'" + filter.getValue() + "'");
					break;
				case "district":
					districtList.add("'" + filter.getValue() + "'");
					break;
				case "state":
					stateList.add("'" + filter.getValue() + "'");
					break;
				case "pincode":
					pinCodeList.add("'" + filter.getValue() + "'");
					break;
				}
			}

			StringBuilder sql = new StringBuilder(
					"SELECT v.tc_visit_id AS id,v.mobileno AS mobileNo,v.date AS date,f.name AS farmerName,v.cycleNo AS cycleNo,\n"
							+ "vt.name AS visitTypeName,s.name AS Status,f.tc_farmer_id AS farmerId,f.villagename AS villagename,f.landmark AS landmark,\n"
							+ "f.villageName2 As villageName2,f.talukName As talukName,f.city as city,f.district as district,f.state as state,f.pinCode As pincode,v.visitdone As visitdone,COUNT(*) OVER () AS totalCount	\n"
							+ "FROM adempiere.tc_visit v \n"
							+ "JOIN adempiere.tc_farmer f ON f.tc_farmer_id = v.tc_farmer_id \n"
							+ "JOIN adempiere.tc_status s ON s.tc_status_id = v.tc_status_id \n"
							+ "JOIN adempiere.tc_visittype vt ON vt.tc_visittype_id = v.tc_visittype_id\n"
							+ "JOIN adempiere.ad_user u ON u.ad_user_id = v.createdby \n" + "WHERE v.ad_client_id = ? AND u.name = ? "
							+ " AND s.name <> 'Cancelled' AND s.name <> 'Completed' AND v.date >= CURRENT_DATE");

			if (!farmerList.isEmpty()) {
				sql.append(" AND f.name IN (")
				   .append(farmerList.stream().map(x -> "?").collect(Collectors.joining(",")))
				   .append(")");
			}
			if (!mobilenoList.isEmpty()) {
				sql.append(" AND v.mobileno IN (")
				   .append(mobilenoList.stream().map(x -> "?").collect(Collectors.joining(",")))
				   .append(")");
			}
			if (!dateList.isEmpty()) {
				sql.append(" AND v.date IN (")
				   .append(dateList.stream().map(x -> "?").collect(Collectors.joining(",")))
				   .append(")");
			}
			if (!visitTypeList.isEmpty()) {
				sql.append(" AND vt.name IN (")
				   .append(visitTypeList.stream().map(x -> "?").collect(Collectors.joining(",")))
				   .append(")");
			}
			if (!villageName2List.isEmpty()) {
				sql.append(" AND f.villageName2 IN (")
				   .append(villageName2List.stream().map(x -> "?").collect(Collectors.joining(",")))
				   .append(")");
			}
			if (!talukNameList.isEmpty()) {
				sql.append(" AND f.talukName IN (")
				   .append(talukNameList.stream().map(x -> "?").collect(Collectors.joining(",")))
				   .append(")");
			}
			if (!cityNameList.isEmpty()) {
				sql.append(" AND f.city IN (")
				   .append(cityNameList.stream().map(x -> "?").collect(Collectors.joining(",")))
				   .append(")");
			}
			if (!districtList.isEmpty()) {
				sql.append(" AND f.district IN (")
				   .append(districtList.stream().map(x -> "?").collect(Collectors.joining(",")))
				   .append(")");
			}
			if (!stateList.isEmpty()) {
				sql.append(" AND f.state IN (")
				   .append(stateList.stream().map(x -> "?").collect(Collectors.joining(",")))
				   .append(")");
			}
			if (!pinCodeList.isEmpty()) {
				sql.append(" AND f.pincode IN (")
				   .append(pinCodeList.stream().map(x -> "?").collect(Collectors.joining(",")))
				   .append(")");
			}
			if (searchKey != null && !searchKey.trim().isEmpty()) {
				sql.append(
						" AND (f.name ILIKE ? OR f.pincode ILIKE ? OR v.mobileno ILIKE ? OR vt.name ILIKE ? OR f.villageName2 ILIKE ? OR f.talukName ILIKE ? OR f.city ILIKE ? OR f.district ILIKE ? OR f.state ILIKE ?)");
			}
			sql.append(" order by v.tc_visit_id desc");
			sql.append(" LIMIT ? OFFSET ?");

			pstm = DB.prepareStatement(sql.toString(), null);
			
			int userId = Env.getAD_User_ID(ctx);
			MUser user = new MUser(ctx, userId, null);
			
			int parameterIndex = 1;
			pstm.setInt(parameterIndex++, clientId);
			pstm.setString(parameterIndex++, user.getName());
			
			for (String f : farmerList) pstm.setString(parameterIndex++, f);
			for (String m : mobilenoList) pstm.setString(parameterIndex++, m);
			for (String d : dateList) pstm.setString(parameterIndex++, d);
			for (String vt : visitTypeList) pstm.setString(parameterIndex++, vt);
			for (String vn : villageName2List) pstm.setString(parameterIndex++, vn);
			for (String tn : talukNameList) pstm.setString(parameterIndex++, tn);
			for (String cn : cityNameList) pstm.setString(parameterIndex++, cn);
			for (String d : districtList) pstm.setString(parameterIndex++, d);
			for (String s : stateList) pstm.setString(parameterIndex++, s);
			for (String pc : pinCodeList) pstm.setString(parameterIndex++, pc);
			
			if (searchKey != null && !searchKey.trim().isEmpty()) {
				for (int i = 0; i < 9; i++) {
					pstm.setString(parameterIndex++, "%" + searchKey + "%");
				}
			}
			pstm.setInt(parameterIndex++, pageSize);
	        pstm.setInt(parameterIndex++, offset);
			rs = pstm.executeQuery();

			if (!rs.isBeforeFirst()) {
				getUpcomingVisitResponse.setIsError(false);
				getUpcomingVisitResponse.setCount(count);
				getUpcomingVisitResponse.addNewGetUpcomingVisitData();
				return getUpcomingVisitResponseDocument;
			}

			while (rs.next()) {
				GetUpcomingVisitData listOfVisits = getUpcomingVisitResponse.addNewGetUpcomingVisitData();
				int visitId = rs.getInt("id");
				String name = rs.getString("farmerName");
				String visitType = rs.getString("visitTypeName");
				String date = rs.getString("date");
				String mobileNo = rs.getString("mobileNo");
				int farmerId = rs.getInt("farmerId");
				String villageName2 = rs.getString("villageName2");
				String talukName = rs.getString("talukName");
				String city = rs.getString("city");
				String district = rs.getString("district");
				String state = rs.getString("state");
				String pincode = rs.getString("pincode");
				count = rs.getInt("totalCount");
				Boolean visitDone = rs.getBoolean("visitdone");
				int cycleNo = rs.getInt("cycleNo");

				listOfVisits.setVisitId(visitId);
				listOfVisits.setFarmerId(farmerId);
				listOfVisits.setFarmerName(name);
				listOfVisits.setVisitTypeName(visitType);
				listOfVisits.setDate(date);
				listOfVisits.setMobileNo(mobileNo);
				listOfVisits.setVillageName(villageName2 != null ? villageName2 : "");
				listOfVisits.setTalukName(talukName != null ? talukName : "");
				listOfVisits.setCityName(city != null ? city : "");
				listOfVisits.setDistrict(district != null ? district : "");
				listOfVisits.setState(state != null ? state : "");
				listOfVisits.setPincode(pincode != null ? pincode : "");
				listOfVisits.setVisitDone(visitDone);
				listOfVisits.setCycleNo(cycleNo);
			}
			trx.commit();
			getUpcomingVisitResponse.setCount(count);
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			getUpcomingVisitResponse.setError(INTERNAL_SERVER_ERROR);
			getUpcomingVisitResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return getUpcomingVisitResponseDocument;
	}

	@Override
	public ChangeVisitDateResponseDocument changeVisitDate(ChangeVisitDateRequestDocument req) {
		ChangeVisitDateResponseDocument changeVisitDateResponseDocument = ChangeVisitDateResponseDocument.Factory
				.newInstance();
		ChangeVisitDateResponse changeVisitDateResponse = changeVisitDateResponseDocument
				.addNewChangeVisitDateResponse();
		ChangeVisitDateRequest loginRequest = req.getChangeVisitDateRequest();
		int visitId = 0;
		String changeDate = safeTrim(loginRequest.getChangeDate());
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String reason = loginRequest.getReason();
		Trx trx = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			visitId = loginRequest.getVisitId();
			
			if (containsMaliciousPattern(changeDate)) {
				changeVisitDateResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				changeVisitDateResponse.setIsError(true);
			    return changeVisitDateResponseDocument;
			}
			
//			if (containsMaliciousPattern(reason)) {
//				changeVisitDateResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
//				changeVisitDateResponse.setIsError(true);
//			    return changeVisitDateResponseDocument;
//			}
			
			if (!TCUtills.isValidLocationName(reason)) {
				changeVisitDateResponse.setError("Invalid Reason format");
				changeVisitDateResponse.setIsError(true);
				return changeVisitDateResponseDocument;
	        }
			
			Date date = (Date) dateFormat.parse(changeDate);
			long timestampValue = date.getTime();
			Timestamp timestamp = new Timestamp(timestampValue);

			TCVisit visit = new TCVisit(ctx, visitId, trx.getTrxName());
			visit.setdate(timestamp);
			visit.setreason(reason);
			visit.saveEx();
			trx.commit();
			changeVisitDateResponse.setIsError(false);
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			changeVisitDateResponse.setError(INTERNAL_SERVER_ERROR);
			changeVisitDateResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return changeVisitDateResponseDocument;
	}

	@Override
	public CancelVisitResponseDocument cancelVisit(CancelVisitRequestDocument req) {
		CancelVisitResponseDocument cancelVisitResponseDocument = CancelVisitResponseDocument.Factory.newInstance();
		CancelVisitResponse cancelVisitResponse = cancelVisitResponseDocument.addNewCancelVisitResponse();
		CancelVisitRequest loginRequest = req.getCancelVisitRequest();
		int visitId = 0;
		String reason = loginRequest.getReason();
		Trx trx = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
		int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			visitId = loginRequest.getVisitId();
			int cancelId = TCUtills.getRecordId(clientId, TABLE_STATUS, STATUS_CANCELLED);
			if (cancelId == 0) {
				cancelVisitResponse.setError("Status id is not in table record " + STATUS_CANCELLED + "");
				cancelVisitResponse.setIsError(true);
				return cancelVisitResponseDocument;
			}
			
			if (!TCUtills.isValidLocationName(reason)) {
				cancelVisitResponse.setError("Invalid Reason format");
				cancelVisitResponse.setIsError(true);
				return cancelVisitResponseDocument;
	        }
			
			TCVisit visit = new TCVisit(ctx, visitId, trx.getTrxName());
			visit.setTC_Status_ID(cancelId);
			visit.setreason(reason);
			visit.saveEx();
			trx.commit();
			cancelVisitResponse.setIsError(false);
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			cancelVisitResponse.setError(INTERNAL_SERVER_ERROR);
			cancelVisitResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return cancelVisitResponseDocument;
	}

	@Override
	public AddNextVisitDatetForFVResponseDocument addNextVisitDatetForFV(AddNextVisitDatetForFVRequestDocument req) {
		AddNextVisitDatetForFVResponseDocument addNextVisitDatetForFVResponseDocument = AddNextVisitDatetForFVResponseDocument.Factory
				.newInstance();
		AddNextVisitDatetForFVResponse addNextVisitDatetForFVResponse = addNextVisitDatetForFVResponseDocument
				.addNewAddNextVisitDatetForFVResponse();
		AddNextVisitDatetForFVRequest loginRequest = req.getAddNextVisitDatetForFVRequest();
		int firstVisitId = 0;
		int cycleNo = loginRequest.getCycleNo();
		Trx trx = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String nextVisitDate = loginRequest.getNextVisitDate();
		int visitTypeId = loginRequest.getVisitTypeId();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
			int orgId = Env.getAD_Org_ID(ctx);
			int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			firstVisitId = loginRequest.getFirstVisitId();
			int statusId = TCUtills.getRecordId(clientId,TABLE_STATUS, STATUS_IN_PROGRESS);
			if (statusId == 0) {
				addNextVisitDatetForFVResponse.setError("Status id is not in table record " + STATUS_IN_PROGRESS + "");
				addNextVisitDatetForFVResponse.setIsError(true);
				return addNextVisitDatetForFVResponseDocument;
			}
			
			if (containsMaliciousPattern(nextVisitDate)) {
				addNextVisitDatetForFVResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				addNextVisitDatetForFVResponse.setIsError(true);
			    return addNextVisitDatetForFVResponseDocument;
			}


			int intermediateVisitId = TCUtills.getRecordId(clientId, TABLE_VISIT_TYPE, INTERMEDIATE_VISIT);
			if (intermediateVisitId == 0) {
				addNextVisitDatetForFVResponse
						.setError("Intermediate visit type is not in table record " + INTERMEDIATE_VISIT + "");
				addNextVisitDatetForFVResponse.setIsError(true);
				return addNextVisitDatetForFVResponseDocument;
			}

			int collectionVisitId = TCUtills.getRecordId(clientId, TABLE_VISIT_TYPE, COLLECTION_VISIT);
			if (collectionVisitId == 0) {
				addNextVisitDatetForFVResponse
						.setError("Collection visit type is not in table record " + COLLECTION_VISIT + "");
				addNextVisitDatetForFVResponse.setIsError(true);
				return addNextVisitDatetForFVResponseDocument;
			}

			Date date = (Date) dateFormat.parse(nextVisitDate);
			long timestampValue = date.getTime();
			Timestamp timestamp = new Timestamp(timestampValue);

			TCFirstVisit firstVisit = new TCFirstVisit(ctx, firstVisitId, trx.getTrxName());
			firstVisit.setnextvisitdate(timestamp);
			int farmerId = firstVisit.getTC_Farmer_ID();
			firstVisit.saveEx();
			trx.commit();

			TCFarmer farmer = new TCFarmer(ctx, farmerId, trx.getTrxName());
			String farmerName = farmer.getName();
			String mobileNo = farmer.getmobileno();

			if (!TCUtills.canCreateVisit(farmerId, cycleNo, visitTypeId)) {
				addNextVisitDatetForFVResponse.setError("only one first and collection visit allowed per cycle");
				addNextVisitDatetForFVResponse.setIsError(true);
				return addNextVisitDatetForFVResponseDocument;
			}

			TCVisit visit = new TCVisit(ctx, 0, trx.getTrxName());
			visit.setAD_Org_ID(orgId);
			visit.setName(farmerName);
			visit.setTC_VisitType_ID(visitTypeId);
			visit.setdate(timestamp);
			visit.setmobileno(mobileNo);
			visit.setTC_Farmer_ID(farmerId);
			visit.setTC_Status_ID(statusId);
			visit.setcycleno(cycleNo);
			if (!visit.save()) {
				throw new Exception("Failed to add Visit data: " + visit);
			}
			trx.commit();
			int visitId = visit.get_ID();
			if (visitTypeId == intermediateVisitId) {
				TCIntermediateVisit intermediateVisit = new TCIntermediateVisit(ctx, 0, trx.getTrxName());
				intermediateVisit.setAD_Org_ID(orgId);
				intermediateVisit.setTC_Farmer_ID(farmerId);
				intermediateVisit.setTC_Visit_ID(visitId);
				intermediateVisit.setTC_FirstVisit_ID(firstVisitId);
				if (!intermediateVisit.save()) {
					throw new Exception("Failed to add Intermediate Visit data: " + intermediateVisit);
				}
				trx.commit();
				intermediateVisit.setName("IntermediateVisit_" + intermediateVisit.get_ID() + "_" + nextVisitDate);
				intermediateVisit.saveEx();
				trx.commit();
				addNextVisitDatetForFVResponse.setIntermediateVisitId(intermediateVisit.get_ID());

				String sql = "SELECT tc_plantdetails_id AS id FROM adempiere.tc_firstjoinplant\n"
						+ "WHERE ad_client_id = ? AND tc_firstvisit_id = ?;";
				pstm = DB.prepareStatement(sql, null);
				pstm.setInt(1, clientId);
				pstm.setInt(2, firstVisitId);
				rs = pstm.executeQuery();
				while (rs.next()) {
					int plantId = rs.getInt("id");
					TCIntermediateJoinPlantDetails intermediateJoinPlantDetails = new TCIntermediateJoinPlantDetails(
							ctx, 0, trx.getTrxName());
					intermediateJoinPlantDetails.setAD_Org_ID(orgId);
					intermediateJoinPlantDetails.setTC_IntermediateVisit_ID(intermediateVisit.get_ID());
					intermediateJoinPlantDetails.setTC_PlantDetails_ID(plantId);
					intermediateJoinPlantDetails.saveEx();
				}
				closeDbCon(pstm, rs);

			} else if (visitTypeId == collectionVisitId) {
				TCCollectionVisit collectionVisit = new TCCollectionVisit(ctx, 0, trx.getTrxName());
				collectionVisit.setAD_Org_ID(orgId);
				collectionVisit.setTC_Farmer_ID(farmerId);
				collectionVisit.setTC_Visit_ID(visitId);
				collectionVisit.setTC_FirstVisit_ID(firstVisitId);
				if (!collectionVisit.save()) {
					throw new Exception("Failed to add Collection Visit data: " + collectionVisit);
				}
				trx.commit();
				collectionVisit.setName("CollectionVisit_" + collectionVisit.get_ID() + "_" + nextVisitDate);
				collectionVisit.saveEx();
				trx.commit();

				TCFirstVisit firstVisits = new TCFirstVisit(ctx, firstVisitId, trxName);
				firstVisits.setisattachedintermediate(true);
				firstVisits.saveEx();
				trx.commit();

				addNextVisitDatetForFVResponse.setCollectionVisitId(collectionVisit.get_ID());

				String sql = "SELECT tc_plantdetails_id AS id FROM adempiere.tc_firstjoinplant\n"
						+ "WHERE ad_client_id = ? AND tc_firstvisit_id = ?;";
				pstm = DB.prepareStatement(sql, null);
				pstm.setInt(1, clientId);
				pstm.setInt(2, firstVisitId);
				rs = pstm.executeQuery();
				while (rs.next()) {
					int plantId = rs.getInt("id");
					TCCollectionJoinPlantDetails collectJoin = new TCCollectionJoinPlantDetails(ctx, 0,
							trx.getTrxName());
					collectJoin.setAD_Org_ID(orgId);
					collectJoin.setTC_CollectionDetails_ID(collectionVisit.get_ID());
					collectJoin.setTC_PlantDetails_ID(plantId);
					collectJoin.saveEx();
				}
				closeDbCon(pstm, rs);
			}
			addNextVisitDatetForFVResponse.setNewVisitId(visitId);
			addNextVisitDatetForFVResponse.setIsError(false);
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			addNextVisitDatetForFVResponse.setError(INTERNAL_SERVER_ERROR);
			addNextVisitDatetForFVResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return addNextVisitDatetForFVResponseDocument;
	}

	@Override
	public GetFirstVisitByIdResponseDocument getFirstVisitById(GetFirstVisitByIdRequestDocument req) {
		GetFirstVisitByIdResponseDocument getFirstVisitResponseDocument = GetFirstVisitByIdResponseDocument.Factory
				.newInstance();
		GetFirstVisitByIdResponse getFirstVisitResponse = getFirstVisitResponseDocument
				.addNewGetFirstVisitByIdResponse();
		GetFirstVisitByIdRequest loginRequest = req.getGetFirstVisitByIdRequest();
		int firstVisitId = 0;
		MAttachment attachment = null;
//		String base64 = "";
		int tableId = MTable.getTable_ID(TABLE_FARMER);
		Trx trx = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
			int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			firstVisitId = loginRequest.getFirstVisitId();
			
			int recordId = TCUtills.getIdss(TABLE_FIRST_VISIT,firstVisitId,clientId);
			if (recordId == 0) {
				getFirstVisitResponse.setError("First visit id not valid");
				getFirstVisitResponse.setIsError(true);
				return getFirstVisitResponseDocument;
			}		
					
			String sql = "SELECT vi.cycleNo AS cycleNo,fv.tc_firstvisit_id,fv.c_uuid AS UUId,fv.isattachedintermediate As attachedintermediate,fv.plantno AS plantNo,fv.visitdate AS visitDate,fv.nextvisitdate AS nextVisitDate,fv.pesthistory AS pestHistory,\n"
					+ "fs.name AS fieldSelectionName,vi.visitdone As visitdone,st.name AS soilTypeName,wt.name AS wateringTypeName,fm.name AS fieldManagementName,fv.enterDetailsOfInfestation AS enterDetailsOfInfestation,fv.tc_farmer_id As farmerId\n"
					+ "FROM adempiere.tc_firstvisit fv JOIN adempiere.tc_visit vi ON vi.tc_visit_id = fv.tc_visit_id\n"
					+ "LEFT JOIN adempiere.tc_fieldselection fs ON fs.tc_fieldselection_id = fv.tc_fieldselection_id\n"
					+ "LEFT JOIN adempiere.tc_soiltype st ON st.tc_soiltype_id = fv.tc_soiltype_id\n"
					+ "LEFT JOIN adempiere.tc_wateringtype wt ON wt.tc_wateringtype_id = fv.tc_wateringtype_id\n"
					+ "LEFT JOIN adempiere.tc_fieldmanagement fm ON fm.tc_fieldmanagement_id = fv.tc_fieldmanagement_id\n"
					+ "WHERE fv.ad_client_id = ? AND fv.tc_firstvisit_id = ?";
			pstmt = DB.prepareStatement(sql, null);
			pstmt.setInt(1, clientId);
			pstmt.setInt(2, firstVisitId);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				GetFirstVisitData listOfVisits = getFirstVisitResponse.addNewGetFirstVisitData();
				String plantNo = rs.getString("plantNo");
				String visitDate = rs.getString("visitDate");
				String nextVisitDate = rs.getString("nextVisitDate");
				String fieldSelectionName = rs.getString("fieldSelectionName");
				String soilTypeName = rs.getString("soilTypeName");
				String wateringTypeName = rs.getString("wateringTypeName");
				String fieldManagementName = rs.getString("fieldManagementName");
				String pestHistory = rs.getString("pestHistory");
				String uuId = rs.getString("UUId");
				String enterDetailsOfInfestation = rs.getString("enterDetailsOfInfestation");
				int farmerId = rs.getInt("farmerId");
				Boolean attachedintermediate = rs.getBoolean("attachedintermediate");
				Boolean visitDone = rs.getBoolean("visitdone");
				int cycleNo = rs.getInt("cycleNo");

				listOfVisits.setFirstVisitId(firstVisitId);
				listOfVisits.setFarmerId(farmerId);
				listOfVisits.setVisitDate(visitDate);
				listOfVisits.setPlantNo(plantNo != null ? plantNo : "");
				listOfVisits.setNextVisitDate(nextVisitDate != null ? nextVisitDate : "");
				listOfVisits.setFieldSelectionName(fieldSelectionName != null ? fieldSelectionName : "");
				listOfVisits.setSoilTypeName(soilTypeName != null ? soilTypeName : "");
				listOfVisits.setWateringTypeName(wateringTypeName != null ? wateringTypeName : "");
				listOfVisits.setFieldManagementName(fieldManagementName != null ? fieldManagementName : "");
				listOfVisits.setUUId(uuId != null ? uuId : "");
				listOfVisits.setPestHistory(pestHistory != null ? pestHistory : "");
				listOfVisits.setEnterDetailsOfInfestation(
						enterDetailsOfInfestation != null ? enterDetailsOfInfestation : "");
				listOfVisits.setAttachedIntermediateVisit(attachedintermediate);
				listOfVisits.setVisitDone(visitDone);
				listOfVisits.setCycleNo(cycleNo);

				attachment = MAttachment.get(ctx, tableId, farmerId);
				if (attachment != null) {
					MAttachmentEntry[] entries = attachment.getEntries();
					listOfVisits.setImageRecordId(attachment.getRecord_ID());
					listOfVisits.setFarmerTableId(tableId);
//					if (entries.length > 0) {
					for (int i = entries.length - 1; i >= 0; i--) {
//						MAttachmentEntry entry = entries[i];
//						byte[] data = entry.getData();
//						base64 = Base64.getEncoder().encodeToString(data);
						ImageArray2 imageArray = listOfVisits.addNewImageArray1();
//						imageArray.setImage64(base64);
						imageArray.setImageIndexId(i);
					}
				} else
					listOfVisits.addNewImageArray1();
			}

			trx.commit();
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			getFirstVisitResponse.setError(INTERNAL_SERVER_ERROR);
			getFirstVisitResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstmt, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return getFirstVisitResponseDocument;
	}

	@Override
	public RejectPlantResponseDocument rejectPlant(RejectPlantRequestDocument req) {
		RejectPlantResponseDocument rejectPlantResponseDocument = RejectPlantResponseDocument.Factory.newInstance();
		RejectPlantResponse rejectPlantResponse = rejectPlantResponseDocument.addNewRejectPlantResponse();
		RejectPlantRequest loginRequest = req.getRejectPlantRequest();
		PreparedStatement pstm = null, pstm1 = null;
		ResultSet rs = null, rs1 = null;
		Trx trx = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
			int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			int plantStatusId = TCUtills.getRecordId(clientId, TABLE_PLANT_STATUS, TABLE_PLANT_STATUS_NAME);
			if (plantStatusId == 0) {
				rejectPlantResponse.setError("Plant Status id is not in table record " + TABLE_PLANT_STATUS_NAME + "");
				rejectPlantResponse.setIsError(true);
				return rejectPlantResponseDocument;
			}

			AddPlantIdAndReason[] addPlantIdAndReason = loginRequest.getAddPlantIdAndReasonArray();
			for (AddPlantIdAndReason data : addPlantIdAndReason) {
				int plantId = data.getPlantId();
				String reason = data.getReason();
				
//				if (containsMaliciousPattern(reason)) {
//					rejectPlantResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
//					rejectPlantResponse.setIsError(true);
//				    return rejectPlantResponseDocument;
//				}

				TCPlantDetail plant = new TCPlantDetail(ctx, plantId, trx.getTrxName());
				plant.setisRejected(true);
				plant.setreason(reason);
				plant.saveEx();
				trx.commit();
				rejectPlantResponse.setIsError(false);
				String sql = "SELECT c.tc_intermediatejoinplant_id As id FROM adempiere.tc_intermediatejoinplant c\n"
						+ "	JOIN adempiere.tc_plantdetails pd ON pd.tc_plantdetails_id = c.tc_plantdetails_id\n"
						+ "	WHERE c.tc_plantdetails_id = ? AND pd.isrejected = 'Y';";
				pstm = DB.prepareStatement(sql, null);
				pstm.setInt(1, plantId);
				rs = pstm.executeQuery();
				while (rs.next()) {
					int intermediateJoinId = rs.getInt("id");
					TCIntermediateJoinPlantDetails data1 = new TCIntermediateJoinPlantDetails(ctx, intermediateJoinId,
							trx.getTrxName());
					data1.setTC_plantstatus_ID(plantStatusId);
					data1.setDescription(reason);
					data1.saveEx();
					trx.commit();
				}

				String sql1 = "SELECT c.tc_collectionjoinplant_id As id FROM adempiere.tc_collectionjoinplant c\n"
						+ "	JOIN adempiere.tc_plantdetails pd ON pd.tc_plantdetails_id = c.tc_plantdetails_id\n"
						+ "	WHERE c.tc_plantdetails_id = ? AND pd.isrejected = 'Y'";
				pstm1 = DB.prepareStatement(sql1, null);
				pstm1.setInt(1, plantId);
				rs1 = pstm1.executeQuery();
				while (rs1.next()) {
					int collectionJoinId = rs1.getInt("id");
					TCCollectionJoinPlantDetails data2 = new TCCollectionJoinPlantDetails(ctx, collectionJoinId,
							trx.getTrxName());
					data2.setDescription(reason);
					data2.setTC_plantstatus_ID(plantStatusId);
					data2.saveEx();
					trx.commit();
				}
			}
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			rejectPlantResponse.setError(INTERNAL_SERVER_ERROR);
			rejectPlantResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			closeDbCon(pstm1, rs1);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return rejectPlantResponseDocument;
	}

	@Override
	public UpdateIntermediateVisitResponseDocument updateIntermediateVisit(UpdateIntermediateVisitRequestDocument req) {
		UpdateIntermediateVisitResponseDocument updateIntermediateVisitResponseDocument = UpdateIntermediateVisitResponseDocument.Factory
				.newInstance();
		UpdateIntermediateVisitResponse updateIntermediateVisitResponse = updateIntermediateVisitResponseDocument
				.addNewUpdateIntermediateVisitResponse();
		UpdateIntermediateVisitRequest loginRequest = req.getUpdateIntermediateVisitRequest();
		String healthMonitoringDetails = loginRequest.getHealthMonitoringDetails();
		int monitoringStateId = 0;
		String reason = loginRequest.getReason();
		int intermediateVisitId = 0;
		String nextVisitDate = loginRequest.getNextVisitDate();
		boolean visitDone = loginRequest.getVisitDone();
		Trx trx = null;
		Timestamp nextTimestamp = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
			int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			monitoringStateId = loginRequest.getMonitoringStateId();
			intermediateVisitId = loginRequest.getIntermediateVisitId();
			int visitCompletedId = TCUtills.getRecordId(clientId, TABLE_STATUS, STATUS_COMPLETED);
			if (visitCompletedId == 0) {
				updateIntermediateVisitResponse.setError("Status id is not in table record " + STATUS_COMPLETED + "");
				updateIntermediateVisitResponse.setIsError(true);
				return updateIntermediateVisitResponseDocument;
			}
			if (nextVisitDate != null && !nextVisitDate.isEmpty()) {
				Date nextDate = (Date) dateFormat.parse(nextVisitDate);
				long timestampValue = nextDate.getTime();
				nextTimestamp = new Timestamp(timestampValue);
			}
			
			if (containsMaliciousPattern(nextVisitDate)) {
				updateIntermediateVisitResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				updateIntermediateVisitResponse.setIsError(true);
			    return updateIntermediateVisitResponseDocument;
			}
			
			if (containsMaliciousPattern(healthMonitoringDetails)) {
				updateIntermediateVisitResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				updateIntermediateVisitResponse.setIsError(true);
			    return updateIntermediateVisitResponseDocument;
			}
			
//			if (containsMaliciousPattern(reason)) {
//				updateIntermediateVisitResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
//				updateIntermediateVisitResponse.setIsError(true);
//			    return updateIntermediateVisitResponseDocument;
//			}
			
			if (!TCUtills.isValidLocationName(healthMonitoringDetails)) {
				updateIntermediateVisitResponse.setError("Invalid Health Monitoring Details format");
				updateIntermediateVisitResponse.setIsError(true);
				return updateIntermediateVisitResponseDocument;
	        }
			
			TCIntermediateVisit intermediateVisit = new TCIntermediateVisit(ctx, intermediateVisitId, trx.getTrxName());
			intermediateVisit.setreviewdetails(healthMonitoringDetails);
			intermediateVisit.setTC_Decision_ID(monitoringStateId);
			intermediateVisit.setreasondetails(reason);
			if (nextVisitDate != null && !nextVisitDate.isEmpty()) {
				intermediateVisit.setnextvisitdate(nextTimestamp);
			}
			if (!intermediateVisit.save()) {
				throw new Exception("Failed to save Update Intermediate Visit : " + intermediateVisit.get_ID());
			}
			int firstVisitId = intermediateVisit.getTC_FirstVisit_ID();
			TCFirstVisit firstVisit = new TCFirstVisit(ctx, firstVisitId, trxName);
			if (nextVisitDate != null && !nextVisitDate.isEmpty()) {
				firstVisit.setnextvisitdate(nextTimestamp);
			}
			firstVisit.saveEx();
			trx.commit();
			int visitId = intermediateVisit.getTC_Visit_ID();
			TCVisit visit = new TCVisit(ctx, visitId, trx.getTrxName());
			visit.setTC_Status_ID(visitCompletedId);
			visit.setvisitdone(visitDone);
			visit.saveEx();
			trx.commit();
			int intermediateVisitIds = intermediateVisit.get_ID();
			String intermediateVisitUUId = intermediateVisit.getc_uuid();
			updateIntermediateVisitResponse.setIntermediateVisitId(intermediateVisitIds);
			updateIntermediateVisitResponse.setIntermediateVisitUUId(intermediateVisitUUId);
			updateIntermediateVisitResponse.setIsError(false);
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			updateIntermediateVisitResponse.setError(INTERNAL_SERVER_ERROR);
			updateIntermediateVisitResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return updateIntermediateVisitResponseDocument;
	}

	@Override
	public GetPlantTagResponseDocument getPlantTag(GetPlantTagRequestDocument req) {
		GetPlantTagResponseDocument getPlantTagResponseDocument = GetPlantTagResponseDocument.Factory.newInstance();
		GetPlantTagResponse getPlantTagResponse = getPlantTagResponseDocument.addNewGetPlantTagResponse();
		GetPlantTagRequest loginRequest = req.getGetPlantTagRequest();
		String plantTagUUid = safeTrim(loginRequest.getPlantTagUUId());
		Trx trx = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
			int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			int plantTagId = TCUtills.getId(TABLE_PLANT, plantTagUUid, clientId);
			if (plantTagId == 0) {
				getPlantTagResponse.setError("Plant Tag UUId not found in table record " + plantTagUUid + "");
				getPlantTagResponse.setIsError(true);
				return getPlantTagResponseDocument;
			}
			GetPlantTagData data = getPlantTagResponse.addNewGetPlantTagData();
			data.setPlantTagId(plantTagId);
			data.setPlantTagUUId(plantTagUUid);

			int plantDetailsId = TCUtills.getId(plantTagUUid, clientId);

			X_TC_collectionjoinplant plants = new Query(ctx, X_TC_collectionjoinplant.Table_Name,
					"tc_plantdetails_id =? AND ad_client_id =?", trxName).setParameters(plantDetailsId, clientId)
					.first();

			if (plantDetailsId == 0) {
				getPlantTagResponse.setIsError(false);
				getPlantTagResponse.addNewGetPlantDetailsData();
			} else {

				String sql = "SELECT ps.codeno AS croptype,v.codeno AS variety,pd.parentCultureLine As parentCultureLine FROM adempiere.tc_plantdetails pd\n"
						+ "JOIN adempiere.tc_plantspecies ps ON ps.tc_plantspecies_id = pd.tc_species_id\n"
						+ "JOIN adempiere.tc_variety v ON	v.tc_variety_id = pd.tc_variety_id\n"
						+ "WHERE pd.Ad_Client_Id = ? AND pd.tc_plantdetails_id = ?";
				pstm = DB.prepareStatement(sql, null);
				pstm.setInt(1, clientId);
				pstm.setInt(2, plantDetailsId);
				rs = pstm.executeQuery();
				while (rs.next()) {
					GetPlantDetailsData plantData = getPlantTagResponse.addNewGetPlantDetailsData();
					String cropType = rs.getString("croptype");
					String variety = rs.getString("variety");
					String parentCultureLine = rs.getString("parentCultureLine");
					plantData.setCropType(cropType);
					plantData.setVariety(variety);
					plantData.setParentCultureLine(parentCultureLine != null ? parentCultureLine : "");
					getPlantTagResponse.setIsError(false);

					if (plants == null || plants.get_ID() == 0)
						plantData.setDateOfSourcing("");
					else {
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
						String formattedDate = sdf.format(plants.getCreated());
						plantData.setDateOfSourcing(formattedDate);
					}
				}
				closeDbCon(pstm, rs);
				pstm = null;
				rs = null;
				String sql2 = "SELECT i.tc_in_id As id,i.tc_order_id As orderId FROM adempiere.tc_planttag p\n"
						+ "JOIN adempiere.tc_in i on i.parentuuid = p.c_uuid	\n" + "WHERE p.ad_client_id = ?"
						+ " AND i.parentuuid = ? ORDER BY id DESC;";
				pstm = DB.prepareStatement(sql2, null);
				pstm.setInt(1, clientId);
				pstm.setString(2, plantTagUUid);
				rs = pstm.executeQuery();
				if (!rs.isBeforeFirst()) {
					getPlantTagResponse.addNewGetPlantTagInId();
					return getPlantTagResponseDocument;
				}
				while (rs.next()) {
					GetPlantTagInId inId = getPlantTagResponse.addNewGetPlantTagInId();
					int inIds = rs.getInt("id");
					int orderId = rs.getInt("orderId");
					inId.setInId(inIds);
					inId.setOrderId(orderId);
				}

			}

		} catch (Exception e) {
			if (trx != null) trx.rollback();
			getPlantTagResponse.setError(INTERNAL_SERVER_ERROR);
			getPlantTagResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return getPlantTagResponseDocument;
	}

	@Override
	public AddNextVisitDatetForIVResponseDocument addNextVisitDatetForIV(AddNextVisitDatetForIVRequestDocument req) {
		AddNextVisitDatetForIVResponseDocument addNextVisitDatetForIVResponseDocument = AddNextVisitDatetForIVResponseDocument.Factory
				.newInstance();
		AddNextVisitDatetForIVResponse addNextVisitDatetForIVResponse = addNextVisitDatetForIVResponseDocument
				.addNewAddNextVisitDatetForIVResponse();
		AddNextVisitDatetForIVRequest loginRequest = req.getAddNextVisitDatetForIVRequest();
		int intermediateVisitId = 0;
		int cycleNo = 0;
		Trx trx = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String nextVisitDate = loginRequest.getNextVisitDate();
		int visitType = loginRequest.getVisitTypeId();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
			int clientId = Env.getAD_Client_ID(ctx);
			int orgId = Env.getAD_Org_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			
			if (containsMaliciousPattern(nextVisitDate)) {
				addNextVisitDatetForIVResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				addNextVisitDatetForIVResponse.setIsError(true);
			    return addNextVisitDatetForIVResponseDocument;
			}
			
			intermediateVisitId = loginRequest.getIntermediateVisitId();
			cycleNo = loginRequest.getCycleNo();
			int statusId = TCUtills.getRecordId(clientId, TABLE_STATUS, STATUS_IN_PROGRESS);
			if (statusId == 0) {
				addNextVisitDatetForIVResponse.setError("Status id is not in table record " + STATUS_IN_PROGRESS + "");
				addNextVisitDatetForIVResponse.setIsError(true);
				return addNextVisitDatetForIVResponseDocument;
			}

			Date date = (Date) dateFormat.parse(nextVisitDate);
			long timestampValue = date.getTime();
			Timestamp timestamp = new Timestamp(timestampValue);

			TCIntermediateVisit intermediateVisit = new TCIntermediateVisit(ctx, intermediateVisitId, trx.getTrxName());
			intermediateVisit.setnextvisitdate(timestamp);
			int farmerId = intermediateVisit.getTC_Farmer_ID();
			int firstVisitId = intermediateVisit.getTC_FirstVisit_ID();
			intermediateVisit.saveEx();
			trx.commit();

			TCFarmer farmer = new TCFarmer(ctx, farmerId, trx.getTrxName());
			String farmerName = farmer.getName();
			String MobileNo = farmer.getmobileno();

			if (!TCUtills.canCreateVisit(farmerId, cycleNo, visitType)) {
				addNextVisitDatetForIVResponse.setError("only one first and collection visit allowed per cycle");
				addNextVisitDatetForIVResponse.setIsError(true);
				return addNextVisitDatetForIVResponseDocument;
			}

			TCVisit visit = new TCVisit(ctx, 0, trx.getTrxName());
			visit.setAD_Org_ID(orgId);
			visit.setName(farmerName);
			visit.setTC_VisitType_ID(visitType);
			visit.setdate(timestamp);
			visit.setmobileno(MobileNo);
			visit.setTC_Farmer_ID(farmerId);
			visit.setTC_Status_ID(statusId);
			visit.setcycleno(cycleNo);
			if (!visit.save()) {
				throw new Exception("Failed to add Visit data: " + visit);
			}
			trx.commit();
			int VisitId = visit.get_ID();
			addNextVisitDatetForIVResponse.setNewVisitId(VisitId);
			addNextVisitDatetForIVResponse.setIsError(false);

			TCCollectionVisit collectionVisit = new TCCollectionVisit(ctx, 0, trx.getTrxName());
			collectionVisit.setAD_Org_ID(orgId);
			collectionVisit.setTC_Farmer_ID(farmerId);
			collectionVisit.setTC_Visit_ID(VisitId);
			collectionVisit.setTC_FirstVisit_ID(firstVisitId);
			collectionVisit.setTC_IntermediateVisit_ID(intermediateVisitId);
			if (!collectionVisit.save()) {
				throw new Exception("Failed to add Collection Visit data: " + collectionVisit);
			}
			trx.commit();
			int collectionVisitId = collectionVisit.get_ID();
			collectionVisit.setName("CollectionVisit_" + collectionVisitId + "_" + nextVisitDate);
			collectionVisit.saveEx();
			trx.commit();

			TCFirstVisit firstVisit = new TCFirstVisit(ctx, firstVisitId, trxName);
			firstVisit.setisattachedintermediate(true);
			firstVisit.saveEx();
			trx.commit();

			addNextVisitDatetForIVResponse.setCollectionVisitId(collectionVisitId);

			String sql = "SELECT tc_plantdetails_id AS id FROM adempiere.tc_intermediatejoinplant\n"
					+ "WHERE ad_client_id = ? AND tc_intermediatevisit_id = ?"
					+ " AND tc_plantstatus_id IS NULL;";
			pstm = DB.prepareStatement(sql, null);
			pstm.setInt(1, clientId);
			pstm.setInt(2, intermediateVisitId);
			rs = pstm.executeQuery();
			while (rs.next()) {
				int plantDetailsId = rs.getInt("id");
				TCCollectionJoinPlantDetails collectionJoinPlantDetails = new TCCollectionJoinPlantDetails(ctx, 0,
						trx.getTrxName());
				collectionJoinPlantDetails.setAD_Org_ID(orgId);
				collectionJoinPlantDetails.setTC_CollectionDetails_ID(collectionVisitId);
				collectionJoinPlantDetails.setTC_PlantDetails_ID(plantDetailsId);
				collectionJoinPlantDetails.saveEx();
				trx.commit();
			}
			closeDbCon(pstm, rs);
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			addNextVisitDatetForIVResponse.setError(INTERNAL_SERVER_ERROR);
			addNextVisitDatetForIVResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return addNextVisitDatetForIVResponseDocument;
	}

	@Override
	public UpdateCollectionVisitResponseDocument updateCollectionVisit(UpdateCollectionVisitRequestDocument req) {
		UpdateCollectionVisitResponseDocument updateCollectionVisitResponseDocument = UpdateCollectionVisitResponseDocument.Factory
				.newInstance();
		UpdateCollectionVisitResponse updateCollectionVisitResponse = updateCollectionVisitResponseDocument
				.addNewUpdateCollectionVisitResponse();
		UpdateCollectionVisitRequest loginRequest = req.getUpdateCollectionVisitRequest();
		int collectionVisitId = 0;
		String YieldWeight = safeTrim(loginRequest.getYieldWeight());
		boolean visitDone = loginRequest.getVisitDone();
		Trx trx = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		int suckerNo = 0;
		float noOfPlant = 0, noOfRejectPlant = 0;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
			int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			
			if (TCUtills.isValidMobileNumber(YieldWeight)) {
				updateCollectionVisitResponse.setError("Inter the number");
				updateCollectionVisitResponse.setIsError(true);
			    return updateCollectionVisitResponseDocument;
			}
			
			if (containsMaliciousPattern(YieldWeight)) {
				updateCollectionVisitResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				updateCollectionVisitResponse.setIsError(true);
			    return updateCollectionVisitResponseDocument;
			}
			
			int intermediateVisitId = 0;
			collectionVisitId = loginRequest.getCollectionVisitId();
			int visitCompletedId = TCUtills.getRecordId(clientId, TABLE_STATUS, STATUS_COMPLETED);
			if (visitCompletedId == 0) {
				updateCollectionVisitResponse.setError("Status id is not in table record " + STATUS_COMPLETED + "");
				updateCollectionVisitResponse.setIsError(true);
				return updateCollectionVisitResponseDocument;
			}
			
			if (!TCUtills.isValidLocationName(YieldWeight)) {
				updateCollectionVisitResponse.setError("Invalid Yield Weight format");
				updateCollectionVisitResponse.setIsError(true);
				return updateCollectionVisitResponseDocument;
	        }

			TCCollectionVisit collectionVisit = new TCCollectionVisit(ctx, collectionVisitId, trx.getTrxName());
			collectionVisit.setyieldweight(YieldWeight);
			collectionVisit.saveEx();
			trx.commit();
			intermediateVisitId = collectionVisit.getTC_IntermediateVisit_ID();

			String collectionVisitUUId = collectionVisit.getc_uuid();

			PlantDetailsAndNoOfSuckerList[] plantlists = loginRequest.getPlantDetailsAndNoOfSuckerListArray();
			for (PlantDetailsAndNoOfSuckerList list : plantlists) {
				int plantId = list.getCollectionVisitPlantDetailsId();
				int noOfSucker = list.getNoOfSucker();

				TCCollectionJoinPlantDetails plantDetails = new TCCollectionJoinPlantDetails(ctx, plantId,
						trx.getTrxName());
				plantDetails.setsuckerno(noOfSucker);
				plantDetails.saveEx();
				trx.commit();

				collectionVisit.setissuckercollectcollection(true);
				collectionVisit.saveEx();
				trx.commit();
			}

			if (intermediateVisitId != 0) {
				TCIntermediateVisit intermediateVisit = new TCIntermediateVisit(ctx, intermediateVisitId, trxName);
				intermediateVisit.setisattachedocollection(true);
				intermediateVisit.saveEx();
				trx.commit();
			}

			String sql = "SELECT COUNT(*) AS total_plants,sum(suckerno) As suckerno,\n"
					+ "SUM(CASE WHEN ps.name = 'Rejected' THEN 1 ELSE 0 END) AS rejected_plants\n"
					+ "FROM adempiere.tc_collectionjoinplant cjp\n"
					+ "left JOIN adempiere.tc_plantstatus ps ON ps.tc_plantstatus_id = cjp.tc_plantstatus_id\n"
					+ "WHERE cjp.tc_collectiondetails_id = ? AND cjp.ad_client_id = ?"
					+ ";";
			pstm = DB.prepareStatement(sql.toString(), null);
			pstm.setInt(1, collectionVisitId);
			pstm.setInt(2, clientId);
			rs = pstm.executeQuery();
			while (rs.next()) {
				suckerNo = rs.getInt("suckerno");
				noOfPlant = rs.getFloat("total_plants");
				noOfRejectPlant = rs.getFloat("rejected_plants");
			}
			if (suckerNo == 0 && noOfRejectPlant / noOfPlant == 1) {
				if (intermediateVisitId != 0) {
					TCIntermediateVisit intermediateVisit = new TCIntermediateVisit(ctx, intermediateVisitId, trxName);
					intermediateVisit.setisattachedocollection(true);
					intermediateVisit.saveEx();
				}
				collectionVisit.setissuckercollectcollection(true);
				collectionVisit.saveEx();
				trx.commit();
			}
			int visitId = collectionVisit.getTC_Visit_ID();
			TCVisit visit = new TCVisit(ctx, visitId, trx.getTrxName());
			visit.setTC_Status_ID(visitCompletedId);
			visit.setvisitdone(visitDone);
			visit.saveEx();
			trx.commit();
			updateCollectionVisitResponse.setCollectionVisitId(collectionVisitId);
			updateCollectionVisitResponse.setCollectionVisitUUId(collectionVisitUUId);
			updateCollectionVisitResponse.setIsError(false);
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			updateCollectionVisitResponse.setError(INTERNAL_SERVER_ERROR);
			updateCollectionVisitResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return updateCollectionVisitResponseDocument;
	}

	@Override
	public DiscardMediaLabelResponseDocument discardMediaLabel(DiscardMediaLabelRequestDocument req) {
		DiscardMediaLabelResponseDocument discardMediaLabelResponseDocument = DiscardMediaLabelResponseDocument.Factory
				.newInstance();
		DiscardMediaLabelResponse discardMediaLabelResponse = discardMediaLabelResponseDocument
				.addNewDiscardMediaLabelResponse();
		DiscardMediaLabelRequest loginRequest = req.getDiscardMediaLabelRequest();
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		int warehouseId = 0;
		int documentType = 0;
		int fromLocatorId = 0, productId = 0, mediaLineId = 0, qty = 1;
		BigDecimal quantity = new BigDecimal(qty);
		Trx trx = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
		int clientId = Env.getAD_Client_ID(ctx);
		int orgId = Env.getAD_Org_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			warehouseId = Env.getContextAsInt(ctx, Env.M_WAREHOUSE_ID);
			documentType = TCUtills.getDocTypeId(clientId, DOCUMENT_MATERIAL_MOVEMENT);

			int toLocatorId = TCUtills.getLocatorId(clientId, TABLE_LOCATOR, DEFAULT_DISCARD_MEDIA_LABEL);
			if (toLocatorId == 0) {
				discardMediaLabelResponse.setError("locator searchKey is not in table record " + DEFAULT_DISCARD_MEDIA_LABEL + "");
				discardMediaLabelResponse.setIsError(true);
				return discardMediaLabelResponseDocument;
			}

			Timestamp currentTimestamp = Timestamp.valueOf(LocalDateTime.now());

			MMovement move = new MMovement(ctx, 0, trx.getTrxName());
			move.setAD_Org_ID(orgId);
			move.setDateReceived(currentTimestamp);
			move.setC_DocType_ID(documentType);
			move.setM_Warehouse_ID(warehouseId);
			move.setM_WarehouseTo_ID(warehouseId);
			if (!move.save()) {
				throw new Exception("Failed to add Internal move data: " + move);
			}
			trx.commit();

			AddMediaLabelDiscardDetail[] mediadata = loginRequest.getAddMediaLabelDiscardDetailArray();
			for (AddMediaLabelDiscardDetail data : mediadata) {
				String mediaLabelUUId = data.getMediaLabelUUId();
				String discardReason = data.getDiscardReason();
				int discardTypeId = data.getDiscardType();
				String dateOfDiscard = data.getDiscardDate();
				String tcpf = data.getTCPF();
				String personalCode = data.getPersonalCode();

				String userName = TCUtills.getUserName(clientId, personalCode);
				if (userName == "") {
					discardMediaLabelResponse
							.setError("Personal code is not available for any user, " + personalCode + "");
					discardMediaLabelResponse.setIsError(true);
					return discardMediaLabelResponseDocument;
				}
				
				if (containsMaliciousPattern(dateOfDiscard)) {
					discardMediaLabelResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
					discardMediaLabelResponse.setIsError(true);
				    return discardMediaLabelResponseDocument;
				}
				
				if (containsMaliciousPattern(tcpf)) {
					discardMediaLabelResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
					discardMediaLabelResponse.setIsError(true);
				    return discardMediaLabelResponseDocument;
				}

				Date date = (Date) formatter.parse(dateOfDiscard);
				long timestampValue = date.getTime();
				Timestamp timestamp = new Timestamp(timestampValue);

				int mediaLabelId = TCUtills.getId(TABLE_MEDIA, mediaLabelUUId, clientId);
				if (mediaLabelId == 0) {
					discardMediaLabelResponse
							.setError("Media Label UUId not found in table record " + mediaLabelUUId + "");
					discardMediaLabelResponse.setIsError(true);
					return discardMediaLabelResponseDocument;
				}

				TCMediaLabelQr label = new TCMediaLabelQr(ctx, mediaLabelId, trx.getTrxName());
				mediaLineId = label.getTC_MediaLine_ID();
				label.setdiscardreason(discardReason);
				label.setisDiscarded(true);
				label.settcpf2(tcpf);
				label.setpersonalcode2(personalCode);
				label.setdiscarddate(timestamp);
				label.setTC_MediaDiscardType_ID(discardTypeId);
				label.saveEx();
				trx.commit();

				MediaLabelRecords records = discardMediaLabelResponse.addNewMediaLabelRecords();
				records.setMediaLabelId(mediaLabelId);
				records.setMediaLabelUUId(mediaLabelUUId);

				TCMediaLine line = new TCMediaLine(ctx, mediaLineId, trx.getTrxName());
				productId = line.getM_Product_ID();
				fromLocatorId = line.getM_Locator_ID();

				moveInventory(ctx, trxName, move, quantity, productId, fromLocatorId, toLocatorId);

			}

			move.saveEx();
			move.setDocStatus(DocAction.ACTION_Complete);
			move.setDocAction(DocAction.ACTION_Close);
			move.setPosted(true);
			move.setProcessed(true);
			move.setIsApproved(true);
			move.completeIt();
			move.saveEx();
			int moveLineId = move.get_ID();
			discardMediaLabelResponse.setMovementId(moveLineId);
			discardMediaLabelResponse.setIsError(false);
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			discardMediaLabelResponse.setError(INTERNAL_SERVER_ERROR);
			discardMediaLabelResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return discardMediaLabelResponseDocument;
	}

	@Override
	public DiscardCultureLabelResponseDocument discardCultureLabel(DiscardCultureLabelRequestDocument req) {
		DiscardCultureLabelResponseDocument discardCultureLabelResponseDocument = DiscardCultureLabelResponseDocument.Factory
				.newInstance();
		DiscardCultureLabelResponse discardCultureLabelResponse = discardCultureLabelResponseDocument
				.addNewDiscardCultureLabelResponse();
		DiscardCultureLabelRequest loginRequest = req.getDiscardCultureLabelRequest();
		String cultureLabelUUId = safeTrim(loginRequest.getCultureLabelUUId());
		String discardReason = loginRequest.getDiscardReason();
		int discardType = 0;
		String dateOfDiscard = safeTrim(loginRequest.getDiscardDate());
		String tcpf = safeTrim(loginRequest.getTCPF());
		String personalCode = safeTrim(loginRequest.getPersonalCode());
		int documentType = 0;
		int fromLocatorId = 0, productId = 0, outId = 0;
		int qty = 0;
		BigDecimal quantity = new BigDecimal(qty);
		Trx trx = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
		int clientId = Env.getAD_Client_ID(ctx);
		int orgId = Env.getAD_Org_ID(ctx);
		int warehouseId = Env.getContextAsInt(ctx, Env.M_WAREHOUSE_ID);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			discardType = loginRequest.getDiscardType();
			documentType = TCUtills.getDocTypeId(clientId, DOCUMENT_MATERIAL_MOVEMENT);
			String userName = TCUtills.getUserName(clientId, personalCode);
			if (userName == "") {
				discardCultureLabelResponse
						.setError("Personal code is not available for any user, " + personalCode + "");
				discardCultureLabelResponse.setIsError(true);
				return discardCultureLabelResponseDocument;
			}
			
			if (containsMaliciousPattern(dateOfDiscard)) {
				discardCultureLabelResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				discardCultureLabelResponse.setIsError(true);
			    return discardCultureLabelResponseDocument;
			}
			
			if (containsMaliciousPattern(tcpf)) {
				discardCultureLabelResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				discardCultureLabelResponse.setIsError(true);
			    return discardCultureLabelResponseDocument;
			}

			int toLocatorId = TCUtills.getLocatorId(clientId, TABLE_LOCATOR, DEFAULT_DISCARD_CULTURE_LABEL);
			if (toLocatorId == 0) {
				discardCultureLabelResponse
						.setError("locator searchKey is not in table record " + DEFAULT_DISCARD_CULTURE_LABEL + "");
				discardCultureLabelResponse.setIsError(true);
				return discardCultureLabelResponseDocument;
			}

			int CultureLabelId = TCUtills.getId(TABLE_TC_CULTURE, cultureLabelUUId, clientId);
			if (CultureLabelId == 0) {
				discardCultureLabelResponse
						.setError("Culture Label UUId not found in table record " + cultureLabelUUId + "");
				discardCultureLabelResponse.setIsError(true);
				return discardCultureLabelResponseDocument;
			}
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			Date date = (Date) dateFormat.parse(dateOfDiscard);
			long timestampValue = date.getTime();
			Timestamp timestamp = new Timestamp(timestampValue);

			TCCultureLabel label = new TCCultureLabel(ctx, CultureLabelId, trx.getTrxName());
			label.setisDiscarded(true);
			label.settcpf2(tcpf);
			label.setdiscarddate(timestamp);
			label.setpersonalcode2(personalCode);
			label.setTC_DiscardType_ID(discardType);
			label.setdiscardreason(discardReason);
			label.saveEx();
			trx.commit();
			outId = label.getTC_out_ID();

			TCOut out = new TCOut(ctx, outId, trx.getTrxName());
			productId = out.getM_Product_ID();
			fromLocatorId = out.getM_Locator_ID();
			quantity = out.getQuantity();
			out.setdiscardqty(quantity);
			out.setDescription("Discarded");
			out.saveEx();
			trx.commit();

			TCQualityCheck qualityCheck = new TCQualityCheck(ctx, 0, trx.getTrxName());
			qualityCheck.setAD_Org_ID(orgId);
			qualityCheck.setculturelabeluuid(cultureLabelUUId);
			qualityCheck.setdiscardreason(discardReason);
			qualityCheck.setTC_DiscardType_ID(discardType);
			qualityCheck.setName("Discard_" + CultureLabelId + "_" + dateOfDiscard);
			qualityCheck.setdate(timestamp);
			qualityCheck.settcpf(tcpf);
			qualityCheck.setpersonalcode(personalCode);
			qualityCheck.saveEx();
			trx.commit();
			int qualityCheckId = qualityCheck.get_ID();

			MMovement move = new MMovement(ctx, 0, trx.getTrxName());
			move.setAD_Org_ID(orgId);
			move.setDateReceived(timestamp);
			move.setC_DocType_ID(documentType);
			move.setM_Warehouse_ID(warehouseId);
			move.setM_WarehouseTo_ID(warehouseId);
			if (!move.save()) {
				throw new Exception("Failed to add Internal move data: " + move);
			}
			trx.commit();

			moveInventory(ctx, trxName, move, quantity, productId, fromLocatorId, toLocatorId);

			move.saveEx();
			move.setDocStatus(DocAction.ACTION_Complete);
			move.setDocAction(DocAction.ACTION_Close);
			move.setPosted(true);
			move.setProcessed(true);
			move.setIsApproved(true);
			move.completeIt();
			move.saveEx();

			int moveId = move.get_ID();
			discardCultureLabelResponse.setIsError(false);
			discardCultureLabelResponse.setQualityCheckId(qualityCheckId);
			discardCultureLabelResponse.setCultureLabelId(CultureLabelId);
			discardCultureLabelResponse.setCultureLabelUUId(cultureLabelUUId);
			discardCultureLabelResponse.setMovementId(moveId);

		} catch (Exception e) {
			if (trx != null) trx.rollback();
			discardCultureLabelResponse.setError(INTERNAL_SERVER_ERROR);
			discardCultureLabelResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return discardCultureLabelResponseDocument;
	}

	@Override
	public GetIntermediateVisitResponseDocument getIntermediateVisit(GetIntermediateVisitRequestDocument req) {
		GetIntermediateVisitResponseDocument getIntermediateVisitResponseDocument = GetIntermediateVisitResponseDocument.Factory
				.newInstance();
		GetIntermediateVisitResponse getIntermediateVisitResponse = getIntermediateVisitResponseDocument
				.addNewGetIntermediateVisitResponse();
		GetIntermediateVisitRequest loginRequest = req.getGetIntermediateVisitRequest();
		
		int visitId = 0;
		int intermediateVisitId = 0;
		Trx trx = null;
//		String base64 = "";
		int tableId = MTable.getTable_ID(TABLE_FARMER);
		int plantTableId = MTable.getTable_ID(TABLE_PLANT_DETAILS);
		MAttachment attachment = null, attachment1 = null;
		PreparedStatement pstm = null, pstm1 = null;
		ResultSet rs = null, rs1 = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
			int clientId = Env.getAD_Client_ID(ctx);
			
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			visitId = loginRequest.getVisitId();
			GetIntermediateVisitData data = getIntermediateVisitResponse.addNewGetIntermediateVisitData();

			String sql = "SELECT vi.cycleNo AS cycleNo,iv.tc_intermediatevisit_id AS id,iv.isattachedocollection As isattachedocollection,iv.c_uuid AS intermediateVisitUUId,iv.reviewdetails AS monitoringDetails,\n"
					+ "d.name AS monitoringState,iv.reasondetails AS reason,iv.tc_firstvisit_id AS firstId,iv.tc_farmer_id AS farmerId,\n"
					+ "fv.nextvisitdate As date,vi.visitdone As visitdone FROM adempiere.tc_intermediatevisit iv\n"
					+ "LEFT JOIN adempiere.tc_decision d ON d.tc_decision_id = iv.tc_decision_id JOIN adempiere.tc_visit vi ON vi.tc_visit_id = iv.tc_visit_id\n"
					+ "LEFT JOIN adempiere.tc_firstvisit fv ON fv.tc_firstvisit_id = iv.tc_firstvisit_id	\n"
					+ "WHERE iv.ad_client_id = ? AND iv.tc_visit_id = ? LIMIT 1;";
			pstm = DB.prepareStatement(sql, null);
			pstm.setInt(1, clientId);
			pstm.setInt(2, visitId);
			rs = pstm.executeQuery();
			while (rs.next()) {
				intermediateVisitId = rs.getInt("id");
				String IntermediateVisitUUid = rs.getString("intermediateVisitUUId");
				String monitoringDetails = rs.getString("monitoringDetails");
				String monitoringState = rs.getString("monitoringState");
				String reason = rs.getString("reason");
				int firstVisitId = rs.getInt("firstId");
				int farmerId = rs.getInt("farmerId");
				String date = rs.getString("date");
				boolean isattachedocollection = rs.getBoolean("isattachedocollection");
				Boolean visitDone = rs.getBoolean("visitdone");
				int cycleNo = rs.getInt("cycleNo");

				data.setIntermediateVisitId(intermediateVisitId);
				data.setIntermediateVisitUUId(IntermediateVisitUUid);
				data.setMonitoringDetails(monitoringDetails != null ? monitoringDetails : "");
				data.setMonitoringState(monitoringState != null ? monitoringState : "");
				data.setReason(reason != null ? reason : "");
				data.setFirstVisitId(firstVisitId);
				data.setFarmerId(farmerId);
				data.setDate(date != null ? date : "");
				data.setAttachedColltionVisit(isattachedocollection);
				data.setVisitDone(visitDone);
				data.setCycleNo(cycleNo);

				attachment = MAttachment.get(ctx, tableId, farmerId);
				if (attachment != null) {
					MAttachmentEntry[] entries = attachment.getEntries();
					getIntermediateVisitResponse.setImageRecordId(attachment.getRecord_ID());
					getIntermediateVisitResponse.setFarmerTableId(tableId);
//					if (entries.length > 0) {
					for (int i = entries.length - 1; i >= 0; i--) {
//						MAttachmentEntry entry = entries[i];
//						byte[] data = entry.getData();
//						base64 = Base64.getEncoder().encodeToString(data);
						ImageArray2 imageArray = getIntermediateVisitResponse.addNewImageArray1();
//						imageArray.setImage64(base64);
						imageArray.setImageIndexId(i);
					}
//					} else {
//						getIntermediateVisitResponse.addNewImageArray1();
//					}
				} else
					getIntermediateVisitResponse.addNewImageArray1();
			}

			String sql1 = "SELECT iv.tc_plantdetails_id AS id,pd.raw as raw, pd.columns as column,pd.c_uuid AS plantDetailsUUid,pd.planttaguuid AS plantTagUUId,\n"
					+ "ps.codeNo AS cropType,v.codeNo AS variety,pd.isrejected AS plantRejectStatus,pt.tc_planttag_id As plantTagId,vi.visitdone As visitdone\n"
					+ "FROM adempiere.tc_intermediatejoinplant iv \n"
					+ "JOIN adempiere.tc_intermediatevisit imv ON iv.tc_intermediatevisit_id = imv.tc_intermediatevisit_id\n"
					+ "JOIN adempiere.tc_visit vi ON vi.tc_visit_id = imv.tc_visit_id\n"
					+ "JOIN adempiere.tc_plantdetails pd ON pd.tc_plantdetails_id = iv.tc_plantdetails_id\n"
					+ "JOIN adempiere.tc_plantspecies ps ON ps.tc_plantspecies_id = pd.tc_species_id\n"
					+ "JOIN adempiere.tc_variety v ON v.tc_variety_id = pd.tc_variety_id JOIN adempiere.tc_planttag pt ON pt.c_uuid = pd.planttaguuid\n"
					+ "WHERE iv.ad_client_id = ? AND iv.tc_intermediatevisit_id = ?"
					+ "";
			pstm1 = DB.prepareStatement(sql1, null);
			pstm1.setInt(1, clientId);
			pstm1.setInt(2, intermediateVisitId);
			rs1 = pstm1.executeQuery();
			while (rs1.next()) {
				GetIntermediatePlantData plantData = getIntermediateVisitResponse.addNewGetIntermediatePlantData();
				int plantDetailsId = rs1.getInt("id");
				String plantDetalsUUid = rs1.getString("plantDetailsUUid");
				String plantTagUUId = rs1.getString("plantTagUUId");
				String cropType = rs1.getString("cropType");
				String variety = rs1.getString("variety");
				String raw = rs1.getString("raw");
				String column = rs1.getString("column");
				String plantRejectStatus = rs1.getString("plantRejectStatus");
				int plantTagId = rs1.getInt("plantTagId");

				plantData.setPlantId(plantDetailsId);
				plantData.setPlantUUId(plantDetalsUUid);
				plantData.setPlantTagUUId(plantTagUUId);
				plantData.setCropType(cropType);
				plantData.setVariety(variety);
				plantData.setRow(raw != null ? raw : "");
				plantData.setColumn(column != null ? column : "");
				plantData.setPlantTagId(plantTagId);
				plantData.setPlantRejectStatus(plantRejectStatus);

				attachment1 = MAttachment.get(ctx, plantTableId, plantDetailsId);
				if (attachment1 != null) {
					MAttachmentEntry[] entries1 = attachment1.getEntries();
					plantData.setPlantTableId(plantTableId);
					plantData.setImageRecordId(attachment1.getRecord_ID());
//					if (entries1.length > 0) {
					for (int i = entries1.length - 1; i >= 0; i--) {
//						MAttachmentEntry entry = entries[i];
//						byte[] data = entry.getData();
//						base64 = Base64.getEncoder().encodeToString(data);
						ImageArray2 imageArray = plantData.addNewImageArray1();
//						imageArray.setImage64(base64);
						imageArray.setImageIndexId(i);
					}
//					} else {
//						plantData.addNewImageArray1();
//					}
				} else
					plantData.addNewImageArray1();
			}
			if (getIntermediateVisitResponse.sizeOfGetIntermediatePlantDataArray() == 0)
				getIntermediateVisitResponse.addNewGetIntermediatePlantData();
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			getIntermediateVisitResponse.setError(INTERNAL_SERVER_ERROR);
			getIntermediateVisitResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			closeDbCon(pstm1, rs1);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return getIntermediateVisitResponseDocument;
	}

	@Override
	public GetCollectionVisitResponseDocument getCollectionVisit(GetCollectionVisitRequestDocument req) {
		GetCollectionVisitResponseDocument getCollectionVisitResponseDocument = GetCollectionVisitResponseDocument.Factory
				.newInstance();
		GetCollectionVisitResponse getCollectionVisitResponse = getCollectionVisitResponseDocument
				.addNewGetCollectionVisitResponse();
		GetCollectionVisitRequest loginRequest = req.getGetCollectionVisitRequest();
		int visitId = 0;
		int collectionVisitId = 0;
		Trx trx = null;
//		String base64 = "";
		int tableId = MTable.getTable_ID(TABLE_FARMER);
		int plantTableId = MTable.getTable_ID(TABLE_PLANT_DETAILS);
		MAttachment attachment = null, attachment1 = null;
		PreparedStatement pstm = null, pstm1 = null;
		ResultSet rs = null, rs1 = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
			int clientId = Env.getAD_Client_ID(ctx);
			
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			visitId = loginRequest.getVisitId();
			GetCollectionVisitData data = getCollectionVisitResponse.addNewGetCollectionVisitData();
			String sql = "SELECT v.cycleNo AS cycleNo,cv.tc_collectiondetails_id AS id,cv.issuckercollectcollection As issuckercollectcollection,cv.c_uuid As collectionvisitUUId,cv.tc_firstvisit_id AS firstId,cv.tc_intermediatevisit_id As intermediateId,\n"
					+ "cv.yieldweight AS yieldWeight,cv.tc_farmer_id AS farmerId,v.visitdone As visitdone FROM adempiere.tc_collectiondetails cv JOIN adempiere.tc_visit v ON v.tc_visit_id = cv.tc_visit_id\n"
					+ "WHERE cv.ad_client_id = ? AND cv.tc_visit_id = ? limit 1";
			pstm = DB.prepareStatement(sql, null);
			pstm.setInt(1, clientId);
			pstm.setInt(2, visitId);
			rs = pstm.executeQuery();
			while (rs.next()) {
				collectionVisitId = rs.getInt("id");
				String collectionVisitUUId = rs.getString("collectionvisitUUId");
				String yieldWeight = rs.getString("yieldWeight");
				int firstVisitId = rs.getInt("firstId");
				int intermediateVisitId = rs.getInt("intermediateId");
				int farmerId = rs.getInt("farmerId");
				Boolean issuckercollectcollection = rs.getBoolean("issuckercollectcollection");
				Boolean visitDone = rs.getBoolean("visitdone");
				int cycleNo = rs.getInt("cycleNo");

				data.setCollectionVisitId(collectionVisitId);
				data.setFarmerId(farmerId);
				data.setCollectionVisitUUId(collectionVisitUUId);
				data.setFirstVisitId(firstVisitId);
				data.setIntermediateVisitId(intermediateVisitId);
				data.setYieldWeight(yieldWeight != null ? yieldWeight : "");
				data.setCollectNoOfSucker(issuckercollectcollection);
				data.setVisitDone(visitDone);
				data.setCycleNo(cycleNo);

				attachment = MAttachment.get(ctx, tableId, farmerId);
				if (attachment != null) {
					MAttachmentEntry[] entries = attachment.getEntries();
					getCollectionVisitResponse.setImageRecordId(attachment.getRecord_ID());
					getCollectionVisitResponse.setFarmerTableId(tableId);
//					if (entries.length > 0) {
					for (int i = entries.length - 1; i >= 0; i--) {
//						MAttachmentEntry entry = entries[i];
//						byte[] data = entry.getData();
//						base64 = Base64.getEncoder().encodeToString(data);
						ImageArray2 imageArray = getCollectionVisitResponse.addNewImageArray1();
//						imageArray.setImage64(base64);
						imageArray.setImageIndexId(i);
					}
//					} else {
//						getCollectionVisitResponse.addNewImageArray1();
//					}
				} else
					getCollectionVisitResponse.addNewImageArray1();
			}
			String sql1 = "SELECT cv.tc_collectionjoinplant_id AS collectionVisitPlantDetailsId,pd.raw as raw, pd.columns as column,cv.tc_plantdetails_id AS id,pd.c_uuid AS plantDetailsUUid,\n"
					+ "pd.planttaguuid AS plantTagUUId,ps.codeNo AS cropType,v.codeNo AS variety,cv.suckerno AS noOfSucker,pd.isrejected AS plantRejectStatus,pt.tc_planttag_id AS plantTagId\n"
					+ "FROM adempiere.tc_collectionjoinplant cv\n"
					+ "JOIN adempiere.tc_plantdetails pd ON pd.tc_plantdetails_id = cv.tc_plantdetails_id\n"
					+ "JOIN adempiere.tc_plantspecies ps ON ps.tc_plantspecies_id = pd.tc_species_id\n"
					+ "JOIN adempiere.tc_variety v ON v.tc_variety_id = pd.tc_variety_id JOIN adempiere.tc_planttag pt ON pt.c_uuid = pd.planttaguuid\n"
					+ "WHERE cv.ad_client_id = ? AND cv.tc_collectiondetails_id = ?"
					+ ";\n" + "";
			pstm1 = DB.prepareStatement(sql1, null);
			pstm1.setInt(1, clientId);
			pstm1.setInt(2, collectionVisitId);
			rs1 = pstm1.executeQuery();
			while (rs1.next()) {
				GetCollectionPlantData plantData = getCollectionVisitResponse.addNewGetCollectionPlantData();
				int collectionVisitPlantDetailsId = rs1.getInt("collectionVisitPlantDetailsId");
				int plantDetailsId = rs1.getInt("id");
				String plantDetalsUUid = rs1.getString("plantDetailsUUid");
				String plantTagUUId = rs1.getString("plantTagUUId");
				String cropType = rs1.getString("cropType");
				String variety = rs1.getString("variety");
				String raw = rs1.getString("raw");
				String column = rs1.getString("column");
				int noOfSucker = rs1.getInt("noOfSucker");
				String plantRejectStatus = rs1.getString("plantRejectStatus");
				int plantTagId = rs1.getInt("plantTagId");

				plantData.setCollectionVisitPlantDetailsId(collectionVisitPlantDetailsId);
				plantData.setPlantId(plantDetailsId);
				plantData.setPlantUUId(plantDetalsUUid);
				plantData.setPlantTagUUId(plantTagUUId);
				plantData.setCropType(cropType);
				plantData.setVariety(variety);
				plantData.setRow(raw != null ? raw : "");
				plantData.setColumn(column != null ? column : "");
				plantData.setNoOfSucker(noOfSucker);
				plantData.setPlantTagId(plantTagId);
				plantData.setPlantRejectStatus(plantRejectStatus);

				attachment1 = MAttachment.get(ctx, plantTableId, plantDetailsId);
				if (attachment1 != null) {
					MAttachmentEntry[] entries1 = attachment1.getEntries();
					plantData.setPlantTableId(plantTableId);
					plantData.setImageRecordId(attachment1.getRecord_ID());
//					if (entries1.length > 0) {
					for (int i = entries1.length - 1; i >= 0; i--) {
//						MAttachmentEntry entry = entries[i];
//						byte[] data = entry.getData();
//						base64 = Base64.getEncoder().encodeToString(data);
						ImageArray2 imageArray = plantData.addNewImageArray1();
//						imageArray.setImage64(base64);
						imageArray.setImageIndexId(i);
					}
//					} else {
//						plantData.addNewImageArray1();
//					}
				} else
					plantData.addNewImageArray1();
			}
			if (getCollectionVisitResponse.sizeOfGetCollectionPlantDataArray() == 0)
				getCollectionVisitResponse.addNewGetCollectionPlantData();
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			getCollectionVisitResponse.setError(INTERNAL_SERVER_ERROR);
			getCollectionVisitResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			closeDbCon(pstm1, rs1);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return getCollectionVisitResponseDocument;
	}

	@Override
	public GetFirstVisitByVisitIdResponseDocument getFirstVisitbyvisitId(GetFirstVisitByVisitIdRequestDocument req) {
		GetFirstVisitByVisitIdResponseDocument getFirstVisitByVisitIdResponseDocument = GetFirstVisitByVisitIdResponseDocument.Factory
				.newInstance();
		GetFirstVisitByVisitIdResponse getFirstVisitByVisitIdResponse = getFirstVisitByVisitIdResponseDocument
				.addNewGetFirstVisitByVisitIdResponse();
		GetFirstVisitByVisitIdRequest loginRequest = req.getGetFirstVisitByVisitIdRequest();
		int visitId = 0;
		int fisrtVisitId = 0;
		Trx trx = null;
//		String base64 = "";
		int tableId = MTable.getTable_ID(TABLE_FARMER);
		int plantTableId = MTable.getTable_ID(TABLE_PLANT_DETAILS);
		MAttachment attachment = null, attachment1 = null;
		PreparedStatement pstm = null, pstm1 = null;
		ResultSet rs = null, rs1 = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
			int clientId = Env.getAD_Client_ID(ctx);
			
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			visitId = loginRequest.getVisitId();
			GetFirstVisitDatas data = getFirstVisitByVisitIdResponse.addNewGetFirstVisitDatas();
			String sql = "SELECT vi.cycleNo AS cycleNo,fv.tc_firstvisit_id AS id,fv.c_uuid As firstvisitUUId,fv.isattachedintermediate As attachedIntermediate,fv.plantno AS plantno,fv.visitdate AS date,fv.pesthistory AS pestHistory,\n"
					+ "fv.enterdetailsofinfestation AS enterDetailsOfInfestation,fs.name As fieldSelection,st.name AS soilType,wt.name As wateringType,\n"
					+ "fm.name AS fieldManagement,fv.tc_farmer_id As farmerId,vi.visitdone As visitdone FROM adempiere.tc_firstvisit fv\n"
					+ "LEFT JOIN adempiere.tc_fieldSelection fs ON fs.tc_fieldSelection_id = fv.tc_fieldSelection_id\n"
					+ "LEFT JOIN adempiere.tc_soilType st ON st.tc_soilType_id = fv.tc_soilType_id JOIN adempiere.tc_visit vi ON vi.tc_visit_id = fv.tc_visit_id\n"
					+ "LEFT JOIN adempiere.tc_wateringType wt ON wt.tc_wateringType_id = fv.tc_wateringType_id\n"
					+ "LEFT JOIN adempiere.tc_fieldManagement fm ON fm.tc_fieldManagement_id = fv.tc_fieldManagement_id	\n"
					+ "WHERE fv.ad_client_id = ? AND fv.tc_visit_id = ? LIMIT 1;";
			pstm = DB.prepareStatement(sql, null);
			pstm.setInt(1, clientId);
			pstm.setInt(2, visitId);
			rs = pstm.executeQuery();
			while (rs.next()) {
				fisrtVisitId = rs.getInt("id");
				String fisrtVisitUUId = rs.getString("firstvisitUUId");
				int plantno = rs.getInt("plantno");
				String date = rs.getString("date");
				String pestHistory = rs.getString("pestHistory");
				String enterDetailsOfInfestation = rs.getString("enterDetailsOfInfestation");
				String fieldSelection = rs.getString("fieldSelection");
				String soilType = rs.getString("soilType");
				String wateringType = rs.getString("wateringType");
				String fieldManagement = rs.getString("fieldManagement");
				int farmerId = rs.getInt("farmerId");
				Boolean attachedIntermediate = rs.getBoolean("attachedIntermediate");
				Boolean visitDone = rs.getBoolean("visitdone");
				int cycleNo = rs.getInt("cycleNo");

				data.setFirstVisitId(fisrtVisitId);
				data.setFarmerId(farmerId);
				data.setFirstVisitUUId(fisrtVisitUUId);
				data.setPlantNo(plantno);
				data.setDate(date);
				data.setFieldManagement(fieldManagement != null ? fieldManagement : "");
				data.setWateringType(wateringType != null ? wateringType : "");
				data.setSoilType(soilType != null ? soilType : "");
				data.setPestHistory(pestHistory != null ? pestHistory : "");
				data.setFieldSelection(fieldSelection != null ? fieldSelection : "");
				data.setEnterDetailsOfInfestation(enterDetailsOfInfestation != null ? enterDetailsOfInfestation : "");
				data.setAttachedIntermediateVisit(attachedIntermediate);
				data.setVisitDone(visitDone);
				data.setCycleNo(cycleNo);

				attachment = MAttachment.get(ctx, tableId, farmerId);
				if (attachment != null) {
					MAttachmentEntry[] entries = attachment.getEntries();
					getFirstVisitByVisitIdResponse.setImageRecordId(attachment.getRecord_ID());
					getFirstVisitByVisitIdResponse.setFarmerTableId(tableId);
//					if (entries.length > 0) {
					for (int i = entries.length - 1; i >= 0; i--) {
//						MAttachmentEntry entry = entries[i];
//						byte[] data = entry.getData();
//						base64 = Base64.getEncoder().encodeToString(data);
						ImageArray2 imageArray = getFirstVisitByVisitIdResponse.addNewImageArray1();
//						imageArray.setImage64(base64);
						imageArray.setImageIndexId(i);
					}
//					} else {
//						getFirstVisitByVisitIdResponse.addNewImageArray1();
//					}
				} else
					getFirstVisitByVisitIdResponse.addNewImageArray1();
			}
			String sql1 = "SELECT cv.tc_plantdetails_id As id,pd.raw as raw, pd.columns as column,pd.c_uuid AS plantDetailsUUid,pd.planttaguuid AS plantTagUUId,ps.codeNo As cropType,v.codeNo As variety,pt.tc_planttag_id AS plantTagId FROM adempiere.tc_firstjoinplant cv\n"
					+ "JOIN adempiere.tc_plantdetails pd ON pd.tc_plantdetails_id = cv.tc_plantdetails_id\n"
					+ "JOIN adempiere.tc_plantspecies ps On ps.tc_plantspecies_id = pd.tc_species_id\n"
					+ "JOIN adempiere.tc_variety v ON v.tc_variety_id = pd.tc_variety_id JOIN adempiere.tc_planttag pt ON pt.c_uuid = pd.planttaguuid\n"
					+ "WHERE cv.ad_client_id = ? AND cv.tc_firstvisit_id = ?"
					+ " AND cv.tc_plantstatus_id is NULL;";
			pstm1 = DB.prepareStatement(sql1, null);
			rs1 = pstm1.executeQuery();
			while (rs1.next()) {
				GetFirstVisitPlantData plantData = getFirstVisitByVisitIdResponse.addNewGetFirstVisitPlantData();
				int plantDetailsId = rs1.getInt("id");
				String plantDetalsUUid = rs1.getString("plantDetailsUUid");
				String plantTagUUId = rs1.getString("plantTagUUId");
				String cropType = rs1.getString("cropType");
				String variety = rs1.getString("variety");
				String raw = rs1.getString("raw");
				String column = rs1.getString("column");
				int plantTagId = rs1.getInt("plantTagId");

				plantData.setPlantId(plantDetailsId);
				plantData.setPlantUUId(plantDetalsUUid);
				plantData.setPlantTagUUId(plantTagUUId);
				plantData.setCropType(cropType);
				plantData.setVariety(variety);
				plantData.setRow(raw != null ? raw : "");
				plantData.setColumn(column != null ? column : "");
				plantData.setPlantTagId(plantTagId);

				attachment1 = MAttachment.get(ctx, plantTableId, plantDetailsId);
				if (attachment1 != null) {
					MAttachmentEntry[] entries1 = attachment1.getEntries();
					plantData.setPlantTableId(plantTableId);
					plantData.setImageRecordId(attachment1.getRecord_ID());
//					if (entries1.length > 0) {
					for (int i = entries1.length - 1; i >= 0; i--) {
//						MAttachmentEntry entry = entries[i];
//						byte[] data = entry.getData();
//						base64 = Base64.getEncoder().encodeToString(data);
						ImageArray2 imageArray = plantData.addNewImageArray1();
//						imageArray.setImage64(base64);
						imageArray.setImageIndexId(i);
					}
//					} else {
//						plantData.addNewImageArray1();
//					}
				} else
					plantData.addNewImageArray1();
			}
			if (getFirstVisitByVisitIdResponse.sizeOfGetFirstVisitPlantDataArray() == 0)
				getFirstVisitByVisitIdResponse.addNewGetFirstVisitPlantData();
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			getFirstVisitByVisitIdResponse.setError(INTERNAL_SERVER_ERROR);
			getFirstVisitByVisitIdResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			closeDbCon(pstm1, rs1);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return getFirstVisitByVisitIdResponseDocument;
	}

	@Override
	public GetAllFarmerListResponseDocument getAllFarmerList(GetAllFarmerListRequestDocument req) {
		GetAllFarmerListResponseDocument getAllFarmerListResponseDocument = GetAllFarmerListResponseDocument.Factory
				.newInstance();
		GetAllFarmerListResponse getAllFarmerListResponse = getAllFarmerListResponseDocument
				.addNewGetAllFarmerListResponse();
		GetAllFarmerListRequest loginRequest = req.getGetAllFarmerListRequest();
		Trx trx = null;
		int count = 0;
//		String base64 = "";
		int tableId = MTable.getTable_ID(TABLE_FARMER);
		MAttachment attachment = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String searchKey = loginRequest.getSearchKey();
		int pageSize = loginRequest.getPageSize(); // Number of records per page
		int pageNumber = loginRequest.getPageNumber(); // Current page number
		int offset = (pageNumber - 1) * pageSize; // Calculate offset
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
		int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			List<String> farmerList = new ArrayList<>();
			List<String> mobilenoList = new ArrayList<>();
			List<String> villageName2List = new ArrayList<>();
			List<String> landmarkList = new ArrayList<>();
			List<String> talukNameList = new ArrayList<>();
			List<String> cityNameList = new ArrayList<>();
			List<String> districtList = new ArrayList<>();
			List<String> stateList = new ArrayList<>();
			List<String> pinCodeList = new ArrayList<>();

			for (Filter filter : loginRequest.getFiltersArray()) {
				switch (filter.getKey().toLowerCase()) {
				case "farmername":
					farmerList.add("'" + filter.getValue() + "'");
					break;
				case "mobileno":
					mobilenoList.add("'" + filter.getValue() + "'");
					break;
				case "villagename":
					villageName2List.add("'" + filter.getValue() + "'");
					break;
				case "landmark":
					landmarkList.add("'" + filter.getValue() + "'");
					break;
				case "talukname":
					talukNameList.add("'" + filter.getValue() + "'");
					break;
				case "cityname":
					cityNameList.add("'" + filter.getValue() + "'");
					break;
				case "district":
					districtList.add("'" + filter.getValue() + "'");
					break;
				case "state":
					stateList.add("'" + filter.getValue() + "'");
					break;
				case "pincode":
					pinCodeList.add("'" + filter.getValue() + "'");
					break;
				}
			}

			StringBuilder sql = new StringBuilder(
					"SELECT tc_farmer_id AS id,name AS farmerName,mobileno AS mobileNo,villagename AS villageName,landmark AS landmark,\n"
							+ "villageName2 As villageName2,landmark AS landmark,talukName As talukName,city as city,district as district,state as state,pinCode As pincode,COUNT(*) OVER () AS totalCount	\n"
							+ "FROM adempiere.tc_farmer WHERE AD_Client_Id = ? ");
			
			if (!farmerList.isEmpty())
	            sql.append(" AND name IN (").append(farmerList.stream().map(x -> "?").collect(Collectors.joining(","))).append(")");
	        if (!mobilenoList.isEmpty())
	            sql.append(" AND mobileno IN (").append(mobilenoList.stream().map(x -> "?").collect(Collectors.joining(","))).append(")");
	        if (!villageName2List.isEmpty())
	            sql.append(" AND villageName2 IN (").append(villageName2List.stream().map(x -> "?").collect(Collectors.joining(","))).append(")");
	        if (!landmarkList.isEmpty())
	            sql.append(" AND landmark IN (").append(landmarkList.stream().map(x -> "?").collect(Collectors.joining(","))).append(")");
	        if (!talukNameList.isEmpty())
	            sql.append(" AND talukName IN (").append(talukNameList.stream().map(x -> "?").collect(Collectors.joining(","))).append(")");
	        if (!cityNameList.isEmpty())
	            sql.append(" AND city IN (").append(cityNameList.stream().map(x -> "?").collect(Collectors.joining(","))).append(")");
	        if (!districtList.isEmpty())
	            sql.append(" AND district IN (").append(districtList.stream().map(x -> "?").collect(Collectors.joining(","))).append(")");
	        if (!stateList.isEmpty())
	            sql.append(" AND state IN (").append(stateList.stream().map(x -> "?").collect(Collectors.joining(","))).append(")");
	        if (!pinCodeList.isEmpty())
	            sql.append(" AND pincode IN (").append(pinCodeList.stream().map(x -> "?").collect(Collectors.joining(","))).append(")");

			// Add conditions for searchKey
			if (searchKey != null && !searchKey.trim().isEmpty()) {
				sql.append(
						" AND (name ILIKE ? OR villagename ILIKE ? OR pincode ILIKE ? OR landmark ILIKE ? OR mobileno ILIKE ? OR villageName2 ILIKE ? OR talukName ILIKE ? OR city ILIKE ? OR district ILIKE ? OR state ILIKE ?)");
			}
			sql.append(" order by tc_farmer_id desc");
			sql.append(" LIMIT " + pageSize + " OFFSET " + offset + " ");

			pstm = DB.prepareStatement(sql.toString(), null);

			// Set searchKey parameters
			pstm.setInt(1, clientId);
			int parameterIndex = 1;
			
			if (searchKey != null && !searchKey.trim().isEmpty()) {
				for (int i = 0; i < 10; i++) {
					pstm.setString(parameterIndex++, "%" + searchKey + "%");
				}
			}
			rs = pstm.executeQuery();

			if (!rs.isBeforeFirst()) {
				getAllFarmerListResponse.setIsError(false);
				getAllFarmerListResponse.setCount(count);
				getAllFarmerListResponse.addNewGetFarmerData();
				return getAllFarmerListResponseDocument;
			}

			while (rs.next()) {
				GetFarmerData data = getAllFarmerListResponse.addNewGetFarmerData();
				int farmerId = rs.getInt("id");
				String farmerName = rs.getString("farmerName");
				String mobileNo = rs.getString("mobileNo");
				String villageName2 = rs.getString("villageName2");
				String landmark = rs.getString("landmark");
				String talukName = rs.getString("talukName");
				String city = rs.getString("city");
				String district = rs.getString("district");
				String state = rs.getString("state");
				String pincode = rs.getString("pincode");
				count = rs.getInt("totalCount");

				data.setFarmerId(farmerId);
				data.setFarmerName(farmerName);
				data.setMobileNo(mobileNo);
				data.setVillageName(villageName2 != null ? villageName2 : "");
				data.setLandmark(landmark != null ? landmark : "");
				data.setTalukName(talukName != null ? talukName : "");
				data.setCityName(city != null ? city : "");
				data.setDistrict(district != null ? district : "");
				data.setState(state != null ? state : "");
				data.setPincode(pincode != null ? pincode : "");

				attachment = MAttachment.get(ctx, tableId, farmerId);
				if (attachment != null) {
					MAttachmentEntry[] entries = attachment.getEntries();
//					if (entries.length > 0) {
					for (int i = entries.length - 1; i >= 0;) {
//						MAttachmentEntry entry = entries[i];
//						byte[] data = entry.getData();
//						base64 = Base64.getEncoder().encodeToString(data);
						ImageArray2 imageArray = data.addNewImageArray1();
//						imageArray.setImage64(base64);
						imageArray.setImageIndexId(i);
						break;
					}
//					} else {
//						data.addNewImageArray1();
//					}
				} else
					data.addNewImageArray1();
			}
			getAllFarmerListResponse.setCount(count);
			getAllFarmerListResponse.setTableId(tableId);
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			getAllFarmerListResponse.setError(INTERNAL_SERVER_ERROR);
			getAllFarmerListResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return getAllFarmerListResponseDocument;
	}

	@Override
	public GetFarmerListByIdResponseDocument getFarmerListById(GetFarmerListByIdRequestDocument req) {
		GetFarmerListByIdResponseDocument getFarmerListByIdResponseDocument = GetFarmerListByIdResponseDocument.Factory
				.newInstance();
		GetFarmerListByIdResponse getFarmerListByIdResponse = getFarmerListByIdResponseDocument
				.addNewGetFarmerListByIdResponse();
		GetFarmerListByIdRequest loginRequest = req.getGetFarmerListByIdRequest();
		int farmerId = 0;
		Trx trx = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
		int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			farmerId = loginRequest.getFarmerId();
			String farmerNames = TCUtills.getFarmerName(clientId, farmerId);
			if (farmerNames == "") {
				getFarmerListByIdResponse.setError("FramerId is not valid please check");
				getFarmerListByIdResponse.setIsError(true);
				return getFarmerListByIdResponseDocument;
			}

			String sql = "SELECT tc_farmer_id AS id,name As farmerName,mobileno As mobileNo,longitude AS longitude,latitude AS latitude,\n"
					+ "villageName2 As villageName2,landmark As landmark,talukName As talukName,city as city,district as district,state as state,pinCode As pincode\n"
					+ "FROM adempiere.tc_farmer WHERE AD_Client_Id = ? AND tc_farmer_id = ?"
					+ ";";
			pstm = DB.prepareStatement(sql, null);
			pstm.setInt(1, clientId);
			pstm.setInt(2, farmerId);
			rs = pstm.executeQuery();
			while (rs.next()) {
				GetFarmerByIdData data = getFarmerListByIdResponse.addNewGetFarmerByIdData();
				String farmerName = rs.getString("farmerName");
				String mobileNo = rs.getString("mobileNo");
				String longitude = rs.getString("longitude");
				String latitude = rs.getString("latitude");
				String villageName = rs.getString("villageName2");
				String landmark = rs.getString("landmark");
				String talukName = rs.getString("talukName");
				String cityName = rs.getString("city");
				String district = rs.getString("district");
				String state = rs.getString("state");
				String pincode = rs.getString("pincode");

				data.setFarmerId(farmerId);
				data.setFarmerName(farmerName);
				data.setMobileNo(mobileNo);
				data.setLongitude(longitude != null ? longitude : "");
				data.setLatitude(latitude != null ? latitude : "");
				data.setVillageName(villageName != null ? villageName : "");
				data.setLandmark(landmark != null ? landmark + " " : "");
				data.setTalukName(talukName != null ? talukName : "");
				data.setCityName(cityName != null ? cityName : "");
				data.setDistrict(district != null ? district : "");
				data.setState(state != null ? state : "");
				data.setPincode(pincode != null ? pincode : "");
			}
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			getFarmerListByIdResponse.setError(INTERNAL_SERVER_ERROR);
			getFarmerListByIdResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return getFarmerListByIdResponseDocument;
	}

	@Override
	public GetVisitListByFarmerIdResponseDocument getVisitListByFarmerId(GetVisitListByFarmerIdRequestDocument req) {
		GetVisitListByFarmerIdResponseDocument getVisitListByFarmerIdResponseDocument = GetVisitListByFarmerIdResponseDocument.Factory
				.newInstance();
		GetVisitListByFarmerIdResponse getVisitListByFarmerIdResponse = getVisitListByFarmerIdResponseDocument
				.addNewGetVisitListByFarmerIdResponse();
		GetVisitListByFarmerIdRequest loginRequest = req.getGetVisitListByFarmerIdRequest();
		int farmerId = 0;
		Trx trx = null;
//		String base64 = "";
		int tableId = MTable.getTable_ID(TABLE_FARMER);
		MAttachment attachment = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
		int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			farmerId = loginRequest.getFarmerId();
			String farmerNames = TCUtills.getFarmerName(clientId, farmerId);
			if (farmerNames == "") {
				getVisitListByFarmerIdResponse.setError("FramerId is not valid please check");
				getVisitListByFarmerIdResponse.setIsError(true);
				return getVisitListByFarmerIdResponseDocument;
			}
			String sql = "SELECT v.cycleNo AS cycleNo,v.tc_visit_id AS id,v.tc_farmer_id As farmerId,f.name As farmerName,v.mobileNo AS mobileNo,v.date As visitDate,\n"
					+ "vt.name As visitType,s.name As status,v.visitdone As visitdone FROM adempiere.tc_visit v\n"
					+ "JOIN adempiere.tc_farmer f ON f.tc_farmer_id = v.tc_farmer_id\n"
					+ "JOIN adempiere.tc_visitType vt ON vt.tc_visittype_id = v.tc_visittype_id\n"
					+ "JOIN adempiere.tc_status s ON s.tc_status_id = v.tc_status_id	\n" + "WHERE v.ad_client_id = ?"
					+ " AND v.tc_farmer_id = ? AND s.name <> 'Cancelled'  ORDER BY v.created";
			pstm = DB.prepareStatement(sql, null);
			pstm.setInt(1, clientId);
			pstm.setInt(2, farmerId);
			rs = pstm.executeQuery();
			while (rs.next()) {
				getVisitListByFarmerIdResponse.setTableId(tableId);
				GetVisitDataByFarmerId data = getVisitListByFarmerIdResponse.addNewGetVisitDataByFarmerId();
				int visitId = rs.getInt("id");
				String farmerName = rs.getString("farmerName");
				String mobileNo = rs.getString("mobileNo");
				String visitDate = rs.getString("visitDate");
				String visitType = rs.getString("visitType");
				String status = rs.getString("status");
				Boolean visitDone = rs.getBoolean("visitdone");
				int cycleNo = rs.getInt("cycleNo");

				data.setVisitId(visitId);
				data.setFarmerId(farmerId);
				data.setFarmerName(farmerName);
				data.setMobileNo(mobileNo);
				data.setDate(visitDate);
				data.setVisitType(visitType);
				data.setStatus(status);
				data.setVisitDone(visitDone);
				data.setCycleNo(cycleNo);

				attachment = MAttachment.get(ctx, tableId, farmerId);
				if (attachment != null) {
					MAttachmentEntry[] entries = attachment.getEntries();
//					if (entries.length > 0) {
					for (int i = entries.length - 1; i >= 0;) {
//						MAttachmentEntry entry = entries[i];
//						byte[] data = entry.getData();
//						base64 = Base64.getEncoder().encodeToString(data);
						ImageArray2 imageArray = data.addNewImageArray1();
//						imageArray.setImage64(base64);
						imageArray.setImageIndexId(i);
						break;
					}
//					} else {
//						data.addNewImageArray1();
//					}
				} else
					data.addNewImageArray1();
			}
			if (getVisitListByFarmerIdResponse.sizeOfGetVisitDataByFarmerIdArray() == 0)
				getVisitListByFarmerIdResponse.addNewGetVisitDataByFarmerId();
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			getVisitListByFarmerIdResponse.setError(INTERNAL_SERVER_ERROR);
			getVisitListByFarmerIdResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return getVisitListByFarmerIdResponseDocument;
	}

	@Override
	public GetPlantDetailsListByFarmerIdResponseDocument getPlantDetailsListByFarmerId(
			GetPlantDetailsListByFarmerIdRequestDocument req) {
		GetPlantDetailsListByFarmerIdResponseDocument getPlantDetailsListByFarmerIdResponseDocument = GetPlantDetailsListByFarmerIdResponseDocument.Factory
				.newInstance();
		GetPlantDetailsListByFarmerIdResponse getPlantDetailsListByFarmerIdResponse = getPlantDetailsListByFarmerIdResponseDocument
				.addNewGetPlantDetailsListByFarmerIdResponse();
		GetPlantDetailsListByFarmerIdRequest loginRequest = req.getGetPlantDetailsListByFarmerIdRequest();
		int farmerId = 0;
		Trx trx = null;
//		String base64 = "";
		int tableId = MTable.getTable_ID(TABLE_PLANT_DETAILS);
		MAttachment attachment = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
		int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			farmerId = loginRequest.getFarmerId();
			String farmerName = TCUtills.getFarmerName(clientId, farmerId);
			if (farmerName == "") {
				getPlantDetailsListByFarmerIdResponse.setError("FramerId is not valid please check");
				getPlantDetailsListByFarmerIdResponse.setIsError(true);
				return getPlantDetailsListByFarmerIdResponseDocument;
			}

			String sql = "SELECT pd.tc_plantdetails_id AS id,pd.planttaguuid AS plantTagUUid,pt.tc_planttag_id As plantTagId,ps.codeNo AS cropType,v.codeNo As variety,pd.isrejected AS rejectStatus,pd.raw AS row,pd.columns AS column\n"
					+ "FROM adempiere.tc_plantdetails pd JOIN adempiere.tc_plantspecies ps ON ps.tc_plantspecies_id = pd.tc_species_id\n"
					+ "JOIN adempiere.tc_variety v ON v.tc_variety_id = pd.tc_variety_id JOIN adempiere.tc_planttag pt ON pt.c_uuid = pd.planttaguuid	\n"
					+ "WHERE pd.AD_Client_ID = ? AND pd.tc_farmer_id = ?"
					+ " ORDER BY pd.created;";
			pstm = DB.prepareStatement(sql, null);
			pstm.setInt(1, clientId);
			pstm.setInt(2, farmerId);
			rs = pstm.executeQuery();
			while (rs.next()) {
				getPlantDetailsListByFarmerIdResponse.setTableId(tableId);
				GetPlantDetailsByFarmerId data = getPlantDetailsListByFarmerIdResponse
						.addNewGetPlantDetailsByFarmerId();
				int plantDetailsId = rs.getInt("id");
				int plantTagId = rs.getInt("plantTagId");
				String plantTagUUId = rs.getString("plantTagUUid");
				String cropType = rs.getString("cropType");
				String variety = rs.getString("variety");
				String rejectedStatus = rs.getString("rejectStatus");
				String row = rs.getString("row");
				String column = rs.getString("column");

				data.setPlantDetailsId(plantDetailsId);
				data.setFarmerId(farmerId);
				data.setPlantTagId(plantTagId);
				data.setPlantTagUUid(plantTagUUId);
				data.setCropType(cropType);
				data.setVariety(variety);
				data.setRejectStatus(rejectedStatus);
				data.setRow(row != null ? row : "");
				data.setColumn(column != null ? column : "");

				attachment = MAttachment.get(ctx, tableId, plantDetailsId);
				if (attachment != null) {
					MAttachmentEntry[] entries = attachment.getEntries();
//					if (entries.length > 0) {
					for (int i = entries.length - 1; i >= 0;) {
//						MAttachmentEntry entry = entries[i];
//						byte[] data = entry.getData();
//						base64 = Base64.getEncoder().encodeToString(data);
						ImageArray2 imageArray = data.addNewImageArray1();
//						imageArray.setImage64(base64);
						imageArray.setImageIndexId(i);
						break;
					}
//					} else {
//						data.addNewImageArray1();
//					}
				} else
					data.addNewImageArray1();
			}
			if (getPlantDetailsListByFarmerIdResponse.sizeOfGetPlantDetailsByFarmerIdArray() == 0)
				getPlantDetailsListByFarmerIdResponse.addNewGetPlantDetailsByFarmerId();
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			getPlantDetailsListByFarmerIdResponse.setError(INTERNAL_SERVER_ERROR);
			getPlantDetailsListByFarmerIdResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return getPlantDetailsListByFarmerIdResponseDocument;
	}

	@Override
	public AddIotRecordResponseDocument addIotRecord(AddIotRecordRequestDocument req) {
		AddIotRecordResponseDocument addIotRecordResponseDocument = AddIotRecordResponseDocument.Factory.newInstance();
		AddIotRecordResponse addIotRecordResponse = addIotRecordResponseDocument.addNewAddIotRecordResponse();
		AddIotRecordRequest loginRequest = req.getAddIotRecordRequest();
		int deviceId = 0;
		String humidity = safeTrim(loginRequest.getHumidity());
		String temperature = safeTrim(loginRequest.getTemperature());
		String voltage = safeTrim(loginRequest.getVoltage());
		String userTimestampStr = safeTrim(loginRequest.getCustomTimestamp());
		int roomId = 0, cornorNameId = 0;
		String cornorName = "", deviceUUid = "", roomName = "";
		Trx trx = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
			int orgId = Env.getAD_Org_ID(ctx);
			int clientId = Env.getAD_Client_ID(ctx);

			if (containsMaliciousPattern(humidity)) {
				addIotRecordResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				addIotRecordResponse.setIsError(true);
			    return addIotRecordResponseDocument;
			}
			
			if (containsMaliciousPattern(temperature)) {
				addIotRecordResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				addIotRecordResponse.setIsError(true);
			    return addIotRecordResponseDocument;
			}
			
			if (containsMaliciousPattern(voltage)) {
				addIotRecordResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				addIotRecordResponse.setIsError(true);
			    return addIotRecordResponseDocument;
			}
			
			if (containsMaliciousPattern(userTimestampStr)) {
				addIotRecordResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				addIotRecordResponse.setIsError(true);
			    return addIotRecordResponseDocument;
			}
			
			deviceId = loginRequest.getDeviceId();
			
	        String trxName = Trx.createTrxName(getClass().getName() + "_");
	        trx = Trx.get(trxName, true);
	        trx.start();

	        log.info("Trx Started at: {}" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));

			int normalId = TCUtills.getRecordId(clientId, "tc_tempstatus", TEMPERATURE_STATUS_NORMAL);
			if (normalId == 0) {
				addIotRecordResponse.setError("Temperature Statusis not in table record " + TEMPERATURE_STATUS_NORMAL + "");
				addIotRecordResponse.setIsError(true);
				return addIotRecordResponseDocument;
			}
			
			
			Timestamp userTimestamp = null;
			if (userTimestampStr != null && !userTimestampStr.trim().isEmpty()) {
			    try {
			        if (userTimestampStr.contains("T")) {
			            userTimestamp = Timestamp.valueOf(
			                userTimestampStr.replace("T", " ")
			            );
			        } else {
			            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			            LocalDateTime localDateTime = LocalDateTime.parse(userTimestampStr, formatter);
			            userTimestamp = Timestamp.valueOf(localDateTime);
			        }
			    } catch (Exception e) {
			    	if (trx != null) trx.rollback();
			        addIotRecordResponse.setError("Invalid timestamp format. Use yyyy-MM-dd HH:mm:ss or ISO 8601 format");
			        log.saveError(trxName, e);
			        addIotRecordResponse.setIsError(true);
			        return addIotRecordResponseDocument;
			    }
			}
			
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
			LocalDateTime afterTrx = LocalDateTime.now();
			log.info("Trx Started at: " + afterTrx.format(formatter));

			TCIOTdeviceData data = new TCIOTdeviceData(ctx, deviceId, trx.getTrxName());
			if (data == null || data.get_ID() == 0) {
				addIotRecordResponse
						.setError("Device Id not available in Database please enter valid Device Id " + deviceId + "");
				addIotRecordResponse.setIsError(true);
				return addIotRecordResponseDocument;
			}
			
			X_tc_tempstatus temStatus = new X_tc_tempstatus(ctx, normalId, trxName);
	        int minTemp = safeParseInt(temStatus.getmin_temperature(), 21);
	        int maxTemp = safeParseInt(temStatus.getmax_temperature(), 26);
	        int minHumidity = safeParseInt(temStatus.getmin_humidity(), 40);
	        int maxHumidity = safeParseInt(temStatus.getmax_humidity(), 70);
	        double deadBattery = safeParseDouble(temStatus.getdead_value(), 2.9);
	        double highBattery = safeParseDouble(temStatus.gethigh_value(), 3.3);
	        
	        BigDecimal humidityVal = safeParseBigDecimal(humidity, BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
	        BigDecimal temperatureVal = safeParseBigDecimal(temperature, BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
//	        BigDecimal voltageVal = safeParseBigDecim al(voltage, BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
			
			roomId = data.getM_LocatorType_ID();
			cornorNameId = data.getTC_temperatureposition_ID();
			deviceUUid = data.getc_uuid();
			X_TC_temperatureposition cornor = new X_TC_temperatureposition(ctx, cornorNameId, trx.getTrxName());
			cornorName = cornor.getName();

			MLocatorType room = new MLocatorType(ctx, roomId, trx.getTrxName());
			roomName = room.getName();
			
			String batteryPercentage = "";
			if (voltage != null && !voltage.trim().isEmpty()) {
				String voltageInt = voltage.replaceAll("[^0-9.\\-]", "");
				BigDecimal voltageVal = safeParseBigDecimal(voltageInt, BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
 	            double result = (voltageVal.doubleValue() - deadBattery) / (highBattery - deadBattery) * 100;
	            result = Math.max(0, Math.min(result, 100));
	            batteryPercentage = String.format("%.0f", result);
	        }

			TCIOTRecord record = new TCIOTRecord(ctx, 0, trx.getTrxName());
			record.setAD_Org_ID(orgId);
			record.setM_LocatorType_ID(roomId);
			record.setHumidity(humidityVal.toPlainString());
			record.setTemperature(temperatureVal.toPlainString());
			record.setName(data.getValue());
			record.setTC_devicedata_ID(deviceId);
			record.setbattery_percentage(batteryPercentage);
			
			// Set the user timestamp if provided
	        if (userTimestamp != null) {
	            record.setcustom_timestamp(userTimestamp);
	        }
			
			double temp = temperatureVal.doubleValue();
	        if (temp < minTemp) {
	            record.settc_tempstatus_ID(TCUtills.getRecordId(clientId, "tc_tempstatus", TEMPERATURE_STATUS_OVERCOOL));
	        } else if (temp > maxTemp) {
	            record.settc_tempstatus_ID(TCUtills.getRecordId(clientId, "tc_tempstatus", TEMPERATURE_STATUS_OVERHEAT));
	        } else {
	            record.settc_tempstatus_ID(normalId);
	        }
	        
	        double hum = humidityVal.doubleValue();
	        if (hum < minHumidity) {
	            record.sethumiditystatus("Less Humidity");
	        } else if (hum > maxHumidity) {
	            record.sethumiditystatus("High Humidity");
	        } else {
	            record.sethumiditystatus("Normal");
	        }
			
			if (!record.save()) {
				throw new Exception("Failed to add IOT data: " + record);
			}
			trx.commit();
			addIotRecordResponse.setRecordId(record.get_ID());
			addIotRecordResponse.setIsError(false);			
			LocalDateTime afterTrxCompleted = LocalDateTime.now();
			log.info("Trx Started at: " + afterTrxCompleted.format(formatter));

			Map<String, String> datas = new HashMap<>();
			datas.put("Room No. ", " " + roomName);
			datas.put("Cornor Name ", " " + cornorName);
//			datas.put("Sensor Type ", " " + sensorType);
			datas.put("Device UUId ", " " + deviceUUid);

		} catch (Exception e) {
			if (trx != null) trx.rollback();
			addIotRecordResponse.setError(INTERNAL_SERVER_ERROR);
			addIotRecordResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return addIotRecordResponseDocument;
	}
	
	/** Utility parsers */
	private int safeParseInt(String val, int defaultVal) {
	    try { return (val != null && !val.trim().isEmpty()) ? Integer.parseInt(val) : defaultVal; }
	    catch (Exception e) { return defaultVal; }
	}
	private double safeParseDouble(String val, double defaultVal) {
	    try { return (val != null && !val.trim().isEmpty()) ? Double.parseDouble(val) : defaultVal; }
	    catch (Exception e) { return defaultVal; }
	}
	private BigDecimal safeParseBigDecimal(String val, BigDecimal defaultVal) {
	    try { return (val != null && !val.trim().isEmpty()) ? new BigDecimal(val) : defaultVal; }
	    catch (Exception e) { return defaultVal; }
	}

	@Override
	public GetFORoleReportResponseDocument getFiledOfficerRoleReport(GetFORoleReportRequestDocument req) {
		GetFORoleReportResponseDocument getFORoleReportResponseDocument = GetFORoleReportResponseDocument.Factory
				.newInstance();
		GetFORoleReportResponse getFORoleReportResponse = getFORoleReportResponseDocument
				.addNewGetFORoleReportResponse();
		PreparedStatement pstm = null;
		ResultSet rs = null;
		Trx trx = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
			String user = safeTrim(new MUser(ctx, Env.getAD_User_ID(ctx), null).getName());
		int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			String sql = "WITH status_list AS (\n" + "	SELECT 'Completed' AS name\n" + "    UNION ALL\n"
					+ "    SELECT 'Cancelled' AS name\n" + "    UNION ALL\n" + "    SELECT 'In Progress' AS name)\n"
					+ "SELECT sl.name As name,COALESCE(COUNT(v.tc_status_id), 0) AS total_count\n"
					+ "FROM status_list sl CROSS JOIN adempiere.ad_user u LEFT JOIN adempiere.tc_status s ON sl.name = s.name\n"
					+ "LEFT JOIN adempiere.tc_visit v ON s.tc_status_id = v.tc_status_id AND v.createdby = u.ad_user_id\n"
					+ "WHERE s.ad_client_id = ? AND u.name = ?"
					+ " GROUP BY sl.name,s.tc_status_id ORDER BY s.tc_status_id;\n" + "";
			pstm = DB.prepareStatement(sql, null);
			pstm.setInt(1, clientId);
			pstm.setString(2, user);
			rs = pstm.executeQuery();
			while (rs.next()) {
				GetReportData data = getFORoleReportResponse.addNewGetReportData();
				String name = rs.getString("name");
				int totalCount = rs.getInt("total_count");
				data.setStatusName(name);
				data.setStatusCount(totalCount);
			}
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			getFORoleReportResponse.setError(INTERNAL_SERVER_ERROR);
			getFORoleReportResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return getFORoleReportResponseDocument;
	}

	@Override
	public GetFORoleReportSuckerCountResponseDocument getFORoleReportSuckerCount(
			GetFORoleReportSuckerCountRequestDocument req) {
		GetFORoleReportSuckerCountResponseDocument getFORoleReportSuckerCountResponseDocument = GetFORoleReportSuckerCountResponseDocument.Factory
				.newInstance();
		GetFORoleReportSuckerCountResponse getFORoleReportSuckerCountResponse = getFORoleReportSuckerCountResponseDocument
				.addNewGetFORoleReportSuckerCountResponse();
		GetFORoleReportSuckerCountRequest loginRequest = req.getGetFORoleReportSuckerCountRequest();
		String userInput = loginRequest.getUserInput();

		PreparedStatement pstm = null;
		ResultSet rs = null;
		Trx trx = null;
		String sql = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
			String user = safeTrim(new MUser(ctx, Env.getAD_User_ID(ctx), null).getName());
		int clientId = Env.getAD_Client_ID(ctx);
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();


			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			
			if (containsMaliciousPattern(userInput)) {
				getFORoleReportSuckerCountResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				getFORoleReportSuckerCountResponse.setIsError(true);
			    return getFORoleReportSuckerCountResponseDocument;
			}
			
			
			if (userInput.equals("day")) {
				sql = "SELECT day_info.day_name AS day_name,current_date AS date,COALESCE(SUM(cv.suckerno), 0) AS sucker_count\n"
						+ "FROM (SELECT to_char(current_date, 'Day') AS day_name) AS day_info\n"
						+ "LEFT JOIN adempiere.tc_collectionjoinplant cv ON cv.created::date = current_date AND cv.ad_client_id = ?\n"
						+ "AND cv.createdby IN (SELECT ad_user_id FROM adempiere.ad_user WHERE name = ?) GROUP BY day_info.day_name;";
			} else if (userInput.equals("week")) {
				sql = "WITH days AS (SELECT generate_series(0, 6) AS day_of_week),\n" + "sucker_counts AS (\n"
						+ "SELECT date_trunc('day', v.created) AS visit_day,to_char(v.created, 'FMDay') AS day_name,\n"
						+ "EXTRACT(dow FROM v.created) AS day_of_week,SUM(v.suckerno) AS visit_count\n"
						+ "FROM adempiere.tc_collectionjoinplant v JOIN adempiere.ad_user u ON u.ad_user_id = v.createdby\n"
						+ "WHERE v.ad_client_id = ? AND u.name = ? AND v.created::date >= current_date - interval '6 days' AND v.created::date <= current_date\n"
						+ "GROUP BY date_trunc('day', v.created),to_char(v.created, 'FMDay'),EXTRACT(dow FROM v.created))\n"
						+ "SELECT (current_date - interval '6 days' + d.day_of_week * interval '1 day')::date AS dates,\n"
						+ "COALESCE(vc.day_name, to_char(current_date - interval '6 days' + d.day_of_week * interval '1 day', 'FMDay')) AS day_name,\n"
						+ "COALESCE(vc.visit_count, 0) AS sucker_count FROM days d\n"
						+ "LEFT JOIN sucker_counts vc ON current_date - interval '6 days' + d.day_of_week * interval '1 day' = vc.visit_day ORDER BY dates;";
			} else if (userInput.equals("month")) {
				sql = "WITH weeks AS (SELECT generate_series(0, 4) AS week_number),\n"
						+ "sucker_counts AS (SELECT date_trunc('week', v.created) AS week_start,to_char(date_trunc('week', v.created), 'YYYY-MM-DD') AS week_start_str,\n"
						+ "SUM(v.suckerno) AS sucker_count FROM adempiere.tc_collectionjoinplant v JOIN adempiere.ad_user u ON u.ad_user_id = v.createdby\n"
						+ "WHERE v.ad_client_id = ? AND u.name = ? AND v.created::date >= (current_date - interval '29 days') AND v.created::date <= current_date GROUP BY date_trunc('week', v.created)),\n"
						+ "date_range AS (SELECT (current_date - interval '29 days')::date + generate_series(0, 29) AS day)\n"
						+ "SELECT to_char(date_trunc('week', day), 'YYYY-MM-DD') AS week_start,COALESCE(vc.sucker_count, 0) AS sucker_count\n"
						+ "FROM date_range LEFT JOIN sucker_counts vc ON date_trunc('week', day) = vc.week_start\n"
						+ "GROUP BY date_trunc('week', day), vc.sucker_count ORDER BY week_start;\n" + "";
			} else if (userInput.equals("year")) {
				sql = "WITH months AS (SELECT generate_series(0, 11) AS month),\n" + "sucker_counts AS (\n"
						+ "SELECT date_trunc('month', v.created) AS month_year,to_char(v.created, 'FMMonth') AS month_name,SUM(v.suckerno) AS sucker_count\n"
						+ "FROM adempiere.tc_collectionjoinplant v JOIN adempiere.ad_user u ON u.ad_user_id = v.createdby\n"
						+ "WHERE v.ad_client_id = ? AND u.name = ? AND v.created::date >= (current_date - interval '364 days') \n"
						+ "AND v.created::date <= current_date GROUP BY date_trunc('month', v.created), to_char(v.created, 'FMMonth'))\n"
						+ "SELECT to_char(date_trunc('month', current_date) - (m.month || ' months')::interval, 'YYYY-MM-01') AS month_date,COALESCE(vc.sucker_count, 0) AS sucker_count \n"
						+ "FROM months m LEFT JOIN sucker_counts vc \n"
						+ "ON date_trunc('month', current_date) - (m.month || ' months')::interval = vc.month_year\n"
						+ "ORDER BY date_trunc('month', current_date) - (m.month || ' months')::interval;";
			} else if (userInput.equals("all")) {
				sql = "WITH year_counts AS (\n"
						+ "SELECT date_trunc('year', v.created) AS year_start, sum(v.suckerno) AS counts FROM adempiere.tc_collectionjoinplant v\n"
						+ "JOIN adempiere.ad_user u ON u.ad_user_id = v.createdby WHERE v.ad_client_id = ? AND u.name = ?\n" + "GROUP BY date_trunc('year', v.created)),\n"
						+ "year_range AS (SELECT date_trunc('year', CURRENT_DATE) AS year_start\n" + "UNION ALL\n"
						+ "SELECT generate_series((SELECT MIN(year_start) FROM year_counts),date_trunc('year', CURRENT_DATE),interval '1 year') AS year_start\n"
						+ "),\n" + "all_years AS (SELECT DISTINCT year_start FROM year_range)\n"
						+ "SELECT to_char(a.year_start, 'YYYY-01-01') AS year_date, COALESCE(y.counts, 0) AS counts FROM all_years a\n"
						+ "LEFT JOIN year_counts y ON a.year_start = y.year_start ORDER BY a.year_start;";
			}
			if (sql == null) {
				getFORoleReportSuckerCountResponse.setError("No SQL");
				getFORoleReportSuckerCountResponse.setIsError(true);
				return getFORoleReportSuckerCountResponseDocument;
			}

			SXSSFWorkbook workbook = new SXSSFWorkbook(-1);

			CellStyle myStyle = workbook.createCellStyle();
			org.apache.poi.ss.usermodel.Font myFont = workbook.createFont();
			myFont.setBold(true);
			myStyle.setFont(myFont);

			CellStyle wrapStyle = workbook.createCellStyle();
			wrapStyle.setWrapText(true);
			// Create sheet and rows
			SXSSFSheet sheet = workbook.createSheet("Field Officer Visit Count Report");
			SXSSFRow headerRow = sheet.createRow(0);

			// Create header cells
			SXSSFCell cellH0 = headerRow.createCell(0);
			cellH0.setCellValue("Label Name");
			cellH0.setCellStyle(myStyle);

			SXSSFCell cellH1 = headerRow.createCell(1);
			cellH1.setCellValue("Count");
			cellH1.setCellStyle(myStyle);

			pstm = DB.prepareStatement(sql, null);
			pstm.setInt(1, clientId);
			pstm.setString(2, user);
			rs = pstm.executeQuery();
			int i = 1;
			while (rs.next()) {
				GetReportDataSuckerCount data = getFORoleReportSuckerCountResponse.addNewGetReportDataSuckerCount();
				if (userInput.equals("day")) {
					String date = rs.getString("date");
					int count = rs.getInt("sucker_count");
					data.setLabelName(date);
					data.setCount(count);

					SXSSFRow tableDescriptionRow = sheet.createRow(i);
					SXSSFCell cell1 = tableDescriptionRow.createCell(0);
					cell1.setCellValue(date);
					cell1.setCellStyle(wrapStyle);

					SXSSFCell cell2 = tableDescriptionRow.createCell(1);
					cell2.setCellValue(count);
					cell2.setCellStyle(wrapStyle);
				} else if (userInput.equals("week")) {
					String dates = rs.getString("dates");
					int count = rs.getInt("sucker_count");
					data.setLabelName(dates);
					data.setCount(count);

					SXSSFRow tableDescriptionRow = sheet.createRow(i);
					SXSSFCell cell1 = tableDescriptionRow.createCell(0);
					cell1.setCellValue(dates);
					cell1.setCellStyle(wrapStyle);

					SXSSFCell cell2 = tableDescriptionRow.createCell(1);
					cell2.setCellValue(count);
					cell2.setCellStyle(wrapStyle);
				} else if (userInput.equals("month")) {
					String week_start = rs.getString("week_start");
					int count = rs.getInt("sucker_count");
					data.setLabelName(week_start);
					data.setCount(count);

					SXSSFRow tableDescriptionRow = sheet.createRow(i);
					SXSSFCell cell1 = tableDescriptionRow.createCell(0);
					cell1.setCellValue(week_start);
					cell1.setCellStyle(wrapStyle);

					SXSSFCell cell2 = tableDescriptionRow.createCell(1);
					cell2.setCellValue(count);
					cell2.setCellStyle(wrapStyle);
				} else if (userInput.equals("year")) {
					String month_date = rs.getString("month_date");
					int count = rs.getInt("sucker_count");
					data.setLabelName(month_date);
					data.setCount(count);

					SXSSFRow tableDescriptionRow = sheet.createRow(i);
					SXSSFCell cell1 = tableDescriptionRow.createCell(0);
					cell1.setCellValue(month_date);
					cell1.setCellStyle(wrapStyle);

					SXSSFCell cell2 = tableDescriptionRow.createCell(1);
					cell2.setCellValue(count);
					cell2.setCellStyle(wrapStyle);
				} else if (userInput.equals("all")) {
					String year_date = rs.getString("year_date");
					int count = rs.getInt("counts");
					data.setLabelName(year_date);
					data.setCount(count);

					SXSSFRow tableDescriptionRow = sheet.createRow(i);
					SXSSFCell cell1 = tableDescriptionRow.createCell(0);
					cell1.setCellValue(year_date);
					cell1.setCellStyle(wrapStyle);

					SXSSFCell cell2 = tableDescriptionRow.createCell(1);
					cell2.setCellValue(count);
					cell2.setCellStyle(wrapStyle);
				}
				i++;
			}
			workbook.write(byteArrayOutputStream);
			workbook.close();
			byte[] excelBlobBytes = byteArrayOutputStream.toByteArray();
			getFORoleReportSuckerCountResponse.setExcelBlob(excelBlobBytes);
			getFORoleReportSuckerCountResponse.setIsError(false);
		} catch (Exception e) {
			if (trx != null) {
				trx.rollback(); // Rollback on error
			}
			getFORoleReportSuckerCountResponse.setError(INTERNAL_SERVER_ERROR);
			getFORoleReportSuckerCountResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return getFORoleReportSuckerCountResponseDocument;
	}

	@Override
	public GetLTRoleReportCultureLabelCountResponseDocument getLTRoleReportCultureLabelCount(
			GetLTRoleReportCultureLabelCountRequestDocument req) {
		GetLTRoleReportCultureLabelCountResponseDocument getLTRoleReportCultureLabelCountResponseDocument = GetLTRoleReportCultureLabelCountResponseDocument.Factory
				.newInstance();
		GetLTRoleReportCultureLabelCountResponse getLTRoleReportCultureLabelCountResponse = getLTRoleReportCultureLabelCountResponseDocument
				.addNewGetLTRoleReportCultureLabelCountResponse();
		GetLTRoleReportCultureLabelCountRequest loginRequest = req.getGetLTRoleReportCultureLabelCountRequest();
		String userInput = loginRequest.getUserInput();
		PreparedStatement pstm = null;
		ResultSet rs = null;
		Trx trx = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
			String user = safeTrim(new MUser(ctx, Env.getAD_User_ID(ctx), null).getName());
		int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			
			if (containsMaliciousPattern(userInput)) {
				getLTRoleReportCultureLabelCountResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				getLTRoleReportCultureLabelCountResponse.setIsError(true);
			    return getLTRoleReportCultureLabelCountResponseDocument;
			}
			
			String sql = null;

			if (userInput.equals("day")) {
				sql = "SELECT day_info.day_name AS day_name,current_date AS date,COALESCE(COUNT(v.*), 0) AS visit_count\n"
						+ "FROM (SELECT to_char(current_date, 'FMDay') AS day_name) AS day_info\n"
						+ "LEFT JOIN adempiere.tc_culturelabel v ON v.created::date = current_date AND v.ad_client_id = ? AND v.isdiscarded = 'N'\n"
						+ "AND v.createdby IN (SELECT ad_user_id FROM adempiere.ad_user WHERE name = ?)GROUP BY day_info.day_name;";
			} else if (userInput.equals("week")) {
				sql = "WITH days AS (SELECT generate_series(0, 6) AS day_of_week),\n" + "visit_counts AS (\n"
						+ "SELECT date_trunc('day', v.created) AS visit_day,to_char(v.created, 'FMDay') AS day_name,EXTRACT(dow FROM v.created) AS day_of_week,COUNT(*) AS visit_count\n"
						+ "FROM adempiere.tc_culturelabel v JOIN adempiere.ad_user u ON u.ad_user_id = v.createdby \n"
						+ "WHERE v.ad_client_id = ? AND v.isdiscarded = 'N' AND u.name = ? AND v.created::date >= current_date - interval '6 days' \n"
						+ "AND v.created::date <= current_date GROUP BY date_trunc('day', v.created),to_char(v.created, 'FMDay'),EXTRACT(dow FROM v.created))\n"
						+ "SELECT (current_date - interval '6 days' + d.day_of_week * interval '1 day')::date AS dates,\n"
						+ "COALESCE(vc.day_name, to_char(current_date - interval '6 days' + d.day_of_week * interval '1 day', 'FMDay')) AS day_name,COALESCE(vc.visit_count, 0) AS visit_count FROM days d \n"
						+ "LEFT JOIN visit_counts vc ON date_trunc('day', current_date - interval '6 days' + d.day_of_week * interval '1 day') = vc.visit_day ORDER BY dates;\n"
						+ "";
			} else if (userInput.equals("month")) {
				sql = "WITH weeks AS (SELECT generate_series(0, 4) AS week_number),\n" + "visit_counts AS (\n"
						+ "SELECT date_trunc('week', v.created) AS week_start,to_char(date_trunc('week', v.created), 'YYYY-MM-DD') AS week_start_str,\n"
						+ "COUNT(*) AS visit_count FROM adempiere.tc_culturelabel v JOIN adempiere.ad_user u ON u.ad_user_id = v.createdby\n"
						+ "WHERE v.ad_client_id = ? AND v.isdiscarded = 'N' AND u.name = ?  AND v.created::date >= (current_date - interval '29 days') AND v.created::date <= current_date GROUP BY date_trunc('week', v.created)),\n"
						+ "date_range AS (SELECT (current_date - interval '29 days')::date + generate_series(0, 29) AS day)\n"
						+ "SELECT to_char(date_trunc('week', day), 'YYYY-MM-DD') AS week_start,COALESCE(vc.visit_count, 0) AS visit_count\n"
						+ "FROM date_range LEFT JOIN visit_counts vc ON date_trunc('week', day) = vc.week_start\n"
						+ "GROUP BY date_trunc('week', day),vc.visit_count ORDER BY week_start;";
			} else if (userInput.equals("year")) {
				sql = "WITH months AS (SELECT generate_series(0, 11) AS month),\n" + "visit_counts AS (\n"
						+ "SELECT date_trunc('month', v.created) AS month_year,to_char(v.created, 'FMMonth') AS month_name,COUNT(*) AS visit_count\n"
						+ "FROM adempiere.tc_culturelabel v JOIN adempiere.ad_user u ON u.ad_user_id = v.createdby\n"
						+ "WHERE v.ad_client_id = ? AND v.isdiscarded = 'N' AND u.name = ? AND v.created::date >= (current_date - interval '364 days') AND v.created::date <= current_date\n"
						+ "GROUP BY date_trunc('month', v.created), to_char(v.created, 'FMMonth'))\n"
						+ "SELECT to_char(date_trunc('month', current_date) - (m.month || ' months')::interval, 'YYYY-MM-01') AS month_date, \n"
						+ "COALESCE(vc.visit_count, 0) AS visit_count FROM months m \n"
						+ "LEFT JOIN visit_counts vc ON date_trunc('month', current_date) - (m.month || ' months')::interval = vc.month_year\n"
						+ "ORDER BY date_trunc('month', current_date) - (m.month || ' months')::interval;";
			} else if (userInput.equals("all")) {
				sql = "WITH year_counts AS (\n"
						+ "SELECT date_trunc('year', v.created) AS year_start, COUNT(*) AS counts FROM adempiere.tc_culturelabel v\n"
						+ "JOIN adempiere.ad_user u ON u.ad_user_id = v.createdby WHERE v.ad_client_id = ? AND v.isdiscarded = 'N' AND u.name = ?\n"
						+ "GROUP BY date_trunc('year', v.created)),\n"
						+ "year_range AS (SELECT date_trunc('year', CURRENT_DATE) AS year_start\n" + "UNION ALL\n"
						+ "SELECT generate_series((SELECT MIN(year_start) FROM year_counts),date_trunc('year', CURRENT_DATE),interval '1 year') AS year_start\n"
						+ "),\n" + "all_years AS (SELECT DISTINCT year_start FROM year_range)\n"
						+ "SELECT to_char(a.year_start, 'YYYY-01-01') AS year_date, COALESCE(y.counts, 0) AS counts FROM all_years a\n"
						+ "LEFT JOIN year_counts y ON a.year_start = y.year_start ORDER BY a.year_start;\n" + "";
			}
			if (sql == null) {
				getLTRoleReportCultureLabelCountResponse.setError("No SQL");
				getLTRoleReportCultureLabelCountResponse.setIsError(true);
				return getLTRoleReportCultureLabelCountResponseDocument;
			}

			pstm = DB.prepareStatement(sql, null);
			pstm.setInt(1, clientId);
			pstm.setString(2, user);
			rs = pstm.executeQuery();
			while (rs.next()) {
				GetCultureLabelCount data = getLTRoleReportCultureLabelCountResponse.addNewGetCultureLabelCount();
				if (userInput.equals("day")) {
					String date = rs.getString("date");
					int count = rs.getInt("visit_count");
					data.setLabelName(date);
					data.setCount(count);
				} else if (userInput.equals("week")) {
					String date = rs.getString("dates");
					int count = rs.getInt("visit_count");
					data.setLabelName(date);
					data.setCount(count);
				} else if (userInput.equals("month")) {
					String week_start = rs.getString("week_start");
					int count = rs.getInt("visit_count");
					data.setLabelName(week_start);
					data.setCount(count);
				} else if (userInput.equals("year")) {
					String month_date = rs.getString("month_date");
					int count = rs.getInt("visit_count");
					data.setLabelName(month_date);
					data.setCount(count);
				} else if (userInput.equals("all")) {
					String year_date = rs.getString("year_date");
					int count = rs.getInt("counts");
					data.setLabelName(year_date);
					data.setCount(count);
				}
			}
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			getLTRoleReportCultureLabelCountResponse.setError(INTERNAL_SERVER_ERROR);
			getLTRoleReportCultureLabelCountResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return getLTRoleReportCultureLabelCountResponseDocument;
	}

	@Override
	public GetMTRoleReportMediaLabelCountResponseDocument getMTRoleReportMediaLabelCount(
			GetMTRoleReportMediaLabelCountRequestDocument req) {
		GetMTRoleReportMediaLabelCountResponseDocument getMTRoleReportMediaLabelCountResponseDocument = GetMTRoleReportMediaLabelCountResponseDocument.Factory
				.newInstance();
		GetMTRoleReportMediaLabelCountResponse getMTRoleReportMediaLabelCountResponse = getMTRoleReportMediaLabelCountResponseDocument
				.addNewGetMTRoleReportMediaLabelCountResponse();
		GetMTRoleReportMediaLabelCountRequest loginRequest = req.getGetMTRoleReportMediaLabelCountRequest();
		String userInput = loginRequest.getUserInput();
		String mediaType = loginRequest.getMediaType();
		PreparedStatement pstm = null;
		ResultSet rs = null;
		Trx trx = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
			String user = safeTrim(new MUser(ctx, Env.getAD_User_ID(ctx), null).getName());
			int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			
			if (containsMaliciousPattern(userInput)) {
				getMTRoleReportMediaLabelCountResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				getMTRoleReportMediaLabelCountResponse.setIsError(true);
			    return getMTRoleReportMediaLabelCountResponseDocument;
			}
			
			if (containsMaliciousPattern(mediaType)) {
				getMTRoleReportMediaLabelCountResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				getMTRoleReportMediaLabelCountResponse.setIsError(true);
			    return getMTRoleReportMediaLabelCountResponseDocument;
			}
			
			List<PO> poList = new Query(ctx, TABLE_MEDIATYPE, "ad_client_id=?", trxName)
			        .setParameters(clientId)
			        .list();

			StringBuilder mediaTypeListQuoted = new StringBuilder();

			for (int i = 0; i < poList.size(); i++) {
			    PO record = poList.get(i);
			    X_TC_MediaType tt = new X_TC_MediaType(ctx, record.get_ID(), trxName);
			    String name = tt.getName();

			    if (i > 0) mediaTypeListQuoted.append(",");
			    mediaTypeListQuoted.append("'").append(name).append("'");
			}

			String allMediaTypes = mediaTypeListQuoted.toString();

			if (mediaType == null || mediaType.trim().isEmpty() || mediaType.equalsIgnoreCase("all")) {
				mediaType = allMediaTypes;
			}else {
				mediaType = "'" + mediaType + "'";
		    }
			
			String sql = null;

			if (userInput.equals("day")) {
				sql = "SELECT current_date AS date,COALESCE(COUNT(v.*), 0) AS counts\n"
						+ "FROM (SELECT to_char(current_date, 'FMDay') AS day_name) AS day_info\n"
						+ "CROSS JOIN adempiere.tc_mediatype mt\n"
						+ "LEFT JOIN adempiere.tc_medialabelqr v ON v.created::date = current_date\n"
						+ "AND v.ad_client_id = ? AND v.isdiscarded = 'N' AND v.tc_mediatype_id = mt.tc_mediatype_id\n"
						+ "AND v.createdby IN (SELECT ad_user_id FROM adempiere.ad_user WHERE name = ?)\n"
						+ "WHERE mt.name  IN (" + mediaType + ")   GROUP BY day_info.day_name;";
			} else if (userInput.equals("week")) {
				sql = "WITH days AS (SELECT generate_series(0, 6) AS day_of_week),\n"
						+ "visit_counts AS (SELECT date_trunc('day', v.created) AS visit_day,to_char(v.created, 'FMDay') AS day_name,\n"
						+ "EXTRACT(dow FROM v.created) AS day_of_week,COUNT(*) AS visit_count FROM adempiere.tc_medialabelqr v\n"
						+ "JOIN adempiere.ad_user u ON u.ad_user_id = v.createdby JOIN adempiere.tc_mediatype mt ON mt.tc_mediatype_id = v.tc_mediatype_id\n"
						+ "WHERE v.ad_client_id = ? AND v.isdiscarded = 'N' AND u.name = ? AND mt.name IN (" + mediaType + ")                 \n"
						+ "AND v.created::date >= current_date - interval '6 days' AND v.created::date <= current_date\n"
						+ "GROUP BY date_trunc('day', v.created),to_char(v.created, 'FMDay'),EXTRACT(dow FROM v.created))\n"
						+ "SELECT (current_date - interval '6 days' + d.day_of_week * interval '1 day')::date AS dates,\n"
						+ "COALESCE(vc.day_name, to_char(current_date - interval '6 days' + d.day_of_week * interval '1 day', 'FMDay')) AS day_name,\n"
						+ "COALESCE(vc.visit_count, 0) AS counts FROM days d\n"
						+ "LEFT JOIN visit_counts vc ON date_trunc('day', current_date - interval '6 days' + d.day_of_week * interval '1 day') = vc.visit_day\n"
						+ "ORDER BY dates;";
			} else if (userInput.equals("month")) {
				sql = "WITH weeks AS (SELECT generate_series(0, 4) AS week_number),\n"
						+ "counts AS (SELECT date_trunc('week', v.created) AS week_start,\n"
						+ "to_char(date_trunc('week', v.created), 'YYYY-MM-DD') AS week_start_str,COUNT(*) AS counts\n"
						+ "FROM adempiere.tc_medialabelqr v JOIN adempiere.ad_user u ON u.ad_user_id = v.createdby\n"
						+ "JOIN adempiere.TC_MediaType mt ON mt.TC_MediaType_id = v.TC_MediaType_id\n"
						+ "WHERE v.ad_client_id = ? AND v.isdiscarded = 'N' AND u.name = ? AND mt.name IN (" + mediaType + ")   \n"
						+ "AND v.created::date >= (current_date - interval '29 days') AND v.created::date <= current_date GROUP BY date_trunc('week', v.created)),\n"
						+ "date_range AS (SELECT (current_date - interval '29 days')::date + generate_series(0, 29) AS day)\n"
						+ "SELECT to_char(date_trunc('week', day), 'YYYY-MM-DD') AS week_start,COALESCE(vc.counts, 0) AS counts\n"
						+ "FROM date_range LEFT JOIN counts vc ON date_trunc('week', day) = vc.week_start\n"
						+ "GROUP BY date_trunc('week', day), vc.counts ORDER BY week_start;";
			} else if (userInput.equals("year")) {
				sql = "WITH months AS (SELECT generate_series(0, 11) AS month),\n"
						+ "visit_counts AS (SELECT date_trunc('month', v.created) AS month_year,to_char(v.created, 'FMMonth') AS month_name,COUNT(*) AS visit_count\n"
						+ "FROM adempiere.tc_medialabelqr v JOIN adempiere.ad_user u ON u.ad_user_id = v.createdby\n"
						+ "JOIN adempiere.tc_mediatype mt ON mt.tc_mediatype_id = v.tc_mediatype_id\n"
						+ "WHERE v.ad_client_id = ? AND v.isdiscarded = 'N' AND u.name = ? AND mt.name IN (" + mediaType + ")   \n"
						+ "AND v.created::date BETWEEN (current_date - interval '364 days') AND current_date\n"
						+ "GROUP BY date_trunc('month', v.created),to_char(v.created, 'FMMonth'))\n"
						+ "SELECT to_char(date_trunc('month', current_date) - (m.month || ' months')::interval, 'YYYY-MM-01') AS month_date,\n"
						+ "COALESCE(vc.visit_count, 0) AS counts FROM months m\n"
						+ "LEFT JOIN visit_counts vc ON date_trunc('month', current_date) - (m.month || ' months')::interval = vc.month_year\n"
						+ "ORDER BY date_trunc('month', current_date) - (m.month || ' months')::interval;";
			} else if (userInput.equals("all")) {
				sql = "WITH year_counts AS (SELECT date_trunc('year', v.created) AS year_start,COUNT(*) AS counts\n"
						+ "FROM adempiere.tc_medialabelqr v JOIN adempiere.ad_user u ON u.ad_user_id = v.createdby\n"
						+ "JOIN adempiere.tc_mediatype mt ON mt.tc_mediatype_id = v.tc_mediatype_id\n"
						+ "WHERE v.ad_client_id = ? AND v.isdiscarded = 'N' AND u.name = ? AND mt.name IN (" + mediaType + ")   \n"
						+ "GROUP BY date_trunc('year', v.created)),year_range AS (SELECT date_trunc('year', current_date) AS year_start\n"
						+ "UNION ALL\n"
						+ "SELECT generate_series((SELECT MIN(year_start) FROM year_counts),date_trunc('year', current_date),\n"
						+ "interval '1 year') AS year_start),\n"
						+ "all_years AS (SELECT DISTINCT year_start FROM year_range)\n"
						+ "SELECT to_char(a.year_start, 'YYYY-01-01') AS year_date,COALESCE(y.counts, 0) AS counts\n"
						+ "FROM all_years a LEFT JOIN year_counts y ON a.year_start = y.year_start ORDER BY a.year_start;";
			}
			if (sql == null) {
				getMTRoleReportMediaLabelCountResponse.setError("No SQL");
				getMTRoleReportMediaLabelCountResponse.setIsError(true);
				return getMTRoleReportMediaLabelCountResponseDocument;
			}

			pstm = DB.prepareStatement(sql, null);
			pstm.setInt(1, clientId);
			pstm.setString(2, user);
			rs = pstm.executeQuery();
			while (rs.next()) {
				GetMediaLabelCount data = getMTRoleReportMediaLabelCountResponse.addNewGetMediaLabelCount();
				if (userInput.equals("day")) {
					String date = rs.getString("date");
					int count = rs.getInt("counts");
					data.setLabelName(date);
					data.setCount(count);
				} else if (userInput.equals("week")) {
					String dates = rs.getString("dates");
					int count = rs.getInt("counts");
					data.setLabelName(dates);
					data.setCount(count);
				} else if (userInput.equals("month")) {
					String week_start = rs.getString("week_start");
					int count = rs.getInt("counts");
					data.setLabelName(week_start);
					data.setCount(count);
				} else if (userInput.equals("year")) {
					String month_date = rs.getString("month_date");
					int count = rs.getInt("counts");
					data.setLabelName(month_date);
					data.setCount(count);
				} else if (userInput.equals("all")) {
					String year_date = rs.getString("year_date");
					int count = rs.getInt("counts");
					data.setLabelName(year_date);
					data.setCount(count);
				}
			}
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			getMTRoleReportMediaLabelCountResponse.setError(INTERNAL_SERVER_ERROR);
			getMTRoleReportMediaLabelCountResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return getMTRoleReportMediaLabelCountResponseDocument;
	}

	@Override
	public GetMTRoleReportDiscardLabelCountResponseDocument getMTRoleReportDiscardLabelCount(
			GetMTRoleReportDiscardLabelCountRequestDocument req) {
		GetMTRoleReportDiscardLabelCountResponseDocument getMTRoleReportDiscardLabelCountResponseDocument = GetMTRoleReportDiscardLabelCountResponseDocument.Factory
				.newInstance();
		GetMTRoleReportDiscardLabelCountResponse getMTRoleReportDiscardLabelCountResponse = getMTRoleReportDiscardLabelCountResponseDocument
				.addNewGetMTRoleReportDiscardLabelCountResponse();
		GetMTRoleReportDiscardLabelCountRequest loginRequest = req.getGetMTRoleReportDiscardLabelCountRequest();
		String userInput = loginRequest.getUserInput();
		String mediaType = loginRequest.getMediaType();
		PreparedStatement pstm = null;
		ResultSet rs = null;
		Trx trx = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
			String user = safeTrim(new MUser(ctx, Env.getAD_User_ID(ctx), null).getName());
			int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			
			if (containsMaliciousPattern(userInput)) {
				getMTRoleReportDiscardLabelCountResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				getMTRoleReportDiscardLabelCountResponse.setIsError(true);
			    return getMTRoleReportDiscardLabelCountResponseDocument;
			}
			
			if (containsMaliciousPattern(mediaType)) {
				getMTRoleReportDiscardLabelCountResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				getMTRoleReportDiscardLabelCountResponse.setIsError(true);
			    return getMTRoleReportDiscardLabelCountResponseDocument;
			}
			
			List<PO> poList = new Query(ctx, TABLE_MEDIATYPE, "ad_client_id=?", trxName)
			        .setParameters(clientId)
			        .list();

			StringBuilder mediaTypeListQuoted = new StringBuilder();

			for (int i = 0; i < poList.size(); i++) {
			    PO record = poList.get(i);
			    X_TC_MediaType tt = new X_TC_MediaType(ctx, record.get_ID(), trxName);
			    String name = tt.getName();

			    if (i > 0) mediaTypeListQuoted.append(",");
			    mediaTypeListQuoted.append("'").append(name).append("'");
			}

			String allMediaTypes = mediaTypeListQuoted.toString();

			if (mediaType == null || mediaType.trim().isEmpty() || mediaType.equalsIgnoreCase("all")) {
				mediaType = allMediaTypes;
			}else {
				mediaType = "'" + mediaType + "'";
		    }
			
			String sql = null;

			if (userInput.equals("day")) {
				sql = "SELECT current_date AS date,COALESCE(COUNT(v.*), 0) AS counts\n"
						+ "FROM (SELECT to_char(current_date, 'FMDay') AS day_name) AS day_info\n"
						+ "CROSS JOIN adempiere.tc_mediatype mt\n"
						+ "LEFT JOIN adempiere.tc_medialabelqr v ON v.created::date = current_date\n"
						+ "AND v.ad_client_id = ? AND v.isdiscarded = 'Y' AND v.tc_mediatype_id = mt.tc_mediatype_id\n"
						+ "AND v.createdby IN (SELECT ad_user_id FROM adempiere.ad_user WHERE name = ?)\n"
						+ "WHERE mt.name  IN (" + mediaType + ")  GROUP BY day_info.day_name;";
			} else if (userInput.equals("week")) {
				sql = "WITH days AS (SELECT generate_series(0, 6) AS day_of_week),\n"
						+ "visit_counts AS (SELECT date_trunc('day', v.created) AS visit_day,to_char(v.created, 'FMDay') AS day_name,\n"
						+ "EXTRACT(dow FROM v.created) AS day_of_week,COUNT(*) AS visit_count FROM adempiere.tc_medialabelqr v\n"
						+ "JOIN adempiere.ad_user u ON u.ad_user_id = v.createdby JOIN adempiere.tc_mediatype mt ON mt.tc_mediatype_id = v.tc_mediatype_id\n"
						+ "WHERE v.ad_client_id = ? AND v.isdiscarded = 'Y' AND u.name = ? AND mt.name  IN (" + mediaType + ")  \n"
						+ "AND v.created::date >= current_date - interval '6 days' AND v.created::date <= current_date\n"
						+ "GROUP BY date_trunc('day', v.created),to_char(v.created, 'FMDay'),EXTRACT(dow FROM v.created))\n"
						+ "SELECT (current_date - interval '6 days' + d.day_of_week * interval '1 day')::date AS dates,\n"
						+ "COALESCE(vc.day_name, to_char(current_date - interval '6 days' + d.day_of_week * interval '1 day', 'FMDay')) AS day_name,\n"
						+ "COALESCE(vc.visit_count, 0) AS counts FROM days d\n"
						+ "LEFT JOIN visit_counts vc ON date_trunc('day', current_date - interval '6 days' + d.day_of_week * interval '1 day') = vc.visit_day\n"
						+ "ORDER BY dates;";
			} else if (userInput.equals("month")) {
				sql = "WITH weeks AS (SELECT generate_series(0, 4) AS week_number),\n"
						+ "counts AS (SELECT date_trunc('week', v.created) AS week_start,\n"
						+ "to_char(date_trunc('week', v.created), 'YYYY-MM-DD') AS week_start_str,COUNT(*) AS counts\n"
						+ "FROM adempiere.tc_medialabelqr v JOIN adempiere.ad_user u ON u.ad_user_id = v.createdby\n"
						+ "JOIN adempiere.TC_MediaType mt ON mt.TC_MediaType_id = v.TC_MediaType_id\n"
						+ "WHERE v.ad_client_id = ? AND v.isdiscarded = 'Y' AND u.name = ? AND mt.name  IN (" + mediaType + ") \n"
						+ "AND v.created::date >= (current_date - interval '29 days') AND v.created::date <= current_date GROUP BY date_trunc('week', v.created)),\n"
						+ "date_range AS (SELECT (current_date - interval '29 days')::date + generate_series(0, 29) AS day)\n"
						+ "SELECT to_char(date_trunc('week', day), 'YYYY-MM-DD') AS week_start,COALESCE(vc.counts, 0) AS counts\n"
						+ "FROM date_range LEFT JOIN counts vc ON date_trunc('week', day) = vc.week_start\n"
						+ "GROUP BY date_trunc('week', day), vc.counts ORDER BY week_start;";
			} else if (userInput.equals("year")) {
				sql = "WITH months AS (SELECT generate_series(0, 11) AS month),\n"
						+ "visit_counts AS (SELECT date_trunc('month', v.created) AS month_year,to_char(v.created, 'FMMonth') AS month_name,COUNT(*) AS visit_count\n"
						+ "FROM adempiere.tc_medialabelqr v JOIN adempiere.ad_user u ON u.ad_user_id = v.createdby\n"
						+ "JOIN adempiere.tc_mediatype mt ON mt.tc_mediatype_id = v.tc_mediatype_id\n"
						+ "WHERE v.ad_client_id = ? AND v.isdiscarded = 'Y' AND u.name = ? AND mt.name  IN (" + mediaType + ")  \n"
						+ "AND v.created::date BETWEEN (current_date - interval '364 days') AND current_date\n"
						+ "GROUP BY date_trunc('month', v.created),to_char(v.created, 'FMMonth'))\n"
						+ "SELECT to_char(date_trunc('month', current_date) - (m.month || ' months')::interval, 'YYYY-MM-01') AS month_date,\n"
						+ "COALESCE(vc.visit_count, 0) AS counts FROM months m\n"
						+ "LEFT JOIN visit_counts vc ON date_trunc('month', current_date) - (m.month || ' months')::interval = vc.month_year\n"
						+ "ORDER BY date_trunc('month', current_date) - (m.month || ' months')::interval;";
			} else if (userInput.equals("all")) {
				sql = "WITH year_counts AS (SELECT date_trunc('year', v.created) AS year_start,COUNT(*) AS counts\n"
						+ "FROM adempiere.tc_medialabelqr v JOIN adempiere.ad_user u ON u.ad_user_id = v.createdby\n"
						+ "JOIN adempiere.tc_mediatype mt ON mt.tc_mediatype_id = v.tc_mediatype_id\n"
						+ "WHERE v.ad_client_id = ? AND v.isdiscarded = 'Y' AND u.name = ? AND mt.name IN (" + mediaType + ") \n"
						+ "GROUP BY date_trunc('year', v.created)),year_range AS (SELECT date_trunc('year', current_date) AS year_start\n"
						+ "UNION ALL\n"
						+ "SELECT generate_series((SELECT MIN(year_start) FROM year_counts),date_trunc('year', current_date),\n"
						+ "interval '1 year') AS year_start),\n"
						+ "all_years AS (SELECT DISTINCT year_start FROM year_range)\n"
						+ "SELECT to_char(a.year_start, 'YYYY-01-01') AS year_date,COALESCE(y.counts, 0) AS counts\n"
						+ "FROM all_years a LEFT JOIN year_counts y ON a.year_start = y.year_start ORDER BY a.year_start;";
			}
			if (sql == null) {
				getMTRoleReportDiscardLabelCountResponse.setError("No SQL");
				getMTRoleReportDiscardLabelCountResponse.setIsError(true);
				return getMTRoleReportDiscardLabelCountResponseDocument;
			}

			pstm = DB.prepareStatement(sql, null);
			pstm.setInt(1, clientId);
			pstm.setString(2, user);
			rs = pstm.executeQuery();
			while (rs.next()) {
				GetDiscardLabelCount data = getMTRoleReportDiscardLabelCountResponse.addNewGetDiscardLabelCount();
				if (userInput.equals("day")) {
					String date = rs.getString("date");
					int count = rs.getInt("counts");
					data.setLabelName(date);
					data.setCount(count);
				} else if (userInput.equals("week")) {
					String dates = rs.getString("dates");
					int count = rs.getInt("counts");
					data.setLabelName(dates);
					data.setCount(count);
				} else if (userInput.equals("month")) {
					String week_start = rs.getString("week_start");
					int count = rs.getInt("counts");
					data.setLabelName(week_start);
					data.setCount(count);
				} else if (userInput.equals("year")) {
					String month_date = rs.getString("month_date");
					int count = rs.getInt("counts");
					data.setLabelName(month_date);
					data.setCount(count);
				} else if (userInput.equals("all")) {
					String year_date = rs.getString("year_date");
					int count = rs.getInt("counts");
					data.setLabelName(year_date);
					data.setCount(count);
				}
			}
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			getMTRoleReportDiscardLabelCountResponse.setError(INTERNAL_SERVER_ERROR);
			getMTRoleReportDiscardLabelCountResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return getMTRoleReportDiscardLabelCountResponseDocument;
	}


	@Override
	public GetQARoleReportDiscardLabelCountResponseDocument getQARoleReportDiscardLabelCount(
			GetQARoleReportDiscardLabelCountRequestDocument req) {
		GetQARoleReportDiscardLabelCountResponseDocument getQARoleReportDiscardLabelCountResponseDocument = GetQARoleReportDiscardLabelCountResponseDocument.Factory
				.newInstance();
		GetQARoleReportDiscardLabelCountResponse getQARoleReportDiscardLabelCountResponse = getQARoleReportDiscardLabelCountResponseDocument
				.addNewGetQARoleReportDiscardLabelCountResponse();
		GetQARoleReportDiscardLabelCountRequest loginRequest = req.getGetQARoleReportDiscardLabelCountRequest();
		String userInput = loginRequest.getUserInput();
		PreparedStatement pstm = null;
		ResultSet rs = null;
		Trx trx = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
			String user = safeTrim(new MUser(ctx, Env.getAD_User_ID(ctx), null).getName());
		int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			
			if (containsMaliciousPattern(userInput)) {
				getQARoleReportDiscardLabelCountResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				getQARoleReportDiscardLabelCountResponse.setIsError(true);
			    return getQARoleReportDiscardLabelCountResponseDocument;
			}
			
			String sql = null;

			if (userInput.equals("day")) {
				sql = "SELECT day_info.day_name AS day_name,current_date AS date,COALESCE(COUNT(v.*), 0) AS counts\n"
						+ "FROM (SELECT to_char(current_date, 'FMDay') AS day_name) AS day_info\n"
						+ "LEFT JOIN adempiere.tc_culturelabel v ON v.updated::date = current_date AND v.ad_client_id = ? AND v.isdiscarded = 'Y'\n"
						+ "AND v.updatedby IN (SELECT ad_user_id FROM adempiere.ad_user WHERE name = ?)GROUP BY day_info.day_name;";
			} else if (userInput.equals("week")) {
				sql = "WITH days AS (SELECT generate_series(0, 6) AS day_of_week),\n" + "visit_counts AS (\n"
						+ "SELECT date_trunc('day', v.updated) AS visit_day,to_char(v.updated, 'FMDay') AS day_name,EXTRACT(dow FROM v.updated) AS day_of_week,COUNT(*) AS visit_count\n"
						+ "FROM adempiere.tc_culturelabel v JOIN adempiere.ad_user u ON u.ad_user_id = v.updatedby \n"
						+ "WHERE v.ad_client_id = ? AND v.isdiscarded = 'Y' AND u.name = ? AND v.updated::date >= current_date - interval '6 days' \n"
						+ "AND v.updated::date <= current_date GROUP BY date_trunc('day', v.updated),to_char(v.updated, 'FMDay'),EXTRACT(dow FROM v.updated))\n"
						+ "SELECT (current_date - interval '6 days' + d.day_of_week * interval '1 day')::date AS dates,\n"
						+ "COALESCE(vc.day_name, to_char(current_date - interval '6 days' + d.day_of_week * interval '1 day', 'FMDay')) AS day_name,COALESCE(vc.visit_count, 0) AS counts FROM days d \n"
						+ "LEFT JOIN visit_counts vc ON date_trunc('day', current_date - interval '6 days' + d.day_of_week * interval '1 day') = vc.visit_day ORDER BY dates;\n"
						+ "";
			} else if (userInput.equals("month")) {
				sql = "WITH weeks AS (SELECT generate_series(0, 4) AS week_number),\n" + "visit_counts AS (\n"
						+ "SELECT date_trunc('week', v.updated) AS week_start,to_char(date_trunc('week', v.updated), 'YYYY-MM-DD') AS week_start_str,\n"
						+ "COUNT(*) AS visit_count FROM adempiere.tc_culturelabel v JOIN adempiere.ad_user u ON u.ad_user_id = v.updatedby\n"
						+ "WHERE v.ad_client_id = ? AND v.isdiscarded = 'Y' AND u.name = ?  AND v.updated::date >= (current_date - interval '29 days') AND v.updated::date <= current_date GROUP BY date_trunc('week', v.updated)),\n"
						+ "date_range AS (SELECT (current_date - interval '29 days')::date + generate_series(0, 29) AS day)\n"
						+ "SELECT to_char(date_trunc('week', day), 'YYYY-MM-DD') AS week_start,COALESCE(vc.visit_count, 0) AS counts\n"
						+ "FROM date_range LEFT JOIN visit_counts vc ON date_trunc('week', day) = vc.week_start\n"
						+ "GROUP BY date_trunc('week', day),vc.visit_count ORDER BY week_start;";
			} else if (userInput.equals("year")) {
				sql = "WITH months AS (SELECT generate_series(0, 11) AS month),\n" + "visit_counts AS (\n"
						+ "SELECT date_trunc('month', v.updated) AS month_year,to_char(v.updated, 'FMMonth') AS month_name,COUNT(*) AS visit_count\n"
						+ "FROM adempiere.tc_culturelabel v JOIN adempiere.ad_user u ON u.ad_user_id = v.updatedby\n"
						+ "WHERE v.ad_client_id = ? AND v.isdiscarded = 'Y' AND u.name = ? AND v.updated::date >= (current_date - interval '364 days') AND v.updated::date <= current_date\n"
						+ "GROUP BY date_trunc('month', v.updated), to_char(v.updated, 'FMMonth'))\n"
						+ "SELECT to_char(date_trunc('month', current_date) - (m.month || ' months')::interval, 'YYYY-MM-01') AS month_date, \n"
						+ "COALESCE(vc.visit_count, 0) AS counts FROM months m \n"
						+ "LEFT JOIN visit_counts vc ON date_trunc('month', current_date) - (m.month || ' months')::interval = vc.month_year\n"
						+ "ORDER BY date_trunc('month', current_date) - (m.month || ' months')::interval;";
			} else if (userInput.equals("all")) {
				sql = "WITH year_counts AS (\n"
						+ "SELECT date_trunc('year', v.updated) AS year_start, COUNT(*) AS counts FROM adempiere.tc_culturelabel v\n"
						+ "JOIN adempiere.ad_user u ON u.ad_user_id = v.updatedby WHERE v.ad_client_id = ? AND v.isdiscarded = 'Y' AND u.name = ?\n"
						+ "GROUP BY date_trunc('year', v.updated)),\n"
						+ "year_range AS (SELECT date_trunc('year', CURRENT_DATE) AS year_start\n" + "UNION ALL\n"
						+ "SELECT generate_series((SELECT MIN(year_start) FROM year_counts),date_trunc('year', CURRENT_DATE),interval '1 year') AS year_start\n"
						+ "),\n" + "all_years AS (SELECT DISTINCT year_start FROM year_range)\n"
						+ "SELECT to_char(a.year_start, 'YYYY-01-01') AS year_date, COALESCE(y.counts, 0) AS counts FROM all_years a\n"
						+ "LEFT JOIN year_counts y ON a.year_start = y.year_start ORDER BY a.year_start;\n" + "";
			}
			if (sql == null) {
				getQARoleReportDiscardLabelCountResponse.setError("No SQL");
				getQARoleReportDiscardLabelCountResponse.setIsError(true);
				return getQARoleReportDiscardLabelCountResponseDocument;
			}

			pstm = DB.prepareStatement(sql, null);
			pstm.setInt(1, clientId);
			pstm.setString(2, user);
			rs = pstm.executeQuery();
			while (rs.next()) {
				GetQADiscardLabelCount data = getQARoleReportDiscardLabelCountResponse.addNewGetQADiscardLabelCount();
				if (userInput.equals("day")) {
					String date = rs.getString("date");
					int count = rs.getInt("counts");
					data.setLabelName(date);
					data.setCount(count);
				} else if (userInput.equals("week")) {
					String dates = rs.getString("dates");
					int count = rs.getInt("counts");
					data.setLabelName(dates);
					data.setCount(count);
				} else if (userInput.equals("month")) {
					String week_start = rs.getString("week_start");
					int count = rs.getInt("counts");
					data.setLabelName(week_start);
					data.setCount(count);
				} else if (userInput.equals("year")) {
					String month_date = rs.getString("month_date");
					int count = rs.getInt("counts");
					data.setLabelName(month_date);
					data.setCount(count);
				} else if (userInput.equals("all")) {
					String year_date = rs.getString("year_date");
					int count = rs.getInt("counts");
					data.setLabelName(year_date);
					data.setCount(count);
				}
			}
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			getQARoleReportDiscardLabelCountResponse.setError(INTERNAL_SERVER_ERROR);
			getQARoleReportDiscardLabelCountResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return getQARoleReportDiscardLabelCountResponseDocument;
	}

	@Override
	public GetTCTRoleToSubCultureReportResponseDocument getTCTRoleToSubCultureReport(
			GetTCTRoleToSubCultureReportRequestDocument req) {
		GetTCTRoleToSubCultureReportResponseDocument getTCTRoleToSubCultureReportResponseDocument = GetTCTRoleToSubCultureReportResponseDocument.Factory
				.newInstance();
		GetTCTRoleToSubCultureReportResponse getTCTRoleToSubCultureReportResponse = getTCTRoleToSubCultureReportResponseDocument
				.addNewGetTCTRoleToSubCultureReportResponse();
		GetTCTRoleToSubCultureReportRequest loginRequest = req.getGetTCTRoleToSubCultureReportRequest();
		String userInput = loginRequest.getUserInput();
		Trx trx = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
			String user = safeTrim(new MUser(ctx, Env.getAD_User_ID(ctx), null).getName());
		int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			
			if (containsMaliciousPattern(userInput)) {
				getTCTRoleToSubCultureReportResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				getTCTRoleToSubCultureReportResponse.setIsError(true);
			    return getTCTRoleToSubCultureReportResponseDocument;
			}
			
			String sql = null;

			if (userInput.equals("day")) {
				sql = "WITH subquery AS (\n"
						+ "SELECT cl.tc_culturelabel_id,DATE(cl.created + (cs.period::int * INTERVAL '1 day')) AS expiryDate,cs.period::int AS period,\n"
						+ "(SELECT COUNT(*) FROM adempiere.tc_culturelabel cll WHERE cll.parentuuid = cl.c_uuid LIMIT 1) AS subquery_column,cl.createdby\n"
						+ "FROM adempiere.tc_culturelabel cl JOIN adempiere.tc_out o ON o.tc_out_id = cl.tc_out_id\n"
						+ "JOIN adempiere.m_locator l ON l.m_locator_id = o.m_locator_id \n"
						+ "JOIN adempiere.tc_culturestage cs ON cs.tc_culturestage_id = cl.tc_culturestage_id \n"
						+ "WHERE cl.ad_client_id = ? AND cl.isdiscarded = 'N' AND cl.c_uuid IS NOT NULL AND cl.parentuuid IS NOT NULL),\n"
						+ "day_info AS (SELECT to_char(current_date, 'FMDay') AS day_name,current_date AS date)\n"
						+ "SELECT di.day_name AS day_name,di.date AS date,COUNT(v.tc_culturelabel_id) AS counts\n"
						+ "FROM day_info di LEFT JOIN (SELECT s.tc_culturelabel_id\n"
						+ "FROM subquery s WHERE s.expiryDate = current_date AND s.subquery_column = 0 AND s.createdby IN (\n"
						+ "SELECT ad_user_id FROM adempiere.ad_user WHERE name = ?)) v ON TRUE\n"
						+ "GROUP BY di.day_name, di.date;";
			} else if (userInput.equals("week")) {
				sql = "WITH days AS (SELECT generate_series(0, 6) AS day_of_week),\n" + "visit_counts AS (\n"
						+ "SELECT date_trunc('day', v.created) AS visit_day,to_char(v.created, 'FMDay') AS day_name,EXTRACT(dow FROM v.created) AS day_of_week,COUNT(*) AS visit_count,\n"
						+ "COUNT(cll.c_uuid) AS subquery_column,COUNT(*) - COUNT(cll.c_uuid) AS actualCount,DATE(v.created + (cs.period::int * INTERVAL '1 day')) AS expiryDate\n"
						+ "FROM adempiere.tc_culturelabel v JOIN adempiere.ad_user u ON u.ad_user_id = v.createdby \n"
						+ "JOIN adempiere.tc_culturestage cs ON cs.tc_culturestage_id = v.tc_culturestage_id LEFT JOIN adempiere.tc_culturelabel cll ON cll.parentuuid = v.c_uuid \n"
						+ "WHERE v.ad_client_id = ? AND v.isdiscarded = 'N' AND u.name = ? AND DATE(v.created + (cs.period::int * INTERVAL '1 day')) >= current_date\n"
						+ "AND DATE(v.created + (cs.period::int * INTERVAL '1 day')) <= current_date + INTERVAL '6 days' AND cs.period IS NOT NULL\n"
						+ "GROUP BY date_trunc('day', v.created),to_char(v.created, 'FMDay'),EXTRACT(dow FROM v.created),DATE(v.created + (cs.period::int * INTERVAL '1 day'))),\n"
						+ "dynamic_period_counts AS (\n"
						+ "SELECT (current_date + d.day_of_week * INTERVAL '1 day')::date AS dates,vc.day_name,vc.day_of_week,vc.visit_count,vc.actualCount\n"
						+ "FROM days d LEFT JOIN visit_counts vc ON DATE_TRUNC('day', current_date + d.day_of_week * INTERVAL '1 day') = vc.expiryDate)\n"
						+ "SELECT dates,to_char(dates, 'FMDay') AS day_name,\n"
						+ "COALESCE(visit_count, 0) AS visit_count,COALESCE(actualCount, 0) AS counts FROM dynamic_period_counts ORDER BY dates;";
			} else if (userInput.equals("month")) {
				sql = "WITH weeks AS (\n" + "    SELECT generate_series(0, 4) AS week_of_month),\n"
						+ "visit_counts AS (SELECT\n"
						+ "        (MIN(v.created::date + (cs.period::int * INTERVAL '1 day')) - \n"
						+ "            CASE EXTRACT(DOW FROM MIN(v.created::date + (cs.period::int * INTERVAL '1 day')))\n"
						+ "                WHEN 0 THEN interval '6 days'\n"
						+ "                WHEN 1 THEN interval '0 days'\n"
						+ "                WHEN 2 THEN interval '1 day'\n"
						+ "                WHEN 3 THEN interval '2 days'\n"
						+ "                WHEN 4 THEN interval '3 days'\n"
						+ "                WHEN 5 THEN interval '4 days'\n"
						+ "                WHEN 6 THEN interval '5 days'\n" + "            END\n"
						+ "        ) AS adjusted_date,COUNT(*) AS visit_count,COUNT(cll.c_uuid) AS subquery_column,COUNT(*) - COUNT(cll.c_uuid) AS actualCount\n"
						+ "FROM adempiere.tc_culturelabel v JOIN adempiere.tc_culturestage cs ON cs.tc_culturestage_id = v.tc_culturestage_id\n"
						+ "JOIN adempiere.ad_user u ON u.ad_user_id = v.createdby LEFT JOIN adempiere.tc_culturelabel cll ON cll.parentuuid = v.c_uuid\n"
						+ "WHERE v.ad_client_id = ? AND v.isdiscarded = 'N' AND u.name = ? \n"
						+ "AND v.created::date >= (current_date - interval '21 days') AND v.created::date <= (current_date + interval '29 days')GROUP BY date_trunc('week', v.created))\n"
						+ "SELECT (current_date + w.week_of_month * interval '1 week')::date AS week_start,COALESCE(vc.visit_count, 0) AS visitcounts,COALESCE(vc.actualCount,0) As counts FROM weeks w \n"
						+ "LEFT JOIN visit_counts vc ON date_trunc('week', current_date + w.week_of_month * interval '1 week') = vc.adjusted_date ORDER BY week_start;";
			}
			if (sql == null) {
				getTCTRoleToSubCultureReportResponse.setError("No SQL");
				getTCTRoleToSubCultureReportResponse.setIsError(true);
				return getTCTRoleToSubCultureReportResponseDocument;
			}
			pstm = DB.prepareStatement(sql, null);
			pstm.setInt(1, clientId);
			pstm.setString(2, user);
			rs = pstm.executeQuery();
			while (rs.next()) {
				GetToSubCultureCount data = getTCTRoleToSubCultureReportResponse.addNewGetToSubCultureCount();
				if (userInput.equals("day")) {
					String date = rs.getString("date");
					int count = rs.getInt("counts");
					data.setLabelName(date);
					data.setCount(count);
				} else if (userInput.equals("week")) {
					String date = rs.getString("dates");
					int count = rs.getInt("counts");
					data.setLabelName(date);
					data.setCount(count);
				} else if (userInput.equals("month")) {
					String week_start = rs.getString("week_start");
					int count = rs.getInt("counts");
					data.setLabelName(week_start);
					data.setCount(count);
				}
			}
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			getTCTRoleToSubCultureReportResponse.setError(INTERNAL_SERVER_ERROR);
			getTCTRoleToSubCultureReportResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return getTCTRoleToSubCultureReportResponseDocument;
	}

	@Override
	public GetQARoleAllocateStorageReportResponseDocument getQARoleAllocateStorageReport(
			GetQARoleAllocateStorageReportRequestDocument req) {
		GetQARoleAllocateStorageReportResponseDocument getQARoleAllocateStorageReportResponseDocument = GetQARoleAllocateStorageReportResponseDocument.Factory
				.newInstance();
		GetQARoleAllocateStorageReportResponse getQARoleAllocateStorageReportResponse = getQARoleAllocateStorageReportResponseDocument
				.addNewGetQARoleAllocateStorageReportResponse();
		Trx trx = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
		int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			String sql = "WITH RoomCapacity AS (\n"
					+ "SELECT lt.name AS RoomType,COUNT(DISTINCT l.m_locator_id) AS num_locators,COUNT(DISTINCT l.m_locator_id) * 50 AS max_capacity\n"
					+ "FROM adempiere.m_locator l JOIN adempiere.m_locatortype lt ON lt.m_locatortype_id = l.m_locatortype_id\n"
					+ "WHERE l.ad_client_id = ? AND lt.name IN ('C1', 'C2', 'C3', 'C4') GROUP BY lt.name)\n"
					+ "SELECT rc.RoomType AS roomType,rc.num_locators,rc.max_capacity,COALESCE(SUM(s.qtyonhand), 0) AS total_qtyonhand,\n"
					+ "ROUND((COALESCE(SUM(s.qtyonhand), 0) / rc.max_capacity::float) * 100) AS percentage_occupied FROM RoomCapacity rc\n"
					+ "LEFT JOIN adempiere.m_locator l ON l.m_locatortype_id = (SELECT m_locatortype_id FROM adempiere.m_locatortype WHERE name = rc.RoomType)\n"
					+ "LEFT JOIN adempiere.m_storageonhand s ON s.m_locator_id = l.m_locator_id GROUP BY rc.RoomType, rc.num_locators, rc.max_capacity ORDER BY rc.RoomType;\n"
					+ "";

			pstm = DB.prepareStatement(sql.toString(), null);
			pstm.setInt(1, clientId);
			rs = pstm.executeQuery();
			while (rs.next()) {
				GetQAAllocateStorageCount data = getQARoleAllocateStorageReportResponse
						.addNewGetQAAllocateStorageCount();
				String roomType = rs.getString("roomType");
				int occupied = rs.getInt("percentage_occupied");
				data.setLabelName(roomType);
				data.setCount(occupied);
			}
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			getQARoleAllocateStorageReportResponse.setError(INTERNAL_SERVER_ERROR);
			getQARoleAllocateStorageReportResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return getQARoleAllocateStorageReportResponseDocument;
	}

	@Override
	public DownloadExcelApiForVisitCountResponseDocument downloadExcelApiForVisitCount(
			DownloadExcelApiForVisitCountRequestDocument req) {
		DownloadExcelApiForVisitCountResponseDocument downloadExcelApiForVisitCountResponseDocument = DownloadExcelApiForVisitCountResponseDocument.Factory
				.newInstance();
		DownloadExcelApiForVisitCountResponse downloadExcelApiForVisitCountResponse = downloadExcelApiForVisitCountResponseDocument
				.addNewDownloadExcelApiForVisitCountResponse();
		DownloadExcelApiForVisitCountRequest loginRequest = req.getDownloadExcelApiForVisitCountRequest();
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		String userInput = loginRequest.getUserInput();
		String visitType = loginRequest.getVisitType();
		PreparedStatement pstm = null;
		ResultSet rs = null;
		Trx trx = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
			String user = safeTrim(new MUser(ctx, Env.getAD_User_ID(ctx), null).getName());
			int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();

			List<PO> poList = new Query(ctx, TABLE_VISIT, "ad_client_id=?", trxName)
			        .setParameters(clientId)
			        .list();
			
			if (containsMaliciousPattern(userInput)) {
				downloadExcelApiForVisitCountResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				downloadExcelApiForVisitCountResponse.setIsError(true);
			    return downloadExcelApiForVisitCountResponseDocument;
			}
			
			if (containsMaliciousPattern(visitType)) {
				downloadExcelApiForVisitCountResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
			    downloadExcelApiForVisitCountResponse.setIsError(true);
			    return downloadExcelApiForVisitCountResponseDocument;
			}

			StringBuilder visitTypeListQuoted = new StringBuilder();

			for (int i = 0; i < poList.size(); i++) {
			    PO record = poList.get(i);
			    X_TC_VisitType tt = new X_TC_VisitType(ctx, record.get_ID(), trxName);
			    String name = tt.getName();

			    if (i > 0) visitTypeListQuoted.append(",");
			    visitTypeListQuoted.append("'").append(name).append("'");
			}

			String allVisitTypes = visitTypeListQuoted.toString();

			if (visitType == null || visitType.trim().isEmpty() || visitType.equalsIgnoreCase("all")) {
			    visitType = allVisitTypes;
			}else {
		        visitType = "'" + visitType + "'";
		    }
			
			String sql = null;
			if (userInput.equals("day")) {
				sql = "SELECT TO_CHAR(CURRENT_DATE, 'Day') AS day_name,CURRENT_DATE AS date,COUNT(DISTINCT v.tc_visit_id) AS visit_count\n"
						+ "FROM adempiere.tc_visit v INNER JOIN adempiere.ad_user u ON u.ad_user_id = v.updatedby\n"
						+ "INNER JOIN adempiere.tc_status s ON s.tc_status_id = v.tc_status_id AND s.name = 'Completed'\n"
						+ "INNER JOIN adempiere.tc_visittype vt ON vt.tc_visittype_id = v.tc_visittype_id\n"
						+ "AND vt.name IN (" + visitType + ")  WHERE v.updated::date = CURRENT_DATE\n"
						+ "AND v.ad_client_id = ? AND u.name = ?;";
			} else if (userInput.equals("week")) {
				sql = "WITH days AS (SELECT generate_series(0, 6) AS day_of_week),\n"
						+ "visit_counts AS (SELECT v.updated::date AS visit_day,to_char(v.updated::date, 'FMDay') AS day_name,\n"
						+ "EXTRACT(dow FROM v.updated::date)::int AS day_of_week,COUNT(DISTINCT v.tc_visit_id) AS visit_count\n"
						+ "FROM adempiere.tc_visit v JOIN adempiere.ad_user u ON u.ad_user_id = v.updatedby           \n"
						+ "JOIN adempiere.tc_status s ON s.tc_status_id = v.tc_status_id AND s.name = 'Completed'\n"
						+ "JOIN adempiere.tc_visittype vt ON vt.tc_visittype_id = v.tc_visittype_id\n"
						+ "AND vt.name IN (" + visitType + ") WHERE v.ad_client_id = ? AND TRIM(u.name) = ?\n"
						+ "AND v.updated::date BETWEEN (CURRENT_DATE - INTERVAL '6 days')::date AND CURRENT_DATE\n"
						+ "GROUP BY v.updated::date,to_char(v.updated::date, 'FMDay'),EXTRACT(dow FROM v.updated::date))\n"
						+ "SELECT (CURRENT_DATE - INTERVAL '6 days' + d.day_of_week * INTERVAL '1 day')::date AS dates,\n"
						+ "COALESCE(vc.day_name,to_char((CURRENT_DATE - INTERVAL '6 days' + d.day_of_week * INTERVAL '1 day')::date, 'FMDay')) AS day_name,\n"
						+ "COALESCE(vc.visit_count, 0) AS visit_count FROM days d\n"
						+ "LEFT JOIN visit_counts vc ON (CURRENT_DATE - INTERVAL '6 days' + d.day_of_week * INTERVAL '1 day')::date = vc.visit_day ORDER BY dates;";
			} else if (userInput.equals("month")) {
				sql = "WITH weeks AS (SELECT generate_series(0, 4) AS week_number),\n"
						+ "visit_counts AS (SELECT date_trunc('week', v.updated)::date AS week_start,\n"
						+ "to_char(date_trunc('week', v.updated), 'YYYY-MM-DD') AS week_start_str,COUNT(*) AS visit_count\n"
						+ "FROM adempiere.tc_visit v JOIN adempiere.ad_user u ON u.ad_user_id = v.updatedby\n"
						+ "JOIN adempiere.tc_status s ON s.tc_status_id = v.tc_status_id AND s.name = 'Completed'\n"
						+ "JOIN adempiere.tc_visittype vt ON vt.tc_visittype_id = v.tc_visittype_id AND vt.name IN (" + visitType + ") \n"
						+ "WHERE v.ad_client_id = ? AND u.name = ? AND v.updated::date >= (current_date - interval '29 days')::date\n"
						+ "AND v.updated::date <= current_date GROUP BY date_trunc('week', v.updated)),\n"
						+ "date_range AS (SELECT (current_date - interval '29 days')::date + generate_series(0, 29) AS day)\n"
						+ "SELECT to_char(date_trunc('week', day), 'YYYY-MM-DD') AS week_start,COALESCE(vc.visit_count, 0) AS visit_count\n"
						+ "FROM date_range LEFT JOIN visit_counts vc ON date_trunc('week', day) = vc.week_start\n"
						+ "GROUP BY date_trunc('week', day),vc.visit_count ORDER BY week_start;";
			} else if (userInput.equals("year")) {
				sql = "WITH months AS (SELECT generate_series(0, 11) AS month),\n"
						+ "visit_counts AS (SELECT date_trunc('month', v.updated)::date AS month_year,to_char(v.updated, 'FMMonth') AS month_name,COUNT(*) AS visit_count\n"
						+ "FROM adempiere.tc_visit v JOIN adempiere.ad_user u ON u.ad_user_id = v.updatedby\n"
						+ "JOIN adempiere.tc_status s ON s.tc_status_id = v.tc_status_id AND s.name = 'Completed'\n"
						+ "JOIN adempiere.tc_visittype vt ON vt.tc_visittype_id = v.tc_visittype_id AND vt.name IN (" + visitType + ") \n"
						+ "WHERE v.ad_client_id = ? AND u.name = ?\n"
						+ "AND v.updated::date >= (current_date - interval '364 days')::date AND v.updated::date <= current_date\n"
						+ "GROUP BY date_trunc('month', v.updated),to_char(v.updated, 'FMMonth'))\n"
						+ "SELECT to_char(date_trunc('month', current_date)  - (m.month || ' months')::interval, 'YYYY-MM-01') AS month_date,\n"
						+ "COALESCE(vc.visit_count, 0) AS visit_count FROM months m LEFT JOIN visit_counts vc\n"
						+ "ON date_trunc('month', current_date) - (m.month || ' months')::interval = vc.month_year\n"
						+ "ORDER BY date_trunc('month', current_date) - (m.month || ' months')::interval;";
			} else if (userInput.equals("all")) {
				sql = "WITH year_counts AS (SELECT date_trunc('year', v.updated)::date AS year_start,COUNT(*) AS counts\n"
						+ "FROM adempiere.tc_visit v JOIN adempiere.ad_user u ON u.ad_user_id = v.updatedby\n"
						+ "JOIN adempiere.tc_status s ON s.tc_status_id = v.tc_status_id AND s.name = 'Completed'\n"
						+ "JOIN adempiere.tc_visittype vt ON vt.tc_visittype_id = v.tc_visittype_id AND vt.name IN (" + visitType + ") \n"
						+ "WHERE v.ad_client_id = ? AND u.name = ? GROUP BY date_trunc('year', v.updated)),\n"
						+ "year_range AS (SELECT date_trunc('year', CURRENT_DATE)::date AS year_start\n"
						+ "UNION ALL\n"
						+ "SELECT generate_series((SELECT MIN(year_start) FROM year_counts),date_trunc('year', CURRENT_DATE)::date,interval '1 year')::date AS year_start),\n"
						+ "all_years AS (SELECT DISTINCT year_start FROM year_range)\n"
						+ "SELECT to_char(a.year_start, 'YYYY-01-01') AS year_date,COALESCE(y.counts, 0) AS counts FROM all_years a\n"
						+ "LEFT JOIN year_counts y ON a.year_start = y.year_start ORDER BY a.year_start;";

			}
			
			if (sql == null) {
				downloadExcelApiForVisitCountResponse.setError("No SQL");
				downloadExcelApiForVisitCountResponse.setIsError(true);
				return downloadExcelApiForVisitCountResponseDocument;
			}
			// Create SXSSFWorkbook
			SXSSFWorkbook workbook = new SXSSFWorkbook(-1);

			// Create styles
			CellStyle myStyle = workbook.createCellStyle();
			org.apache.poi.ss.usermodel.Font myFont = workbook.createFont();
			myFont.setBold(true);
			myStyle.setFont(myFont);

			CellStyle wrapStyle = workbook.createCellStyle();
			wrapStyle.setWrapText(true);
			// Create sheet and rows
			SXSSFSheet sheet = workbook.createSheet("Field Officer Visit Count Report");
			SXSSFRow headerRow = sheet.createRow(0);

			// Create header cells
			SXSSFCell cellH0 = headerRow.createCell(0);
			cellH0.setCellValue("Label Name");
			cellH0.setCellStyle(myStyle);

			SXSSFCell cellH1 = headerRow.createCell(1);
			cellH1.setCellValue("Count");
			cellH1.setCellStyle(myStyle);

			pstm = DB.prepareStatement(sql, null);
			pstm.setInt(1, clientId);
			pstm.setString(2, user);
			rs = pstm.executeQuery();
			int i = 1;
			while (rs.next()) {
				GetReportDataVisitCount data = downloadExcelApiForVisitCountResponse.addNewGetReportDataVisitCount();
				if (userInput.equals("day")) {
					String date = rs.getString("date");
					int count = rs.getInt("visit_count");
					data.setLabelName(date);
					data.setCount(count);
					SXSSFRow tableDescriptionRow = sheet.createRow(i);
					SXSSFCell cell1 = tableDescriptionRow.createCell(0);
					cell1.setCellValue(date);
					cell1.setCellStyle(wrapStyle);

					SXSSFCell cell2 = tableDescriptionRow.createCell(1);
					cell2.setCellValue(count);
					cell2.setCellStyle(wrapStyle);
				} else if (userInput.equals("week")) {
					String dates = rs.getString("dates");
					int count = rs.getInt("visit_count");
					data.setLabelName(dates);
					data.setCount(count);
					SXSSFRow tableDescriptionRow = sheet.createRow(i);
					SXSSFCell cell1 = tableDescriptionRow.createCell(0);
					cell1.setCellValue(dates);
					cell1.setCellStyle(wrapStyle);

					SXSSFCell cell2 = tableDescriptionRow.createCell(1);
					cell2.setCellValue(count);
					cell2.setCellStyle(wrapStyle);
				} else if (userInput.equals("month")) {
					String week_start = rs.getString("week_start");
					int count = rs.getInt("visit_count");
					data.setLabelName(week_start);
					data.setCount(count);
					SXSSFRow tableDescriptionRow = sheet.createRow(i);
					SXSSFCell cell1 = tableDescriptionRow.createCell(0);
					cell1.setCellValue(week_start);
					cell1.setCellStyle(wrapStyle);

					SXSSFCell cell2 = tableDescriptionRow.createCell(1);
					cell2.setCellValue(count);
					cell2.setCellStyle(wrapStyle);
				} else if (userInput.equals("year")) {
					String month_date = rs.getString("month_date");
					int count = rs.getInt("visit_count");
					data.setLabelName(month_date);
					data.setCount(count);
					SXSSFRow tableDescriptionRow = sheet.createRow(i);
					SXSSFCell cell1 = tableDescriptionRow.createCell(0);
					cell1.setCellValue(month_date);
					cell1.setCellStyle(wrapStyle);

					SXSSFCell cell2 = tableDescriptionRow.createCell(1);
					cell2.setCellValue(count);
					cell2.setCellStyle(wrapStyle);
				} else if (userInput.equals("all")) {
					String year_date = rs.getString("year_date");
					int count = rs.getInt("counts");
					data.setLabelName(year_date);
					data.setCount(count);
					SXSSFRow tableDescriptionRow = sheet.createRow(i);
					SXSSFCell cell1 = tableDescriptionRow.createCell(0);
					cell1.setCellValue(year_date);
					cell1.setCellStyle(wrapStyle);

					SXSSFCell cell2 = tableDescriptionRow.createCell(1);
					cell2.setCellValue(count);
					cell2.setCellStyle(wrapStyle);
				}
				i++;
			}
			// Write workbook to ByteArrayOutputStream
			workbook.write(byteArrayOutputStream);
			workbook.close();
			byte[] excelBlobBytes = byteArrayOutputStream.toByteArray();
			downloadExcelApiForVisitCountResponse.setExcelBlob(excelBlobBytes);
			downloadExcelApiForVisitCountResponse.setIsError(false);
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			e.printStackTrace();
			downloadExcelApiForVisitCountResponse.setError(INTERNAL_SERVER_ERROR);
			downloadExcelApiForVisitCountResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return downloadExcelApiForVisitCountResponseDocument;
	}

	@Override
	public GetVillageListResponseDocument getVillageList(GetVillageListRequestDocument req) {
		GetVillageListResponseDocument getVillageListResponseDocument = GetVillageListResponseDocument.Factory
				.newInstance();
		GetVillageListResponse getVillageListResponse = getVillageListResponseDocument.addNewGetVillageListResponse();
		Trx trx = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
		int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			String sql = "SELECT villagename FROM adempiere.tc_farmer WHERE ad_client_id = ?\n"
					+ "GROUP BY villagename HAVING COUNT(*) > 1\n" + "UNION ALL\n"
					+ "SELECT villagename FROM adempiere.tc_farmer WHERE ad_client_id = ?\n"
					+ "GROUP BY villagename HAVING COUNT(*) = 1 ORDER BY villagename;";

			pstm = DB.prepareStatement(sql.toString(), null);
			pstm.setInt(1, clientId);
			pstm.setInt(2, clientId);
			rs = pstm.executeQuery();
			while (rs.next()) {
				GetVillageList data = getVillageListResponse.addNewGetVillageList();
				String villageNumber = rs.getString("villagename");
				data.setVillageNumber(villageNumber);
			}
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			getVillageListResponse.setError(INTERNAL_SERVER_ERROR);
			getVillageListResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return getVillageListResponseDocument;
	}

	@Override
	public GetQARoleExpiryReportResponseDocument getQARoleExpiryReport(GetQARoleExpiryReportRequestDocument req) {
		GetQARoleExpiryReportResponseDocument getQARoleExpiryReportResponseDocument = GetQARoleExpiryReportResponseDocument.Factory
				.newInstance();
		GetQARoleExpiryReportResponse getQARoleExpiryReportResponse = getQARoleExpiryReportResponseDocument
				.addNewGetQARoleExpiryReportResponse();
		Trx trx = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
		int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			String sql = "WITH subquery AS (\n"
					+ "SELECT ps.codeno || ' ' || v.codeno || ' ' || cl.parentcultureline || ' ' || TO_CHAR(cl.culturedate, 'DD-MM-YY') || ' ' || ns.codeno AS cultureCode,\n"
					+ "cs.name || ' - ' || cl.cycleno AS cycle,DATE(cl.created) AS manufacturingDate,\n"
					+ "DATE(cl.created + (cs.period::int * INTERVAL '1 day')) AS expiryDate,l.x AS room,l.y AS rack,l.z AS columns,cs.period AS period,\n"
					+ "(SELECT COUNT(*) FROM adempiere.tc_culturelabel cll WHERE cll.parentuuid = cl.c_uuid LIMIT 1) AS subquery_column\n"
					+ "FROM adempiere.tc_culturelabel cl \n"
					+ "JOIN adempiere.tc_plantspecies ps ON ps.tc_plantspecies_id = cl.tc_species_id JOIN adempiere.tc_variety v ON v.tc_variety_id = cl.tc_species_ids\n"
					+ "JOIN adempiere.tc_naturesample ns ON ns.tc_naturesample_id = cl.tc_naturesample_id JOIN adempiere.tc_out o ON o.tc_out_id = cl.tc_out_id\n"
					+ "JOIN adempiere.m_locator l ON l.m_locator_id = o.m_locator_id JOIN adempiere.tc_culturestage cs ON cs.tc_culturestage_id = cl.tc_culturestage_id \n"
					+ "WHERE cl.ad_client_id = ? AND cl.isdiscarded = 'N' AND cl.tosubculturecheck = 'N' AND cl.c_uuid IS NOT NULL AND cl.parentuuid IS NOT NULL)\n"
					+ "SELECT cultureCode,cycle,manufacturingDate,expiryDate,room,rack,columns,COUNT(*) AS count,period\n"
					+ "FROM subquery WHERE subquery_column = 0 AND expiryDate <= current_date\n"
					+ "GROUP BY cycle,room,rack,columns,cultureCode,manufacturingDate,expiryDate,period\n"
					+ "ORDER BY expiryDate,cycle,room,rack,columns;\n" + "";

			pstm = DB.prepareStatement(sql.toString(), null);
			pstm.setInt(1, clientId);
			rs = pstm.executeQuery();
			while (rs.next()) {
				GetExpiryList data = getQARoleExpiryReportResponse.addNewGetExpiryList();
				String cultureCode = rs.getString("cultureCode");
				String cycle = rs.getString("cycle");
				String manufacturingDate = rs.getString("manufacturingDate");
				String expiryDate = rs.getString("expiryDate");
				String room = rs.getString("room");
				String rack = rs.getString("rack");
				String columns = rs.getString("columns");
				String count = rs.getString("count");

				data.setCultureCode(cultureCode);
				data.setCycle(cycle);
				data.setManufacturingDate(manufacturingDate);
				data.setExpiryDate(expiryDate);
				data.setRoom(room);
				data.setRack(rack);
				data.setColumns(columns);
				data.setCount(count);
			}
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			getQARoleExpiryReportResponse.setError(INTERNAL_SERVER_ERROR);
			getQARoleExpiryReportResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return getQARoleExpiryReportResponseDocument;
	}

	@Override
	public GetQARoleWorkLogResponseDocument getQARoleWorkLog(GetQARoleWorkLogRequestDocument req) {
		GetQARoleWorkLogResponseDocument getQARoleWorkLogResponseDocument = GetQARoleWorkLogResponseDocument.Factory
				.newInstance();
		GetQARoleWorkLogResponse getQARoleWorkLogResponse = getQARoleWorkLogResponseDocument
				.addNewGetQARoleWorkLogResponse();
		Trx trx = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
		int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			String sql1 = "WITH subquery AS (\n"
					+ "SELECT ps.codeno || ' ' || v.codeno || ' ' || cl.parentcultureline || ' ' || TO_CHAR(cl.culturedate, 'DD-MM-YY') || ' ' || ns.codeno AS cultureCode,\n"
					+ "cs.name || ' - ' || cl.cycleno AS cycle,DATE(cl.created) AS manufacturingDate,\n"
					+ "DATE(cl.created + (cs.period::int * INTERVAL '1 day')) AS expiryDate,l.x AS room,l.y AS rack,l.z AS columns,cs.period AS period,\n"
					+ "(SELECT COUNT(*) FROM adempiere.tc_culturelabel cll WHERE cll.parentuuid = cl.c_uuid LIMIT 1) AS subquery_column FROM adempiere.tc_culturelabel cl \n"
					+ "JOIN adempiere.tc_plantspecies ps ON ps.tc_plantspecies_id = cl.tc_species_id JOIN adempiere.tc_variety v ON v.tc_variety_id = cl.tc_variety_id\n"
					+ "JOIN adempiere.tc_naturesample ns ON ns.tc_naturesample_id = cl.tc_naturesample_id JOIN adempiere.tc_out o ON o.tc_out_id = cl.tc_out_id\n"
					+ "JOIN adempiere.m_locator l ON l.m_locator_id = o.m_locator_id JOIN adempiere.tc_culturestage cs ON cs.tc_culturestage_id = cl.tc_culturestage_id \n"
					+ "WHERE cl.ad_client_id = ? AND cl.isdiscarded = 'N' AND cl.tosubculturecheck = 'N' AND cl.c_uuid IS NOT NULL AND cl.parentuuid IS NOT NULL),\n"
					+ "total_counts AS (SELECT SUM(COUNT(*)) OVER() AS totalCount,cultureCode,cycle,manufacturingDate,expiryDate,room,rack,columns,COUNT(*) AS count,period FROM subquery \n"
					+ "WHERE subquery_column = 0 AND expiryDate = current_date GROUP BY cycle,room,rack,columns,cultureCode,manufacturingDate,expiryDate,period),\n"
					+ "dummy AS (SELECT 0 AS totalCount)\n"
					+ "SELECT COALESCE(tc.totalCount, d.totalCount) AS totalCount FROM dummy d LEFT JOIN (SELECT totalCount FROM total_counts LIMIT 1) tc ON TRUE;";

			pstm = DB.prepareStatement(sql1.toString(), null);
			pstm.setInt(1, clientId);
			rs = pstm.executeQuery();
			while (rs.next()) {
				int todayTotalCount = rs.getInt("totalCount");
				getQARoleWorkLogResponse.setTodayTotalCount(todayTotalCount);
			}
			closeDbCon(pstm, rs);
			pstm = null;
			rs = null;

			String sql2 = "WITH subquery AS (\n"
					+ "SELECT ps.codeno || ' ' || v.codeno || ' ' || cl.parentcultureline || ' ' || TO_CHAR(cl.culturedate, 'DD-MM-YY') || ' ' || ns.codeno AS cultureCode,\n"
					+ "cs.name || ' - ' || cl.cycleno AS cycle,DATE(cl.created) AS manufacturingDate,\n"
					+ "DATE(cl.created + (cs.period::int * INTERVAL '1 day')) AS expiryDate,l.x AS room,l.y AS rack,l.z AS columns,cs.period AS period,\n"
					+ "(SELECT COUNT(*) FROM adempiere.tc_culturelabel cll WHERE cll.parentuuid = cl.c_uuid LIMIT 1) AS subquery_column FROM adempiere.tc_culturelabel cl \n"
					+ "JOIN adempiere.tc_plantspecies ps ON ps.tc_plantspecies_id = cl.tc_species_id JOIN adempiere.tc_variety v ON v.tc_variety_id = cl.tc_variety_id\n"
					+ "JOIN adempiere.tc_naturesample ns ON ns.tc_naturesample_id = cl.tc_naturesample_id JOIN adempiere.tc_out o ON o.tc_out_id = cl.tc_out_id\n"
					+ "JOIN adempiere.m_locator l ON l.m_locator_id = o.m_locator_id JOIN adempiere.tc_culturestage cs ON cs.tc_culturestage_id = cl.tc_culturestage_id \n"
					+ "WHERE cl.ad_client_id = ? AND cl.isdiscarded = 'N' AND cl.tosubculturecheck = 'N' AND cl.c_uuid IS NOT NULL AND cl.parentuuid IS NOT NULL),\n"
					+ "total_counts AS (SELECT SUM(COUNT(*)) OVER() AS totalCount,cultureCode,cycle,manufacturingDate,expiryDate,room,rack,columns,COUNT(*) AS count,period FROM subquery \n"
					+ "WHERE subquery_column = 0 AND expiryDate < current_date GROUP BY cycle,room,rack,columns,cultureCode,manufacturingDate,expiryDate,period),\n"
					+ "dummy AS (SELECT 0 AS totalCount)\n"
					+ "SELECT COALESCE(tc.totalCount, d.totalCount) AS pendingTotalCount FROM dummy d LEFT JOIN (SELECT totalCount FROM total_counts LIMIT 1) tc ON TRUE;";
			pstm = DB.prepareStatement(sql2.toString(), null);
			pstm.setInt(1, clientId);
			rs = pstm.executeQuery();
			while (rs.next()) {
				int pendingTotalCount = rs.getInt("pendingTotalCount");
				getQARoleWorkLogResponse.setPendingTotalCount(pendingTotalCount);
			}
			closeDbCon(pstm, rs);
			pstm = null;
			rs = null;
			String sql3 = "WITH subquery AS (\n"
					+ "SELECT ps.codeno || ' ' || v.codeno || ' ' || cl.parentcultureline || ' ' || TO_CHAR(cl.culturedate, 'DD-MM-YY') || ' ' || ns.codeno AS cultureCode,\n"
					+ "cs.name || ' - ' || cl.cycleno AS cycle,DATE(cl.created) AS manufacturingDate,\n"
					+ "DATE(cl.created + (cs.period::int * INTERVAL '1 day')) AS expiryDate,l.x AS room,l.y AS rack,l.z AS columns,cs.period AS period,\n"
					+ "(SELECT COUNT(*) FROM adempiere.tc_culturelabel cll WHERE cll.parentuuid = cl.c_uuid LIMIT 1) AS subquery_column FROM adempiere.tc_culturelabel cl \n"
					+ "JOIN adempiere.tc_plantspecies ps ON ps.tc_plantspecies_id = cl.tc_species_id JOIN adempiere.tc_variety v ON v.tc_variety_id = cl.tc_variety_id\n"
					+ "JOIN adempiere.tc_naturesample ns ON ns.tc_naturesample_id = cl.tc_naturesample_id JOIN adempiere.tc_out o ON o.tc_out_id = cl.tc_out_id\n"
					+ "JOIN adempiere.m_locator l ON l.m_locator_id = o.m_locator_id JOIN adempiere.tc_culturestage cs ON cs.tc_culturestage_id = cl.tc_culturestage_id \n"
					+ "WHERE cl.ad_client_id = ? AND cl.isdiscarded = 'N' AND cl.tosubculturecheck = 'Y' AND cl.c_uuid IS NOT NULL AND cl.parentuuid IS NOT NULL),\n"
					+ "total_counts AS (SELECT SUM(COUNT(*)) OVER() AS totalCount,cultureCode,cycle,manufacturingDate,expiryDate,room,rack,columns,COUNT(*) AS count,period FROM subquery \n"
					+ "WHERE subquery_column = 0 AND expiryDate <= current_date GROUP BY cycle,room,rack,columns,cultureCode,manufacturingDate,expiryDate,period),\n"
					+ "dummy AS (SELECT 0 AS totalCount)\n"
					+ "SELECT COALESCE(tc.totalCount, d.totalCount) AS cultureCount FROM dummy d LEFT JOIN (SELECT totalCount FROM total_counts LIMIT 1) tc ON TRUE;";
			pstm = DB.prepareStatement(sql3.toString(), null);
			pstm.setInt(1, clientId);
			rs = pstm.executeQuery();
			while (rs.next()) {
				int cultureTotalCount = rs.getInt("cultureCount");
				getQARoleWorkLogResponse.setCultureTotalCount(cultureTotalCount);
			}
			closeDbCon(pstm, rs);
			pstm = null;
			rs = null;
			String sql4 = "WITH subquery AS (SELECT DATE(cl.updated) AS manufacturingDate,\n"
					+ "(SELECT COUNT(*) FROM adempiere.tc_culturelabel cll WHERE cll.parentuuid = cl.c_uuid LIMIT 1) AS subquery_column \n"
					+ "FROM adempiere.tc_culturelabel cl Join adempiere.tc_variety v ON v.tc_variety_id = cl.tc_variety_id WHERE cl.ad_client_id = ? AND cl.isdiscarded = 'Y' AND cl.c_uuid IS NOT NULL AND cl.parentuuid IS NOT NULL),\n"
					+ "total_counts AS (SELECT COUNT(*) AS totalCount FROM subquery WHERE manufacturingDate = current_date),\n"
					+ "dummy AS (SELECT 0 AS totalCount)\n"
					+ "SELECT COALESCE(tc.totalCount, d.totalCount) AS todayDiscardCount FROM dummy d \n"
					+ "LEFT JOIN (SELECT totalCount FROM total_counts LIMIT 1)tc ON TRUE;";
			pstm = DB.prepareStatement(sql4.toString(), null);
			pstm.setInt(1, clientId);
			rs = pstm.executeQuery();
			while (rs.next()) {
				int todayDiscardCount = rs.getInt("todayDiscardCount");
				getQARoleWorkLogResponse.setTodayDiscardCount(todayDiscardCount);
			}
			closeDbCon(pstm, rs);
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			getQARoleWorkLogResponse.setError(INTERNAL_SERVER_ERROR);
			getQARoleWorkLogResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return getQARoleWorkLogResponseDocument;
	}

	@Override
	public GetAllWorkLogRecordsResponseDocument getAllWorkLogRecords(GetAllWorkLogRecordsRequestDocument req) {
		GetAllWorkLogRecordsResponseDocument getAllWorkLogRecordsResponseDocument = GetAllWorkLogRecordsResponseDocument.Factory
				.newInstance();
		GetAllWorkLogRecordsResponse getAllWorkLogRecordsResponse = getAllWorkLogRecordsResponseDocument
				.addNewGetAllWorkLogRecordsResponse();
		GetAllWorkLogRecordsRequest loginRequest = req.getGetAllWorkLogRecordsRequest();
		Trx trx = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		int allCount = 0;
		int pageSize = 0; // Number of records per page
		int pageNumber = 0; // Current page number
		int offset = 0; // Calculate offset
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
		int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			pageSize = loginRequest.getPageSize(); // Number of records per page
			pageNumber = loginRequest.getPageNumber(); // Current page number
			offset = (pageNumber - 1) * pageSize; // Calculate offset
			StringBuilder sql = new StringBuilder(
					"WITH subquery AS (SELECT ps.codeno || ' ' || v.codeno || ' ' || cl.parentcultureline || ' ' || TO_CHAR(cl.culturedate, 'DD-MM-YY') || ' ' || ns.codeno AS cultureCode,\n"
							+ "cs.name || ' - ' || cl.cycleno AS cycle,DATE(cl.created) AS manufacturingDate,\n"
							+ "DATE(cl.created + (cs.period::int * INTERVAL '1 day')) AS expiryDate,l.x AS room,l.y AS rack,l.z AS columns,\n"
							+ "cs.period AS period,(SELECT COUNT(*) FROM adempiere.tc_culturelabel cll WHERE cll.parentuuid = cl.c_uuid LIMIT 1) AS subquery_column\n"
							+ "FROM adempiere.tc_culturelabel cl JOIN adempiere.tc_plantspecies ps ON ps.tc_plantspecies_id = cl.tc_species_id \n"
							+ "JOIN adempiere.tc_variety v ON v.tc_variety_id = cl.tc_variety_id JOIN adempiere.tc_naturesample ns ON ns.tc_naturesample_id = cl.tc_naturesample_id \n"
							+ "JOIN adempiere.tc_in i ON i.tc_in_id = cl.tc_in_id JOIN adempiere.tc_out o ON o.tc_out_id = cl.tc_out_id\n"
							+ "JOIN adempiere.m_locator l ON l.m_locator_id = o.m_locator_id JOIN adempiere.tc_culturestage cs ON cs.tc_culturestage_id = cl.tc_culturestage_id \n"
							+ "WHERE cl.ad_client_id = ? AND cl.isdiscarded = 'N' AND cl.tosubculturecheck = 'N' AND cl.c_uuid IS NOT NULL AND cl.parentuuid IS NOT NULL),\n"
							+ "aggregated AS (SELECT cultureCode,cycle,manufacturingDate,expiryDate,room,rack,columns,COUNT(*) AS count,period FROM subquery\n"
							+ "WHERE subquery_column = 0 AND expiryDate = current_date GROUP BY cycle, room, rack, columns, cultureCode, manufacturingDate, expiryDate, period)\n"
							+ "SELECT cultureCode,cycle,manufacturingDate,expiryDate,room,rack,columns,count,SUM(count) OVER () AS totalCount,period\n"
							+ "FROM aggregated ORDER BY expiryDate, cycle, room, rack, columns  ");

			sql.append(" LIMIT " + pageSize + " OFFSET " + offset + "");

			pstm = DB.prepareStatement(sql.toString(), null);
			pstm.setInt(1, clientId);
			rs = pstm.executeQuery();

			if (!rs.isBeforeFirst()) {
				getAllWorkLogRecordsResponse.setIsError(false);
				getAllWorkLogRecordsResponse.setTotalCount(allCount);
				getAllWorkLogRecordsResponse.addNewGetAllList();
				return getAllWorkLogRecordsResponseDocument;
			}

			while (rs.next()) {
				GetAllList data = getAllWorkLogRecordsResponse.addNewGetAllList();
				String cultureCode = rs.getString("cultureCode");
				String cycle = rs.getString("cycle");
				String manufacturingDate = rs.getString("manufacturingDate");
				String expiryDate = rs.getString("expiryDate");
				String room = rs.getString("room");
				String rack = rs.getString("rack");
				String columns = rs.getString("columns");
				String count = rs.getString("count");
				allCount = rs.getInt("totalCount");

				data.setCultureCode(cultureCode);
				data.setCycle(cycle);
				data.setManufacturingDate(manufacturingDate);
				data.setExpiryDate(expiryDate);
				data.setRoom(room);
				data.setRack(rack);
				data.setColumns(columns);
				data.setCount(count);
			}
			getAllWorkLogRecordsResponse.setIsError(false);
			getAllWorkLogRecordsResponse.setTotalCount(allCount);
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			getAllWorkLogRecordsResponse.setError(INTERNAL_SERVER_ERROR);
			getAllWorkLogRecordsResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return getAllWorkLogRecordsResponseDocument;
	}

	@Override
	public GetAllPendingRecordsResponseDocument getAllPendingRecords(GetAllPendingRecordsRequestDocument req) {
		GetAllPendingRecordsResponseDocument getAllPendingRecordsResponseDocument = GetAllPendingRecordsResponseDocument.Factory
				.newInstance();
		GetAllPendingRecordsResponse getAllPendingRecordsResponse = getAllPendingRecordsResponseDocument
				.addNewGetAllPendingRecordsResponse();
		GetAllPendingRecordsRequest loginRequest = req.getGetAllPendingRecordsRequest();
		Trx trx = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		int allCount = 0;
		int pageSize = 0; // Number of records per page
		int pageNumber = 0; // Current page number
		int offset = 0; // Calculate offset

		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
		int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			pageSize = loginRequest.getPageSize(); // Number of records per page
			pageNumber = loginRequest.getPageNumber(); // Current page number
			offset = (pageNumber - 1) * pageSize; // Calculate offset
			StringBuilder sql = new StringBuilder(
					"WITH subquery AS (SELECT ps.codeno || ' ' || v.codeno || ' ' || cl.parentcultureline || ' ' || TO_CHAR(cl.culturedate, 'DD-MM-YY') || ' ' || ns.codeno AS cultureCode,\n"
							+ "cs.name || ' - ' || cl.cycleno AS cycle,DATE(cl.created) AS manufacturingDate,DATE(cl.created + (cs.period::int * INTERVAL '1 day')) AS expiryDate,\n"
							+ "l.x AS room,l.y AS rack,l.z AS columns,cs.period AS period,(SELECT COUNT(*) FROM adempiere.tc_culturelabel cll WHERE cll.parentuuid = cl.c_uuid LIMIT 1) AS subquery_column\n"
							+ "FROM adempiere.tc_culturelabel cl JOIN adempiere.tc_plantspecies ps ON ps.tc_plantspecies_id = cl.tc_species_id\n"
							+ "JOIN adempiere.tc_variety v ON v.tc_variety_id = cl.tc_variety_id JOIN adempiere.tc_naturesample ns ON ns.tc_naturesample_id = cl.tc_naturesample_id\n"
							+ "JOIN adempiere.tc_in i ON i.tc_in_id = cl.tc_in_id JOIN adempiere.tc_out o ON o.tc_out_id = cl.tc_out_id\n"
							+ "JOIN adempiere.m_locator l ON l.m_locator_id = o.m_locator_id JOIN adempiere.tc_culturestage cs ON cs.tc_culturestage_id = cl.tc_culturestage_id\n"
							+ "WHERE cl.ad_client_id = ? AND cl.isdiscarded = 'N' AND cl.tosubculturecheck = 'N' AND cl.c_uuid IS NOT NULL AND cl.parentuuid IS NOT NULL),\n"
							+ "aggregated AS (SELECT cultureCode,cycle,manufacturingDate,expiryDate,room,rack,columns,COUNT(*) AS count,period FROM subquery\n"
							+ "WHERE subquery_column = 0 AND expiryDate < current_date GROUP BY cycle, room, rack, columns, cultureCode, manufacturingDate, expiryDate, period)\n"
							+ "SELECT cultureCode,cycle,manufacturingDate,expiryDate,room,rack,columns,count,SUM(count) OVER () AS totalCount,period\n"
							+ "FROM aggregated ORDER BY expiryDate, cycle, room, rack, columns  ");

			sql.append(" LIMIT " + pageSize + " OFFSET " + offset + "");

			pstm = DB.prepareStatement(sql.toString(), null);
			pstm.setInt(1, clientId);
			rs = pstm.executeQuery();

			if (!rs.isBeforeFirst()) {
				getAllPendingRecordsResponse.setIsError(false);
				getAllPendingRecordsResponse.setTotalCount(allCount);
				getAllPendingRecordsResponse.addNewGetAllPendingRecords();
				return getAllPendingRecordsResponseDocument;
			}

			while (rs.next()) {
				GetAllPendingRecords data = getAllPendingRecordsResponse.addNewGetAllPendingRecords();
				String cultureCode = rs.getString("cultureCode");
				String cycle = rs.getString("cycle");
				String manufacturingDate = rs.getString("manufacturingDate");
				String expiryDate = rs.getString("expiryDate");
				String room = rs.getString("room");
				String rack = rs.getString("rack");
				String columns = rs.getString("columns");
				String count = rs.getString("count");
				allCount = rs.getInt("totalCount");

				data.setCultureCode(cultureCode);
				data.setCycle(cycle);
				data.setManufacturingDate(manufacturingDate);
				data.setExpiryDate(expiryDate);
				data.setRoom(room);
				data.setRack(rack);
				data.setColumns(columns);
				data.setCount(count);
			}
			getAllPendingRecordsResponse.setIsError(false);
			getAllPendingRecordsResponse.setTotalCount(allCount);
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			getAllPendingRecordsResponse.setError(INTERNAL_SERVER_ERROR);
			getAllPendingRecordsResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return getAllPendingRecordsResponseDocument;
	}

	@Override
	public GetDiscardListResponseDocument getDiscardList(GetDiscardListRequestDocument req) {
		GetDiscardListResponseDocument getDiscardListResponseDocument = GetDiscardListResponseDocument.Factory
				.newInstance();
		GetDiscardListResponse getDiscardListResponse = getDiscardListResponseDocument.addNewGetDiscardListResponse();
		GetDiscardListRequest loginRequest = req.getGetDiscardListRequest();
		Trx trx = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		int allCount = 0;
		int pageSize = 0; // Number of records per page
		int pageNumber = 0; // Current page number
		int offset = 0; // Calculate offset
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
		int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			pageSize = loginRequest.getPageSize(); // Number of records per page
			pageNumber = loginRequest.getPageNumber(); // Current page number
			offset = (pageNumber - 1) * pageSize;
			StringBuilder sql = new StringBuilder(
					"WITH subquery AS (SELECT ps.codeno || ' ' || v.codeno || ' ' || cl.parentcultureline || ' ' || TO_CHAR(cl.culturedate, 'DD-MM-YY') || ' ' || ns.codeno AS cultureCode,\n"
							+ "cs.name || ' - ' || cl.cycleno AS cycle,DATE(cl.updated) AS updatedDate,DATE(cl.created + (cs.period::int * INTERVAL '1 day')) AS expiryDate,l.x AS room,l.y AS rack,l.z AS columns,cs.period AS period,\n"
							+ "(SELECT COUNT(*) FROM adempiere.tc_culturelabel cll WHERE cll.parentuuid = cl.c_uuid LIMIT 1) AS subquery_column FROM adempiere.tc_culturelabel cl \n"
							+ "JOIN adempiere.tc_plantspecies ps ON ps.tc_plantspecies_id = cl.tc_species_id \n"
							+ "JOIN adempiere.tc_variety v ON v.tc_variety_id = cl.tc_variety_id JOIN adempiere.tc_naturesample ns ON ns.tc_naturesample_id = cl.tc_naturesample_id \n"
							+ "JOIN adempiere.tc_in i ON i.tc_in_id = cl.tc_in_id JOIN adempiere.tc_out o ON o.tc_out_id = cl.tc_out_id \n"
							+ "JOIN adempiere.m_locator l ON l.m_locator_id = o.m_locator_id JOIN adempiere.tc_culturestage cs ON cs.tc_culturestage_id = cl.tc_culturestage_id \n"
							+ "WHERE cl.ad_client_id = ? AND cl.isdiscarded = 'Y' AND cl.c_uuid IS NOT NULL AND cl.parentuuid IS NOT NULL),\n"
							+ "aggregated AS (SELECT cultureCode,cycle,updatedDate,expiryDate,room,rack,columns,COUNT(*) AS count,period FROM subquery\n"
							+ "WHERE subquery_column = 0 AND updatedDate = current_date GROUP BY cycle, room, rack, columns, cultureCode, updatedDate, expiryDate, period)\n"
							+ "SELECT cultureCode,cycle,updatedDate,expiryDate,room,rack,columns,count,SUM(count) OVER () AS totalCount,period\n"
							+ "FROM aggregated ORDER BY expiryDate, cycle, room, rack, columns ");

			sql.append(" LIMIT " + pageSize + " OFFSET " + offset + "");

			pstm = DB.prepareStatement(sql.toString(), null);
			pstm.setInt(1, clientId);
			rs = pstm.executeQuery();

			if (!rs.isBeforeFirst()) {
				getDiscardListResponse.setIsError(false);
				getDiscardListResponse.setTotalCount(allCount);
				getDiscardListResponse.addNewGetDiscardListRecords();
				return getDiscardListResponseDocument;
			}

			while (rs.next()) {
				GetDiscardListRecords data = getDiscardListResponse.addNewGetDiscardListRecords();
				String cultureCode = rs.getString("cultureCode");
				String cycle = rs.getString("cycle");
				String updatedDate = rs.getString("updatedDate");
				String expiryDate = rs.getString("expiryDate");
				String room = rs.getString("room");
				String rack = rs.getString("rack");
				String columns = rs.getString("columns");
				String count = rs.getString("count");
				allCount = rs.getInt("totalCount");

				data.setCultureCode(cultureCode);
				data.setCycle(cycle);
				data.setUpdatedDate(updatedDate);
				data.setExpiryDate(expiryDate);
				data.setRoom(room);
				data.setRack(rack);
				data.setColumns(columns);
				data.setCount(count);
			}
			getDiscardListResponse.setIsError(false);
			getDiscardListResponse.setTotalCount(allCount);
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			getDiscardListResponse.setError(INTERNAL_SERVER_ERROR);
			getDiscardListResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return getDiscardListResponseDocument;
	}

	@Override
	public AddCultureOrderToAllRecordsResponseDocument addCultureOrderToAllRecords(
			AddCultureOrderToAllRecordsRequestDocument req) {
		AddCultureOrderToAllRecordsResponseDocument addCultureOrderToAllRecordsResponseDocument = AddCultureOrderToAllRecordsResponseDocument.Factory
				.newInstance();
		AddCultureOrderToAllRecordsResponse addCultureOrderToAllRecordsResponse = addCultureOrderToAllRecordsResponseDocument
				.addNewAddCultureOrderToAllRecordsResponse();
		AddCultureOrderToAllRecordsRequest loginRequest = req.getAddCultureOrderToAllRecordsRequest();

		String cultureCode = safeTrim(loginRequest.getCultureCode());
		String cycle = safeTrim(loginRequest.getCycle());
		String expiryDate = safeTrim(loginRequest.getExpiryDate());
		String room = safeTrim(loginRequest.getRoom());
		String rack = safeTrim(loginRequest.getRack());
		String columns = safeTrim(loginRequest.getColumns());
		int documentType = 0;
		int uomId = 0;
		Trx trx = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
			int clientId = Env.getAD_Client_ID(ctx);
			int orgId = Env.getAD_Org_ID(ctx);
			
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			documentType = TCUtills.getDocTypeId(clientId, DOCUMENT_ORDERED);
			
			if (containsMaliciousPattern(cultureCode)) {
				addCultureOrderToAllRecordsResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				addCultureOrderToAllRecordsResponse.setIsError(true);
			    return addCultureOrderToAllRecordsResponseDocument;
			}
			
			if (containsMaliciousPattern(cycle)) {
				addCultureOrderToAllRecordsResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				addCultureOrderToAllRecordsResponse.setIsError(true);
			    return addCultureOrderToAllRecordsResponseDocument;
			}
			
			if (containsMaliciousPattern(room)) {
				addCultureOrderToAllRecordsResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				addCultureOrderToAllRecordsResponse.setIsError(true);
			    return addCultureOrderToAllRecordsResponseDocument;
			}
			
			if (containsMaliciousPattern(rack)) {
				addCultureOrderToAllRecordsResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				addCultureOrderToAllRecordsResponse.setIsError(true);
			    return addCultureOrderToAllRecordsResponseDocument;
			}
			
			if (containsMaliciousPattern(columns)) {
				addCultureOrderToAllRecordsResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				addCultureOrderToAllRecordsResponse.setIsError(true);
			    return addCultureOrderToAllRecordsResponseDocument;
			}
			
			if (containsMaliciousPattern(expiryDate)) {
				addCultureOrderToAllRecordsResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				addCultureOrderToAllRecordsResponse.setIsError(true);
			    return addCultureOrderToAllRecordsResponseDocument;
			}
			

			String sql = "WITH subquery AS (SELECT cl.c_uuid AS cultureUUid,cl.tc_culturelabel_id AS cultureId,o.c_uuid AS outUUid,o.tc_out_id AS outId,\n"
					+ "ps.codeno || ' ' || v.codeno || ' ' || cl.parentcultureline || ' ' || TO_CHAR(cl.culturedate, 'DD-MM-YY') || ' ' || ns.codeno AS cultureCode,\n"
					+ "cs.name || ' - ' || cl.cycleno AS cycle,DATE(cl.created) AS manufacturingDate,\n"
					+ "DATE(cl.created + (cs.period::int * INTERVAL '1 day')) AS expiryDate,l.x AS room,l.y AS rack,l.z AS columns,\n"
					+ "(SELECT COUNT(*) FROM adempiere.tc_culturelabel cll WHERE cll.parentuuid = cl.c_uuid LIMIT 1) AS subquery_column FROM adempiere.tc_culturelabel cl \n"
					+ "JOIN adempiere.tc_plantspecies ps ON ps.tc_plantspecies_id = cl.tc_species_id JOIN adempiere.tc_variety v ON v.tc_variety_id = cl.tc_variety_id \n"
					+ "JOIN adempiere.tc_naturesample ns ON ns.tc_naturesample_id = cl.tc_naturesample_id JOIN adempiere.tc_out o ON o.tc_out_id = cl.tc_out_id \n"
					+ "JOIN adempiere.m_locator l ON l.m_locator_id = o.m_locator_id JOIN adempiere.tc_culturestage cs ON cs.tc_culturestage_id = cl.tc_culturestage_id \n"
					+ "WHERE cl.ad_client_id = ? AND cl.isdiscarded = 'N' AND cl.tosubculturecheck = 'N' AND cl.c_uuid IS NOT NULL AND cl.parentuuid IS NOT NULL)\n"
					+ "SELECT cultureUUid,cultureId,outUUid,outId,cultureCode,cycle,room,rack,columns,manufacturingDate,expiryDate\n"
					+ "FROM subquery WHERE subquery_column = 0 AND expiryDate <= current_date \n"
					+ "AND cultureCode = ? AND cycle = ? AND room = ? AND rack = ? AND columns = ? AND expirydate = ?::date\n"
					+ "ORDER BY expiryDate,cycle,rack,columns;";

			pstm = DB.prepareStatement(sql.toString(), null);
			pstm.setInt(1, clientId);
			pstm.setString(2, cultureCode);
			pstm.setString(3, cycle);
			pstm.setString(4, room);
			pstm.setString(5, rack);
			pstm.setString(6, columns);
			pstm.setString(7, expiryDate);
			rs = pstm.executeQuery();
			LinkedHashMap<Integer, Integer> cultureOutPairs = new LinkedHashMap<>();
			while (rs.next()) {
				int cultureid = rs.getInt("cultureId");
				int outid = rs.getInt("outId");
				cultureOutPairs.put(outid, cultureid);
			}
			if (cultureOutPairs.size() == 0) {
				addCultureOrderToAllRecordsResponse.setError("No Sub Culture Available");
				addCultureOrderToAllRecordsResponse.setIsError(true);
				return addCultureOrderToAllRecordsResponseDocument;
			}
			Date date = new Date();
			Timestamp timestamp = new Timestamp(date.getTime());
			TCOrder order = new TCOrder(ctx, 0, trx.getTrxName());
			order.setAD_Org_ID(orgId);
			order.setM_Warehouse_ID(Env.getContextAsInt(ctx, Env.M_WAREHOUSE_ID));
			order.setDateOrdered(timestamp);
			order.setC_DocTypeTarget_ID(documentType);
			order.setC_DocType_ID(0);
			order.setName("Multiple Culture Records created");
			order.setDocStatus(DocAction.STATUS_Drafted);
			order.setDocAction(DocAction.STATUS_Drafted);
			if (!order.save()) {
				throw new Exception("Failed to add Tc Order data: " + order);
			}
			trx.commit();
			int orderId = order.get_ID();
			addCultureOrderToAllRecordsResponse.setCultureOrder(orderId);

			for (Integer outId : cultureOutPairs.keySet()) {
				GetAllInRecords records = addCultureOrderToAllRecordsResponse.addNewGetAllInRecords();
				int cultureid = cultureOutPairs.get(outId);
				TCOut out = new TCOut(ctx, outId, trxName);
				int productId = out.getM_Product_ID();
				int locatorId = out.getM_Locator_ID();
				String ouuid = out.getc_uuid();
				uomId = out.getC_UOM_ID();

				TCIn in = new TCIn(ctx, 0, trx.getTrxName());
				in.setAD_Org_ID(orgId);
				in.setTC_order_ID(orderId);
				in.setM_Locator_ID(locatorId);
				in.setM_Product_ID(productId);
				in.setQuantity(out.getQuantity());
				in.setparentuuid(ouuid);
				in.setC_UOM_ID(uomId);
				in.saveEx();
				trx.commit();

				records.setInId(in.get_ID());
				TCCultureLabel label = new TCCultureLabel(ctx, cultureid, trx.getTrxName());
				
				label.settosubculturecheck(true);
				label.saveEx();
				trx.commit();
			}
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			addCultureOrderToAllRecordsResponse.setError(INTERNAL_SERVER_ERROR);
			addCultureOrderToAllRecordsResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return addCultureOrderToAllRecordsResponseDocument;
	}

	@Override
	public GetTCTRoleWorkLogResponseDocument getTCTRoleWorkLog(GetTCTRoleWorkLogRequestDocument req) {
		GetTCTRoleWorkLogResponseDocument getTCTRoleWorkLogResponseDocument = GetTCTRoleWorkLogResponseDocument.Factory
				.newInstance();
		GetTCTRoleWorkLogResponse getTCTRoleWorkLogResponse = getTCTRoleWorkLogResponseDocument
				.addNewGetTCTRoleWorkLogResponse();
		Trx trx = null;
		int cultureCount = 0, explantQty = 0, primaryHQty = 0, secondaryHQty = 0, finalQty = 0;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
		int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			String sql = "WITH subquery AS (\n"
					+ "SELECT ps.codeno || ' ' || v.codeno || ' ' || cl.parentcultureline || ' ' || TO_CHAR(cl.culturedate, 'DD-MM-YY') || ' ' || ns.codeno AS cultureCode,\n"
					+ "cs.name || ' - ' || cl.cycleno AS cycle,DATE(cl.created) AS manufacturingDate,\n"
					+ "DATE(cl.created + (cs.period::int * INTERVAL '1 day')) AS expiryDate,l.x AS room,l.y AS rack,l.z AS columns,cs.period AS period,\n"
					+ "(SELECT COUNT(*) FROM adempiere.tc_culturelabel cll WHERE cll.parentuuid = cl.c_uuid LIMIT 1) AS subquery_column FROM adempiere.tc_culturelabel cl \n"
					+ "JOIN adempiere.tc_plantspecies ps ON ps.tc_plantspecies_id = cl.tc_species_id JOIN adempiere.tc_variety v ON v.tc_variety_id = cl.tc_variety_id\n"
					+ "JOIN adempiere.tc_naturesample ns ON ns.tc_naturesample_id = cl.tc_naturesample_id JOIN adempiere.tc_out o ON o.tc_out_id = cl.tc_out_id\n"
					+ "JOIN adempiere.m_locator l ON l.m_locator_id = o.m_locator_id JOIN adempiere.tc_culturestage cs ON cs.tc_culturestage_id = cl.tc_culturestage_id \n"
					+ "WHERE cl.ad_client_id = ? AND cl.isdiscarded = 'N' AND cl.tosubculturecheck = 'Y' AND cl.c_uuid IS NOT NULL AND cl.parentuuid IS NOT NULL),\n"
					+ "total_counts AS (SELECT SUM(COUNT(*)) OVER() AS totalCount,cultureCode,cycle,manufacturingDate,expiryDate,room,rack,columns,COUNT(*) AS count,period FROM subquery \n"
					+ "WHERE subquery_column = 0 AND expiryDate <= current_date AND cycle != 'Rooting - 2' GROUP BY cycle,room,rack,columns,cultureCode,manufacturingDate,expiryDate,period),\n"
					+ "dummy AS (SELECT 0 AS totalCount)\n"
					+ "SELECT COALESCE(tc.totalCount, d.totalCount) AS cultureCount FROM dummy d LEFT JOIN (SELECT totalCount FROM total_counts LIMIT 1) tc ON TRUE;";
			pstm = DB.prepareStatement(sql.toString(), null);
			pstm.setInt(1, clientId);
			rs = pstm.executeQuery();
			while (rs.next()) {
				cultureCount = rs.getInt("cultureCount");
//				getTCTRoleWorkLogResponse.setCultureTotalCount(cultureTotalCount);
			}
			closeDbCon(pstm, rs);
			pstm = null;
			rs = null;
			String sql2 = "SELECT \n" + "((SELECT count(*) FROM adempiere.tc_culturelabel WHERE ad_client_id = ? AND DATE(created) = CURRENT_DATE) +\n"
					+ "(SELECT count(*) FROM adempiere.tc_explantlabel WHERE ad_client_id = ? AND DATE(created) = CURRENT_DATE) +\n"
					+ "(SELECT count(*) FROM adempiere.tc_primaryhardeninglabel WHERE ad_client_id = ? AND DATE(created) = CURRENT_DATE) +\n"
					+ "(SELECT count(*) FROM adempiere.tc_secondaryhardeninglabel WHERE ad_client_id = ? AND DATE(created) = CURRENT_DATE)) AS total_count;";
			pstm = DB.prepareStatement(sql2, null);
			pstm.setInt(1, clientId);
			pstm.setInt(2, clientId);
			pstm.setInt(3, clientId);
			pstm.setInt(4, clientId);
			rs = pstm.executeQuery();

			if (!rs.isBeforeFirst()) {
				getTCTRoleWorkLogResponse.setCompletedLabelCountToday(0);
			}

			while (rs.next()) {
				int cultureLabelCount = rs.getInt("total_count");
				getTCTRoleWorkLogResponse.setCompletedLabelCountToday(cultureLabelCount);
			}
			closeDbCon(pstm, rs);
			pstm = null;
			rs = null;
			String sql3 = "WITH subquery AS (\n"
					+ "SELECT DISTINCT i.tc_in_id as id, i.m_product_id AS productId,pr.name As productName,\n"
					+ "(SELECT COUNT(*) FROM adempiere.tc_out o WHERE o.tc_in_id = i.tc_in_id LIMIT 1) AS query_column\n"
					+ "FROM adempiere.tc_in i JOIN adempiere.tc_plantdetails pd ON pd.planttaguuid = i.parentuuid JOIN adempiere.tc_variety v ON v.tc_variety_id = pd.tc_variety_id\n"
					+ "JOIN adempiere.tc_collectionjoinplant cp ON cp.tc_plantdetails_id = pd.tc_plantdetails_id\n"
					+ "JOIN adempiere.m_product pr ON pr.m_product_id = i.m_product_id WHERE i.ad_client_id = ?)\n"
					+ "SELECT COUNT(*) FROM subquery WHERE subquery.productName = 'Plant Tag' AND query_column = 0;";
			pstm = DB.prepareStatement(sql3, null);
			pstm.setInt(1, clientId);
			rs = pstm.executeQuery();
			if (!rs.isBeforeFirst()) {
				explantQty = 0;
			}

			while (rs.next()) {
				explantQty = rs.getInt("count");
			}
			closeDbCon(pstm, rs);

			pstm = null;
			rs = null;
			String sql4 = "WITH subquery AS(SELECT i.tc_in_id as id,i.m_product_id As productId,pr.description As productName,i.primarycheck = 'N',(SELECT COUNT(*) FROM adempiere.tc_out o \n"
					+ "WHERE o.tc_in_id = i.tc_in_id LIMIT 1)As query_column FROM adempiere.tc_in i "
					+ "JOIN adempiere.tc_out o ON o.c_uuid = i.parentuuid \n"
					+ "JOIN adempiere.tc_culturelabel cl ON cl.tc_out_id = o.tc_out_id\n"
					+ "JOIN adempiere.tc_variety v ON v.tc_variety_id = cl.tc_variety_id\n"
					+ "JOIN adempiere.m_product pr ON pr.m_product_id = i.m_product_id WHERE i.ad_client_id = ? AND i.primarycheck = 'N' AND cl.isdiscarded = 'N' )\n"
					+ "SELECT COUNT(*) As count FROM subquery WHERE subquery.productName ='Rooting' AND query_column = 0;";
			pstm = DB.prepareStatement(sql4, null);
			pstm.setInt(1, clientId);
			rs = pstm.executeQuery();
			if (!rs.isBeforeFirst()) {
				primaryHQty = 0;
			}
			while (rs.next()) {
				primaryHQty = rs.getInt("count");
			}
			closeDbCon(pstm, rs);

			pstm = null;
			rs = null;
			String sql5 = "WITH subquery AS(SELECT i.tc_in_id as id,i.m_product_id As productId,pr.name As productName,(SELECT COUNT(*) FROM adempiere.tc_out o WHERE o.tc_in_id = i.tc_in_id LIMIT 1)As query_column FROM adempiere.tc_in i "
					+ "JOIN adempiere.tc_out o ON o.c_uuid = i.parentuuid \n"
					+ "JOIN adempiere.tc_primaryhardeninglabel cl ON cl.tc_out_id = o.tc_out_id\n"
					+ "JOIN adempiere.tc_variety v ON v.tc_variety_id = cl.tc_variety_id\n"
					+ "JOIN adempiere.m_product pr ON pr.m_product_id = i.m_product_id WHERE i.ad_client_id = ?)\n"
					+ "SELECT COUNT(*) as count FROM subquery WHERE subquery.productName ='H01' AND query_column = 0;";
			pstm = DB.prepareStatement(sql5, null);
			pstm.setInt(1, clientId);
			rs = pstm.executeQuery();
			if (!rs.isBeforeFirst()) {
				secondaryHQty = 0;
			}
			while (rs.next()) {
				secondaryHQty = rs.getInt("count");
			}
			closeDbCon(pstm, rs);

			finalQty = explantQty + cultureCount + primaryHQty + secondaryHQty;
			getTCTRoleWorkLogResponse.setTotalCount(finalQty);
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			getTCTRoleWorkLogResponse.setError(INTERNAL_SERVER_ERROR);
			getTCTRoleWorkLogResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return getTCTRoleWorkLogResponseDocument;
	}

	@Override
	public GetTCTRoleWorkLogDetailsRecordsResponseDocument getTCTRoleWorkLogDetailsRecords(
			GetTCTRoleWorkLogDetailsRecordsRequestDocument req) {
		GetTCTRoleWorkLogDetailsRecordsResponseDocument getTCTRoleWorkLogDetailsRecordsResponseDocument = GetTCTRoleWorkLogDetailsRecordsResponseDocument.Factory
				.newInstance();
		GetTCTRoleWorkLogDetailsRecordsResponse getTCTRoleWorkLogDetailsRecordsResponse = getTCTRoleWorkLogDetailsRecordsResponseDocument
				.addNewGetTCTRoleWorkLogDetailsRecordsResponse();
		Trx trx = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
		int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			String sql = "WITH subquery1 AS (\n"
					+ "SELECT DISTINCT ps.codeno || ' ' || v.codeno || ' ' || Date(cp.created) AS labelCode,pr.name AS stage,Date(cp.created) AS manufacturingDate,\n"
					+ "null::Date AS expiryDate,l.x AS room,l.y AS rack,l.z AS columns,i.quantity AS count,i.tc_in_id AS inId,\n"
					+ "'Explant Label' AS label,(SELECT COUNT(*) FROM adempiere.tc_out o WHERE o.tc_in_id = i.tc_in_id LIMIT 1) AS query_column\n"
					+ "FROM adempiere.tc_in i JOIN adempiere.tc_plantdetails pd ON pd.planttaguuid = i.parentuuid\n"
					+ "JOIN adempiere.tc_collectionjoinplant cp ON cp.tc_plantdetails_id = pd.tc_plantdetails_id JOIN adempiere.tc_plantspecies ps ON ps.tc_plantspecies_id = pd.tc_species_id\n"
					+ "JOIN adempiere.tc_variety v ON v.tc_variety_id = pd.tc_variety_id JOIN adempiere.m_locator l ON i.m_locator_id = l.m_locator_id \n"
					+ "JOIN adempiere.m_product pr ON pr.m_product_id = i.m_product_id WHERE i.ad_client_id = ?),\n" + "subquery2 AS (\n"
					+ "SELECT ps.codeno || ' ' || v.codeno || ' ' || cl.parentcultureline || ' ' || Date(cl.created) AS labelCode,\n"
					+ "pr.description || ' - ' || cl.cycleno AS stage,Date(cl.created) AS manufacturingDate,Date(cl.created) AS expiryDate,\n"
					+ "l.x AS room,l.y AS rack,l.z AS columns,i.quantity AS count,i.tc_in_id AS inId,'Culture Label' AS label,\n"
					+ "(SELECT COUNT(*) FROM adempiere.tc_out o WHERE o.tc_in_id = i.tc_in_id LIMIT 1) AS query_column FROM adempiere.tc_in i\n"
					+ "JOIN adempiere.tc_out o ON o.c_uuid = i.parentuuid JOIN adempiere.tc_culturelabel cl ON cl.tc_out_id = o.tc_out_id\n"
					+ "JOIN adempiere.tc_plantspecies ps ON ps.tc_plantspecies_id = cl.tc_species_id JOIN adempiere.tc_variety v ON v.tc_variety_id = cl.tc_variety_id\n"
					+ "JOIN adempiere.m_locator l ON i.m_locator_id = l.m_locator_id JOIN adempiere.m_product pr ON pr.m_product_id = i.m_product_id \n"
					+ "WHERE i.ad_client_id = ? AND cl.isdiscarded = 'N' AND pr.description = 'Rooting' AND i.primarycheck = 'N'),\n"
					+ "subquery3 AS (\n"
					+ "SELECT ps.codeno || ' ' || v.codeno || ' ' || cl.parentcultureline || ' ' || Date(cl.created) AS labelCode,\n"
					+ "pr.name AS stage,Date(cl.created) AS manufacturingDate,null::Date AS expiryDate,\n"
					+ "l.x AS room,l.y AS rack,l.z AS columns,i.quantity AS count,i.tc_in_id AS inId,'Primary Hardening Label' AS label,\n"
					+ "(SELECT COUNT(*) FROM adempiere.tc_out o WHERE o.tc_in_id = i.tc_in_id LIMIT 1) AS query_column FROM adempiere.tc_in i\n"
					+ "JOIN adempiere.tc_out o ON o.c_uuid = i.parentuuid JOIN adempiere.tc_primaryhardeninglabel cl ON cl.tc_out_id = o.tc_out_id\n"
					+ "JOIN adempiere.tc_plantspecies ps ON ps.tc_plantspecies_id = cl.tc_species_id JOIN adempiere.tc_variety v ON v.tc_variety_id = cl.tc_variety_id\n"
					+ "JOIN adempiere.m_locator l ON i.m_locator_id = l.m_locator_id JOIN adempiere.m_product pr ON pr.m_product_id = i.m_product_id WHERE i.ad_client_id = ?),\n" + "subquery4 AS (\n"
					+ "SELECT ps.codeno || ' ' || v.codeno || ' ' || cl.parentcultureline || ' ' || Date(cl.culturedate) || ' ' || ns.codeno AS labelCode,cs.name || ' - ' || cl.cycleno AS stage,\n"
					+ "DATE(cl.created) AS manufacturingDate,DATE(cl.created + (cs.period::int * INTERVAL '1 day')) AS expiryDate,l.x AS room,l.y AS rack,l.z AS columns,\n"
					+ "COUNT(*) AS count,(SELECT i.tc_in_id FROM adempiere.tc_in i WHERE i.parentuuid = o.c_uuid LIMIT 1) AS inId,\n"
					+ "'Culture Label' AS label,(SELECT COUNT(*) FROM adempiere.tc_culturelabel cll WHERE cll.parentuuid = cl.c_uuid LIMIT 1) AS subquery_column\n"
					+ "FROM adempiere.tc_culturelabel cl JOIN adempiere.tc_plantspecies ps ON ps.tc_plantspecies_id = cl.tc_species_id\n"
					+ "JOIN adempiere.tc_variety v ON v.tc_variety_id = cl.tc_variety_id JOIN adempiere.tc_naturesample ns ON ns.tc_naturesample_id = cl.tc_naturesample_id\n"
					+ "JOIN adempiere.tc_in i ON i.tc_in_id = cl.tc_in_id JOIN adempiere.tc_out o ON o.tc_out_id = cl.tc_out_id\n"
					+ "JOIN adempiere.m_locator l ON l.m_locator_id = o.m_locator_id JOIN adempiere.tc_culturestage cs ON cs.tc_culturestage_id = cl.tc_culturestage_id\n"
					+ "WHERE cl.ad_client_id = ? AND cl.isdiscarded = 'N' AND cl.tosubculturecheck = 'Y' AND cl.c_uuid IS NOT NULL AND cl.parentuuid IS NOT NULL\n"
					+ "GROUP BY ps.codeno, v.codeno, cl.parentcultureline, cl.culturedate, ns.codeno, cs.name, cl.cycleno,cl.c_uuid, cl.created, l.x, l.y, l.z, o.c_uuid,cs.period)\n"
					+ "SELECT * FROM subquery1 WHERE subquery1.stage = 'Plant Tag' AND query_column = 0\n"
					+ "UNION ALL\n"
					+ "SELECT * FROM subquery4 WHERE subquery_column = 0 AND expiryDate <= current_date AND subquery4.stage != 'Rooting - 2'\n"
					+ "UNION ALL\n" + "SELECT * FROM subquery2 WHERE  query_column = 0\n" + "UNION ALL\n"
					+ "SELECT * FROM subquery3 WHERE subquery3.stage = 'H01' AND query_column = 0;";

			pstm = DB.prepareStatement(sql.toString(), null);
			pstm.setInt(1, clientId);
			pstm.setInt(2, clientId);
			pstm.setInt(3, clientId);
			pstm.setInt(4, clientId);
			rs = pstm.executeQuery();

			if (!rs.isBeforeFirst()) {
				getTCTRoleWorkLogDetailsRecordsResponse.setIsError(false);
				getTCTRoleWorkLogDetailsRecordsResponse.addNewGetCultureListRecords();
				return getTCTRoleWorkLogDetailsRecordsResponseDocument;
			}

			while (rs.next()) {
				GetCultureListRecords data = getTCTRoleWorkLogDetailsRecordsResponse.addNewGetCultureListRecords();
				String labelCode = rs.getString("labelCode");
				String stage = rs.getString("stage");
				String manufacturingDate = rs.getString("manufacturingDate");
				String expiryDate = rs.getString("expiryDate");
				String room = rs.getString("room");
				String rack = rs.getString("rack");
				String columns = rs.getString("columns");
				String count = rs.getString("count");
				int tcId = rs.getInt("inId");
				String labelName = rs.getString("label");

				data.setLabelCode(labelCode);
				data.setStage(stage);
				data.setManufacturingDate(manufacturingDate);
				data.setExpiryDate(expiryDate != null ? expiryDate : "");
				data.setRoom(room);
				data.setRack(rack);
				data.setColumns(columns);
				data.setCount(count);
				data.setInId(tcId);
				data.setLabel(labelName);
			}
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			getTCTRoleWorkLogDetailsRecordsResponse.setError(INTERNAL_SERVER_ERROR);
			getTCTRoleWorkLogDetailsRecordsResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return getTCTRoleWorkLogDetailsRecordsResponseDocument;
	}

	@Override
	public GetMTRoleWorkLogResponseDocument getMTRoleWorkLog(GetMTRoleWorkLogRequestDocument req) {
		GetMTRoleWorkLogResponseDocument getMTRoleWorkLogResponseDocument = GetMTRoleWorkLogResponseDocument.Factory
				.newInstance();
		GetMTRoleWorkLogResponse getMTRoleWorkLogResponse = getMTRoleWorkLogResponseDocument
				.addNewGetMTRoleWorkLogResponse();
		Trx trx = null;
		PreparedStatement pstm = null, pstm1 = null, pstm2 = null;
		ResultSet rs = null, rs1 = null, rs2 = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
		int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			String sql = "SELECT \n" + "    mo.tc_mediaorder_id,\n" + "    mo.quantity,\n" + "    mo.m_product_id,\n"
					+ "    mo.ad_user_id,\n" + "    COALESCE(sub.total_quantity, 0) AS total_quantity,\n"
					+ "    mo.quantity - COALESCE(sub.total_quantity, 0) AS actualQty,\n"
					+ "    SUM(mo.quantity - COALESCE(sub.total_quantity, 0)) OVER () AS totalActualQty\n" + "FROM \n"
					+ "    adempiere.tc_mediaorder mo\n" + "LEFT JOIN (\n" + "    SELECT \n"
					+ "        ml.tc_mediaorder_id,\n" + "        ml.m_product_id,\n"
					+ "        SUM(ml.quantity) AS total_quantity\n" + "    FROM \n"
					+ "        adempiere.tc_medialine ml\n" + "    GROUP BY \n"
					+ "        ml.tc_mediaorder_id, ml.m_product_id\n"
					+ ") sub ON mo.tc_mediaorder_id = sub.tc_mediaorder_id AND mo.m_product_id = sub.m_product_id\n"
					+ "WHERE \n" + "    mo.ad_client_id = ? AND mo.quantity IS NOT NULL limit 1;";

			pstm = DB.prepareStatement(sql.toString(), null);
			pstm.setInt(1, clientId);
			rs = pstm.executeQuery();
			while (rs.next()) {
				int mediumLabelCount = rs.getInt("totalActualQty");
				getMTRoleWorkLogResponse.setMediumLabelsCount(mediumLabelCount);
			}
			closeDbCon(pstm, rs);
			String sql2 = "SELECT COUNT(*) As count FROM adempiere.tc_medialabelQr\n" + "WHERE ad_client_id = ? AND isdiscarded = 'Y' AND Date(updated) = Date(CURRENT_DATE);";
			pstm2 = DB.prepareStatement(sql2.toString(), null);
			pstm2.setInt(1, clientId);
			rs2 = pstm2.executeQuery();
			while (rs2.next()) {
				int discardCount = rs2.getInt("count");
				getMTRoleWorkLogResponse.setDiscardCount(discardCount);
			}
			String sql3 = "SELECT mo.tc_mediaorder_id AS id,mo.quantity,mo.m_product_id,mo.ad_user_id,sub.mediaType AS mediaType,DATE(sub.mltiming) AS date,sub.lineId AS lineId,\n"
					+ "COALESCE(sub.total_quantity, 0) AS lineQty,SUM(COALESCE(sub.total_quantity, 0)) OVER () AS lineTotalQty,mo.quantity - COALESCE(sub.total_quantity, 0) AS actualQty,\n"
					+ "SUM(mo.quantity - COALESCE(sub.total_quantity, 0)) OVER () AS totalActualQt FROM adempiere.tc_mediaorder mo\n"
					+ "LEFT JOIN \n"
					+ "(SELECT ml.tc_mediaorder_id,ml.m_product_id,SUM(ml.quantity) AS total_quantity,ml.tc_medialine_id AS lineId,ml.updated AS mltiming,pr.description AS mediaType\n"
					+ "FROM adempiere.tc_medialine ml JOIN adempiere.m_product pr ON pr.m_product_id = ml.m_product_id\n"
					+ "GROUP BY ml.tc_mediaorder_id,ml.m_product_id,ml.updated,ml.tc_medialine_id,pr.description) sub \n"
					+ "ON mo.tc_mediaorder_id = sub.tc_mediaorder_id AND mo.m_product_id = sub.m_product_id \n"
					+ "WHERE mo.ad_client_id = ? AND DATE(sub.mltiming) = CURRENT_DATE AND mo.quantity IS NOT NULL\n"
					+ "GROUP BY mo.tc_mediaorder_id,mo.quantity,mo.m_product_id,mo.ad_user_id,sub.mediaType,sub.total_quantity,sub.mltiming,sub.lineId LIMIT 1;";
			pstm1 = DB.prepareStatement(sql3.toString(), null);
			pstm1.setInt(1, clientId);
			rs1 = pstm1.executeQuery();
			if (!rs1.isBeforeFirst()) {
				getMTRoleWorkLogResponse.setCompletedCount(0);
			}
			while (rs1.next()) {
				int completedCount = rs1.getInt("lineTotalQty");
				getMTRoleWorkLogResponse.setCompletedCount(completedCount);
			}
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			getMTRoleWorkLogResponse.setError(INTERNAL_SERVER_ERROR);
			getMTRoleWorkLogResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			closeDbCon(pstm1, rs1);
			closeDbCon(pstm2, rs2);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return getMTRoleWorkLogResponseDocument;
	}

	@Override
	public GetMTRoleWorkLogDetailsResponseDocument getMTRoleWorkLogDetails(GetMTRoleWorkLogDetailsRequestDocument req) {
		GetMTRoleWorkLogDetailsResponseDocument getMTRoleWorkLogResponseDetailsDocument = GetMTRoleWorkLogDetailsResponseDocument.Factory
				.newInstance();
		GetMTRoleWorkLogDetailsResponse getMTRoleWorkLogDetailsResponse = getMTRoleWorkLogResponseDetailsDocument
				.addNewGetMTRoleWorkLogDetailsResponse();
		Trx trx = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
		int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			String sql = "SELECT \n" + "    mo.tc_mediaorder_id As mediaOrderId,pr.description As mediaType,\n"
					+ "    mo.quantity AS quantity,\n" + "    mo.m_product_id,\n"
					+ "    mo.ad_user_id,Date(mo.created) As date,\n"
					+ "    COALESCE(sub.total_quantity, 0) AS total_quantity,\n"
					+ "    mo.quantity - COALESCE(sub.total_quantity, 0) AS actualQty\n" + "FROM \n"
					+ "    adempiere.tc_mediaorder mo\n"
					+ "JOIN adempiere.m_product pr On pr.m_product_id = mo.m_product_id	\n" + "LEFT JOIN (\n"
					+ "    SELECT \n" + "        ml.tc_mediaorder_id,\n" + "        ml.m_product_id,\n"
					+ "        SUM(ml.quantity) AS total_quantity\n" + "    FROM \n"
					+ "        adempiere.tc_medialine ml\n" + "    GROUP BY \n"
					+ "        ml.tc_mediaorder_id, ml.m_product_id\n"
					+ ") sub ON mo.tc_mediaorder_id = sub.tc_mediaorder_id AND mo.m_product_id = sub.m_product_id\n"
					+ "WHERE \n" + "    mo.ad_client_id = ? AND mo.quantity IS NOT NULL AND mo.quantity - COALESCE(sub.total_quantity, 0) > 0 ORDER BY mo.tc_mediaorder_id;";

			pstm = DB.prepareStatement(sql.toString(), null);
			pstm.setInt(1, clientId);
			rs = pstm.executeQuery();

			if (!rs.isBeforeFirst()) {
				getMTRoleWorkLogDetailsResponse.setIsError(false);
				getMTRoleWorkLogDetailsResponse.addNewGetMediumRecords();
				return getMTRoleWorkLogResponseDetailsDocument;
			}

			while (rs.next()) {
				GetMediumRecords records = getMTRoleWorkLogDetailsResponse.addNewGetMediumRecords();
				String mediaType = rs.getString("mediaType");
				String date = rs.getString("date");
				int actualQty = rs.getInt("actualQty");
				int mediaOrderId = rs.getInt("mediaOrderId");

				records.setMediaType(mediaType);
				records.setDate(date);
				records.setCount(actualQty);
				records.setMediaOrderId(mediaOrderId);
			}
			closeDbCon(pstm, rs);
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			getMTRoleWorkLogDetailsResponse.setError(INTERNAL_SERVER_ERROR);
			getMTRoleWorkLogDetailsResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return getMTRoleWorkLogResponseDetailsDocument;
	}

	@Override
	public GetTokenValidationResponseDocument getTokenValidation(int userId, String token) {
		GetTokenValidationResponseDocument getTokenValidationResponseDocument = GetTokenValidationResponseDocument.Factory
				.newInstance();
		GetTokenValidationResponse getTokenValidationResponse = getTokenValidationResponseDocument
				.addNewGetTokenValidationResponse();
		Trx trx = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			
			String fullURL = httpServletRequest.getRequestURL().toString();
			String contextPath = httpServletRequest.getContextPath(); // gives `/tcapi` if hit via tcapi
			String baseURL = fullURL.substring(0, fullURL.indexOf(contextPath) + contextPath.length());
			String publicBaseURLVerify = baseURL.replaceFirst("/ADInterface.*", "/tcapi");
			
			TokenVerificationService tokenVerificationService = new TokenVerificationService();
			Boolean tokenValid = tokenVerificationService.verifyToken(userId, token, publicBaseURLVerify);
			if (tokenValid) {
				getTokenValidationResponse.setTokenMessage("Token is Valid");
			} else {
				getTokenValidationResponse.setTokenMessage("Token is Invalid");
			}
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			getTokenValidationResponse.setError(INTERNAL_SERVER_ERROR);
			getTokenValidationResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return getTokenValidationResponseDocument;
	}

	@Override
	public UpdateUserPasswordResponseDocument updateUserPassword(UpdateUserPasswordRequestDocument req) {
		UpdateUserPasswordResponseDocument updateUserPasswordResponseDocument = UpdateUserPasswordResponseDocument.Factory
				.newInstance();
		UpdateUserPasswordResponse updateUserPasswordResponse = updateUserPasswordResponseDocument
				.addNewUpdateUserPasswordResponse();
		UpdateUserPasswordRequest loginRequest = req.getUpdateUserPasswordRequest();
		String password = safeTrim(loginRequest.getPassword());
		String userName = safeTrim(loginRequest.getUserName());
		String token = safeTrim(loginRequest.getToken());
		PreparedStatement pstm = null;
		ResultSet rs = null;
		int userId = 0;
		Trx trx = null;
		String publicBaseURL = "";
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			String fullURL = httpServletRequest.getRequestURL().toString();
			String contextPath = httpServletRequest.getContextPath(); // gives `/tcapi` if hit via tcapi
			String baseURL = fullURL.substring(0, fullURL.indexOf(contextPath) + contextPath.length());
			String publicBaseURLVerify = baseURL.replaceFirst("/ADInterface.*", "/tcapi");
			
			String sql = "select ad_user_id As id from adempiere.ad_user\n" + "where name = ?;";
			pstm = DB.prepareStatement(sql, null);
			pstm.setString(1, userName);
			rs = pstm.executeQuery();
			while (rs.next()) {
				userId = rs.getInt("id");
			}
			if (userId == 0) {
				updateUserPasswordResponse.setError("userName is not Valid, Please check userName " + userName);
				updateUserPasswordResponse.setIsError(true);
				return updateUserPasswordResponseDocument;
			}
			
			if (containsMaliciousPattern(password)) {
				updateUserPasswordResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				updateUserPasswordResponse.setIsError(true);
			    return updateUserPasswordResponseDocument;
			}
			
			if (containsMaliciousPattern(token)) {
				updateUserPasswordResponse.setError("Invalid input: Base64 or URL-encoded text not allowed");
				updateUserPasswordResponse.setIsError(true);
			    return updateUserPasswordResponseDocument;
			}
			
			if(token == "") {
				updateUserPasswordResponse.setError("Please enter the Token");
				updateUserPasswordResponse.setIsError(true);
				return updateUserPasswordResponseDocument;
			}
			TokenVerificationService tokenVerificationService = new TokenVerificationService();
			Boolean tokenValid = tokenVerificationService.verifyToken(userId, token,publicBaseURLVerify);
			if (!tokenValid) {
				updateUserPasswordResponse.setError("Token is Invalid");
				updateUserPasswordResponse.setIsError(true);
				return updateUserPasswordResponseDocument;
			}
			publicBaseURL = baseURL.replaceFirst("/ADInterface.*", "/webui");
			
			MUser user = new MUser(ctx, userId, trx.getTrxName());
			user.setPassword(password);
			user.saveEx();
			trx.commit();
			String updateSQL = "UPDATE AD_User SET token=?, tokentime=? WHERE AD_User_ID=?";
			DB.executeUpdate(updateSQL, new Object[] { "", "", user.getAD_User_ID() }, false, null);
			trx.commit();
			updateUserPasswordResponse.setWebUrl(publicBaseURL);
			updateUserPasswordResponse.setStatus("Password Update Successfully");
			updateUserPasswordResponse.setIsError(false);
			closeDbCon(pstm, rs);
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			e.printStackTrace();
			updateUserPasswordResponse.setError(INTERNAL_SERVER_ERROR);
			updateUserPasswordResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return updateUserPasswordResponseDocument;
	}

	@Override
	public GetVillageNameListResponseDocument getVillageNameList(GetVillageNameListRequestDocument req) {
		GetVillageNameListResponseDocument getVillageNameListResponseDocument = GetVillageNameListResponseDocument.Factory
				.newInstance();
		GetVillageNameListResponse getVillageNameListResponse = getVillageNameListResponseDocument
				.addNewGetVillageNameListResponse();
		Trx trx = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
		int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			String sql = "SELECT villagename2 As villagename FROM adempiere.tc_farmer WHERE ad_client_id = ? AND villagename2 IS NOT NULL\n" + "GROUP BY villagename2 HAVING COUNT(*) > 1\n" + "UNION ALL \n"
					+ "SELECT villagename2 As villagename FROM adempiere.tc_farmer WHERE ad_client_id = ? AND villagename2 IS NOT NULL\n"
					+ "GROUP BY villagename2 HAVING COUNT(*) = 1 ORDER BY villagename;";

			pstm = DB.prepareStatement(sql.toString(), null);
			pstm.setInt(1, clientId);
			pstm.setInt(2, clientId);
			rs = pstm.executeQuery();
			while (rs.next()) {
				GetVillageNameList data = getVillageNameListResponse.addNewGetVillageNameList();
				String villageName = rs.getString("villagename");
				data.setVillageName(villageName);
			}
			closeDbCon(pstm, rs);
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			getVillageNameListResponse.setError(INTERNAL_SERVER_ERROR);
			getVillageNameListResponse.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return getVillageNameListResponseDocument;
	}

	@Override
	public Response getCityNameList(GetCityNameListRequestDocument req) {
		GetCityNameListResponseDocument getCityNameListResponseDocument = GetCityNameListResponseDocument.Factory
				.newInstance();
		GetCityNameListResponse response = getCityNameListResponseDocument.addNewGetCityNameListResponse();
		Trx trx = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		JSONObject jsonObject = new JSONObject();
		List<String> cityNames = new ArrayList<>();
		List<String> villageNames = new ArrayList<>();
		List<String> districtNames = new ArrayList<>();
		List<String> farmerNames = new ArrayList<>();
		List<String> stateNames = new ArrayList<>();
		List<String> pincode = new ArrayList<>();
		List<String> visitName = new ArrayList<>();
		List<String> statusNames = new ArrayList<>();
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
			int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();

			String sql = "SELECT DISTINCT TRIM(city) AS cityName FROM adempiere.tc_farmer\n" + "WHERE ad_client_id = ? AND city IS NOT NULL ORDER BY cityName;";
			pstm = DB.prepareStatement(sql.toString(), null);
			pstm.setInt(1, clientId);
			rs = pstm.executeQuery();

			while (rs.next()) {
				String cityName = rs.getString("cityName").trim();
				cityNames.add(cityName);
			}

			closeDbCon(pstm, rs);
			pstm = null;
			rs = null;
			String villageSql = "SELECT DISTINCT TRIM(villagename2) AS villageName FROM adempiere.tc_farmer\n"
					+ "WHERE ad_client_id = ? AND villagename2 IS NOT NULL ORDER BY villageName;";
			pstm = DB.prepareStatement(villageSql.toString(), null);
			pstm.setInt(1, clientId);
			rs = pstm.executeQuery();
			while (rs.next()) {
				String villageName = rs.getString("villageName").trim();
				villageNames.add(villageName);
			}

			closeDbCon(pstm, rs);
			pstm = null;
			rs = null;

			String districtSql = "SELECT DISTINCT TRIM(district) AS districtName FROM adempiere.tc_farmer\n"
					+ "WHERE ad_client_id = ? AND district IS NOT NULL ORDER BY districtName;";
			pstm = DB.prepareStatement(districtSql.toString(), null);
			pstm.setInt(1, clientId);
			rs = pstm.executeQuery();
			while (rs.next()) {
				String districtName = rs.getString("districtName").trim();
				districtNames.add(districtName);
			}

			closeDbCon(pstm, rs);
			pstm = null;
			rs = null;

			String stateSql = "SELECT DISTINCT TRIM(state) AS stateName FROM adempiere.tc_farmer\n"
					+ "WHERE ad_client_id = ? AND state IS NOT NULL ORDER BY stateName;";
			pstm = DB.prepareStatement(stateSql.toString(), null);
			pstm.setInt(1, clientId);
			rs = pstm.executeQuery();
			while (rs.next()) {
				String stateName = rs.getString("stateName").trim();
				stateNames.add(stateName);
			}

			closeDbCon(pstm, rs);
			pstm = null;
			rs = null;

			String farmerSql = "SELECT DISTINCT TRIM(name) AS farmerName FROM adempiere.tc_farmer\n"
					+ "WHERE ad_client_id = ? AND name IS NOT NULL ORDER BY farmerName;";
			pstm = DB.prepareStatement(farmerSql.toString(), null);
			pstm.setInt(1, clientId);
			rs = pstm.executeQuery();
			while (rs.next()) {
				String farmerName = rs.getString("farmerName").trim();
				farmerNames.add(farmerName);
			}

			closeDbCon(pstm, rs);
			pstm = null;
			rs = null;

			String pincodeSql = "SELECT DISTINCT TRIM(pincode) AS pincode FROM adempiere.tc_farmer\n"
					+ "WHERE ad_client_id = ? AND pincode IS NOT NULL ORDER BY pincode;";
			pstm = DB.prepareStatement(pincodeSql.toString(), null);
			pstm.setInt(1, clientId);
			rs = pstm.executeQuery();
			while (rs.next()) {
				String pinCode = rs.getString("pincode").trim();
				pincode.add(pinCode);
			}

			closeDbCon(pstm, rs);
			pstm = null;
			rs = null;

			String visitTypeSql = "SELECT DISTINCT TRIM(name) AS visitName, tc_visittype_id FROM adempiere.tc_visittype\n"
					+ "WHERE ad_client_id = ? AND name IS NOT NULL ORDER BY tc_visittype_id;";
			pstm = DB.prepareStatement(visitTypeSql.toString(), null);
			pstm.setInt(1, clientId);
			rs = pstm.executeQuery();
			while (rs.next()) {
				String visitNames = rs.getString("visitName").trim();
				visitName.add(visitNames);
			}

			closeDbCon(pstm, rs);
			pstm = null;
			rs = null;

			String statusSql = "SELECT DISTINCT TRIM(name) AS statusName, tc_status_id FROM adempiere.tc_status\n"
					+ "WHERE ad_client_id = ? AND name IS NOT NULL ORDER BY tc_status_id;";
			pstm = DB.prepareStatement(statusSql.toString(), null);
			pstm.setInt(1, clientId);
			rs = pstm.executeQuery();
			while (rs.next()) {
				String statusName = rs.getString("statusName").trim();
				statusNames.add(statusName);
			}

			jsonObject.put("Farmer Name", farmerNames);
			jsonObject.put("Visit Type", visitName);
			jsonObject.put("State", stateNames);
			jsonObject.put("City", cityNames);
			jsonObject.put("District", districtNames);
			jsonObject.put("Village", villageNames);
			jsonObject.put("Pincode", pincode);
			jsonObject.put("Status", statusNames);

			closeDbCon(pstm, rs);
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			response.setError(INTERNAL_SERVER_ERROR);
			response.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		String jsonString = jsonObject.toString();
		return Response.ok(jsonString).type("application/json").build();
	}

	@Override
	public GetTCMediaOrderListResponseDocument getTCMediaOrderList(GetTCMediaOrderListRequestDocument req) {
		GetTCMediaOrderListResponseDocument getTCMediaOrderListResponseDocument = GetTCMediaOrderListResponseDocument.Factory
				.newInstance();
		GetTCMediaOrderListResponse response = getTCMediaOrderListResponseDocument.addNewGetTCMediaOrderListResponse();
		GetTCMediaOrderListRequest loginRequest = req.getGetTCMediaOrderListRequest();
		String mediaType = safeTrim(loginRequest.getMediaType());
		Trx trx = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
		int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			String sql = "SELECT mo.tc_mediaorder_id AS mediaOrderId,pr.description AS mediaType,mo.quantity AS totalQty,\n"
					+ "mo.m_product_id,mo.ad_user_id,DATE(mo.created) AS date,COALESCE(sub.total_quantity, 0) AS lineQty,\n"
					+ "mo.quantity - COALESCE(sub.total_quantity, 0) AS actualQty FROM adempiere.tc_mediaorder mo\n"
					+ "JOIN adempiere.m_product pr ON pr.m_product_id = mo.m_product_id\n"
					+ "LEFT JOIN (SELECT ml.tc_mediaorder_id,ml.m_product_id,SUM(ml.quantity) AS total_quantity\n"
					+ "FROM adempiere.tc_medialine ml GROUP BY ml.tc_mediaorder_id, ml.m_product_id) sub \n"
					+ "ON mo.tc_mediaorder_id = sub.tc_mediaorder_id AND mo.m_product_id = sub.m_product_id\n"
					+ "WHERE mo.ad_client_id = ? AND mo.quantity IS NOT NULL AND pr.description = ?\n"
					+ "AND mo.quantity - COALESCE(sub.total_quantity, 0) > 0 ORDER BY mo.tc_mediaorder_id;";

			pstm = DB.prepareStatement(sql.toString(), null);
			pstm.setInt(1, clientId);
			pstm.setString(2, mediaType);
			
			rs = pstm.executeQuery();
			if (!rs.isBeforeFirst()) {
				response.setIsError(false);
				response.addNewGetTCMediaOrderList();
				return getTCMediaOrderListResponseDocument;
			}

			while (rs.next()) {
				GetTCMediaOrderList data = response.addNewGetTCMediaOrderList();
				int mediaOrderId = rs.getInt("mediaOrderId");
				int pendingCount = rs.getInt("actualQty");
				int totalCount = rs.getInt("totalQty");
				int lineCount = rs.getInt("lineQty");
				data.setMediaOrderID(mediaOrderId);
				data.setTotalCount(totalCount);
				data.setLineCount(lineCount);
				data.setPendingCount(pendingCount);
			}
			closeDbCon(pstm, rs);
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			response.setError(INTERNAL_SERVER_ERROR);
			response.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return getTCMediaOrderListResponseDocument;
	}

	@Override
	public CreateMediaOrderResponseDocument createMediaOrder(CreateMediaOrderRequestDocument req) {
		CreateMediaOrderResponseDocument createMediaOrderResponseDocument = CreateMediaOrderResponseDocument.Factory
				.newInstance();
		CreateMediaOrderResponse response = createMediaOrderResponseDocument.addNewCreateMediaOrderResponse();
		CreateMediaOrderRequest loginRequest = req.getCreateMediaOrderRequest();
		String mediaType = safeTrim(loginRequest.getMediaType());
		int qty = 0;
		Trx trx = null;
		int docTypeId = 0;
		int productId = 0;
		int userId = 0;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
			int clientId = Env.getAD_Client_ID(ctx);
			int warehouseId = Env.getContextAsInt(ctx, Env.M_WAREHOUSE_ID);
			int orgId = Env.getAD_Org_ID(ctx);
			String user = new MUser(ctx, Env.getAD_User_ID(ctx), null).getName();

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			qty = loginRequest.getQuantity();
			
			if (containsMaliciousPattern(mediaType)) {
				response.setError("Invalid input: Base64 or URL-encoded text not allowed");
				response.setIsError(true);
			    return createMediaOrderResponseDocument;
			}
			
			docTypeId = TCUtills.getDocTypeId(clientId, DOCUMENT_MEDIA_ORDER);
			productId = TCUtills.getProductIdthroughdescription(clientId, mediaType);
			userId = TCUtills.getUserId(clientId, user);

			Date currentDate = new Date();
			Timestamp currentTimestamp = new Timestamp(currentDate.getTime());

			TCMediaOrder order = new TCMediaOrder(ctx, 0, trx.getTrxName());
			order.setAD_Org_ID(orgId);
			order.setName("create new records");
			order.setDateOrdered(currentTimestamp);
			order.setM_Warehouse_ID(warehouseId);
			order.setQuantity(BigDecimal.valueOf(qty));
			order.setM_Product_ID(productId);
			order.setAD_User_ID(userId);
			order.setC_DocType_ID(0);
			order.setC_DocTypeTarget_ID(docTypeId);
			order.setDocStatus(DocAction.STATUS_Drafted);
			order.setDocAction(DocAction.STATUS_Drafted);
			order.saveEx();
			trx.commit();
			response.setMediaOrderId(order.getTC_mediaorder_ID());
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			response.setError(INTERNAL_SERVER_ERROR);
			response.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return createMediaOrderResponseDocument;
	}

	@Override
	public GetTCPFResponseDocument getTCPF(GetTCPFRequestDocument req) {
		GetTCPFResponseDocument getTCPFResponseDocument = GetTCPFResponseDocument.Factory.newInstance();
		GetTCPFResponse response = getTCPFResponseDocument.addNewGetTCPFResponse();
		Trx trx = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
		int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			String sql = "SELECT codeno FROM adempiere.tc_tcpf WHERE ad_client_id = ?;";

			pstm = DB.prepareStatement(sql.toString(), null);
			pstm.setInt(1, clientId);
			rs = pstm.executeQuery();

			if (!rs.isBeforeFirst()) {
				response.setIsError(false);
				response.addNewGetTCPFList();
				return getTCPFResponseDocument;
			}

			while (rs.next()) {
				GetTCPFList data = response.addNewGetTCPFList();
				String tcpf = rs.getString("codeno");
				data.setTcpf(tcpf);
			}
			closeDbCon(pstm, rs);
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			response.setError(INTERNAL_SERVER_ERROR);
			response.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return getTCPFResponseDocument;
	}

	@Override
	public GetPersonalCodeResponseDocument getPersonalCode(GetPersonalCodeRequestDocument req) {
		GetPersonalCodeResponseDocument getPersonalCodeResponseDocument = GetPersonalCodeResponseDocument.Factory
				.newInstance();
		GetPersonalCodeResponse response = getPersonalCodeResponseDocument.addNewGetPersonalCodeResponse();
		Trx trx = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
			String user = safeTrim(new MUser(ctx, Env.getAD_User_ID(ctx), null).getName());
		int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			String sql = "SELECT personalcode FROM adempiere.ad_user WHERE ad_client_id = ? AND name = ?";

			pstm = DB.prepareStatement(sql.toString(), null);
			pstm.setInt(1, clientId);
			pstm.setString(2, user);
			rs = pstm.executeQuery();

			if (!rs.isBeforeFirst()) {
				response.setIsError(false);
				response.addNewGetPersonalCodeList();
				return getPersonalCodeResponseDocument;
			}

			while (rs.next()) {
				GetPersonalCodeList data = response.addNewGetPersonalCodeList();
				String personalCode = rs.getString("personalcode");
				data.setPersoanlCode(personalCode);
			}
			closeDbCon(pstm, rs);
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			response.setError(INTERNAL_SERVER_ERROR);
			response.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return getPersonalCodeResponseDocument;
	}

	@Override
	public GetMachineCodeResponseDocument getMachineCode(GetMachineCodeRequestDocument req) {
		GetMachineCodeResponseDocument getMachineCodeResponseDocument = GetMachineCodeResponseDocument.Factory
				.newInstance();
		GetMachineCodeResponse response = getMachineCodeResponseDocument.addNewGetMachineCodeResponse();
		GetMachineCodeRequest loginRequest = req.getGetMachineCodeRequest();
		String mediaType = safeTrim(loginRequest.getMediaType());
		Trx trx = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
			int clientId = Env.getAD_Client_ID(ctx);
		
			if (containsMaliciousPattern(mediaType)) {
				response.setError("Invalid input: Base64 or URL-encoded text not allowed");
				response.setIsError(true);
		    return getMachineCodeResponseDocument;
			}
			
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			String sql = "SELECT name,codeno FROM adempiere.tc_machinetype WHERE ad_client_id = ? AND ismediatype = ?;";

			pstm = DB.prepareStatement(sql.toString(), null);
			pstm.setInt(1, clientId);
			pstm.setString(2, mediaType);
			rs = pstm.executeQuery();

			if (!rs.isBeforeFirst()) {
				response.setIsError(false);
				response.addNewGetMachineCodeList();
				return getMachineCodeResponseDocument;
			}

			while (rs.next()) {
				GetMachineCodeList data = response.addNewGetMachineCodeList();
				String machineCode = rs.getString("codeno");
				String name = rs.getString("name");
				data.setName(name);
				data.setMachineCode(machineCode);
			}
			closeDbCon(pstm, rs);
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			response.setError(INTERNAL_SERVER_ERROR);
			response.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return getMachineCodeResponseDocument;
	}

	@Override
	public GetMediaDiscardLabelDetailsResponseDocument getMediaDiscardLabelDetails(
			GetMediaDiscardLabelDetailsRequestDocument req) {
		GetMediaDiscardLabelDetailsResponseDocument getMediaDiscardLabelDetailsResponseDocument = GetMediaDiscardLabelDetailsResponseDocument.Factory
				.newInstance();
		GetMediaDiscardLabelDetailsResponse response = getMediaDiscardLabelDetailsResponseDocument
				.addNewGetMediaDiscardLabelDetailsResponse();
		Trx trx = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
		int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			String sql = "SELECT mt.name As mediatype,ml.tc_medialabelqr_id As id,ml.discarddate as date,ml.discardreason as reason,mdt.name As mediaTypeName,1 as count FROM adempiere.tc_medialabelQr ml\n"
					+ "JOIN adempiere.tc_mediatype mt ON mt.tc_mediatype_id = ml.tc_mediatype_id\n"
					+ "left JOIN adempiere.tc_mediadiscardtype mdt ON mdt.tc_mediadiscardtype_id = ml.tc_mediadiscardtype_id\n"
					+ "WHERE ml.ad_client_id = ? AND ml.isdiscarded = 'Y' AND Date(ml.updated) = Date(CURRENT_DATE) order by Date(ml.updated),mt.name;";

			pstm = DB.prepareStatement(sql.toString(), null);
			pstm.setInt(1, clientId);
			rs = pstm.executeQuery();

			if (!rs.isBeforeFirst()) {
				response.setIsError(false);
				response.addNewGetMediaDiscardLabelDetailsList();
				return getMediaDiscardLabelDetailsResponseDocument;
			}

			while (rs.next()) {
				GetMediaDiscardLabelDetailsList data = response.addNewGetMediaDiscardLabelDetailsList();
				String mediaType = rs.getString("mediatype");
				int id = rs.getInt("id");
				String date = rs.getString("date");
				String reason = rs.getString("reason");
				String discardType = rs.getString("mediaTypeName");
				int count = rs.getInt("count");
				data.setMediaType(mediaType);
				data.setMediaLabelId(id);
				data.setDate(date);
				data.setReason(reason != null ? reason : "");
				data.setDiscardType(discardType != null ? discardType : "");
				data.setCount(count);
			}
			closeDbCon(pstm, rs);
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			response.setError(INTERNAL_SERVER_ERROR);
			response.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return getMediaDiscardLabelDetailsResponseDocument;
	}

	@Override
	public GetMTRoleCompletedCountDetailsResponseDocument getMTRoleCompletedCountDetails(
			GetMTRoleCompletedCountDetailsRequestDocument req) {
		GetMTRoleCompletedCountDetailsResponseDocument getMTRoleCompletedCountDetailsResponseDocument = GetMTRoleCompletedCountDetailsResponseDocument.Factory
				.newInstance();
		GetMTRoleCompletedCountDetailsResponse response = getMTRoleCompletedCountDetailsResponseDocument
				.addNewGetMTRoleCompletedCountDetailsResponse();
		Trx trx = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
		int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			String sql = "SELECT mo.tc_mediaorder_id As id,mo.quantity,mo.m_product_id,mo.ad_user_id,sub.mediaType As mediaType,Date(sub.mltiming) As date,sub.lineId As lineId,\n"
					+ "COALESCE(sub.total_quantity, 0) AS lineQty,SUM(COALESCE(sub.total_quantity, 0)) OVER () AS lineTotalQty,\n"
					+ "mo.quantity - COALESCE(sub.total_quantity, 0) AS actualQty,SUM(mo.quantity - COALESCE(sub.total_quantity, 0)) OVER () AS totalActualQty\n"
					+ "FROM adempiere.tc_mediaorder mo\n"
					+ "LEFT JOIN (SELECT ml.tc_mediaorder_id,ml.m_product_id,SUM(ml.quantity) AS total_quantity,ml.tc_medialine_id As lineId,\n"
					+ "ml.updated AS mltiming,pr.description AS mediaType FROM adempiere.tc_medialine ml\n"
					+ "JOIN adempiere.m_product pr ON pr.m_product_id = ml.m_product_id\n"
					+ "GROUP BY ml.tc_mediaorder_id,ml.m_product_id,ml.updated,ml.tc_medialine_id,pr.description) sub \n"
					+ "ON mo.tc_mediaorder_id = sub.tc_mediaorder_id AND mo.m_product_id = sub.m_product_id \n"
					+ "WHERE mo.ad_client_id = ? AND DATE(sub.mltiming) = CURRENT_DATE AND mo.quantity IS NOT NULL\n"
					+ "GROUP BY mo.tc_mediaorder_id,mo.quantity,mo.m_product_id,mo.ad_user_id,sub.mediaType,sub.total_quantity,sub.mltiming,sub.lineId;";

			pstm = DB.prepareStatement(sql.toString(), null);
			pstm.setInt(1, clientId);
			rs = pstm.executeQuery();

			if (!rs.isBeforeFirst()) {
				response.setIsError(false);
				response.addNewGetMTRoleCompletedCountDetailsList();
				return getMTRoleCompletedCountDetailsResponseDocument;
			}

			while (rs.next()) {
				GetMTRoleCompletedCountDetailsList data = response.addNewGetMTRoleCompletedCountDetailsList();
				String mediaType = rs.getString("mediatype");
				int id = rs.getInt("id");
				int lineId = rs.getInt("lineId");
				String date = rs.getString("date");
				int count = rs.getInt("lineQty");
				data.setMediaType(mediaType);
				data.setMediaLabelId(id);
				data.setMediaLineId(lineId);
				data.setDate(date);
				data.setCount(count);
			}
			closeDbCon(pstm, rs);
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			response.setError(INTERNAL_SERVER_ERROR);
			response.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return getMTRoleCompletedCountDetailsResponseDocument;
	}

	@Override
	public GetTraceabilityResponseDocument getTraceability(GetTraceabilityRequestDocument req) {
		GetTraceabilityResponseDocument getTraceabilityResponseDocument = GetTraceabilityResponseDocument.Factory
				.newInstance();
		GetTraceabilityResponse response = getTraceabilityResponseDocument.addNewGetTraceabilityResponse();
		GetTraceabilityRequest loginRequest = req.getGetTraceabilityRequest();
		String cultureLabelUUid = safeTrim(loginRequest.getLabelUUId());
		Trx trx = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
		int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			if (TCUtills.getId(TABLE_SH, cultureLabelUUid, clientId) == 0) {
				if (TCUtills.getId(TABLE_PH, cultureLabelUUid, clientId) == 0) {
					if (TCUtills.getId(TABLE_TC_CULTURE, cultureLabelUUid, clientId) == 0) {
						if (TCUtills.getId(TABLE_EXPLANT, cultureLabelUUid, clientId) == 0) {
							if (TCUtills.getId(TABLE_PLANT, cultureLabelUUid, clientId) == 0) {
								response.setError(
										"The Secondary Hardening UUID, Primary Hardening UUID Culture UUID, Explant UUID, and Plant Tag UUID do not exist. Please check the UUIDs you entered.");
								response.setIsError(true);
								return getTraceabilityResponseDocument;
							}
						}
					}
				}
			}

			String sql = "WITH RECURSIVE\n"
					+ "----------------------------------------------------------------\n"
					+ "-- Recursive culture chain\n"
					+ "----------------------------------------------------------------\n"
					+ "cte AS (\n"
					+ "    -- 1. Start from CULTURE UUID\n"
					+ "    SELECT cl.parentuuid,cl.tc_in_id,cl.tc_out_id,cl.c_uuid,loc.value AS location,cl.created,\n"
					+ "           cl.cycleno,ps.name AS cropType,cs.name AS stage,var.name AS variety,\n"
					+ "           cl.personal_code,1 AS level,u.name As user,\n"
					+ "           'culture' as source_type\n"
					+ "    FROM adempiere.tc_culturelabel cl\n"
					+ "    JOIN adempiere.tc_out o ON o.tc_out_id = cl.tc_out_id\n"
					+ "    JOIN adempiere.m_locator loc ON loc.m_locator_id = o.m_locator_id\n"
					+ "    JOIN adempiere.tc_plantspecies ps ON ps.tc_plantspecies_id = cl.tc_species_id\n"
					+ "    JOIN adempiere.tc_culturestage cs ON cs.tc_culturestage_id = cl.tc_culturestage_id\n"
					+ "    JOIN adempiere.tc_variety var ON var.tc_variety_id = cl.tc_variety_id\n"
					+ "	JOIN adempiere.ad_user u ON u.ad_user_id = cl.createdby\n"
					+ "    WHERE TRIM(cl.c_uuid) = TRIM(?)\n"
					+ "      AND cl.ad_client_id = ?\n"
					+ "\n"
					+ "    UNION ALL\n"
					+ "    -- 2. Primary Hardening → Rooting culture\n"
					+ "    SELECT phs.cultureuuid AS parentuuid,cl.tc_in_id,cl.tc_out_id,cl.c_uuid,loc.value AS location,cl.created,\n"
					+ "           cl.cycleno,ps.name AS cropType,cs.name AS stage,var.name AS variety,\n"
					+ "           cl.personal_code,2 AS level,u.name As user,  -- Culture becomes level 2 when coming from Primary\n"
					+ "           'culture' as source_type\n"
					+ "    FROM adempiere.TC_PrimaryHardeningLabel ph\n"
					+ "    JOIN adempiere.tc_primaryHardeningcultureS phs ON phs.TC_PrimaryHardeningLabel_id = ph.TC_PrimaryHardeningLabel_id\n"
					+ "    JOIN adempiere.tc_culturelabel cl ON phs.cultureuuid = cl.c_uuid\n"
					+ "    JOIN adempiere.tc_out o ON o.tc_out_id = cl.tc_out_id\n"
					+ "    JOIN adempiere.m_locator loc ON loc.m_locator_id = o.m_locator_id\n"
					+ "    JOIN adempiere.tc_plantspecies ps ON ps.tc_plantspecies_id = cl.tc_species_id\n"
					+ "    JOIN adempiere.tc_culturestage cs ON cs.tc_culturestage_id = cl.tc_culturestage_id\n"
					+ "    JOIN adempiere.tc_variety var ON var.tc_variety_id = cl.tc_variety_id\n"
					+ "	JOIN adempiere.ad_user u ON u.ad_user_id = cl.createdby\n"
					+ "    WHERE TRIM(ph.c_uuid) = TRIM(?)\n"
					+ "      AND ph.ad_client_id = ?\n"
					+ "\n"
					+ "    UNION ALL\n"
					+ "    -- 3. Secondary Hardening → Primary Hardening → Culture\n"
					+ "    SELECT phs.cultureuuid AS parentuuid,cl.tc_in_id,cl.tc_out_id,cl.c_uuid,loc.value AS location,cl.created,\n"
					+ "           cl.cycleno,ps.name AS cropType,cs.name AS stage,var.name AS variety,\n"
					+ "           cl.personal_code,3 AS level,u.name As user,  -- Culture becomes level 3 when coming from Secondary\n"
					+ "           'culture' as source_type\n"
					+ "    FROM adempiere.TC_SecondaryHardeningLabel sh\n"
					+ "    JOIN adempiere.TC_PrimaryHardeningLabel ph ON sh.parentuuid = ph.c_uuid\n"
					+ "    JOIN adempiere.tc_primaryHardeningcultureS phs ON phs.TC_PrimaryHardeningLabel_id = ph.TC_PrimaryHardeningLabel_id\n"
					+ "    JOIN adempiere.tc_culturelabel cl ON phs.cultureuuid = cl.c_uuid\n"
					+ "    JOIN adempiere.tc_out o ON o.tc_out_id = cl.tc_out_id\n"
					+ "    JOIN adempiere.m_locator loc ON loc.m_locator_id = o.m_locator_id\n"
					+ "    JOIN adempiere.tc_plantspecies ps ON ps.tc_plantspecies_id = cl.tc_species_id\n"
					+ "    JOIN adempiere.tc_culturestage cs ON cs.tc_culturestage_id = cl.tc_culturestage_id\n"
					+ "    JOIN adempiere.tc_variety var ON var.tc_variety_id = cl.tc_variety_id\n"
					+ "	JOIN adempiere.ad_user u ON u.ad_user_id = cl.createdby\n"
					+ "    WHERE TRIM(sh.c_uuid) = TRIM(?)\n"
					+ "      AND sh.ad_client_id = ?\n"
					+ "\n"
					+ "    UNION ALL\n"
					+ "    -- 4. Recursive step: culture → parent culture\n"
					+ "    SELECT cl2.parentuuid,cl2.tc_in_id,cl2.tc_out_id,cl2.c_uuid,loc.value AS location,cl2.created,\n"
					+ "           cl2.cycleno,ps.name AS cropType,cs.name AS stage,var.name AS variety,\n"
					+ "           cl2.personal_code,cte.level + 1 AS level,u.name As user,\n"
					+ "           'culture' as source_type\n"
					+ "    FROM cte\n"
					+ "    JOIN adempiere.tc_culturelabel cl2 ON cte.parentuuid = cl2.c_uuid\n"
					+ "    JOIN adempiere.tc_out o ON o.tc_out_id = cl2.tc_out_id\n"
					+ "    JOIN adempiere.m_locator loc ON loc.m_locator_id = o.m_locator_id\n"
					+ "    JOIN adempiere.tc_plantspecies ps ON ps.tc_plantspecies_id = cl2.tc_species_id\n"
					+ "    JOIN adempiere.tc_culturestage cs ON cs.tc_culturestage_id = cl2.tc_culturestage_id\n"
					+ "    JOIN adempiere.tc_variety var ON var.tc_variety_id = cl2.tc_variety_id\n"
					+ "	JOIN adempiere.ad_user u ON u.ad_user_id = cl2.createdby\n"
					+ "),\n"
					+ "----------------------------------------------------------------\n"
					+ "-- Culture stage aggregation\n"
					+ "----------------------------------------------------------------\n"
					+ "culture_result AS (\n"
					+ "    SELECT cte.parentuuid,cte.tc_in_id,cte.tc_out_id,cte.c_uuid,cte.location,cte.created,cte.cycleno,\n"
					+ "           cte.cropType,cte.stage,cte.variety,cte.personal_code,cte.level AS level,cte.user,\n"
					+ "           cte.source_type\n"
					+ "    FROM cte\n"
					+ "    GROUP BY cte.parentuuid, cte.tc_in_id, cte.tc_out_id, cte.c_uuid,cte.location,\n"
					+ "             cte.created, cte.cycleno, cte.cropType, cte.stage, cte.variety,\n"
					+ "             cte.personal_code, cte.level,cte.user, cte.source_type\n"
					+ "\n"
					+ "    UNION ALL\n"
					+ "    -- Explant from culture\n"
					+ "    SELECT DISTINCT tcc.parentuuid,tcc.tc_in_id,tcc.tc_out_id,tcc.c_uuid,loc.value AS location,tcc.created,0 AS cycleno,\n"
					+ "           cte.cropType,pr.name AS stage,cte.variety,tcc.personalcode AS personal_code,\n"
					+ "           cte.level + 1 AS level,u.name AS user,  -- Explant is always one level below culture\n"
					+ "           'explant' as source_type\n"
					+ "    FROM cte\n"
					+ "    LEFT JOIN adempiere.tc_explantlabel tcc ON cte.parentuuid = tcc.c_uuid\n"
					+ "    JOIN adempiere.tc_out eo ON eo.tc_out_id = tcc.tc_out_id\n"
					+ "    JOIN adempiere.m_locator loc ON loc.m_locator_id = eo.m_locator_id\n"
					+ "    JOIN adempiere.m_product pr ON pr.m_product_id = eo.m_product_id\n"
					+ "	JOIN adempiere.ad_user u ON u.ad_user_id = tcc.createdby\n"
					+ "\n"
					+ "    UNION ALL\n"
					+ "    -- Plant tag from explant\n"
					+ "    SELECT DISTINCT NULL,0,0,tpt.c_uuid,NULL,tpt.created,0 AS cycleno,\n"
					+ "           cte.cropType,'Plant Tag' AS stage,cte.variety,NULL,\n"
					+ "           cte.level + 2 AS level,u.name AS user,  -- Plant tag is two levels below culture\n"
					+ "           'plant_tag' as source_type\n"
					+ "    FROM cte\n"
					+ "    LEFT JOIN adempiere.tc_explantlabel tcc ON cte.parentuuid = tcc.c_uuid\n"
					+ "    LEFT JOIN adempiere.tc_planttag tpt ON tcc.parentuuid = tpt.c_uuid\n"
					+ "	LEFT JOIN adempiere.ad_user u ON u.ad_user_id = tpt.createdby\n"
					+ "    WHERE tpt.c_uuid IS NOT NULL\n"
					+ "),\n"
					+ "----------------------------------------------------------------\n"
					+ "-- Explant starting point\n"
					+ "----------------------------------------------------------------\n"
					+ "explant_result AS (\n"
					+ "    SELECT DISTINCT tcc.parentuuid AS parentuuid,tcc.tc_in_id,tcc.tc_out_id,tcc.c_uuid,loc.value AS location,tcc.created,0 AS cycleno,\n"
					+ "           ps.name AS cropType,pr.name AS stage,var.name AS variety,tcc.personalcode,1 AS level,u.name AS user,\n"
					+ "           'explant' as source_type\n"
					+ "    FROM adempiere.tc_explantlabel tcc\n"
					+ "    JOIN adempiere.tc_out eo ON eo.tc_out_id = tcc.tc_out_id\n"
					+ "    JOIN adempiere.m_locator loc ON loc.m_locator_id = eo.m_locator_id\n"
					+ "    JOIN adempiere.tc_plantspecies ps ON ps.tc_plantspecies_id = tcc.tc_species_id\n"
					+ "    JOIN adempiere.tc_variety var ON var.tc_variety_id = tcc.tc_variety_id\n"
					+ "    JOIN adempiere.m_product pr ON pr.m_product_id = eo.m_product_id\n"
					+ "	JOIN adempiere.ad_user u ON u.ad_user_id = tcc.createdby\n"
					+ "    WHERE TRIM(tcc.c_uuid) = TRIM(?)\n"
					+ "      AND tcc.ad_client_id = ?\n"
					+ "\n"
					+ "    UNION ALL\n"
					+ "    SELECT DISTINCT NULL AS parentuuid,0,0,tpt.c_uuid,NULL,tpt.created,0 AS cycleno,\n"
					+ "           ps.name AS cropType,'Plant Tag' AS stage,var.name AS variety,NULL,2 AS level,u.name AS user,\n"
					+ "           'plant_tag' as source_type\n"
					+ "    FROM adempiere.tc_planttag tpt\n"
					+ "    JOIN adempiere.tc_explantlabel tcc ON tcc.parentuuid = tpt.c_uuid\n"
					+ "    JOIN adempiere.tc_plantdetails pd ON pd.planttaguuid = tpt.c_uuid\n"
					+ "    JOIN adempiere.tc_plantspecies ps ON ps.tc_plantspecies_id = pd.tc_species_id\n"
					+ "    JOIN adempiere.tc_variety var ON var.tc_variety_id = pd.tc_variety_id\n"
					+ "	JOIN adempiere.ad_user u ON u.ad_user_id = tpt.createdby\n"
					+ "    WHERE TRIM(tcc.c_uuid) = TRIM(?)\n"
					+ "      AND tpt.c_uuid IS NOT NULL\n"
					+ "),\n"
					+ "----------------------------------------------------------------\n"
					+ "-- Plant tag starting point\n"
					+ "----------------------------------------------------------------\n"
					+ "plant_tag_result AS (\n"
					+ "    SELECT DISTINCT NULL AS parentuuid,0,0,tpt.c_uuid,NULL,tpt.created,0 AS cycleno,\n"
					+ "           ps.name AS cropType,'Plant Tag' AS stage,var.name AS variety,NULL,1 AS level,u.name AS user,\n"
					+ "           'plant_tag' as source_type\n"
					+ "    FROM adempiere.tc_planttag tpt\n"
					+ "    JOIN adempiere.tc_plantdetails pd ON pd.planttaguuid = tpt.c_uuid\n"
					+ "    JOIN adempiere.tc_plantspecies ps ON ps.tc_plantspecies_id = pd.tc_species_id\n"
					+ "    JOIN adempiere.tc_variety var ON var.tc_variety_id = pd.tc_variety_id\n"
					+ "	JOIN adempiere.ad_user u ON u.ad_user_id = tpt.createdby\n"
					+ "    WHERE TRIM(tpt.c_uuid) = TRIM(?)\n"
					+ "      AND tpt.ad_client_id = ?\n"
					+ "),\n"
					+ "----------------------------------------------------------------\n"
					+ "-- Primary Hardening standalone\n"
					+ "----------------------------------------------------------------\n"
					+ "primary_result AS (\n"
					+ "    SELECT phs.cultureuuid AS parentuuid,ph.tc_in_id,ph.tc_out_id,ph.c_uuid,loc.value,ph.created,0 AS cycleno,\n"
					+ "           ps.name AS cropType,'Primary Hardening' AS stage,var.name AS variety,\n"
					+ "           u.personalcode,1 AS level,u.name AS user,  -- Primary Hardening as level 1\n"
					+ "           'primary' as source_type\n"
					+ "    FROM adempiere.TC_PrimaryHardeningLabel ph\n"
					+ "    JOIN adempiere.tc_primaryHardeningcultureS phs ON phs.TC_PrimaryHardeningLabel_id = ph.TC_PrimaryHardeningLabel_id\n"
					+ "    JOIN adempiere.tc_out o ON o.tc_out_id = ph.tc_out_id\n"
					+ "    JOIN adempiere.m_locator loc ON loc.m_locator_id = o.m_locator_id\n"
					+ "    JOIN adempiere.tc_plantspecies ps ON ps.tc_plantspecies_id = ph.tc_species_id\n"
					+ "    JOIN adempiere.tc_variety var ON var.tc_variety_id = ph.tc_variety_id\n"
					+ "	JOIN adempiere.ad_user u ON u.ad_user_id = ph.createdby\n"
					+ "    WHERE TRIM(ph.c_uuid) = TRIM(?)\n"
					+ "      AND ph.ad_client_id = ?\n"
					+ "),\n"
					+ "----------------------------------------------------------------\n"
					+ "-- Secondary Hardening standalone and linked to Primary\n"
					+ "----------------------------------------------------------------\n"
					+ "secondary_result AS (\n"
					+ "    -- Secondary Hardening record itself\n"
					+ "    SELECT sh.parentuuid,sh.tc_in_id,sh.tc_out_id,sh.c_uuid,loc.value AS location,sh.created,0 AS cycleno,\n"
					+ "           ps.name AS cropType,'Secondary Hardening' AS stage,var.name AS variety,\n"
					+ "           u.personalcode,1 AS level,u.name AS user,  -- Secondary Hardening as level 1\n"
					+ "           'secondary' as source_type\n"
					+ "    FROM adempiere.TC_SecondaryHardeningLabel sh\n"
					+ "    JOIN adempiere.tc_out o ON o.tc_out_id = sh.tc_out_id\n"
					+ "    JOIN adempiere.m_locator loc ON loc.m_locator_id = o.m_locator_id\n"
					+ "    JOIN adempiere.tc_plantspecies ps ON ps.tc_plantspecies_id = sh.tc_species_id\n"
					+ "    JOIN adempiere.tc_variety var ON var.tc_variety_id = sh.tc_variety_id\n"
					+ "	JOIN adempiere.ad_user u ON u.ad_user_id = sh.createdby\n"
					+ "    WHERE TRIM(sh.c_uuid) = TRIM(?)\n"
					+ "      AND sh.ad_client_id = ?\n"
					+ "\n"
					+ "    UNION ALL\n"
					+ "    -- Linked Primary Hardening record\n"
					+ "    SELECT phs.cultureuuid AS parentuuid,ph.tc_in_id,ph.tc_out_id,ph.c_uuid,loc.value AS location,ph.created,0 AS cycleno,\n"
					+ "           ps.name AS cropType,'Primary Hardening' AS stage,var.name AS variety,\n"
					+ "           u.personalcode,2 AS level,u.name AS user,  -- Primary becomes level 2 when linked from Secondary\n"
					+ "           'primary' as source_type\n"
					+ "    FROM adempiere.TC_SecondaryHardeningLabel sh\n"
					+ "    JOIN adempiere.TC_PrimaryHardeningLabel ph ON sh.parentuuid = ph.c_uuid\n"
					+ "    JOIN adempiere.tc_primaryHardeningcultureS phs ON phs.TC_PrimaryHardeningLabel_id = ph.TC_PrimaryHardeningLabel_id\n"
					+ "    JOIN adempiere.tc_out o ON o.tc_out_id = ph.tc_out_id\n"
					+ "    JOIN adempiere.m_locator loc ON loc.m_locator_id = o.m_locator_id\n"
					+ "    JOIN adempiere.tc_plantspecies ps ON ps.tc_plantspecies_id = ph.tc_species_id\n"
					+ "    JOIN adempiere.tc_variety var ON var.tc_variety_id = ph.tc_variety_id\n"
					+ "	JOIN adempiere.ad_user u ON u.ad_user_id = sh.createdby\n"
					+ "    WHERE TRIM(sh.c_uuid) = TRIM(?)\n"
					+ "      AND sh.ad_client_id = ?\n"
					+ ")\n"
					+ "----------------------------------------------------------------\n"
					+ "-- FINAL OUTPUT\n"
					+ "----------------------------------------------------------------\n"
					+ "SELECT * FROM secondary_result\n"
					+ "UNION ALL\n"
					+ "SELECT * FROM primary_result \n"
					+ "UNION ALL\n"
					+ "SELECT * FROM culture_result\n"
					+ "WHERE (parentuuid IS NULL OR parentuuid <> c_uuid)\n"
					+ "UNION ALL\n"
					+ "SELECT * FROM explant_result WHERE NOT EXISTS (SELECT 1 FROM culture_result)\n"
					+ "UNION ALL \n"
					+ "SELECT * FROM plant_tag_result \n"
					+ "WHERE NOT EXISTS (SELECT 1 FROM explant_result) AND NOT EXISTS (SELECT 1 FROM culture_result)\n"
					+ "ORDER BY created ASC;\n"
					+ "";

			pstm = DB.prepareStatement(sql.toString(), null);
			int paramIndex = 1;
			pstm.setString(paramIndex++, cultureLabelUUid);
			pstm.setInt(paramIndex++, clientId);
			pstm.setString(paramIndex++, cultureLabelUUid);
			pstm.setInt(paramIndex++, clientId);
			pstm.setString(paramIndex++, cultureLabelUUid);
			pstm.setInt(paramIndex++, clientId);
			pstm.setString(paramIndex++, cultureLabelUUid);
			pstm.setInt(paramIndex++, clientId);
			pstm.setString(paramIndex++, cultureLabelUUid);
			pstm.setString(paramIndex++, cultureLabelUUid);
			pstm.setInt(paramIndex++, clientId);
			pstm.setString(paramIndex++, cultureLabelUUid);
			pstm.setInt(paramIndex++, clientId);
			pstm.setString(paramIndex++, cultureLabelUUid);
			pstm.setInt(paramIndex++, clientId);
			pstm.setString(paramIndex++, cultureLabelUUid);
			pstm.setInt(paramIndex++, clientId);
			rs = pstm.executeQuery();

			if (!rs.isBeforeFirst()) {
				response.setIsError(false);
				response.addNewGetTraceabilityList();
				return getTraceabilityResponseDocument;
			}

			while (rs.next()) {
				GetTraceabilityList data = response.addNewGetTraceabilityList();
				String cultureLabelUUId = rs.getString("c_uuid");
				String cropType = rs.getString("cropType");
				String variety = rs.getString("variety");
				String date = rs.getString("created");
				String stage = rs.getString("stage");
				String cycle = rs.getString("cycleno");
				String personalcode = rs.getString("personalcode");
				String user = rs.getString("user");
				String location = rs.getString("location");

				Date dates = inputFormat.parse(date);
				String formattedDate = dateFormat.format(dates);

				data.setLabelUUId(cultureLabelUUId);
				data.setCropType(cropType);
				data.setVariety(variety);
				data.setDate(formattedDate);
				data.setStage(stage);
				data.setCycle(cycle != null ? cycle : "");
				data.setPersonalcode(personalcode != null ? personalcode : "");
				data.setUser(user != null ? user : "");
				data.setLocation(location != null ? location : "");
			}
			closeDbCon(pstm, rs);
		} catch (Exception e) {
			if(trx != null)
				trx.rollback();
			response.setError(INTERNAL_SERVER_ERROR);
			response.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return getTraceabilityResponseDocument;
	}

	@Override
	public CreateOrderIdAndInIdResponseDocument createOrderIdAndInId(CreateOrderIdAndInIdRequestDocument req) {
		CreateOrderIdAndInIdResponseDocument createOrderIdAndInIdResponseDocument = CreateOrderIdAndInIdResponseDocument.Factory
				.newInstance();
		CreateOrderIdAndInIdResponse response = createOrderIdAndInIdResponseDocument
				.addNewCreateOrderIdAndInIdResponse();
		CreateOrderIdAndInIdRequest loginRequest = req.getCreateOrderIdAndInIdRequest();
		String plantTagUUId = safeTrim(loginRequest.getPlantTagUUId());
		Trx trx = null;
		int docTypeId = 0;
		int productId = 0;
		int locatorId = 0;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
		int clientId = Env.getAD_Client_ID(ctx);
		int orgId = Env.getAD_Org_ID(ctx);
		int warehouseId = Env.getContextAsInt(ctx, Env.M_WAREHOUSE_ID);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			docTypeId = TCUtills.getDocTypeId(clientId, DOCUMENT_ORDERED);
			
			int planttagId = TCUtills.getIds(plantTagUUId, clientId);
			if (planttagId != 0) {
				response.setError("This plant tag is not valid");
				response.setIsError(true);
				return createOrderIdAndInIdResponseDocument;
			}

			Date currentDate = new Date();
			Timestamp currentTimestamp = new Timestamp(currentDate.getTime());

			TCOrder order = new TCOrder(ctx, 0, trx.getTrxName());
			order.setAD_Org_ID(orgId);
			order.setName("create plant records");
			order.setDateOrdered(currentTimestamp);
			order.setM_Warehouse_ID(warehouseId);
			order.setC_DocType_ID(0);
			order.setC_DocTypeTarget_ID(docTypeId);
			order.setDocStatus(DocAction.STATUS_Drafted);
			order.setDocAction(DocAction.STATUS_Drafted);
			order.saveEx();
			trx.commit();
			int orderId = order.getTC_order_ID();
			response.setOrderId(orderId);

			locatorId = TCUtills.getLocatorId(clientId, TABLE_LOCATOR, DEFAULT_LOCATOR_PLANT);
			productId = TCUtills.getProductIdByName(clientId, DEFAULT_PRODUCT_PLANT);

			TCIn in = new TCIn(ctx, 0, trx.getTrxName());
			in.setTC_order_ID(orderId);
			in.setAD_Org_ID(orgId);
			in.setM_Product_ID(productId);
			in.setM_Locator_ID(locatorId);
			in.setQuantity(BigDecimal.ONE);
			in.setparentuuid(plantTagUUId);
			in.setC_UOM_ID(UOM_ID);
			in.saveEx();
			trx.commit();
			response.setInId(in.getTC_in_ID());
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			response.setError(INTERNAL_SERVER_ERROR);
			response.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return createOrderIdAndInIdResponseDocument;
	}

	@Override
	public CreateOrderIdForExplantResponseDocument createOrderIdForExplant(CreateOrderIdForExplantRequestDocument req) {
		CreateOrderIdForExplantResponseDocument createOrderIdForExplantResponseDocument = CreateOrderIdForExplantResponseDocument.Factory
				.newInstance();
		CreateOrderIdForExplantResponse response = createOrderIdForExplantResponseDocument
				.addNewCreateOrderIdForExplantResponse();
		CreateOrderIdForExplantRequest loginRequest = req.getCreateOrderIdForExplantRequest();
		String explantOutUUId = safeTrim(loginRequest.getExplantOutUUId());
		Trx trx = null;
		int docTypeId = 0;
		int productId = 0;
		int locatorId = 0;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
			int clientId = Env.getAD_Client_ID(ctx);
			int orgId = Env.getAD_Org_ID(ctx);
			int warehouseId = Env.getContextAsInt(ctx, Env.M_WAREHOUSE_ID);

			int recordId = TCUtills.getId(TABLE_TC_OUT, explantOutUUId, clientId);
			if (recordId == 0) {
				response.setError("Out Label UUId not found in table record " + explantOutUUId + "");
				response.setIsError(true);
				return createOrderIdForExplantResponseDocument;
			}

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			docTypeId = TCUtills.getDocTypeId(clientId, DOCUMENT_ORDERED);

			Date currentDate = new Date();
			Timestamp currentTimestamp = new Timestamp(currentDate.getTime());

			TCOrder order = new TCOrder(ctx, 0, trx.getTrxName());
			order.setAD_Org_ID(orgId);
			order.setName("create explant record");
			order.setDateOrdered(currentTimestamp);
			order.setM_Warehouse_ID(warehouseId);
			order.setC_DocType_ID(0);
			order.setC_DocTypeTarget_ID(docTypeId);
			order.setDocStatus(DocAction.STATUS_Drafted);
			order.setDocAction(DocAction.STATUS_Drafted);
			order.saveEx();
			trx.commit();
			int orderId = order.getTC_order_ID();
			response.setOrderId(orderId);

			locatorId = TCUtills.getLocatorId(clientId, TABLE_LOCATOR, DEFAULT_LOCATOR_EXPLANT);
			productId = TCUtills.getProductIdByName(clientId, DEFAULT_PRODUCT_EXPLANT);

			TCIn in = new TCIn(ctx, 0, trx.getTrxName());
			in.setTC_order_ID(orderId);
			in.setAD_Org_ID(orgId);
			in.setM_Product_ID(productId);
			in.setM_Locator_ID(locatorId);
			in.setQuantity(BigDecimal.ONE);
			in.setparentuuid(explantOutUUId);
			in.setC_UOM_ID(UOM_ID);
			in.saveEx();
			trx.commit();
			response.setInId(in.getTC_in_ID());
			closeDbCon(pstm, rs);
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			response.setError(INTERNAL_SERVER_ERROR);
			response.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return createOrderIdForExplantResponseDocument;
	}

	@Override
	public CreateOrderIdForCultureResponseDocument createOrderIdForCulture(CreateOrderIdForCultureRequestDocument req) {
		CreateOrderIdForCultureResponseDocument createOrderIdForCultureResponseDocument = CreateOrderIdForCultureResponseDocument.Factory
				.newInstance();
		CreateOrderIdForCultureResponse response = createOrderIdForCultureResponseDocument
				.addNewCreateOrderIdForCultureResponse();
		CreateOrderIdForCultureRequest loginRequest = req.getCreateOrderIdForCultureRequest();

		String cultureOutUUId = safeTrim(loginRequest.getCultureOutUUId());
		Trx trx = null;
		int docTypeId = 0;
		int outId = 0;
		int productId = 0;
		int locatorId = 0;
		int parentId = 0;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
			int clientId = Env.getAD_Client_ID(ctx);
			int warehouseId = Env.getContextAsInt(ctx, Env.M_WAREHOUSE_ID);
			int orgId = Env.getAD_Org_ID(ctx);
			
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			docTypeId = TCUtills.getDocTypeId(clientId, DOCUMENT_ORDERED);
			
			int recordId = TCUtills.getId(TABLE_TC_OUT, cultureOutUUId, clientId);
			if (recordId == 0) {
				response.setError("Out Label UUId not found in table record " + cultureOutUUId + "");
				response.setIsError(true);
				return createOrderIdForCultureResponseDocument;
			}

			outId = TCUtills.getId(TABLE_TC_OUT, cultureOutUUId, clientId);
			System.out.println(outId);
			if (outId == 0) {
				response.setIsError(true);
				response.setError("Culture OutUUId is not correct please check");
				return createOrderIdForCultureResponseDocument;
			}
			TCOut out = new TCOut(ctx, outId, trxName);
			productId = out.getM_Product_ID();
			locatorId = out.getM_Locator_ID();
			BigDecimal qty = out.getQuantity();

			Date currentDate = new Date();
			Timestamp currentTimestamp = new Timestamp(currentDate.getTime());

			TCOrder order = new TCOrder(ctx, 0, trx.getTrxName());
			order.setAD_Org_ID(orgId);
			order.setName("Create Culture Record");
			order.setDateOrdered(currentTimestamp);
			order.setM_Warehouse_ID(warehouseId);
			order.setC_DocType_ID(0);
			order.setC_DocTypeTarget_ID(docTypeId);
			order.setDocStatus(DocAction.STATUS_Drafted);
			order.setDocAction(DocAction.STATUS_Drafted);
			order.saveEx();
			trx.commit();
			int orderId = order.getTC_order_ID();
			response.setOrderId(orderId);

			TCIn in = new TCIn(ctx, 0, trx.getTrxName());
			in.setTC_order_ID(orderId);
			in.setAD_Org_ID(orgId);
			in.setM_Product_ID(productId);
			in.setM_Locator_ID(locatorId);
			in.setQuantity(qty);
			in.setparentuuid(cultureOutUUId);
			in.setC_UOM_ID(UOM_ID);
			in.saveEx();
			trx.commit();
			response.setInId(in.getTC_in_ID());

			String sql = "SELECT cl.tc_culturelabel_id as cultureId,cl.c_uuid AS parentUUid,o.c_uuid,o.tc_out_id FROM adempiere.tc_culturelabel cl\n"
					+ "JOIN adempiere.tc_out o ON o.tc_out_id = cl.tc_out_id\n" + "WHERE o.c_uuid = ?";
			pstm = DB.prepareStatement(sql, null);
			pstm.setString(1, cultureOutUUId);
			rs = pstm.executeQuery();

			if (!rs.isBeforeFirst()) {
				response.setError("Out Label UUid not attached in Culture Label");
				return createOrderIdForCultureResponseDocument;
			}
			while (rs.next()) {
				parentId = rs.getInt("cultureId");
			}
			TCCultureLabel label = new TCCultureLabel(ctx, parentId, trx.getTrxName());
			label.settosubculturecheck(true);
			label.saveEx();
			trx.commit();
			closeDbCon(pstm, rs);
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			response.setError(INTERNAL_SERVER_ERROR);
			response.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return createOrderIdForCultureResponseDocument;
	}

	@Override
	public GetLabelDetailsResponseDocument getLabelDetails(GetLabelDetailsRequestDocument req) {
		GetLabelDetailsResponseDocument getLabelDetailsResponseDocument = GetLabelDetailsResponseDocument.Factory
				.newInstance();
		GetLabelDetailsResponse response = getLabelDetailsResponseDocument.addNewGetLabelDetailsResponse();
		Trx trx = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
		int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			String sql = "SELECT ps.codeno || ' ' || v.codeno || ' ' || el.parentcultureline || ' ' || TO_CHAR(el.created, 'DD-MM-YY') || ' ' || ns.codeno AS cultureCode,\n"
					+ "pr.value AS stage,DATE(el.created) AS manufacturingDate,l.x AS room,l.y AS rack,l.z AS columns,el.c_uuid as UUId,'Explant Label' As label\n"
					+ "FROM adempiere.tc_explantlabel el\n"
					+ "JOIN adempiere.tc_plantspecies ps ON ps.tc_plantspecies_id = el.tc_species_id\n"
					+ "JOIN adempiere.tc_variety v ON v.tc_variety_id = el.tc_variety_id\n"
					+ "JOIN adempiere.tc_naturesample ns ON ns.tc_naturesample_id = el.tc_naturesample_id	\n"
					+ "JOIN adempiere.tc_out o ON o.tc_out_id = el.tc_out_id\n"
					+ "JOIN adempiere.m_product pr ON pr.m_product_id = o.m_product_id	\n"
					+ "JOIN adempiere.m_locator l ON l.m_locator_id = o.m_locator_id\n" + "WHERE el.ad_client_id = ? AND DATE(el.created) IN (CURRENT_DATE)\n" + "UNION ALL\n"
					+ "SELECT ps.codeno || ' ' || v.codeno || ' ' || cl.parentcultureline || ' ' || TO_CHAR(cl.culturedate, 'DD-MM-YY') || ' ' || ns.codeno AS cultureCode,\n"
					+ "pr.value AS stage,DATE(cl.created) AS manufacturingDate,l.x AS room,l.y AS rack,l.z AS columns,cl.c_uuid as UUId,'Culture Label' As label\n"
					+ "FROM adempiere.tc_culturelabel cl\n"
					+ "JOIN adempiere.tc_plantspecies ps ON ps.tc_plantspecies_id = cl.tc_species_id\n"
					+ "JOIN adempiere.tc_variety v ON v.tc_variety_id = cl.tc_variety_id\n"
					+ "JOIN adempiere.tc_naturesample ns ON ns.tc_naturesample_id = cl.tc_naturesample_id	\n"
					+ "JOIN adempiere.tc_culturestage cs ON cs.tc_culturestage_id = cl.tc_culturestage_id	\n"
					+ "JOIN adempiere.tc_out o ON o.tc_out_id = cl.tc_out_id\n"
					+ "JOIN adempiere.m_product pr ON pr.m_product_id = o.m_product_id	\n"
					+ "JOIN adempiere.m_locator l ON l.m_locator_id = o.m_locator_id\n" + "WHERE cl.ad_client_id = ? AND DATE(cl.created) IN (CURRENT_DATE)\n" + "UNION ALL	\n"
					+ "SELECT ps.codeno || ' ' || v.codeno || ' ' || phl.parentcultureline || ' ' || TO_CHAR(phl.created, 'DD-MM-YY') AS cultureCode,\n"
					+ "pr.value AS stage,DATE(phl.created) AS manufacturingDate,l.x AS room,l.y AS rack,l.z AS columns,phl.c_uuid as UUId,'Primary Hardening Label' As label\n"
					+ "FROM adempiere.tc_primaryhardeninglabel phl\n"
					+ "JOIN adempiere.tc_plantspecies ps ON ps.tc_plantspecies_id = phl.tc_species_id\n"
					+ "JOIN adempiere.tc_variety v ON v.tc_variety_id = phl.tc_variety_id	\n"
					+ "JOIN adempiere.tc_out o ON o.tc_out_id = phl.tc_out_id\n"
					+ "JOIN adempiere.m_product pr ON pr.m_product_id = o.m_product_id	\n"
					+ "JOIN adempiere.m_locator l ON l.m_locator_id = o.m_locator_id\n" + "WHERE phl.ad_client_id = ? AND DATE(phl.created) IN (CURRENT_DATE)\n" + "UNION ALL\n"
					+ "SELECT ps.codeno || ' ' || v.codeno || ' ' || shl.parentcultureline || ' ' || TO_CHAR(shl.created, 'DD-MM-YY') AS cultureCode,\n"
					+ "pr.value AS stage,DATE(shl.created) AS manufacturingDate,l.x AS room,l.y AS rack,l.z AS columns,shl.c_uuid as UUId,'Secondary Hardening Label' As label\n"
					+ "FROM adempiere.tc_secondaryhardeninglabel shl\n"
					+ "JOIN adempiere.tc_plantspecies ps ON ps.tc_plantspecies_id = shl.tc_species_id\n"
					+ "JOIN adempiere.tc_variety v ON v.tc_variety_id = shl.tc_variety_id	\n"
					+ "JOIN adempiere.tc_out o ON o.tc_out_id = shl.tc_out_id\n"
					+ "JOIN adempiere.m_product pr ON pr.m_product_id = o.m_product_id	\n"
					+ "JOIN adempiere.m_locator l ON l.m_locator_id = o.m_locator_id\n" + "WHERE shl.ad_client_id = ? AND DATE(shl.created) IN (CURRENT_DATE);";

			pstm = DB.prepareStatement(sql.toString(), null);
			pstm.setInt(1, clientId);
			pstm.setInt(2, clientId);
			pstm.setInt(3, clientId);
			pstm.setInt(4, clientId);
			rs = pstm.executeQuery();

			if (!rs.isBeforeFirst()) {
				response.setIsError(false);
				response.addNewGetLabelDetailsList();
				return getLabelDetailsResponseDocument;
			}

			while (rs.next()) {
				GetLabelDetailsList data = response.addNewGetLabelDetailsList();
				String cultureCode = rs.getString("cultureCode");
				String stage = rs.getString("stage");
				String manufacturingDate = rs.getString("manufacturingDate");
				String room = rs.getString("room");
				String rack = rs.getString("rack");
				String columns = rs.getString("columns");
				String uUId = rs.getString("UUId");
				String label = rs.getString("label");

				data.setLabelCode(cultureCode);
				data.setStage(stage);
				data.setManufacturingDate(manufacturingDate);
				data.setRoom(room);
				data.setRack(rack);
				data.setColumns(columns);
				data.setUUId(uUId);
				data.setLabel(label);
			}
			closeDbCon(pstm, rs);
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			response.setError(INTERNAL_SERVER_ERROR);
			response.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return getLabelDetailsResponseDocument;
	}

	@Override
	public CreateOrderIdForPrimaryHResponseDocument createOrderIdForPrimaryH(
			CreateOrderIdForPrimaryHRequestDocument req) {
		CreateOrderIdForPrimaryHResponseDocument createOrderIdForPrimaryHResponseDocument = CreateOrderIdForPrimaryHResponseDocument.Factory
				.newInstance();
		CreateOrderIdForPrimaryHResponse response = createOrderIdForPrimaryHResponseDocument
				.addNewCreateOrderIdForPrimaryHResponse();
		CreateOrderIdForPrimaryHRequest loginRequest = req.getCreateOrderIdForPrimaryHRequest();
		String primaryHOutUUId = safeTrim(loginRequest.getPrimaryHOutUUId());
		Trx trx = null;
		int docTypeId = 0;
		int outId = 0;
		int productId = 0;
		int locatorId = 0;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
		int orgId = Env.getAD_Org_ID(ctx);
		int clientId = Env.getAD_Client_ID(ctx);
		int warehouseId = Env.getContextAsInt(ctx, Env.M_WAREHOUSE_ID);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			docTypeId = TCUtills.getDocTypeId(clientId, DOCUMENT_ORDERED);

			outId = TCUtills.getId(TABLE_TC_OUT, primaryHOutUUId, clientId);
			if (outId == 0) {
				response.setIsError(true);
				response.setError("Primary Hardening OutUUId is not correct please check");
				return createOrderIdForPrimaryHResponseDocument;
			}
			TCOut out = new TCOut(ctx, outId, trxName);
			productId = out.getM_Product_ID();
			locatorId = out.getM_Locator_ID();
			BigDecimal qty = out.getQuantity();

			Date currentDate = new Date();
			Timestamp currentTimestamp = new Timestamp(currentDate.getTime());

			TCOrder order = new TCOrder(ctx, 0, trx.getTrxName());
			order.setAD_Org_ID(orgId);
			order.setName("create primary hardening record");
			order.setDateOrdered(currentTimestamp);
			order.setM_Warehouse_ID(warehouseId);
			order.setC_DocType_ID(0);
			order.setC_DocTypeTarget_ID(docTypeId);
			order.setDocStatus(DocAction.STATUS_Drafted);
			order.setDocAction(DocAction.STATUS_Drafted);
			order.saveEx();
			trx.commit();
			int orderId = order.getTC_order_ID();
			response.setOrderId(orderId);

			TCIn in = new TCIn(ctx, 0, trx.getTrxName());
			in.setTC_order_ID(orderId);
			in.setAD_Org_ID(orgId);
			in.setM_Product_ID(productId);
			in.setM_Locator_ID(locatorId);
			in.setQuantity(qty);
			in.setparentuuid(primaryHOutUUId);
			in.setC_UOM_ID(UOM_ID);
			in.saveEx();
			trx.commit();
			response.setInId(in.getTC_in_ID());
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			response.setError(INTERNAL_SERVER_ERROR);
			response.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return createOrderIdForPrimaryHResponseDocument;
	}

	@Override
	public GetQARoleExpiryCountsResponseDocument getQARoleExpiryCounts(GetQARoleExpiryCountsRequestDocument req) {
		GetQARoleExpiryCountsResponseDocument getQARoleExpiryCountsResponseDocument = GetQARoleExpiryCountsResponseDocument.Factory
				.newInstance();
		GetQARoleExpiryCountsResponse response = getQARoleExpiryCountsResponseDocument
				.addNewGetQARoleExpiryCountsResponse();
		Trx trx = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
		int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			String sql = "WITH subquery AS (SELECT DATE(cl.created + (cs.period::int * INTERVAL '1 day')) AS expiryDate,cs.period AS period,\n"
					+ "(SELECT COUNT(*) FROM adempiere.tc_culturelabel cll WHERE cll.parentuuid = cl.c_uuid LIMIT 1) AS subquery_column\n"
					+ "FROM adempiere.tc_culturelabel cl JOIN adempiere.tc_culturestage cs ON cs.tc_culturestage_id = cl.tc_culturestage_id\n"
					+ "JOIN adempiere.tc_in i ON i.tc_in_id = cl.tc_in_id	\n" + "WHERE cl.ad_client_id = ? AND cl.isdiscarded = 'N' AND cl.tosubculturecheck = 'N' AND i.primarycheck = 'N'\n"
					+ "AND cl.c_uuid IS NOT NULL AND cl.parentuuid IS NOT NULL)\n"
					+ "SELECT expiryDate,COUNT(*) AS count FROM subquery WHERE subquery_column = 0 AND expiryDate <= current_date\n"
					+ "GROUP BY expiryDate ORDER BY expiryDate;";

			pstm = DB.prepareStatement(sql.toString(), null);
			pstm.setInt(1, clientId);
			rs = pstm.executeQuery();

			if (!rs.isBeforeFirst()) {
				response.setIsError(false);
				response.addNewGetExpiryCountList();
				return getQARoleExpiryCountsResponseDocument;
			}

			while (rs.next()) {
				GetExpiryCountList data = response.addNewGetExpiryCountList();
				String expiryDate = rs.getString("expiryDate");
				String count = rs.getString("count");

				data.setExpiryDate(expiryDate);
				data.setCount(count);
			}
			closeDbCon(pstm, rs);
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			response.setError(INTERNAL_SERVER_ERROR);
			response.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return getQARoleExpiryCountsResponseDocument;
	}

	@Override
	public DeleteAttachmentRecordUsingIndexResponseDocument deleteAttachmentRecordUsingIndex(
			DeleteAttachmentRecordUsingIndexRequestDocument req) {
		DeleteAttachmentRecordUsingIndexResponseDocument deleteAttachmentRecordUsingIndexResponseDocument = DeleteAttachmentRecordUsingIndexResponseDocument.Factory
				.newInstance();
		DeleteAttachmentRecordUsingIndexResponse response = deleteAttachmentRecordUsingIndexResponseDocument
				.addNewDeleteAttachmentRecordUsingIndexResponse();
		DeleteAttachmentRecordUsingIndexRequest loginRequest = req.getDeleteAttachmentRecordUsingIndexRequest();
		int tableId = 0;
		int recordId = 0;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);

			tableId = loginRequest.getTableId();
			recordId = loginRequest.getRecordId();
			MAttachment attachment = MAttachment.get(Env.getCtx(), tableId, recordId);
			IndexList[] lists = loginRequest.getIndexListArray();
			for (IndexList list : lists) {
				int imageIndex = list.getIndexNo();
				if (attachment != null && imageIndex >= 0 && imageIndex < attachment.getEntryCount()) {
					attachment.deleteEntry(imageIndex);
				}
			}
			attachment.save();
			response.setIsError(false);
		} catch (Exception e) {
			response.setError(INTERNAL_SERVER_ERROR);
			response.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			getCompiereService().disconnect();
		}
		return deleteAttachmentRecordUsingIndexResponseDocument;
	}

	@Override
	public UpdateFarmerDetailResponseDocument updateFarmerDetail(UpdateFarmerDetailRequestDocument req) {
		Trx trx = null;
		UpdateFarmerDetailResponseDocument updateFarmerDetailResponseDocument = UpdateFarmerDetailResponseDocument.Factory
				.newInstance();
		UpdateFarmerDetailResponse response = updateFarmerDetailResponseDocument.addNewUpdateFarmerDetailResponse();
		UpdateFarmerDetailRequest loginRequest = req.getUpdateFarmerDetailRequest();
		int farmerId = 0;

		UpdateFarmerDetail addFarmer = loginRequest.getUpdateFarmerDetail();
		
		String name       = safeTrim(addFarmer.getName());
	    String latitude   = safeTrim(addFarmer.getLatitude());
	    String longitude  = safeTrim(addFarmer.getLongitude());
	    String mobileNo   = safeTrim(addFarmer.getMobileNo());
	    String villageName    = safeTrim(addFarmer.getVillageName());
	    String landmark   = safeTrim(addFarmer.getLandmark());
	    String talukName      = safeTrim(addFarmer.getTalukName());
	    String cityName       = safeTrim(addFarmer.getCityName());
	    String district   = safeTrim(addFarmer.getDistrict());
	    String state      = safeTrim(addFarmer.getState());
	    String pinCode    = safeTrim(addFarmer.getPinCode());

		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
			int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			farmerId = loginRequest.getFarmerId();
			PO po = new Query(ctx, TCFarmer.Table_Name, "ad_client_id =? AND tc_farmer_id =?", trxName)
					.setParameters(clientId, farmerId).first();
			if (po == null) {
				response.setError("FarmerId invalid");
				response.setIsError(true);
				return updateFarmerDetailResponseDocument;
			}

			TCFarmer farmer = new TCFarmer(ctx, farmerId, trx.getTrxName());
			if (!name.isEmpty()) {
				if (!TCUtills.isValidNameNumber(name)) {
					response.setError("Invalid Name format");
					response.setIsError(true);
					return updateFarmerDetailResponseDocument;
				}
				farmer.setName(name);
			}
			
			if (containsMaliciousPattern(latitude)) {
				response.setError("Invalid input: Base64 or URL-encoded text not allowed");
				response.setIsError(true);
			    return updateFarmerDetailResponseDocument;
			}
			
			if (containsMaliciousPattern(longitude)) {
				response.setError("Invalid input: Base64 or URL-encoded text not allowed");
				response.setIsError(true);
			    return updateFarmerDetailResponseDocument;
			}
			
			if (containsMaliciousPattern(district)) {
				response.setError("Invalid input: Base64 or URL-encoded text not allowed");
				response.setIsError(true);
			    return updateFarmerDetailResponseDocument;
			}
			
			if (containsMaliciousPattern(state)) {
				response.setError("Invalid input: Base64 or URL-encoded text not allowed");
				response.setIsError(true);
			    return updateFarmerDetailResponseDocument;
			}
			
			if (containsMaliciousPattern(cityName)) {
				response.setError("Invalid input: Base64 or URL-encoded text not allowed");
				response.setIsError(true);
			    return updateFarmerDetailResponseDocument;
			}
			
			if (containsMaliciousPattern(talukName)) {
				response.setError("Invalid input: Base64 or URL-encoded text not allowed");
				response.setIsError(true);
			    return updateFarmerDetailResponseDocument;
			}
			
			if (containsMaliciousPattern(landmark)) {
				response.setError("Invalid input: Base64 or URL-encoded text not allowed");
				response.setIsError(true);
			    return updateFarmerDetailResponseDocument;
			}
			
			if (containsMaliciousPattern(villageName)) {
				response.setError("Invalid input: Base64 or URL-encoded text not allowed");
				response.setIsError(true);
			    return updateFarmerDetailResponseDocument;
			}
			
			if (latitude != null && !latitude.isEmpty()) {
				if (!TCUtills.isValidLocationName(latitude)) {
					 response.setError("Invalid Latitude format");
					 response.setIsError(true);
			            return updateFarmerDetailResponseDocument;
			        }
				 
				farmer.setlatitude(latitude);
			}
			if (longitude != null && !longitude.isEmpty()) {
				if (!TCUtills.isValidLocationName(longitude)) {
					 response.setError("Invalid Longitude format");
					 response.setIsError(true);
			            return updateFarmerDetailResponseDocument;
			        }
				 
				farmer.setlongitude(longitude);
			}
			 
			if (!mobileNo.isEmpty()) {
				if (!TCUtills.isValidMobileNumber(mobileNo)) {
					response.setError("Invalid Mobile number, Only 10 digits allowed");
					response.setIsError(true);
					return updateFarmerDetailResponseDocument;
				}
				farmer.setmobileno(mobileNo);
			}
			if (!villageName.isEmpty()) {
				if (!TCUtills.isValidLocationName(villageName)) {
					response.setError("Invalid Village Name format");
					response.setIsError(true);
					return updateFarmerDetailResponseDocument;
				}
				farmer.setvillagename2(villageName);
			}
			if (!landmark.isEmpty()) {
				if (!TCUtills.isValidLocationName(landmark)) {
					response.setError("Invalid landmark format");
					response.setIsError(true);
					return updateFarmerDetailResponseDocument;
				}
				farmer.setlandmark(landmark);
			}
			if (!talukName.isEmpty()) {
				if (!TCUtills.isValidName(talukName)) {
					response.setError("Invalid Taluk Name format");
					response.setIsError(true);
					return updateFarmerDetailResponseDocument;
				}
				farmer.settalukname(talukName);
			}
			if (!cityName.isEmpty()) {
				if (!TCUtills.isValidName(cityName)) {
					response.setError("Invalid City Name format");
					response.setIsError(true);
					return updateFarmerDetailResponseDocument;
				}
				farmer.setCity(cityName);
			}
			if (!district.isEmpty()) {
				if (!TCUtills.isValidName(district)) {
					response.setError("Invalid District Name format");
					response.setIsError(true);
					return updateFarmerDetailResponseDocument;
				}
				farmer.setdistrict(district);
			}
			if (!state.isEmpty()) {
				if (!TCUtills.isValidLocationName(state)) {
					response.setError("Invalid State Name format");
					response.setIsError(true);
					return updateFarmerDetailResponseDocument;
				}
				farmer.setstate(state);
			}
			if (!pinCode.isEmpty()) {
				if (!TCUtills.isValidPinCode(pinCode)) {
					response.setError("Invalid PIN code, Only 6 digits allowed");
					response.setIsError(true);
					return updateFarmerDetailResponseDocument;
				}
				farmer.setpincode(pinCode);
			}
			if (!farmer.save()) {
				throw new Exception("Failed to Update Farmer: " + farmer);
			}
			trx.commit();
			response.setIsError(false);
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			response.setError(INTERNAL_SERVER_ERROR);
			response.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return updateFarmerDetailResponseDocument;
	}

	@Override
	public GetMediaLabelExpiryCountResponseDocument getMediaLabelExpiryCount(
			GetMediaLabelExpiryCountRequestDocument req) {
		GetMediaLabelExpiryCountResponseDocument res = GetMediaLabelExpiryCountResponseDocument.Factory.newInstance();
		GetMediaLabelExpiryCountResponse response = res.addNewGetMediaLabelExpiryCountResponse();
		Trx trx = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
		int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			String sql = "SELECT ml.TC_MediaLine_id,md.name As mediaType, mel.c_uuid AS mediaLabelUUId, mel.tc_mediaLabelQr_id, DATE(mel.updated),count(*) AS count,\n"
					+ "SUM(COUNT(*)) OVER () AS total_labels	\n" + "FROM adempiere.TC_MediaLine ml\n"
					+ "JOIN adempiere.tc_mediaLabelQr mel ON mel.TC_MediaLine_id = ml.TC_MediaLine_id\n"
					+ "JOIN adempiere.tc_mediatype md ON md.tc_mediatype_id = mel.tc_mediatype_id\n"
					+ "WHERE ml.ad_client_id = ? AND isdiscarded = 'N'\n"
					+ "AND DATE(mel.updated) <= CURRENT_DATE - INTERVAL '21 days'\n"
					+ "AND (SELECT COUNT(*) FROM adempiere.TC_MediaOutLine mol WHERE mol.TC_MediaLine_id = ml.TC_MediaLine_id LIMIT 1) = 0\n"
					+ "GROUP BY ml.TC_MediaLine_id, md.name, mel.c_uuid, mel.tc_mediaLabelQr_id, DATE(mel.updated) Limit 1;\n"
					+ "";

			pstm = DB.prepareStatement(sql.toString(), null);
			pstm.setInt(1, clientId);
			rs = pstm.executeQuery();
			while (rs.next()) {
				int mediumLabelCount = rs.getInt("total_labels");
				response.setTotalCount(mediumLabelCount);
			}
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			response.setError(INTERNAL_SERVER_ERROR);
			response.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return res;
	}

	@Override
	public GetMediaLabelExpiryListResponseDocument getMediaLabelExpiryList(GetMediaLabelExpiryListRequestDocument req) {
		GetMediaLabelExpiryListResponseDocument res = GetMediaLabelExpiryListResponseDocument.Factory.newInstance();
		GetMediaLabelExpiryListResponse response = res.addNewGetMediaLabelExpiryListResponse();
		Trx trx = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
		int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			String sql = "SELECT ml.tc_mediaorder_id As mediaOrderId,ml.TC_MediaLine_id As medialineId,ml.quantity As quantity,md.name As mediaType,\n"
					+ "mel.c_uuid AS mediaLabelUUId, mel.tc_mediaLabelQr_id AS mediaLabelId,DATE(mel.updated) As date\n"
					+ "FROM adempiere.TC_MediaLine ml\n"
					+ "JOIN adempiere.tc_mediaLabelQr mel ON mel.TC_MediaLine_id = ml.TC_MediaLine_id\n"
					+ "JOIN adempiere.tc_mediatype md ON md.tc_mediatype_id = mel.tc_mediatype_id\n"
					+ "WHERE ml.ad_client_id = ? AND isdiscarded = 'N'\n"
					+ "AND DATE(mel.updated) <= CURRENT_DATE - INTERVAL '21 days'\n"
					+ "AND (SELECT COUNT(*) FROM adempiere.TC_MediaOutLine mol WHERE mol.TC_MediaLine_id = ml.TC_MediaLine_id LIMIT 1) = 0\n"
					+ "GROUP BY ml.TC_MediaLine_id, md.name, mel.c_uuid, mel.tc_mediaLabelQr_id, DATE(mel.updated);";

			pstm = DB.prepareStatement(sql.toString(), null);
			pstm.setInt(1, clientId);
			rs = pstm.executeQuery();
			while (rs.next()) {
				int mediaLabelId = rs.getInt("mediaLabelId");
				String mediaLabelUUId = rs.getString("mediaLabelUUId");
				String mediaType = rs.getString("mediaType");
				String labelDate = rs.getString("date");
				int mediaOrderId = rs.getInt("mediaOrderId");
				int mediaLineId = rs.getInt("medialineId");
				int quantity = rs.getInt("quantity");

				MediaLabelExpiryList list = response.addNewMediaLabelExpiryList();
				list.setMediaLabelId(mediaLabelId);
				list.setMediaLabelUUId(mediaLabelUUId);
				list.setMediaType(mediaType);
				list.setLabelDate(labelDate);
				list.setMediaOrderId(mediaOrderId);
				list.setMediaLineId(mediaLineId);
				list.setQuantity(quantity);
			}
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			response.setError(INTERNAL_SERVER_ERROR);
			response.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return res;
	}

	@Override
	public Response getImage(int tableId, int id, int index) {
		try {
			Properties ctx = Env.getCtx();
			MAttachment attachment = MAttachment.get(ctx, tableId, id);

			if (attachment == null) {
				return Response.status(Response.Status.NOT_FOUND)
						.entity("{\"error\":\"Attachment not found\"}")
						.type("application/json").build();
			}

			MAttachmentEntry[] entries = attachment.getEntries();
			if (entries == null || index >= entries.length || index < 0) {
				return Response.status(Response.Status.NOT_FOUND)
						.entity("{\"error\":\"Image index not found\"}")
						.type("application/json").build();
			}

			MAttachmentEntry entry = entries[index];
			byte[] data = entry.getData();
			
			if (data == null || data.length == 0) {
				return Response.status(Response.Status.NOT_FOUND)
						.entity("{\"error\":\"Image data is empty\"}")
						.type("application/json").build();
			}

			String fileName = entry.getName();
			String contentType = "image/png";
			if (fileName != null) {
				if (fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".jpeg")) {
					contentType = "image/jpeg";
				} else if (fileName.toLowerCase().endsWith(".gif")) {
					contentType = "image/gif";
				}
			}

			return Response.ok(data)
					.type(contentType)
					.header("Content-Disposition", "inline; filename=\"" + (fileName != null ? fileName : "image.png") + "\"")
					.build();
		} catch (Exception e) {
			log.saveError("Error in getImage: ", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("{\"error\":\"" + e.getMessage() + "\"}")
					.type("application/json").build();
		}
	}

	@Override
	public GetIOTDeviceRecordByIdResponseDocument getIOTDeviceRecordById(GetIOTDeviceRecordByIdRequestDocument req) {
		GetIOTDeviceRecordByIdResponseDocument res = GetIOTDeviceRecordByIdResponseDocument.Factory.newInstance();
		GetIOTDeviceRecordByIdResponse response = res.addNewGetIOTDeviceRecordByIdResponse();
		GetIOTDeviceRecordByIdRequest loginRequest = req.getGetIOTDeviceRecordByIdRequest();
		int deviceId = 0;
		Trx trx = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
		int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			deviceId = loginRequest.getDeviceId();
			String sql = "SELECT lt.frequency As roomFrequency,dd.tc_devicedata_id As id,dd.c_uuid AS uuid,dd.name As name,dd.deviceid AS deviceId,dd.frequency as frequency,lt.name AS roomNo,tp.name As cornorType,dd.sensontype AS sensorType FROM adempiere.tc_devicedata dd\n"
					+ "JOIN adempiere.m_locatortype lt ON lt.m_locatortype_id = dd.m_locatortype_id\n"
					+ "JOIN adempiere.tc_temperatureposition tp ON tp.tc_temperatureposition_id = dd.tc_temperatureposition_id\n"
//					+ "LEFT JOIN adempiere.tc_sensortype st ON st.tc_sensortype_id = dd.tc_sensortype_id\n"
					+ "WHERE dd.ad_client_id = ? AND DD.tc_devicedata_id = ?";

			pstm = DB.prepareStatement(sql.toString(), null);
			pstm.setInt(1, clientId);
			pstm.setInt(2, deviceId);
			rs = pstm.executeQuery();
			while (rs.next()) {
				GetIOTDataById data = response.addNewGetIOTDataById();
				String name = rs.getString("name");
				String uuid = rs.getString("uuid");
				String frequency = rs.getString("roomFrequency");
				String roomNo = rs.getString("roomNo");
				String cornorType = rs.getString("cornorType");
				String sensorType = rs.getString("sensorType");

				data.setDeviceId(deviceId);
				data.setUuid(uuid);
				data.setName(name != null ? name : "");
				data.setFrequency(frequency != null ? frequency : "");
				data.setRoomNo(roomNo != null ? roomNo : "");
				data.setCornorType(cornorType != null ? cornorType : "");
				data.setSensorType(sensorType != null ? sensorType : "");
			}
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			response.setError(INTERNAL_SERVER_ERROR);
			response.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			if (trx != null)
				trx.close();

			closeDbCon(pstm, rs);
			getCompiereService().disconnect();
		}
		return res;
	}
	
	
//	@Override
	
//	public Response uploadfile(
//	        @FormDataParam("file") InputStream fileInputStream,
//	        @FormDataParam("file") FormDataContentDisposition fileDetail,
//	        @QueryParam("tableId") int tableId,
//	        @QueryParam("recordId") int recordId) {
//
//	    JSONObject object = new JSONObject();
//	    Trx trx = null;
//
//	    try {
//
//	        Properties ctx = Env.getCtx();
//	        int clientId = Env.getAD_Client_ID(ctx);
//	        int orgId = Env.getAD_Org_ID(ctx);
//
//	        String trxName = Trx.createTrxName("UPLOAD_");
//	        trx = Trx.get(trxName, true);
//	        trx.start();
//
//	        MTable table = new MTable(ctx, tableId, trxName);
//	        PO po = table.getPO(recordId, trxName);
//
//	        if (po == null || po.get_ID() == 0)
//	            return Response.status(404).entity("{\"error\":\"Record not found\"}").build();
//
//	        byte[] data = fileInputStream.readAllBytes();
//	        if (data.length == 0)
//	            return Response.status(400).entity("{\"error\":\"Empty file\"}").build();
//
//	        MAttachment attachment = po.getAttachment();
//	        if (attachment == null) {
//	            attachment = new MAttachment(ctx, tableId, recordId, trxName);
//	            attachment.setClientOrg(clientId, orgId);
//	        }
//
//	        attachment.addEntry(fileDetail.getFileName(), data);
//	        attachment.saveEx();
//
//	        trx.commit();
//
//	        object.put("success", true);
//	        object.put("attachmentId", attachment.get_ID());
//
//	    } catch (Exception e) {
//	        if (trx != null) trx.rollback();
//	        return Response.status(500)
//	            .entity("{\"error\":\"" + e.getMessage() + "\"}")
//	            .build();
//	    } finally {
//	        if (trx != null) trx.close();
//	    }
//
//	    return Response.ok(object.toString()).build();
//	}
	
//	public Response uploadfile(List<Attachment> attachments, int tableId, int recordId) {
//		Trx trx = null;
//		JSONObject object = new JSONObject();
//		
//		try {
//			initializeProps(Env.getCtx());
//			getCompiereService().connect();
//			CompiereService m_cs = getCompiereService();
//			Properties ctx = m_cs.getCtx();
//			setCtxProp(ctx);
//
//	        int clientId = Env.getAD_Client_ID(ctx);
//	        int orgId = Env.getAD_Org_ID(ctx);
//
//	        String trxName = Trx.createTrxName("UPLOAD_");
//	        trx = Trx.get(trxName, true);
//	        trx.start();
//
////	        for (Attachment att : attachmentsents) {
////	            InputStream fileInputStream = att.getDataHandler().getInputStream();
////	            String fileName = att.getContentDisposition().getParameter("filename");
//
//	            MTable table = new MTable(ctx, tableId, trxName);
//	            if (table == null || table.get_ID() == 0)
//	                return Response.status(Status.NOT_FOUND).entity("{\"error\":\"Table not found\"}").type("application/json").build();
//
//	            PO po = table.getPO(recordId, trxName);
//	            if (po == null || po.get_ID() == 0)
//	                return Response.status(Status.NOT_FOUND).entity("{\"error\":\"Record not found\"}").type("application/json").build();
//
//	            MAttachment attachment = new MAttachment(ctx, tableId, recordId, trxName);
//	            attachment.setClientOrg(clientId, orgId);
//
//	            for (Attachment att : attachments) {
//
//	                InputStream fileInputStream = att.getDataHandler().getInputStream();
//	                String fileName = att.getContentDisposition().getParameter("filename");
//	                byte[] data = inputStreamToByteArray(fileInputStream);
////	                byte[] data = is.readAllBytes();    // SAFE in Idempiere 10+
//
//	                if (data.length == 0)
//	                    return Response.status(400).entity("{\"error\":\"Empty file\"}").build();
//
//	                // add image entry
//	                attachment.addEntry(fileName, data);
//	            }
//
//	            attachment.saveEx();
//	            trx.commit();
//
//	            object.put("success", true);
//	            object.put("attachmentId", attachment.get_ID());
//	            
////	            DataHandler handler = att.getDataHandler();
////	            ByteArrayOutputStream baos = new ByteArrayOutputStream();
////	            handler.writeTo(baos);
////	            byte[] data = baos.toByteArray();
//	            
////	            byte[] data = inputStreamToByteArray(fileInputStream);
////	            if (data == null || data.length == 0)
////	                return Response.status(Status.BAD_REQUEST).entity("{\"error\":\"Empty file\"}").type("application/json").build();
//
////	            MAttachment attachment = po.getAttachment();
////	            if (attachment == null) {
////	                attachment = new MAttachment(ctx, tableId, recordId, trxName);
////	                attachment.setClientOrg(clientId, orgId);
////	            } else {
////	                attachment.set_TrxName(trxName);
////	            }
////
////	            attachment.addEntry(fileName, data);
////	            attachment.saveEx();
////
////	            object.put("success", true);
////	            object.put("attachmentId", attachment.get_ID());
////	        }
//
//	        trx.commit();
//		} catch (Exception e) {
//			if (trx != null) trx.rollback();
//			log.saveError("Error in uploadfile: ", e);
//			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
//					.entity("{\"error\":\"" + e.getMessage().replace("\"", "\\\"" ) + "\"}")
//					.type("application/json")
//					.build();
//		} finally {
//			if (trx != null)
//				trx.close();
//			getCompiereService().disconnect();
//		}
//        return Response.ok(object.toString()).type("application/json").build();
//	}
	
	private byte[] inputStreamToByteArray(InputStream inputStream) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;

        try {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return byteArrayOutputStream.toByteArray();
    }

	@Override
	public Response getFORoleReportSuckerCountDownload(GetFORoleReportSuckerCountDownloadRequestDocument req) {
		GetFORoleReportSuckerCountDownloadRequest loginRequest = req.getGetFORoleReportSuckerCountDownloadRequest();
		String userInput = loginRequest.getUserInput();
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		PreparedStatement pstm = null;
		ResultSet rs = null;
		Trx trx = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
			String user = safeTrim(new MUser(ctx, Env.getAD_User_ID(ctx), null).getName());
			int clientId = Env.getAD_Client_ID(ctx);
			
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			SXSSFWorkbook workbook = new SXSSFWorkbook(-1);

			CellStyle myStyle = workbook.createCellStyle();
			org.apache.poi.ss.usermodel.Font myFont = workbook.createFont();
			myFont.setBold(true);
			myStyle.setFont(myFont);

			CellStyle wrapStyle = workbook.createCellStyle();
			wrapStyle.setWrapText(true);
			// Create sheet and rows
			SXSSFSheet sheet = workbook.createSheet("Field Officer Sucker Count Report");
			SXSSFRow headerRow = sheet.createRow(0);

			// Create header cells
			SXSSFCell cellH0 = headerRow.createCell(0);
			cellH0.setCellValue("Label Name");
			cellH0.setCellStyle(myStyle);

			SXSSFCell cellH1 = headerRow.createCell(1);
			cellH1.setCellValue("Count");
			cellH1.setCellStyle(myStyle);
			String sql = null;

			if (userInput.equals("day")) {
				sql = "SELECT day_info.day_name AS day_name,current_date AS date,COALESCE(SUM(cv.suckerno), 0) AS sucker_count\n"
						+ "FROM (SELECT to_char(current_date, 'Day') AS day_name) AS day_info\n"
						+ "LEFT JOIN adempiere.tc_collectionjoinplant cv ON cv.created::date = current_date AND cv.ad_client_id = ?\n"
						+ "AND cv.createdby IN (SELECT ad_user_id FROM adempiere.ad_user WHERE name = ?) GROUP BY day_info.day_name;";
			} else if (userInput.equals("week")) {
				sql = "WITH days AS (SELECT generate_series(0, 6) AS day_of_week),\n" + "sucker_counts AS (\n"
						+ "SELECT date_trunc('day', v.created) AS visit_day,to_char(v.created, 'FMDay') AS day_name,\n"
						+ "EXTRACT(dow FROM v.created) AS day_of_week,SUM(v.suckerno) AS visit_count\n"
						+ "FROM adempiere.tc_collectionjoinplant v JOIN adempiere.ad_user u ON u.ad_user_id = v.createdby\n"
						+ "WHERE v.ad_client_id = ? AND u.name = ? AND v.created::date >= current_date - interval '6 days' AND v.created::date <= current_date\n"
						+ "GROUP BY date_trunc('day', v.created),to_char(v.created, 'FMDay'),EXTRACT(dow FROM v.created))\n"
						+ "SELECT (current_date - interval '6 days' + d.day_of_week * interval '1 day')::date AS dates,\n"
						+ "COALESCE(vc.day_name, to_char(current_date - interval '6 days' + d.day_of_week * interval '1 day', 'FMDay')) AS day_name,\n"
						+ "COALESCE(vc.visit_count, 0) AS sucker_count FROM days d\n"
						+ "LEFT JOIN sucker_counts vc ON current_date - interval '6 days' + d.day_of_week * interval '1 day' = vc.visit_day ORDER BY dates;";
			} else if (userInput.equals("month")) {
				sql = "WITH weeks AS (SELECT generate_series(0, 4) AS week_number),\n"
						+ "sucker_counts AS (SELECT date_trunc('week', v.created) AS week_start,to_char(date_trunc('week', v.created), 'YYYY-MM-DD') AS week_start_str,\n"
						+ "SUM(v.suckerno) AS sucker_count FROM adempiere.tc_collectionjoinplant v JOIN adempiere.ad_user u ON u.ad_user_id = v.createdby\n"
						+ "WHERE v.ad_client_id = ? AND u.name = ? AND v.created::date >= (current_date - interval '29 days') AND v.created::date <= current_date GROUP BY date_trunc('week', v.created)),\n"
						+ "date_range AS (SELECT (current_date - interval '29 days')::date + generate_series(0, 29) AS day)\n"
						+ "SELECT to_char(date_trunc('week', day), 'YYYY-MM-DD') AS week_start,COALESCE(vc.sucker_count, 0) AS sucker_count\n"
						+ "FROM date_range LEFT JOIN sucker_counts vc ON date_trunc('week', day) = vc.week_start\n"
						+ "GROUP BY date_trunc('week', day), vc.sucker_count ORDER BY week_start;\n" + "";
			} else if (userInput.equals("year")) {
				sql = "WITH months AS (SELECT generate_series(0, 11) AS month),\n" + "sucker_counts AS (\n"
						+ "SELECT date_trunc('month', v.created) AS month_year,to_char(v.created, 'FMMonth') AS month_name,SUM(v.suckerno) AS sucker_count\n"
						+ "FROM adempiere.tc_collectionjoinplant v JOIN adempiere.ad_user u ON u.ad_user_id = v.createdby\n"
						+ "WHERE v.ad_client_id = ? AND u.name = ? AND v.created::date >= (current_date - interval '364 days') \n"
						+ "AND v.created::date <= current_date GROUP BY date_trunc('month', v.created), to_char(v.created, 'FMMonth'))\n"
						+ "SELECT to_char(date_trunc('month', current_date) - (m.month || ' months')::interval, 'YYYY-MM-01') AS month_date,COALESCE(vc.sucker_count, 0) AS sucker_count \n"
						+ "FROM months m LEFT JOIN sucker_counts vc \n"
						+ "ON date_trunc('month', current_date) - (m.month || ' months')::interval = vc.month_year\n"
						+ "ORDER BY date_trunc('month', current_date) - (m.month || ' months')::interval;";
			} else if (userInput.equals("all")) {
				sql = "WITH year_counts AS (\n"
						+ "SELECT date_trunc('year', v.created) AS year_start, sum(v.suckerno) AS counts FROM adempiere.tc_collectionjoinplant v\n"
						+ "JOIN adempiere.ad_user u ON u.ad_user_id = v.createdby WHERE v.ad_client_id = ? AND u.name = ?\n" + "GROUP BY date_trunc('year', v.created)),\n"
						+ "year_range AS (SELECT date_trunc('year', CURRENT_DATE) AS year_start\n" + "UNION ALL\n"
						+ "SELECT generate_series((SELECT MIN(year_start) FROM year_counts),date_trunc('year', CURRENT_DATE),interval '1 year') AS year_start\n"
						+ "),\n" + "all_years AS (SELECT DISTINCT year_start FROM year_range)\n"
						+ "SELECT to_char(a.year_start, 'YYYY-01-01') AS year_date, COALESCE(y.counts, 0) AS counts FROM all_years a\n"
						+ "LEFT JOIN year_counts y ON a.year_start = y.year_start ORDER BY a.year_start;";
			}
			pstm = DB.prepareStatement(sql.toString(), null);
			pstm.setInt(1, clientId);
			pstm.setString(2, user);
			rs = pstm.executeQuery();
			int i = 1;
			while (rs.next()) {
				if (userInput.equals("day")) {
					String date = rs.getString("date");
					int count = rs.getInt("sucker_count");

					Row row = sheet.createRow(i++);
					row.createCell(0).setCellValue(date);
					row.createCell(1).setCellValue(count);
				} else if (userInput.equals("week")) {
					String dates = rs.getString("dates");
					int count = rs.getInt("sucker_count");

					Row row = sheet.createRow(i++);
					row.createCell(0).setCellValue(dates);
					row.createCell(1).setCellValue(count);
				} else if (userInput.equals("month")) {
					String week_start = rs.getString("week_start");
					int count = rs.getInt("sucker_count");

					Row row = sheet.createRow(i++);
					row.createCell(0).setCellValue(week_start);
					row.createCell(1).setCellValue(count);
				} else if (userInput.equals("year")) {
					String month_date = rs.getString("month_date");
					int count = rs.getInt("sucker_count");

					Row row = sheet.createRow(i++);
					row.createCell(0).setCellValue(month_date);
					row.createCell(1).setCellValue(count);
				} else if (userInput.equals("all")) {
					String year_date = rs.getString("year_date");
					int count = rs.getInt("counts");

					Row row = sheet.createRow(i++);
					row.createCell(0).setCellValue(year_date);
					row.createCell(1).setCellValue(count);
				}
				i++;
			}
			workbook.write(byteArrayOutputStream);
			workbook.close();
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			log.severe("Error fetching data: " + e.getMessage());
			e.printStackTrace();
			log.saveError("Error : " , e);
		} finally {
			closeDbCon(pstm, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		byte[] excelFileContent = byteArrayOutputStream.toByteArray();
		return Response.ok(excelFileContent).type("field_officer_report/xlsx")
				.header("Content-Disposition", "inline; filename=\"field_officer_report.xlsx\"")
//				            .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
				.build();

	}

	@Override
	public Response getFORoleReportVisitCountDownload(GetFORoleReportVisitCountDownloadRequestDocument req) {
		GetFORoleReportVisitCountDownloadRequest loginRequest = req.getGetFORoleReportVisitCountDownloadRequest();
		String userInput = loginRequest.getUserInput();
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		PreparedStatement pstm = null;
		ResultSet rs = null;
		Trx trx = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
			String user = safeTrim(new MUser(ctx, Env.getAD_User_ID(ctx), null).getName());
		int clientId = Env.getAD_Client_ID(ctx);

			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			SXSSFWorkbook workbook = new SXSSFWorkbook(-1);

			CellStyle myStyle = workbook.createCellStyle();
			org.apache.poi.ss.usermodel.Font myFont = workbook.createFont();
			myFont.setBold(true);
			myStyle.setFont(myFont);

			CellStyle wrapStyle = workbook.createCellStyle();
			wrapStyle.setWrapText(true);
			// Create sheet and rows
			SXSSFSheet sheet = workbook.createSheet("Field Officer Visit Count Report");
			SXSSFRow headerRow = sheet.createRow(0);

			// Create header cells
			SXSSFCell cellH0 = headerRow.createCell(0);
			cellH0.setCellValue("Label Name");
			cellH0.setCellStyle(myStyle);

			SXSSFCell cellH1 = headerRow.createCell(1);
			cellH1.setCellValue("Count");
			cellH1.setCellStyle(myStyle);
			String sql = null;

			if (userInput.equals("day")) {
				sql = "SELECT day_info.day_name AS day_name,current_date AS date,COALESCE(COUNT(v.*), 0) AS visit_count\n"
						+ "FROM (SELECT to_char(current_date, 'Day') AS day_name) AS day_info\n"
						+ "LEFT JOIN adempiere.tc_visit v ON v.date::date = current_date \n" + "AND v.ad_client_id = ? AND v.createdby IN (\n"
						+ "SELECT ad_user_id FROM adempiere.ad_user WHERE name = ?)\n"
						+ "GROUP BY day_info.day_name;";
			} else if (userInput.equals("week")) {
				sql = "WITH days AS (SELECT generate_series(0, 6) AS day_of_week),\n"
						+ "visit_counts AS (SELECT date_trunc('day', v.date) AS visit_day,to_char(v.date, 'FMDay') AS day_name,v.date AS dates,\n"
						+ "EXTRACT(dow FROM v.date) AS day_of_week,COUNT(*) AS visit_count FROM adempiere.tc_visit v\n"
						+ "JOIN adempiere.ad_user u ON u.ad_user_id = v.createdby WHERE v.ad_client_id = ?\n" + "AND u.name = ? AND v.date >= current_date - interval '6 days' AND v.date <= current_date\n"
						+ "GROUP BY date_trunc('day', v.date),to_char(v.date, 'FMDay'),EXTRACT(dow FROM v.date),v.date)\n"
						+ "SELECT (current_date - interval '6 days' + d.day_of_week * interval '1 day')::date AS dates,\n"
						+ "COALESCE(vc.day_name, to_char(current_date - interval '6 days' + d.day_of_week * interval '1 day', 'FMDay')) AS day_name,\n"
						+ "COALESCE(vc.visit_count, 0) AS visit_count FROM days d\n"
						+ "LEFT JOIN visit_counts vc ON date_trunc('day', current_date - interval '6 days' + d.day_of_week * interval '1 day') = vc.visit_day\n"
						+ "ORDER BY dates;";
			} else if (userInput.equals("month")) {
				sql = "WITH weeks AS (SELECT generate_series(0, 4) AS week_number),\n"
						+ "visit_counts AS (SELECT date_trunc('week', v.date) AS week_start,to_char(date_trunc('week', v.date), 'YYYY-MM-DD') AS week_start_str,\n"
						+ "COUNT(*) AS visit_count FROM adempiere.tc_visit v JOIN adempiere.ad_user u ON u.ad_user_id = v.createdby\n"
						+ "WHERE v.ad_client_id = ? AND u.name = ? AND v.date >= (current_date - interval '29 days')\n"
						+ "AND v.date <= current_date GROUP BY date_trunc('week', v.date)),\n"
						+ "date_range AS (SELECT (current_date - interval '29 days')::date + generate_series(0, 29) AS day)\n"
						+ "SELECT to_char(date_trunc('week', day), 'YYYY-MM-DD') AS week_start,COALESCE(vc.visit_count, 0) AS visit_count\n"
						+ "FROM date_range LEFT JOIN visit_counts vc ON date_trunc('week', day) = vc.week_start\n"
						+ "GROUP BY date_trunc('week', day), vc.visit_count ORDER BY week_start;";
			} else if (userInput.equals("year")) {
				sql = "WITH months AS (SELECT generate_series(0, 11) AS month),\n"
						+ "visit_counts AS (SELECT date_trunc('month', v.date) AS month_year,to_char(v.date, 'FMMonth') AS month_name,COUNT(*) AS visit_count\n"
						+ "FROM adempiere.tc_visit v JOIN adempiere.ad_user u ON u.ad_user_id = v.createdby\n"
						+ "WHERE v.ad_client_id = ? AND u.name = ? AND v.date >= (current_date - interval '364 days')\n"
						+ "AND v.date <= current_date GROUP BY date_trunc('month', v.date),to_char(v.date, 'FMMonth'))\n"
						+ "SELECT to_char(date_trunc('month', current_date) - (m.month || ' months')::interval, 'YYYY-MM-01') AS month_date,\n"
						+ "COALESCE(vc.visit_count, 0) AS visit_count FROM months m LEFT JOIN visit_counts vc ON \n"
						+ "date_trunc('month', current_date) - (m.month || ' months')::interval = vc.month_year \n"
						+ "ORDER BY date_trunc('month', current_date) - (m.month || ' months')::interval;";
			} else if (userInput.equals("all")) {
				sql = "WITH year_counts AS (SELECT date_trunc('year', v.created) AS year_start,COUNT(*) AS counts FROM adempiere.tc_visit v\n"
						+ "JOIN adempiere.ad_user u ON u.ad_user_id = v.createdby WHERE v.ad_client_id = ?\n" + "AND u.name = ? GROUP BY date_trunc('year', v.created)),\n"
						+ "year_range AS (SELECT date_trunc('year', CURRENT_DATE) AS year_start\n"
						+ "UNION ALL SELECT generate_series((SELECT MIN(year_start) FROM year_counts),date_trunc('year', CURRENT_DATE),\n"
						+ "interval '1 year') AS year_start),all_years AS (SELECT DISTINCT year_start FROM year_range)\n"
						+ "SELECT to_char(a.year_start, 'YYYY-01-01') AS year_date,COALESCE(y.counts, 0) AS counts\n"
						+ "FROM all_years a LEFT JOIN year_counts y ON a.year_start = y.year_start ORDER BY a.year_start;";
			}
			pstm = DB.prepareStatement(sql, null);
			pstm.setInt(1, clientId);
			pstm.setString(2, user);
			rs = pstm.executeQuery();
			int i = 1;
			while (rs.next()) {
				if (userInput.equals("day")) {
					String date = rs.getString("date");
					int count = rs.getInt("visit_count");

					Row row = sheet.createRow(i++);
					row.createCell(0).setCellValue(date);
					row.createCell(1).setCellValue(count);
				} else if (userInput.equals("week")) {
					String dates = rs.getString("dates");
					int count = rs.getInt("visit_count");

					Row row = sheet.createRow(i++);
					row.createCell(0).setCellValue(dates);
					row.createCell(1).setCellValue(count);
				} else if (userInput.equals("month")) {
					String week_start = rs.getString("week_start");
					int count = rs.getInt("visit_count");

					Row row = sheet.createRow(i++);
					row.createCell(0).setCellValue(week_start);
					row.createCell(1).setCellValue(count);
				} else if (userInput.equals("year")) {
					String month_date = rs.getString("month_date");
					int count = rs.getInt("visit_count");

					Row row = sheet.createRow(i++);
					row.createCell(0).setCellValue(month_date);
					row.createCell(1).setCellValue(count);
				} else if (userInput.equals("all")) {
					String year_date = rs.getString("year_date");
					int count = rs.getInt("counts");

					Row row = sheet.createRow(i++);
					row.createCell(0).setCellValue(year_date);
					row.createCell(1).setCellValue(count);
				}
				i++;
			}
			workbook.write(byteArrayOutputStream);
			workbook.close();
		} catch (Exception e) {
			if (trx != null) trx.rollback();
			log.severe("Error fetching data: " + e.getMessage());
		} finally {
			closeDbCon(pstm, rs);
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		byte[] excelFileContent = byteArrayOutputStream.toByteArray();
		return Response.ok(excelFileContent).type("field_officer_report/xlsx")
				.header("Content-Disposition", "inline; filename=\"field_officer_report.xlsx\"").build();
	}

	@Override
	public AddLightRecordResponseDocument addLightRecord(AddLightRecordRequestDocument req) {
		AddLightRecordResponseDocument response = AddLightRecordResponseDocument.Factory.newInstance();
		AddLightRecordResponse res = response.addNewAddLightRecordResponse();
		AddLightRecordRequest loginRequest = req.getAddLightRecordRequest();
		int deviceId = 0;
		String lightstatus = safeTrim(loginRequest.getLightStatus());
		String time = safeTrim(loginRequest.getTime());
		String appearance = safeTrim(loginRequest.getAppearance());
		String userTimestampStr = safeTrim(loginRequest.getCustomTimestamp());
		Trx trx = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
		int clientId = Env.getAD_Client_ID(ctx);
		int orgId = Env.getAD_Org_ID(ctx);

		        String trxName = Trx.createTrxName(getClass().getName() + "_");
		        trx = Trx.get(trxName, true);
		        trx.start();
			
			deviceId = loginRequest.getDeviceId();
			
			if (!TCUtills.isValidNameNumber(lightstatus)) {
				res.setError("Invalid Light Status format");
				res.setIsError(true);
				return response;
	        }
			
			if (containsMaliciousPattern(lightstatus)) {
				res.setError("Invalid input: Base64 or URL-encoded text not allowed");
				res.setIsError(true);
			    return response;
			}
			
			if (containsMaliciousPattern(time)) {
				res.setError("Invalid input: Base64 or URL-encoded text not allowed");
				res.setIsError(true);
			    return response;
			}
			
			if (containsMaliciousPattern(appearance)) {
				res.setError("Invalid input: Base64 or URL-encoded text not allowed");
				res.setIsError(true);
			    return response;
			}
			
			if (containsMaliciousPattern(userTimestampStr)) {
				res.setError("Invalid input: Base64 or URL-encoded text not allowed");
				res.setIsError(true);
			    return response;
			}

			
			int lightstatusId = TCUtills.getRecordId(clientId, TABLE_LIGHT_STATUS, lightstatus);
			if (lightstatusId == 0) {
				res.setError("Light Status is not in table record " + lightstatus + "");
				res.setIsError(true);
				return response;
			}
			
			Timestamp userTimestamp = null;
			if (userTimestampStr != null && !userTimestampStr.trim().isEmpty()) {
			    try {
			        if (userTimestampStr.contains("T")) {
			            userTimestamp = Timestamp.valueOf(
			                userTimestampStr.replace("T", " ")
			            );
			        } else {
			            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			            LocalDateTime localDateTime = LocalDateTime.parse(userTimestampStr, formatter);
			            userTimestamp = Timestamp.valueOf(localDateTime);
			        }
			    } catch (Exception e) {
			    	if (trx != null) trx.rollback();
			    	res.setError("Invalid timestamp format. Use yyyy-MM-dd HH:mm:ss or ISO 8601 format");
			        log.saveError("Error: ", e);
			        res.setIsError(true);
			        return response;
			    }
			}
		        
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

			
			LocalDateTime afterTrx = LocalDateTime.now();
			log.info("Trx Started at: " + afterTrx.format(formatter));

			TCIOTdeviceData data = new TCIOTdeviceData(ctx, deviceId, trx.getTrxName());
			if (data == null || data.get_ID() == 0) {
				res.setError("Device Id not available in Database please enter valid Device Id " + deviceId + "");
				res.setIsError(true);
				trx.rollback();
				return response;
			}

			int roomId = data.getM_LocatorType_ID();

			X_tc_light record = new X_tc_light(ctx, 0, trx.getTrxName());
			record.setAD_Org_ID(orgId);
			record.setM_LocatorType_ID(roomId);
			record.setTC_LightStatus_ID(lightstatusId);
			record.setlighton(time);
			record.setName(data.getValue());
			record.setTC_devicedata_ID(deviceId);
			
	        if (userTimestamp != null) {
	            record.setcustom_timestamp(userTimestamp);
	        }
			
			if (appearance != null && !appearance.trim().isEmpty()) {
			    BigDecimal appeVal = new BigDecimal(appearance.trim());
			    record.setappearance(appeVal.setScale(2, RoundingMode.HALF_UP).toPlainString());
			}
			record.saveEx();
			trx.commit();
			int lightId = record.gettc_light_ID();
			res.setRecordId(lightId);
			res.setIsError(false);
			
			LocalDateTime afterTrxCompleted = LocalDateTime.now();
			log.info("Trx Started at: " + afterTrxCompleted.format(formatter));
			
		} catch (Exception e) {
			log.saveError("Error : " , e);
			if (trx != null) trx.rollback();
			res.setError(INTERNAL_SERVER_ERROR);
			res.setIsError(true);
			log.saveError("Error : " , e);
		} finally {
			if (trx != null) {
				trx.close();
			}

			getCompiereService().disconnect();
		}
		return response;
	}

	@Override
	public GenerateTokenAndSendMailNewResponseDocument generateTokenAndSendMailNew(
			GenerateTokenAndSendMailNewRequestDocument req) {
		GenerateTokenAndSendMailNewResponseDocument responseDoc = GenerateTokenAndSendMailNewResponseDocument.Factory.newInstance();
		GenerateTokenAndSendMailNewResponse response = responseDoc.addNewGenerateTokenAndSendMailNewResponse();

		GenerateTokenAndSendMailNewRequest loginRequest = req.getGenerateTokenAndSendMailNewRequest();
	    String userName = safeTrim(loginRequest.getUserName());

	    PreparedStatement pstm = null;
	    ResultSet rs = null;
	    int userId = 0;
	    int TOKEN_EXPIRATION_TIMES = 24 * 3600 * 1000;
	    Trx trx = null;

	    try {
			initializeProps(Env.getCtx());
	    	getCompiereService().connect();
	    	CompiereService m_cs = getCompiereService();
	    	Properties ctx = m_cs.getCtx();
	    	setCtxProp(ctx);
	        String trxName = Trx.createTrxName(getClass().getName() + "_");
	        trx = Trx.get(trxName, true);
	        trx.start();

	        String fullURL = httpServletRequest.getRequestURL().toString();
	        String contextPath = httpServletRequest.getContextPath();
	        String baseURL = fullURL.substring(0, fullURL.indexOf(contextPath) + contextPath.length());
	        String publicBaseURL = baseURL.replaceFirst("/ADInterface.*", "/reset-password");

	        String sql = "select ad_user_id As id from adempiere.ad_user where name = ?;";
	        pstm = DB.prepareStatement(sql, null);
	        pstm.setString(1, userName);
	        rs = pstm.executeQuery();
	        while (rs.next()) {
	            userId = rs.getInt("id");
	        }
	        if (userId == 0) {
	            response.setError("userName is not Valid, Please check userName: " + userName);
	            response.setIsError(true);
	            return responseDoc;
	        }

	        MUser user = new MUser(ctx, userId, trx.getTrxName());
	        if (user.getEMail() == null) {
	            response.setError("This user does not have an email: " + userName);
	            response.setIsError(true);
	            return responseDoc;
	        }

	        String token = generateToken();
	        long expirationTime = System.currentTimeMillis() + TOKEN_EXPIRATION_TIMES;
	        Timestamp timestamp = new Timestamp(expirationTime);

	        EmailService emailService = new EmailService();
	        emailService.sendEmail(user, token, publicBaseURL);

	        String updateSQL = "UPDATE AD_User SET token=?, tokentime=? WHERE AD_User_ID=?";
	        DB.executeUpdate(updateSQL, new Object[]{token, timestamp, user.getAD_User_ID()}, false, null);
	        response.setToken(token);

	    } catch (Exception e) {
	    	if (trx != null) trx.rollback();
	        e.printStackTrace();
	        response.setError(INTERNAL_SERVER_ERROR);
	        response.setIsError(true);
	        log.saveError("Error : ", e);
	    } finally {
	        closeDbCon(pstm, rs);
	        if (trx != null) trx.close();
	    getCompiereService().disconnect();
	    }
	    return responseDoc;
	}


	/**
	 * @param parameters
	 * @param userName
	 * @param clients
	 * @return
	 */
	private String processLoginParameters(int clientId, int roleId, int orgId, int warehouseId, String userName, boolean refreshToken) {
		Builder builder = JWT.create().withSubject(userName);
		String defaultLanguage = Language.getBaseAD_Language();

		builder.withClaim(LoginClaims.AD_Client_ID.name(), clientId);
		Env.setContext(Env.getCtx(), Env.AD_CLIENT_ID, clientId);
		MUser user = MUser.get(Env.getCtx(), userName);
		builder.withClaim(LoginClaims.AD_User_ID.name(), user.getAD_User_ID());
		defaultLanguage = getPreferenceUserLanguage(user.getAD_User_ID());

		String errorMessage = validateLoginParameters(userName, clientId, roleId, orgId, warehouseId);

		if (Util.isEmpty(errorMessage)) {
			builder.withClaim(LoginClaims.AD_Role_ID.name(), roleId);
			builder.withClaim(LoginClaims.AD_Org_ID.name(), orgId);
			if (orgId > 0 && warehouseId > 0)
				builder.withClaim(LoginClaims.M_Warehouse_ID.name(), warehouseId);
		} else {
			return null;
		}

		builder.withClaim(LoginClaims.AD_Language.name(), defaultLanguage);

		// Create AD_Session here and set the session in the token as another parameter
		MSession session = MSession.get(Env.getCtx());
		if (session == null) {
			session = MSession.create(Env.getCtx());
			session.setWebSession("idempiere-rest");
			session.saveEx();
		}
		builder.withClaim(LoginClaims.AD_Session_ID.name(), session.getAD_Session_ID());

		Timestamp expiresAt = null;
		if (refreshToken)
			expiresAt = TokenUtils.getRefreshTokenExpiresAt();
		else
			expiresAt = TokenUtils.getTokenExpiresAt();
		builder.withIssuer(TokenUtils.getTokenIssuer()).withExpiresAt(expiresAt).withKeyId(TokenUtils.getTokenKeyId());
		String token = null;
		try {
			 token = builder.sign(Algorithm.HMAC512(TokenUtils.getTokenSecret()));
		} catch (IllegalArgumentException | JWTCreationException e) {
			e.printStackTrace();
			return null;
		}
		
		return token;
	}



	private String validateLoginParameters(String userName, int clientId, int roleId, int orgId, int warehouseId) {
		MClient client = MClient.get(Env.getCtx(), clientId);
		KeyNamePair clientKeyNamePair = new KeyNamePair(client.getAD_Client_ID(), client.getName());
		Login login = new Login(Env.getCtx());
		KeyNamePair[] roles = login.getRoles(userName, clientKeyNamePair, ROLE_TYPES_WEBSERVICE);
		boolean isValidRole = isValidRole(roleId, roles);

		Env.setContext(Env.getCtx(),  RequestFilter.LOGIN_NAME, userName);
		
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
	
	@Override
	public GetTraceabilityDataForQRResponseDocument getTraceabilityDataForQR(
			GetTraceabilityDataForQRRequestDocument req) {
		GetTraceabilityDataForQRResponseDocument response = GetTraceabilityDataForQRResponseDocument.Factory.newInstance();
		GetTraceabilityDataForQRResponse res = response.addNewGetTraceabilityDataForQRResponse();
		GetTraceabilityDataForQRRequest loginReq = req.getGetTraceabilityDataForQRRequest();
		Trx trx = null;
		String plantUUId = "";
		String dateHardening = "";
		String parentCultureLine = "";
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
			
			String cultureLabelUUid = safeTrim(loginReq.getLabelUUId());
	        if (cultureLabelUUid.isEmpty()) {
	            res.setError("labelUUId is required and cannot be empty");
	            res.setIsError(true);
	            return response;
	        }
	        
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();		
			
			int shId = TCUtills.getIds(TABLE_SH, cultureLabelUUid);
	        int phId = TCUtills.getIds(TABLE_PH, cultureLabelUUid);

	        if (shId == 0 && phId == 0) {
	            res.setError("The Secondary Hardening UUID or Primary Hardening UUID do not exist. Please check the UUID you entered.");
	            res.setIsError(true);
	            return response;
	        }
	        
	        final String sql = "WITH RECURSIVE cte AS (SELECT cl.parentuuid,cl.c_uuid,cl.created,var.name AS variety\n"
					+ "FROM adempiere.tc_culturelabel cl\n"
					+ "JOIN adempiere.tc_variety var ON var.tc_variety_id = cl.tc_variety_id\n"
					+ "WHERE cl.c_uuid = ?\n"
					+ "UNION ALL\n"
					+ "SELECT phs.cultureuuid AS parentuuid,cl.c_uuid,cl.created,var.name AS variety\n"
					+ "FROM adempiere.TC_PrimaryHardeningLabel ph JOIN adempiere.tc_primaryHardeningcultureS phs \n"
					+ "ON phs.TC_PrimaryHardeningLabel_id = ph.TC_PrimaryHardeningLabel_id\n"
					+ "JOIN adempiere.tc_culturelabel cl ON phs.cultureuuid = cl.c_uuid\n"
					+ "JOIN adempiere.tc_culturestage cs ON cs.tc_culturestage_id = cl.tc_culturestage_id\n"
					+ "JOIN adempiere.tc_variety var ON var.tc_variety_id = cl.tc_variety_id\n"
					+ "WHERE ph.c_uuid = ?\n"
					+ "UNION ALL\n"
					+ "SELECT phs.cultureuuid AS parentuuid,cl.c_uuid,cl.created,var.name AS variety\n"
					+ "FROM adempiere.TC_SecondaryHardeningLabel sh JOIN adempiere.TC_PrimaryHardeningLabel ph ON sh.parentuuid = ph.c_uuid\n"
					+ "JOIN adempiere.tc_primaryHardeningcultureS phs ON phs.TC_PrimaryHardeningLabel_id = ph.TC_PrimaryHardeningLabel_id\n"
					+ "JOIN adempiere.tc_culturelabel cl ON phs.cultureuuid = cl.c_uuid\n"
					+ "JOIN adempiere.tc_culturestage cs ON cs.tc_culturestage_id = cl.tc_culturestage_id\n"
					+ "JOIN adempiere.tc_variety var ON var.tc_variety_id = cl.tc_variety_id\n"
					+ "WHERE sh.c_uuid = ?\n"
					+ "UNION ALL\n"
					+ "SELECT cl2.parentuuid,cl2.c_uuid,cl2.created,var.name AS variety\n"
					+ "FROM cte JOIN adempiere.tc_culturelabel cl2 ON cte.parentuuid = cl2.c_uuid\n"
					+ "JOIN adempiere.tc_variety var ON var.tc_variety_id = cl2.tc_variety_id),\n"
					+ "culture_result AS (SELECT cte.parentuuid,cte.c_uuid,cte.created,cte.variety\n"
					+ "FROM cte GROUP BY cte.parentuuid, cte.c_uuid,cte.created, cte.variety\n"
					+ "UNION ALL\n"
					+ "SELECT DISTINCT tcc.parentuuid,tcc.c_uuid,tcc.created,cte.variety\n"
					+ "FROM cte LEFT JOIN adempiere.tc_explantlabel tcc ON cte.parentuuid = tcc.c_uuid\n"
					+ "UNION ALL\n"
					+ "SELECT DISTINCT NULL, tpt.c_uuid, tpt.created, cte.variety\n"
					+ "FROM cte LEFT JOIN adempiere.tc_explantlabel tcc ON cte.parentuuid = tcc.c_uuid\n"
					+ "LEFT JOIN adempiere.tc_planttag tpt ON tcc.parentuuid = tpt.c_uuid WHERE tpt.c_uuid IS NOT NULL),\n"
					+ "explant_result AS (SELECT DISTINCT tcc.parentuuid,tcc.c_uuid,tcc.created,var.name AS variety\n"
					+ "FROM adempiere.tc_explantlabel tcc JOIN adempiere.tc_variety var ON var.tc_variety_id = tcc.tc_variety_id\n"
					+ "WHERE tcc.c_uuid = ?\n"
					+ "UNION ALL\n"
					+ "SELECT DISTINCT NULL AS parentuuid,tpt.c_uuid,tpt.created,var.name AS variety\n"
					+ "FROM adempiere.tc_planttag tpt JOIN adempiere.tc_explantlabel tcc ON tcc.parentuuid = tpt.c_uuid\n"
					+ "JOIN adempiere.tc_plantdetails pd ON pd.planttaguuid = tpt.c_uuid\n"
					+ "JOIN adempiere.tc_plantspecies ps ON ps.tc_plantspecies_id = pd.tc_species_id\n"
					+ "JOIN adempiere.tc_variety var ON var.tc_variety_id = pd.tc_variety_id\n"
					+ "WHERE tcc.c_uuid = ? AND tpt.c_uuid IS NOT NULL),\n"
					+ "plant_tag_result AS (SELECT DISTINCT NULL AS parentuuid,tpt.c_uuid,tpt.created,var.name AS variety\n"
					+ "FROM adempiere.tc_planttag tpt JOIN adempiere.tc_plantdetails pd ON pd.planttaguuid = tpt.c_uuid\n"
					+ "JOIN adempiere.tc_variety var ON var.tc_variety_id = pd.tc_variety_id\n"
					+ "WHERE tpt.c_uuid = ?),\n"
					+ "primary_result AS (SELECT phs.cultureuuid AS parentuuid,ph.c_uuid,ph.created,var.name AS variety\n"
					+ "FROM adempiere.TC_PrimaryHardeningLabel ph JOIN adempiere.tc_primaryHardeningcultureS phs ON phs.TC_PrimaryHardeningLabel_id = ph.TC_PrimaryHardeningLabel_id\n"
					+ "JOIN adempiere.tc_variety var ON var.tc_variety_id = ph.tc_variety_id\n"
					+ "WHERE ph.c_uuid = ?),\n"
					+ "secondary_result AS (SELECT sh.parentuuid,sh.c_uuid,sh.created,var.name AS variety\n"
					+ "FROM adempiere.TC_SecondaryHardeningLabel sh JOIN adempiere.tc_variety var ON var.tc_variety_id = sh.tc_variety_id\n"
					+ "WHERE sh.c_uuid = ?\n"
					+ "UNION ALL\n"
					+ "SELECT phs.cultureuuid AS parentuuid,ph.c_uuid,ph.created,var.name AS variety\n"
					+ "FROM adempiere.TC_SecondaryHardeningLabel sh JOIN adempiere.TC_PrimaryHardeningLabel ph ON sh.parentuuid = ph.c_uuid\n"
					+ "JOIN adempiere.tc_primaryHardeningcultureS phs ON phs.TC_PrimaryHardeningLabel_id = ph.TC_PrimaryHardeningLabel_id\n"
					+ "JOIN adempiere.tc_variety var ON var.tc_variety_id = ph.tc_variety_id\n"
					+ "WHERE sh.c_uuid = ?)\n"
					+ "SELECT * FROM secondary_result UNION ALL\n"
					+ "SELECT * FROM primary_result UNION ALL\n"
					+ "SELECT * FROM culture_result\n"
					+ "WHERE (parentuuid IS NULL OR parentuuid <> c_uuid) UNION ALL\n"
					+ "SELECT * FROM explant_result WHERE NOT EXISTS (SELECT 1 FROM culture_result) UNION ALL SELECT * FROM plant_tag_result\n"
					+ "WHERE NOT EXISTS (SELECT 1 FROM explant_result) AND NOT EXISTS (SELECT 1 FROM culture_result) ORDER BY created ASC limit 1;";
			
	        try (PreparedStatement pstm = DB.prepareStatement(sql, null)) {
	            for (int i = 1; i <= 9; i++) {
	                pstm.setString(i, cultureLabelUUid);
	            }
	            try (ResultSet rs = pstm.executeQuery()) {
	                if (!rs.isBeforeFirst()) {
	                    res.setIsError(false);
	                    trx.commit();
	                    return response;
	                }
	                if (rs.next()) {
	                    plantUUId = rs.getString("c_uuid");
	                    String variety = rs.getString("variety");
	                    res.setVariety(variety != null ? variety : "");
	                }
	            }
	        }
			
			PO plantDetails = new Query(ctx,I_TC_PlantDetails.Table_Name, "planttaguuid=?", trxName)
					.setParameters(plantUUId).first();
			X_TC_PlantDetails plantDetail = new X_TC_PlantDetails(ctx, plantDetails.get_ID(), trxName);
			X_TC_Farmer farmer = new X_TC_Farmer(ctx, plantDetail.getTC_Farmer_ID(), trxName);
			
			res.setTaluk(farmer.gettalukname() != null ? farmer.gettalukname() : "");
	        res.setPanchayath(farmer.getvillagename2() != null ? farmer.getvillagename2() : "");
	        res.setDistrict(farmer.getdistrict() != null ? farmer.getdistrict() : "");
			
			if (plantDetails != null) {
			    PO collectionJointPlant = new Query(ctx, X_TC_collectionjoinplant.Table_Name, 
			            "tc_plantdetails_id=?", trxName)
			            .setParameters(plantDetails.get_ID())
			            .first();

			    if (collectionJointPlant != null) {
			        X_TC_collectionjoinplant collectionJoin = new X_TC_collectionjoinplant(ctx, collectionJointPlant.get_ID(), trxName);
			        String dateStr = "";
			        if (collectionJoin.getUpdated() != null) {
			            dateStr = dateFormat.format(collectionJoin.getUpdated());
			        }
			        res.setDateOfSourcing(dateStr);
			    }
			}
			
			if (shId > 0) {
			    X_TC_SecondaryHardeningLabel secondary = new X_TC_SecondaryHardeningLabel(ctx, shId, trxName);
			    if (secondary.getUpdated() != null) {
	                dateHardening = dateFormat.format(secondary.getUpdated());
	                parentCultureLine = secondary.getparentcultureline();
	            }
			} else {
			    if (phId > 0) {
			        X_TC_PrimaryHardeningLabel primary = new X_TC_PrimaryHardeningLabel(ctx, phId, trxName);
			        if (primary.getUpdated() != null) {
		                dateHardening = dateFormat.format(primary.getUpdated());
		                parentCultureLine = primary.getparentcultureline();
		            }
			    }
			}
			res.setDateOfSeconderyHardening(dateHardening);
			res.setParentCultureLine(parentCultureLine != null ? parentCultureLine : "");
			res.setIsError(false);
			
			trx.commit();
		}catch (Exception e) {		
			if(trx != null)
				trx.rollback();
			log.saveError("Error in getTraceabilityDataForQR: ", e);
			res.setIsError(true);
			res.setError(INTERNAL_SERVER_ERROR);
		}finally {
			if(trx !=null)
				trx.close();
			getCompiereService().disconnect();
		}
		return response;
	}	
	
	int clientId = 0;
	int orgId = 0;
	int roleId = 0;
	int warehouseId = 0;
	int userId = 0;
	String userName = null;
	
	private void initializeProps(Properties ctx) {

		 clientId = Env.getAD_Client_ID(ctx);
		 orgId = Env.getAD_Org_ID(ctx);
		 roleId = Env.getAD_Role_ID(ctx);
		 warehouseId = Env.getContextAsInt(ctx,  Env.M_WAREHOUSE_ID);
		 userId = Env.getAD_User_ID(ctx);
		 userName = Env.getContext(ctx,  Env.AD_USER_NAME);
	}
			
	private void setCtxProp(Properties ctx) {
		Env.setContext(ctx, Env.AD_USER_ID, userId);
		Env.setContext(ctx, Env.AD_CLIENT_ID, clientId);
		Env.setContext(ctx, Env.AD_ORG_ID, orgId);
		Env.setContext(ctx, Env.AD_ROLE_ID, roleId);
		Env.setContext(ctx, Env.M_WAREHOUSE_ID, warehouseId);
		Env.setContext(ctx, Env.AD_USER_NAME, userName);
	}

	@Override
	public StandardResponseDocument setDocAction(ModelSetDocActionRequestDocument req) {
		StandardResponseDocument res = StandardResponseDocument.Factory.newInstance();
		StandardResponse response = res.addNewStandardResponse();
		ModelSetDocActionRequest request = req.getModelSetDocActionRequest();
		ModelSetDocAction modelSetDocAction = request.getModelSetDocAction();
		Trx trx = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();
			String tableName = modelSetDocAction.getTableName();

			int recordID = modelSetDocAction.getRecordID();

			String docAction = modelSetDocAction.getDocAction();
			response.setRecordID(recordID);
			
			MTable table = MTable.get(ctx, tableName);
			if (table == null) {

				response.setIsError(true);
				response.setError("No Table Found");
				return res;

			}

			PO po = table.getPO(recordID, trxName);
			if (po == null) {
				response.setIsError(true);
				response.setError("No Record Found");
				return res;
			}

			// set explicitly the column DocAction to avoid automatic process of
			// default option
			po.set_ValueOfColumn("DocAction", docAction);
			if (!po.save()) {
				response.setIsError(true);
				response.setError("Cannot save before set docAction");
				return res;
			}
			
			try {
				if (!((org.compiere.process.DocAction) po).processIt(docAction)) {

					response.setIsError(true);
					response.setError("Cannot process docAction");
					return res;

				}
			} catch (Exception e) {
				response.setIsError(true);
				response.setError("Cannot process docAction");
				return res;
			}

			// close the trx
			if (!po.save()) {

				response.setIsError(true);
				response.setError("Cannot save before set docAction");
				return res;

			}

			if (!trx.commit()) {
				response.setIsError(true);
				response.setError("Cannot commit after docAction");
				return res;
			}
			
			response.setIsError(false);
			
			
		}catch(Exception e) {
			
		}finally {
			if (trx != null)
				trx.close();

			getCompiereService().disconnect();
		}
		
		return res;
	}
	
	public StandardResponseDocument uploadfile(List<File> files, int tableId, int recordId) {
		StandardResponseDocument response = StandardResponseDocument.Factory.newInstance();
		StandardResponse resp = response.addNewStandardResponse();
		Trx trx = null;
		try {
			initializeProps(Env.getCtx());
			getCompiereService().connect();
			CompiereService m_cs = getCompiereService();
			Properties ctx = m_cs.getCtx();
			setCtxProp(ctx);
			String trxName = Trx.createTrxName(getClass().getName() + "_");
			trx = Trx.get(trxName, true);
			trx.start();

			MAttachment attachment = new MAttachment(ctx, tableId, recordId, trxName);
			attachment.setClientOrg(Env.getAD_Client_ID(ctx), Env.getAD_Org_ID(ctx));
			attachment.setTextMsg("Visit Photos Uploaded");

			List<BufferedImage> images = new ArrayList<>();
			int i = 1;
			for (File file : files) {

				byte[] data = convertFileToByteArray(file);

				attachment.addEntry("image" + i + ".png", data);
				BufferedImage img = ImageIO.read(new ByteArrayInputStream(data));

				if (img != null) {
					images.add(img);
				}
				i++;
			}

			attachment.saveEx();
			trx.commit();
			resp.setIsError(false);

		} catch (Exception e) {
			if (trx != null)
				trx.rollback();
			resp.setError(e.getMessage());
			resp.setIsError(true);
			return response;
		} finally {
			if (manageTrx && trx != null)
				trx.close();
			getCompiereService().disconnect();
		}
		return response;
	}
	
	public static byte[] convertFileToByteArray(File file) {
		byte[] fileBytes = null;
		try (FileInputStream fis = new FileInputStream(file)) {
			long fileLength = file.length();
			fileBytes = new byte[(int) fileLength];

			fis.read(fileBytes);

		} catch (IOException e) {
			throw new AdempiereException(e.getMessage());
		}
		return fileBytes;
	}
	
//	@Override
//	public Response uploadImage(
//	        @FormDataParam("recordId") int recordId,
//	        @FormDataParam("tableId") int tableId,
//	        @FormDataParam("image") InputStream fileInputStream,
//	        @FormDataParam("image") FormDataContentDisposition fileDetail) 
//	{
//		Trx trx = null;
//	    try 
//	    {
//	    	 Properties ctx = Env.getCtx();
//           int clientId = Env.getAD_Client_ID(ctx);
//           int orgId = Env.getAD_Org_ID(ctx);
//
//           String trxName = Trx.createTrxName("UPLOAD_");
//           trx = Trx.get(trxName, true);
//           trx.start();
//	        // 1️⃣ Validate
//	        if (fileInputStream == null || fileDetail == null) {
//	            return Response.status(400).entity("Image file missing").build();
//	        }
//
//	        // 2️⃣ Read image bytes
//	        byte[] data = fileInputStream.readAllBytes();
//
//	        // 3️⃣ Get or create attachment
//	        MAttachment attachment = MAttachment.get(Env.getCtx(), tableId, recordId);
//
//	        if (attachment == null) {
//	            attachment = new MAttachment(Env.getCtx(), tableId, recordId, null);
//	        }
//
//	        // 4️⃣ Add file
//	        attachment.addEntry(fileDetail.getFileName(), data);
//	        attachment.setClientOrg(clientId, orgId);
//
//	        attachment.saveEx();
//
//	        // 7️⃣ SUCCESS
//	        return Response.ok("{\"status\":\"success\",\"message\":\"Image uploaded\"}").build();
//
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        return Response.serverError().entity("Error: " + e.getMessage()).build();
//	    }
//	}

	
}