package easy.domain.eventactivemq;

import easy.domain.application.BaseApplication;
import easy.domain.application.subscriber.IDomainEventManager;
import easy.domain.eventactivemq.addevents.TestDomainEvent;

public class DemoApplication extends BaseApplication {

	public DemoApplication(IDomainEventManager manager) {
		super(manager);
	}

	public void add() {

		TestDomainEvent evt = new TestDomainEvent();
		evt.setName("test");

		this.publishEvent(evt);
	}
}
