package easy.domain.activemqdomainevent;


import easy.domain.application.subscriber.ISubscriber;

public interface IActiveMqDomainEventSubscriber extends ISubscriber {
	void handleEvent(String aDomainEvent) throws Exception;
}
