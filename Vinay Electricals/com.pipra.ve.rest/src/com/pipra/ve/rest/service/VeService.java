package com.pipra.ve.rest.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.pipra.ve.rest.model.request.Order;


	@Path("rest")
	/**
	 * VeService
	 * 
	 * @author Mahendhar Reddy
	 *
	 */
	public interface VeService {

		@POST
		@Path("createPO")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public Response createPurchaseOrder(Order order);

		
		@Path("createSO")
		@POST
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public Response createSalesOrder(Order order);
}
