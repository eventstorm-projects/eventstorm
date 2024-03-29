package eu.eventstorm.core.apt.query.db;

import eu.eventstorm.annotation.CqrsQueryDatabaseProperty;
import eu.eventstorm.core.apt.SourceCode;
import eu.eventstorm.core.apt.model.QueryDescriptor;
import eu.eventstorm.core.apt.model.QueryPropertyDescriptor;
import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.apt.Helper;
import eu.eventstorm.sql.apt.log.Logger;
import eu.eventstorm.sql.jdbc.ResultSetMapper;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import static eu.eventstorm.sql.apt.Helper.preparedStatementGetter;
import static eu.eventstorm.sql.apt.Helper.writeGenerated;
import static eu.eventstorm.sql.apt.Helper.writeNewLine;
import static eu.eventstorm.sql.apt.Helper.writePackage;


/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class QueryDatabaseMapperGenerator {

    private Logger logger;

    public void generate(ProcessingEnvironment env, SourceCode sourceCode) {

        try (Logger logger = Logger.getLogger(env, "eu.eventstorm.event.query.db", "QueryDatabaseMapperGenerator")) {
            this.logger = logger;


            sourceCode.forEachDatabaseViewQuery((descriptor) -> {
                try {
                    doGenerate(env, descriptor);
                } catch (Exception cause) {
                    logger.error("QueryDatabaseDescriptorGenerator -> Exception for [" + descriptor + "] -> [" + cause.getMessage() + "]", cause);
                }
            });
        }


    }

    private void doGenerate(ProcessingEnvironment env, QueryDescriptor descriptor) throws IOException {

        // check due to "org.aspectj.org.eclipse.jdt.internal.compiler.apt.dispatch.BatchFilerImpl.createSourceFile(BatchFilerImpl.java:149)"
        if (env.getElementUtils().getTypeElement(descriptor.fullyQualidiedClassName() + "Mapper") != null) {
            logger.info("Java SourceCode already exist [" + descriptor.fullyQualidiedClassName() + "Mapper" + "]");
            return;
        }
        JavaFileObject object = env.getFiler().createSourceFile(descriptor.fullyQualidiedClassName() + "Mapper");
        Writer writer = object.openWriter();

        writeHeader(writer, env, descriptor);
        writeVariables(writer, descriptor);
        writeConstructor(writer, descriptor);
        writeMap(writer, descriptor);

        writeNewLine(writer);
        writer.write("}");
        writer.close();
    }


    private static void writeHeader(Writer writer, ProcessingEnvironment env, QueryDescriptor descriptor) throws IOException {

        writePackage(writer, env.getElementUtils().getPackageOf(descriptor.element()).toString());
        writeNewLine(writer);

        writeGenerated(writer, QueryDatabaseMapperGenerator.class.getName());

        writer.write("final class ");
        writer.write(descriptor.simpleName() + "Mapper");
        writer.write(" implements ");
        writer.write(ResultSetMapper.class.getName());
        writer.write("<");
        writer.write(descriptor.element().getSimpleName().toString());
        writer.write(">");
        writer.write(" {");
        writeNewLine(writer);
    }

    private static void writeConstructor(Writer writer, QueryDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writer.write("    ");
        writer.write(descriptor.simpleName() + "Mapper() {");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
    }

    private void writeVariables(Writer writer, QueryDescriptor descriptor) throws IOException {

        for (QueryPropertyDescriptor qpd : descriptor.properties()) {
            if (OffsetDateTime.class.getName().equals(qpd.getter().getReturnType().toString())) {
                writeNewLine(writer);
                writer.write("    private static final java.time.ZoneId UTC = java.time.ZoneId.of(\"UTC\");");
                writeNewLine(writer);
                break;
            }
        }

    }

    private static void writeMap(Writer writer, QueryDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writer.write("    public ");
        writer.write(descriptor.element().getSimpleName().toString());
        writer.write(" map(");
        writer.write(Dialect.class.getName());
        writer.write(" dialect, ");
        writer.write(ResultSet.class.getName());
        writer.write(" rs) throws ");
        writer.write(SQLException.class.getName());
        writer.write(" {");

        writeNewLine(writer);
        writer.write("        ");
        writer.write(descriptor.element().getSimpleName().toString() + "Builder");
        writer.write(" builder = new ");
        writer.write(descriptor.element().getSimpleName().toString() + "Builder();");
        writeNewLine(writer);

        int index = 1;

        for (QueryPropertyDescriptor vpd : descriptor.properties()) {

            CqrsQueryDatabaseProperty cqrsProperty = vpd.getter().getAnnotation(CqrsQueryDatabaseProperty.class);

            if (Helper.isArray(vpd.getter().getReturnType().toString())) {
//                writer.write("        java.sql.Array array" + index + " = rs."
//                        + preparedStatementGetter(ppd.getter().getReturnType().toString()) + "(" + index + ");");
//                writeNewLine(writer);
//                writer.write("        pojo." + ppd.setter().getSimpleName().toString() + "(array" + index
//                        + " == null ? null : (" + ppd.getter().getReturnType().toString() + ")array" + index
//                        + ".getArray());");
//                writeNewLine(writer);
//                index++;

            } else if (vpd.getter().getReturnType().toString().equals("java.sql.Blob")) {

//                writer.write("        builder.");
//                writer.write(vpd.name());
//                writer.write(".with");
//
//                String method = Helper.isDialectType(vpd.getter().getReturnType().toString());
//                if (method != null) {
//                    writer.write("dialect.");
//                    writer.write(method);
//                    writer.write("(rs,");
//                    writer.write("" + index++);
//                    writer.write(");");
//                } else {
//                    writer.write("new javax.sql.rowset.serial.SerialBlob(rs.");
//                    writer.write(preparedStatementGetter(vpd.getter().getReturnType().toString()));
//                    writer.write("(");
//                    writer.write("" + index++);
//                    writer.write("));");
//                }
//                writeNewLine(writer);
//                if (cqrsProperty.column().nullable()
//                        && isLinkToPrimitive(vpd.getter().getReturnType().toString())) {
//                    writer.write("        if (rs.wasNull()) {");
//                    writeNewLine(writer);
//                    writer.write("            pojo = null;");
//                    writeNewLine(writer);
//                    writer.write("        }");
//                    writeNewLine(writer);
//                }
            } else if (OffsetDateTime.class.getName().equals(vpd.getter().getReturnType().toString())) {
                int i = index++;
                writer.write("        java.sql.Timestamp tmp" + i + " = rs.getTimestamp(" + i + ");");
                writeNewLine(writer);
                writer.write("        if (tmp" + i + " != null) {");
                writeNewLine(writer);
                writer.write("            builder.with");
                writer.write(Helper.firstToUpperCase(vpd.name()));
                writer.write("(" + OffsetDateTime.class.getName() + ".ofInstant(tmp" + i + ".toInstant(), UTC));");
                writeNewLine(writer);
                writer.write("        }");
                writeNewLine(writer);
            } else if (LocalDate.class.getName().equals(vpd.getter().getReturnType().toString())) {
                int i = index++;
                writer.write("        java.sql.Date tmp" + i + " = rs.getDate(" + i + ");");
                writeNewLine(writer);
                writer.write("        if (tmp" + i + " != null) {");
                writeNewLine(writer);
                writer.write("            builder.with");
                writer.write(Helper.firstToUpperCase(vpd.name()));
                writer.write("(tmp" + i + ".toLocalDate());");
                writeNewLine(writer);
                writer.write("        }");
                writeNewLine(writer);
            } else {

                writer.write("        builder.with");
                writer.write(Helper.firstToUpperCase(vpd.name()));
                writer.write("(");

                String method = Helper.isDialectType(vpd.getter().getReturnType().toString());
                if (method != null) {
                    writer.write("dialect.");
                    writer.write(method);
                    writer.write("(rs,");
                    writer.write("" + index++);
                    writer.write("));");
                } else {
                    writer.write("rs.");
                    writer.write(preparedStatementGetter(vpd.getter().getReturnType().toString()));
                    writer.write("(");
                    writer.write("" + index++);
                    writer.write("));");
                }
                writeNewLine(writer);
                if (cqrsProperty.column().nullable() && !isAPrimitive(vpd.getter().getReturnType().toString())) {
                    writer.write("        if (rs.wasNull()) {");
                    writeNewLine(writer);
                    writer.write("        builder.with");
                    writer.write(Helper.firstToUpperCase(vpd.name()));
                    writer.write("(null);");
                    writeNewLine(writer);
                    writer.write("        }");
                    writeNewLine(writer);
                }
            }
        }

        writer.write("        return  builder.build();");

        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);

    }

    private static boolean isAPrimitive(String type) {

        return ("int".equals(type) ||
                "long".equals(type) ||
                "short".equals(type) ||
                "byte".equals(type) ||
                "boolean".equals(type) ||
                "double".equals(type) ||
                "float".equals(type));

    }


}
