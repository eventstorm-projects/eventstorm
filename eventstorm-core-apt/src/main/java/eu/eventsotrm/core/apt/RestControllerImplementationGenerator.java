package eu.eventsotrm.core.apt;

import static eu.eventsotrm.sql.apt.Helper.writeGenerated;
import static eu.eventsotrm.sql.apt.Helper.writeNewLine;
import static eu.eventsotrm.sql.apt.Helper.writePackage;

import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;

import com.google.common.collect.ImmutableList;

import eu.eventsotrm.core.apt.model.RestControllerDescriptor;
import eu.eventsotrm.sql.apt.Helper;
import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;
import eu.eventstorm.core.CommandGateway;
import eu.eventstorm.core.annotation.HttpMethod;
import eu.eventstorm.core.cloudevent.CloudEvent;
import eu.eventstorm.core.cloudevent.CloudEvents;
import eu.eventstorm.core.util.NamedThreadFactory;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class RestControllerImplementationGenerator {

	private final Logger logger;

	RestControllerImplementationGenerator() {
		logger = LoggerFactory.getInstance().getLogger(RestControllerImplementationGenerator.class);
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
			writeConstructor(writer, desc.get(0), sourceCode);
			writeMethods(writer, desc);
			writer.write("}");
		}

	}

	private void writeHeader(Writer writer, ProcessingEnvironment env, ImmutableList<RestControllerDescriptor> desc) throws IOException {

	    String javaPackage = desc.get(0).getPackage(env);
	    
		writePackage(writer, javaPackage);
		writeNewLine(writer);

		
        writer.write("import " + Stream.class.getName() +";");
        writeNewLine(writer);
        writer.write("import " + Executor.class.getName() + ";");
        writeNewLine(writer);
        writer.write("import " + Executors.class.getName() + ";");
        writeNewLine(writer);
        
		writer.write("import org.springframework.web.bind.annotation.RequestBody;");
		writeNewLine(writer);
		writer.write("import org.springframework.web.bind.annotation.RestController;");		
		
		writeNewLine(writer);
		writer.write("import " + NamedThreadFactory.class.getName() + ";");
	    writeNewLine(writer);
		
		
		writer.write("import " + CompletableFuture.class.getName() + ";");
		writeNewLine(writer);
		
		writer.write("import " + CloudEvent.class.getName() + ";");
		writeNewLine(writer);
		writer.write("import " + CloudEvents.class.getName() + ";");
		writeNewLine(writer);
		
		writeGenerated(writer, RestControllerImplementationGenerator.class.getName());
		writer.write("@RestController");
		writeNewLine(writer);
		writer.write("public final class ");
		writer.write(desc.get(0).getRestController().name());
		writer.write(" {");
		writeNewLine(writer);
		writeNewLine(writer);
	}

	private void writeVariables(Writer writer, String name) throws IOException {

		writer.write("    private static final ");
		writer.write(org.slf4j.Logger.class.getName());
		writer.write(" LOGGER = ");
		writer.write(org.slf4j.LoggerFactory.class.getName());
		writer.write(".getLogger(");
		writer.write(name);
		writer.write(".class);");
		writeNewLine(writer);
		writeNewLine(writer);

		writer.write("    private final ");
		writer.write(CommandGateway.class.getName());
		writer.write(" gateway;");
		writeNewLine(writer);
		writer.write("    private final ");
        writer.write(Executor.class.getSimpleName());
        writer.write(" executor;");
        writeNewLine(writer);
        
		writeNewLine(writer);
	}

	private void writeConstructor(Writer writer, RestControllerDescriptor desc, SourceCode sourceCode) throws IOException {

		String threadName = "es_";
		if (sourceCode.getCqrsConfiguration() != null) {
			threadName = sourceCode.getCqrsConfiguration().eventStoreThread();
		} 
	    
		writer.write("    public ");
		writer.write(desc.getRestController().name());
		writer.write("(");
		writer.write(CommandGateway.class.getName());
		writer.write(" gateway) {");
		writeNewLine(writer);
		writer.write("        this.gateway = gateway;");
		writeNewLine(writer);
	    writer.write("        this.executor = Executors.newFixedThreadPool(1, new NamedThreadFactory(\"" + Helper.toSnakeCase(threadName) + "\"));");
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

	private static void writeMethodRestAsync(Writer writer, RestControllerDescriptor rcd) throws IOException {
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

	private static void writeSpring(Writer writer, RestControllerDescriptor rcd) throws IOException {

		if (HttpMethod.POST == rcd.getRestController().method()) {
			writer.write("    @org.springframework.web.bind.annotation.PostMapping(path=\"" + rcd.getRestController().uri() + "\", produces = \"application/cloudevents+json\")");
			writeNewLine(writer);

			return;
		}

		if (HttpMethod.PUT == rcd.getRestController().method()) {
			writer.write("    @org.springframework.web.bind.annotation.PutMapping(name=\"" + rcd.getRestController().uri() + "\", produces = \"application/cloudevents+json\")");
			writeNewLine(writer);
			return;
		}

	}

}