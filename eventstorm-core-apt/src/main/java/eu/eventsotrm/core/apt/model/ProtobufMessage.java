package eu.eventsotrm.core.apt.model;

public final class ProtobufMessage {

	private final Protobuf protobuf;
	private final String name;
	
	public ProtobufMessage(Protobuf protobuf, String name) {
		this.protobuf = protobuf;
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public Protobuf getProtobuf() {
		return this.protobuf;
	}
}
