package easy.domain.activemqdomainevent;

import java.util.ArrayList;

import org.junit.Test;

import easy.domain.event.ISubscriber;

public class ActiveMqDomainEventManagerTest {
	@Test
	public void loadTest() {
		ActiveMqManager m = new ActiveMqManager(
				"tcp://127.0.0.1:61616?wireFormat.maxInactivityDuration=0","abbbc");

		ActiveMqDomainEventManager manager = new ActiveMqDomainEventManager(m);
		
		ArrayList<Class<?>> ar =new ArrayList<>();
		ar.add(DemoDomainEvent.class);
		
		manager.registerDomainEvent(ar);
		
		DemoSubscriber dsub =new DemoSubscriber();
		
		ArrayList<ISubscriber> sub =new ArrayList<>();
		sub.add(dsub);
		
		manager.registerSubscriber("", sub);
		
		DemoDomainEvent evt =new DemoDomainEvent();
		evt.setName("test");
		
		manager.publishEvent("", evt);
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
