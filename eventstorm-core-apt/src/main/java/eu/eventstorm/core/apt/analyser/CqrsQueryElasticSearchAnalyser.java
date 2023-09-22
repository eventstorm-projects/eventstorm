package eu.eventstorm.core.apt.analyser;

import eu.eventstorm.core.apt.model.ElsQueryDescriptor;
import eu.eventstorm.core.apt.model.QueryPropertyDescriptor;
import eu.eventstorm.sql.apt.log.Logger;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class CqrsQueryElasticSearchAnalyser implements Function<Element, ElsQueryDescriptor>, AutoCloseable {

    private final Logger logger;

    public CqrsQueryElasticSearchAnalyser(ProcessingEnvironment processingEnv) {
        this.logger = Logger.getLogger(processingEnv, "eu.eventstorm.event.analyser", "CqrsQueryElasticSearchAnalyser");
    }

    @Override
    public ElsQueryDescriptor apply(Element element) {

        try {
            return doApply(element);
        } catch (Exception cause) {
            this.logger.error(cause.getMessage(), cause);
            //  throw new AnalyserException(cause);
            return null;
        }

    }

    public ElsQueryDescriptor doApply(Element element) {

        if (ElementKind.INTERFACE != element.getKind()) {
            logger.error("element [" + element + "] should be an interface");
            return null;
        }

        logger.info("Analyse " + element);

        List<QueryPropertyDescriptor> properties = new ArrayList<>();

        for (Element method : element.getEnclosedElements()) {

            if (ElementKind.METHOD != method.getKind()) {
                logger.error("element [" + method + "] in [" + element + "] is not a method, it's [" + element.getKind() + "]");
                return null;
            }

            ExecutableElement executableElement = (ExecutableElement) method;

            if (executableElement.getSimpleName().toString().startsWith("get")) {
                properties.add(new QueryPropertyDescriptor(executableElement));
                continue;
            }

            throw new IllegalStateException("method [" + method + "] does'nt start with 'get'");
        }

        return new ElsQueryDescriptor(element, properties);
    }

    @Override
    public void close() {
        logger.close();
    }

}