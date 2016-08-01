package easy.domain.activemqdomainevent;

import easy.domain.event.ISubscriber;

public interface IActiveMqDomainEventSubscriber extends ISubscriber {
	void handleEvent(String aDomainEvent);
}
