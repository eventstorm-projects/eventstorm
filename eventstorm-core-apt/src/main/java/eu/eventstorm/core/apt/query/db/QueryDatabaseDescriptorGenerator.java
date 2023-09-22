package eu.eventstorm.core.apt.query.db;

import com.google.common.collect.ImmutableList;
import eu.eventstorm.annotation.CqrsQueryDatabaseProperty;
import eu.eventstorm.annotation.CqrsQueryDatabaseView;
import eu.eventstorm.core.apt.SourceCode;
import eu.eventstorm.core.apt.model.QueryDescriptor;
import eu.eventstorm.core.apt.model.QueryPropertyDescriptor;
import eu.eventstorm.sql.Descriptor;
import eu.eventstorm.sql.annotation.ViewColumn;
import eu.eventstorm.sql.apt.log.Logger;
import eu.eventstorm.sql.apt.util.AliasGenerator;
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
public final class QueryDatabaseDescriptorGenerator {

    private Logger logger;

    public void generate(ProcessingEnvironment processingEnvironment, SourceCode sourceCode) {

        try (Logger logger = Logger.getLogger(processingEnvironment, "eu.eventstorm.event.query.db", "QueryDatabaseDescriptorGenerator")) {
            this.logger = logger;
            sourceCode.forEachDatabaseViewQuery(t -> {
                try {
                    doGenerate(processingEnvironment, t);
                } catch (Exception cause) {
                    logger.error("Exception for [" + t + "] -> [" + cause.getMessage() + "]", cause);
                }
            });
        }

    }

    private void doGenerate(ProcessingEnvironment env, QueryDescriptor descriptor)
            throws IOException {

        // check due to "org.aspectj.org.eclipse.jdt.internal.compiler.apt.dispatch.BatchFilerImpl.createSourceFile(BatchFilerImpl.java:149)"
        if (env.getElementUtils().getTypeElement(descriptor.fullyQualidiedClassName() + "Descriptor") != null) {
            logger.info("Java SourceCode already exist [" + descriptor.fullyQualidiedClassName() + "Descriptor" + "]");
            return;
        }

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

    private static void writeHeader(Writer writer, ProcessingEnvironment env, QueryDescriptor descriptor)
            throws IOException {
        writePackage(writer, env.getElementUtils().getPackageOf(descriptor.element()).toString());
        writeGenerated(writer, QueryDatabaseDescriptorGenerator.class.getName());

        writer.write("public final class ");
        writer.write(descriptor.simpleName() + "Descriptor implements ");
        writer.write(Descriptor.class.getName());
        writer.write(" {");
        writeNewLine(writer);
    }

    private static void writeSingleton(Writer writer, QueryDescriptor descriptor) throws IOException {

        writeNewLine(writer);
        writer.write("    static final ");
        writer.write(Descriptor.class.getName());
        writer.write(" INSTANCE = new ");
        writer.write(descriptor.simpleName() + "Descriptor();");
        writeNewLine(writer);

    }

    private static void writeConstructor(Writer writer, QueryDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writer.write("    private ");
        writer.write(descriptor.simpleName() + "Descriptor");
        writer.write(" () {");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
    }

    private static void writeTable(Writer writer, QueryDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writer.write("    // SQL VIEW DESCRIPTOR");
        writeNewLine(writer);
        writer.write("    public static final ");
        writer.write(SqlTable.class.getName());
        writer.write(" VIEW = new ");
        writer.write(SqlTable.class.getName());
        writer.write("(\"");
        writer.write(descriptor.element().getAnnotation(CqrsQueryDatabaseView.class).view().value());
        writer.write("\", \"");
        writer.write(AliasGenerator.generate());
        writer.write("\");");
        writeNewLine(writer);
    }

    private static void writeProperties(Writer writer, QueryDescriptor descriptor) throws IOException {

        writeNewLine(writer);
        writer.write("    // SQL PROPERTIES");
        writeNewLine(writer);

        for (QueryPropertyDescriptor ppd : descriptor.properties()) {
            ViewColumn column = ppd.getter().getAnnotation(CqrsQueryDatabaseProperty.class).column();
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

    private static void writeAllColumns(Writer writer, QueryDescriptor descriptor) throws IOException {

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

        for (QueryPropertyDescriptor id : descriptor.properties()) {
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

    private static void writeMethods(Writer writer, QueryDescriptor descriptor) throws IOException {

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

    private static void writeAllSingleColumns(Writer writer, QueryDescriptor descriptor) throws IOException {

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

        for (QueryPropertyDescriptor id : descriptor.properties()) {
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
