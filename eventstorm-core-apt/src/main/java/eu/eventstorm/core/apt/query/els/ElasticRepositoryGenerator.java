package eu.eventstorm.core.apt.query.els;

import eu.eventstorm.core.apt.SourceCode;
import eu.eventstorm.core.apt.model.ElsQueryDescriptor;
import eu.eventstorm.cqrs.els.ElsRepository;
import eu.eventstorm.sql.apt.log.Logger;
import eu.eventstorm.util.ToStringBuilder;
import reactor.core.publisher.Mono;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;

import static eu.eventstorm.sql.apt.Helper.writeGenerated;
import static eu.eventstorm.sql.apt.Helper.writeNewLine;
import static eu.eventstorm.sql.apt.Helper.writePackage;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class ElasticRepositoryGenerator {

    private static final String TO_STRING_BUILDER = ToStringBuilder.class.getName();

    private Logger logger;

    public void generate(ProcessingEnvironment processingEnvironment, SourceCode sourceCode) {

        try (Logger logger = Logger.getLogger(processingEnvironment, "eu.eventstorm.event.query.els", "ElasticRepositoryGenerator")) {
            this.logger = logger;
            sourceCode.forEachElasticSearchQuery(t -> {
                try {
                    generate(processingEnvironment, t);
                } catch (Exception cause) {
                    logger.error("Exception for [" + t + "] -> [" + cause.getMessage() + "]", cause);
                }
            });
        }
    }

    private void generate(ProcessingEnvironment env, ElsQueryDescriptor descriptor) throws IOException {
        JavaFileObject object = env.getFiler().createSourceFile(descriptor.getPackage() + ".Abstract" + descriptor.simpleName() + "Repository");
        Writer writer = object.openWriter();

        writeHeader(writer, env, descriptor);
        //writeVariables(writer, descriptor);
        writeConstructor(writer, descriptor);

        writeMethods(writer, descriptor);

        writer.write("}");
        writer.close();
    }

    private static void writeHeader(Writer writer, ProcessingEnvironment env, ElsQueryDescriptor descriptor) throws IOException {

        writePackage(writer, env.getElementUtils().getPackageOf(descriptor.element()).toString());
        writeNewLine(writer);


        writer.write("import ");
        writer.write(Mono.class.getName()+";");
        //writeNewLine(writer);
        //writer.write("import " + SqlQuery.class.getName() + ";");
        writeNewLine(writer);

        /*for (PojoPropertyDescriptor id : descriptor.ids()) {
            writer.write("import static ");
            writer.write(descriptor.fullyQualidiedClassName() + "Descriptor.");
            writer.write(toUpperCase(id.name()));
            writer.write(";");
            writeNewLine(writer);
        }
        */


        /*for (PojoPropertyDescriptor bk : descriptor.businessKeys()) {
            writer.write("import static ");
            writer.write(descriptor.fullyQualidiedClassName() + "Descriptor.");
            writer.write(toUpperCase(bk.name()));
            writer.write(";");
            writeNewLine(writer);
        }

        writer.write("import static ");
        writer.write(Expressions.class.getName() + ".eq;");
        writeNewLine(writer);
        writer.write("import static ");
        writer.write(Expressions.class.getName() + ".and;");
        writeNewLine(writer);
*/

        writeGenerated(writer, ElasticRepositoryGenerator.class.getName());

        writer.write("public abstract class Abstract");
        writer.write(descriptor.simpleName() + "Repository");
        writer.write(" extends ");
        writer.write(ElsRepository.class.getName());
        writer.write(" {");
        writeNewLine(writer);
    }

    private static void writeConstructor(Writer writer, ElsQueryDescriptor descriptor) throws IOException {
        writer.write("    protected Abstract");
        writer.write(descriptor.simpleName() + "Repository(");
        writer.write("co.elastic.clients.elasticsearch.ElasticsearchAsyncClient");
        writer.write(" client) {");
        writeNewLine(writer);
        writer.write("        super(client);");
        writeNewLine(writer);


        //generateFindById(writer, descriptor);
        //generateFindByIdForUpdate(writer, descriptor);
        //generateFindByBusinessKey(writer, descriptor);
        //generateInsert(writer, descriptor);
        //generateUpdate(writer, descriptor);
        //generateDelete(writer, descriptor);


        writer.write("    }");
        writeNewLine(writer);
    }


    private static void writeMethods(Writer writer, ElsQueryDescriptor descriptor) throws IOException {
        generateMethodFindById(writer, descriptor);
        generateMethodInsert(writer, descriptor);
        /*
        generateMethodFindByIdForUpdate(writer, descriptor);

        generateMethodUpdate(writer, descriptor);
        generateMethodBatch(writer, descriptor);
        generateMethodFindByBusinessKey(writer, descriptor);
        generateMethodDelete(writer, descriptor);
        //generateMethodPage(writer, descriptor);
        generateMethodLink(writer, descriptor);
        generateMethodUnlink(writer, descriptor);*/
    }

    private static void generateMethodFindById(Writer writer, ElsQueryDescriptor descriptor) throws IOException {

        writeNewLine(writer);
        writer.write("    public final Mono<");
        writer.write(descriptor.element().toString());
        writer.write("> findById(String id) {");
        writeNewLine(writer);

        writer.write("        return super.doFindById(\"" + descriptor.indice().name()+"\", id, " + descriptor.simpleName() + ".class);");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);

    }

    private static void generateMethodInsert(Writer writer, ElsQueryDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writer.write("    public final Mono<co.elastic.clients.elasticsearch.core.IndexResponse>");
        writer.write(" insert(String id, "+descriptor.element().toString()+" pojo) {");
        writeNewLine(writer);

        writer.write("        return super.doInsert(\"" + descriptor.indice().name()+"\", id, pojo);");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
    }
    /*
    private static void generateFindById(Writer writer, ElsQueryDescriptor descriptor) throws IOException {


        writer.write("        this.findById = select(ALL).from(TABLE).where(");

        if (descriptor.ids().size() == 1) {
            writer.write("eq(");
            writer.write(toUpperCase(descriptor.ids().get(0).name()));
            writer.write(")");
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append("and(");
            for (PojoPropertyDescriptor id : descriptor.ids()) {
                builder.append("eq(");
                builder.append(toUpperCase(id.name()));
                builder.append("),");
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.append(")");
            writer.write(builder.toString());
        }
        writer.write(").build();");
        writeNewLine(writer);

    }*/

}