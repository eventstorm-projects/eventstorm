package eu.eventstorm.sql.apt;

import static eu.eventstorm.sql.apt.Helper.preparedStatementGetter;
import static eu.eventstorm.sql.apt.Helper.writeGenerated;
import static eu.eventstorm.sql.apt.Helper.writeNewLine;
import static eu.eventstorm.sql.apt.Helper.writePackage;

import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;

import eu.eventstorm.sql.apt.log.Logger;
import eu.eventstorm.sql.apt.log.LoggerFactory;
import eu.eventstorm.sql.apt.model.ViewDescriptor;
import eu.eventstorm.sql.apt.model.ViewPropertyDescriptor;
import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.annotation.ViewColumn;
import eu.eventstorm.sql.jdbc.ResultSetMapper;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class ViewMapperGenerator implements Generator {

    private final Logger logger;

	ViewMapperGenerator() {
		logger = LoggerFactory.getInstance().getLogger(ViewMapperGenerator.class);
	}

    @Override
    public void generate(ProcessingEnvironment processingEnvironment, SourceCode sourceCode) {

        sourceCode.forEachView(t -> {
            try {
                generate(processingEnvironment, t);
            } catch (Exception cause) {
                logger.error("ViewMapperGenerator -> Exception for [" + t + "] -> [" + cause.getMessage() + "]", cause);
            }
        });


    }

    private void generate(ProcessingEnvironment env, ViewDescriptor descriptor) throws IOException {
        JavaFileObject object = env.getFiler().createSourceFile(descriptor.fullyQualidiedClassName() + "Mapper");
        Writer writer = object.openWriter();

        writeHeader(writer, env, descriptor);
        writeConstructor(writer, descriptor);
        writeMap(writer, descriptor);

        writeNewLine(writer);
        writer.write("}");
        writer.close();
    }

    private static void writeHeader(Writer writer, ProcessingEnvironment env, ViewDescriptor descriptor) throws IOException {

        writePackage(writer, env.getElementUtils().getPackageOf(descriptor.element()).toString());
        writeNewLine(writer);

        writeGenerated(writer, ViewMapperGenerator.class.getName());

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

    private static void writeConstructor(Writer writer, ViewDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writer.write("    ");
        writer.write(descriptor.simpleName() + "Mapper() {");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
    }

    private static void writeMap(Writer writer, ViewDescriptor descriptor) throws IOException {
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
        writer.write(descriptor.element().getSimpleName().toString()+ "Impl");
        writer.write(" pojo = new ");
        writer.write(descriptor.element().getSimpleName().toString() + "Impl();");
        writeNewLine(writer);

        int index = 1;

        for (ViewPropertyDescriptor vpd : descriptor.properties()) {

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

                writer.write("        pojo.");
                writer.write(vpd.name());
                writer.write(" = ");

                String method = Helper.isDialectType(vpd.getter().getReturnType().toString());
                if (method != null) {
                    writer.write("dialect.");
                    writer.write(method);
                    writer.write("(rs,");
                    writer.write("" + index++);
                    writer.write(");");
                } else {
                    writer.write("new javax.sql.rowset.serial.SerialBlob(rs.");
                    writer.write(preparedStatementGetter(vpd.getter().getReturnType().toString()));
                    writer.write("(");
                    writer.write("" + index++);
                    writer.write("));");
                }
                writeNewLine(writer);
                if (vpd.getter().getAnnotation(ViewColumn.class).nullable()
                        && isLinkToPrimitive(vpd.getter().getReturnType().toString())) {
                    writer.write("        if (rs.wasNull()) {");
                    writeNewLine(writer);
                    writer.write("            pojo = null;");
                    writeNewLine(writer);
                    writer.write("        }");
                    writeNewLine(writer);
                }
            } else {
                writer.write("        pojo.");
                writer.write(vpd.name());
                writer.write(" = ");

                String method = Helper.isDialectType(vpd.getter().getReturnType().toString());
                if (method != null) {
                    writer.write("dialect.");
                    writer.write(method);
                    writer.write("(rs,");
                    writer.write("" + index++);
                    writer.write(");");
                } else {
                    writer.write("rs.");
                    writer.write(preparedStatementGetter(vpd.getter().getReturnType().toString()));
                    writer.write("(");
                    writer.write("" + index++);
                    writer.write(");");
                }
                writeNewLine(writer);
                if (vpd.getter().getAnnotation(ViewColumn.class).nullable()
                        && isLinkToPrimitive(vpd.getter().getReturnType().toString())) {
                    writer.write("        if (rs.wasNull()) {");
                    writeNewLine(writer);
                    writer.write("            pojo = null;");
                    writeNewLine(writer);
                    writer.write("        }");
                    writeNewLine(writer);
                }
            }
        }

        writer.write("        return pojo;");

        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);

    }

    private static boolean isLinkToPrimitive(String type) {

        return ("java.lang.Integer".equals(type) ||
                "java.lang.Long".equals(type) ||
                "java.lang.Short".equals(type) ||
                "java.lang.Byte".equals(type) ||
                "java.lang.Double".equals(type) ||
                "java.lang.Float".equals(type));

    }


}
