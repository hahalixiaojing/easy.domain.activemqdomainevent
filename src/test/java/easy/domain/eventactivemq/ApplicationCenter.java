package easy.domain.eventactivemq;

import easy.domain.activemqdomainevent.ActiveMqDomainEventManager;
import easy.domain.activemqdomainevent.ActiveMqManager;
import easy.domain.application.ApplicationFactory;

public abstract class ApplicationCenter {
	static {

		ActiveMqManager m = new ActiveMqManager(
				"tcp://127.0.0.1:61616?wireFormat.maxInactivityDuration=0",
				"abbbc");

		ActiveMqDomainEventManager manager = new ActiveMqDomainEventManager(m);

		ApplicationFactory.instance().register(new DemoApplication(manager));
	}

	public static DemoApplication getDemo() {
		return ApplicationFactory.instance().get(DemoApplication.class);
	}
}
