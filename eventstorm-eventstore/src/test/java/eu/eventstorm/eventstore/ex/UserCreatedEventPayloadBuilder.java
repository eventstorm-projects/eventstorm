package eu.eventstorm.eventstore.ex;

public final class UserCreatedEventPayloadBuilder {

	private String name;
	private String email;
	private int age;

	public UserCreatedEventPayloadBuilder name(String name) {
		this.name = name;
		return this;
	}

	public UserCreatedEventPayloadBuilder email(String email) {
		this.email = email;
		return this;
	}

	public UserCreatedEventPayloadBuilder age(int age) {
		this.age = age;
		return this;
	}

	UserCreatedEventPayload build() {
		return new UserCreatedEventPayloadImpl(name, email, age);
	}

}
