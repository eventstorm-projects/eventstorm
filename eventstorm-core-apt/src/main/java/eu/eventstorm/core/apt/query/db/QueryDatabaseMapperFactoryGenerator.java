package eu.eventstorm.core.apt.query.db;

import static eu.eventstorm.sql.apt.Helper.toUpperCase;
import static eu.eventstorm.sql.apt.Helper.writeGenerated;
import static eu.eventstorm.sql.apt.Helper.writeNewLine;
import static eu.eventstorm.sql.apt.Helper.writePackage;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;

import eu.eventstorm.core.apt.SourceCode;
import eu.eventstorm.core.apt.model.DatabaseViewQueryDescriptor;
import eu.eventstorm.core.apt.model.QueryDescriptor;
import eu.eventstorm.sql.apt.log.Logger;
import eu.eventstorm.sql.apt.log.LoggerFactory;
import eu.eventstorm.annotation.CqrsQueryDatabaseView;
import eu.eventstorm.sql.jdbc.ResultSetMapper;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class QueryDatabaseMapperFactoryGenerator {

    private final Logger logger;

	public QueryDatabaseMapperFactoryGenerator() {
		logger = LoggerFactory.getInstance().getLogger(QueryDatabaseMapperFactoryGenerator.class);
	}

    public void generate(ProcessingEnvironment env, SourceCode sourceCode) {
    	
    	sourceCode.forEachDatabaseViewQueryPackage((package_, list) -> {
    		if (list.stream().filter(desc -> desc.element().getAnnotation(CqrsQueryDatabaseView.class) != null).findFirst().isPresent()) {
    			 try {
                     create(env, package_, list);
                 } catch (Exception cause) {
                     logger.error("ViewMapperFactoryGenerator -> Exception for [" + package_ + "] -> [" + cause.getMessage() + "]", cause);
                 }
    		} else {
    			logger.info("no CqrsQueryDatabaseView in the package [" + package_ + "]");
    		}
		});
    }

    private void create(ProcessingEnvironment env, String pack, List<DatabaseViewQueryDescriptor> descriptors) throws IOException {

        // check due to "org.aspectj.org.eclipse.jdt.internal.compiler.apt.dispatch.BatchFilerImpl.createSourceFile(BatchFilerImpl.java:149)"
        if (env.getElementUtils().getTypeElement(pack + ".QueryViewMappers") != null) {
            logger.info("Java SourceCode already exist [" + pack + ".QueryViewMappers" + "]");
            return;
        }
        
        JavaFileObject object = env.getFiler().createSourceFile(pack + ".QueryViewMappers");
        Writer writer = object.openWriter();

        writePackage(writer, pack);
        writeGenerated(writer, QueryDatabaseMapperFactoryGenerator.class.getName());

        writer.write("public final class QueryViewMappers {");
        writeNewLine(writer);

        for (QueryDescriptor desc : descriptors) {
        	
        	if (desc.element().getAnnotation(CqrsQueryDatabaseView.class) == null) {
        		continue;
        	}
        	
            writeNewLine(writer);
            writer.write("    public static final ");
            writer.write(ResultSetMapper.class.getName());
            writer.write("<");
            writer.write(desc.element().toString());
            writer.write("> ");

            writer.write(toUpperCase(desc.element().getSimpleName().toString()));

            writer.write(" = new ");
            writer.write(desc.element().getSimpleName().toString());
            writer.write("Mapper();");
            writeNewLine(writer);

        }

        writeNewLine(writer);
        writer.write('}');
        writer.close();
    }

}
