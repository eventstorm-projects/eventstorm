package eu.eventsotrm.core.apt.analyser;

import java.util.function.Function;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;

import eu.eventsotrm.core.apt.model.EventEvolutionDescriptor;
import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;


/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class EventEvolutionAnalyser implements Function<Element, EventEvolutionDescriptor> {

    private final Logger logger;

    public EventEvolutionAnalyser() {
    	this.logger = LoggerFactory.getInstance().getLogger(EventEvolutionAnalyser.class);
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
    
}