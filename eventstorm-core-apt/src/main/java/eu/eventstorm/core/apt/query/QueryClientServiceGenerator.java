package eu.eventstorm.core.apt.query;

import eu.eventstorm.annotation.CqrsQueryClientService;
import eu.eventstorm.annotation.Headers;
import eu.eventstorm.core.apt.SourceCode;
import eu.eventstorm.core.apt.model.QueryClientServiceDescriptor;
import eu.eventstorm.core.apt.model.QueryClientServiceMethodDescriptor;
import eu.eventstorm.sql.apt.log.Logger;
import eu.eventstorm.util.Strings;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static eu.eventstorm.sql.apt.Helper.writeGenerated;
import static eu.eventstorm.sql.apt.Helper.writeNewLine;
import static eu.eventstorm.sql.apt.Helper.writePackage;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class QueryClientServiceGenerator {

    private static Logger logger;

    public void generateClient(ProcessingEnvironment processingEnv, SourceCode sourceCode) {

        try (Logger l = Logger.getLogger(processingEnv, "eu.eventstorm.event.query", "QueryClientServiceGenerator")) {
            logger = l;
            sourceCode.forEachQueryClientService(t -> {
                try {
                    generate(processingEnv, t);
                } catch (Exception cause) {
                    logger.error("Exception for [" + t + "] -> [" + cause.getMessage() + "]", cause);
                }
            });
        }

    }

    private void generate(ProcessingEnvironment env, QueryClientServiceDescriptor descriptor) throws IOException {

        // check due to "org.aspectj.org.eclipse.jdt.internal.compiler.apt.dispatch.BatchFilerImpl.createSourceFile(BatchFilerImpl.java:149)"
        if (env.getElementUtils().getTypeElement(descriptor.fullyQualifiedClassName() + "Impl") != null) {
            logger.info("Java SourceCode already exist [" + descriptor.fullyQualifiedClassName() + "Impl]");
            return;
        }

        JavaFileObject object = env.getFiler().createSourceFile(descriptor.fullyQualifiedClassName() + "Impl");
        try (Writer writer = object.openWriter()) {
            writeHeader(writer, env, descriptor);
            writeVariables(writer, descriptor);
            writeConstructor(writer, descriptor);
            writeMethods(writer, descriptor);
            writer.write("}");
        }

    }


    private static void writeHeader(Writer writer, ProcessingEnvironment env, QueryClientServiceDescriptor descriptor) throws IOException {

        writePackage(writer, env.getElementUtils().getPackageOf(descriptor.element()).toString());

        writeNewLine(writer);
        writer.write("import org.slf4j.Logger;");
        writeNewLine(writer);
        writer.write("import org.slf4j.LoggerFactory;");
        writeNewLine(writer);
        writer.write("import org.springframework.web.reactive.function.client.WebClient;");
        writeNewLine(writer);

        if (hasCache(descriptor)) {
            writer.write("import java.util.concurrent.CompletableFuture;");
            writeNewLine(writer);
            writer.write("import com.github.benmanes.caffeine.cache.LoadingCache;");
            writeNewLine(writer);
            writer.write("import com.github.benmanes.caffeine.cache.Caffeine;");
            writeNewLine(writer);
            writer.write("import eu.eventstorm.cqrs.QueryServiceClientCacheFactory;");
            writeNewLine(writer);
            writer.write("import org.springframework.beans.factory.annotation.Qualifier;");
            writeNewLine(writer);
        }

        writeGenerated(writer, QueryClientServiceGenerator.class.getName());


        /*
        writer.write("@org.springframework.stereotype.Component");
        writeNewLine(writer);
        if (!Strings.isEmpty(descriptor.getAnnotation().profile())) {
            writer.write("@org.springframework.context.annotation.Profile(\"" + descriptor.getAnnotation().profile() + "\")");
            writeNewLine(writer);
        }
         */

        writer.write("public final class ");
        writer.write(descriptor.element().getSimpleName() + "Impl implements " + descriptor.element().getSimpleName());
        writer.write(" {");
        writeNewLine(writer);

    }


    private static void writeConstructor(Writer writer, QueryClientServiceDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writer.write("    public ");
        writer.write(descriptor.element().getSimpleName() + "Impl(WebClient webClient");

        StringBuilder builder = new StringBuilder();
        descriptor.getMethods().forEach(m -> {
            if (Strings.isEmpty(m.getAnnotation().cacheFactoryBean())) {
                return;
            }
            builder.append("@Qualifier(\"" + m.getAnnotation().cacheFactoryBean() + "\") QueryServiceClientCacheFactory<String, " + m.getMethod().getReturnType().toString() + "> ").append(m.getAnnotation().cacheFactoryBean()).append(',');
        });
        if (builder.length() > 1) {
            writer.write(", ");
            builder.deleteCharAt(builder.length() - 1);
            writer.write(builder.toString());
        }

        writer.write(") {");
        writeNewLine(writer);
        writer.write("        this.webClient = webClient;");
        writeNewLine(writer);
        descriptor.getMethods().forEach(m -> {
            if (Strings.isEmpty(m.getAnnotation().cacheFactoryBean())) {
                return;
            }
            try {
                writer.write("        this." + m.getMethod().getSimpleName() + " = " + m.getAnnotation().cacheFactoryBean() + ".newInstance(key -> ");
                writeCacheCall(writer, descriptor, m);
                writer.write("        );");
                writeNewLine(writer);
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        writer.write("    }");
        writeNewLine(writer);
    }

    private static void writeCacheCall(Writer writer, QueryClientServiceDescriptor ed, QueryClientServiceMethodDescriptor epd) throws IOException {
        writer.write("webClient.get()");
        writeNewLine(writer);
        writer.write("                .uri(\"" + ed.element().getAnnotation(CqrsQueryClientService.class).uri() + "/" + epd.getAnnotation().path() + "\", key");
        writer.write(")");
        for (QueryClientServiceMethodDescriptor.HttpHeader h : epd.getHeaders()) {
            writeNewLine(writer);
            writer.write("                .header(\"" + h.getHeader().name() + "\", " + h.getName() + ")");
        }
        writeNewLine(writer);
        writer.write("                .retrieve()");
        writeNewLine(writer);

        if (epd.getMethod().toString().contains("Flux<")) {
            writer.write("              .bodyToFlux(");
        } else {

            String type = getMonoType(epd);

            writer.write("                .bodyToMono(" + type + ".class)");
            writeNewLine(writer);
            writer.write("                .cache()");
            writeNewLine(writer);
            //writer.write("                .toFuture()");
            //writeNewLine(writer);
            //writer.write("                .get()");
            //writeNewLine(writer);
        }
    }

    private static String getHeaderConsumerVariable(QueryClientServiceMethodDescriptor.HttpHeaderConsumer httpHeaderConsumer) {
        int last = httpHeaderConsumer.getType().lastIndexOf('.');
        return httpHeaderConsumer.getType().substring(last + 1).toUpperCase();
    }

    private static void writeVariables(Writer writer, QueryClientServiceDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writer.write("    private static final Logger LOGGER = LoggerFactory.getLogger(" + descriptor.element().getSimpleName() + "Impl.class);");
        writeNewLine(writer);

        List<String> classes = new ArrayList<>();
        descriptor.getMethods().forEach(m -> {
            m.getHeadersConsumers().forEach(httpHeaderConsumer -> {
                TypeMirror val = getHeaderConsumerClass(httpHeaderConsumer.getHeaders());
                if (classes.contains(val.toString())) {
                    return;
                }
                try {
                    writer.write("    private static final " + val + " " + getHeaderConsumerVariable(httpHeaderConsumer) + " = ");
                    writer.write("new " + val + "();");
                    writeNewLine(writer);
                } catch (IOException e) {
                    logger.error("failed to write HeaderConsumer", e);
                }
                classes.add(val.toString());
            });
        });

        writer.write("    private final WebClient webClient;");
        writeNewLine(writer);

        descriptor.getMethods().forEach(m -> {
            if (Strings.isEmpty(m.getAnnotation().cacheFactoryBean())) {
                return;
            }
            try {
                writer.write("    private final LoadingCache<String, " + m.getMethod().getReturnType().toString() + "> " + m.getMethod().getSimpleName() + ";");
                writeNewLine(writer);

            } catch (IOException cause) {
                logger.error("failed to write", cause);
            }
        });
    }

    private static void writeMethod(Writer writer, QueryClientServiceDescriptor ed, QueryClientServiceMethodDescriptor epd) throws IOException {
        writeNewLine(writer);
        writer.write("    public ");
        writer.write(epd.getMethod().getReturnType() + " ");
        writer.write(epd.getMethod().getSimpleName() + "(");

        StringBuilder builder = new StringBuilder();
        StringBuilder loggerParams = new StringBuilder();
        StringBuilder loggerParamsValue = new StringBuilder();
        epd.getParameters().forEach(parameter -> {
            builder.append(parameter.getType());
            builder.append(" ");
            builder.append(parameter.getName());
            builder.append(",");


            if (parameter instanceof QueryClientServiceMethodDescriptor.HttpHeader) {
                return;
            }

            if (parameter instanceof QueryClientServiceMethodDescriptor.HttpHeaderConsumer) {
                return;
            }

            loggerParams.append("{},");
            loggerParamsValue.append(parameter.getName()).append(',');


        });
        if (epd.getParameters().size() > 0) {
            builder.deleteCharAt(builder.length() - 1);
            if (loggerParams.length() > 0) {
                loggerParams.deleteCharAt(loggerParams.length() - 1);
                loggerParamsValue.deleteCharAt(loggerParamsValue.length() - 1);
            }
        }

        writer.write(builder.toString());
        writer.write(") { ");

        writeNewLine(writer);
        writer.write("        if (LOGGER.isDebugEnabled()) {");
        writeNewLine(writer);
        writer.write("            LOGGER.debug(\"" + epd.getMethod().getSimpleName() + "(" + loggerParams.toString() + ")\"");
        if (epd.getParameters().size() > 0 && loggerParamsValue.length() > 0) {
            writer.write(", " + loggerParamsValue);
        }
        writer.write(");");
        writeNewLine(writer);
        writer.write("        }");
        writeNewLine(writer);

        if (Strings.isEmpty(epd.getAnnotation().cacheFactoryBean())) {
            writer.write("        return this.webClient.get()");
            writeNewLine(writer);


            if (epd.getParameters().stream().anyMatch(p -> p.getType().startsWith(Map.class.getName()))) {
                writer.write("                .uri(uriBuilder -> {");
                writeNewLine(writer);
                String uri = ed.element().getAnnotation(CqrsQueryClientService.class).uri();
                writer.write("                    uriBuilder.scheme(\"" + uri.substring(0, uri.indexOf("://")) + "\");");
                writeNewLine(writer);
                writer.write("                    uriBuilder.host(\"" + uri.substring(uri.indexOf("://") + 3) + "\");");
                writeNewLine(writer);
                writer.write("                    uriBuilder.path(\"" + epd.getAnnotation().path() + "\");");
                writeNewLine(writer);
                writer.write("                    " + epd.getParameters().get(epd.getParameters().size() - 1).getName() + ".forEach((key,value) -> uriBuilder.queryParam(key,value));");
                writeNewLine(writer);
                writer.write("                    return uriBuilder.build();");
                writeNewLine(writer);
                writer.write("                })");
                writeNewLine(writer);
            } else {
                writer.write("                .uri(\"" + ed.element().getAnnotation(CqrsQueryClientService.class).uri() + "/" + epd.getAnnotation().path() + "\"");
                if (epd.getParameters().size() > 0 && loggerParamsValue.length() > 0) {
                    writer.write(", " + loggerParamsValue);
                }
                writer.write(")");
                writeNewLine(writer);
            }
            for (QueryClientServiceMethodDescriptor.HttpHeader h : epd.getHeaders()) {
                writer.write("                .header(\"" + h.getHeader().name() + "\", " + h.getName() + ")");
                writeNewLine(writer);
            }
            for (QueryClientServiceMethodDescriptor.HttpHeaderConsumer h : epd.getHeadersConsumers()) {
                writer.write("                .headers(httpHeaders -> " + getHeaderConsumerVariable(h) + ".accept(httpHeaders, " + h.getName() + "))");
                writeNewLine(writer);
            }
            writer.write("                .retrieve()");
            writeNewLine(writer);

            if (epd.getMethod().getReturnType().toString().contains("org.springframework.http.ResponseEntity")) {
                String type = getMonoType(epd);
                int start = type.indexOf('<');
                int end = type.indexOf('>');
                writer.write("                .toEntity(" + type.substring(start + 1, end) + ".class);");
                writeNewLine(writer);
            } else if (epd.getMethod().getReturnType().toString().contains("reactor.core.publisher.Flux<")) {
                String type = getFluxType(epd);
                writer.write("                .bodyToFlux(" + type + ".class);");
                writeNewLine(writer);
            } else {

                String type = getMonoType(epd);

                writer.write("                .bodyToMono(" + type + ".class);");
                writeNewLine(writer);
            }
        } else {
            writer.write("        return this." + epd.getMethod().getSimpleName().toString() + ".get(" + loggerParamsValue + ");");
            writeNewLine(writer);
        }


        writer.write("    }");
        writeNewLine(writer);
    }

    private static String getFluxType(QueryClientServiceMethodDescriptor epd) {
        String type = epd.getMethod().getReturnType().toString();
        type = type.substring(28);
        type = type.substring(0, type.length() - 1);
        return type;
    }

    private static String getMonoType(QueryClientServiceMethodDescriptor epd) {
        String type = epd.getMethod().getReturnType().toString();
        type = type.substring(28);
        type = type.substring(0, type.length() - 1);
        return type;
    }

    private static void writeMethods(Writer writer, QueryClientServiceDescriptor descriptor) {
        descriptor.getMethods().forEach(method -> {
            try {
                writeMethod(writer, descriptor, method);
            } catch (IOException cause) {
                logger.error("failed to implements method [" + method.getMethod() + "]", cause);
            }
        });
    }

    private static boolean hasCache(QueryClientServiceDescriptor descriptor) {
        return descriptor.getMethods().stream().anyMatch(s -> !Strings.isEmpty(s.getAnnotation().cacheFactoryBean()));
    }

    private static TypeMirror getHeaderConsumerClass(Headers headers) {
        try {
            headers.value(); // this should throw
        } catch (MirroredTypeException mte) {
            return mte.getTypeMirror();
        }
        return null; // can this ever happen ??
    }
}