package easy.domain.activemqdomainevent;

import com.alibaba.fastjson.JSON;
import easy.domain.application.subscriber.ISubscriber;
import easy.domain.event.IDomainEvent;


public abstract class AbstractJsonActiveMqDomainEventSubscriber<T extends IDomainEvent> implements IActiveMqDomainEventSubscriber {

    public abstract Class<?> subscribedToEventType();

    public abstract void handleEvent(T data) throws Exception;

    @Override
    @SuppressWarnings("unchecked")
    public void handleEvent(String aDomainEvent) throws Exception {

        Object object = JSON.parseObject(aDomainEvent, this.<T>subscribedToEventType());
        this.handleEvent((T) object);
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        ISubscriber subscriber = (ISubscriber) obj;
        if (subscriber == null) {
            return false;
        }
        return this.getClass() == subscriber.getClass();
    }
}
