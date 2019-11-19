package eu.eventsotrm.core.apt.model;

import static eu.eventsotrm.sql.apt.Helper.isPrimitiveType;
import static eu.eventsotrm.sql.apt.Helper.propertyName;

import javax.lang.model.element.ExecutableElement;

import eu.eventstorm.util.ToStringBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class EventPropertyDescriptor {

    private static int COUNTER = 0;

    private final String name;
    private final String variable;
    private final ExecutableElement getter;

    public EventPropertyDescriptor(ExecutableElement getter) {
        this.name = propertyName(getter.getSimpleName().subSequence(3, getter.getSimpleName().length()).toString());
        this.getter = getter;
        if (isPrimitiveType(this.name)) {
            this.variable = new StringBuilder().append('_').append(this.name).append(++COUNTER).toString();
        } else {
            this.variable = name;
        }
    }

    public String name() {
        return this.name;
    }

    public ExecutableElement getter() {
        return this.getter;
    }

    public String variable() {
       return this.variable;
    }

	@Override
	public String toString() {
		return new ToStringBuilder(true)
				.append("name", name)
				.append("variable", variable)
				.append("getter", getter)
				.toString();
	}
    
    
}
