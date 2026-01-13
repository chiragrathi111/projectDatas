package org.pipra.callout.custom;

import org.adempiere.base.AnnotationBasedModelFactory;
import org.adempiere.base.IModelFactory;
import org.compiere.model.MUser;
import org.osgi.service.component.annotations.Component;
import org.pipra.webservices.custom.COrder_Custom;
import org.pipra.webservices.custom.MInOut_Custom;

@Component(immediate = true, service = IModelFactory.class, property = "service.ranking:Integer=1")
public class PiModelFactory extends AnnotationBasedModelFactory {

	@Override
	protected String[] getPackages() {
		return new String[] { COrder_Custom.class.getPackageName(), MInOut_Custom.class.getPackageName(),
				MUser.class.getPackageName()};
	}

}
