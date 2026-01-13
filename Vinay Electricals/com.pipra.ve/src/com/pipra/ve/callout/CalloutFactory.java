package com.pipra.ve.callout;

import org.adempiere.base.ICalloutFactory;
import org.compiere.model.Callout;

public class CalloutFactory implements ICalloutFactory {

	@Override
	public Callout getCallout(String className, String methodName) {
		
//		if (className.equalsIgnoreCase("com.pipra.ve.callout.PaymentCallout")) {
//			return new PaymentCallout();
//		}

		return null;
	}

}