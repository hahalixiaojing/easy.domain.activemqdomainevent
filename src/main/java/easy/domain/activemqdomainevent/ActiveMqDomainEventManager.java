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
import easy.domain.event.IDomainEvent;
import org.apache.activemq.command.ActiveMQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActiveMqDomainEventManager implements IDomainEventManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActiveMqDomainEventManager.class);

    private final ActiveMqManager manager;
    private final MessageProducer messageProducer;
    private Map<String, List<ISubscriber>> stringListMap = new HashMap<>();

    public ActiveMqDomainEventManager(ActiveMqManager activeMqManager) throws Exception {
        this.manager = activeMqManager;
        this.messageProducer = activeMqManager.createQueueProducer();
    }

    @Override
    public void registerDomainEvent(Class<?> domainEventType) {
        String evtName = ClassUtils.getShortName(domainEventType);
        this.manager.registerQueueConsumer(evtName, new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    String event = message.getStringProperty("EVENT");
                    String subscriber = message.getStringProperty("SUBSCRIBER");

                    List<ISubscriber> subscribers = stringListMap.get(event);
                    for (ISubscriber s : subscribers) {
                        if (ClassUtils.getShortName(s.getClass()).equals(subscriber)) {
                            IActiveMqDomainEventSubscriber mqSubscriber = (IActiveMqDomainEventSubscriber) s;
                            TextMessage textMessage = (TextMessage) message;
                            mqSubscriber.handleEvent(textMessage.getText());
                            message.acknowledge();
                            break;
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
        String event = ClassUtils.getShortName(subscriber.subscribedToEventType());

        if (stringListMap.containsKey(event)) {
            stringListMap.get(event).add(subscriber);
        } else {
            List<ISubscriber> subscribers = new ArrayList<>();
            subscribers.add(subscriber);

            stringListMap.put(event, subscribers);
        }
    }
    @Override
    public void registerSubscriber(ISubscriber subscriber, String alias) {
        //TODO:待实现
    }

    @Override
    public void registerDomainEvent(Set<Class<?>> domainEventTypes) {

        for (Class<?> cls : domainEventTypes) {
            this.registerDomainEvent(cls);
        }
    }

    @Override
    public void registerSubscriber(Set<ISubscriber> items) {

        for (ISubscriber subscriber : items) {
            this.registerSubscriber(subscriber);
        }
    }

    @Override
    public <T extends IDomainEvent> void publishEvent(T obj) throws Exception {

        String evt = ClassUtils.getShortName(obj.getClass());
        List<ISubscriber> subscribers = this.stringListMap.get(evt);
        String jsonText = JSON.toJSONString(obj);

        for (ISubscriber subscriber : subscribers) {
            TextMessage textMsg = this.manager.createTextMessage(jsonText);
            textMsg.setStringProperty("EVENT", evt);
            textMsg.setStringProperty("SUBSCRIBER", ClassUtils.getShortName(subscriber.getClass()));
            textMsg.setJMSDeliveryMode(DeliveryMode.PERSISTENT);
            messageProducer.send(new ActiveMQQueue(evt), textMsg);
        }
    }

    public <T extends IDomainEvent> void publishEvent(T obj, String subscriber) throws Exception {
        //TODO:待实现
    }
}
