package eu.eventstorm.core.apt.query.els;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import eu.eventstorm.annotation.els.Date;
import eu.eventstorm.annotation.els.Id;
import eu.eventstorm.annotation.els.Keyword;
import eu.eventstorm.annotation.els.Nested;
import eu.eventstorm.annotation.els.Number;
import eu.eventstorm.annotation.els.Text;
import eu.eventstorm.core.apt.SourceCode;
import eu.eventstorm.core.apt.model.ElsQueryDescriptor;
import eu.eventstorm.core.apt.model.QueryDescriptor;
import eu.eventstorm.core.apt.model.QueryPropertyDescriptor;
import eu.eventstorm.cqrs.PageQueryDescriptor;
import eu.eventstorm.cqrs.PageQueryDescriptors;
import eu.eventstorm.cqrs.els.page.ElsField;
import eu.eventstorm.cqrs.els.page.ElsFieldType;
import eu.eventstorm.page.SinglePropertyFilter;
import eu.eventstorm.sql.apt.log.Logger;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.function.Function;

import static eu.eventstorm.sql.apt.Helper.writeGenerated;
import static eu.eventstorm.sql.apt.Helper.writeNewLine;
import static eu.eventstorm.sql.apt.Helper.writePackage;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class ElasticPageRequestDescriptorGenerator {

    private Logger logger;

    public void generate(ProcessingEnvironment processingEnvironment, SourceCode sourceCode) {

        try (Logger logger = Logger.getLogger(processingEnvironment, "eu.eventstorm.event.query", "ElasticPageRequestDescriptorGenerator")) {
            this.logger = logger;
            sourceCode.forEachElsQueryPackage((pack, list) -> {
                try {
                    generate(processingEnvironment, pack, list);
                } catch (Exception cause) {
                    logger.error("Exception for [" + pack + "] -> [" + cause.getMessage() + "]", cause);
                }
            });
        }

    }

    private void generate(ProcessingEnvironment env, String pack, ImmutableList<ElsQueryDescriptor> descriptors) throws IOException {

        for (QueryDescriptor ed : descriptors) {

            String fcqn = pack + "." + ed.simpleName() + "ElsPageRequestDescriptor";

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
        writer.write("import " + SinglePropertyFilter.class.getName() + ";");
        writeNewLine(writer);
        writer.write("import " + PageQueryDescriptor.class.getName() + ";");
        writeNewLine(writer);
        writer.write("import " + PageQueryDescriptors.class.getName() + ";");
        writeNewLine(writer);
        writer.write("import " + ElsField.class.getName() + ";");
        writeNewLine(writer);
        writer.write("import " + ElsFieldType.class.getName() + ";");
        writeNewLine(writer);
        writer.write("import " + descriptor.fullyQualidiedClassName() + ";");
        writeNewLine(writer);
        writer.write("import eu.eventstorm.cqrs.els.page.ElsPageRequestDescriptor;");
        writeNewLine(writer);


        writeGenerated(writer, ElasticPageRequestDescriptorGenerator.class.getName());
        writer.write("@SuppressWarnings(\"serial\")");
        writeNewLine(writer);
        writer.write("public final class " + descriptor.simpleName() + "ElsPageRequestDescriptor implements ElsPageRequestDescriptor {");
        writeNewLine(writer);
    }


    private void writeStatic(Writer writer, QueryDescriptor ed) throws IOException {

        writeNewLine(writer);
        writer.write("    private static final ImmutableMap<String, ElsField> VALUES = ImmutableMap.<String, ElsField>builder() ");
        writeNewLine(writer);
        for (QueryPropertyDescriptor property : ed.properties()) {
            writer.write("        .put(\"" + property.name() + "\", new ElsField(\""+property.name()+"\",ElsFieldType." + convert(property).name() + "))");
            writeNewLine(writer);
        }
        writer.write("        .build();");
        writeNewLine(writer);

        /*
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
        writeNewLine(writer);*/
    }


    private static void writeConstructor(Writer writer, QueryDescriptor ed) throws IOException {
        writeNewLine(writer);
        writer.write("    public " + ed.simpleName() + "ElsPageRequestDescriptor");
        writer.write("() {");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
    }

    private void writeMethodGet(Writer writer, QueryDescriptor ed) throws IOException {
        writeNewLine(writer);
        writer.write("    @Override");
        writeNewLine(writer);
        writer.write("    public ElsField get(String property) {");
        writeNewLine(writer);
        writer.write("        return VALUES.get(property);");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);

    }

    private void writeMethodExpression(Writer writer, QueryDescriptor ed) throws IOException {
      /*  writeNewLine(writer);
        writer.write("    @Override");
        writeNewLine(writer);
        writer.write("    public PreparedStatementIndexSetter getPreparedStatementIndexSetter(SinglePropertyFilter filter) {");
        writeNewLine(writer);
        writer.write("        Function<SinglePropertyFilter, PreparedStatementIndexSetter> function = PREPARED_STATEMENT_INDEX_SETTERS.get(filter.getProperty());");
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

       */
    }

    private static ElsFieldType convert(QueryPropertyDescriptor property) {
        if (property.getter().getAnnotation(Keyword.class) != null) {
            return ElsFieldType.KEYWORD;
        }
        if (property.getter().getAnnotation(Text.class) != null) {
            return ElsFieldType.TEXT;
        }
        if (property.getter().getAnnotation(Number.class) != null) {
            return ElsFieldType.NUMBER;
        }
        if (property.getter().getAnnotation(Nested.class) != null) {
            return ElsFieldType.NESTED;
        }
        if (property.getter().getAnnotation(Id.class) != null) {
            return ElsFieldType.ID;
        }
        if (property.getter().getAnnotation(Date.class) != null) {
            return ElsFieldType.DATE;
        }
        throw new IllegalStateException("Unknown property [" + property + "]");
    }

}