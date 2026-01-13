package com.pipra.rwpl.util;

import javax.ws.rs.core.Response.Status;

import org.compiere.util.Util;

import com.google.gson.JsonObject;

public class StandardResponse {

	private Status status = null;
	private String title = null;
	private StringBuilder detail = new StringBuilder();
	private String type = null;

	public StandardResponse() {
	}

	/**
	 * 
	 * @param status http status code
	 * @return StandardResponse
	 */
	public StandardResponse status(Status status) {
		this.status = status;
		return this;
	}

	/**
	 * 
	 * @param title error summary
	 * @return StandardResponse
	 */
	public StandardResponse title(String title) {
		this.title = title;
		return this;
	}

	/**
	 * error type/code
	 * 
	 * @param type
	 * @return StandardResponse
	 */
	public StandardResponse type(String type) {
		this.type = type;
		return this;
	}

	/**
	 * 
	 * @param detail extra details
	 * @return StandardResponse
	 */
	public StandardResponse append(String detail) {
		this.detail.append(detail);
		return this;
	}

	/**
	 * 
	 * @return error response json object
	 */
	public JsonObject build() {
		JsonObject jso = new JsonObject();
		if (!Util.isEmpty(type, true)) {
			jso.addProperty("type", type);
		}
		if (!Util.isEmpty("title", true)) {
			jso.addProperty("title", title);
		}
		if (status != null) {
			jso.addProperty("status", status.getStatusCode());
		}
		if (detail.length() > 0) {
			jso.addProperty("detail", detail.toString());
		}
		return jso;
	}

	public StandardResponse append(int i) {
		detail.append(i);
		return this;
	}
}
