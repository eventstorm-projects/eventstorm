package eu.eventstorm.core.apt.query;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import eu.eventstorm.annotation.CqrsPropertyFactory;
import eu.eventstorm.core.apt.SourceCode;
import eu.eventstorm.core.apt.model.QueryDescriptor;
import eu.eventstorm.core.apt.model.QueryPropertyDescriptor;
import eu.eventstorm.core.json.DeserializerException;
import eu.eventstorm.core.util.PropertyFactory;
import eu.eventstorm.core.util.PropertyFactoryType;
import eu.eventstorm.cqrs.util.Jsons;
import eu.eventstorm.sql.apt.Helper;
import eu.eventstorm.sql.apt.log.Logger;
import eu.eventstorm.sql.type.Json;
import eu.eventstorm.util.Dates;
import eu.eventstorm.util.TriConsumer;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import static eu.eventstorm.sql.apt.Helper.getReturnType;
import static eu.eventstorm.sql.apt.Helper.writeGenerated;
import static eu.eventstorm.sql.apt.Helper.writeNewLine;
import static eu.eventstorm.sql.apt.Helper.writePackage;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class QueryJacksonStdDeserializerGenerator {

    private Logger logger;

    public void generate(ProcessingEnvironment processingEnvironment, SourceCode sourceCode) {

        try (Logger logger = Logger.getLogger(processingEnvironment, "eu.eventstorm.event.query", "QueryJacksonStdDeserializerGenerator")) {
            this.logger = logger;
            sourceCode.forEachQueryClientPackage((pack, list) -> {
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
            sourceCode.forEachDatabaseViewQueryPackage((pack, list) -> {
                try {
                    generate(processingEnvironment, pack, list);
                } catch (Exception cause) {
                    logger.error("Exception for [" + pack + "] -> [" + cause.getMessage() + "]", cause);
                }
            });
            sourceCode.forEachPojoQueryPackage((pack, list) -> {
                try {
                    generate(processingEnvironment, pack, list);
                } catch (Exception cause) {
                    logger.error("Exception for [" + pack + "] -> [" + cause.getMessage() + "]", cause);
                }
            });
            sourceCode.forEachElsQueryPackage((pack, list) -> {
                try {
                    generate(processingEnvironment, pack, list);
                } catch (Exception cause) {
                    logger.error("Exception for [" + pack + "] -> [" + cause.getMessage() + "]", cause);
                }
            });
        }

    }

    private void generate(ProcessingEnvironment env, String pack, ImmutableList<? extends QueryDescriptor> descriptors) throws IOException {

        for (QueryDescriptor cd : descriptors) {

            // check due to "org.aspectj.org.eclipse.jdt.internal.compiler.apt.dispatch.BatchFilerImpl.createSourceFile(BatchFilerImpl.java:149)"
            if (env.getElementUtils().getTypeElement(pack + ".json." + cd.simpleName() + "StdDeserializer") != null) {
                logger.info("Java SourceCode already exist [" + pack + ".json." + cd.simpleName() + "StdDeserializer" + "]");
                return;
            }

            JavaFileObject object = env.getFiler().createSourceFile(pack + ".json." + cd.simpleName() + "StdDeserializer");
            Writer writer = object.openWriter();

            writeHeader(writer, pack + ".json", cd);
            writeVariables(writer, cd);
            writeStatic(writer, cd, descriptors);
            writeConstructor(writer, cd);
            writeMethod(writer, cd);

            writer.write("}");
            writer.close();
        }

    }

    private static void writeHeader(Writer writer, String pack, QueryDescriptor cd) throws IOException {
        writePackage(writer, pack);

        writeNewLine(writer);
        writer.write("import " + ImmutableMap.class.getName() + ";");
        writeNewLine(writer);
        writer.write("import " + TriConsumer.class.getName() + ";");
        writeNewLine(writer);
        writer.write("import " + DeserializerException.class.getName() + ";");
        writeNewLine(writer);

        writer.write("import " + StdDeserializer.class.getName() + ";");
        writeNewLine(writer);
        writer.write("import " + JsonParser.class.getName() + ";");
        writeNewLine(writer);
        writer.write("import " + JsonToken.class.getName() + ";");
        writeNewLine(writer);
        writer.write("import " + IOException.class.getName() + ";");
        writeNewLine(writer);
        writer.write("import " + DeserializationContext.class.getName() + ";");
        writeNewLine(writer);
        writer.write("import " + cd.fullyQualidiedClassName() + ";");
        writeNewLine(writer);
        writer.write("import " + cd.fullyQualidiedClassName() + "Builder;");
        writeNewLine(writer);

        writeImport(writer, cd, OffsetDateTime.class.getName(), Dates.class.getName() + ".parseOffsetDateTime");
        writeImport(writer, cd, LocalDate.class.getName(), Dates.class.getName() + ".parseLocalDate");
        writeImport(writer, cd, Date.class.getName(), Dates.class.getName() + ".parseLocalDate");
        writeImport(writer, cd, LocalTime.class.getName(), Dates.class.getName() + ".parseLocalTime");
        writeImport(writer, cd, Timestamp.class.getName(), Dates.class.getName() + ".parseOffsetDateTime");

        writeGenerated(writer, QueryJacksonStdDeserializerGenerator.class.getName());

        writer.write("@SuppressWarnings(\"serial\")");
        writeNewLine(writer);
        writer.write("final class " + cd.simpleName() + "StdDeserializer extends StdDeserializer<" + cd.simpleName() + "> {");
        writeNewLine(writer);
    }

    private static void writeImport(Writer writer, QueryDescriptor cd, String fcqn, String staticMethod) throws IOException {
        for (QueryPropertyDescriptor cpd : cd.properties()) {
            if (fcqn.equals(cpd.getter().getReturnType().toString())) {
                writer.write("import static " + staticMethod + ";");
                writeNewLine(writer);
                break;
            }
        }
    }

    private void writeStatic(Writer writer, QueryDescriptor cd, ImmutableList<? extends QueryDescriptor> descriptors) throws IOException {

        writeNewLine(writer);
        writer.write("    private static final ImmutableMap<String, TriConsumer<JsonParser,DeserializationContext," + cd.simpleName() + "Builder>> FIELDS;");
        writeNewLine(writer);
        writer.write("    static {");
        writeNewLine(writer);
        writer.write("        FIELDS = ImmutableMap.<String, TriConsumer<JsonParser,DeserializationContext," + cd.simpleName() + "Builder>>builder()");
        for (QueryPropertyDescriptor cpd : cd.properties()) {
            writer.write(".put(\"" + cpd.name() + "\", (parser, ctxt, builder) -> {");
            writeNewLine(writer);
            writer.write("			try {");
            writeNewLine(writer);

            String returnType = getReturnType(cpd.getter());

            if (returnType.startsWith(List.class.getName())) {
                if (returnType.equals(List.class.getName() + "<java.lang.String>")) {
                    writer.write("				builder.with" + Helper.firstToUpperCase(cpd.name()) + "(" + eu.eventstorm.cqrs.util.Jsons.class.getName() + ".readListString(parser));");
                } else {
                    writer.write("				builder.with" + Helper.firstToUpperCase(cpd.name()) + "(" + eu.eventstorm.cqrs.util.Jsons.class.getName() + ".readList(parser, " + returnType.substring(15, returnType.length() - 1) + ".class));");
                }
                writeNewLine(writer);
            } else if (returnType.startsWith(Map.class.getName())) {
                if (returnType.equals(Map.class.getName() + "<java.lang.String,java.lang.String>")) {
                    writer.write("				builder.with" + Helper.firstToUpperCase(cpd.name()) + "(" + eu.eventstorm.cqrs.util.Jsons.class.getName() + ".readMapStringString(parser));");
                } else {
                    writer.write("				builder.with" + Helper.firstToUpperCase(cpd.name()) + "((" +
                            returnType + ")" + eu.eventstorm.cqrs.util.Jsons.class.getName() + ".readMapStringObject(parser));");
                }
                writeNewLine(writer);

            } else {

                if ("java.lang.String".equals(returnType)) {
                    writer.write("				builder.with" + Helper.firstToUpperCase(cpd.name()) + "(parser.nextTextValue());");
                } else if ("int".equals(returnType) || "java.lang.Integer".equals(returnType)) {
                    writer.write("				builder.with" + Helper.firstToUpperCase(cpd.name()) + "(parser.nextIntValue(0));");
                } else if ("byte".equals(returnType) || "java.lang.Byte".equals(returnType)) {
                    writer.write("				builder.with" + Helper.firstToUpperCase(cpd.name()) + "((byte)parser.nextIntValue(0));");
                } else if ("short".equals(returnType) || "java.lang.Short".equals(returnType)) {
                    writer.write("				builder.with" + Helper.firstToUpperCase(cpd.name()) + "((short)parser.nextIntValue(0));");
                } else if ("long".equals(returnType) || "java.lang.Long".equals(returnType)) {
                    writer.write("				builder.with" + Helper.firstToUpperCase(cpd.name()) + "(parser.nextLongValue(0l));");
                } else if ("boolean".equals(returnType) || "java.lang.Boolean".equals(returnType)) {
                    writer.write("				builder.with" + Helper.firstToUpperCase(cpd.name()) + "(parser.nextBooleanValue());");
                } else if (OffsetDateTime.class.getName().equals(returnType)) {
                    writer.write("				builder.with" + Helper.firstToUpperCase(cpd.name()) + "(parseOffsetDateTime(parser.nextTextValue()));");
                } else if (LocalDate.class.getName().equals(returnType)) {
                    writer.write("				builder.with" + Helper.firstToUpperCase(cpd.name()) + "(parseLocalDate(parser.nextTextValue()));");
                } else if (LocalTime.class.getName().equals(returnType)) {
                    writer.write("				builder.with" + Helper.firstToUpperCase(cpd.name()) + "(parseLocalTime(parser.nextTextValue()));");
                } else if (Timestamp.class.getName().equals(returnType)) {
                    writer.write("				builder.with" + Helper.firstToUpperCase(cpd.name()) + "(" + Timestamp.class.getName() + ".valueOf(parseOffsetDateTime(parser.nextTextValue()).toLocalDateTime()));");
                } else if (Date.class.getName().equals(returnType)) {
                    writer.write("				builder.with" + Helper.firstToUpperCase(cpd.name()) + "(" + Date.class.getName() + ".valueOf(parseLocalDate(parser.nextTextValue())));");
                } else if (Json.class.getName().equals(returnType)) {

                    writer.write("				parser.nextToken();");
                    writeNewLine(writer);
                    writer.write("				" + Jsons.class.getName() + ".ignoreField(parser);");
                } else if (Helper.isEnum(cpd.getter().getReturnType())) {
                    CqrsPropertyFactory factory = cpd.getter().getAnnotation(CqrsPropertyFactory.class);
                    if (factory == null) {
                        writer.write("				builder.with" + Helper.firstToUpperCase(cpd.name()) + "(" + cpd.getter().getReturnType().toString() + ".valueOf(parser.nextTextValue()));");
                    } else {
                        if (PropertyFactoryType.STRING == factory.type()) {
                            writer.write("				builder.with" + Helper.firstToUpperCase(cpd.name()) + "(" + cpd.name().toUpperCase() + "_FACTORY.apply(parser.nextTextValue()));");
                        } else if (PropertyFactoryType.INTEGER == factory.type()) {
                            writer.write("				builder.with" + Helper.firstToUpperCase(cpd.name()) + "(" + cpd.name().toUpperCase() + "_FACTORY.apply(parser.nextIntValue(0)));");
                        } else {
                            writer.write("				builder.with" + Helper.firstToUpperCase(cpd.name()) + "(" + cpd.name().toUpperCase() + "_FACTORY.apply(parser.nextLongValue(0l)));");
                        }
                    }
                } else {

                    QueryDescriptor other = null;

                    for (QueryDescriptor item : descriptors) {
                        if (returnType.equals(item.fullyQualidiedClassName())) {
                            other = item;
                        }
                    }

                    if (other == null) {
                        throw new UnsupportedOperationException("Type not supported [" + returnType + "]");
                    } else {
                        writer.write("				builder.with" + Helper.firstToUpperCase(cpd.name()) + "(parser.readValueAs(" + other.fullyQualidiedClassName() + ".class));");
                    }

                }
                writeNewLine(writer);
            }

            writer.write("			} catch (IOException cause) {");
            writeNewLine(writer);
            writer.write("			    throw new DeserializerException(DeserializerException.Type.PARSE_ERROR, ImmutableMap.of(\"field\",\"" + cpd.name() + "\"), cause);");
            writeNewLine(writer);
            writer.write("			}");


            writeNewLine(writer);
            writer.write("		})");
        }

        writer.write(".build();");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);

    }

    private void writeVariables(Writer writer, QueryDescriptor cd) throws IOException {

        writer.write("    private static final " + org.slf4j.Logger.class.getName() + " LOGGER = " + org.slf4j.LoggerFactory.class.getName() + ".getLogger(" + cd.simpleName() + "StdDeserializer.class);");
        writeNewLine(writer);

        for (QueryPropertyDescriptor cpd : cd.properties()) {
            CqrsPropertyFactory factory = cpd.getter().getAnnotation(CqrsPropertyFactory.class);
            if (factory != null) {
                writer.write("    private static final " + PropertyFactory.class.getName() + "<");
                if (PropertyFactoryType.INTEGER == factory.type()) {
                    writer.write("Integer,");
                } else if (PropertyFactoryType.LONG == factory.type()) {
                    writer.write("Long,");
                } else {
                    writer.write("String,");
                }
                writer.write(cpd.getter().getReturnType().toString());
                writer.write("> " + cpd.name().toUpperCase() + "_FACTORY = ");
                writeNewLine(writer);
                writer.write("        new " + getFactory(factory) + "();");
                writeNewLine(writer);
            }
        }
    }

    private static void writeConstructor(Writer writer, QueryDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writer.write("    " + descriptor.simpleName() + "StdDeserializer");
        writer.write("() {");
        writeNewLine(writer);
        writer.write("        super(" + descriptor.simpleName() + ".class);");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
    }


    private void writeMethod(Writer writer, QueryDescriptor cd) throws IOException {
        writeNewLine(writer);
        writer.write("    @Override");
        writeNewLine(writer);
        writer.write("    public " + cd.simpleName() + " deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {");


        writeNewLine(writer);
        writer.write("        " + cd.simpleName() + "Builder builder = new " + cd.simpleName() + "Builder();");

        writeNewLine(writer);
        writer.write("        if (JsonToken.START_OBJECT != p.currentToken()) {");
        writeNewLine(writer);
        writer.write("            throw new DeserializerException(DeserializerException.Type.PARSE_ERROR, ImmutableMap.of(\"expected\",JsonToken.START_OBJECT,\"current\", p.currentToken()));");
        writeNewLine(writer);
        writer.write("        }");
        writeNewLine(writer);

        writer.write("        p.nextToken();");
        writeNewLine(writer);

        writer.write("        while (p.currentToken() != JsonToken.END_OBJECT) {");
        writeNewLine(writer);

        writer.write("            if (JsonToken.FIELD_NAME != p.currentToken()) {");
        writeNewLine(writer);
        writer.write("                throw new DeserializerException(DeserializerException.Type.PARSE_ERROR, ImmutableMap.of(\"expected\",JsonToken.FIELD_NAME,\"current\", p.currentToken()));");
        writeNewLine(writer);
        writer.write("            }");
        writeNewLine(writer);
        writer.write("            TriConsumer<JsonParser,DeserializationContext," + cd.simpleName() + "Builder> consumer = FIELDS.get(p.currentName());");
        writeNewLine(writer);
        writer.write("            if (consumer == null) {");
        writeNewLine(writer);
        //writer.write("                throw new DeserializerException(DeserializerException.Type.FIELD_NOT_FOUND, ImmutableMap.of(\"field\",p.currentName(),\"eventPayload\", \""+ cd.simpleName()+"\"));");
        writer.write("                if (LOGGER.isDebugEnabled()) {");
        writeNewLine(writer);
        writer.write("                    LOGGER.debug(\"Field [{}] not found -> skip\", p.currentName());");
        writeNewLine(writer);
        writer.write("                }");
        writeNewLine(writer);
        writer.write("                " + Jsons.class.getName() + ".ignoreField(p);");
        writeNewLine(writer);
        writer.write("            } else {");
        writeNewLine(writer);
        writer.write("                consumer.accept(p, ctxt, builder);");
        writeNewLine(writer);
        writer.write("            }");
        writeNewLine(writer);
        writer.write("            p.nextToken();");
        writeNewLine(writer);
        writer.write("        }");

        writeNewLine(writer);
        writer.write("        return builder.build();");
        writeNewLine(writer);
        writer.write("     }");
        writeNewLine(writer);
    }

    private static TypeMirror getFactory(CqrsPropertyFactory factory) {
        try {
            factory.factory(); // this should throw
        } catch (MirroredTypeException mte) {
            return mte.getTypeMirror();
        }
        return null; // can this ever happen ??
    }
}