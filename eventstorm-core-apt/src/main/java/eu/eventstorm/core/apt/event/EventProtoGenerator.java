package eu.eventstorm.core.apt.event;

import static eu.eventstorm.sql.apt.Helper.writeGenerated;
import static eu.eventstorm.sql.apt.Helper.writeNewLine;
import static eu.eventstorm.sql.apt.Helper.writePackage;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;

import eu.eventstorm.core.apt.model.EventEvolutionDescriptor;
import eu.eventstorm.core.apt.model.Protobuf;
import eu.eventstorm.core.apt.model.ProtobufMessage;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.InvalidProtocolBufferException;

import eu.eventstorm.core.apt.SourceCode;
import eu.eventstorm.sql.apt.log.Logger;
import eu.eventstorm.sql.apt.log.LoggerFactory;
import eu.eventstorm.annotation.CqrsConfiguration;
import eu.eventstorm.core.Event;
import eu.eventstorm.core.apt.protobuf.Protobuf3BaseListener;
import eu.eventstorm.core.apt.protobuf.Protobuf3Lexer;
import eu.eventstorm.core.apt.protobuf.Protobuf3Parser;
import eu.eventstorm.core.apt.protobuf.Protobuf3Parser.MessageContext;
import eu.eventstorm.core.apt.protobuf.Protobuf3Parser.OptionContext;
import eu.eventstorm.cqrs.event.EvolutionHandler;
import eu.eventstorm.util.Strings;

public final class EventProtoGenerator {
	

	private final Logger logger;

	public EventProtoGenerator() {
		logger = LoggerFactory.getInstance().getLogger(EventProtoGenerator.class);
	}

    public void generate(ProcessingEnvironment processingEnvironment, SourceCode sourceCode) {
	      sourceCode.forEventEvolution(t -> {
	    		logger.info("EventProtoGenerator -> " + t);
		      try {
		          generate(processingEnvironment, t, sourceCode.getCqrsConfiguration());
		      } catch (Exception cause) {
		      	logger.error("Exception for [" + t + "] -> [" + cause.getMessage() + "]", cause);
		      }
	      });
    	
    }
    
    
	private void generate(ProcessingEnvironment processingEnvironment, EventEvolutionDescriptor descriptor, CqrsConfiguration configuration) throws IOException {

		String[] protos = descriptor.eventEvolution().proto();
		
		ImmutableList.Builder<Protobuf> builder = ImmutableList.builder();
		
		for (String proto : protos) {
			
			Protobuf protobuf = new Protobuf(proto);
			ImmutableList.Builder<ProtobufMessage> messages = ImmutableList.builder();

			LinkedList<MessageContext> queue = new  LinkedList<>();
			
			Protobuf3Lexer lexer;
			try {
				lexer = new Protobuf3Lexer(CharStreams.fromStream(EventProtoGenerator.class.getResourceAsStream(proto)));	
			} catch (Exception cause) {
				logger.error("proto [" + proto + "] => error : ", cause);
				continue;
			}
			
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			Protobuf3Parser parser = new Protobuf3Parser(tokens);
			parser.addParseListener(new Protobuf3BaseListener() {

				@Override
				public void enterMessage(MessageContext ctx) {
					queue.add(ctx);
				}

				@Override
				public void exitOption(OptionContext ctx) {
					if ("java_package".equals(ctx.optionName().getText())) {
						String pack = ctx.constant().getText();
						protobuf.setJavaPackage(pack.substring(pack.indexOf('"')+1,pack.lastIndexOf('"')));
					}
				}
				@Override
				public void exitMessage(MessageContext ctx) {

					if (queue.size() == 1) {
						messages.add(new ProtobufMessage(protobuf, ctx.messageName().getText()));	
					}
					queue.removeLast();
				}
			});
			
			parser.proto();
			
			protobuf.setMessages(messages.build());
			builder.add(protobuf);
			
			descriptor.add(protobuf);
		}
		
	
		
		generate(processingEnvironment, descriptor, builder.build(), configuration);
		
	}

