package eu.eventstorm.core.apt.query;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import eu.eventstorm.core.apt.SourceCode;
import eu.eventstorm.core.apt.model.QueryDescriptor;
import eu.eventstorm.core.apt.model.QueryPropertyDescriptor;
import eu.eventstorm.cqrs.PageQueryDescriptor;
import eu.eventstorm.cqrs.PageQueryDescriptors;
import eu.eventstorm.page.Filter;
import eu.eventstorm.sql.annotation.Column;
import eu.eventstorm.sql.annotation.ColumnFormat;
import eu.eventstorm.sql.apt.Helper;
import eu.eventstorm.sql.apt.log.Logger;
import eu.eventstorm.sql.desc.SqlColumn;
import eu.eventstorm.sql.page.PreparedStatementIndexSetter;
import eu.eventstorm.sql.page.SqlPageRequestDescriptor;
import eu.eventstorm.sql.page.SqlPageRequestDescriptorException;
import eu.eventstorm.sql.type.Json;
import eu.eventstorm.sql.util.Dates;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.function.Function;

import static eu.eventstorm.sql.apt.Helper.toUpperCase;
import static eu.eventstorm.sql.apt.Helper.writeGenerated;
import static eu.eventstorm.sql.apt.Helper.writeNewLine;
import static eu.eventstorm.sql.apt.Helper.writePackage;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class SqlPageRequestDescriptorGenerator {

    private Logger logger;

    public void generate(ProcessingEnvironment processingEnvironment, SourceCode sourceCode) {

        try (Logger logger = Logger.getLogger(processingEnvironment, "eu.eventstorm.event.query", "SqlPageRequestDescriptorGenerator")) {
            this.logger = logger;
            sourceCode.forEachDatabaseViewQueryPackage((pack, list) -> {
                try {
                    generate(processingEnvironment, pack, list);
                } catch (Exception cause) {
                    logger.error("Exception for [" + pack + "] -> [" + cause.getMessage() + "]", cause);
                }
            });
            sourceCode.forEachDatabaseTableQueryPackage((pack, list) -> {
                try {
                    generate(processingEnvironment, pack, list);
                } catch (Exception cause) {
                    logger.error("Exception for [" + pack + "] -> [" + cause.getMessage() + "]", cause);
                }
            });
        }

    }

    private void generate(ProcessingEnvironment env, String pack, ImmutableList<? extends QueryDescriptor> descriptors) throws IOException {

        for (QueryDescriptor ed : descriptors) {

            String fcqn = pack + "." + ed.simpleName() + "SqlPageRequestDescriptor";

            // check due to "org.aspectj.org.eclipse.jdt.internal.compiler.apt.dispatch.BatchFilerImpl.createSourceFile(BatchFilerImpl.java:149)"
            if (env.getElementUtils().getTypeElement(fcqn) != null) {
                logger.info("Java SourceCode already exist [" + fcqn + "]");
                return;
            }

            JavaFileObject object = env.getFiler().createSourceFile(fcqn);
            Writer writer = object.openWriter();

            writeHeader(writer, pack, ed);
            writeStatic(writer, ed);
            writeConstructor(writer, ed);
            writeMethodGet(writer, ed);
            writeMethodExpression(writer, ed);

            writer.write("}");
            writer.close();
        }

    }

    private static void writeHeader(Writer writer, String pack, QueryDescriptor descriptor) throws IOException {
        writePackage(writer, pack);


        writer.write("import " + ImmutableMap.class.getName() + ";");
        writeNewLine(writer);
        writer.write("import " + Function.class.getName() + ";");
        writeNewLine(writer);
        writer.write("import " + SqlColumn.class.getName() + ";");
        writeNewLine(writer);
        writer.write("import " + Filter.class.getName() + ";");
        writeNewLine(writer);
        writer.write("import " + PageQueryDescriptor.class.getName() + ";");
        writeNewLine(writer);
        writer.write("import " + PreparedStatementIndexSetter.class.getName() + ";");
        writeNewLine(writer);
        writer.write("import " + PageQueryDescriptors.class.getName() + ";");
        writeNewLine(writer);
        writer.write("import " + SqlPageRequestDescriptor.class.getName() + ";");
        writeNewLine(writer);
        writer.write("import " + SqlPageRequestDescriptorException.class.getName() + ";");
        writeNewLine(writer);

        writer.write("import " + descriptor.fullyQualidiedClassName() + ";");
        writeNewLine(writer);

        writeGenerated(writer, SqlPageRequestDescriptorGenerator.class.getName());
        writer.write("@SuppressWarnings(\"serial\")");
        writeNewLine(writer);
        writer.write("public final class " + descriptor.simpleName() + "SqlPageRequestDescriptor implements SqlPageRequestDescriptor {");
        writeNewLine(writer);
    }


    private void writeStatic(Writer writer, QueryDescriptor ed) throws IOException {

        writeNewLine(writer);
        writer.write("    private static final ImmutableMap<String, SqlColumn> VALUES = ImmutableMap.<String, SqlColumn>builder() ");
        writeNewLine(writer);
        for (QueryPropertyDescriptor property : ed.properties()) {
            writer.write("        .put(\"" + property.name() + "\", " + ed.fullyQualidiedClassName() + "Descriptor." + toUpperCase(property.name()) + ")");
            writeNewLine(writer);
        }
        writer.write("        .build();");
        writeNewLine(writer);

        writeNewLine(writer);
        writer.write("    private static final ImmutableMap<String, Function<Filter, PreparedStatementIndexSetter>> PREPARED_STATEMENT_INDEX_SETTERS");
        writeNewLine(writer);
        writer.write("        = ImmutableMap.<String, Function<Filter, PreparedStatementIndexSetter>>builder()");
        writeNewLine(writer);

        // private static final Function<String,String> COUNTRY = t -> t;
        for (QueryPropertyDescriptor property : ed.properties()) {
            String type = Helper.getReturnType(property.getter());
            if (Helper.isInteger(type)) {
                writer.write("            .put(\"" + property.name() + "\", filter -> (dialect,ps,index) -> { for (String value : filter.getValues()) { ps.setInt(index++, Integer.parseInt(value));} return index; })");
            } else if (Helper.isLong(type)) {
                writer.write("            .put(\"" + property.name() + "\", filter -> (dialect,ps,index) -> { for (String value : filter.getValues()) { ps.setLong(index++, Long.parseLong(value));} return index; })");
            } else if (Helper.isBoolean(type)) {
                writer.write("            .put(\"" + property.name() + "\", filter -> (dialect,ps,index) -> { for (String value : filter.getValues()) { ps.setBoolean(index++, Boolean.valueOf(value));} return index; })");
            } else if (Helper.isByte(type)) {
                writer.write("            .put(\"" + property.name() + "\", filter -> (dialect,ps,index) -> { for (String value : filter.getValues()) { ps.setByte(index++, Byte.valueOf(value));} return index; })");
            } else if (Helper.isString(type)) {
                Column column = property.getter().getAnnotation(Column.class);
                if (column != null && ColumnFormat.UUID.equals(column.format())) {
                    writer.write("            .put(\"" + property.name() + "\", filter -> (dialect,ps,index) -> { for (String value : filter.getValues()) { dialect.setPreparedStatement(ps,index++, value); } return index; })");
                } else {
                    writer.write("            .put(\"" + property.name() + "\", filter -> (dialect,ps,index) -> { for (String value : filter.getValues()) { ps.setString(index++, value); } return index; })");
                }

            } else if (Date.class.getName().equals(type)) {
                writer.write("            .put(\"" + property.name() + "\", filter -> (dialect,ps,index) -> { for (String value : filter.getValues()) { ps.setDate(index++, " + Dates.class.getName() + ".convertDate(value)); } return index; })");
            } else if (Timestamp.class.getName().equals(type)) {
                writer.write("            .put(\"" + property.name() + "\", filter -> (dialect,ps,index) -> { for (String value : filter.getValues()) { ps.setTimestamp(index++, " + Dates.class.getName() + ".convertTimestamp(value)); } return index; })");
            } else if (Json.class.getName().equals(type)) {
                writer.write("            // json");
            } else {
                writer.write("            // type [" + type + "] not supported.");
            }
            writeNewLine(writer);
        }
        writer.write("        .build();");
        writeNewLine(writer);
        writeNewLine(writer);
    }


    private static void writeConstructor(Writer writer, QueryDescriptor ed) throws IOException {
        writeNewLine(writer);
        writer.write("    public " + ed.simpleName() + "SqlPageRequestDescriptor");
        writer.write("() {");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
    }

    private void writeMethodGet(Writer writer, QueryDescriptor ed) throws IOException {
        writeNewLine(writer);
        writer.write("    @Override");
        writeNewLine(writer);
        writer.write("    public SqlColumn get(String property) {");
        writeNewLine(writer);
        writer.write("        return VALUES.get(property);");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
    }

    private void writeMethodExpression(Writer writer, QueryDescriptor ed) throws IOException {
        writeNewLine(writer);
        writer.write("    @Override");
        writeNewLine(writer);
        writer.write("    public PreparedStatementIndexSetter getPreparedStatementIndexSetter(Filter filter) {");
        writeNewLine(writer);
        writer.write("        Function<Filter, PreparedStatementIndexSetter> function = PREPARED_STATEMENT_INDEX_SETTERS.get(filter.getProperty());");
        writeNewLine(writer);
        writer.write("        if (function == null) {");
        writeNewLine(writer);
        writer.write("            throw new SqlPageRequestDescriptorException(SqlPageRequestDescriptorException.Type.PROPERTY_NOT_FOUND, ImmutableMap.of(\"filter\",filter));");
        writeNewLine(writer);
        writer.write("        }");
        writeNewLine(writer);
        writer.write("        return function.apply(filter);");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
    }

}