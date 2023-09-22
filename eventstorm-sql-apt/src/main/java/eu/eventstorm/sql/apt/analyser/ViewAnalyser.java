package eu.eventstorm.sql.apt.analyser;

import eu.eventstorm.sql.annotation.ViewColumn;
import eu.eventstorm.sql.apt.log.Logger;
import eu.eventstorm.sql.apt.model.ViewDescriptor;
import eu.eventstorm.sql.apt.model.ViewPropertyDescriptor;

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
public final class ViewAnalyser implements Function<Element, ViewDescriptor>, AutoCloseable {

    private final Logger logger;

    public ViewAnalyser(ProcessingEnvironment processingEnv) {
        this.logger = Logger.getLogger(processingEnv, "eu.eventstorm.sql.analyser", "ViewAnalyser");
    }

    @Override
    public ViewDescriptor apply(Element element) {

        try {
            return doApply(element);
        } catch (AnalyserException cause) {
            this.logger.error(cause.getMessage(), cause);
            throw cause;
        } catch (Exception cause) {
            this.logger.error(cause.getMessage(), cause);
            throw new AnalyserException(cause);
        }

    }

    public ViewDescriptor doApply(Element element) {

        if (ElementKind.INTERFACE != element.getKind()) {
        	logger.error("element [" + element + "] should be an interface");
            return null;
        }

        logger.info("Analyse " + element);

        List<ViewPropertyDescriptor> ppds = new ArrayList<>();

        for (Element method : element.getEnclosedElements()) {

            if (ElementKind.METHOD != method.getKind()) {
            	logger.error( "element [" + method + "] in [" + element + "] is not a method, it's [" + element.getKind() + "]");
                return null;
            }

            ExecutableElement executableElement = (ExecutableElement) method;

            if (executableElement.getSimpleName().toString().startsWith("get")) {
             
                ViewColumn column = executableElement.getAnnotation(ViewColumn.class);
                if (column == null) {
                	logger.error( "getter [" + method + "] in [" + element + "] should have @ViewColumn.");
                } else {
                    ppds.add(new ViewPropertyDescriptor(executableElement));
                }
                continue;
            }

            logger.error("method [" + method + "] in [" + element + "] should start with [get] prefix");
        }

        return new ViewDescriptor(element, ppds);
    }

    @Override
    public void close() {
        logger.close();
    }
}