package com.pipra.rwpl.factory;

import org.adempiere.webui.factory.IFormFactory;
import org.adempiere.webui.panel.ADForm;
import com.pipra.rwpl.model.SalesOrderForm;

public class RWPLFormFactory implements IFormFactory{

	@Override
	public ADForm newFormInstance(String formName) {
		
		if(formName.contains("SalesOrderForm"))
			return new SalesOrderForm();
		return null;
		
//		className != null &&  className.equalsIgnoreCase("com.pipra.rwpl.factory.SalesOrderProcess")
	}

}
