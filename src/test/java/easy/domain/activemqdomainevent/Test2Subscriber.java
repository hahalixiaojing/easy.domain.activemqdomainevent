package easy.domain.activemqdomainevent;

import com.alibaba.fastjson.JSON;


public class Test2Subscriber extends AbstractJsonActiveMqDomainEventSubscriber<Demo2DomainEvent> {
    @Override
    public Class<?> subscribedToEventType() {
        return Demo2DomainEvent.class;
    }

    @Override
    public void handleEvent(Demo2DomainEvent data) {
        System.out.println("thread id=" + Thread.currentThread().getId() + " " + JSON.toJSONString(data) + " " + this.getClass().getSimpleName());
    }
}
