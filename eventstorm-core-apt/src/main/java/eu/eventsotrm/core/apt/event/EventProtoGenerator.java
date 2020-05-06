package eu.eventsotrm.core.apt.event;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.DeclaredType;
import javax.tools.JavaFileObject;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;

import com.google.common.collect.ImmutableSet;

import eu.eventsotrm.core.apt.SourceCode;
import eu.eventsotrm.core.apt.model.EventDescriptor;
import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;
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

    	FileDescriptorLoader fdl = new FileDescriptorLoaderImpl(new ParseErrorLogger(), ImmutableSet.of());
	     Importer importer = new ImporterImpl(fdl);
	     ProtoContext ctx  =     importer.importFile(new FileReader() {			
			@Override
			public CharStream read(String name) {
				try {
					return  CharStreams.fromStream(EventProtoGenerator.class.getResourceAsStream(name));
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			}
		}, "/proto/TransportMode.proto");
    	
	     
	     for (Message message : ctx.getProto().getMessages()) {
	    	 
	    	 
	    	 
	     }
    	
    	
    }
    
    
    private void generate(ProcessingEnvironment env, DeclaredType domainModel, List<Message> messages) throws IOException {

	    // check due to "org.aspectj.org.eclipse.jdt.internal.compiler.apt.dispatch.BatchFilerImpl.createSourceFile(BatchFilerImpl.java:149)"
       // if (env.getElementUtils().getTypeElement(getName(domainModel.asElement().toString())) != null) {
      //      logger.info("Java SourceCode already exist [" + getName(domainModel.asElement().toString()) + "]");
       //     return;
     //   }
//        
//		JavaFileObject object = env.getFiler().createSourceFile(getName(domainModel.asElement().toString()));
//		try (Writer writer = object.openWriter()) {
//
//			writeHeader(writer, env, domainModel, events);
//			// writeConstructor(writer, descriptor);
//			// writeVariables(writer, descriptor);
//			writeMethods(writer, events);
//			writer.write("}");
//		}

	}
    
    
    
//    
//    package eu.europa.europarl.emiss.bu.referential.cqrs.domain;
//
//    import com.google.protobuf.InvalidProtocolBufferException;
//
//    import eu.europa.europarl.emiss.bu.referential.cqrs.event.RegisteredTransportMode;
//    import eu.eventstorm.core.Event;
//
//    public abstract class TransportModeEvolutionHandler {
//    	
//    	protected void on(Event event) {
//    	
//    		if ("RegisteredTransportMode".equals(event.getData().getTypeUrl())) {
//    			try {
//    				on(event, event.getData().unpack(RegisteredTransportMode.class));
//    			} catch (InvalidProtocolBufferException e) {
//    				throw new RuntimeException();
//    			}
//    			return;
//    		}
//    		 
//    	}
//    	
//    	protected abstract void on(Event event, RegisteredTransportMode payload);
//    	
//    }

    
}
