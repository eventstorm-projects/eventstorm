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
import eu.eventstorm.cqrs.PageQueryDescriptor;
import eu.eventstorm.cqrs.PageQueryDescriptors;
import eu.eventstorm.cqrs.web.SqlPageQueryDescriptor;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class PageQueryDescriptorsGenerator {

	private final Logger logger;

	public PageQueryDescriptorsGenerator() {
		logger = LoggerFactory.getInstance().getLogger(PageQueryDescriptorsGenerator.class);
	}

	public void generate(ProcessingEnvironment env, SourceCode sourceCode) {
		// generate Implementation class;
		
		String fcqn = sourceCode.getCqrsConfiguration().basePackage() + ".EventstormPageQueryDescriptors";
		
	    // check due to "org.aspectj.org.eclipse.jdt.internal.compiler.apt.dispatch.BatchFilerImpl.createSourceFile(BatchFilerImpl.java:149)"
        if (env.getElementUtils().getTypeElement(fcqn) != null) {
            logger.info("Java SourceCode already exist [" + fcqn + "]");
            return;
        }
        
        AtomicInteger counter = new AtomicInteger(0);
        sourceCode.forEachDatabaseViewQuery(item -> counter.incrementAndGet());
		sourceCode.forEachDatabaseTableQuery(item -> counter.incrementAndGet());
        
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
		writer.write("import " + PageQueryDescriptor.class.getName() + ";");
		writeNewLine(writer);
		writer.write("import " + PageQueryDescriptors.class.getName() + ";");
		writeNewLine(writer);
		writer.write("import " + SqlPageQueryDescriptor.class.getName() + ";");
		writeNewLine(writer);

		
		writeGenerated(writer, PageQueryDescriptorsGenerator.class.getName());
		writer.write("@SuppressWarnings(\"serial\")");
        writeNewLine(writer);
		writer.write("final class EventstormPageQueryDescriptors implements PageQueryDescriptors {");
		writeNewLine(writer);
	}
	

	private void writeStatic(Writer writer, SourceCode sourceCode) throws IOException {
		
		writeNewLine(writer);
		writer.write("    private static final ImmutableMap<String, PageQueryDescriptor> DESCRIPTORS = ImmutableMap.<String, PageQueryDescriptor>builder() ");
		writeNewLine(writer);
		
		sourceCode.forEachDatabaseViewQuery(query -> {
			try {
				writer.write("        .put(\"" + query.fullyQualidiedClassName() + "\", new SqlPageQueryDescriptor(");
				writer.write("\n            new " + query.fullyQualidiedClassName() + "SqlPageRequestDescriptor()))");
				writeNewLine(writer);
			} catch (IOException cause) {
				logger.error("failed to generate [" + query + "]", cause);
			}
		});
		sourceCode.forEachDatabaseTableQuery(query -> {
			try {
				writer.write("        .put(\"" + query.fullyQualidiedClassName() + "\", new SqlPageQueryDescriptor(");
				writer.write("\n            new " + query.fullyQualidiedClassName() + "SqlPageRequestDescriptor()))");
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
		writer.write("    public PageQueryDescriptor get(String fcqn) {");
		writeNewLine(writer);
		writer.write("        return DESCRIPTORS.get(fcqn);");
		writeNewLine(writer);
		writer.write("    }");
		writeNewLine(writer);
	}

}