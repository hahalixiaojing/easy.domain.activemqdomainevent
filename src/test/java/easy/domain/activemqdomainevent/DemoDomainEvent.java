package easy.domain.activemqdomainevent;

import easy.domain.event.EventName;
import easy.domain.event.IDomainEvent;

@EventName("NewEvent")
public class DemoDomainEvent implements IDomainEvent {

    public String name;

    public DemoDomainEvent() {
    }

    public DemoDomainEvent(String name) {
        this.name = name;
    }

    @Override
    public String getBusinessId() {
        return null;
    }
}
