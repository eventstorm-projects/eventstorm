package eu.eventsotrm.core.apt;

import static eu.eventsotrm.sql.apt.Helper.toUpperCase;
import static eu.eventsotrm.sql.apt.Helper.writeGenerated;
import static eu.eventsotrm.sql.apt.Helper.writeNewLine;
import static eu.eventsotrm.sql.apt.Helper.writePackage;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;

import eu.eventsotrm.core.apt.model.QueryDescriptor;
import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;
import eu.eventstorm.core.annotation.CqrsQueryDatabaseView;
import eu.eventstorm.sql.jdbc.ResultSetMapper;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class QueryDatabaseMapperFactoryGenerator {

    private final Logger logger;

	QueryDatabaseMapperFactoryGenerator() {
		logger = LoggerFactory.getInstance().getLogger(QueryDatabaseMapperFactoryGenerator.class);
	}

    public void generate(ProcessingEnvironment env, SourceCode sourceCode) {
    	
    	sourceCode.forEachQueryPackage((package_, list) -> {
    		if (list.stream().filter(desc -> desc.getClass().getAnnotation(CqrsQueryDatabaseView.class) != null).findFirst().isPresent()) {
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

    private void create(ProcessingEnvironment env, String pack, List<QueryDescriptor> descriptors) throws IOException {

        JavaFileObject object = env.getFiler().createSourceFile(pack + ".Mappers");
        Writer writer = object.openWriter();

        writePackage(writer, pack);
        writeGenerated(writer, QueryDatabaseMapperFactoryGenerator.class.getName());

        writer.write("public final class Mappers {");
        writeNewLine(writer);

        for (QueryDescriptor desc : descriptors) {
        	
        	if (desc.getClass().getAnnotation(CqrsQueryDatabaseView.class) == null) {
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
