package eu.eventstorm.core.apt.analyser;

import com.google.common.collect.ImmutableList;
import eu.eventstorm.annotation.CqrsQueryClientService;
import eu.eventstorm.annotation.CqrsQueryClientServiceMethod;
import eu.eventstorm.annotation.Header;
import eu.eventstorm.core.apt.model.QueryClientServiceDescriptor;
import eu.eventstorm.core.apt.model.QueryClientServiceMethodDescriptor;
import eu.eventstorm.sql.apt.log.Logger;
import eu.eventstorm.sql.apt.log.LoggerFactory;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import java.util.function.Function;


/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class CqrsQueryClientServiceAnalyser implements Function<Element, QueryClientServiceDescriptor> {

    private static final Logger LOGGER = LoggerFactory.getInstance().getLogger(CqrsQueryClientServiceAnalyser.class);

    @Override
    public QueryClientServiceDescriptor apply(Element element) {

        LOGGER.info("analyse CqrsQueryClientServiceAnalyser");

        try {
            return doApply(element);
        } catch (Exception cause) {
            LOGGER.error(cause.getMessage(), cause);
            return null;
        }

    }

    public QueryClientServiceDescriptor doApply(Element element) {

        LOGGER.info("Analyse " + element);

        if (ElementKind.INTERFACE != element.getKind()) {
            LOGGER.error("element [" + element + "] should be an interface");
            return null;
        }

        ImmutableList.Builder<QueryClientServiceMethodDescriptor> builder = ImmutableList.builder();

        for (Element method : element.getEnclosedElements()) {

            if (ElementKind.FIELD == method.getKind()) {
                continue;
            }

            if (ElementKind.METHOD != method.getKind()) {
                LOGGER.error("element [" + method + "]-[" + method.getKind() + "] in [" + element +
                        "] is not a method, it's [" + element.getKind() + "]");
                continue;
            }

            ExecutableElement executableElement = (ExecutableElement) method;

            CqrsQueryClientServiceMethod anno = executableElement.getAnnotation(CqrsQueryClientServiceMethod.class);

            if (anno == null) {
                LOGGER.error("method " + method + " has no @CqrsQueryClientServiceMethod");
                continue;
            }

            LOGGER.info("found [" + executableElement + "]");

            ImmutableList.Builder<QueryClientServiceMethodDescriptor.Parameter> parameters = ImmutableList.builder();
            ImmutableList.Builder<QueryClientServiceMethodDescriptor.HttpHeader> headers = ImmutableList.builder();


            executableElement.getParameters().forEach(t -> {
                String name = t.getSimpleName().toString();
                String type = t.asType().toString();
                if (t.getAnnotation(Header.class) != null) {
                    headers.add(new QueryClientServiceMethodDescriptor.HttpHeader(name, type, t.getAnnotation(Header.class)));
                    parameters.add(new QueryClientServiceMethodDescriptor.HttpHeader(name, type, t.getAnnotation(Header.class)));
                } else {
                    parameters.add(new QueryClientServiceMethodDescriptor.Parameter(name, type));
                }
            });

            builder.add(new QueryClientServiceMethodDescriptor(executableElement, anno, parameters.build(), headers.build()));

        }

        return new QueryClientServiceDescriptor(element, element.getAnnotation(CqrsQueryClientService.class), builder.build());
    }

}