	private void generate(ProcessingEnvironment env, EventEvolutionDescriptor descriptor, ImmutableList<Protobuf> ctxs, CqrsConfiguration configuration) throws IOException {

		logger.info("---------------------------> EventProtoGenerator -> generate : " + ctxs);
		
		// check due to "org.aspectj.org.eclipse.jdt.internal.compiler.apt.dispatch.BatchFilerImpl.createSourceFile(BatchFilerImpl.java:149)"
        // if (env.getElementUtils().getTypeElement(getName(domainModel.asElement().toString())) != null) {
        //    logger.info("Java SourceCode already exist [" + getName(domainModel.asElement().toString()) + "]");
        //    return;
        //}
        
		
		String fcqn = env.getElementUtils().getPackageOf(descriptor.element()).toString() + ".evolution.Abstract" + descriptor.simpleName() + "EvolutionHandler" ;
		
		
		JavaFileObject object = env.getFiler().createSourceFile(fcqn);
		try (Writer writer = object.openWriter()) {
			writeHeader(writer, env, descriptor, ctxs);
			writeMethods(writer, descriptor, ctxs, configuration);
			writer.write("}");
		}

	}
	
	private void writeHeader(Writer writer, ProcessingEnvironment env, EventEvolutionDescriptor descriptor, ImmutableList<Protobuf> protobufs) throws IOException {

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
		
		
		
		for (Protobuf protobuf : protobufs) {
        	for (ProtobufMessage message : protobuf.getMessages()) {

        		//descriptor.add(protobuf.getName(), message);
        		
        		writer.write("import ");
        		writer.write(String.valueOf(protobuf.getJavaPackage()));
        		writer.write(".");
        		writer.write(message.getName());
        		writer.write(";");
        		writeNewLine(writer);
        	}
		}
		
		

		writeGenerated(writer, EventProtoGenerator.class.getName());

		writer.write("public abstract class ");
		writer.write("Abstract" + descriptor.simpleName() + "EvolutionHandler");
		writer.write(" implements ");
		writer.write(EvolutionHandler.class.getSimpleName());
		writer.write(" {");
		writeNewLine(writer);
	}

	private void writeMethods(Writer writer, EventEvolutionDescriptor descriptor, ImmutableList<Protobuf> protobufs, CqrsConfiguration configuration) throws IOException {
		writeMethodOnEvent(writer, descriptor, protobufs, configuration);
		
		for (Protobuf protobuf : protobufs) {
			writeMethods(writer, descriptor, protobuf);
		}
	}

	private void writeMethodOnEvent(Writer writer, EventEvolutionDescriptor descriptor, ImmutableList<Protobuf> protobufs, CqrsConfiguration configuration) throws IOException {
		writeNewLine(writer);
        writer.write("    /** {@inheritDoc} */");
        writeNewLine(writer);
        writer.write("    @Override");
        writeNewLine(writer);
        writer.write("    public final void on(Event event) {");
        
        for (Protobuf protobuf : protobufs) {
        	for (ProtobufMessage message : protobuf.getMessages()) {
        		writeNewLine(writer);
        		writer.write("        if (event.getData().getTypeUrl().equals(\"");
        		writer.write(configuration.evolutionDataTypeBaseUrl());
        		writer.write("/");
        		writer.write(getStreamName(protobuf));
        		writer.write("/");
//        		if (!Strings.isEmpty(protobuf.getJavaPackage())) {
//        			writer.write(protobuf.getJavaPackage() + ".");	
//        		}
        		writer.write(message.getName());
        		writer.write("\")) {");
        		writeNewLine(writer);
        		writer.write("            try {");
        		writeNewLine(writer);
        		writer.write("                on(event, event.getData().unpack(");
        		if (!Strings.isEmpty(protobuf.getJavaPackage())) {
        			writer.write(protobuf.getJavaPackage() + ".");	
        		}
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
	
	private void writeMethods(Writer writer, EventEvolutionDescriptor descriptor, Protobuf protobuf) throws IOException {
		for (ProtobufMessage message : protobuf.getMessages()) {
			writeNewLine(writer);
	        writer.write("    protected abstract void on(Event event, ");
	        if (!Strings.isEmpty(protobuf.getJavaPackage())) {
    			writer.write(protobuf.getJavaPackage() + ".");	
    		}
	        writer.write(message.getName());
	        writer.write(" payload);");
	        writeNewLine(writer);
		}
		
	}
    
	private String getStreamName(Protobuf protobuf) {
		int index = protobuf.getProto().lastIndexOf('/');
		if (index == -1) {
			index = 0;
		} else {
			index++;
		}
		return protobuf.getProto().substring(index, protobuf.getProto().indexOf(".proto"));
	}
	
}
