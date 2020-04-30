package eu.eventstorm.eventstore.ex;

// Created + UserCommand + Event
public final class UserCreatedEventPayloadImpl { // implements UserCreatedEventPayload {

	private final String name;
	private final String email;
	private final int age;

	public UserCreatedEventPayloadImpl(String name, String email, int age) {
		this.name = name;
		this.email = email;
		this.age = age;
	}

	//@Override
	public String getName() {
		return this.name;
	}

	//@Override
	public int getAge() {
		return this.age;
	}

	//@Override
	public String getEmail() {
		return this.email;
	}

}
