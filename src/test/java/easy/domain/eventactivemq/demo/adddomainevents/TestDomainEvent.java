package easy.domain.eventactivemq.demo.adddomainevents;

import easy.domain.event.IDomainEvent;

public class TestDomainEvent implements IDomainEvent {
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
