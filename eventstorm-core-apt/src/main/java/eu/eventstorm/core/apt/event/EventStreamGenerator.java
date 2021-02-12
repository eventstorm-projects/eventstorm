//package eu.eventsotrm.core.apt.event;
//
//import static eu.eventsotrm.sql.apt.Helper.writeGenerated;
//import static eu.eventsotrm.sql.apt.Helper.writeNewLine;
//import static eu.eventsotrm.sql.apt.Helper.writePackage;
//
//import java.io.IOException;
//import java.io.Writer;
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.annotation.processing.ProcessingEnvironment;
//import javax.tools.JavaFileObject;
//
//import eu.eventsotrm.core.apt.SourceCode;
//import eu.eventsotrm.sql.apt.log.Logger;
//import eu.eventsotrm.sql.apt.log.LoggerFactory;
//
//public final class EventStreamGenerator {
//	
//	private final Logger logger;
//
//	public EventStreamGenerator() {
//		logger = LoggerFactory.getInstance().getLogger(EventStreamGenerator.class);
//	}
//
//    public void generate(ProcessingEnvironment processingEnvironment, SourceCode sourceCode) {
//    	List<String> streams = new ArrayList<>();
//	    sourceCode.forEventEvolution(t -> {
//	    	
//	    	for (String proto : t.eventEvolution().proto()) {
//	    		int index = proto.lastIndexOf('/');
//	    		if (index == -1) {
//	    			index = 0;
//	    		} else {
//	    			index++;
//	    		}
//	    		String item = proto.substring(index, proto.indexOf(".proto"));
//	    		if (!streams.contains(item)) {
//	    			streams.add(item);
//	    		}
//	    	}
//	    });
//	    
//	    generate(processingEnvironment, sourceCode, streams);
//    	
//    }
//    
//	private void generate(ProcessingEnvironment env, SourceCode sourceCode, List<String> streams) {
//
//		// check due to
//		// "org.aspectj.org.eclipse.jdt.internal.compiler.apt.dispatch.BatchFilerImpl.createSourceFile(BatchFilerImpl.java:149)"
//		if (env.getElementUtils().getTypeElement(sourceCode.getCqrsConfiguration().basePackage() + ".Streams") != null) {
//			logger.info("Java SourceCode already exist [" + sourceCode.getCqrsConfiguration().basePackage() + ".Streams]");
//			return;
//		}
//
//		if (streams.size() == 0) {
//			logger.info("No proto found => skip");
//			return;
//		}
//		
//		
//		try {
//			JavaFileObject object = env.getFiler().createSourceFile(sourceCode.getCqrsConfiguration().basePackage() + ".Streams");
//			try (Writer writer = object.openWriter()) {
//				writeHeader(writer, env, sourceCode);
//				writeEnumContent(writer, streams);
//				writer.write("}");
//			}
//		} catch (Exception cause) {
//			logger.error("Exception -> [" + cause.getMessage() + "]", cause);
//		}
//
//	}
//
//	private void writeEnumContent(Writer writer, List<String> streams) throws IOException {
//		for (int i = 0; i < streams.size() ; i++) {
//			writer.write("    ");
//			writer.write(streams.get(i));
//			if (i + 1 < streams.size()) {
//				writer.write(",");
//				writeNewLine(writer);
//			} else {
//				writer.write(";");
//				writeNewLine(writer);
//			}
//		}
//	}
//
//	private void writeHeader(Writer writer, ProcessingEnvironment env, SourceCode sourceCode) throws IOException {
//
//		writePackage(writer, sourceCode.getCqrsConfiguration().basePackage());
//		
//		writeGenerated(writer, EventStreamGenerator.class.getName());
//		writer.write("public enum Streams {");
//		writeNewLine(writer);
//	}
//
//}
