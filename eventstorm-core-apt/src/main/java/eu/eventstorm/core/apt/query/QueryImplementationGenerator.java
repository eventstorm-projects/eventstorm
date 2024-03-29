package eu.eventstorm.core.apt.query;

import eu.eventstorm.annotation.EqualsAndHashCode;
import eu.eventstorm.core.apt.SourceCode;
import eu.eventstorm.core.apt.model.QueryDescriptor;
import eu.eventstorm.core.apt.model.QueryPropertyDescriptor;
import eu.eventstorm.sql.apt.log.Logger;
import eu.eventstorm.util.ToStringBuilder;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.stream.Collectors;

import static eu.eventstorm.sql.apt.Helper.isPrimitiveType;
import static eu.eventstorm.sql.apt.Helper.writeGenerated;
import static eu.eventstorm.sql.apt.Helper.writeNewLine;
import static eu.eventstorm.sql.apt.Helper.writePackage;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class QueryImplementationGenerator {

    private static final String TO_STRING_BUILDER = ToStringBuilder.class.getName();

    private Logger logger;

    public void generateClient(ProcessingEnvironment processingEnvironment, SourceCode sourceCode) {

        try (Logger logger = Logger.getLogger(processingEnvironment, "eu.eventstorm.event.query", "QueryImplementationGenerator")) {
            this.logger = logger;
            sourceCode.forEachQueryClient(t -> {
                try {
                    generate(processingEnvironment, t);
                } catch (Exception cause) {
                    logger.error("Exception for [" + t + "] -> [" + cause.getMessage() + "]", cause);
                }
            });

        }


    }

    public void generate(ProcessingEnvironment processingEnvironment, SourceCode sourceCode) {
        // generate Implementation class;
        sourceCode.forEachQuery(t -> {
            try {
                generate(processingEnvironment, t);
            } catch (Exception cause) {
                logger.error("Exception for [" + t + "] -> [" + cause.getMessage() + "]", cause);
            }
        });


    }

    private void generate(ProcessingEnvironment env, QueryDescriptor descriptor) throws IOException {

        // check due to "org.aspectj.org.eclipse.jdt.internal.compiler.apt.dispatch.BatchFilerImpl.createSourceFile(BatchFilerImpl.java:149)"
        if (env.getElementUtils().getTypeElement(descriptor.fullyQualidiedClassName() + "Impl") != null) {
            logger.info("Java SourceCode already exist [" + descriptor.fullyQualidiedClassName() + "Impl" + "]");
            return;
        }

        JavaFileObject object = env.getFiler().createSourceFile(descriptor.fullyQualidiedClassName() + "Impl");
        Writer writer = object.openWriter();

        writeHeader(writer, env, descriptor);
        writeConstructor(writer, descriptor);
        writeVariables(writer, descriptor);
        writeMethods(writer, descriptor);
        writeToStringBuilder(writer, descriptor);
        writeEquals(writer, descriptor);
        writeHashcode(writer, descriptor);

        writer.write("}");
        writer.close();
    }


    private static void writeHeader(Writer writer, ProcessingEnvironment env, QueryDescriptor descriptor) throws IOException {

        writePackage(writer, env.getElementUtils().getPackageOf(descriptor.element()).toString());
        writeGenerated(writer, QueryImplementationGenerator.class.getName());

        writer.write("final class ");
        writer.write(descriptor.simpleName() + "Impl");
        writer.write(" implements ");
        writer.write(descriptor.fullyQualidiedClassName());
        writer.write(" {");
        writeNewLine(writer);
    }

    private static void writeConstructor(Writer writer, QueryDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writer.write("    ");
        writer.write(descriptor.simpleName() + "Impl");
        writer.write("(" + descriptor.simpleName() + "Builder builder) {");
        writeNewLine(writer);
        for (QueryPropertyDescriptor property : descriptor.properties()) {
            writer.write("        this." + property.name() + "= builder." + property.name() + "$$;");
            writeNewLine(writer);
        }
        writer.write("    }");
        writeNewLine(writer);
    }

    private static void writeVariables(Writer writer, QueryDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writeVariables(writer, descriptor.properties());
    }

    private static void writeVariables(Writer writer, List<QueryPropertyDescriptor> descriptors) throws IOException {
        for (QueryPropertyDescriptor ppd : descriptors) {
            writer.write("    private final ");
            writer.write(ppd.getter().getReturnType().toString());
            writer.write(" ");
            writer.write(ppd.variable());
            writer.write(";");
            writeNewLine(writer);
        }
    }

    private static void writeMethods(Writer writer, QueryDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writeMethods(writer, descriptor.properties());
    }

    private static void writeMethods(Writer writer, List<QueryPropertyDescriptor> descriptors) throws IOException {
        for (QueryPropertyDescriptor ppd : descriptors) {
            writeGetter(writer, ppd);
        }
    }

    private static void writeGetter(Writer writer, QueryPropertyDescriptor ppd) throws IOException {
        writeNewLine(writer);
        writer.write("    /** {@inheritDoc} */");
        writeNewLine(writer);
        writer.write("    @Override");
        writeNewLine(writer);
        writer.write("    public ");
        writer.write(ppd.getter().getReturnType().toString());
        writer.write(' ');
        writer.write(ppd.getter().getSimpleName().toString());
        writer.write("() {");
        writeNewLine(writer);
        writer.write("        return this.");
        writer.write(ppd.variable());
        writer.write(";");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
    }

    private static void writeToStringBuilder(Writer writer, QueryDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writer.write("    /** {@inheritDoc} */");
        writeNewLine(writer);
        writer.write("    @Override");
        writeNewLine(writer);
        writer.write("    public String toString() {");
        writeNewLine(writer);
        writer.write("        " + TO_STRING_BUILDER + " builder = new " + TO_STRING_BUILDER + "(this);");
        writeNewLine(writer);

        for (QueryPropertyDescriptor ppd : descriptor.properties()) {
            writer.write("        builder.append(\"");
            writer.write(ppd.name());
            writer.write("\", this.");
            writer.write(ppd.variable());
            writer.write(");");
            writeNewLine(writer);
        }
        writer.write("        return builder.toString();");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
    }


    private static void writeEquals(Writer writer, QueryDescriptor descriptor) throws IOException {

        writeNewLine(writer);
        writer.write("    /** {@inheritDoc} */");
        writeNewLine(writer);
        writer.write("    @Override");
        writeNewLine(writer);
        writer.write("    public boolean equals(Object object) {");
        writeNewLine(writer);

        writer.write("        if (object == this) {");
        writeNewLine(writer);
        writer.write("            return true;");
        writeNewLine(writer);
        writer.write("        }");
        writeNewLine(writer);

        writer.write("        if ((object == null) || (!(object instanceof ");
        writer.write(descriptor.element().toString());
        writer.write("))) {");
        writeNewLine(writer);
        writer.write("            return false;");
        writeNewLine(writer);
        writer.write("        }");
        writeNewLine(writer);


        writer.write("        ");
        writer.write(descriptor.element().toString());
        writer.write(" other = (");
        writer.write(descriptor.element().toString());
        writer.write(") object;");
        writeNewLine(writer);

        List<QueryPropertyDescriptor> toEquals = descriptor.properties().stream().filter(t -> t.getter().getAnnotation(EqualsAndHashCode.class) != null).collect(Collectors.toList());

        if (toEquals.size() == 0) {
            writer.write("        // no @EqualsAndHashCode -> return Identity");
            writeNewLine(writer);

            writer.write("        return super.equals(object);");
            writeNewLine(writer);
        } else {
            int number = toEquals.size();
            writer.write("        // " + number + " toEquals key" + ((number > 1) ? "s" : "") + " on propert" + ((number > 1) ? "ies" : "y") + " : ");
            for (int i = 0; i < number; i++) {
                writer.write(toEquals.get(i).name());
                if (i + 1 < number) {
                    writer.write(", ");
                }
            }
            writeNewLine(writer);
            writer.write("        return ");
            for (int i = 0; i < number; i++) {
                writeEqualsPojoPropertyDescriptor(writer, toEquals.get(i));
                if (i + 1 < number) {
                    writer.write(" && ");
                    writeNewLine(writer);
                    writer.write("           ");
                }
            }
            writer.write(";");
            writeNewLine(writer);
        }

        writer.write("    }");
        writeNewLine(writer);

    }

    private void writeHashcode(Writer writer, QueryDescriptor descriptor) throws IOException {
        List<QueryPropertyDescriptor> toEquals = descriptor.properties().stream().filter(t -> t.getter().getAnnotation(EqualsAndHashCode.class) != null).collect(Collectors.toList());

        if (toEquals.size() == 0) {
            writer.write("        // no @EqualsAndHashCode -> to hashcode");
            writeNewLine(writer);
            return;
        }

        writeNewLine(writer);
        writer.write("    /** {@inheritDoc} */");
        writeNewLine(writer);
        writer.write("    @Override");
        writeNewLine(writer);
        writer.write("    public int hashCode() {");
        writeNewLine(writer);
        int number = toEquals.size();
        writer.write("        // " + number + " hashcode key" + ((number > 1) ? "s" : "") + " on propert" + ((number > 1) ? "ies" : "y") + " : ");
        writeNewLine(writer);
        writer.write("        return java.util.Objects.hash(");
        for (int i = 0; i < number; i++) {

            writer.write(toEquals.get(i).name());
            if (i + 1 < number) {
                writer.write(", ");
            }
        }
        writer.write(");");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);


    }

    static void writeEqualsPojoPropertyDescriptor(Writer writer, QueryPropertyDescriptor qpd) throws IOException {
        writer.write(qpd.name());

        if (isPrimitiveType(qpd.getter().getReturnType().toString())) {
            writer.write(" == other.");
            writer.write(qpd.getter().getSimpleName().toString() + "()");
        } else {
            writer.write(".equals(other.");
            writer.write(qpd.getter().getSimpleName().toString() + "()");
            writer.write(")");
        }
    }
}