package easy.domain.activemqdomainevent;


import com.alibaba.fastjson.JSON;

import easy.domain.event.IDomainEvent;

public class DemoSubscriber implements IActiveMqDomainEventSubscriber{

	@Override
	public <T extends IDomainEvent> Class<?> suscribedToEventType() {
		return DemoDomainEvent.class;
	}

	@Override
	public void handleEvent(String aDomainEvent) {

		 DemoDomainEvent evt = JSON.parseObject(aDomainEvent, DemoDomainEvent.class);
		 
		 System.out.println(evt.getName());
	}

}
