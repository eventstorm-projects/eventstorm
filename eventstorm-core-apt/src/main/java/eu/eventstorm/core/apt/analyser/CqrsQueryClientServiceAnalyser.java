package eu.eventstorm.core.apt.analyser;

import com.google.common.collect.ImmutableList;
import eu.eventstorm.annotation.CqrsQueryClientService;
import eu.eventstorm.annotation.CqrsQueryClientServiceMethod;
import eu.eventstorm.annotation.Header;
import eu.eventstorm.annotation.Headers;
import eu.eventstorm.core.apt.model.QueryClientServiceDescriptor;
import eu.eventstorm.core.apt.model.QueryClientServiceMethodDescriptor;
import eu.eventstorm.sql.apt.log.Logger;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import java.util.function.Function;


/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class CqrsQueryClientServiceAnalyser implements Function<Element, QueryClientServiceDescriptor>, AutoCloseable {

    private final Logger logger;

    public CqrsQueryClientServiceAnalyser(ProcessingEnvironment processingEnv) {
        this.logger = Logger.getLogger(processingEnv, "eu.eventstorm.event.analyser", "CqrsQueryClientServiceAnalyser");
    }

    @Override
    public QueryClientServiceDescriptor apply(Element element) {

        logger.info("analyse CqrsQueryClientServiceAnalyser");

        try {
            return doApply(element);
        } catch (Exception cause) {
            logger.error(cause.getMessage(), cause);
            return null;
        }

    }

    public QueryClientServiceDescriptor doApply(Element element) {

        logger.info("Analyse " + element);

        if (ElementKind.INTERFACE != element.getKind()) {
            logger.error("element [" + element + "] should be an interface");
            return null;
        }

        ImmutableList.Builder<QueryClientServiceMethodDescriptor> builder = ImmutableList.builder();

        for (Element method : element.getEnclosedElements()) {

            if (ElementKind.FIELD == method.getKind()) {
                continue;
            }

            if (ElementKind.METHOD != method.getKind()) {
                logger.error("element [" + method + "]-[" + method.getKind() + "] in [" + element +
                        "] is not a method, it's [" + element.getKind() + "]");
                continue;
            }

            ExecutableElement executableElement = (ExecutableElement) method;

            CqrsQueryClientServiceMethod anno = executableElement.getAnnotation(CqrsQueryClientServiceMethod.class);

            if (anno == null) {
                logger.error("method " + method + " has no @CqrsQueryClientServiceMethod");
                continue;
            }

            logger.info("found [" + executableElement + "]");

            ImmutableList.Builder<QueryClientServiceMethodDescriptor.Parameter> parameters = ImmutableList.builder();
            ImmutableList.Builder<QueryClientServiceMethodDescriptor.HttpHeader> headers = ImmutableList.builder();
            ImmutableList.Builder<QueryClientServiceMethodDescriptor.HttpHeaderConsumer> headersConsumers = ImmutableList.builder();


            executableElement.getParameters().forEach(t -> {
                String name = t.getSimpleName().toString();
                String type = t.asType().toString();
                if (t.getAnnotation(Header.class) != null) {
                    headers.add(new QueryClientServiceMethodDescriptor.HttpHeader(name, type, t.getAnnotation(Header.class)));
                    parameters.add(new QueryClientServiceMethodDescriptor.HttpHeader(name, type, t.getAnnotation(Header.class)));
                } else if (t.getAnnotation(Headers.class) != null) {
                    headersConsumers.add(new QueryClientServiceMethodDescriptor.HttpHeaderConsumer(name, type, t.getAnnotation(Headers.class)));
                    parameters.add(new QueryClientServiceMethodDescriptor.HttpHeaderConsumer(name, type, t.getAnnotation(Headers.class)));
                } else {
                    parameters.add(new QueryClientServiceMethodDescriptor.Parameter(name, type));
                }
            });

            builder.add(new QueryClientServiceMethodDescriptor(executableElement, anno, parameters.build(), headers.build(), headersConsumers.build()));

        }

        return new QueryClientServiceDescriptor(element, element.getAnnotation(CqrsQueryClientService.class), builder.build());
    }

    @Override
    public void close(){
        logger.close();
    }

}
