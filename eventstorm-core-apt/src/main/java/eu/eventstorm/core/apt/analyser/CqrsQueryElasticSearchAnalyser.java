package eu.eventstorm.core.apt.analyser;

import eu.eventstorm.annotation.els.Id;
import eu.eventstorm.core.apt.model.ElsQueryDescriptor;
import eu.eventstorm.core.apt.model.QueryPropertyDescriptor;
import eu.eventstorm.sql.annotation.PrimaryKey;
import eu.eventstorm.sql.apt.log.Logger;
import eu.eventstorm.sql.apt.model.PojoPropertyDescriptor;

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

        QueryPropertyDescriptor queryId = null;

        for (Element method : element.getEnclosedElements()) {

            if (ElementKind.METHOD != method.getKind()) {
                logger.error("element [" + method + "] in [" + element + "] is not a method, it's [" + element.getKind() + "]");
                return null;
            }

            ExecutableElement executableElement = (ExecutableElement) method;

            if (executableElement.getSimpleName().toString().startsWith("get")) {

                Id id = executableElement.getAnnotation(Id.class);
                if (id != null) {
                    queryId = new QueryPropertyDescriptor(executableElement);
                    properties.add(queryId);
                    continue;
                }

                properties.add(new QueryPropertyDescriptor(executableElement));
                continue;
            }


            throw new IllegalStateException("method [" + method + "] doesn't start with 'get'");
        }

        if (queryId == null) {
            throw new IllegalStateException("no @Id for the ELS Query + [" + element + "]");

        }

        return new ElsQueryDescriptor(element, queryId, properties);
    }

    @Override
    public void close() {
        logger.close();
    }

}