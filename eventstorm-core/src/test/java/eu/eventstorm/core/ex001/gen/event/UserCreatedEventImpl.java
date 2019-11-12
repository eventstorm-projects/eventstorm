package eu.eventstorm.core.ex001.gen.event;


import eu.eventstorm.core.AggregateId;
import eu.eventstorm.core.DomainModel;
import eu.eventstorm.core.ex001.event.UserCreatedEvent;
import eu.eventstorm.core.ex001.gen.domain.UserDomainHandler;

// Created + UserCommand + Event
public final class UserCreatedEventImpl implements UserCreatedEvent {

	@Override
	public AggregateId getAggregateId() {
		return null;
	}

	@Override
	public int getVersion() {
		return 1;
	}

	@Override
	public String getEventType() {
		return "UserCreatedEvent";
	}

	public void applyOn(UserDomainHandler domainHandler) {
		domainHandler.apply(this);
	}

//	@Override
//	public void applyOn(UserDomain domain) {
//		domain.apply(this);
//	}

	/*public UserCreatedEvent(String id, OffsetDateTime timestamp, String type, String contentType) {
		super(id, timestamp, type, contentType);
	}
*/
    

}
