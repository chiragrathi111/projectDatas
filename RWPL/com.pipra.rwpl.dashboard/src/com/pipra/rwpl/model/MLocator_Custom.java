package com.pipra.rwpl.model;

import java.math.BigDecimal;
import java.util.Properties;

import org.compiere.model.MLocator;

@org.adempiere.base.Model(table = MLocator.Table_Name)
public class MLocator_Custom extends MLocator {

	private static final long serialVersionUID = 1L;

	public MLocator_Custom(Properties ctx, int M_Locator_ID, String trxName) {
		super(ctx, M_Locator_ID, trxName);
	}

	public static final String COLUMNNAME_HEIGHT = "height";
	public static final String COLUMNNAME_DEPTH = "depth";
	public static final String COLUMNNAME_WIDTH = "width";
	public static final String COLUMNNAME_WEIGHT = "weight";
	public static final String COLUMNNAME_WEIGHT_CONSTRAINT = "weightConstraint";

	public void setHeight(BigDecimal height) {
		set_Value(COLUMNNAME_HEIGHT, height);
	}

	public BigDecimal getHeight() {
		return (BigDecimal) get_Value(COLUMNNAME_HEIGHT);
	}

	public void setDepth(BigDecimal depth) {
		set_Value(COLUMNNAME_DEPTH, depth);
	}

	public BigDecimal getDepth() {
		return (BigDecimal) get_Value(COLUMNNAME_DEPTH);
	}

	public void setWidth(BigDecimal width) {
		set_Value(COLUMNNAME_WIDTH, width);
	}

	public BigDecimal getWidth() {
		return (BigDecimal) get_Value(COLUMNNAME_WIDTH);
	}

	public void setWeight(BigDecimal weight) {
		set_Value(COLUMNNAME_WEIGHT, weight);
	}

	public BigDecimal getWeight() {
		return (BigDecimal) get_Value(COLUMNNAME_WEIGHT);
	}

	public void setWeightConstraint(boolean weightConstraint) {
		set_Value(COLUMNNAME_WEIGHT_CONSTRAINT, Boolean.valueOf(weightConstraint));
	}

	public boolean getWeightConstraint() {
		Object oo = get_Value(COLUMNNAME_WEIGHT_CONSTRAINT);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}
}
