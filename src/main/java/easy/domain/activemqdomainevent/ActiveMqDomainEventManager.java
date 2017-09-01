package easy.domain.activemqdomainevent;

import java.util.*;

import javax.jms.DeliveryMode;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;

import com.alibaba.fastjson.JSON;

import easy.domain.application.subscriber.IDomainEventManager;
import easy.domain.application.subscriber.ISubscriber;
import easy.domain.event.EventName;
import easy.domain.event.IDomainEvent;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActiveMqDomainEventManager implements IDomainEventManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActiveMqDomainEventManager.class);

    private final String environmentName;
    private final ActiveMqManager manager;
    private final MessageProducer messageProducer;
    private Map<String, List<ISubscriber>> stringListMap = new HashMap<>();
    private final Map<String, Map<String, ISubscriber>> subsribers = new HashMap<>();


    public ActiveMqDomainEventManager(ActiveMqManager activeMqManager, String environmentName) throws Exception {
        this.manager = activeMqManager;
        this.environmentName = environmentName;
        this.messageProducer = activeMqManager.createQueueProducer();
    }

    private String getEventName(Class<?> eventType) {
        EventName alias = eventType.getAnnotation(EventName.class);

        String evtName = "";
        if (alias == null) {
            evtName = eventType.getSimpleName();
        } else {
            evtName = alias.value();
        }
        if (StringUtils.isNotBlank(this.environmentName)) {
            evtName = evtName + "_" + this.environmentName;
        }
        return evtName;
    }

    private void registerSubscriber(ISubscriber subscriber, String event, String alias) {
        if (!subsribers.containsKey(event)) {

            Map<String, ISubscriber> subscriberMap = new HashMap<>();
            subscriberMap.put(alias, subscriber);

            subsribers.put(event, subscriberMap);
        } else {
            Map<String, ISubscriber> stringISubscriberMap = subsribers.get(event);
            if (stringISubscriberMap.containsKey(alias)) {
                throw new IllegalArgumentException(alias + " is duplication");
            }

            subsribers.get(event).put(alias, subscriber);
        }
    }

    @Override
    public void registerDomainEvent(Class<?> domainEventType) {
        String evtName = this.getEventName(domainEventType);
        this.manager.registerQueueConsumer(evtName, new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    ActiveMQQueue jmsDestination = (ActiveMQQueue) message.getJMSDestination();
                    String event = jmsDestination.getQueueName();
                    String subscriber = message.getStringProperty("SUBSCRIBER");

                    Map<String, ISubscriber> stringISubscriberMap = subsribers.get(event);
                    if (stringISubscriberMap != null) {
                        IActiveMqDomainEventSubscriber iSubscriber = (IActiveMqDomainEventSubscriber) stringISubscriberMap.get(subscriber);
                        if (iSubscriber != null) {
                            TextMessage textMessage = (TextMessage) message;
                            iSubscriber.handleEvent(textMessage.getText());
                            message.acknowledge();
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("message error", e);
                }
            }
        });
    }

    @Override
    public void registerSubscriber(ISubscriber subscriber) {
        throw new NotImplementedException("not implement");
    }

    @Override
    public void registerSubscriber(ISubscriber subscriber, String alias) {
        if (StringUtils.isBlank(alias)) {
            throw new NullPointerException("alias value is null or empty");
        }

        String event = getEventName(subscriber.subscribedToEventType());
        this.registerSubscriber(subscriber, event, alias);
    }

    @Override
    public void registerDomainEvent(Set<Class<?>> domainEventTypes) {

        for (Class<?> cls : domainEventTypes) {
            this.registerDomainEvent(cls);
        }
    }

    @Override
    public void registerSubscriber(Set<ISubscriber> items) {
        throw new NotImplementedException("not implement");
    }

    @Override
    public <T extends IDomainEvent> void publishEvent(T obj) throws Exception {

        String evt = this.getEventName(obj.getClass());
        Map<String, ISubscriber> stringISubscriberMap = this.subsribers.get(evt);

        if (stringISubscriberMap != null) {
            String jsonText = JSON.toJSONString(obj);

            Collection<ISubscriber> subscribers = stringISubscriberMap.values();
            for (Map.Entry<String, ISubscriber> subscriber : stringISubscriberMap.entrySet()) {
                TextMessage textMsg = this.manager.createTextMessage(jsonText);
                textMsg.setStringProperty("SUBSCRIBER", subscriber.getKey());
                textMsg.setJMSDeliveryMode(DeliveryMode.PERSISTENT);
                messageProducer.send(new ActiveMQQueue(evt), textMsg);
            }
        }


    }

    public <T extends IDomainEvent> void publishEvent(T obj, String subscriber) throws Exception {
        //TODO:待实现
    }
}
