package org.pipra.custom.fcm;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.adempiere.exceptions.AdempiereException;
import org.codehaus.jettison.json.JSONObject;
import org.compiere.model.MAttachment;
import org.compiere.model.MAttachmentEntry;
import org.compiere.model.MSysConfig;
import org.compiere.model.Query;
import org.compiere.util.Env;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class FCMService {

	public static String BASE_URL ;
	public static final String CONTENT_TYPE = "application/json";

	private static String token;
	private static Date expTokenDate;
	private static String projectId;

	public static boolean sendFCMMessage(String deviceToken, String title, String body, String serverKeyPath,
			Map<String, String> data) {
		try {

			if (token == null || expTokenDate.compareTo(new Date(System.currentTimeMillis())) < 0) {
				boolean error = generateOAuthToken();
				if(error)
					return true;
				BASE_URL = "https://fcm.googleapis.com/v1/projects/"+projectId+"/messages:send";
			}

			URL url = new URL(BASE_URL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", CONTENT_TYPE);
			conn.setRequestProperty("Authorization", "Bearer " + token);

			conn.setDoOutput(true);

			JSONObject payload = new JSONObject();
			JSONObject message = new JSONObject();
			JSONObject notification = new JSONObject();

			notification.put("title", title);
			notification.put("body", body);

			message.put("notification", notification);
			message.put("token", deviceToken);

			if (data != null && !data.isEmpty()) {
				JSONObject dataJson = new JSONObject(data);
				message.put("data", dataJson);
			}

			payload.put("message", message);

			String paloadString = payload.toString();

			OutputStream outputStream = conn.getOutputStream();
			outputStream.write(paloadString.getBytes(StandardCharsets.UTF_8));

			int responseCode = conn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
			} else {
				InputStream errorStream = conn.getErrorStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream));
				String line;
				while ((line = reader.readLine()) != null) {
					System.out.println(line);
				}
				reader.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static String generateJwtToken() {

		Query query = new Query(Env.getCtx(), MSysConfig.Table_Name, "Name= 'FCM_CONFIGURATION' AND AD_Client_ID = 0",
				null);
		MSysConfig sysConfig = query.setOrderBy("AD_Client_ID Desc").first();

		if (sysConfig == null)
			return null;
		MAttachment attachment = sysConfig.getAttachment();
		if (attachment == null)
			return null;
		MAttachmentEntry[] attachmentEntries = attachment.getEntries();
		MAttachmentEntry firstAttachmentEntry = attachmentEntries[0];
		File file = firstAttachmentEntry.getFile();

		String privateKey = null;
		String clientEmail = null;

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(file);
			privateKey = jsonNode.get("private_key").asText();
			clientEmail = jsonNode.get("client_email").asText();
			projectId = jsonNode.get("project_id").asText();

		} catch (IOException e) {
			e.printStackTrace();
		}

		StringBuilder pkcs8Lines = new StringBuilder();
		BufferedReader rdr = new BufferedReader(new StringReader(privateKey));
		String line;
		RSAPrivateKey key = null;
		try {
			while ((line = rdr.readLine()) != null) {
				pkcs8Lines.append(line);
			}

			String pkcs8Pem = pkcs8Lines.toString();
			pkcs8Pem = pkcs8Pem.replace("-----BEGIN PRIVATE KEY-----", "");
			pkcs8Pem = pkcs8Pem.replace("-----END PRIVATE KEY-----", "");
			pkcs8Pem = pkcs8Pem.replaceAll("\\s+", "");

			byte[] valueDecoded = Base64.getDecoder().decode(pkcs8Pem);
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(valueDecoded);
			KeyFactory rsaFact = KeyFactory.getInstance("RSA");
			key = (RSAPrivateKey) rsaFact.generatePrivate(keySpec);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Map<String, Object> claims = new LinkedHashMap<>();
		claims.put("scope", "https://www.googleapis.com/auth/firebase.messaging");
		claims.put("iss", clientEmail);
		return Jwts.builder().setHeaderParam("typ", "JWT").setClaims(claims)
				.setAudience("https://oauth2.googleapis.com/token").setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1)))
				.signWith(SignatureAlgorithm.RS256, key).compact();

	}

	public static boolean generateOAuthToken() {

		String assertion = generateJwtToken();
		String tokenEndpoint = "https://oauth2.googleapis.com/token";

		try {
			HttpClient client = HttpClient.newHttpClient();

			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(tokenEndpoint))
					.header("Content-Type", "application/x-www-form-urlencoded")
					.POST(HttpRequest.BodyPublishers
							.ofString("grant_type=urn:ietf:params:oauth:grant-type:jwt-bearer&assertion=" + assertion))
					.build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() != 200)
				return true;
//				throw new AdempiereException("Invalid Credential File");
			JSONObject jsonResponse = new JSONObject(response.body());

			token = jsonResponse.getString("access_token");

			int expiresIn = jsonResponse.getInt("expires_in");
			long expirationTimeMillis = System.currentTimeMillis() + expiresIn * 1000L;
			expTokenDate = new Date(expirationTimeMillis);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
