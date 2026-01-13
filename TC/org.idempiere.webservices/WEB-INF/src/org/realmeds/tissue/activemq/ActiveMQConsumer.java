package org.realmeds.tissue.activemq;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.codehaus.jettison.json.JSONObject;
import org.compiere.util.DB;

public class ActiveMQConsumer implements Runnable {

	private String brokerUrl = "tcp://127.0.0.1:61616";
	private String topicName = "tissueculture-update-queue";

	@Override
	public void run() {
		try {
			ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
			Connection connection = factory.createConnection();
			connection.start();

			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createQueue(topicName);

			MessageConsumer consumer = session.createConsumer(destination);
			consumer.setMessageListener(message -> {
				try {
					if (message instanceof TextMessage) {
						String text = ((TextMessage) message).getText();
						processMessage(text);
						System.out.println("Received TextMessage: " + text);
					} else if (message instanceof BytesMessage) {
						BytesMessage bytesMessage = (BytesMessage) message;
						byte[] data = new byte[(int) bytesMessage.getBodyLength()];
						bytesMessage.readBytes(data);
						String text = new String(data);
						processMessage(text);
						System.out.println("Received BytesMessage: " + text);
					} else {
						System.out.println("Received unsupported message type: " + message.getClass());
					}
				} catch (JMSException e) {
					e.printStackTrace();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void processMessage(String message) {
		try {
			JSONObject json = new JSONObject(message);
			String table = json.getString("tableName");
			String recordId = json.getString("recordId");
			String uuid = json.getString("uuid");
			String tablecolumn = (table + "_id");
			int recordIdInt = Integer.parseInt(recordId);

			String sql = "UPDATE " + table + " SET c_uuid = ? WHERE " + tablecolumn + " = ?";
			DB.executeUpdate(sql, new Object[] { uuid, recordIdInt }, false, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}