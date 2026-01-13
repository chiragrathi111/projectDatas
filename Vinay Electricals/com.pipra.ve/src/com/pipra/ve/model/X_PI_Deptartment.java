/******************************************************************************
 * Product: iDempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2012 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY, without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program, if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
/** Generated Model - DO NOT CHANGE */
package com.pipra.ve.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.compiere.model.I_Persistent;
import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.model.POInfo;

/** Generated Model for PI_Deptartment
 *  @author iDempiere (generated) 
 *  @version Release 10 - $Id$ */
@org.adempiere.base.Model(table="PI_Deptartment")
public class X_PI_Deptartment extends PO implements I_PI_Deptartment, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20240822L;

    /** Standard Constructor */
    public X_PI_Deptartment (Properties ctx, int PI_Deptartment_ID, String trxName)
    {
      super (ctx, PI_Deptartment_ID, trxName);
      /** if (PI_Deptartment_ID == 0)
        {
			setpackingmodule (false);
			setPI_Deptartment_ID (0);
        } */
    }

    /** Standard Constructor */
    public X_PI_Deptartment (Properties ctx, int PI_Deptartment_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, PI_Deptartment_ID, trxName, virtualColumns);
      /** if (PI_Deptartment_ID == 0)
        {
			setpackingmodule (false);
			setPI_Deptartment_ID (0);
        } */
    }

    /** Load Constructor */
    public X_PI_Deptartment (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 3 - Client - Org 
      */
    protected int get_AccessLevel()
    {
      return accessLevel.intValue();
    }

    /** Load Meta Data */
    protected POInfo initPO (Properties ctx)
    {
      POInfo poi = POInfo.getPOInfo (ctx, Table_ID, get_TrxName());
      return poi;
    }

    public String toString()
    {
      StringBuilder sb = new StringBuilder ("X_PI_Deptartment[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set deptname.
		@param deptname deptname
	*/
	public void setdeptname (String deptname)
	{
		set_Value (COLUMNNAME_deptname, deptname);
	}

	/** Get deptname.
		@return deptname	  */
	public String getdeptname()
	{
		return (String)get_Value(COLUMNNAME_deptname);
	}

	/** Set packingmodule.
		@param packingmodule packingmodule
	*/
	public void setpackingmodule (boolean packingmodule)
	{
		set_Value (COLUMNNAME_packingmodule, Boolean.valueOf(packingmodule));
	}

	/** Get packingmodule.
		@return packingmodule	  */
	public boolean ispackingmodule()
	{
		Object oo = get_Value(COLUMNNAME_packingmodule);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}
	
	@Override
	public void setmanufacturingModule(boolean manufacturingModule) {
		set_Value (COLUMNNAME_manufacturingModule, Boolean.valueOf(manufacturingModule));
	}

	@Override
	public boolean ismanufacturingModule() {
		Object oo = get_Value(COLUMNNAME_manufacturingModule);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set PI_Deptartment.
		@param PI_Deptartment_ID PI_Deptartment
	*/
	public void setPI_Deptartment_ID (int PI_Deptartment_ID)
	{
		if (PI_Deptartment_ID < 1)
			set_ValueNoCheck (COLUMNNAME_PI_Deptartment_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_PI_Deptartment_ID, Integer.valueOf(PI_Deptartment_ID));
	}

	/** Get PI_Deptartment.
		@return PI_Deptartment	  */
	public int getPI_Deptartment_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_PI_Deptartment_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	@Override
	public void setdispatch(boolean dispatch) {
		set_Value (COLUMNNAME_dispatch, Boolean.valueOf(dispatch));
	}

	@Override
	public boolean isdispatch() {
		Object oo = get_Value(COLUMNNAME_dispatch);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}
	
//	@Override
//	public void setSubDepartment(boolean subDepartment) {
//		set_Value (COLUMNNAME_subDepartment, Boolean.valueOf(subDepartment));
//	}
//
//	@Override
//	public boolean isSubDepartment() {
//		Object oo = get_Value(COLUMNNAME_subDepartment);
//		if (oo != null) 
//		{
//			 if (oo instanceof Boolean) 
//				 return ((Boolean)oo).booleanValue(); 
//			return "Y".equals(oo);
//		}
//		return false;
//	}
	
	@Override
	public X_PI_Deptartment getreceiptTransferDepartment() throws RuntimeException {
		PO po =MTable.get(getCtx(), X_PI_Deptartment.Table_ID)
		.getPO(getreceiptTransferDepartmentId(), get_TrxName());
		return new X_PI_Deptartment(p_ctx, po.get_ID(), null);
	}

	@Override
	public int getreceiptTransferDepartmentId() {
		Integer ii = (Integer) get_Value(COLUMNNAME_receiptTransferDepartment);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	@Override
	public void setreceiptTransferDepartment(int receiptTransferDepartment) {
		set_Value(COLUMNNAME_receiptTransferDepartment, Integer.valueOf(receiptTransferDepartment));

	}
	
	public void setpcknginrecivn (boolean pcknginrecivn)
	{
		set_Value (COLUMNNAME_pcknginrecivn, Boolean.valueOf(pcknginrecivn));
	}

	/** Get pcknginrecivn.
		@return pcknginrecivn	  */
	public boolean ispcknginrecivn()
	{
		Object oo = get_Value(COLUMNNAME_pcknginrecivn);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}
	
	public void setIsMcbReceiving (boolean ismcbreceiving)
	{
		set_Value (COLUMNNAME_MCBReceiving, Boolean.valueOf(ismcbreceiving));
	}

	/** Get pcknginrecivn.
		@return pcknginrecivn	  */
	public boolean isMcbReceiving()
	{
		Object oo = get_Value(COLUMNNAME_MCBReceiving);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}
}