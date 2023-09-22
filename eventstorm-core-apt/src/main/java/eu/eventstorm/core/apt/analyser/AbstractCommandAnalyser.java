package eu.eventstorm.core.apt.analyser;

import eu.eventstorm.core.apt.model.AbstractCommandDescriptor;
import eu.eventstorm.core.apt.model.PropertyDescriptor;
import eu.eventstorm.sql.apt.log.Logger;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
abstract class AbstractCommandAnalyser<T extends AbstractCommandDescriptor> implements Function<Element, T>, AutoCloseable {

    private final Logger logger;

    public AbstractCommandAnalyser(ProcessingEnvironment processingEnv) {
        this.logger = Logger.getLogger(processingEnv, "eu.eventstorm.event.analyser", getClass().getSimpleName());
    }

    @Override
    public T apply(Element element) {
        try {
            return doApply(element);
        } catch (Exception cause) {
            this.logger.error(cause.getMessage(), cause);
            // throw new AnalyserException(cause);
            return null;
        }
    }

    public T doApply(Element element) {

        if (ElementKind.INTERFACE != element.getKind()) {
            logger.error("element [" + element + "] should be an interface");
            return null;
        }

        logger.info("Analyse " + element);

        TypeElement parent = (TypeElement) element;
        List<PropertyDescriptor> properties = new ArrayList<>();
        read(parent, properties);

        return newInstance(element, properties);
    }

    private void read(TypeElement element, List<PropertyDescriptor> properties) {
        for (Element method : element.getEnclosedElements()) {

            if (ElementKind.METHOD != method.getKind()) {
                logger.error("element [" + method + "] in [" + element + "] is not a method, it's [" + element.getKind() + "]");
                return;
            }

            ExecutableElement executableElement = (ExecutableElement) method;

            if (executableElement.getSimpleName().toString().startsWith("get")) {
                properties.add(new PropertyDescriptor(executableElement));
                continue;
            }

            throw new IllegalStateException("method [" + method + "] doesn't start with 'get'");
        }

        element.getInterfaces().forEach(typeMirror -> {
            if (!"eu.eventstorm.cqrs.Command".equals(typeMirror.toString())) {
                // skip
                read((TypeElement) ((DeclaredType) typeMirror).asElement(), properties);
            }
        });
    }

    protected abstract T newInstance(Element element, List<PropertyDescriptor> properties);

    @Override
    public void close() {
        logger.close();
    }

}