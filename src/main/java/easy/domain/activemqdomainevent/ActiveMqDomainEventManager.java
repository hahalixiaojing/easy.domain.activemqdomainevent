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
    private Map<String, List<ISubscriber>> stringListMap = new HashMap<>();

    public ActiveMqDomainEventManager(ActiveMqManager activeMqManager) throws Exception {
        this.manager = activeMqManager;
    }

    @Override
    public void registerDomainEvent(List<Class<?>> domainEventTypes) {

        for (Class<?> cls : domainEventTypes) {

            String evtName = ClassUtils.getShortName(cls);
            MessageProducer messageProducer = this.manager.createQueueProducer(evtName);

            this.producers.put(evtName, messageProducer);
            for (int i = 0; i < 2; i++) {
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
                        }

                    }
                });
            }

        }

    }

    @Override
    public void registerSubscriber(List<ISubscriber> items) {

        for (ISubscriber subscriber : items) {
            String event = ClassUtils.getShortName(subscriber.subscribedToEventType());

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
        if (!this.producers.containsKey(evt) || !this.stringListMap.containsKey(evt)) {
            return;
        }

        MessageProducer producer = producers.get(evt);

        List<ISubscriber> subscribers = this.stringListMap.get(evt);
        String jsonText = JSON.toJSONString(obj);

        for (ISubscriber subscriber : subscribers) {
            TextMessage textMsg = this.manager.createTextMessage(jsonText);
            textMsg.setStringProperty("EVENT", evt);
            textMsg.setStringProperty("SUBSCRIBER", ClassUtils.getShortName(subscriber.getClass()));
            textMsg.setJMSDeliveryMode(DeliveryMode.PERSISTENT);
            producer.send(textMsg);
        }
    }
}
