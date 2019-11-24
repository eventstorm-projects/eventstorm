package eu.eventsotrm.core.apt.model;

import javax.lang.model.element.Element;

import eu.eventstorm.core.annotation.CqrsCommand;
import eu.eventstorm.core.annotation.CqrsRestController;

public class RestControllerDescriptor {

	private final Element element;
	private final CqrsCommand command;
	private final CqrsRestController restController;

	public RestControllerDescriptor(Element element) {
		this.element = element;
		this.command = element.getAnnotation(CqrsCommand.class);
		this.restController = element.getAnnotation(CqrsRestController.class);
	}

	public Element element() {
		return element;
	}

	public CqrsCommand getCommand() {
		return command;
	}

	public CqrsRestController getRestController() {
		return restController;
	}

	public String getName() {
		return restController.javaPackage() + '.' + restController.name();
	}

}
