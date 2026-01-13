package com.pipra.ve.rest.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MBPartner;
import org.compiere.model.MBPartnerLocation;
import org.compiere.model.MDocType;
import org.compiere.model.MLocation;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MPriceListVersion;
import org.compiere.model.MProduct;
import org.compiere.model.MProductCategory;
import org.compiere.model.MProductPrice;
import org.compiere.model.MTaxCategory;
import org.compiere.model.MUOM;
import org.compiere.model.MUser;
import org.compiere.model.MWarehouse;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Trx;
import org.eevolution.model.X_PP_MRP;

import com.google.gson.JsonObject;
import com.pipra.ve.model.MProduct_Custom;
import com.pipra.ve.model.MUser_Custom;
import com.pipra.ve.model.X_PI_Deptartment;
import com.pipra.ve.rest.model.COrder_Custom;
import com.pipra.ve.rest.model.request.Order;
import com.pipra.ve.rest.model.request.Order.DTItem;
import com.pipra.ve.rest.util.ErrorBuilder;
import com.pipra.ve.utils.VeUtils;

/**
 * @author Mahendhar Reddy
 *
 */
public class VeServiceImpl implements VeService {

	@Override
	public Response createPurchaseOrder(Order request) {
		JsonObject response = new JsonObject();
		try {
			Properties ctx = Env.getCtx();
			String trxName = null;
			String bPartnerName = request.getAcHeadName();

			int adClientId = Env.getAD_Client_ID(ctx);
			int adOrgId = Env.getAD_Org_ID(ctx);
			int userId = Env.getAD_User_ID(ctx);

			MUser mUser = new MUser(ctx, userId, trxName);

			PO po = new Query(ctx, MWarehouse.Table_Name, "ad_client_ID = ? AND ad_org_ID = ?", trxName)
					.setParameters(adClientId, adOrgId).list().get(0);

			MWarehouse warehouse = new MWarehouse(ctx, po.get_ID(), trxName);

			MDocType mDocTypee = (MDocType) new Query(ctx, MDocType.Table_Name,
					"name = 'Purchase Order' and ad_client_id = ?", trxName).setParameters(adClientId).firstOnly();
			MBPartner mBPartner = (MBPartner) new Query(ctx, MBPartner.Table_Name,
					"name = ? and ad_client_id = ? and isVendor = 'Y'", trxName).setParameters(bPartnerName, adClientId)
					.firstOnly();

			if (mBPartner == null) {

				boolean sameAddress = true;
				MLocation billAddress = null;

				billAddress = new MLocation(ctx, 0, trxName);

				String address1 = request.getBillTo();
				String address2 = null;
				String address3 = null;
				String cutString = null;
				String cutString2 = null;
				if (request.getBillTo().length() > 60) {
					address1 = request.getBillTo().substring(0, 60);
					cutString = request.getBillTo().substring(60, request.getBillTo().length());
				}
				if (cutString != null) {

					if (cutString.length() > 60) {
						cutString2 = cutString.substring(60, cutString.length());
						address2 = cutString.substring(0, 60);
					} else
						address2 = cutString.substring(0, cutString.length());
				}
				if (cutString2 != null)
					address3 = cutString2.substring(0, 60);

				billAddress.setAddress1(address1);
				billAddress.setAddress2(address2);
				billAddress.setAddress3(address3);
				billAddress.setC_Country_ID(208);
				billAddress.saveEx(trxName);

				MLocation shipAddress = null;
				if (!request.getBillTo().equals(request.getShipTo()) && request.getShipTo() != ""
						&& request.getShipTo() != null) {
					shipAddress = new MLocation(ctx, 0, trxName);

					String a1 = request.getShipTo();
					String a2 = null;
					String a3 = null;
					String c1 = null;
					String c2 = null;
					if (request.getShipTo().length() > 60) {
						a1 = request.getShipTo().substring(0, 60);
						c1 = request.getShipTo().substring(60, request.getShipTo().length());
					}
					if (c1 != null) {
						if (c1.length() > 60) {
							c2 = c1.substring(60, c1.length());
							a2 = c1.substring(0, 60);
						} else
							a2 = c1.substring(0, c1.length());
					}
					if (c2 != null)
						a3 = c2.substring(0, 60);

					shipAddress.setAddress1(a1);
					shipAddress.setAddress2(a2);
					shipAddress.setAddress3(a3);

					shipAddress.setC_Country_ID(208);
					shipAddress.saveEx(trxName);

					sameAddress = false;
				}

				mBPartner = new MBPartner(ctx, 0, trxName);
				mBPartner.setClientOrg(adClientId, adOrgId);
				mBPartner.setName(bPartnerName);
				mBPartner.setIsVendor(true);
				mBPartner.saveEx(trxName);

				MBPartnerLocation shipBpLocation = new MBPartnerLocation(mBPartner);
				shipBpLocation.setC_Location_ID(billAddress.getC_Location_ID());

				if (!sameAddress) {
					MBPartnerLocation billBpLocation = new MBPartnerLocation(mBPartner);
					billBpLocation.setC_Location_ID(shipAddress.getC_Location_ID());
					billBpLocation.setIsShipTo(true);
					billBpLocation.setIsBillTo(false);
					billBpLocation.saveEx(trxName);

					shipBpLocation.setIsShipTo(false);
				}

				shipBpLocation.saveEx(trxName);

			}

			COrder_Custom mOrder = createPOOrder(adClientId, adOrgId, warehouse.getM_Warehouse_ID(), mBPartner.get_ID(), ctx,
					mDocTypee.get_ID(), mUser.getAD_User_ID(), trxName, request, null,request.getDeptName());
			response.addProperty("orderId", mOrder.getC_Order_ID());
			response.addProperty("documenNumber", mOrder.getDocumentNo());
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST)
					.entity(new ErrorBuilder().status(Status.BAD_REQUEST).title(e.getMessage()).build().toString())
					.build();
		}
		return Response.ok(response.toString()).build();
	}

	private COrder_Custom createPOOrder(int ad_client_id, int org_id, int warehouseId, int mBPartnerId, Properties ctx,
			int docTypeId, int userId, String trxName, Order request, Trx a,String departName) throws Exception {

		MBPartner mBPartner = new MBPartner(ctx, mBPartnerId, trxName);

		int orderId = 0;
		int erpId = request.getId();
		if (erpId != 0) {
			String id = String.valueOf(erpId);
			PO po = new Query(ctx, MOrder.Table_Name, "ad_client_id = ? AND ad_org_id = ? AND POReference = ? AND issotrx = 'N'", trxName)
					.setParameters(ad_client_id, org_id, id).firstOnly();
			if (po != null && po.get_ID() != 0)
				orderId = po.get_ID();
		}

		COrder_Custom po = new COrder_Custom(ctx, orderId, trxName);
		if (orderId == 0) {
			po.setC_DocTypeTarget_ID(docTypeId);
			po.setClientOrg(ad_client_id, org_id);
			po.setDescription(request.getNarration());
			po.setPOReference(String.valueOf(request.getId()));

			po.setDateOrdered(request.getvDate());
			po.setDatePromised(request.getvDate());
			po.setC_BPartner_ID(mBPartner.getC_BPartner_ID());
			po.setBill_BPartner_ID(mBPartner.getC_BPartner_ID());
			po.setBill_User_ID(userId);
			po.setM_Warehouse_ID(warehouseId);
			po.setIsSOTrx(false);
			po.setSalesRep_ID(userId);
			po.setAD_User_ID(userId);
			po.setPaymentRule("B");
			
			PO po1 = new Query(ctx, X_PI_Deptartment.Table_Name,"ad_client_id=? AND value=?", trxName)
					.setParameters(ad_client_id,departName).firstOnly();
			if(po1 != null && po1.get_ID() != 0) {
				po.setpidepartmentID(po1.get_ID());
			}
			
			po.saveEx(trxName);
		}

		List<Integer> lineIdList = new ArrayList<Integer>();
		for (DTItem line : request.getDtItems()) {
			if (line.getQty() <= 0)
				continue;
			int cOrderLineId = 0;
			int lineId = line.getId();
			lineIdList.add(lineId);
			if (orderId != 0)
				for (MOrderLine mol : po.getLines()) {
					if (mol.getLine() == lineId) {
						cOrderLineId = mol.getC_OrderLine_ID();
						break;
					}
				}

			int productQTY = line.getQty();
			Double discount = line.getDiscAmt();
			String productName = line.getMaterialName();
//			if (productQTY == 0)
//				throw new AdempiereException("Please Enter Product Quantity > 0");

			MOrderLine mOrderLine = null;
			if (cOrderLineId == 0) {
				mOrderLine = new MOrderLine(po);
			} else
				mOrderLine = new MOrderLine(ctx, cOrderLineId, trxName);

			MProduct mProduct = (MProduct) new Query(ctx, MProduct.Table_Name, "name = ? and ad_client_id = ?", trxName)
					.setParameters(productName, ad_client_id).firstOnly();
			if (mProduct == null) {
				mProduct = new MProduct_Custom(ctx, 0, trxName);
				mProduct.setAD_Org_ID(org_id);
				mProduct.setValue(productName);
				mProduct.setName(productName);

				MProductCategory mProductCategory = VeUtils.getDefaultProductCategory(ctx, trxName, ad_client_id);
				if (mProductCategory.get_ID() == 0)
					throw new AdempiereException("No Default Product Category");
				mProduct.setM_Product_Category_ID(mProductCategory.get_ID());

				MTaxCategory mTaxCategory = VeUtils.getDefaultTaxCategory(ctx, trxName, ad_client_id);
				if (mTaxCategory.get_ID() == 0)
					throw new AdempiereException("No Default Tax Category");
				mProduct.setC_TaxCategory_ID(mTaxCategory.get_ID());

				mProduct.setC_UOM_ID(MUOM.getDefault_UOM_ID(ctx));

				mProduct.saveEx(trxName);

				MProductPrice mProductPrice = new MProductPrice(ctx, 0, trxName);
				mProductPrice.setM_Product_ID(mProduct.get_ID());
				mProductPrice.setPriceLimit(BigDecimal.valueOf(line.getRate()));
				mProductPrice.setPriceList(BigDecimal.valueOf(line.getRate()));
				mProductPrice.setPriceStd(BigDecimal.valueOf(line.getRate()));

				MPriceListVersion mPriceListVersion = VeUtils.getDefaultPriceListVersion(ctx, trxName, ad_client_id,
						"N");
				mProductPrice.setM_PriceList_Version_ID(mPriceListVersion.get_ID());

				mProductPrice.saveEx(trxName);
			}else {
				MPriceListVersion mPriceListVersion = VeUtils.getDefaultPriceListVersion(ctx, trxName, ad_client_id,
						"N");
				MProductPrice mProductPrice = MProductPrice.get(ctx, mPriceListVersion.get_ID(), mProduct.get_ID(), trxName);
				if(mProductPrice == null || mProductPrice.get_ID() ==0) {
					mProductPrice = new MProductPrice(ctx, 0, trxName);
					mProductPrice.setM_Product_ID(mProduct.get_ID());
					mProductPrice.setPriceLimit(BigDecimal.valueOf(line.getRate()));
					mProductPrice.setPriceList(BigDecimal.valueOf(line.getRate()));
					mProductPrice.setPriceStd(BigDecimal.valueOf(line.getRate()));
					mProductPrice.setM_PriceList_Version_ID(mPriceListVersion.get_ID());
					mProductPrice.saveEx(trxName);
				}
			}

			mOrderLine.setM_Product_ID(mProduct.getM_Product_ID());
			mOrderLine.setDescription(line.getNarration());
			mOrderLine.setQty(BigDecimal.valueOf(productQTY));
//				mOrderLine.setC_Tax_ID(tax.getC_Tax_ID());
			mOrderLine.setDiscount(BigDecimal.valueOf(discount));
			mOrderLine.setC_UOM_ID(mProduct.getC_UOM_ID());
			mOrderLine.setLine(line.getId());
			mOrderLine.saveEx(trxName);
		}

		for (MOrderLine orderline : po.getLines()) {
			if (!lineIdList.contains(orderline.getLine())) {
				int pp_mrp_id = DB.getSQLValue(null,
						"SELECT pp_mrp_id FROM pp_mrp WHERE c_orderline_id =" + orderline.get_ID() + "");
				if (pp_mrp_id != 0) {
					X_PP_MRP mrp = new X_PP_MRP(ctx, pp_mrp_id, trxName);
					mrp.delete(true);
				}
				orderline.delete(true);
			}

		}
//			po.saveEx();
//			po.setC_DocType_ID(docTypeId);
//			po.setDocStatus(MOrder.DOCACTION_Complete);
//			po.setDocAction(MOrder.DOCACTION_Complete);
//			po.setIsApproved(true);
//			po.setProcessed(true);
//			po.saveEx();

		return po;
	}

	@Override
	public Response createSalesOrder(Order request) {
		JsonObject response = new JsonObject();
		try {
			Properties ctx = Env.getCtx();
			String trxName = null;

			int adClientId = Env.getAD_Client_ID(ctx);
			int adOrgId = Env.getAD_Org_ID(ctx);
			int userId = Env.getAD_User_ID(ctx);
			MUser mUser = new MUser(ctx, userId, trxName);
			String bPartnerName = request.getAcHeadName();

			PO po = new Query(ctx, MWarehouse.Table_Name, "ad_client_ID = ? AND ad_org_ID = ?", trxName)
					.setParameters(adClientId, adOrgId).list().get(0);

			MWarehouse warehouse = new MWarehouse(ctx, po.get_ID(), trxName);

			MDocType mDocTypee = (MDocType) new Query(ctx, MDocType.Table_Name,
					"name = 'Standard Order' and ad_client_id = ?", trxName).setParameters(adClientId).firstOnly();
			MBPartner mBPartner = (MBPartner) new Query(ctx, MBPartner.Table_Name,
					"name = ? and ad_client_id = ? and isCustomer = 'Y'", trxName)
					.setParameters(bPartnerName, adClientId).firstOnly();

			if (mBPartner == null) {

				boolean sameAddress = true;
				MLocation billAddress = null;

				billAddress = new MLocation(ctx, 0, trxName);

				String address1 = request.getBillTo();
				String address2 = null;
				String address3 = null;
				String cutString = null;
				String cutString2 = null;
				if (request.getBillTo().length() > 60) {
					address1 = request.getBillTo().substring(0, 60);
					cutString = request.getBillTo().substring(60, request.getBillTo().length());
				}
				if (cutString != null) {

					if (cutString.length() > 60) {
						cutString2 = cutString.substring(60, cutString.length());
						address2 = cutString.substring(0, 60);
					} else
						address2 = cutString.substring(0, cutString.length());
				}
				if (cutString2 != null)
					address3 = cutString2.substring(0, 60);

				billAddress.setAddress1(address1);
				billAddress.setAddress2(address2);
				billAddress.setAddress3(address3);
				billAddress.setC_Country_ID(208);
				billAddress.saveEx(trxName);

				MLocation shipAddress = null;
				if (!request.getBillTo().equals(request.getShipTo()) && request.getShipTo() != ""
						&& request.getShipTo() != null) {
					shipAddress = new MLocation(ctx, 0, trxName);

					String a1 = request.getShipTo();
					String a2 = null;
					String a3 = null;
					String c1 = null;
					String c2 = null;
					if (request.getShipTo().length() > 60) {
						a1 = request.getShipTo().substring(0, 60);
						c1 = request.getShipTo().substring(60, request.getShipTo().length());
					}
					if (c1 != null) {
						if (c1.length() > 60) {
							c2 = c1.substring(60, c1.length());
							a2 = c1.substring(0, 60);
						} else
							a2 = c1.substring(0, c1.length());
					}
					if (c2 != null)
						a3 = c2.substring(0, 60);

					shipAddress.setAddress1(a1);
					shipAddress.setAddress2(a2);
					shipAddress.setAddress3(a3);

					shipAddress.setC_Country_ID(208);
					shipAddress.saveEx(trxName);

					sameAddress = false;
				}

				mBPartner = new MBPartner(ctx, 0, trxName);
				mBPartner.setClientOrg(adClientId, adOrgId);
				mBPartner.setName(bPartnerName);
				mBPartner.setIsCustomer(true);
				mBPartner.saveEx(trxName);

				MBPartnerLocation shipBpLocation = new MBPartnerLocation(mBPartner);
				shipBpLocation.setC_Location_ID(billAddress.getC_Location_ID());

				if (!sameAddress) {
					MBPartnerLocation billBpLocation = new MBPartnerLocation(mBPartner);
					billBpLocation.setC_Location_ID(shipAddress.getC_Location_ID());
					billBpLocation.setIsShipTo(true);
					billBpLocation.setIsBillTo(false);
					billBpLocation.saveEx(trxName);

					shipBpLocation.setIsShipTo(false);
				}
				shipBpLocation.saveEx(trxName);
			}
			int orderId = 0;
			int erpId = request.getId();
			if (erpId != 0) {
				String id = String.valueOf(erpId);
				PO po2 = new Query(ctx, MOrder.Table_Name, "ad_client_id = ? AND ad_org_id = ? AND POReference = ? AND issotrx = 'Y'",
						trxName).setParameters(adClientId, adOrgId, id).firstOnly();
				if (po2 != null && po2.get_ID() != 0)
					orderId = po2.get_ID();
			}

			MOrder mOrder = new MOrder(ctx, orderId, trxName);
			if (orderId == 0) {
				mOrder.setC_DocTypeTarget_ID(mDocTypee.get_ID());
				mOrder.setClientOrg(adClientId, adOrgId);
				mOrder.setDescription(request.getNarration());
				mOrder.setPOReference(String.valueOf(request.getId()));

				mOrder.setDateOrdered(request.getvDate());
				mOrder.setDatePromised(request.getvDate());
				mOrder.setC_BPartner_ID(mBPartner.getC_BPartner_ID());
				mOrder.setBill_BPartner_ID(mBPartner.getC_BPartner_ID());
				mOrder.setBill_User_ID(mUser.get_ID());
				mOrder.setM_Warehouse_ID(warehouse.get_ID());
				mOrder.setIsSOTrx(true);
				mOrder.setSalesRep_ID(mUser.get_ID());
				mOrder.setAD_User_ID(mUser.get_ID());
				mOrder.setPaymentRule("B");
				mOrder.saveEx(trxName);
			}

			List<Integer> lineIdList = new ArrayList<Integer>();
			for (DTItem line : request.getDtItems()) {
				int cOrderLineId = 0;
				if (line.getQty() <= 0)
					continue;
				int lineId = line.getId();
				lineIdList.add(lineId);
				if (orderId != 0)
					for (MOrderLine mol : mOrder.getLines()) {
						if (mol.getLine() == lineId) {
							cOrderLineId = mol.getC_OrderLine_ID();
							break;
						}
					}

				int productQTY = line.getQty();
				Double discount = line.getDiscAmt();
				String productName = line.getMaterialName();
//				if (productQTY == 0)
//					throw new AdempiereException("Please Enter Product Quantity > 0");

				MOrderLine mOrderLine = null;
				if (cOrderLineId == 0) {
					mOrderLine = new MOrderLine(mOrder);
				} else
					mOrderLine = new MOrderLine(ctx, cOrderLineId, trxName);

				MProduct mProduct = (MProduct) new Query(ctx, MProduct.Table_Name, "name = ? and ad_client_id = ?",
						trxName).setParameters(productName, adClientId).firstOnly();
				if (mProduct == null) {
					mProduct = new MProduct_Custom(ctx, 0, trxName);
					mProduct.setAD_Org_ID(adOrgId);
					mProduct.setValue(productName);
					mProduct.setName(productName);

					MProductCategory mProductCategory = VeUtils.getDefaultProductCategory(ctx, trxName, adClientId);
					if (mProductCategory.get_ID() == 0)
						throw new AdempiereException("No Default Product Category");
					mProduct.setM_Product_Category_ID(mProductCategory.get_ID());

					MTaxCategory mTaxCategory = VeUtils.getDefaultTaxCategory(ctx, trxName, adClientId);
					if (mTaxCategory.get_ID() == 0)
						throw new AdempiereException("No Default Tax Category");
					mProduct.setC_TaxCategory_ID(mTaxCategory.get_ID());

					mProduct.setC_UOM_ID(MUOM.getDefault_UOM_ID(ctx));

					mProduct.saveEx(trxName);

					MProductPrice mProductPrice = new MProductPrice(ctx, 0, trxName);
					mProductPrice.setM_Product_ID(mProduct.get_ID());
					mProductPrice.setPriceLimit(BigDecimal.valueOf(line.getRate()));
					mProductPrice.setPriceList(BigDecimal.valueOf(line.getRate()));
					mProductPrice.setPriceStd(BigDecimal.valueOf(line.getRate()));

					MPriceListVersion mPriceListVersion = getSalesPriceListVersion(ctx, trxName, adClientId);
					mProductPrice.setM_PriceList_Version_ID(mPriceListVersion.get_ID());

					mProductPrice.saveEx(trxName);

				}else {
					MPriceListVersion mPriceListVersion = getSalesPriceListVersion(ctx, trxName, adClientId);
					MProductPrice mProductPrice = MProductPrice.get(ctx, mPriceListVersion.get_ID(), mProduct.get_ID(), trxName);
					if(mProductPrice == null || mProductPrice.get_ID() ==0) {
						mProductPrice = new MProductPrice(ctx, 0, trxName);
						mProductPrice.setM_Product_ID(mProduct.get_ID());
						mProductPrice.setPriceLimit(BigDecimal.valueOf(line.getRate()));
						mProductPrice.setPriceList(BigDecimal.valueOf(line.getRate()));
						mProductPrice.setPriceStd(BigDecimal.valueOf(line.getRate()));
						mProductPrice.setM_PriceList_Version_ID(mPriceListVersion.get_ID());
						mProductPrice.saveEx(trxName);
					}
				}

				mOrderLine.setM_Product_ID(mProduct.getM_Product_ID());
				mOrderLine.setDescription(line.getNarration());
				mOrderLine.setQty(BigDecimal.valueOf(productQTY));
//					mOrderLine.setC_Tax_ID(tax.getC_Tax_ID());
				mOrderLine.setDiscount(BigDecimal.valueOf(discount));
				mOrderLine.setC_UOM_ID(mProduct.getC_UOM_ID());
				mOrderLine.setLine(line.getId());
				mOrderLine.saveEx(trxName);
			}

			for (MOrderLine orderline : mOrder.getLines()) {
				if (!lineIdList.contains(orderline.getLine())) {
					int pp_mrp_id = DB.getSQLValue(null,
							"SELECT pp_mrp_id FROM pp_mrp WHERE c_orderline_id =" + orderline.get_ID() + "");
					if (pp_mrp_id != 0) {
						X_PP_MRP mrp = new X_PP_MRP(ctx, pp_mrp_id, trxName);
						mrp.delete(true);
					}
					orderline.delete(true);
				}
			}

			mOrder.saveEx();
			response.addProperty("orderId", mOrder.getC_Order_ID());
			response.addProperty("documenNumber", mOrder.getDocumentNo());
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST)
					.entity(new ErrorBuilder().status(Status.BAD_REQUEST).title(e.getMessage()).build().toString())
					.build();
		}
		return Response.ok(response.toString()).build();

	}
	
	public static MPriceListVersion getSalesPriceListVersion(Properties ctx, String trxName, int adClientId) {
		MPriceListVersion mPriceListVersion = new MPriceListVersion(ctx, 0, trxName);

		String tblName = MPriceListVersion.Table_Name;
		List<MPriceListVersion> list = new Query(ctx, MPriceListVersion.Table_Name,
				"" + tblName + ".ad_client_id = ? AND mp.issopricelist = 'Y' ", trxName)
				.setOrderBy(MPriceListVersion.COLUMNNAME_M_PriceList_Version_ID).setParameters(adClientId)
				.addJoinClause("JOIN M_PriceList mp on mp.M_PriceList_ID = " + tblName + ".M_PriceList_ID ").list();
		if (list.size() != 0)
			mPriceListVersion = list.get(0);
		return mPriceListVersion;
	}

}
