package eu.eventsotrm.core.apt.model;

import javax.lang.model.element.Element;

import eu.eventstorm.core.annotation.CqrsCommand;
import eu.eventstorm.core.annotation.CqrsCommandRestController;

public class RestControllerDescriptor {

	private final Element element;
	private final CqrsCommand command;
	private final CqrsCommandRestController restController;

	public RestControllerDescriptor(Element element) {
		this.element = element;
		this.command = element.getAnnotation(CqrsCommand.class);
		this.restController = element.getAnnotation(CqrsCommandRestController.class);
	}

	public Element element() {
		return element;
	}

	public CqrsCommand getCommand() {
		return command;
	}

	public CqrsCommandRestController getRestController() {
		return restController;
	}

	public String getName() {
		return restController.javaPackage() + '.' + restController.name();
	}

}
