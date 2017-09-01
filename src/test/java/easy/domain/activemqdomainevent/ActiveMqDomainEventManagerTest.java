package easy.domain.activemqdomainevent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import easy.domain.application.subscriber.ISubscriber;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.AfterClass;
import org.junit.Test;

import org.junit.rules.Stopwatch;

public class ActiveMqDomainEventManagerTest {

    private ActiveMqDomainEventManager create() throws Exception {

        // failover:(tcp://broker1:61616,tcp://broker2:61616,tcp://broker3:61616)

//		ActiveMqManager activeMq = ActiveMqManagerFactory
//				.createActiveMqManager(
//						"failover:(tcp://127.0.0.1:61616?wireFormat.maxInactivityDuration=0,tcp://127.0.0.1:61617?wireFormat.maxInactivityDuration=0)",
//						clientid);

        ActiveMqManager activeMq = ActiveMqManagerFactory
                .createActiveMqManager(
                        "tcp://127.0.0.1:61616?wireFormat.maxInactivityDuration=0"
                );

        ActiveMqDomainEventManager manager = new ActiveMqDomainEventManager(
                activeMq,"");

        Set<Class<?>> ar = new HashSet<>();
        ar.add(DemoDomainEvent.class);
        ar.add(Demo2DomainEvent.class);

        manager.registerDomainEvent(ar);


        DemoSubscriber dsub = new DemoSubscriber();
        Demo2Subscriber dsub2 = new Demo2Subscriber();
        manager.registerSubscriber(dsub,"dssub");
        manager.registerSubscriber(dsub2,"dssub2");

        return manager;

    }

    @Test
    public void publishEventTest() throws Exception {
        ActiveMqDomainEventManager manager = this.create();
        StopWatch stopWatch =new StopWatch();
        stopWatch.start();
        for (int i = 0; i < 10000; i++) {

            manager.publishEvent(new DemoDomainEvent("aaaaa"));
        }
        stopWatch.stop();

        System.out.println("total time =" + stopWatch.getTime());


        Thread.sleep(2000000);
    }

    @AfterClass
    public static void clear() {
        ActiveMqManagerFactory.clear();
    }
}
