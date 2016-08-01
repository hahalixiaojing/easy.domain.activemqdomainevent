package easy.domain.activemqdomainevent;

import easy.domain.event.IDomainEvent;

public class DemoDomainEvent implements IDomainEvent {

	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
