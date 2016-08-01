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
	private Session session;
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

			session = connection.createSession(false,
					Session.CLIENT_ACKNOWLEDGE);
			System.out.println(session.getClass().getName());
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

	public Session getSession() {
		return session;
	}

	private void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public MessageProducer createTopicPublisher(String topicName) {
		Topic topic;
		try {
			topic = this.session.createTopic(topicName);
			return session.createProducer(topic);
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return null;
	}

	public MessageProducer createQueueProducer(String queueName) {
		Destination dest;
		try {
			dest = this.session.createQueue(queueName);
			return this.session.createProducer(dest);
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
			Topic topic = this.session.createTopic(topicName);
			MessageConsumer consumer = this.session.createDurableSubscriber(
					topic, subscriberName, selector, false);

			consumer.setMessageListener(listener);
			this.topicConsumers.put(subscriberName, consumer);

		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public void registerQueueConsumer(String name, MessageListener listener) {
		try {
			Queue q = this.session.createQueue(name);
			MessageConsumer consumer = session.createConsumer(q);
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

	@Override
	protected void finalize() {
		if (connection != null) {
			try {
				connection.close();
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
	}

}
