package eu.eventstorm.core.apt.analyser;

import eu.eventstorm.annotation.CqrsCommand;
import eu.eventstorm.core.apt.model.RestControllerDescriptor;
import eu.eventstorm.sql.apt.log.Logger;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import java.util.function.Function;


/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class CqrsRestControllerAnalyser implements Function<Element, RestControllerDescriptor>, AutoCloseable {

    private final Logger logger;

    public CqrsRestControllerAnalyser(ProcessingEnvironment processingEnv) {
        this.logger = Logger.getLogger(processingEnv, "eu.eventstorm.event.analyser", "CqrsRestControllerAnalyser");
    }

    @Override
    public RestControllerDescriptor apply(Element element) {

        try {
            return doApply(element);
        } catch (Exception cause) {
            this.logger.error(cause.getMessage(), cause);
            //  throw new AnalyserException(cause);
            return null;
        }

    }

    public RestControllerDescriptor doApply(Element element) {

        if (ElementKind.INTERFACE != element.getKind()) {
            logger.error("element [" + element + "] should be an interface");
            return null;
        }

        CqrsCommand command = element.getAnnotation(CqrsCommand.class);

        if (command == null) {
            logger.error("element [" + element + "] should be annotated by @CqrsCommand");
            return null;
        }

        logger.info("Analyse " + element);

        return new RestControllerDescriptor(element);

    }

    @Override
    public void close() {
        logger.close();
    }
}