package easy.domain.activemqdomainevent;

import easy.domain.event.IDomainEvent;

/**
 * Created by lixiaojing3 on 2017/1/18.
 */
public class Demo2DomainEvent implements IDomainEvent {
    public String name;

    public Demo2DomainEvent() {
    }

    public Demo2DomainEvent(String name) {
        this.name = name;
    }
}
