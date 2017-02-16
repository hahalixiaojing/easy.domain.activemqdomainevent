package easy.domain.activemqdomainevent;

import com.alibaba.fastjson.JSON;


public class Demo2Subscriber extends AbstractJsonActiveMqDomainEventSubscriber<DemoDomainEvent> {


    @Override
    public Class<?> subscribedToEventType() {
        return DemoDomainEvent.class;
    }

    @Override
    public void handleEvent(DemoDomainEvent data) throws Exception {
        System.out.println("thread id=" + Thread.currentThread().getId() + " " + JSON.toJSONString(data) + " " + this.getClass().getSimpleName());

    }
}
