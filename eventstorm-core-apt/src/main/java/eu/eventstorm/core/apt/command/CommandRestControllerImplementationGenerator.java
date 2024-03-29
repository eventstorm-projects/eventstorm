package eu.eventstorm.core.apt.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import eu.eventstorm.annotation.HttpMethod;
import eu.eventstorm.cloudevents.CloudEvent;
import eu.eventstorm.cloudevents.CloudEvents;
import eu.eventstorm.core.Event;
import eu.eventstorm.core.apt.SourceCode;
import eu.eventstorm.core.apt.model.RestControllerDescriptor;
import eu.eventstorm.cqrs.CommandGateway;
import eu.eventstorm.cqrs.context.ReactiveCommandContext;
import eu.eventstorm.cqrs.tracer.Span;
import eu.eventstorm.cqrs.tracer.Tracer;
import eu.eventstorm.sql.apt.log.Logger;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;

import static eu.eventstorm.sql.apt.Helper.writeGenerated;
import static eu.eventstorm.sql.apt.Helper.writeNewLine;
import static eu.eventstorm.sql.apt.Helper.writePackage;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class CommandRestControllerImplementationGenerator {

    private Logger logger;

    public CommandRestControllerImplementationGenerator() {
    }

    public void generate(ProcessingEnvironment processingEnvironment, SourceCode sourceCode) {

        try (Logger logger = Logger.getLogger(processingEnvironment, "eu.eventstorm.event.generator", "CommandRestControllerImplementationGenerator")) {
            this.logger = logger;
            sourceCode.forEachRestController((name, desc) -> {
                try {
                    generate(processingEnvironment, name, desc, sourceCode);
                } catch (Exception cause) {
                    logger.error("Exception for [" + name + "] -> [" + cause.getMessage() + "]", cause);
                }
            });
        }

    }

    private void generate(ProcessingEnvironment env, String name, ImmutableList<RestControllerDescriptor> desc, SourceCode sourceCode) throws IOException {

        // check due to "org.aspectj.org.eclipse.jdt.internal.compiler.apt.dispatch.BatchFilerImpl.createSourceFile(BatchFilerImpl.java:149)"
        if (env.getElementUtils().getTypeElement(name) != null) {
            logger.info("Java SourceCode already exist [" + name + "]");
            return;
        }

        JavaFileObject object = env.getFiler().createSourceFile(name);
        try (Writer writer = object.openWriter()) {

            writeHeader(writer, env, desc);
            writeVariables(writer, desc.get(0).getRestController().name());
            writeConstructor(writer, desc, sourceCode);
            writeMethods(writer, desc);
            writer.write("}");
        }

    }

    private void writeHeader(Writer writer, ProcessingEnvironment env, ImmutableList<RestControllerDescriptor> desc) throws IOException {

        String javaPackage = desc.get(0).getPackage(env);

        writePackage(writer, javaPackage);
        writeNewLine(writer);

        writer.write("import static org.springframework.core.io.buffer.DataBufferUtils.join;");
        writeNewLine(writer);

        writer.write("import org.springframework.http.MediaType;");
        writeNewLine(writer);
        writer.write("import org.springframework.web.bind.annotation.RestController;");
        writeNewLine(writer);
        writer.write("import org.springframework.web.server.ServerWebExchange;");
        writeNewLine(writer);

        writer.write("import io.swagger.v3.oas.annotations.media.Content;");
        writeNewLine(writer);
        writer.write("import io.swagger.v3.oas.annotations.media.Schema;");
        writeNewLine(writer);
        writer.write("import io.swagger.v3.oas.annotations.parameters.RequestBody;");
        writeNewLine(writer);


        writer.write("import reactor.core.publisher.Flux;");
        writeNewLine(writer);
        writer.write("import reactor.core.publisher.Mono;");
        writeNewLine(writer);
        writer.write("import reactor.core.publisher.SignalType;");
        writeNewLine(writer);
        writer.write("import reactor.core.scheduler.Scheduler;");
        writeNewLine(writer);

        writer.write("import " + CloudEvent.class.getName() + ";");
        writeNewLine(writer);
        writer.write("import " + CloudEvents.class.getName() + ";");
        writeNewLine(writer);
        writer.write("import " + ReactiveCommandContext.class.getName() + ";");
        writeNewLine(writer);
        writer.write("import " + Tracer.class.getName() + ";");
        writeNewLine(writer);
        writer.write("import " + Span.class.getName() + ";");
        writeNewLine(writer);

        writeGenerated(writer, CommandRestControllerImplementationGenerator.class.getName());
        writer.write("@RestController");
        writeNewLine(writer);
        writer.write("public final class ");
        writer.write(desc.get(0).getRestController().name());
        writer.write(" {");
        writeNewLine(writer);
        writeNewLine(writer);
    }

    private void writeVariables(Writer writer, String name) throws IOException {
        writer.write("    private final ");
        writer.write(CommandGateway.class.getName());
        writer.write(" gateway;");
        writeNewLine(writer);

        writer.write("    private final ");
        writer.write(ObjectMapper.class.getName());
        writer.write(" mapper;");
        writeNewLine(writer);

        writer.write("    private final ");
        writer.write(Tracer.class.getName());
        writer.write(" tracer;");
        writeNewLine(writer);

        writeNewLine(writer);
    }

    private void writeConstructor(Writer writer, ImmutableList<RestControllerDescriptor> desc, SourceCode sourceCode) throws IOException {

        writer.write("    public ");
        writer.write(desc.get(0).getRestController().name());
        writer.write("(");
        writer.write(CommandGateway.class.getName());
        writer.write(" gateway, " + ObjectMapper.class.getName() + " mapper, " + Tracer.class.getName() + " tracer) {");

        writeNewLine(writer);
        writer.write("        this.gateway = gateway;");
        writeNewLine(writer);
        writer.write("        this.mapper = mapper;");
        writeNewLine(writer);
        writer.write("        this.tracer = tracer;");
        writeNewLine(writer);

        writer.write("    }");
        writeNewLine(writer);
        writeNewLine(writer);

    }

    private static void writeMethods(Writer writer, ImmutableList<RestControllerDescriptor> desc) throws IOException {

        for (RestControllerDescriptor rcd : desc) {
            writeSpring(writer, rcd);
            writeMethodRestAsync(writer, rcd);

        }

    }

	/*
	 * private static void writeMethodRestAsync(Writer writer, RestControllerDescriptor rcd) throws IOException {
		writer.write("    public CompletableFuture<Stream<CloudEvent>> on(@RequestBody ");
		writer.write(rcd.element().toString());
		writer.write(" command) {");
		writeNewLine(writer);
		writeNewLine(writer);
		writer.write("        if (LOGGER.isTraceEnabled()) {");
		writeNewLine(writer);
		writer.write("            LOGGER.trace(\"on(");
		writer.write(rcd.element().getSimpleName().toString());
		writer.write(") : [{}]\", command);");
		writeNewLine(writer);
		writer.write("        }");
		writeNewLine(writer);
		writer.write("        return CompletableFuture.supplyAsync(() -> this.gateway.dispatch(command), this.executor)");
		writeNewLine(writer);
		writer.write("                         .thenApplyAsync(CloudEvents::to);");
		writeNewLine(writer);
		writer.write("    }");
		writeNewLine(writer);
		
	}
*/

    //
    private static void writeMethodRestAsync(Writer writer, RestControllerDescriptor rcd) throws IOException {
        String returnType = getReturnTypeClassname(rcd);

        writer.write("    @RequestBody(required = true, content = @Content(schema = @Schema(implementation = " + rcd.element().toString() + ".class)))");
        writeNewLine(writer);
        if (Object.class.getName().equals(returnType)) {
            writer.write("    public Flux<CloudEvent> on" + rcd.element().getSimpleName() + "(ServerWebExchange exchange) {");
        } else {
            writer.write("    public Flux<" + returnType + "> on" + rcd.element().getSimpleName() + "(ServerWebExchange exchange) {");
        }
        writeNewLine(writer);

        writer.write("        return join(exchange.getRequest().getBody(), -1)");
        writeNewLine(writer);
        writer.write("            .flatMap(buffer -> {");
        writeNewLine(writer);
        writer.write("                try (Span ignored = this.tracer.start(\"convert-payload-to-" + rcd.element().getSimpleName().toString() + "\")) {");
        writeNewLine(writer);
        writer.write("                try (java.io.InputStream is = buffer.asInputStream(true)) {");
        writeNewLine(writer);
        writer.write("                    return Mono.just(mapper.readValue(is, " + rcd.element().toString() + ".class));");
        writeNewLine(writer);
        writer.write("                } catch (java.io.IOException cause) {");
        writeNewLine(writer);
        //writer.write("                   return Mono.error(new " + rcd.element().toString() +"Exception(cause));");
        writer.write("                   return Mono.error(cause);");
        writeNewLine(writer);
        writer.write("                }");
        writeNewLine(writer);
        writer.write("                }");
        writeNewLine(writer);
        writer.write("            })");
        writeNewLine(writer);
        if (Object.class.getName().equals(returnType)) {
            writer.write("            .flatMapMany(command -> gateway.<" + Event.class.getName() + ">dispatch(new ReactiveCommandContext(command, exchange)))");
            writeNewLine(writer);
            writer.write("            .map(CloudEvents::to);");
            writeNewLine(writer);
        } else {
            writer.write("            .flatMapMany(command -> gateway.dispatch(new ReactiveCommandContext(command, exchange)));");
            writeNewLine(writer);
        }

        writer.write("    }");
        writeNewLine(writer);

    }

    /*
    private static void writeMethodRestAsync(Writer writer, RestControllerDescriptor rcd) throws IOException {
        String returnType = getReturnTypeClassname(rcd);

        if (Object.class.getName().equals(returnType)) {
            writer.write("    public Flux<CloudEvent> on" + rcd.element().getSimpleName() + "(@RequestBody Mono<" +  rcd.element().toString() + "> cmd, ServerWebExchange exchange) {");
        } else {
            writer.write("    public Flux<" + returnType + "> on" + rcd.element().getSimpleName() + "(@RequestBody Mono<"+ rcd.element().toString() + "> cmd, ServerWebExchange exchange) {");
        }
        writeNewLine(writer);

        if (Object.class.getName().equals(returnType)) {
            writer.write("        return cmd.flatMapMany(command -> gateway.<" + Event.class.getName() + ">dispatch(new ReactiveCommandContext(command, exchange)))");
            writeNewLine(writer);
            writer.write("            .map(CloudEvents::to);");
            writeNewLine(writer);
        } else {
            writer.write("        return cmd.flatMapMany(command -> gateway.dispatch(new ReactiveCommandContext(command, exchange)));");
            writeNewLine(writer);
        }

        writer.write("    }");
        writeNewLine(writer);

    }*/

    private static void writeSpring(Writer writer, RestControllerDescriptor rcd) throws IOException {
        String returnType = getReturnTypeClassname(rcd);

        String[] type = {"application/cloudevents+json"};
        if (!Object.class.getName().equals(returnType)) {
            type = rcd.getRestController().produces();
        }

        if (HttpMethod.POST == rcd.getRestController().method()) {
            writer.write("    @org.springframework.web.bind.annotation.PostMapping(path=\"" + rcd.getRestController().uri() + "\", produces = " + getProduces(type) + ", consumes = MediaType.APPLICATION_JSON_VALUE)");
            writeNewLine(writer);

            return;
        }

        if (HttpMethod.PUT == rcd.getRestController().method()) {
            writer.write("    @org.springframework.web.bind.annotation.PutMapping(path=\"" + rcd.getRestController().uri() + "\", produces = " + getProduces(type) + ", consumes = MediaType.APPLICATION_JSON_VALUE)");
            writeNewLine(writer);
            return;
        }

        if (HttpMethod.DELETE == rcd.getRestController().method()) {
            writer.write("    @org.springframework.web.bind.annotation.DeleteMapping(path=\"" + rcd.getRestController().uri() + "\", produces = " + getProduces(type) + ", consumes = MediaType.APPLICATION_JSON_VALUE)");
            writeNewLine(writer);
            return;
        }

    }

    private static String getProduces(String[] type) {
        String result = "{";
        for (int i = 0; i < type.length; i++) {
            result += "\"" + type[i] + "\"";
            if (i + 1 != type.length) {
                result += ",";
            }
        }
        result += "}";
        return result;
    }

    private static String getReturnTypeClassname(RestControllerDescriptor rcd) {
        try {
            return rcd.getRestController().returnType().getName();
        } catch (MirroredTypeException e) {
            TypeMirror typeMirror = e.getTypeMirror();
            return typeMirror.toString();
        }
    }
}
