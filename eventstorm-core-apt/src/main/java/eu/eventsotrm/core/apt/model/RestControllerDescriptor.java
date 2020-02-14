package eu.eventsotrm.core.apt.model;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

import eu.eventstorm.annotation.CqrsCommand;
import eu.eventstorm.annotation.CqrsCommandRestController;

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

	public String getPackage(ProcessingEnvironment env) {
	    String javaPackage = restController.javaPackage();
        
        if ("".equals(javaPackage)) {
            javaPackage = env.getElementUtils().getPackageOf(element).toString();
            if (javaPackage.startsWith("package")) {
                // with eclipse compiler
                javaPackage = javaPackage.substring(7).trim();
            }
            javaPackage += ".rest";
        }
        return javaPackage;
	}
	
	public String getFCQN(ProcessingEnvironment env) {
	   return getPackage(env) + '.' + restController.name();
	}

}
