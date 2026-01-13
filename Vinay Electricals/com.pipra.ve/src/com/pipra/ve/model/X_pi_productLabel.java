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

import org.compiere.model.I_M_Locator;
import org.compiere.model.I_Persistent;
import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.model.POInfo;
import org.compiere.util.Env;

/** Generated Model for pi_productLabel
 *  @author iDempiere (generated) 
 *  @version Release 10 - $Id$ */
@org.adempiere.base.Model(table="pi_productLabel")
public class X_pi_productLabel extends PO implements I_pi_productLabel, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20240807L;

    /** Standard Constructor */
    public X_pi_productLabel (Properties ctx, int pi_productLabel_ID, String trxName)
    {
      super (ctx, pi_productLabel_ID, trxName);
      /** if (pi_productLabel_ID == 0)
        {
			setpi_productLabel_ID (0);
			setrepacked (false);
        } */
    }

    /** Standard Constructor */
    public X_pi_productLabel (Properties ctx, int pi_productLabel_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, pi_productLabel_ID, trxName, virtualColumns);
      /** if (pi_productLabel_ID == 0)
        {
			setpi_productLabel_ID (0);
			setrepacked (false);
        } */
    }

    /** Load Constructor */
    public X_pi_productLabel (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_pi_productLabel[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_C_OrderLine getC_OrderLine() throws RuntimeException
	{
		return (org.compiere.model.I_C_OrderLine)MTable.get(getCtx(), org.compiere.model.I_C_OrderLine.Table_ID)
			.getPO(getC_OrderLine_ID(), get_TrxName());
	}

	/** Set Sales Order Line.
		@param C_OrderLine_ID Sales Order Line
	*/
	public void setC_OrderLine_ID (int C_OrderLine_ID)
	{
		if (C_OrderLine_ID < 1)
			set_ValueNoCheck (COLUMNNAME_C_OrderLine_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_C_OrderLine_ID, Integer.valueOf(C_OrderLine_ID));
	}

	/** Get Sales Order Line.
		@return Sales Order Line
	  */
	public int getC_OrderLine_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_OrderLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Sales Transaction.
		@param IsSOTrx This is a Sales Transaction
	*/
	public void setIsSOTrx (boolean IsSOTrx)
	{
		set_ValueNoCheck (COLUMNNAME_IsSOTrx, Boolean.valueOf(IsSOTrx));
	}

	/** Get Sales Transaction.
		@return This is a Sales Transaction
	  */
	public boolean isSOTrx()
	{
		Object oo = get_Value(COLUMNNAME_IsSOTrx);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set LabelUUID.
		@param LabelUUID LabelUUID
	*/
	public void setLabelUUID (String LabelUUID)
	{
		set_Value (COLUMNNAME_LabelUUID, LabelUUID);
	}

	/** Get LabelUUID.
		@return LabelUUID	  */
	public String getLabelUUID()
	{
		return (String)get_Value(COLUMNNAME_LabelUUID);
	}

	public org.compiere.model.I_M_InOutLine getM_InOutLine() throws RuntimeException
	{
		return (org.compiere.model.I_M_InOutLine)MTable.get(getCtx(), org.compiere.model.I_M_InOutLine.Table_ID)
			.getPO(getM_InOutLine_ID(), get_TrxName());
	}

	/** Set Shipment/Receipt Line.
		@param M_InOutLine_ID Line on Shipment or Receipt document
	*/
	public void setM_InOutLine_ID (int M_InOutLine_ID)
	{
		if (M_InOutLine_ID < 1)
			set_ValueNoCheck (COLUMNNAME_M_InOutLine_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_M_InOutLine_ID, Integer.valueOf(M_InOutLine_ID));
	}

	/** Get Shipment/Receipt Line.
		@return Line on Shipment or Receipt document
	  */
	public int getM_InOutLine_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_InOutLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_M_Locator getM_Locator() throws RuntimeException
	{
		return (I_M_Locator)MTable.get(getCtx(), I_M_Locator.Table_ID)
			.getPO(getM_Locator_ID(), get_TrxName());
	}

	/** Set Locator.
		@param M_Locator_ID Warehouse Locator
	*/
	public void setM_Locator_ID (int M_Locator_ID)
	{
		if (M_Locator_ID < 1)
			set_ValueNoCheck (COLUMNNAME_M_Locator_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_M_Locator_ID, Integer.valueOf(M_Locator_ID));
	}

	/** Get Locator.
		@return Warehouse Locator
	  */
	public int getM_Locator_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Locator_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Product getM_Product() throws RuntimeException
	{
		return (org.compiere.model.I_M_Product)MTable.get(getCtx(), org.compiere.model.I_M_Product.Table_ID)
			.getPO(getM_Product_ID(), get_TrxName());
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

	public I_pi_productLabel getparentla() throws RuntimeException
	{
		return (I_pi_productLabel)MTable.get(getCtx(), I_pi_productLabel.Table_ID)
			.getPO(getparentlabel(), get_TrxName());
	}

	/** Set parentlabel.
		@param parentlabel parentlabel
	*/
	public void setparentlabel (int parentlabel)
	{
		set_Value (COLUMNNAME_parentlabel, Integer.valueOf(parentlabel));
	}

	/** Get parentlabel.
		@return parentlabel	  */
	public int getparentlabel()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_parentlabel);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_pi_labeltype getpi_labeltype() throws RuntimeException
	{
		return (I_pi_labeltype)MTable.get(getCtx(), I_pi_labeltype.Table_ID)
			.getPO(getpi_labeltype_ID(), get_TrxName());
	}

	/** Set pi_labeltype_ID.
		@param pi_labeltype_ID pi_labeltype_ID
	*/
	public void setpi_labeltype_ID (int pi_labeltype_ID)
	{
		if (pi_labeltype_ID < 1)
			set_Value (COLUMNNAME_pi_labeltype_ID, null);
		else
			set_Value (COLUMNNAME_pi_labeltype_ID, Integer.valueOf(pi_labeltype_ID));
	}

	/** Get pi_labeltype_ID.
		@return pi_labeltype_ID	  */
	public int getpi_labeltype_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_pi_labeltype_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set pi_productLabel.
		@param pi_productLabel_ID pi_productLabel
	*/
	public void setpi_productLabel_ID (int pi_productLabel_ID)
	{
		if (pi_productLabel_ID < 1)
			set_ValueNoCheck (COLUMNNAME_pi_productLabel_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_pi_productLabel_ID, Integer.valueOf(pi_productLabel_ID));
	}

	/** Get pi_productLabel.
		@return pi_productLabel	  */
	public int getpi_productLabel_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_pi_productLabel_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public void setQcpassed (boolean qcpassed)
	{
		set_ValueNoCheck (COLUMNNAME_QcPassed, Boolean.valueOf(qcpassed));
	}
	

	/** Get qcpassed.
		@return qcpassed	  */
	
	public boolean qcpassed()
	{
		Object oo = get_Value(COLUMNNAME_QcPassed);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Quantity.
		@param Quantity Quantity
	*/
	public void setQuantity (BigDecimal Quantity)
	{
		set_ValueNoCheck (COLUMNNAME_Quantity, Quantity);
	}

	/** Get Quantity.
		@return Quantity	  */
	public BigDecimal getQuantity()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Quantity);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set repacked.
		@param repacked repacked
	*/
	public void setrepacked (boolean repacked)
	{
		set_Value (COLUMNNAME_repacked, Boolean.valueOf(repacked));
	}

	/** Get repacked.
		@return repacked	  */
	public boolean isrepacked()
	{
		Object oo = get_Value(COLUMNNAME_repacked);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}
	
	/**
	 * Set ismanufacturing.
	 * 
	 * @param ismanufacturing ismanufacturing
	 */
	public void setismanufacturing(boolean ismanufacturing) {
		set_Value(COLUMNNAME_ismanufacturing, Boolean.valueOf(ismanufacturing));
	}

	/**
	 * Get ismanufacturing.
	 * 
	 * @return ismanufacturing
	 */
	public boolean ismanufacturing() {
		Object oo = get_Value(COLUMNNAME_ismanufacturing);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	public org.eevolution.model.I_PP_Order getPP_Order() throws RuntimeException {
		return (org.eevolution.model.I_PP_Order) MTable.get(getCtx(), org.eevolution.model.I_PP_Order.Table_ID)
				.getPO(getPP_Order_ID(), get_TrxName());
	}

	/**
	 * Set Manufacturing Order.
	 * 
	 * @param PP_Order_ID Manufacturing Order
	 */
	public void setPP_Order_ID(int PP_Order_ID) {
		if (PP_Order_ID < 1)
			set_ValueNoCheck(COLUMNNAME_PP_Order_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_PP_Order_ID, Integer.valueOf(PP_Order_ID));
	}

	/**
	 * Get Manufacturing Order.
	 * 
	 * @return Manufacturing Order
	 */
	public int getPP_Order_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_PP_Order_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}
	
	@Override
	public void setPi_paorder_ID(int pi_paorder_ID) {
		if (pi_paorder_ID < 1)
			set_ValueNoCheck(COLUMNNAME_pi_paorder_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_pi_paorder_ID, Integer.valueOf(pi_paorder_ID));
	}
	
	@Override
	public int getPi_paorder_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_pi_paorder_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}
	
	@Override
	public int getPi_orderreceipt_ID() {
		Integer ii = (Integer) get_Value(COLUMNNAME_pi_orderreceipt_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}
	
	@Override
	public void setpi_orderreceipt_ID(int pi_orderreceipt_ID) {
		if (pi_orderreceipt_ID < 1)
			set_ValueNoCheck(COLUMNNAME_pi_orderreceipt_ID, null);
		else
			set_ValueNoCheck(COLUMNNAME_pi_orderreceipt_ID, Integer.valueOf(pi_orderreceipt_ID));
	}
	
	@Override
	public X_pi_orderreceipt getpi_orderreceipt() throws RuntimeException {
		PO po = MTable.get(getCtx(), X_pi_orderreceipt.Table_ID).getPO(getPi_orderreceipt_ID(),
				get_TrxName());
		return  new X_pi_orderreceipt(p_ctx, po.get_ID(), null);
	}
	
	/**
	 * Set finaldispatch.
	 * 
	 * @param finaldispatch finaldispatch
	 */
	public void setfinaldispatch(boolean finaldispatch) {
		set_Value(COLUMNNAME_finaldispatch, Boolean.valueOf(finaldispatch));
	}

	/**
	 * Get finaldispatch.
	 * 
	 * @return finaldispatch
	 */
	public boolean isfinaldispatch() {
		Object oo = get_Value(COLUMNNAME_finaldispatch);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}
	
	/** Set reason.
	@param reason reason
*/
	public void setreason(String reason) {
		set_Value(COLUMNNAME_reason, reason);
	}

	/**
	 * Get reason.
	 * 
	 * @return reason
	 */
	public String getreason() {
		return (String) get_Value(COLUMNNAME_reason);
	}
	
//	/** External_Replacement = EI */
	public static final String STATUS_External_Replacement = "EI";
//	/** External_Return = ER */
	public static final String STATUS_External_Return = "ER";
	
	public static final String STATUS_Test = "Test";
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
}