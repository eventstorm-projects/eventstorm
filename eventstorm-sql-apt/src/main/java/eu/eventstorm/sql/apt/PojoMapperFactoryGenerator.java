package eu.eventstorm.sql.apt;

import eu.eventstorm.sql.apt.log.Logger;
import eu.eventstorm.sql.apt.model.PojoDescriptor;
import eu.eventstorm.sql.jdbc.Mapper;
import eu.eventstorm.sql.jdbc.MapperWithAutoIncrement;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import static eu.eventstorm.sql.apt.Helper.hasAutoIncrementPK;
import static eu.eventstorm.sql.apt.Helper.toUpperCase;
import static eu.eventstorm.sql.apt.Helper.writeGenerated;
import static eu.eventstorm.sql.apt.Helper.writeNewLine;
import static eu.eventstorm.sql.apt.Helper.writePackage;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class PojoMapperFactoryGenerator implements Generator {

    private Logger logger;

    PojoMapperFactoryGenerator() {
    }

    public void generate(ProcessingEnvironment processingEnv, SourceCode sourceCode) {
        try (Logger l = Logger.getLogger(processingEnv, "eu.eventstorm.sql.generator", "PojoMapperFactoryGenerator")) {
            this.logger = l;
            sourceCode.forEachByPackage((pack, descriptors) -> {
                try {
                    create(processingEnv, pack, descriptors);
                } catch (Exception cause) {
                    logger.error("Exception for [" + pack + "] -> [" + cause.getMessage() + "]", cause);
                }
            });
        }

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
