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

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.*;
import org.compiere.util.Env;

/** Generated Model for pi_paorder
 *  @author iDempiere (generated) 
 *  @version Release 10 - $Id$ */
@org.adempiere.base.Model(table="pi_paorder")
public class X_pi_paorder extends PO implements I_pi_paorder, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20241118L;

    /** Standard Constructor */
    public X_pi_paorder (Properties ctx, int pi_paorder_ID, String trxName)
    {
      super (ctx, pi_paorder_ID, trxName);
      /** if (pi_paorder_ID == 0)
        {
			setcontractor (0);
			setpackingorder (false);
			setpi_paorder_ID (0);
			setProcessed (false);
        } */
    }

    /** Standard Constructor */
    public X_pi_paorder (Properties ctx, int pi_paorder_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, pi_paorder_ID, trxName, virtualColumns);
      /** if (pi_paorder_ID == 0)
        {
			setcontractor (0);
			setpackingorder (false);
			setpi_paorder_ID (0);
			setProcessed (false);
        } */
    }

    /** Load Constructor */
    public X_pi_paorder (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 7 - System - Client - Org 
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
      StringBuilder sb = new StringBuilder ("X_pi_paorder[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set contractor.
		@param contractor contractor
	*/
	public void setcontractorId (int contractor)
	{
		set_Value (COLUMNNAME_contractor, Integer.valueOf(contractor));
	}

	/** Get contractor.
		@return contractor	  */
	public int getcontractorId()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_contractor);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
	
	/** Set Description.
		@param Description Optional short description of the record
	*/
	public void setDescription (String Description)
	{
		set_Value (COLUMNNAME_Description, Description);
	}

	/** Get Description.
		@return Optional short description of the record
	  */
	public String getDescription()
	{
		return (String)get_Value(COLUMNNAME_Description);
	}

	public org.compiere.model.I_M_Product getM_Product() throws RuntimeException
	{
		return (org.compiere.model.I_M_Product)MTable.get(getCtx(), org.compiere.model.I_M_Product.Table_ID)
			.getPO(getM_Product_ID(), get_TrxName());
	}
	
	
	public org.compiere.model.I_AD_User getcontractor() throws RuntimeException
	{
		return (org.compiere.model.I_AD_User)MTable.get(getCtx(), org.compiere.model.I_AD_User.Table_ID)
			.getPO(getcontractorId(), get_TrxName());
	}

	/** Set Product.
		@param M_Product_ID Product, Service, Item
	*/
	public void setM_Product_ID (int M_Product_ID)
	{
		if (M_Product_ID < 1)
			set_Value (COLUMNNAME_M_Product_ID, null);
		else
			set_Value (COLUMNNAME_M_Product_ID, Integer.valueOf(M_Product_ID));
	}

	/** Get Product.
		@return Product, Service, Item
	  */
	public int getM_Product_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Product_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set packingorder.
		@param packingorder packingorder
	*/
	public void setpackingorder (boolean packingorder)
	{
		set_Value (COLUMNNAME_packingorder, Boolean.valueOf(packingorder));
	}

	/** Get packingorder.
		@return packingorder	  */
	public boolean ispackingorder()
	{
		Object oo = get_Value(COLUMNNAME_packingorder);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	public I_PI_Deptartment getPI_Deptartment() throws RuntimeException
	{
		return (I_PI_Deptartment)MTable.get(getCtx(), I_PI_Deptartment.Table_ID)
			.getPO(getPI_Deptartment_ID(), get_TrxName());
	}

	/** Set PI_Department.
		@param PI_Deptartment_ID PI_Department
	*/
	public void setPI_Deptartment_ID (int PI_Deptartment_ID)
	{
		if (PI_Deptartment_ID < 1)
			set_Value (COLUMNNAME_PI_Deptartment_ID, null);
		else
			set_Value (COLUMNNAME_PI_Deptartment_ID, Integer.valueOf(PI_Deptartment_ID));
	}

	/** Get PI_Department.
		@return PI_Department	  */
	public int getPI_Deptartment_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_PI_Deptartment_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set pi_paorder.
		@param pi_paorder_ID pi_paorder
	*/
	public void setpi_paorder_ID (int pi_paorder_ID)
	{
		if (pi_paorder_ID < 1)
			set_ValueNoCheck (COLUMNNAME_pi_paorder_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_pi_paorder_ID, Integer.valueOf(pi_paorder_ID));
	}

	/** Get pi_paorder.
		@return pi_paorder	  */
	public int getpi_paorder_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_pi_paorder_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Processed.
		@param Processed The document has been processed
	*/
	public void setProcessed (boolean Processed)
	{
		set_Value (COLUMNNAME_Processed, Boolean.valueOf(Processed));
	}

	/** Get Processed.
		@return The document has been processed
	  */
	public boolean isProcessed()
	{
		Object oo = get_Value(COLUMNNAME_Processed);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set quantity.
		@param quantity quantity
	*/
	public void setquantity (BigDecimal quantity)
	{
		set_Value (COLUMNNAME_quantity, quantity);
	}

	/** Get quantity.
		@return quantity	  */
	public BigDecimal getquantity()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_quantity);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Status.
		@param Status Status of the currently running check
	*/
	public void setStatus (String Status)
	{
		set_Value (COLUMNNAME_Status, Status);
	}

	/** Get Status.
		@return Status of the currently running check
	  */
	public String getStatus()
	{
		return (String)get_Value(COLUMNNAME_Status);
	}
	
	/**
	 * Set isrmpicked.
	 * 
	 * @param isrmpicked isrmpicked
	 */
	public void setisrmpicked(boolean isrmpicked) {
		set_Value(COLUMNNAME_isrmpicked, Boolean.valueOf(isrmpicked));
	}

	/**
	 * Get isrmpicked.
	 * 
	 * @return isrmpicked
	 */
	public boolean isrmpicked() {
		Object oo = get_Value(COLUMNNAME_isrmpicked);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}
	
	/**
	 * Set Status.
	 * 
	 * @param Status Status of the currently running check
	 */
	
	public void setIsAutomation(boolean isautomation) {
		set_Value(COLUMNNAME_isautomation, Boolean.valueOf(isautomation));
	}
	
	public boolean getIsAutomation() {
		Object oo = get_Value(COLUMNNAME_isautomation);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/**
	 * Get Status.
	 * 
	 * @return Status of the currently running check
	 */
//	public boolean getIsAutomation() {
//		return (boolean) get_Value(COLUMNNAME_isautomation);
//	}
}