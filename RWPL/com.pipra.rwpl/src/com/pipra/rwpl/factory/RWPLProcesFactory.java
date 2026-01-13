package com.pipra.rwpl.factory;

import org.adempiere.base.IProcessFactory;
import org.compiere.process.ProcessCall;

public class RWPLProcesFactory implements IProcessFactory {

	@Override
	public ProcessCall newProcessInstance(String className) {

//		if (className.equalsIgnoreCase("com.pipra.rwpl.process.AddInwardProcess")) {
//			return (ProcessCall) new AddInwardProcess();
//		}else 
			if (className != null &&  className.equalsIgnoreCase("com.pipra.rwpl.factory.SalesOrderProcess")) {
			return new SalesOrderProcess();
		}else if (className != null &&  className.equalsIgnoreCase("com.pipra.rwpl.factory.RWPLMailProcess")) {
			return new RWPLMailProcess();
		}
		else if (className != null &&  className.equalsIgnoreCase("com.pipra.rwpl.factory.ImportStorage")) {
			return new ImportStorage();
		}

		return null;
	}

}