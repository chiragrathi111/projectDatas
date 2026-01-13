package org.realmeds.tissue.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import org.compiere.acct.Doc;
import org.compiere.acct.Fact;
import org.compiere.model.MAcctSchema;

public class DocTCMediaOrder extends Doc{

	public DocTCMediaOrder(MAcctSchema as, Class<?> clazz, ResultSet rs, String defaultDocumentType, String trxName) {
		super(as, TCMediaOrder.class, rs, null, trxName);
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
		// TODO Auto-generated method stub
		ArrayList<Fact> facts = new ArrayList<Fact>();
		return facts;
	}
}
