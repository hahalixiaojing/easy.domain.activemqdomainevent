package easy.domain.activemqdomainevent;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageNotWriteableException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ActiveMqManager {
	private Connection connection;
	private final List<MessageConsumer> queueConsumers = new ArrayList<>();
	private final HashMap<String, MessageConsumer> topicConsumers = new HashMap<>();

	private String clientId;

	public ActiveMqManager(String url, String clientid, String usrname,
			String password) {

		this.setClientId(clientid);

		ConnectionFactory factory = new ActiveMQConnectionFactory(url);
		try {
			if (!StringUtils.isEmpty(usrname) && !StringUtils.isEmpty(password)) {
				connection = factory.createConnection(usrname, password);
			} else {
				connection = factory.createConnection();
			}

			if (!StringUtils.isEmpty(clientid)) {
				connection.setClientID(clientid);
			}
			connection.start();

		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public ActiveMqManager(String url) {
		this(url, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
	}

	public ActiveMqManager(String url, String clientId) {
		this(url, clientId, StringUtils.EMPTY, StringUtils.EMPTY);
	}

	public String getClientId() {
		return clientId;
	}

	private void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public MessageProducer createTopicPublisher(String topicName) {
		Topic topic;
		try {
			Session publisherSession = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
			topic = publisherSession.createTopic(topicName);
			return publisherSession.createProducer(topic);
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return null;
	}

	public MessageProducer createQueueProducer(String queueName) {
		Destination dest;
		try {
			Session producerSession = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
			dest = producerSession.createQueue(queueName);
			return producerSession.createProducer(dest);
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void registerTopicConsumer(String topicName, String subscriberName,
			MessageListener listener) {
		this.registerTopicConsumer(topicName, subscriberName, null, listener);
	}

	public void registerTopicConsumer(String topicName, String subscriberName,
			String selector, MessageListener listener) {

		try {
			Session consumerSession = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
			Topic topic = consumerSession.createTopic(topicName);
			MessageConsumer consumer = consumerSession.createDurableSubscriber(
					topic, subscriberName, selector, false);

			consumer.setMessageListener(listener);
			this.topicConsumers.put(subscriberName, consumer);

		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public void registerQueueConsumer(String name, MessageListener listener) {
		try {
			Session consumerSession = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
			Queue q = consumerSession.createQueue(name);
			MessageConsumer consumer = consumerSession.createConsumer(q);
			consumer.setMessageListener(listener);
			this.queueConsumers.add(consumer);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public TextMessage createTextMessage(String text) {
		ActiveMQTextMessage message = new ActiveMQTextMessage();

		try {
			message.setText(text);
		} catch (MessageNotWriteableException e) {
			e.printStackTrace();
		}
		message.setConnection((ActiveMQConnection) connection);
		return message;
	}
	
	public void close(){
		if (connection != null) {
			try {
				connection.close();
			} catch (JMSException e) {

			}
		}
	}
}
