package eu.eventstorm.core.apt.model;

import com.google.common.collect.ImmutableList;

public final class Protobuf {

	private final String proto;
	private ImmutableList<ProtobufMessage> messages;
	private String javaPackage;
	
	public Protobuf(String proto) {
		this.proto = proto;
	}

	public ImmutableList<ProtobufMessage> getMessages() {
		return messages;
	}
	
	public void setMessages(ImmutableList<ProtobufMessage> messages) {
		this.messages = messages;
	}

	public String getJavaPackage() {
		return this.javaPackage;
	}

	public void setJavaPackage(String javaPackage) {
		this.javaPackage = javaPackage;
	}

	public String getProto() {
		return this.proto;
	}
}
