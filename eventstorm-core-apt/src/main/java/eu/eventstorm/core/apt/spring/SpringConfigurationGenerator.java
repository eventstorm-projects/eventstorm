package eu.eventstorm.core.apt.spring;

import static eu.eventstorm.sql.apt.Helper.writeGenerated;
import static eu.eventstorm.sql.apt.Helper.writeNewLine;
import static eu.eventstorm.sql.apt.Helper.writePackage;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;

import eu.eventstorm.core.apt.SourceCode;
import eu.eventstorm.core.apt.model.ProtobufMessage;
import eu.eventstorm.sql.apt.log.Logger;
import eu.eventstorm.sql.apt.log.LoggerFactory;
import eu.eventstorm.cqrs.PageQueryDescriptors;
import eu.eventstorm.eventstore.StreamManager;
import eu.eventstorm.eventstore.memory.InMemoryStreamManagerBuilder;
import eu.eventstorm.util.Strings;

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

		String className = "EventstormSpringAutoConfiguration";
		if (!Strings.isEmpty(sourceCode.getCqrsConfiguration().id())) {
			className += "_" + sourceCode.getCqrsConfiguration().id();
		}

		// check due to
		// "org.aspectj.org.eclipse.jdt.internal.compiler.apt.dispatch.BatchFilerImpl.createSourceFile(BatchFilerImpl.java:149)"
		if (env.getElementUtils().getTypeElement(sourceCode.getCqrsConfiguration().basePackage() + "." + className) != null) {
			logger.info("Java SourceCode already exist " + sourceCode.getCqrsConfiguration().basePackage() + "." + className);
			return;
		}

		try {
			JavaFileObject object = env.getFiler().createSourceFile(sourceCode.getCqrsConfiguration().basePackage() + "." + className);
			try (Writer writer = object.openWriter()) {
				writeHeader(writer, env, code, className);
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

		String suffix;
		if (!Strings.isEmpty(sourceCode.getCqrsConfiguration().id())) {
			suffix = "_" + sourceCode.getCqrsConfiguration().id();
		} else {
			suffix = "";
		}

		sourceCode.forEachQueryPackage((pack, list) -> {
			try {
				writeNewLine(writer);
				writer.write("    @Bean");
				writeNewLine(writer);
				writer.write("    com.fasterxml.jackson.databind.Module queryModule"+ counter.incrementAndGet() + suffix +"() {");
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
		sourceCode.forEachDatabaseViewQuery(dq -> counter.incrementAndGet());
		sourceCode.forEachDatabaseTableQuery(dq -> counter.incrementAndGet());
		if (counter.get() == 0) {
			return;
		}
		
		try {
			writeNewLine(writer);
			writer.write("    @Bean");
			writeNewLine(writer);
			writer.write("    "+ PageQueryDescriptors.class.getName() + " pageQueryDescriptors() {");
			 writeNewLine(writer);
			writer.write("       return new EventstormPageQueryDescriptors();");
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

    private void writeHeader(Writer writer, ProcessingEnvironment env, SourceCode sourceCode, String classname) throws IOException {

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
        writer.write("public class " + classname + " {");
        writeNewLine(writer);
    }
    
}