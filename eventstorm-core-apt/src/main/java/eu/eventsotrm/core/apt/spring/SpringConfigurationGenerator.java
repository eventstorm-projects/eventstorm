package eu.eventsotrm.core.apt.spring;

import static eu.eventsotrm.sql.apt.Helper.writeGenerated;
import static eu.eventsotrm.sql.apt.Helper.writeNewLine;
import static eu.eventsotrm.sql.apt.Helper.writePackage;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;

import eu.eventsotrm.core.apt.SourceCode;
import eu.eventsotrm.core.apt.model.ProtobufMessage;
import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;
import eu.eventstorm.cqrs.QueryDescriptors;
import eu.eventstorm.eventstore.StreamManager;
import eu.eventstorm.eventstore.memory.InMemoryStreamManagerBuilder;

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
				writeQueryDescriptor(writer, code);
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
		sourceCode.forEachQueryPackage((pack, list) -> {
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
	
	private void writeQueryDescriptor(Writer writer, SourceCode sourceCode) {
		
		AtomicInteger counter = new AtomicInteger(0);
		sourceCode.forEachDatabaseQuery(dq -> counter.incrementAndGet());
		if (counter.get() == 0) {
			return;
		}
		
		try {
			writeNewLine(writer);
			writer.write("    @Bean");
			writeNewLine(writer);
			writer.write("    "+ QueryDescriptors.class.getName() + " queryDescriptors() {");
			 writeNewLine(writer);
			writer.write("       return new EventstormQueryDescriptors();");
		    writeNewLine(writer);
			writer.write("    }");
		    writeNewLine(writer);
			
		} catch (Exception cause) {
			logger.error("Exception for [writeQueryDescriptor] -> [" + cause.getMessage() + "]", cause);
		}
	}
	

	private void writeTypeRegistry(Writer writer, ProcessingEnvironment env, SourceCode sourceCode) throws IOException {

		AtomicInteger counter = new AtomicInteger(0);
		sourceCode.forEventEvolution(eed -> counter.incrementAndGet());
		
		if (counter.get() == 0) {
			return;
		}
		
		writeNewLine(writer);
		writer.write("    @Bean");
		writeNewLine(writer);
		writer.write("    eu.eventstorm.core.protobuf.DescriptorModule descriptorModule() {");
		writeNewLine(writer);
		writer.write("        return new eu.eventstorm.core.protobuf.DescriptorModule(\"\", com.google.common.collect.ImmutableList.of(");
		writeNewLine(writer);
		
		
		List<String> names = new ArrayList<>();
		
		sourceCode.forEventEvolution(ee -> {
			for (String stream : ee.getMessages().keySet()) {
				for (ProtobufMessage message : ee.getMessages().get(stream)) {
					names.add(message.getName());
				}	
			}
		});
		
		for (int i = 0; i < names.size() ; i++) {
			writer.write("                " + names.get(i)+ ".getDescriptor()");
			if (i + 1 < names.size()) {
				writer.write(",");
			}
			writeNewLine(writer);
		}
		
		writer.write("                ));");
		writeNewLine(writer);
		writer.write("    }");
	    writeNewLine(writer);
		
	}
    
	private void writeStreamManager(Writer writer, ProcessingEnvironment env, SourceCode sourceCode) throws IOException {
		
		AtomicInteger counter = new AtomicInteger(0);
		sourceCode.forEventEvolution(dq -> counter.incrementAndGet());
		if (counter.get() == 0) {
			return;
		}
		
		writeNewLine(writer);
		writer.write("    @Bean");
		writeNewLine(writer);
        writer.write("    StreamManager streamManager() {");
        writeNewLine(writer);
        writer.write("        return new InMemoryStreamManagerBuilder()");
        writeNewLine(writer);
        
        Set<String> streams = new HashSet<>();
        
        sourceCode.forEventEvolution(ee -> {
        	try {
        		for (String stream : ee.getMessages().keySet()) {
        			
        			if (streams.contains(stream)) {
        				continue;
        			} else {
        				streams.add(stream);
        			}
        			
        			writer.write("            .withDefinition(\"" + stream + "\")");
        			writeNewLine(writer);
        			for (ProtobufMessage message : ee.getMessages().get(stream)) {
        				writer.write("                .withPayload(" + message.getName() + ".class, " +  message.getName()+".getDescriptor(), " +
        						message.getName()+".parser(), " +  message.getName() + "::newBuilder)");
        				writeNewLine(writer);
        				
        			}
        			
        			writer.write("            .and()");
        			writeNewLine(writer);
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
					for (ProtobufMessage message : ee.getMessages().get(stream)) {
	        			writer.write("import " + message.getProtobuf().getJavaPackage() + "." + message.getName() + ";");
	    				writeNewLine(writer);	
					}
	        	}
			} catch (IOException cause) {
				logger.error("Exception for [" + ee + "] -> [" + cause.getMessage() + "]", cause);
			}
        });
        
        writeNewLine(writer);
        writer.write("import " + StreamManager.class.getName() + ";");
		writeNewLine(writer);
		writer.write("import " + InMemoryStreamManagerBuilder.class.getName() + ";");
		writeNewLine(writer);
        writer.write("import org.springframework.context.annotation.Bean;");
		writeNewLine(writer);
        writer.write("import org.springframework.context.annotation.Configuration;");
		writeNewLine(writer);
        writeGenerated(writer,SpringConfigurationGenerator.class.getName());
        writer.write("@Configuration");
        writeNewLine(writer);
        writer.write("public class EventstormSpringAutoConfiguration {");
        writeNewLine(writer);
    }
    
}