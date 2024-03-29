package eu.eventstorm.sql.apt;

import com.google.common.collect.ImmutableList;
import eu.eventstorm.sql.Descriptor;
import eu.eventstorm.sql.annotation.View;
import eu.eventstorm.sql.annotation.ViewColumn;
import eu.eventstorm.sql.apt.log.Logger;
import eu.eventstorm.sql.apt.model.ViewDescriptor;
import eu.eventstorm.sql.apt.model.ViewPropertyDescriptor;
import eu.eventstorm.sql.desc.SqlColumn;
import eu.eventstorm.sql.desc.SqlPrimaryKey;
import eu.eventstorm.sql.desc.SqlSingleColumn;
import eu.eventstorm.sql.desc.SqlTable;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;

import static eu.eventstorm.sql.apt.Helper.toUpperCase;
import static eu.eventstorm.sql.apt.Helper.writeGenerated;
import static eu.eventstorm.sql.apt.Helper.writeNewLine;
import static eu.eventstorm.sql.apt.Helper.writePackage;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class ViewDescriptorGenerator implements Generator {

    private Logger logger;

    ViewDescriptorGenerator() {
    }

    public void generate(ProcessingEnvironment processingEnv, SourceCode sourceCode) {

        try (Logger l = Logger.getLogger(processingEnv, "eu.eventstorm.sql.generator", "ViewDescriptorGenerator")) {
            this.logger = l;
            sourceCode.forEachView(t -> {
                try {
                    generate(processingEnv, t);
                } catch (Exception cause) {
                    logger.error("ViewDescriptorGenerator -> IOException for [" + t + "] -> [" + cause.getMessage() + "]", cause);
                }
            });
        }

    }

    private void generate(ProcessingEnvironment env, ViewDescriptor descriptor)
            throws IOException {

        JavaFileObject object = env.getFiler().createSourceFile(descriptor.fullyQualidiedClassName() + "Descriptor");
        Writer writer = object.openWriter();

        writeHeader(writer, env, descriptor);
        writeSingleton(writer, descriptor);
        writeConstructor(writer, descriptor);
        writeTable(writer, descriptor);
        writeProperties(writer, descriptor);
        writeAllColumns(writer, descriptor);
        writeAllSingleColumns(writer, descriptor);
        writeMethods(writer, descriptor);

        writer.write("}");
        writer.close();
    }

    private static void writeHeader(Writer writer, ProcessingEnvironment env, ViewDescriptor descriptor)
            throws IOException {
        writePackage(writer, env.getElementUtils().getPackageOf(descriptor.element()).toString());
        writeGenerated(writer, ViewDescriptorGenerator.class.getName());

        writer.write("public final class ");
        writer.write(descriptor.simpleName() + "Descriptor implements ");
        writer.write(Descriptor.class.getName());
        writer.write(" {");
        writeNewLine(writer);
    }

    private static void writeSingleton(Writer writer, ViewDescriptor descriptor) throws IOException {

        writeNewLine(writer);
        writer.write("    static final ");
        writer.write(Descriptor.class.getName());
        writer.write(" INSTANCE = new ");
        writer.write(descriptor.simpleName() + "Descriptor();");
        writeNewLine(writer);

    }

    private static void writeConstructor(Writer writer, ViewDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writer.write("    private ");
        writer.write(descriptor.simpleName() + "Descriptor");
        writer.write(" () {");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
    }

    private static void writeTable(Writer writer, ViewDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writer.write("    // SQL VIEW DESCRIPTOR");
        writeNewLine(writer);
        writer.write("    public static final ");
        writer.write(SqlTable.class.getName());
        writer.write(" VIEW = new ");
        writer.write(SqlTable.class.getName());
        writer.write("(\"");
        writer.write(descriptor.element().getAnnotation(View.class).value());
        writer.write("\", \"");
        //writer.write(generateAlias(properties));
        writer.write("\");");
        writeNewLine(writer);
    }

    private static void writeProperties(Writer writer, ViewDescriptor descriptor) throws IOException {

        writeNewLine(writer);
        writer.write("    // SQL PROPERTIES");
        writeNewLine(writer);

        for (ViewPropertyDescriptor ppd : descriptor.properties()) {
            ViewColumn column = ppd.getter().getAnnotation(ViewColumn.class);
            writer.write("    public static final ");
            writer.write(SqlSingleColumn.class.getName());
            writer.write(" ");
            writer.write(toUpperCase(ppd.name()));
            writer.write(" = new ");
            writer.write(SqlSingleColumn.class.getName());
            writer.write("(VIEW, \"");
            writer.write(column.value());
            writer.write("\", true, false, false); ");
            writeNewLine(writer);
        }

    }

    private static void writeAllColumns(Writer writer, ViewDescriptor descriptor) throws IOException {

        writeNewLine(writer);
        writer.write("    // ALL COLUMNS");
        writeNewLine(writer);

        writer.write("    public static final ");
        writer.write(ImmutableList.class.getName());
        writer.write("<");
        writer.write(SqlColumn.class.getName());
        writer.write("> ALL = ");
        writer.write(ImmutableList.class.getName());
        writer.write(".of(");

        StringBuilder builder = new StringBuilder();

        for (ViewPropertyDescriptor id : descriptor.properties()) {
            writeNewLine(builder);
            builder.append("            ");
            builder.append(toUpperCase(id.name()));
            builder.append(',');

        }

        builder.deleteCharAt(builder.length() - 1);

        writer.write(builder.toString());
        writer.write(");");
        writeNewLine(writer);

    }

    private static void writeMethods(Writer writer, ViewDescriptor descriptor) throws IOException {

        writeNewLine(writer);
        writer.write("    public ");
        writer.write(SqlTable.class.getName());
        writer.write(" table() {");
        writeNewLine(writer);
        writer.write("        return VIEW;");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);

        writeNewLine(writer);
        writer.write("    public ");
        writer.write(ImmutableList.class.getName());
        writer.write("<");
        writer.write(SqlSingleColumn.class.getName());
        writer.write("> columns() {");
        writeNewLine(writer);
        writer.write("        return COLUMNS;");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);

        writeNewLine(writer);
        writer.write("    public ");
        writer.write(ImmutableList.class.getName());
        writer.write("<");
        writer.write(SqlPrimaryKey.class.getName());
        writer.write("> ids() {");
        writeNewLine(writer);
        writer.write("        return " + ImmutableList.class.getName() + ".of();");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);

    }

    private static void writeAllSingleColumns(Writer writer, ViewDescriptor descriptor) throws IOException {

        writeNewLine(writer);
        writer.write("    // ALL SINGLE COLUMNS");
        writeNewLine(writer);

        writer.write("    public static final ");
        writer.write(ImmutableList.class.getName());
        writer.write("<");
        writer.write(SqlSingleColumn.class.getName());
        writer.write("> COLUMNS = ");
        writer.write(ImmutableList.class.getName());
        writer.write(".of(");

        StringBuilder builder = new StringBuilder();

        for (ViewPropertyDescriptor id : descriptor.properties()) {
            writeNewLine(builder);
            builder.append("            ");
            builder.append(toUpperCase(id.name()));
            builder.append(',');

        }

        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
            writer.write(builder.toString());
        }

        writer.write(");");
        writeNewLine(writer);

    }

}
