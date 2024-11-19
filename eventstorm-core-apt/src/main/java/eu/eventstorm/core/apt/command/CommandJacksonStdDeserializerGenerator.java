package eu.eventstorm.core.apt.command;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import eu.eventstorm.annotation.CqrsPropertyFactory;
import eu.eventstorm.core.apt.SourceCode;
import eu.eventstorm.core.apt.model.AbstractCommandDescriptor;
import eu.eventstorm.core.apt.model.CommandDescriptor;
import eu.eventstorm.core.apt.model.EmbeddedCommandDescriptor;
import eu.eventstorm.core.apt.model.PropertyDescriptor;
import eu.eventstorm.core.json.DeserializerException;
import eu.eventstorm.core.util.PropertyFactoryType;
import eu.eventstorm.sql.apt.Helper;
import eu.eventstorm.sql.apt.log.Logger;
import eu.eventstorm.util.Dates;
import eu.eventstorm.util.TriConsumer;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
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
public final class CommandJacksonStdDeserializerGenerator {

    private Logger logger;
    private ProcessingEnvironment env;

    public void generate(ProcessingEnvironment env, SourceCode sourceCode) {

        try (Logger logger = Logger.getLogger(env, "eu.eventstorm.event.generator", "CommandJacksonStdDeserializerGenerator")) {
            this.logger = logger;
            this.env = env;
            sourceCode.forEachCommandPackage((pack, list) -> {
                try {
                    generate(env, pack, list, sourceCode);
                } catch (Exception cause) {
                    logger.error("Exception for [" + pack + "] -> [" + cause.getMessage() + "]", cause);
                }
            });
        }
    }

    public void generateEmbedded(ProcessingEnvironment env, SourceCode sourceCode) {
        // generate Implementation class;
        try (Logger logger = Logger.getLogger(env, "eu.eventstorm.event.generator", "CommandJacksonStdDeserializerGeneratorEmbedded")) {
            this.logger = logger;
            this.env = env;
            sourceCode.forEachEmbeddedCommandPackage((pack, list) -> {
                try {
                    generate(env, pack, list, sourceCode);
                } catch (Exception cause) {
                    logger.error("Exception for [" + pack + "] -> [" + cause.getMessage() + "]", cause);
                }
            });
        }

    }


    private void generate(ProcessingEnvironment env, String pack, ImmutableList<? extends AbstractCommandDescriptor> descriptors, SourceCode sourceCode) throws IOException {

        for (AbstractCommandDescriptor cd : descriptors) {

            // check due to "org.aspectj.org.eclipse.jdt.internal.compiler.apt.dispatch.BatchFilerImpl.createSourceFile(BatchFilerImpl.java:149)"
            if (env.getElementUtils().getTypeElement(pack + ".json." + cd.simpleName() + "StdDeserializer") != null) {
                logger.info("Java SourceCode already exist [" + pack + ".json." + cd.simpleName() + "StdDeserializer" + "]");
                return;
            }

            JavaFileObject object = env.getFiler().createSourceFile(pack + ".json." + cd.simpleName() + "StdDeserializer");
            Writer writer = object.openWriter();

            writeHeader(writer, pack + ".json", cd);
            writeStatic(writer, cd, sourceCode);
            writeConstructor(writer, cd);
            writeMethod(writer, cd);

            writer.write("}");
            writer.close();
        }

    }

    private static void writeHeader(Writer writer, String pack, AbstractCommandDescriptor cd) throws IOException {
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
        writeImport(writer, cd, LocalTime.class.getName(), Dates.class.getName() + ".parseLocalTime");

        writeGenerated(writer, CommandJacksonStdDeserializerGenerator.class.getName());

        writer.write("@SuppressWarnings(\"serial\")");
        writeNewLine(writer);
        writer.write("final class " + cd.simpleName() + "StdDeserializer extends StdDeserializer<" + cd.simpleName() + "> {");
        writeNewLine(writer);
    }

