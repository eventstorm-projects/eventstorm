package eu.eventstorm.core.apt.analyser;

import eu.eventstorm.core.apt.model.PropertyDescriptor;
import eu.eventstorm.core.apt.model.SagaCommandDescriptor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import java.util.List;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class SagaCommandAnalyser extends AbstractCommandAnalyser<SagaCommandDescriptor> {

    public SagaCommandAnalyser(ProcessingEnvironment processingEnv) {
        super(processingEnv);
    }

    @Override
    protected SagaCommandDescriptor newInstance(Element element, List<PropertyDescriptor> properties) {
        return new SagaCommandDescriptor(element, properties);
    }

}