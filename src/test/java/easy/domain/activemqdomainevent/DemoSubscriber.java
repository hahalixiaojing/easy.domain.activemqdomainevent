package easy.domain.activemqdomainevent;

import easy.domain.event.IDomainEvent;

public class DemoSubscriber implements IActiveMqDomainEventSubscriber {

	@Override
	public <T extends IDomainEvent> Class<?> suscribedToEventType() {
		return DemoDomainEvent.class;
	}

	@Override
	public void handleEvent(String aDomainEvent) {

		System.out.println("thread id=" + Thread.currentThread().getId());
		System.out.println("event name =" + aDomainEvent);
		System.out.println("subscriber =" + this.getClass().getName());
	}

}
