package easy.domain.activemqdomainevent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;

import easy.domain.application.subscriber.IDomainEventManager;
import easy.domain.event.IDomainEvent;
import easy.domain.event.ISubscriber;

public class ActiveMqDomainEventManager implements IDomainEventManager {

    private final ActiveMqManager manager;
    private final HashMap<String, MessageProducer> producers = new HashMap<>();
    private Map<String, List<ISubscriber>> stringListMap;

    public ActiveMqDomainEventManager(ActiveMqManager activeMqManager) throws Exception {
        this.manager = activeMqManager;
    }

    @Override
    public void registerDomainEvent(List<Class<?>> domainEventTypes) {

        for (Class<?> cls : domainEventTypes) {

            String evtName = ClassUtils.getShortName(cls);
            MessageProducer messageProducer = this.manager.createQueueProducer(ClassUtils.getShortName(cls));

            this.producers.put(evtName, messageProducer);

            this.manager.registerQueueConsumer(evtName, new MessageListener() {
                @Override
                public void onMessage(Message message) {
                }
            });

        }

    }

    @Override
    public void registerSubscriber(List<ISubscriber> items) {

        stringListMap = new HashMap<>();

        for (ISubscriber subscriber : items) {
            String event = ClassUtils.getShortName(subscriber.suscribedToEventType());

            if (stringListMap.containsKey(event)) {
                stringListMap.get(event).add(subscriber);
            } else {
                List<ISubscriber> subscribers = new ArrayList<>();
                subscribers.add(subscriber);

                stringListMap.put(event, subscribers);
            }
        }
    }

    @Override
    public <T extends IDomainEvent> void publishEvent(T obj) throws Exception {

        String evt = ClassUtils.getShortName(obj.getClass());
        if (!this.producers.containsKey(evt)) {
            return;
        }

        if (this.stringListMap.containsKey(evt)) {
            return;
        }

        MessageProducer producer = producers.get(evt);

        List<ISubscriber> subscribers = this.stringListMap.get(evt);
        for (ISubscriber subscriber : subscribers) {
            String jsonText = JSON.toJSONString(obj);
            TextMessage textMsg = this.manager.createTextMessage(jsonText);
            textMsg.setStringProperty("EVENT", evt);
            textMsg.setStringProperty("SUBSCRIBER", subscriber.getClass().getName());
            textMsg.setJMSDeliveryMode(DeliveryMode.PERSISTENT);
            producer.send(textMsg);
        }
    }
}
