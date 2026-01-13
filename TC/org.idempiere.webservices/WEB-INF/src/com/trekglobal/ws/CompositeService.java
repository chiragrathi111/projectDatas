package com.trekglobal.ws;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/composite_service/")
@Consumes({"application/xml", "application/json"}) 
@Produces({"application/xml", "application/json"})
@WebService(targetNamespace="http://idempiere.org/ADInterface/1_0")
@SOAPBinding(style=Style.RPC,use=Use.LITERAL,parameterStyle=ParameterStyle.WRAPPED)
public interface CompositeService {

	/**
	 * 
	 * @param reqs
	 * @return CompositeResponsesDocument 
	 */

}
