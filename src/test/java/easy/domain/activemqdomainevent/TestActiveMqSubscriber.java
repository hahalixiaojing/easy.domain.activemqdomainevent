package easy.domain.activemqdomainevent;


public class TestActiveMqSubscriber extends AbstractJsonActiveMqDomainEventSubscriber<DemoDomainEvent> {


    @Override
    public Class<?> suscribedToEventType() {
        return DemoDomainEvent.class;
    }

    @Override
    public void handleEvent(DemoDomainEvent data) {
        System.out.println(data.getName());
    }
}
