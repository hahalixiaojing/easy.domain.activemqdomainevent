package easy.domain.activemqdomainevent;

import easy.domain.event.IDomainEvent;

public class DemoDomainEvent implements IDomainEvent {

    public String name;

    public DemoDomainEvent() {
    }

    public DemoDomainEvent(String name) {
        this.name = name;
    }
}
