package easy.domain.activemqdomainevent;

import java.util.HashMap;
import java.util.List;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;

import easy.domain.application.IDomainEventManager;
import easy.domain.event.IDomainEvent;
import easy.domain.event.ISubscriber;

public class ActiveMqDomainEventManager implements IDomainEventManager {

	private final ActiveMqManager manager;
	private final HashMap<String, MessageProducer> producers = new HashMap<>();

	public ActiveMqDomainEventManager(ActiveMqManager activeMqManager) {
		this.manager = activeMqManager;
	}

	@Override
	public void registerDomainEvent(List<Class<?>> domainEventTypes) {

		for (Class<?> cls : domainEventTypes) {

			String evtName = cls.getName();
			MessageProducer producer = this.manager
					.createTopicPublisher(evtName);
			this.producers.put(evtName, producer);
		}

	}

	@Override
	public void registerSubscriber(List<ISubscriber> items) {

		String evtName = StringUtils.EMPTY;
		if (items.size() > 0) {
			evtName = items.get(0).suscribedToEventType().getName();
		}
		String route = String.format("route='%s'", this.manager.getClientId());

		for (final ISubscriber subscriber : items) {

			String subscriberName = subscriber.getClass().getName();
			this.manager.registerTopicConsumer(evtName, subscriberName, route,
					new MessageListener() {

						@Override
						public void onMessage(Message message) {
							TextMessage textMsg = (TextMessage) message;
							IActiveMqDomainEventSubscriber sub = (IActiveMqDomainEventSubscriber) subscriber;

							try {
								sub.handleEvent(textMsg.getText());
								message.acknowledge();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
		}
	}

	@Override
	public <T extends IDomainEvent> void publishEvent(T obj) {
		String evt = obj.getClass().getName();
		if (!this.producers.containsKey(evt)) {
			return;
		}
		MessageProducer producer = producers.get(evt);
		String jsonText = JSON.toJSONString(obj);
		TextMessage textMsg = this.manager.createTextMessage(jsonText);
		try {
			textMsg.setStringProperty("route", this.manager.getClientId());
			textMsg.setJMSDeliveryMode(DeliveryMode.PERSISTENT);
			producer.send(textMsg);

		} catch (JMSException e) {
		}
	}
}
