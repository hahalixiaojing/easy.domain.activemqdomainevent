package easy.domain.activemqdomainevent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.Ignore;
import org.junit.Test;

import easy.domain.event.ISubscriber;

public class ActiveMqDomainEventManagerTest {
	@Test
	public void loadTest() throws Exception {
		ActiveMqManager m = new ActiveMqManager(
				"tcp://127.0.0.1:61616?wireFormat.maxInactivityDuration=0",
				"abbbc");

		ActiveMqDomainEventManager manager = new ActiveMqDomainEventManager(m);

		ArrayList<Class<?>> ar = new ArrayList<>();
		ar.add(DemoDomainEvent.class);

		manager.registerDomainEvent(ar);

		DemoSubscriber dsub = new DemoSubscriber();
		Demo2Subscriber dsub2 = new Demo2Subscriber();
		ArrayList<ISubscriber> sub = new ArrayList<>();
		sub.add(dsub);
		sub.add(dsub2);

		manager.registerSubscriber("", sub);

		DemoDomainEvent evt = new DemoDomainEvent();
		evt.setName("test");

		ArrayList<Callable<String>> list = new ArrayList<Callable<String>>();

		for (int i = 0; i < 1; i++) {

			Callable<String> r = () -> {

				for (int j = 0; j < 2000; j++) {

					manager.publishEvent("", evt);
					System.out.println(j);
				}

				return "";
			};

			list.add(r);

		}
		StopWatch sw = new StopWatch();
		sw.start();
		List<Future<String>> result = Executors.newFixedThreadPool(2)
				.invokeAll(list);
		sw.stop();

		System.out.println("time=" + sw.getTime());
		result.get(0);

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		m.close();

	}
}
