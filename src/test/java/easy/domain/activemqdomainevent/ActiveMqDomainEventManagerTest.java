package easy.domain.activemqdomainevent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;

import easy.domain.event.ISubscriber;

public class ActiveMqDomainEventManagerTest {

	private ActiveMqDomainEventManager create(String clientid) {

		ActiveMqManager activeMq = ActiveMqManagerFactory
				.createActiveMqManager(
						"tcp://127.0.0.1:61616?wireFormat.maxInactivityDuration=0",
						clientid);

		ActiveMqDomainEventManager manager = new ActiveMqDomainEventManager(
				activeMq);

		ArrayList<Class<?>> ar = new ArrayList<>();
		ar.add(DemoDomainEvent.class);

		manager.registerDomainEvent(ar);

		DemoSubscriber dsub = new DemoSubscriber();
		Demo2Subscriber dsub2 = new Demo2Subscriber();
		ArrayList<ISubscriber> sub = new ArrayList<>();
		sub.add(dsub);
		sub.add(dsub2);

		manager.registerSubscriber("", sub);

		return manager;

	}

	@Test
	public void loadTest() throws Exception {
		DemoDomainEvent evt = new DemoDomainEvent();
		evt.setName("test");

		ActiveMqDomainEventManager m1 = this.create("abbbc");
		ActiveMqDomainEventManager m2 = this.create("acccb");


		Callable<String> c1 = () -> {
			for (int i = 0; i < 1000; i++) {
				m1.publishEvent("", evt);
				System.out.println("send ="+ Thread.currentThread().getId());
			}

			return "OK1";
		};

		Callable<String> c2 = () -> {
			for (int i = 0; i < 1000; i++) {
				m2.publishEvent("", evt);
				System.out.println("send ="+ Thread.currentThread().getId());
			}

			return "OK2";
		};

		List<Callable<String>> callables = new ArrayList<>(2);
		callables.add(c1);
		callables.add(c2);

		StopWatch sw = new StopWatch();
		sw.start();

		List<Future<String>> result = Executors.newFixedThreadPool(2)
				.invokeAll(callables);

		sw.stop();
		System.out.println(String.format("total time is %s", sw.getTime()));

		result.stream().forEach(f -> {

			try {
				String r = f.get();
				System.out.println(r);
			} catch (Exception e) {
				e.printStackTrace();
			}

		});

		ActiveMqManagerFactory.clear();
		
		

	}
}
