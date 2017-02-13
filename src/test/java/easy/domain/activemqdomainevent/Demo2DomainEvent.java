package easy.domain.activemqdomainevent;

import easy.domain.event.IDomainEvent;


public class Demo2DomainEvent implements IDomainEvent {
    public String name;
    private String businessId;

    public Demo2DomainEvent() {
    }

    public Demo2DomainEvent(String name) {
        this.name = name;
    }

    @Override
    public String getBusinessId() {
        return businessId;
    }
}
