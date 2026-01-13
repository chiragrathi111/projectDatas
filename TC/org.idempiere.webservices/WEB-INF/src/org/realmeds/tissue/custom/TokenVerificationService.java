package org.realmeds.tissue.custom;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.compiere.util.DB;

public class TokenVerificationService {
	private static final String TABLE_USER = "AD_User";

    public boolean verifyToken(int userId, String token, String url) {
        String query = "SELECT token, tokentime FROM "+ TABLE_USER +" WHERE AD_User_ID=?";
        List<List<Object>> result = DB.getSQLArrayObjectsEx(null, query, new Object[]{userId});

        if (result.isEmpty()) {
            return false; // User not found
        }
        List<Object> userData = result.get(0);
        String storedToken = (String) userData.get(0);
        Object expirationObj = userData.get(1);
        long expirationTime = -1;
        if (expirationObj instanceof Timestamp) {
            expirationTime = ((Timestamp) expirationObj).getTime();
        } else if (expirationObj instanceof String) {
            try {
            	if(url.equalsIgnoreCase("https://tissueculture.kdisc.kerala.gov.in/tcapi")) {
            		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSXXX");
            		OffsetDateTime dateTime = OffsetDateTime.parse((String) expirationObj, dateFormat);
                    expirationTime = dateTime.toInstant().toEpochMilli();
            	}else if(url.equalsIgnoreCase("https://tc.warepro.in/tcapi")) {
            		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSX");
            		OffsetDateTime dateTime = OffsetDateTime.parse((String) expirationObj, dateFormat);
                    expirationTime = dateTime.toInstant().toEpochMilli();
            	}else if(url.equalsIgnoreCase("http://localhost:8080/tcapi")) {
            		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSXXX");
            		OffsetDateTime dateTime = OffsetDateTime.parse((String) expirationObj, dateFormat);
                    expirationTime = dateTime.toInstant().toEpochMilli();    
            	}else {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
        long currentTime = System.currentTimeMillis();
        if (!storedToken.equals(token) || currentTime > expirationTime) {
            return false; // Token is invalid or expired
        }
        return true; // Token is valid
    }
}
