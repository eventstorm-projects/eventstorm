package eu.eventsotrm.core.apt.query;

import static eu.eventsotrm.sql.apt.Helper.writeGenerated;
import static eu.eventsotrm.sql.apt.Helper.writeNewLine;
import static eu.eventsotrm.sql.apt.Helper.writePackage;

import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;

import com.google.common.collect.ImmutableMap;

import eu.eventsotrm.core.apt.SourceCode;
import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;
import eu.eventstorm.cqrs.QueryDescriptors;
import eu.eventstorm.cqrs.SqlQueryDescriptor;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class QueryDescriptorsGenerator {

	private final Logger logger;

	public QueryDescriptorsGenerator() {
		logger = LoggerFactory.getInstance().getLogger(QueryDescriptorsGenerator.class);
	}

	public void generate(ProcessingEnvironment env, SourceCode sourceCode) {
		// generate Implementation class;
		
		String fcqn = sourceCode.getCqrsConfiguration().basePackage() + ".EventstormQueryDescriptors";
		
	    // check due to "org.aspectj.org.eclipse.jdt.internal.compiler.apt.dispatch.BatchFilerImpl.createSourceFile(BatchFilerImpl.java:149)"
        if (env.getElementUtils().getTypeElement(fcqn) != null) {
            logger.info("Java SourceCode already exist [" + fcqn + "]");
            return;
        }
        
        AtomicInteger counter = new AtomicInteger(0);
        sourceCode.forEachDatabaseQuery(item -> {
        	counter.incrementAndGet();
        });
        
        if (counter.get() == 0) {
			logger.info("No Database Queries found => skip");
			return;
		}
        
        try {
        	JavaFileObject object = env.getFiler().createSourceFile(fcqn);
        	Writer writer = object.openWriter();
		
        	writeHeader(writer, sourceCode.getCqrsConfiguration().basePackage());
        	writeStatic(writer, sourceCode);
        	writeMethod(writer);
        	
        	writer.write("}");
			writer.close();
			
			
        } catch (Exception cause) {
        	logger.error("Exception for [" + cause.getMessage() + "]", cause);
        }
		

	}

	private static void writeHeader(Writer writer, String pack) throws IOException {
		writePackage(writer, pack);
		

		writer.write("import " + ImmutableMap.class.getName() + ";");
		writeNewLine(writer);
		writer.write("import " + SqlQueryDescriptor.class.getName() + ";");
		writeNewLine(writer);
		writer.write("import " + QueryDescriptors.class.getName() + ";");
		writeNewLine(writer);
		
		writeGenerated(writer, QueryDescriptorsGenerator.class.getName());
		writer.write("@SuppressWarnings(\"serial\")");
        writeNewLine(writer);
		writer.write("final class EventstormQueryDescriptors implements QueryDescriptors {");
		writeNewLine(writer);
	}
	

	private void writeStatic(Writer writer, SourceCode sourceCode) throws IOException {
		
		writeNewLine(writer);
		writer.write("    private static final ImmutableMap<String, SqlQueryDescriptor> DESCRIPTORS = ImmutableMap.<String, SqlQueryDescriptor>builder() ");
		writeNewLine(writer);
		
		sourceCode.forEachDatabaseQuery(query -> {
			try {
				writer.write("        .put(\"" + query.fullyQualidiedClassName() + "\", new " + query.fullyQualidiedClassName() + "SqlQueryDescriptor())");
				writeNewLine(writer);
			} catch (IOException cause) {
				logger.error("failed to generate [" + query + "]", cause);
			}
		});
		
		writer.write("        .build();");
		writeNewLine(writer);
	}
	
	private void writeMethod(Writer writer) throws IOException {
		writeNewLine(writer);
		writer.write("    @Override");
		writeNewLine(writer);
		writer.write("    public SqlQueryDescriptor getSqlQueryDescriptor(String fcqn) {");
		writeNewLine(writer);
		writer.write("        return DESCRIPTORS.get(fcqn);");
		writeNewLine(writer);
		writer.write("    }");
		writeNewLine(writer);
	}

}