package eu.eventstorm.core.apt.analyser;

import eu.eventstorm.core.apt.model.EventEvolutionDescriptor;
import eu.eventstorm.sql.apt.log.Logger;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import java.util.function.Function;


/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class EventEvolutionAnalyser implements Function<Element, EventEvolutionDescriptor>, AutoCloseable {

    private final Logger logger;

    public EventEvolutionAnalyser(ProcessingEnvironment processingEnv) {
        this.logger = Logger.getLogger(processingEnv, "eu.eventstorm.event.analyser", "EventEvolutionAnalyser");
    }

    @Override
    public EventEvolutionDescriptor apply(Element element) {

        try {
            return doApply(element);
        } catch (Exception cause) {
            this.logger.error(cause.getMessage(), cause);
            //  throw new AnalyserException(cause);
            return null;
        }

    }

    public EventEvolutionDescriptor doApply(Element element) {

        if (ElementKind.INTERFACE != element.getKind()) {
            logger.error("element [" + element + "] should be an interface");
            return null;
        }

        logger.info("Analyse " + element);

        return new EventEvolutionDescriptor(element);
    }

    @Override
    public void close() {
        logger.close();
    }
}