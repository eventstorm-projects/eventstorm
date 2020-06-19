package eu.eventsotrm.core.apt.analyser;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;

import eu.eventsotrm.core.apt.model.DatabaseQueryDescriptor;
import eu.eventsotrm.core.apt.model.QueryPropertyDescriptor;
import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;


/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class CqrsQueryDatabaseAnalyser implements Function<Element, DatabaseQueryDescriptor> {

    private final Logger logger;

    public CqrsQueryDatabaseAnalyser() {
        this.logger = LoggerFactory.getInstance().getLogger(CqrsQueryDatabaseAnalyser.class);
    }

    @Override
    public DatabaseQueryDescriptor apply(Element element) {

        try {
            return doApply(element);
        } catch (Exception cause) {
            this.logger.error(cause.getMessage(), cause);
          //  throw new AnalyserException(cause);
            return null;
        }

    }

    public DatabaseQueryDescriptor doApply(Element element) {

        if (ElementKind.INTERFACE != element.getKind()) {
            logger.error("element [" + element + "] should be an interface");
            return null;
        }

        logger.info("Analyse " + element);
        
        List<QueryPropertyDescriptor> properties = new ArrayList<>();
        
        for (Element method : element.getEnclosedElements()) {
            
             if (ElementKind.METHOD != method.getKind()) {
                 logger.error( "element [" + method + "] in [" + element + "] is not a method, it's [" + element.getKind() + "]");
                 return null;
             }

             ExecutableElement executableElement = (ExecutableElement) method;
             
             if (executableElement.getSimpleName().toString().startsWith("get")) {
                 properties.add(new QueryPropertyDescriptor(executableElement));
                 continue;
             }
             
             throw new IllegalStateException("method [" + method + "] does'nt start with 'get'");
        }

        return new DatabaseQueryDescriptor(element, properties);
    }
}