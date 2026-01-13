package org.pipra.webservices.custom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.ResultSet;
import java.util.Properties;

import org.compiere.model.MUser;


@org.adempiere.base.Model(table = MUser_Custom.Table_Name)
public class MUser_Custom extends MUser {

	private static final long serialVersionUID = 1101071347949960064L;

	public MUser_Custom(Properties ctx, int M_User_ID, String trxName) {
		super(ctx, M_User_ID, trxName);
	}

	public MUser_Custom(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		return super.beforeSave(newRecord);
	}

	@Override
	protected boolean beforeDelete() {
		return super.beforeDelete();
	}

	@Override
	protected boolean afterSave(boolean newRecord, boolean success) {
		boolean active = isActive();
		if(!active)
			deleteUserByName(getName());
			
		return super.afterSave(newRecord, success);
	}

	@Override
	protected boolean afterDelete(boolean success) {
		return super.afterDelete(success);
	}
	
	 public static void deleteUserByName(String name) {
	        try {
	            String url = "https://dev.warepro.in/deleteuser/?name=" + name;

	            URL obj = new URL(url);
	            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	            con.setRequestMethod("DELETE");
	            con.setRequestProperty("Content-Type", "application/json");
	            con.setDoOutput(true);

	            int responseCode = con.getResponseCode();

	            if (responseCode == HttpURLConnection.HTTP_OK) {
	                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
	                String inputLine;
	                StringBuilder response = new StringBuilder();
	                while ((inputLine = in.readLine()) != null) {
	                    response.append(inputLine);
	                }
	                in.close();
	                System.out.println("Response: " + response.toString());
	            } else {
	                BufferedReader errorReader = new BufferedReader(new InputStreamReader(con.getErrorStream()));
	                StringBuilder errorResponse = new StringBuilder();
	                String errorLine;
	                while ((errorLine = errorReader.readLine()) != null) {
	                    errorResponse.append(errorLine);
	                }
	                errorReader.close();
	                System.err.println("Error Response: " + errorResponse.toString());
	            }

	            con.disconnect();

	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
}
