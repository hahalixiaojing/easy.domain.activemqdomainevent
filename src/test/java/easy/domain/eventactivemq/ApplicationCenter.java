package easy.domain.eventactivemq;

import easy.domain.activemqdomainevent.ActiveMqDomainEventManager;
import easy.domain.activemqdomainevent.ActiveMqManager;
import easy.domain.activemqdomainevent.ActiveMqManagerFactory;
import easy.domain.application.ApplicationFactory;

public abstract class ApplicationCenter {
	static {

		ActiveMqManager m = ActiveMqManagerFactory.createActiveMqManager(
				"tcp://127.0.0.1:61616?wireFormat.maxInactivityDuration=0",
				"abbbc");

		ActiveMqDomainEventManager manager = new ActiveMqDomainEventManager(m);
		try {
			ApplicationFactory.instance()
					.register(new DemoApplication(manager));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static DemoApplication getDemo() {
		return ApplicationFactory.instance().get(DemoApplication.class);
	}
}
