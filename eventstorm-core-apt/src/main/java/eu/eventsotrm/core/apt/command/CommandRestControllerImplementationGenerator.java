package eu.eventsotrm.core.apt.command;

import static eu.eventsotrm.sql.apt.Helper.writeGenerated;
import static eu.eventsotrm.sql.apt.Helper.writeNewLine;
import static eu.eventsotrm.sql.apt.Helper.writePackage;

import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;

import com.google.common.collect.ImmutableList;

import eu.eventsotrm.core.apt.SourceCode;
import eu.eventsotrm.core.apt.model.RestControllerDescriptor;
import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;
import eu.eventstorm.annotation.HttpMethod;
import eu.eventstorm.cloudevents.CloudEvent;
import eu.eventstorm.cloudevents.CloudEvents;
import eu.eventstorm.core.Event;
import eu.eventstorm.cqrs.CommandGateway;
import eu.eventstorm.cqrs.context.ReactiveCommandContext;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuples;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class CommandRestControllerImplementationGenerator {

	private final Logger logger;

	public CommandRestControllerImplementationGenerator() {
		logger = LoggerFactory.getInstance().getLogger(CommandRestControllerImplementationGenerator.class);
	}

	public void generate(ProcessingEnvironment processingEnvironment, SourceCode sourceCode) {

		sourceCode.forEachRestController((name, desc) -> {
			try {
				generate(processingEnvironment, name, desc, sourceCode);
			} catch (Exception cause) {
				logger.error("Exception for [" + name + "] -> [" + cause.getMessage() + "]", cause);
			}
		});

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


        writer.write("import java.util.logging.Level;");
        writeNewLine(writer);
		
        writer.write("import " + Stream.class.getName() +";");
        writeNewLine(writer);
        writer.write("import " + Executor.class.getName() + ";");
        writeNewLine(writer);
        
		writer.write("import org.springframework.web.bind.annotation.RequestBody;");
		writeNewLine(writer);
		writer.write("import org.springframework.web.bind.annotation.RestController;");		
		writeNewLine(writer);
		writer.write("import org.springframework.beans.factory.annotation.Qualifier;");       
        writeNewLine(writer);
        writer.write("import org.springframework.web.server.ServerWebExchange;");       
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

		writer.write("    private static final reactor.util.Logger LOGGER = reactor.util.Loggers");
		writer.write(".getLogger(");
		writer.write(name);
		writer.write(".class);");
		writeNewLine(writer);
		writeNewLine(writer);

		writer.write("    private final ");
		writer.write(CommandGateway.class.getName());
		writer.write(" gateway;");
		writeNewLine(writer);
		
		writeNewLine(writer);
	}

	private void writeConstructor(Writer writer, ImmutableList<RestControllerDescriptor> desc, SourceCode sourceCode) throws IOException {

		writer.write("    public ");
		writer.write(desc.get(0).getRestController().name());
		writer.write("(");
		writer.write(CommandGateway.class.getName());
		writer.write(" gateway) {");
		
		writeNewLine(writer);
		writer.write("        this.gateway = gateway;");
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
    
	private static void writeMethodRestAsync(Writer writer, RestControllerDescriptor rcd) throws IOException {
		String returnType = getReturnTypeClassname(rcd);
		
		if (Void.class.getName().equals(returnType)) {
			writer.write("    public Flux<CloudEvent> on(ServerWebExchange exchange, @RequestBody ");	
		} else {
			writer.write("    public Flux<" + returnType + "> on(ServerWebExchange exchange, @RequestBody ");
		}
		writer.write(rcd.element().toString());
		writer.write(" command) {");
		writeNewLine(writer);
		writeNewLine(writer);
		writer.write("        return Mono.just("+ Tuples.class.getName() + ".of(new ReactiveCommandContext(exchange), command))");
		writeNewLine(writer);
		writer.write("            .subscribeOn(" + Schedulers.class.getName()+".immediate())");
		writeNewLine(writer); 
		if (Void.class.getName().equals(returnType)) {
			writer.write("            .flatMapMany(tuple -> gateway.<"+rcd.element().toString() + ","+ Event.class.getName() + ">dispatch(tuple.getT1(), tuple.getT2()))");
			writeNewLine(writer);
			writer.write("            .map(CloudEvents::to);");
			writeNewLine(writer);			
		} else {
			writer.write("            .flatMapMany(tuple -> gateway.dispatch(tuple.getT1(), tuple.getT2()));");
			writeNewLine(writer);
		}

		writer.write("    }");
		writeNewLine(writer);
		
	}
	
	private static void writeSpring(Writer writer, RestControllerDescriptor rcd) throws IOException {
		String returnType = getReturnTypeClassname(rcd);
		
		String type = "application/cloudevents+json";
		if (!Void.class.getName().equals(returnType)) {
			type = rcd.getRestController().produces();
		}
		
		if (HttpMethod.POST == rcd.getRestController().method()) {
			writer.write("    @org.springframework.web.bind.annotation.PostMapping(path=\"" + rcd.getRestController().uri() + "\", produces = \""+ type +"\")");
			writeNewLine(writer);

			return;
		}

		if (HttpMethod.PUT == rcd.getRestController().method()) {
			writer.write("    @org.springframework.web.bind.annotation.PutMapping(name=\"" + rcd.getRestController().uri() + "\", produces = \""+ type +"\")");
			writeNewLine(writer);
			return;
		}

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