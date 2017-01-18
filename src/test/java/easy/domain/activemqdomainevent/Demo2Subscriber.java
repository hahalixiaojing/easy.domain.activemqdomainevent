package easy.domain.activemqdomainevent;

import com.alibaba.fastjson.JSON;
import easy.domain.event.IDomainEvent;

import static javafx.scene.input.KeyCode.J;

public class Demo2Subscriber extends AbstractJsonActiveMqDomainEventSubscriber<DemoDomainEvent> {


    @Override
    public Class<?> subscribedToEventType() {
        return DemoDomainEvent.class;
    }

    @Override
    public void handleEvent(DemoDomainEvent data) {
        System.out.println("thread id=" + Thread.currentThread().getId() + " " + JSON.toJSONString(data) + " " + this.getClass().getSimpleName());

    }
}