    private static void writeImport(Writer writer, AbstractCommandDescriptor cd, String fcqn, String staticMethod) throws IOException {
        for (PropertyDescriptor cpd : cd.properties()) {
            if (fcqn.equals(cpd.getter().getReturnType().toString())) {
                writer.write("import static " + staticMethod + ";");
                writeNewLine(writer);
                break;
            }
        }
    }

    private void writeStatic(Writer writer, AbstractCommandDescriptor cd, SourceCode sourceCode) throws IOException {

        writeNewLine(writer);
        writer.write("    private static final ImmutableMap<String, TriConsumer<JsonParser,DeserializationContext," + cd.simpleName() + "Builder>> FIELDS;");
        writeNewLine(writer);
        writer.write("    static {");
        writeNewLine(writer);
        writer.write("        FIELDS = ImmutableMap.<String, TriConsumer<JsonParser,DeserializationContext," + cd.simpleName() + "Builder>>builder()");
        for (PropertyDescriptor cpd : cd.properties()) {
            writer.write(".put(\"" + cpd.name() + "\", (parser, ctxt, builder) -> {");
            writeNewLine(writer);
            writer.write("			try {");
            writeNewLine(writer);

            String returnType = getReturnType(cpd.getter());

            if (returnType.startsWith(List.class.getName())) {

                writer.write("				");
                String fcqnTarget = returnType.substring(15, returnType.length() - 1);

                if (fcqnTarget.equals("java.lang.String")) {

                    String fcqnTargetSimpleName = fcqnTarget.substring(fcqnTarget.lastIndexOf('.') + 1);
                    writer.write(ImmutableList.class.getName() + ".Builder<" + fcqnTargetSimpleName + "> childBuilder = builder.with" + Helper.firstToUpperCase(cpd.name()) + "();");
                    writeNewLine(writer);
                    writer.write("				parser.nextToken();");
                    writeNewLine(writer);
                    writer.write("                while (parser.currentToken() != JsonToken.END_ARRAY) {");
                    writeNewLine(writer);
                    writer.write("                    parser.nextToken();");
                    writeNewLine(writer);
                    writer.write("                    if (parser.currentToken() == JsonToken.VALUE_STRING) {");
                    writeNewLine(writer);
                    writer.write("                        childBuilder.add(parser.getText());");
                    writeNewLine(writer);
                    writer.write("                    }");
                    writeNewLine(writer);
                    writer.write("                }");
                    writeNewLine(writer);

                } else if (fcqnTarget.equals("java.lang.Integer")) {

                    String fcqnTargetSimpleName = fcqnTarget.substring(fcqnTarget.lastIndexOf('.') + 1);
                    writer.write(ImmutableList.class.getName() + ".Builder<" + fcqnTargetSimpleName + "> childBuilder = builder.with" + Helper.firstToUpperCase(cpd.name()) + "();");
                    writeNewLine(writer);
                    writer.write("				parser.nextToken();");
                    writeNewLine(writer);
                    writer.write("                while (parser.currentToken() != JsonToken.END_ARRAY) {");
                    writeNewLine(writer);
                    writer.write("                    parser.nextToken();");
                    writeNewLine(writer);
                    writer.write("                    if (parser.currentToken() == JsonToken.VALUE_NUMBER_INT) {");
                    writeNewLine(writer);
                    writer.write("                        childBuilder.add(parser.getIntValue());");
                    writeNewLine(writer);
                    writer.write("                    }");
                    writeNewLine(writer);
                    writer.write("                }");
                    writeNewLine(writer);

                } else if (fcqnTarget.equals("java.lang.Long")) {

                    String fcqnTargetSimpleName = fcqnTarget.substring(fcqnTarget.lastIndexOf('.') + 1);
                    writer.write(ImmutableList.class.getName() + ".Builder<" + fcqnTargetSimpleName + "> childBuilder = builder.with" + Helper.firstToUpperCase(cpd.name()) + "();");
                    writeNewLine(writer);
                    writer.write("				parser.nextToken();");
                    writeNewLine(writer);
                    writer.write("                while (parser.currentToken() != JsonToken.END_ARRAY) {");
                    writeNewLine(writer);
                    writer.write("                    parser.nextToken();");
                    writeNewLine(writer);
                    writer.write("                    if (parser.currentToken() == JsonToken.VALUE_NUMBER_INT) {");
                    writeNewLine(writer);
                    writer.write("                        childBuilder.add(parser.getLongValue());");
                    writeNewLine(writer);
                    writer.write("                    }");
                    writeNewLine(writer);
                    writer.write("                }");
                    writeNewLine(writer);

                } else {
                    if (Helper.isEnum(env, fcqnTarget)) {
                        writeParseListEnum(writer, cd, cpd, fcqnTarget);
                        //writeParseEnum();
                    } else {
                        writeParseList(writer, cd, cpd, fcqnTarget);
                    }


                }

            } else if (returnType.equals(Map.class.getName() + "<java.lang.String,java.lang.String>")) {

                writer.write("				" + ImmutableMap.class.getName() + ".Builder<String,String> mapBuilder = " + ImmutableMap.class.getName() + ".builder();");
                writeNewLine(writer);
                writer.write("				// FIELD");
                writeNewLine(writer);
                writer.write("				parser.nextToken();");
                writeNewLine(writer);
                writer.write("				// START_OBJECT");
                writeNewLine(writer);
                writer.write("				parser.nextToken();");
                writeNewLine(writer);
                writer.write("				while (parser.currentToken() != JsonToken.END_OBJECT) {");
                writeNewLine(writer);
                writer.write("				    mapBuilder.put(parser.currentName(), parser.nextTextValue());");
                writeNewLine(writer);
                writer.write("				    parser.nextToken();");
                writeNewLine(writer);
                writer.write("				}");
                writeNewLine(writer);
                writer.write("				builder.with" + Helper.firstToUpperCase(cpd.name()) + "(mapBuilder.build());");
                writeNewLine(writer);

            } else if (returnType.equals(Map.class.getName() + "<java.lang.String,java.lang.Object>")) {
                writeNewLine(writer);
                writer.write("				// FIELD");
                writeNewLine(writer);
                writer.write("				parser.nextToken();");
                writeNewLine(writer);
                writer.write("				builder.with" + Helper.firstToUpperCase(cpd.name()) + "((java.util.Map<String, Object>) eu.eventstorm.cqrs.util.Jsons.readMapStringObject(parser));");
                writeNewLine(writer);

            } else {

                CommandDescriptor desc = sourceCode.getCommandDescriptor(returnType);
                EmbeddedCommandDescriptor embeddedDesc = sourceCode.getEmbeddedCommandDescriptor(returnType);
                if (desc != null || embeddedDesc != null) {
                    writer.write("				parser.nextToken();");
                    writeNewLine(writer);
                    writer.write("				builder.with" + Helper.firstToUpperCase(cpd.name()) + "(");
                    writer.write("parser.readValueAs(" + returnType + ".class)");
                } else {
                    writer.write("				builder.with" + Helper.firstToUpperCase(cpd.name()) + "(");

                    if ("java.lang.String".equals(returnType)) {
                        writer.write("parser.nextTextValue()");
                    } else if ("byte".equals(returnType) || "java.lang.Byte".equals(returnType)) {
                        writer.write("(byte)parser.nextIntValue(0)");
                    } else if ("int".equals(returnType) || "java.lang.Integer".equals(returnType)) {
                        writer.write("parser.nextIntValue(0)");
                    } else if ("long".equals(returnType) || "java.lang.Long".equals(returnType)) {
                        writer.write("parser.nextLongValue(0l)");
                    } else if ("boolean".equals(returnType) || "java.lang.Boolean".equals(returnType)) {
                        writer.write("parser.nextBooleanValue()");
                    } else if (OffsetDateTime.class.getName().equals(returnType)) {
                        writer.write("parseOffsetDateTime(parser.nextTextValue())");
                    } else if (LocalDate.class.getName().equals(returnType)) {
                        writer.write("parseLocalDate(parser.nextTextValue())");
                    } else if (LocalTime.class.getName().equals(returnType)) {
                        writer.write("parseLocalTime(parser.nextTextValue())");
                    } else if (Helper.isEnum(cpd.getter().getReturnType())) {
                        CqrsPropertyFactory factory = cpd.getter().getAnnotation(CqrsPropertyFactory.class);
                        if (factory == null) {
                            writer.write(cpd.getter().getReturnType().toString() + ".valueOf(parser.nextTextValue())");
                        } else {
                            if (PropertyFactoryType.STRING == factory.type()) {
                                writer.write(cpd.name().toUpperCase() + "_FACTORY.apply(parser.nextTextValue())");
                            } else if (PropertyFactoryType.INTEGER == factory.type()) {
                                writer.write(cpd.name().toUpperCase() + "_FACTORY.apply(parser.nextIntValue(0))");
                            } else {
                                writer.write(cpd.name().toUpperCase() + "_FACTORY.apply(parser.nextLongValue(0l))");
                            }
                        }
                    } else {
                        throw new UnsupportedOperationException("Type not supported [" + returnType + "]");
                    }
                }

                writer.write(");");
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


    private static void writeConstructor(Writer writer, AbstractCommandDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writer.write("    " + descriptor.simpleName() + "StdDeserializer");
        writer.write("() {");
        writeNewLine(writer);
        writer.write("        super(" + descriptor.simpleName() + ".class);");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
    }


    private void writeMethod(Writer writer, AbstractCommandDescriptor cd) throws IOException {
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
        writer.write("                throw new DeserializerException(DeserializerException.Type.FIELD_NOT_FOUND, ImmutableMap.of(\"field\",p.currentName(),\"eventPayload\", \"" + cd.simpleName() + "\"));");
        writeNewLine(writer);
        writer.write("            }");
        writeNewLine(writer);
        writer.write("            consumer.accept(p, ctxt, builder);");
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


    private void writeParseListEnum(Writer writer, AbstractCommandDescriptor cd, PropertyDescriptor cpd, String fcqnTarget) throws IOException {
        writer.write(ImmutableList.class.getName() + ".Builder<" + fcqnTarget + "> childBuilder = builder.with" + Helper.firstToUpperCase(cpd.name()) + "();");
        writeNewLine(writer);
        writer.write("				ctxt.setAttribute(\"" + fcqnTarget + "\", childBuilder);");
        writeNewLine(writer);
        writer.write("				parser.nextToken();");
        writeNewLine(writer);
        writer.write("                while (parser.currentToken() != JsonToken.END_ARRAY) {");
        writeNewLine(writer);
        writer.write("                    childBuilder.add(" + fcqnTarget + ".valueOf(parser.nextTextValue()));");
        writeNewLine(writer);
        writer.write("                    parser.nextToken();");
        writeNewLine(writer);
        writer.write("                }");
        writeNewLine(writer);
    }

    private void writeParseList(Writer writer, AbstractCommandDescriptor cd, PropertyDescriptor cpd, String fcqnTarget) throws IOException {
        String fcqnTargetSimpleName = fcqnTarget.substring(fcqnTarget.lastIndexOf('.') + 1);
        writer.write(cd.fullyQualidiedClassName() + "__" + fcqnTargetSimpleName + "__Builder childBuilder = builder.with" + Helper.firstToUpperCase(cpd.name()) + "();");
        writeNewLine(writer);
        writer.write("				ctxt.setAttribute(\"" + fcqnTarget + "\", childBuilder);");
        writeNewLine(writer);
        writer.write("				parser.nextToken();");
        writeNewLine(writer);
        writer.write("                while (parser.currentToken() != JsonToken.END_ARRAY) {");
        writeNewLine(writer);
        writer.write("                    if (parser.nextToken() == JsonToken.START_OBJECT) {");
        writeNewLine(writer);
        writer.write("                        childBuilder.and(ctxt.readValue(parser, " + fcqnTarget + ".class));");
        writeNewLine(writer);
        writer.write("                    }");
        writeNewLine(writer);
        writer.write("                }");
        writeNewLine(writer);
    }

}