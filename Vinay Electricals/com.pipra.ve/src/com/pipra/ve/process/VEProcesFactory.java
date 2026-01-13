package com.pipra.ve.process;

import org.adempiere.base.IProcessFactory;
import org.compiere.process.ProcessCall;

public class VEProcesFactory implements IProcessFactory {

	@Override
	public ProcessCall newProcessInstance(String className) {

		if (className.equalsIgnoreCase("com.pipra.ve.process.PaymentProcess")) {
//			return (ProcessCall) new PaymentProcess();
		}else if (className.equalsIgnoreCase("com.pipra.ve.process.WebServiceAcessProcess")) {
			return (ProcessCall) new WebServiceAcessProcess();
		}

		return null;
	}

}