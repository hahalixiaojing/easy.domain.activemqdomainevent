package easy.domain.activemqdomainevent;

import com.alibaba.fastjson.JSON;
import easy.domain.event.IDomainEvent;


public abstract class AbstractJsonActiveMqDomainEventSubscriber<T extends IDomainEvent> implements IActiveMqDomainEventSubscriber {
    @Override
    public abstract Class<?> suscribedToEventType();

    public abstract void handleEvent(T data);

    @Override
    public void handleEvent(String aDomainEvent) {

        Object object = JSON.parseObject(aDomainEvent, this.<T>suscribedToEventType());
        this.handleEvent((T) object);
    }


}
