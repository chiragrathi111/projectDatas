//package com.pipra.ve.process;
//
//import java.io.BufferedReader;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.OutputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.nio.charset.StandardCharsets;
//
//import org.codehaus.jettison.json.JSONObject;
//import org.compiere.process.SvrProcess;
//import org.compiere.util.Env;
//import org.compiere.util.Msg;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.pipra.ve.model.response.ClientOrderResponse;
//
//public class PaymentProcess extends SvrProcess {
//
//	@Override
//	protected void prepare() {
////		Popup popup = new Popup();
////		popup.open(50, 50);
//	}
//
//	@Override
//	protected String doIt() throws Exception {
////		createOrder();
////		Popup popup = new Popup();
////		popup.open(50, 50);
////		String html = getRazorpayHtml();
//		openMobileUserPanelPopup();
//		return null;
//	}
//	
//	protected void openMobileUserPanelPopup() {
//		
////		GridTab mTab
////		Grid
//	}
//
//	 private void showPopup() {
//	        // Create a message dialog to show "Hello World"
//	        String message = Msg.getMsg(Env.getCtx(), "Hello World");
//	        // Use a simple JOptionPane to display the message
//	        javax.swing.JOptionPane.showMessageDialog(null, message, "Message", javax.swing.JOptionPane.INFORMATION_MESSAGE);
//	    }
//	 
//	 private String getRazorpayHtml() {
//	        return "<!DOCTYPE html>" +
//	               "<html lang=\"en\">" +
//	               "<head>" +
//	               "    <meta charset=\"UTF-8\">" +
//	               "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
//	               "    <title>Payment</title>" +
//	               "</head>" +
//	               "<body>" +
//	               "    <button id=\"rzp-button1\">Pay</button>" +
//	               "    <script src=\"https://checkout.razorpay.com/v1/checkout.js\"></script>" +
//	               "    <script>" +
//	               "        var options = {" +
//	               "            \"key\": \"rzp_test_Ok1b1hFZM4KrLc\"," + // Your Key ID
//	               "            \"amount\": \"50000\"," + // Amount in paise
//	               "            \"currency\": \"INR\"," +
//	               "            \"name\": \"Yearly Subscription\"," +
//	               "            \"description\": \"Test Transaction\"," +
//	               "            \"image\": \"https://example.com/your_logo\"," +
//	               "            \"order_id\": \"order_PQbzexVxO70DNv\"," +
//	               "            \"handler\": function (response){" +
//	               "                alert(response.razorpay_payment_id);" +
//	               "                alert(response.razorpay_order_id);" +
//	               "                alert(response.razorpay_signature);" +
//	               "            }," +
//	               "            \"prefill\": {" +
//	               "                \"name\": \"Mahendhar Reddy\"," +
//	               "                \"email\": \"mahendar@pipra.solutions\"," +
//	               "                \"contact\": \"8247228170\"" +
//	               "            }," +
//	               "            \"notes\": {" +
//	               "                \"address\": \"Pipra Corporate Office\"" +
//	               "            }," +
//	               "            \"theme\": {" +
//	               "                \"color\": \"#3399cc\"" +
//	               "            }" +
//	               "        };" +
//	               "        var rzp1 = new Razorpay(options);" +
//	               "        rzp1.on('payment.failed', function (response){" +
//	               "            alert(response.error.code);" +
//	               "            alert(response.error.description);" +
//	               "            alert(response.error.source);" +
//	               "            alert(response.error.step);" +
//	               "            alert(response.error.reason);" +
//	               "            alert(response.error.metadata.order_id);" +
//	               "            alert(response.error.metadata.payment_id);" +
//	               "        });" +
//	               "        document.getElementById('rzp-button1').onclick = function(e){" +
//	               "            rzp1.open();" +
//	               "            e.preventDefault();" +
//	               "        };" +
//	               "    </script>" +
//	               "</body>" +
//	               "</html>";
//	    }
//	 
//	private ClientOrderResponse createOrder() {
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
//			ClientOrderResponse orderResponse = objectMapper.readValue(response.toString(), ClientOrderResponse.class);
//
////			System.out.println(orderResponse.getAmount());
//			return orderResponse;
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//
//	}
//
//}
