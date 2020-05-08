package eu.eventsotrm.core.apt.event;

import static eu.eventsotrm.sql.apt.Helper.getReturnType;
import static eu.eventsotrm.sql.apt.Helper.writeGenerated;
import static eu.eventsotrm.sql.apt.Helper.writeNewLine;
import static eu.eventsotrm.sql.apt.Helper.writePackage;

import java.io.IOException;
import java.io.Writer;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;

import com.google.common.collect.ImmutableSet;
import com.google.protobuf.InvalidProtocolBufferException;

import eu.eventsotrm.core.apt.SourceCode;
import eu.eventsotrm.core.apt.model.EventEvolutionDescriptor;
import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;
import eu.eventstorm.core.Event;
import eu.eventstorm.cqrs.event.EvolutionHandler;
import io.protostuff.compiler.model.Message;
import io.protostuff.compiler.parser.FileDescriptorLoader;
import io.protostuff.compiler.parser.FileDescriptorLoaderImpl;
import io.protostuff.compiler.parser.FileReader;
import io.protostuff.compiler.parser.Importer;
import io.protostuff.compiler.parser.ImporterImpl;
import io.protostuff.compiler.parser.ParseErrorLogger;
import io.protostuff.compiler.parser.ProtoContext;

public final class EventProtoGenerator {
	

	private final Logger logger;

	public EventProtoGenerator() {
		logger = LoggerFactory.getInstance().getLogger(EventProtoGenerator.class);
	}

    public void generate(ProcessingEnvironment processingEnvironment, SourceCode sourceCode) {
    	
    	logger.info("---------------------------> EventProtoGenerator");
    	
	      sourceCode.forEventEvolution(t -> {
	    		logger.info("---------------------------> EventProtoGenerator -> " + t);
		      try {
		          generate(processingEnvironment, t);
		      } catch (Exception cause) {
		      	logger.error("Exception for [" + t + "] -> [" + cause.getMessage() + "]", cause);
		      }
	      });
    	
    }
    
    
	private void generate(ProcessingEnvironment processingEnvironment, EventEvolutionDescriptor descriptor) throws IOException {

		FileDescriptorLoader fdl = new FileDescriptorLoaderImpl(new ParseErrorLogger(), ImmutableSet.of());
		
		String[] protos = descriptor.eventEvolution().proto();
		ProtoContext[] ctxs = new ProtoContext[protos.length]; 
		
		Importer importer = new ImporterImpl(fdl);
		
		for (int i = 0; i < protos.length; i++) {
			try {
				ctxs[i] = importer.importFile(new FileReader() {
					@Override
					public CharStream read(String name) {
						try {
							return CharStreams.fromStream(EventProtoGenerator.class.getResourceAsStream(name));
						} catch (IOException cause) {
							logger.error("failed to parse [" + name + "]", cause);
							return null;
						}
					}
				}, protos[i]);	
			} catch (Throwable ex) {
				logger.error("EventProtoGenerator -> error",  ex);
				
			}
		}
		generate(processingEnvironment, descriptor, ctxs);
		
	}

	private void generate(ProcessingEnvironment env, EventEvolutionDescriptor descriptor, ProtoContext[] ctxs) throws IOException {

		logger.info("---------------------------> EventProtoGenerator -> generate : " + ctxs);
		
		// check due to "org.aspectj.org.eclipse.jdt.internal.compiler.apt.dispatch.BatchFilerImpl.createSourceFile(BatchFilerImpl.java:149)"
        // if (env.getElementUtils().getTypeElement(getName(domainModel.asElement().toString())) != null) {
        //    logger.info("Java SourceCode already exist [" + getName(domainModel.asElement().toString()) + "]");
        //    return;
        //}
        
		
		String fcqn = env.getElementUtils().getPackageOf(descriptor.element()).toString() + ".evolution.Abstract" + descriptor.simpleName() + "EvolutionHandler" ;
		
		
		JavaFileObject object = env.getFiler().createSourceFile(fcqn);
		try (Writer writer = object.openWriter()) {

			writeHeader(writer, env, descriptor);
//			// writeConstructor(writer, descriptor);
//			// writeVariables(writer, descriptor);
			writeMethods(writer, descriptor, ctxs);
			writer.write("}");
		}

	}
	
	private static void writeHeader(Writer writer, ProcessingEnvironment env, EventEvolutionDescriptor descriptor) throws IOException {

		writePackage(writer, env.getElementUtils().getPackageOf(descriptor.element()).toString() + ".evolution");
		writeNewLine(writer);
		writer.write("import ");
		writer.write(Event.class.getName());
		writer.write(";");
		writeNewLine(writer);
		writer.write("import ");
		writer.write(EvolutionHandler.class.getName());
		writer.write(";");
		writeNewLine(writer);
		writer.write("import ");
		writer.write(InvalidProtocolBufferException.class.getName());
		writer.write(";");
		writeNewLine(writer);

		writeGenerated(writer, EventProtoGenerator.class.getName());

		writer.write("public abstract class ");
		writer.write("Abstract" + descriptor.simpleName() + "EvolutionHandler");
		writer.write(" implements ");
		writer.write(EvolutionHandler.class.getSimpleName());
		writer.write(" {");
		writeNewLine(writer);
	}

	private void writeMethods(Writer writer, EventEvolutionDescriptor descriptor, ProtoContext[] ctxs) throws IOException {
		writeMethodOnEvent(writer, descriptor, ctxs);
		
		for (ProtoContext ctx : ctxs) {
			writeMethods(writer, descriptor, ctx);
		}
	}

	private void writeMethodOnEvent(Writer writer, EventEvolutionDescriptor descriptor, ProtoContext[] ctxs) throws IOException {
		writeNewLine(writer);
        writer.write("    /** {@inheritDoc} */");
        writeNewLine(writer);
        writer.write("    @Override");
        writeNewLine(writer);
        writer.write("    public final void on(Event event) {");
        
        for (ProtoContext ctx : ctxs) {
        	for (Message message : ctx.getProto().getMessages()) {
        		writeNewLine(writer);
        		writer.write("        if (event.getData().getTypeUrl().equals(\"");
        		writer.write(ctx.getProto().getName());
        		writer.write("/");
        		writer.write(message.getName());
        		writer.write("\")) {");
        		writeNewLine(writer);
        		writer.write("            try {");
        		writeNewLine(writer);
        		writer.write("                on(event, event.getData().unpack(" + ctx.getProto().getPackage().toString());
        		writer.write(".");
        		writer.write(message.getName());
        		writer.write(".class));");
        		writeNewLine(writer);
        		writer.write("            } catch (" + InvalidProtocolBufferException.class.getSimpleName() + " cause) {");
        		writeNewLine(writer);
        		writer.write("            }");
        		writeNewLine(writer);
        		writer.write("            return;");
        		writeNewLine(writer);
        		writer.write("        }");
        	}
        }
        
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
	}
	
	private void writeMethods(Writer writer, EventEvolutionDescriptor descriptor, ProtoContext ctx) throws IOException {
		for (Message message : ctx.getProto().getMessages()) {
			writeNewLine(writer);
	        writer.write("    protected abstract void on(Event event, " + ctx.getProto().getPackage().toString());
	        writer.write(".");
	        writer.write(message.getName());
	        writer.write(" payload);");
	        writeNewLine(writer);
		}
		
	}
    
}
