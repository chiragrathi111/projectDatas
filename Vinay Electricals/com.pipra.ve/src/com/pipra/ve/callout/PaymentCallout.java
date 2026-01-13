//package com.pipra.ve.callout;
//
//import java.io.BufferedReader;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.OutputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.nio.charset.StandardCharsets;
//import java.util.Properties;
//
//import org.codehaus.jettison.json.JSONObject;
//import org.compiere.model.CalloutEngine;
//import org.compiere.model.GridField;
//import org.compiere.model.GridTab;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.pipra.ve.model.response.ClientOrderResponse;
//import com.pipra.ve.model.response.CreateOrderResponse;
//
//public class PaymentCallout extends CalloutEngine{
//	
//	public String createOrder(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
//		System.out.println("Hello World");
////		mTab.fire
//		CreateOrderResponse orderResponse = createRazorPayOrder();
//		mTab.fireDataStatusEEvent (getRazorpayHtml(orderResponse), "", false);
//		return "";
//	}
//	
//	 private String getRazorpayHtml(CreateOrderResponse orderResponse) {
//		 return "<!DOCTYPE html>" + 
//		           "<html lang=\"en\">" + 
//		           "<head>" + 
//		           "    <meta charset=\"UTF-8\">" + 
//		           "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" + 
//		           "    <title>Payment</title>" + 
//		           "</head>" + 
//		           "<body>" + 
//		           "    <button id=\"rzp-button1\">Pay</button>" + 
//		           "    <script src=\"https://checkout.razorpay.com/v1/checkout.js\"></script>" + 
//		           "    <script>" + 
//		           "        var options = {" + 
////		           "            \"key\": "+orderResponse.getApiKeyId()+""," + "
//		        	"            \"key\": \"" + orderResponse.getApiKeyId() + "\"," +
//		           "            \"amount\": \""+orderResponse.getAmount()+"\"," + 
//		           "            \"currency\": \""+orderResponse.getCurrency()+"\"," + 
//		           "            \"name\": \""+orderResponse.getName()+"\"," + 
//		           "            \"description\": \""+orderResponse.getDescription()+"\"," + 
//		           "            \"image\": \""+orderResponse.getLogoUrl()+"\"," + 
//		           "            \"order_id\": \""+orderResponse.getOrderId()+"\"," + 
//		           "            \"handler\": function (response){" + 
//		           "                alert(response.razorpay_payment_id);" + 
//		           "                alert(response.razorpay_order_id);" + 
//		           "                alert(response.razorpay_signature);" + 
//		           "            }," + 
//		           "            \"prefill\": {" + 
//		           "                \"name\": \"Mahendhar Reddy\"," + 
//		           "                \"email\": \"mahendar@pipra.solutions\"," + 
//		           "                \"contact\": \"8247228170\"" + 
//		           "            }," + 
//		           "            \"notes\": {" + 
//		           "                \"address\": \"Pipra Corporate Office\"" + 
//		           "            }," + 
//		           "            \"theme\": {" + 
//		           "                \"color\": \"#3399cc\"" + 
//		           "            }" + 
//		           "        };" + 
//		           "        var rzp1 = new Razorpay(options);" + 
//		           "        rzp1.on('payment.failed', function (response){" + 
//		           "            alert(response.error.code);" + 
//		           "            alert(response.error.description);" + 
//		           "        });" + 
//		           "        document.getElementById('rzp-button1').onclick = function(e){" + 
//		           "            rzp1.open();" + 
//		           "            e.preventDefault();" + 
//		           "        };" + 
//		           "    </script>" + 
//		           "</body>" + 
//		           "</html>";}
//	 
//	private CreateOrderResponse createRazorPayOrder() {
//
//		String baseUrl = "http://localhost:8090/ADInterface/services/crm/createRazorpayOrder";
//
//		try {
//			URL url = new URL(baseUrl);
//			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//			conn.setRequestMethod("POST");
//			conn.setRequestProperty("Content-Type", "application/json");
//			conn.setRequestProperty("Accept", "application/json");
//
//			conn.setDoOutput(true);
//
//			JSONObject orderRequest = new JSONObject();
//			orderRequest.put("serviceType", "createRazorpayOrder");
//			orderRequest.put("customerName", "Mahendhar");
//
//			JSONObject CreateOrderRequest = new JSONObject();
//			CreateOrderRequest.put("CreateOrderRequest", orderRequest);
//
//			String paloadString = CreateOrderRequest.toString();
//
//			OutputStream outputStream = conn.getOutputStream();
//			outputStream.write(paloadString.getBytes(StandardCharsets.UTF_8));
//
//			int responseCode = conn.getResponseCode();
//			StringBuilder response = new StringBuilder();
//			if (responseCode == HttpURLConnection.HTTP_OK) {
//				System.out.println("Response Code: " + responseCode);
//
//				BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//				String inputLine;
//
//				while ((inputLine = in.readLine()) != null) {
//					response.append(inputLine);
//				}
//				in.close();
//
//			} else {
//				InputStream errorStream = conn.getErrorStream();
//				BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream));
//				String line;
//				while ((line = reader.readLine()) != null) {
//					System.out.println(line);
//				}
//				reader.close();
//			}
//
//			ObjectMapper objectMapper = new ObjectMapper();
//			
//			ClientOrderResponse orderResponse = objectMapper.readValue(response.toString(), ClientOrderResponse.class);
//
//			System.out.println(orderResponse.getCreateOrderResponse().getAmount());
//			System.out.println(orderResponse.getCreateOrderResponse().getApiKeyId());
//
//		
//			return orderResponse.getCreateOrderResponse();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//
//	}
//}
