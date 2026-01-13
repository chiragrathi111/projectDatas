package org.realmeds.tissue.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import org.compiere.acct.Doc;
import org.compiere.acct.Fact;
import org.compiere.model.MAcctSchema;

public class DocTCOrder extends Doc{

	public DocTCOrder(MAcctSchema as, Class<?> clazz, ResultSet rs, String defaultDocumentType, String trxName) {
		super(as, TCOrder.class, rs, null, trxName);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String loadDocumentDetails() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigDecimal getBalance() {
		// TODO Auto-generated method stub
		return BigDecimal.ZERO;
	}

	@Override
	public ArrayList<Fact> createFacts(MAcctSchema as) {
		ArrayList<Fact> facts = new ArrayList<Fact>();
		return facts;
	}

}
