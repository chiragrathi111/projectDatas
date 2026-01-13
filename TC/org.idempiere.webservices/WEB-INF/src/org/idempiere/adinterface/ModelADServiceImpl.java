/**********************************************************************
* This file is part of Adempiere ERP Bazaar                           *
* http://www.adempiere.org                                            *
*                                                                     *
* Copyright (C) Carlos Ruiz - globalqss                               *
* Copyright (C) Contributors                                          *
*                                                                     *
* This program is free software; you can redistribute it and/or       *
* modify it under the terms of the GNU General Public License         *
* as published by the Free Software Foundation; either version 2      *
* of the License, or (at your option) any later version.              *
*                                                                     *
* This program is distributed in the hope that it will be useful,     *
* but WITHOUT ANY WARRANTY; without even the implied warranty of      *
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the        *
* GNU General Public License for more details.                        *
*                                                                     *
* You should have received a copy of the GNU General Public License   *
* along with this program; if not, write to the Free Software         *
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,          *
* MA 02110-1301, USA.                                                 *
*                                                                     *
* Contributors:                                                       *
* - Carlos Ruiz  (globalqss@users.sourceforge.net)                    *
*                                                                     *
* Sponsors:                                                           *
* - GlobalQSS (http://www.globalqss.com)                              *
***********************************************************************/

package org.idempiere.adinterface;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceContext;
import org.apache.cxf.jaxrs.ext.MessageContext;
import org.compiere.model.MInOut;
import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.model.POInfo;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Msg;
import org.compiere.util.Trx;
import org.idempiere.adInterface.x10.ADLoginRequest;
import org.idempiere.adInterface.x10.ModelSetDocAction;
import org.idempiere.adInterface.x10.ModelSetDocActionRequestDocument;
import org.idempiere.adInterface.x10.StandardResponse;
import org.idempiere.adInterface.x10.StandardResponseDocument;
import org.idempiere.webservices.AbstractService;
import org.idempiere.webservices.IWSValidator;
import org.idempiere.webservices.fault.IdempiereServiceFault;
import org.idempiere.webservices.model.MWebServiceType;
import org.idempiere.webservices.model.X_WS_WebService_Para;
import org.pipra.model.custom.PipraUtils;

/*
 * ADEMPIERE/COMPIERE
 *
 * replacement:
 * GridField by GridFieldVO
 * GridTabVO by GridTabVO
 * GridWindowVO by GridWindowVO	
 *
 * Contributors: Carlos Ruiz - globalqss
 *     Add model oriented method modelSetDocAction
 *     Some Polish messages translated to english using google translate
 *     Deepak Pansheriya 
 *     Abstracting out Authenticate and login method
 *     Re factored to add support for composite web service
 *     Added CreateUpdate end point
 *     Added Support for Ctx Variable and ability to pass ctx variable in request
 *     Added configurable output fields
 */


/**
 *
 * @author kolec
 *
 */
@WebService(endpointInterface="org.idempiere.adinterface.ModelADService", serviceName="ModelADService",targetNamespace="http://idempiere.org/ADInterface/1_0")
public class ModelADServiceImpl extends AbstractService implements ModelADService {

	private static final CLogger	log = CLogger.getCLogger(ModelADServiceImpl.class);
	
	private static String webServiceName = new String("ModelADService");
	
	private boolean manageTrx = true;

	private String localTrxName = null;
	
	public boolean isManageTrx() {
		return manageTrx;
	}

	public void setManageTrx(boolean manageTrx) {
		this.manageTrx = manageTrx;
	}

	public String getLocalTrxName() {
		return localTrxName;
	}

	public void setLocalTrxName(String locatTrxName) {
		this.localTrxName = locatTrxName;
	}

	public ModelADServiceImpl()
	{
		
		log.info("Creating session object ADService");
	}
	
	public ModelADServiceImpl(WebServiceContext soapContext,  MessageContext jaxrsContext)
	{
		this.jaxwsContext = soapContext;
		this.jaxrsContext = jaxrsContext;
		
		log.info("Creating session object ADService");
	}
	public String getVersion() {
		return "0.8.0";
	}

	private String validateParameter(String parameterName, String string) {
		
		MWebServiceType m_webservicetype= getWebServiceType();
		
		X_WS_WebService_Para para = m_webservicetype.getParameter(parameterName);
		if (para == null && (string == null || string.length() == 0))
			// if parameter not configured but didn't receive value (optional param)
			return null;
		
		if (para == null)
			throw new IdempiereServiceFault("Web service type "
					+ m_webservicetype.getValue() + ": invalid parameter "
					+ parameterName,
					new QName("validateParameter"));

		if (X_WS_WebService_Para.PARAMETERTYPE_Constant.equals(para.getParameterType())) {
			if (string == null || string.length() == 0) {
				if (log.isLoggable(Level.INFO))log.log(Level.INFO, "Web service type "
						+ m_webservicetype.getValue() + ": constant parameter "
						+ parameterName + " set to "
						+ para.getConstantValue());
				return para.getConstantValue();
			} else if (! para.getConstantValue().equals(string)) {
				log.log(Level.WARNING, "Web service type "
						+ m_webservicetype.getValue() + ": constant parameter "
						+ parameterName + " changed to "
						+ para.getConstantValue());
				return para.getConstantValue();
			}
		}
		return string;
	}

	private int validateParameter(String parameterName, int i) {
		return validateParameter(parameterName, i, null);
	}
	
	private int validateParameter(String parameterName, int i, String uuid) {
		Integer io = Integer.valueOf(i);
		String string = validateParameter(parameterName, io.toString());
		// Use the UUID only if the returned string is empty to not override the constant value if any
		if (string == null || string.equals("0"))
			string = uuid;
		
		if (string == null)
			return -1;
		if (string.equals(io.toString()))
			return i;
		if (parameterName.endsWith("_ID") && ADLookup.isUUID(string)) {
			String tableName = parameterName.substring(0, parameterName.length()-3);
			StringBuilder sql = new StringBuilder("SELECT ");
			sql.append(parameterName).append(" FROM ").append(tableName)
				.append(" WHERE ").append(tableName).append("_UU=?");
			return DB.getSQLValue(null, sql.toString(), string);
		}
		
		Map<String, Object> requestCtx = getRequestCtx();
		if (requestCtx != null && string.charAt(0) == '@') {
			Object value = parseVariable(getCompiereService(), requestCtx, parameterName, string);
			if (value != null && value instanceof Number) {
				return ((Number)value).intValue();
			} else if (value != null ){
				string = value.toString();
			} else {
				return -1;
			}
		}
		return Integer.parseInt(string);
	}

	public static Object parseVariable(CompiereService cs, Map<String, Object> requestCtx, String name, String strValue) {		
		String varName = strValue.substring(1);
		if (varName.charAt(0) == '#') {
			return cs.getCtx().getProperty(varName);
		} else {
			int indDot = varName.indexOf(".");
			if (indDot == -1) {
				return requestCtx.get(varName);
			} else {
				String tblName = varName.substring(0, indDot);
				String colName = varName.substring(indDot + 1);
				if (colName.indexOf(".") >= 0) {
					throw new IdempiereServiceFault(strValue + " contains un supported multi level object resolution",
							new QName("resolveCtxVariable"));
				}
				Object obj = requestCtx.get(tblName);
				if (obj == null || !(obj instanceof PO)) {
					throw new IdempiereServiceFault(" input column " + name + " can not found object of " + tblName
							+ ". Request variable " + strValue + " can not resolved", new QName("resolveCtxVariable"));
				}

				PO refPO = (PO) obj;
				return refPO.get_Value(colName);

			}
		}
	}
}