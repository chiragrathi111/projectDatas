package org.realmeds.tissue.activemq;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.compiere.util.CLogger;

public class ActiveMQProducer{

    private String brokerUrl = "tcp://127.0.0.1:61616";
    private String topicName = "tissueculture-queue";
    protected static final CLogger	log = CLogger.getCLogger (ActiveMQProducer.class);
    
    private static boolean activeMqStarted = false; 

    public void sendMessage(String table, int recordId) throws JMSException, InterruptedException {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);

        Connection connection = factory.createConnection();
        connection.start();

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        
        Destination destination = session.createQueue(topicName);

        MessageProducer producer = session.createProducer(destination);

        String messageContent = "{\"table\": \"" + table + "\", \"recordId\": " + recordId + "}";

        TextMessage message = session.createTextMessage(messageContent);

        producer.send(message);

        producer.close();
        session.close();
        connection.close();
        if(!activeMqStarted) {
        	ActiveMQConsumer consumer = new ActiveMQConsumer();
		 	consumer.run();
		 	activeMqStarted = true;
        }
    }
}