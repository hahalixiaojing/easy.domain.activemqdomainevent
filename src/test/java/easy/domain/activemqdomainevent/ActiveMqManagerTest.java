package easy.domain.activemqdomainevent;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;

import org.junit.AfterClass;
import org.junit.Test;

public class ActiveMqManagerTest {

	@Test
	public void queueTest() {
		ActiveMqManager m = new ActiveMqManager(
				"tcp://127.0.0.1:61616?wireFormat.maxInactivityDuration=0");

		MessageProducer p = m.createQueueProducer("testjava");

		m.registerQueueConsumer("testjava", new MessageListener() {

			@Override
			public void onMessage(Message message) {
				TextMessage msg = (TextMessage) message;

				try {
					String text = msg.getText();
					System.out.println(text);
					msg.acknowledge();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		int i = 0;
		while (i < 50) {
			try {
				TextMessage textMsg;
				textMsg = m.createTextMessage("new 你好啊");
				p.send(textMsg);
				Thread.sleep(1000);
			} catch (JMSException | InterruptedException e) {
				e.printStackTrace();
			}
			i++;

		}
	}

	@Test
	public void topicTest() {
		ActiveMqManager m = new ActiveMqManager(
				"tcp://127.0.0.1:61616?wireFormat.maxInactivityDuration=0",
				"client_001");

		MessageProducer p = m.createTopicPublisher("testtopic");

		m.registerTopicConsumer("testtopic", "wechat", new MessageListener() {

			@Override
			public void onMessage(Message message) {
				TextMessage textMsg = (TextMessage) message;

				try {
					String text = textMsg.getText();
					System.out.println("webchat" + text);

					message.acknowledge();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		m.registerTopicConsumer("testtopic", "email", new MessageListener() {

			@Override
			public void onMessage(Message message) {
				TextMessage textMsg = (TextMessage) message;

				try {
					String text = textMsg.getText();
					System.out.println("email" + text);

					message.acknowledge();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		int i = 0;
		while (i < 50) {
			try {
				TextMessage textMsg;
				textMsg = m.createTextMessage("new 你好啊");
				p.send(textMsg);
				Thread.sleep(1000);
			} catch (JMSException | InterruptedException e) {
				e.printStackTrace();
			}
			i++;

		}

	}
	@AfterClass
	public static void clear(){
		ActiveMqManagerFactory.clear();
	}
}
