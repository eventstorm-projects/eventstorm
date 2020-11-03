package eu.eventsotrm.sql.apt;

import static eu.eventsotrm.sql.apt.Helper.toUpperCase;
import static eu.eventsotrm.sql.apt.Helper.writeGenerated;
import static eu.eventsotrm.sql.apt.Helper.writeNewLine;
import static eu.eventsotrm.sql.apt.Helper.writePackage;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;

import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;
import eu.eventsotrm.sql.apt.model.ViewDescriptor;
import eu.eventstorm.sql.jdbc.ResultSetMapper;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class ViewMapperFactoryGenerator implements Generator {

    private final Logger logger;

	public ViewMapperFactoryGenerator() {
		logger = LoggerFactory.getInstance().getLogger(ViewMapperFactoryGenerator.class);
	}

    public void generate(ProcessingEnvironment env, SourceCode sourceCode) {
        sourceCode.forEachViewByPackage((pack, descriptors) -> {
            try {
                create(env, pack, descriptors);
            } catch (Exception cause) {
                logger.error("ViewMapperFactoryGenerator -> Exception for [" + pack + "] -> [" + cause.getMessage() + "]", cause);
            }
        });
    }

    public void create(ProcessingEnvironment env, String pack, List<ViewDescriptor> descriptors) throws IOException {

        JavaFileObject object = env.getFiler().createSourceFile(pack + ".Mappers");
        Writer writer = object.openWriter();

        writePackage(writer, pack);
        writeGenerated(writer, ViewMapperFactoryGenerator.class.getName());

        writer.write("public final class Mappers {");
        writeNewLine(writer);

        for (ViewDescriptor desc : descriptors) {
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
