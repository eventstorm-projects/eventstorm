package eu.eventstorm.core.apt.analyser;

import eu.eventstorm.core.apt.model.CommandDescriptor;
import eu.eventstorm.core.apt.model.PropertyDescriptor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import java.util.List;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class CqrsCommandAnalyser extends AbstractCommandAnalyser<CommandDescriptor> {

    public CqrsCommandAnalyser(ProcessingEnvironment processingEnv) {
        super(processingEnv);
    }

    @Override
    protected CommandDescriptor newInstance(Element element, List<PropertyDescriptor> properties) {
        return new CommandDescriptor(element, properties);
    }

}