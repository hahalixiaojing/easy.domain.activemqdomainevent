package easy.domain.eventactivemq.addevents;

import easy.domain.activemqdomainevent.IActiveMqDomainEventSubscriber;
import easy.domain.event.IDomainEvent;

public class WechatSubscriber implements IActiveMqDomainEventSubscriber {

	@Override
	public  Class<?> suscribedToEventType() {
		return TestDomainEvent.class;
	}

	@Override
	public void handleEvent(String aDomainEvent) {

		System.out.println(aDomainEvent);
	}

}
