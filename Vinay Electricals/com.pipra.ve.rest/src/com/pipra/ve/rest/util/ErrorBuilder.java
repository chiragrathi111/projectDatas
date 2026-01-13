package com.pipra.ve.rest.util;

import javax.ws.rs.core.Response.Status;

import org.compiere.util.Util;

import com.google.gson.JsonObject;

/**
 * Builder for error response json object
 * 
 * @author Mahendhar Reddy
 *
 */
public class ErrorBuilder {

	private Status status = null;
	private String title = null;
	private StringBuilder detail = new StringBuilder();
	private String type = null;

	public ErrorBuilder() {
	}

	/**
	 * 
	 * @param status http status code
	 * @return ErrorBuilder
	 */
	public ErrorBuilder status(Status status) {
		this.status = status;
		return this;
	}

	/**
	 * 
	 * @param title error summary
	 * @return ErrorBuilder
	 */
	public ErrorBuilder title(String title) {
		this.title = title;
		return this;
	}

	/**
	 * error type/code
	 * 
	 * @param type
	 * @return ErrorBuilder
	 */
	public ErrorBuilder type(String type) {
		this.type = type;
		return this;
	}

	/**
	 * 
	 * @param detail extra details
	 * @return ErrorBuilder
	 */
	public ErrorBuilder append(String detail) {
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

	public ErrorBuilder append(int i) {
		detail.append(i);
		return this;
	}
}
