package eu.eventstorm.core.apt.analyser;

import java.util.function.Function;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;

import eu.eventstorm.core.apt.model.RestControllerDescriptor;
import eu.eventstorm.sql.apt.log.Logger;
import eu.eventstorm.sql.apt.log.LoggerFactory;
import eu.eventstorm.annotation.CqrsCommand;


/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class CqrsRestControllerAnalyser implements Function<Element, RestControllerDescriptor> {

    private final Logger logger;

    public CqrsRestControllerAnalyser() {
    	this.logger = LoggerFactory.getInstance().getLogger(CqrsRestControllerAnalyser.class);
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
}