package eu.eventstorm.core.apt.model;

import eu.eventstorm.annotation.SagaCommand;
import eu.eventstorm.annotation.SagaRestController;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

public final class SagaControllerDescriptor {

    private final Element element;
    private final SagaCommand command;

    public SagaControllerDescriptor(SagaCommandDescriptor descriptor) {
        this.element = descriptor.element();
        this.command = descriptor.element().getAnnotation(SagaCommand.class);
    }

    public Element element() {
        return element;
    }

    public SagaCommand getCommand() {
        return command;
    }

    public SagaRestController getRestController() {
        return command.controller();
    }

    public String getPackage(ProcessingEnvironment env) {
        String javaPackage = env.getElementUtils().getPackageOf(element).toString();
        if (javaPackage.startsWith("package")) {
            // with eclipse compiler
            javaPackage = javaPackage.substring(7).trim();
        }
        javaPackage += ".rest";

        return javaPackage;
    }

    public String getFCQN(ProcessingEnvironment env) {
        return getPackage(env) + '.' + command.controller().name();
    }

}
