package eu.eventstorm.core.apt.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import eu.eventstorm.annotation.HttpMethod;
import eu.eventstorm.cloudevents.CloudEvent;
import eu.eventstorm.cloudevents.CloudEvents;
import eu.eventstorm.core.Event;
import eu.eventstorm.core.apt.SourceCode;
import eu.eventstorm.core.apt.model.RestControllerDescriptor;
import eu.eventstorm.core.apt.model.SagaControllerDescriptor;
import eu.eventstorm.cqrs.CommandGateway;
import eu.eventstorm.cqrs.context.ReactiveCommandContext;
import eu.eventstorm.sql.apt.log.Logger;
import eu.eventstorm.sql.apt.log.LoggerFactory;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static eu.eventstorm.sql.apt.Helper.writeGenerated;
import static eu.eventstorm.sql.apt.Helper.writeNewLine;
import static eu.eventstorm.sql.apt.Helper.writePackage;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class SagaRestControllerImplementationGenerator {

	private final Logger logger;

	public SagaRestControllerImplementationGenerator() {
		logger = LoggerFactory.getInstance().getLogger(SagaRestControllerImplementationGenerator.class);
	}

	public void generate(ProcessingEnvironment env, SourceCode sourceCode) {
		ImmutableList.Builder<SagaControllerDescriptor> builder = ImmutableList.builder();
		sourceCode.forEachSagaCommand(cmd -> {
			builder.add(new SagaControllerDescriptor(cmd));
		});

		Map<String, ImmutableList<SagaControllerDescriptor>> controllers = builder.build().stream()
				.collect(groupingBy( t -> t.getFCQN(env), mapping(identity(), toImmutableList())));

		if (!controllers.isEmpty()) {
			controllers.forEach((name, desc) -> {
				try {
					generate(env, name, desc, sourceCode);
				} catch (Exception cause) {
					logger.error("Exception for [" + name + "] -> [" + cause.getMessage() + "]", cause);
				}
			});
		}
	}

	private void generate(ProcessingEnvironment env, String name, ImmutableList<SagaControllerDescriptor> desc, SourceCode sourceCode) throws IOException {

	    // check due to "org.aspectj.org.eclipse.jdt.internal.compiler.apt.dispatch.BatchFilerImpl.createSourceFile(BatchFilerImpl.java:149)"
        if (env.getElementUtils().getTypeElement(name) != null) {
            logger.info("Java SourceCode already exist [" + name + "]");
            return;
        }
        
		JavaFileObject object = env.getFiler().createSourceFile(name);
		try (Writer writer = object.openWriter()) {

			writeHeader(writer, env, desc);
			writeVariables(writer, desc);
			writeConstructor(writer, desc);
			writeMethods(writer, desc);
			writer.write("}");
		}

	}

	private void writeHeader(Writer writer, ProcessingEnvironment env, ImmutableList<SagaControllerDescriptor> desc) throws IOException {

	    String javaPackage = desc.get(0).getPackage(env);
	    
		writePackage(writer, javaPackage);
		writeNewLine(writer);

        writer.write("import static org.springframework.core.io.buffer.DataBufferUtils.join;");
        writeNewLine(writer);

		writer.write("import org.springframework.http.MediaType;");
		writeNewLine(writer);
		writer.write("import org.springframework.web.bind.annotation.RequestBody;");
		writeNewLine(writer);
		writer.write("import org.springframework.web.bind.annotation.RestController;");		
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

		writer.write("import eu.eventstorm.saga.SagaContext;");
		writeNewLine(writer);
		writer.write("import eu.eventstorm.saga.SagaExecutionCoordinator;");
		writeNewLine(writer);
		writer.write("import eu.eventstorm.saga.SagaExecutionCoordinatorFactory;");
		writeNewLine(writer);
		writer.write("import eu.eventstorm.saga.impl.SagaContextImpl;");
		writeNewLine(writer);

		writeNewLine(writer);
		
		writeGenerated(writer, SagaRestControllerImplementationGenerator.class.getName());
		writer.write("@RestController");
		writeNewLine(writer);
		writer.write("public final class ");
		writer.write(desc.get(0).getRestController().name());
		writer.write(" {");
		writeNewLine(writer);
		writeNewLine(writer);
	}

	private void writeVariables(Writer writer, ImmutableList<SagaControllerDescriptor> desc) throws IOException {

		writer.write("    private static final String SERVER_WEB_EXCHANGE = ServerWebExchange.class.getName();");
		writeNewLine(writer);
		writer.write("    private final SagaExecutionCoordinatorFactory factory;");
		writeNewLine(writer);
		
		writer.write("    private final ");
		writer.write(ObjectMapper.class.getName());
		writer.write(" mapper;");
		writeNewLine(writer);

		ImmutableSet<String> listeners = desc.stream().map(d -> d.getRestController().listeners())
				.flatMap(Arrays::stream)
				.collect(ImmutableSet.toImmutableSet());

		if (!listeners.isEmpty()) {
			for (String listener : listeners) {
				writer.write("    private final eu.eventstorm.saga.SagaContextListener ");
				writer.write(listener);
				writer.write(";");
				writeNewLine(writer);
			}
		}

		writeNewLine(writer);
	}

	private void writeConstructor(Writer writer, ImmutableList<SagaControllerDescriptor> desc) throws IOException {

		ImmutableSet<String> listeners = desc.stream().map(d -> d.getRestController().listeners())
				.flatMap(Arrays::stream)
				.collect(ImmutableSet.toImmutableSet());

		writer.write("    public ");
		writer.write(desc.get(0).getRestController().name());
		writer.write("(");
		writer.write("SagaExecutionCoordinatorFactory factory, "+ ObjectMapper.class.getName() +" mapper");

		if (listeners.isEmpty()) {
			writer.write(") {");
		} else {
			StringBuilder builder = new StringBuilder();
			builder.append(',');
			for (String listener : listeners) {
				builder.append("\n          @org.springframework.beans.factory.annotation.Qualifier(\"").append(listener).append("\") eu.eventstorm.saga.SagaContextListener " + listener + ",");
			}
			builder.deleteCharAt(builder.length()-1);
			writer.write(builder.toString());
			writer.write(") {");
		}

		writeNewLine(writer);
		writer.write("        this.factory = factory;");
		writeNewLine(writer);
		writer.write("        this.mapper = mapper;");
		writeNewLine(writer);

		if (!listeners.isEmpty()) {
			for (String listener : listeners) {
				writer.write("        this." + listener +" = " + listener + ";");
				writeNewLine(writer);
			}
		}
		
		writer.write("    }");
		writeNewLine(writer);
		writeNewLine(writer);

	}

	private static void writeMethods(Writer writer, ImmutableList<SagaControllerDescriptor> desc) throws IOException {

		for (SagaControllerDescriptor rcd : desc) {
			writeSpring(writer, rcd);
			writeMethodRestAsync(writer, rcd);

		}

	}

	private static void writeMethodRestAsync(Writer writer, SagaControllerDescriptor rcd) throws IOException {
		String returnType = getReturnTypeClassname(rcd);
		
		if (Void.class.getName().equals(returnType)) {
			writer.write("    public Flux<CloudEvent> on"+  rcd.element().getSimpleName() +"(ServerWebExchange exchange) {");	
		} else {
			writer.write("    public Flux<" + returnType + "> on" + rcd.element().getSimpleName() + "(ServerWebExchange exchange) {");
		}
		writeNewLine(writer);
		
		writer.write("        return join(exchange.getRequest().getBody(), -1)");
		writeNewLine(writer);
		writer.write("            .flatMap(buffer -> {");
		writeNewLine(writer);
		writer.write("                try (java.io.InputStream is = buffer.asInputStream(true)) {");
		writeNewLine(writer);
		writer.write("                    return Mono.just(mapper.readValue(is, " + rcd.element().toString() + ".class));");
		writeNewLine(writer);
		writer.write("                } catch (java.io.IOException cause) {");
		writeNewLine(writer);
		writer.write("                   return Mono.error(cause);");
		writeNewLine(writer);
		writer.write("                }");
		writeNewLine(writer);
		writer.write("            })");
		writeNewLine(writer);
		if (Void.class.getName().equals(returnType)) {
			writer.write("            .flatMap(command -> {");
			writeNewLine(writer);
			writer.write("                SagaExecutionCoordinator coordinator = factory.newInstance(\""+ rcd.getCommand().name() +"\");");
			writeNewLine(writer);
			writer.write("                SagaContext context = new SagaContextImpl(command);");
			writeNewLine(writer);
			writer.write("                context.put(SERVER_WEB_EXCHANGE, exchange);");
			writeNewLine(writer);
			for (String listener : rcd.getCommand().controller().listeners()) {
				writer.write("                " + listener + ".init(exchange,context);");
				writeNewLine(writer);
			}
			writer.write("                return coordinator.execute(context);");
			writeNewLine(writer);
			writer.write("            })");
			writeNewLine(writer);

			writer.write("             .flatMapMany(ctx -> Flux.fromIterable(ctx.getEvents()));");
			writeNewLine(writer);			
		} else {
			writer.write("            .flatMapMany(command -> gateway.dispatch(new ReactiveCommandContext(command, exchange)));");
			writeNewLine(writer);
		}

		writer.write("    }");
		writeNewLine(writer);
		
	}
	
	private static void writeSpring(Writer writer, SagaControllerDescriptor rcd) throws IOException {
		String returnType = getReturnTypeClassname(rcd);
		
		String type = "application/cloudevents+json";
		if (!Void.class.getName().equals(returnType)) {
			type = rcd.getRestController().produces();
		}
		
		if (HttpMethod.POST == rcd.getRestController().method()) {
			writer.write("    @org.springframework.web.bind.annotation.PostMapping(path=\"" + rcd.getRestController().uri() + "\", produces = \""+ type +"\", consumes = MediaType.APPLICATION_JSON_VALUE)");
			writeNewLine(writer);

			return;
		}

		if (HttpMethod.PUT == rcd.getRestController().method()) {
			writer.write("    @org.springframework.web.bind.annotation.PutMapping(path=\"" + rcd.getRestController().uri() + "\", produces = \""+ type +"\", consumes = MediaType.APPLICATION_JSON_VALUE)");
			writeNewLine(writer);
			return;
		}

		if (HttpMethod.DELETE == rcd.getRestController().method()) {
			writer.write("    @org.springframework.web.bind.annotation.DeleteMapping(path=\"" + rcd.getRestController().uri() + "\", produces = \""+ type +"\", consumes = MediaType.APPLICATION_JSON_VALUE)");
			writeNewLine(writer);
			return;
		}

	}

	private static String getReturnTypeClassname(SagaControllerDescriptor rcd) {
		try {
			return rcd.getRestController().returnType().getName();
		} catch (MirroredTypeException e) {
			TypeMirror typeMirror = e.getTypeMirror();
			return typeMirror.toString();
		}
	}
}