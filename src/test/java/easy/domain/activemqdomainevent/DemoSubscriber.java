package easy.domain.activemqdomainevent;

import com.alibaba.fastjson.JSON;

public class DemoSubscriber extends AbstractJsonActiveMqDomainEventSubscriber<DemoDomainEvent> {
    @Override
    public Class<?> subscribedToEventType() {
        return DemoDomainEvent.class;
    }

    @Override
    public void handleEvent(DemoDomainEvent data) {
        System.out.println("这是我");
        System.out.println("thread id=" + Thread.currentThread().getId() + " " + JSON.toJSONString(data) + " " + this.getClass().getSimpleName());
    }
}
