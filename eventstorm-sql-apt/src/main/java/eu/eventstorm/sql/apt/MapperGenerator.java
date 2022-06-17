package eu.eventstorm.sql.apt;

import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.annotation.AutoIncrement;
import eu.eventstorm.sql.annotation.Column;
import eu.eventstorm.sql.annotation.ColumnFormat;
import eu.eventstorm.sql.annotation.PrimaryKey;
import eu.eventstorm.sql.apt.log.Logger;
import eu.eventstorm.sql.apt.log.LoggerFactory;
import eu.eventstorm.sql.apt.model.PojoDescriptor;
import eu.eventstorm.sql.apt.model.PojoPropertyDescriptor;
import eu.eventstorm.sql.jdbc.Mapper;
import eu.eventstorm.sql.jdbc.MapperWithAutoIncrement;
import eu.eventstorm.sql.type.Json;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class MapperGenerator implements Generator {

    private static Logger logger;

	MapperGenerator() {
		logger = LoggerFactory.getInstance().getLogger(MapperGenerator.class);
	}

    @Override
    public void generate(ProcessingEnvironment processingEnvironment, SourceCode sourceCode) {

        // generate Implementation class;
        sourceCode.forEach(t -> {
            try {
                generate(processingEnvironment, t);
            } catch (Exception cause) {
                logger.error("MapperGenerator -> IOException for [" + t + "] -> [" + cause.getMessage() + "]", cause);
            }
        });


    }

    private void generate(ProcessingEnvironment env, PojoDescriptor descriptor) throws IOException {
        JavaFileObject object = env.getFiler().createSourceFile(descriptor.fullyQualidiedClassName() + "Mapper");
        Writer writer = object.openWriter();

        writeHeader(writer, env, descriptor);
        writeConstructor(writer, descriptor);
        writeMap(writer, descriptor);
        writeInsert(writer, descriptor, env);
        writeUpdate(writer, descriptor);

        writeSetId(writer, descriptor);


        Helper.writeNewLine(writer);
        writer.write("}");
        writer.close();
    }

    private static void writeHeader(Writer writer, ProcessingEnvironment env, PojoDescriptor descriptor) throws IOException {

        Helper.writePackage(writer, env.getElementUtils().getPackageOf(descriptor.element()).toString());
        Helper.writeNewLine(writer);

        Helper.writeGenerated(writer, MapperGenerator.class.getName());

        writer.write("final class ");
        writer.write(descriptor.simpleName() + "Mapper");
        writer.write(" implements ");

        if (Helper.hasAutoIncrementPK(descriptor)) {
            writer.write(MapperWithAutoIncrement.class.getName());
        } else {
            writer.write(Mapper.class.getName());
        }

        writer.write("<");
        writer.write(descriptor.element().getSimpleName().toString());
        writer.write(">");
        writer.write(" {");
        Helper.writeNewLine(writer);
    }

    private static void writeConstructor(Writer writer, PojoDescriptor descriptor) throws IOException {
        Helper.writeNewLine(writer);
        writer.write("    ");
        writer.write(descriptor.simpleName() + "Mapper() {");
        Helper.writeNewLine(writer);
        writer.write("    }");
        Helper.writeNewLine(writer);
    }

    private static void writeMap(Writer writer, PojoDescriptor descriptor) throws IOException {
        Helper.writeNewLine(writer);
        writer.write("    public ");
        writer.write(descriptor.element().getSimpleName().toString());
        writer.write(" map(");
        writer.write(Dialect.class.getName());
        writer.write(" dialect, ");
        writer.write(ResultSet.class.getName());
        writer.write(" rs) throws ");
        writer.write(SQLException.class.getName());
        writer.write(" {");

        Helper.writeNewLine(writer);
        writer.write("        ");
        writer.write(descriptor.element().getSimpleName().toString());
        writer.write(" pojo = new ");
        writer.write(descriptor.element().getSimpleName().toString() + "Impl();");
        Helper.writeNewLine(writer);

        int index = 1;

        for (PojoPropertyDescriptor ppd : descriptor.ids()) {
            writer.write("        pojo.");
            writer.write(ppd.setter().getSimpleName().toString());
            writer.write("(rs.");
            writer.write(Helper.preparedStatementGetter(ppd.getter().getReturnType().toString()));
            writer.write("(");
            writer.write("" + index++);
            writer.write("));");
            Helper.writeNewLine(writer);
        }

        for (PojoPropertyDescriptor ppd : descriptor.properties()) {

            if (Helper.isArray(ppd.getter().getReturnType().toString())) {
                writer.write("        java.sql.Array array" + index + " = rs."
                        + Helper.preparedStatementGetter(ppd.getter().getReturnType().toString()) + "(" + index + ");");
                Helper.writeNewLine(writer);
                writer.write("        pojo." + ppd.setter().getSimpleName().toString() + "(array" + index
                        + " == null ? null : (" + ppd.getter().getReturnType().toString() + ")array" + index
                        + ".getArray());");
                Helper.writeNewLine(writer);
                index++;

            } else if (ppd.getter().getReturnType().toString().equals("java.sql.Blob")) {

                writer.write("        pojo.");
                writer.write(ppd.setter().getSimpleName().toString());
                writer.write("(");

                String method = Helper.isDialectType(ppd.getter().getReturnType().toString());
                if (method != null) {
                    writer.write("dialect.");
                    writer.write(method);
                    writer.write("(rs,");
                    writer.write("" + index++);
                    writer.write("));");
                } else {
                    writer.write("new javax.sql.rowset.serial.SerialBlob(rs.");
                    writer.write(Helper.preparedStatementGetter(ppd.getter().getReturnType().toString()));
                    writer.write("(");
                    writer.write("" + index++);
                    writer.write(")));");
                }
                Helper.writeNewLine(writer);
                if (ppd.getter().getAnnotation(Column.class).nullable()
                        && !isAPrimitive(ppd.getter().getReturnType().toString())) {
                    writer.write("        if (rs.wasNull()) {");
                    Helper.writeNewLine(writer);
                    writer.write("            pojo.");
                    writer.write(ppd.setter().getSimpleName().toString());
                    writer.write("(null);");
                    Helper.writeNewLine(writer);
                    writer.write("        }");
                    Helper.writeNewLine(writer);
                }
            } else {
                writer.write("        pojo.");
                writer.write(ppd.setter().getSimpleName().toString());
                writer.write("(");

                String method = Helper.isDialectType(ppd.getter().getReturnType().toString());
                if (method != null) {
                    writer.write("dialect.");
                    writer.write(method);
                    writer.write("(rs,");
                    writer.write("" + index++);
                    writer.write("));");
                } else {
                    writer.write("rs.");
                    writer.write(Helper.preparedStatementGetter(ppd.getter().getReturnType().toString()));
                    writer.write("(");
                    writer.write("" + index++);
                    writer.write("));");
                }
                Helper.writeNewLine(writer);
                if (ppd.getter().getAnnotation(Column.class).nullable()
                        && !isAPrimitive(ppd.getter().getReturnType().toString())) {
                    writer.write("        if (rs.wasNull()) {");
                    Helper.writeNewLine(writer);
                    writer.write("            pojo.");
                    writer.write(ppd.setter().getSimpleName().toString());
                    writer.write("(null);");
                    Helper.writeNewLine(writer);
                    writer.write("        }");
                    Helper.writeNewLine(writer);
                }
            }
        }

        writer.write("        return pojo;");

        Helper.writeNewLine(writer);
        writer.write("    }");
        Helper.writeNewLine(writer);

    }

    private static void writeInsert(Writer writer, PojoDescriptor descriptor, ProcessingEnvironment env) throws IOException {

        Helper.writeNewLine(writer);
        writer.write("    public void insert(");
        writer.write(Dialect.class.getName());
        writer.write(" dialect, ");
        writer.write(PreparedStatement.class.getName());
        writer.write(" ps, ");
        writer.write(descriptor.element().getSimpleName().toString());
        writer.write(" pojo) throws ");
        writer.write(SQLException.class.getName());
        writer.write(" {");
        Helper.writeNewLine(writer);

        int index = 1;

        for (PojoPropertyDescriptor ppd : descriptor.ids()) {
            if (ppd.getter().getAnnotation(AutoIncrement.class) != null) {
                writer.write("        // Primary Key [");
                writer.write(ppd.name());
                writer.write("] is auto increment -> skip");
                Helper.writeNewLine(writer);
                continue;
            }
            writePsProperty(writer, ppd, index++);
            Helper.writeNewLine(writer);
        }

        for (PojoPropertyDescriptor ppd : descriptor.properties()) {
            if (ppd.getter().getAnnotation(Column.class).insertable()) {
                writePsProperty(writer, ppd, index++);
                Helper.writeNewLine(writer);
            }
        }

        writer.write("    }");
        Helper.writeNewLine(writer);
    }

    private static void writeUpdate(Writer writer, PojoDescriptor descriptor) throws IOException {

        Helper.writeNewLine(writer);
        writer.write("    public void update(");
        writer.write(Dialect.class.getName());
        writer.write(" dialect, ");
        writer.write(PreparedStatement.class.getName());
        writer.write(" ps, ");
        writer.write(descriptor.element().getSimpleName().toString());
        writer.write(" pojo) throws ");
        writer.write(SQLException.class.getName());
        writer.write(" {");
        Helper.writeNewLine(writer);

        int index = 1;

        for (PojoPropertyDescriptor ppd : descriptor.properties()) {
            if (ppd.getter().getAnnotation(Column.class).updatable()) {
                writePsProperty(writer, ppd, index++);
                Helper.writeNewLine(writer);
            }
        }

        // for the where
        writer.write("        //set primary key");
        Helper.writeNewLine(writer);
        for (PojoPropertyDescriptor ppd : descriptor.ids()) {
            writePsProperty(writer, ppd, index++);
            Helper.writeNewLine(writer);
        }

        Helper.writeNewLine(writer);
        writer.write("    }");
        Helper.writeNewLine(writer);
    }

    private static void writePsProperty(Writer writer, PojoPropertyDescriptor ppd, int index) throws IOException {

        Column column = ppd.getter().getAnnotation(Column.class);
        PrimaryKey primaryKey = ppd.getter().getAnnotation(PrimaryKey.class);
        String type = ppd.getter().getReturnType().toString();

        if (column != null && column.nullable()) {
            if (!"eu.eventstorm.sql.type.Json".equals(type)) {
                writer.write("        if (pojo.");
                writer.write(ppd.getter().getSimpleName().toString());
                writer.write("() != null) {");
                Helper.writeNewLine(writer);
                writer.write("    ");
            }
        }

    	if (("java.sql.Blob".equals(type)) ||
    			("java.sql.Clob".equals(type)) ||
    			(Json.class.getName().equals(type)) ||
                (String.class.getName().equals(type) &&
                        (column != null && ColumnFormat.UUID.equals(column.format())) || (primaryKey != null && ColumnFormat.UUID.equals(primaryKey.format())))
        ){
    		writer.write("        dialect.setPreparedStatement(ps, " + index+", pojo." );
			writer.write(ppd.getter().getSimpleName().toString());
	        writer.write("());");
	        Helper.writeNewLine(writer);
    	} else {
    		writer.write("        ps.");
            writer.write(Helper.preparedStatementSetter(ppd.getter().getReturnType().toString()));
            writer.write("(");
            writer.write("" + index);
            writer.write(", ");
            writer.write(" pojo.");
            writer.write(ppd.getter().getSimpleName().toString());
            writer.write("());");	
    	}
		
        if (column != null && column.nullable()) {
            if (!"eu.eventstorm.sql.type.Json".equals(type)) {
                Helper.writeNewLine(writer);
                writer.write("        } else {");
                Helper.writeNewLine(writer);
                writer.write("            ps.setNull(");
                writer.write("" + index);
                writer.write(", ");
                if ("java.lang.Boolean".equals(type)) {
                    writer.write("dialect.getBooleanType()");
                } else if  (String.class.getName().equals(type) && ColumnFormat.UUID.equals(column.format())) {
                    writer.write("dialect.getUuidType()");
                } else {
                    writer.write(Helper.nullableType(type));
                }
                writer.write(");");
                Helper.writeNewLine(writer);
                writer.write("        }");
            }
        }
    }


    private static void writeSetId(Writer writer, PojoDescriptor descriptor) throws IOException {

        if (!Helper.hasAutoIncrementPK(descriptor)) {
            return;
        }

        int index = 1;

        for (PojoPropertyDescriptor ppd : descriptor.ids()) {

            if (ppd.getter().getAnnotation(AutoIncrement.class) == null) {
                continue;
            }

            writer.write("    public void setId(");
            writer.write(descriptor.element().getSimpleName().toString());
            writer.write(" pojo, ");
            writer.write(ResultSet.class.getName());
            writer.write(" rs) throws ");
            writer.write(SQLException.class.getName());
            writer.write(" {");
            Helper.writeNewLine(writer);
            writer.write("        pojo.");
            writer.write(ppd.setter().getSimpleName().toString());
            writer.write("(rs.");
            writer.write(Helper.preparedStatementGetter(ppd.getter().getReturnType().toString()));
            writer.write("(" + index++ + "));");
            Helper.writeNewLine(writer);
        }

        writer.write("    }");
        Helper.writeNewLine(writer);

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
