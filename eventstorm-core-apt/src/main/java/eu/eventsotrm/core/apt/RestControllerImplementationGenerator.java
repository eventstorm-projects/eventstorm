package eu.eventsotrm.core.apt;

import static eu.eventsotrm.sql.apt.Helper.writeGenerated;
import static eu.eventsotrm.sql.apt.Helper.writeNewLine;
import static eu.eventsotrm.sql.apt.Helper.writePackage;

import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.CompletableFuture;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;

import com.google.common.collect.ImmutableList;

import eu.eventsotrm.core.apt.model.RestControllerDescriptor;
import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;
import eu.eventstorm.core.CommandGateway;
import eu.eventstorm.core.EventPayload;
import eu.eventstorm.core.annotation.HttpMethod;
import eu.eventstorm.core.cloudevent.CloudEvent;
import eu.eventstorm.core.cloudevent.CloudEvents;

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
				generate(processingEnvironment, name, desc);
			} catch (Exception cause) {
				logger.error("Exception for [" + name + "] -> [" + cause.getMessage() + "]", cause);
			}
		});

	}

	private void generate(ProcessingEnvironment env, String name, ImmutableList<RestControllerDescriptor> desc) throws IOException {

		JavaFileObject object = env.getFiler().createSourceFile(name);
		try (Writer writer = object.openWriter()) {

			writeHeader(writer, env, desc);
			writeVariables(writer, desc.get(0).getRestController().name());
			writeConstructor(writer, desc.get(0));
			writeMethods(writer, desc);
			writer.write("}");
		}

	}

	private void writeHeader(Writer writer, ProcessingEnvironment env, ImmutableList<RestControllerDescriptor> desc) throws IOException {

	    String javaPackage = desc.get(0).getRestController().javaPackage();
	    if ("".equals(javaPackage)) {
	        javaPackage = env.getElementUtils().getPackageOf(desc.get(0).element()).toString();
	        if (javaPackage.startsWith("package")) {
                // with eclipse compiler
	            javaPackage = javaPackage.substring(7).trim();
	            javaPackage+= ".rest";
            }
	    }
	    
		writePackage(writer, javaPackage);
		writeNewLine(writer);

		writer.write("import org.springframework.http.ResponseEntity;");
		writeNewLine(writer);
		writer.write("import org.springframework.web.bind.annotation.RequestBody;");
		writeNewLine(writer);
		writer.write("import org.springframework.web.bind.annotation.RestController;");
		writeNewLine(writer);
		writer.write("import com.google.common.collect.ImmutableList;");
		writeNewLine(writer);
		
		
		
		for (RestControllerDescriptor rcd : desc) {
			if (rcd.getRestController().async()) {
				writer.write("import " + CompletableFuture.class.getName() + ";");
				writeNewLine(writer);
				break;
			}
		}
		
		writer.write("import " + CloudEvent.class.getName() + ";");
		writeNewLine(writer);
		writer.write("import " + CloudEvents.class.getName() + ";");
		writeNewLine(writer);
		writer.write("import " + EventPayload.class.getName() + ";");
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
		writeNewLine(writer);
	}

	private void writeConstructor(Writer writer, RestControllerDescriptor desc) throws IOException {

		writer.write("    public ");
		writer.write(desc.getRestController().name());
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
			if (rcd.getRestController().async()) {
				writeMethodRestAsync(writer, rcd);
			} else {
				writeMethodRest(writer, rcd);
			}

		}

	}

	private static void writeMethodRestAsync(Writer writer, RestControllerDescriptor rcd) throws IOException {
		writer.write("    public CompletableFuture<ResponseEntity<ImmutableList<CloudEvent>>> on(@RequestBody ");
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
		writer.write("        return CompletableFuture.supplyAsync(() -> this.gateway.dispatch(command))");
		writeNewLine(writer);
		writer.write("                         .thenApplyAsync(e -> ResponseEntity.ok(CloudEvents.to(e)));");
		writeNewLine(writer);
		writer.write("    }");
		writeNewLine(writer);
		
	}

	private static void writeMethodRest(Writer writer, RestControllerDescriptor rcd) throws IOException {
		writer.write("    public void on(@RequestBody ");
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
		writer.write("        this.gateway.dispatch(command);");
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