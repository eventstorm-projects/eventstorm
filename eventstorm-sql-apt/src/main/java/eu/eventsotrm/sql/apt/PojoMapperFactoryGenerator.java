package eu.eventsotrm.sql.apt;

import static eu.eventsotrm.sql.apt.Helper.hasAutoIncrementPK;
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
import eu.eventsotrm.sql.apt.model.PojoDescriptor;
import eu.eventstorm.sql.jdbc.Mapper;
import eu.eventstorm.sql.jdbc.MapperWithAutoIncrement;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class PojoMapperFactoryGenerator implements Generator {

    private final Logger logger;

	PojoMapperFactoryGenerator() {
		logger = LoggerFactory.getInstance().getLogger(PojoMapperFactoryGenerator.class);
	}

    public void generate(ProcessingEnvironment env, SourceCode sourceCode) {
        sourceCode.forEachByPackage((pack, descriptors) -> {
            try {
                create(env, pack, descriptors);
            } catch (Exception cause) {
                logger.error("PojoMapperFactoryGenerator -> IOException for [" + pack + "] -> [" + cause.getMessage() + "]", cause);
            }
        });
    }

    private void create(ProcessingEnvironment env, String pack, List<PojoDescriptor> descriptors) throws IOException {

        JavaFileObject object = env.getFiler().createSourceFile(pack + ".Mappers");
        Writer writer = object.openWriter();

        writePackage(writer, pack);
        writeGenerated(writer, PojoMapperFactoryGenerator.class.getName());

        writer.write("public final class Mappers {");
        writeNewLine(writer);

        for (PojoDescriptor desc : descriptors) {
            writeNewLine(writer);
            writer.write("    public static final ");

            if (hasAutoIncrementPK(desc)) {
                writer.write(MapperWithAutoIncrement.class.getName());
            } else {
                writer.write(Mapper.class.getName());
            }

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
