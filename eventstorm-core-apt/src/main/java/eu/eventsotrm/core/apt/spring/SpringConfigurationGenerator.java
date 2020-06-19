package eu.eventsotrm.core.apt.spring;

import static eu.eventsotrm.sql.apt.Helper.writeGenerated;
import static eu.eventsotrm.sql.apt.Helper.writeNewLine;
import static eu.eventsotrm.sql.apt.Helper.writePackage;

import java.io.IOException;
import java.io.Writer;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import eu.eventsotrm.core.apt.SourceCode;
import eu.eventsotrm.sql.apt.Helper;
import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;
import eu.eventstorm.eventstore.StreamManager;
import eu.eventstorm.eventstore.memory.InMemoryStreamManagerBuilder;
import io.protostuff.compiler.model.Message;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class SpringConfigurationGenerator {

	private final Logger logger;
	
	private ProcessingEnvironment env; 
	private SourceCode code;

	public SpringConfigurationGenerator() {
		logger = LoggerFactory.getInstance().getLogger(SpringConfigurationGenerator.class);
	}

	public void generateCommand(ProcessingEnvironment processingEnvironment, SourceCode sourceCode) {
		this.env = processingEnvironment;
		this.code = sourceCode;

		// check due to
		// "org.aspectj.org.eclipse.jdt.internal.compiler.apt.dispatch.BatchFilerImpl.createSourceFile(BatchFilerImpl.java:149)"
		if (env.getElementUtils().getTypeElement(sourceCode.getCqrsConfiguration().basePackage() + ".EventstormSpringAutoConfiguration") != null) {
			logger.info("Java SourceCode already exist " + sourceCode.getCqrsConfiguration().basePackage() + ".EventstormSpringAutoConfiguration");
			return;
		}

		try {
			JavaFileObject object = env.getFiler().createSourceFile(sourceCode.getCqrsConfiguration().basePackage() + ".EventstormSpringAutoConfiguration");
			try (Writer writer = object.openWriter()) {
				writeHeader(writer, env, code);
				writeStreamManager(writer, env, code);
				writeTypeRegistry(writer, env, code);
				writeCommandModule(writer, env, code);
				writeQueryModule(writer, env, code);
		        writer.write("}");
			}
		} catch (IOException cause) {
			logger.error("Exception for [" + cause.getMessage() + "]", cause);
		}
    	 
    }
	

	private void writeCommandModule(Writer writer, ProcessingEnvironment env, SourceCode sourceCode) {
		AtomicInteger counter = new AtomicInteger();
		sourceCode.forEachAllCommandPackage((pack, list) -> {
			try {
				writeNewLine(writer);
				writer.write("    @Bean");
				writeNewLine(writer);
				writer.write("    com.fasterxml.jackson.databind.Module commandModule"+ counter.incrementAndGet() +"() {");
				 writeNewLine(writer);
				writer.write("       return new " + pack + ".json.CommandModule();");
			    writeNewLine(writer);
				writer.write("    }");
			    writeNewLine(writer);
				
			} catch (Exception cause) {
				logger.error("Exception for [" + pack + "] -> [" + cause.getMessage() + "]", cause);
			}
		});
		
	}
	
	private void writeQueryModule(Writer writer, ProcessingEnvironment env, SourceCode sourceCode) {
		AtomicInteger counter = new AtomicInteger();
		sourceCode.forEachDatabaseQueryPackage((pack, list) -> {
			try {
				writeNewLine(writer);
				writer.write("    @Bean");
				writeNewLine(writer);
				writer.write("    com.fasterxml.jackson.databind.Module queryModule"+ counter.incrementAndGet() +"() {");
				 writeNewLine(writer);
				writer.write("       return new " + pack + ".json.QueryModule();");
			    writeNewLine(writer);
				writer.write("    }");
			    writeNewLine(writer);
				
			} catch (Exception cause) {
				logger.error("Exception for [" + pack + "] -> [" + cause.getMessage() + "]", cause);
			}
		});
	}

	private void writeTypeRegistry(Writer writer, ProcessingEnvironment env, SourceCode sourceCode) throws IOException {

		writeNewLine(writer);
		writer.write("    @Bean");
		writeNewLine(writer);
		writer.write("    TypeRegistry typeRegistry() {");
		writeNewLine(writer);
		writer.write("        return TypeRegistry.newBuilder()");
		writeNewLine(writer);
		sourceCode.forEventEvolution(ee -> {
			try {
				for (String stream : ee.getMessages().keySet()) {
					for (Message message : ee.getMessages().get(stream)) {
						writer.write("                .add(" + message.getName() + ".getDescriptor())");
						writeNewLine(writer);
					}	
				}
				
			} catch (IOException cause) {
				logger.error("Exception for [" + ee + "] -> [" + cause.getMessage() + "]", cause);
			}
		});
		
		AtomicBoolean atomicBoolean = new AtomicBoolean(false);
		sourceCode.forEachCommand(cd -> {
			
			if (atomicBoolean.get()) {
				return;
			}
			Optional<? extends TypeMirror> op = Helper.getTypes().directSupertypes(cd.element().asType()).stream()
				.filter(t -> "eu.eventstorm.cqrs.BatchCommand".equals(t.toString()))
				.findFirst();
			if (op.isPresent()) {
				try {
					writer.write("                .add(eu.eventstorm.cqrs.batch.BatchJobCreated.getDescriptor())");
					writeNewLine(writer);
				} catch (IOException cause) {
					logger.error("Exception for [" + cd + "] -> [" + cause.getMessage() + "]", cause);
				}
				atomicBoolean.set(true);
			}
			
		});
		
		writer.write("                .build();");
		writeNewLine(writer);
		writer.write("    }");
	    writeNewLine(writer);
		
	}
    
	private void writeStreamManager(Writer writer, ProcessingEnvironment env, SourceCode sourceCode) throws IOException {
		
		writeNewLine(writer);
		writer.write("    @Bean");
		writeNewLine(writer);
        writer.write("    StreamManager streamManager() {");
        writeNewLine(writer);
        writer.write("        return new InMemoryStreamManagerBuilder()");
        writeNewLine(writer);
        
        sourceCode.forEventEvolution(ee -> {
        	try {
        		for (String stream : ee.getMessages().keySet()) {
        			writer.write("            .withDefinition(\"" + stream + "\")");
        			writeNewLine(writer);
        			for (Message message : ee.getMessages().get(stream)) {
        				writer.write("                .withPayload(" + message.getName() + ".class, " +  message.getName()+".getDescriptor(), " +
        						message.getName()+".parser(), () -> " +  message.getName() + ".newBuilder())");
        				writeNewLine(writer);
        				writer.write("            .and()");
        				writeNewLine(writer);
        			}
        		}
			} catch (IOException cause) {
				logger.error("Exception for [" + ee + "] -> [" + cause.getMessage() + "]", cause);
			}
        });
       
        writer.write("        .build();");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
		
	}

    private void writeHeader(Writer writer, ProcessingEnvironment env, SourceCode sourceCode) throws IOException {

        writePackage(writer, sourceCode.getCqrsConfiguration().basePackage());
        writeNewLine(writer);
        
        sourceCode.forEventEvolution(ee -> {
        	try {
	        	for (String stream : ee.getMessages().keySet()) {
					for (Message message : ee.getMessages().get(stream)) {
	        			writer.write("import " + String.valueOf(message.getProto().getOptions().get("java_package")) + "." + message.getName() + ";");
	    				writeNewLine(writer);	
					}
	        	}
			} catch (IOException cause) {
				logger.error("Exception for [" + ee + "] -> [" + cause.getMessage() + "]", cause);
			}
        });
        
        writeNewLine(writer);
        writer.write("import com.google.protobuf.TypeRegistry;");
        writeNewLine(writer);
        writer.write("import " + StreamManager.class.getName() + ";");
		writeNewLine(writer);
		writer.write("import " + InMemoryStreamManagerBuilder.class.getName() + ";");
		writeNewLine(writer);
        writer.write("import " + Bean.class.getName() + ";");
		writeNewLine(writer);
        writer.write("import " + Configuration.class.getName() + ";");
		writeNewLine(writer);
        writeGenerated(writer,SpringConfigurationGenerator.class.getName());
        writer.write("@Configuration");
        writeNewLine(writer);
        writer.write("public class EventstormSpringAutoConfiguration {");
        writeNewLine(writer);
    }
    
}