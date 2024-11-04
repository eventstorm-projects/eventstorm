package eu.eventstorm.core.apt.query.els;

import eu.eventstorm.core.apt.SourceCode;
import eu.eventstorm.core.apt.model.ElsQueryDescriptor;
import eu.eventstorm.cqrs.els.ElsRepository;
import eu.eventstorm.page.Page;
import eu.eventstorm.page.PageRequest;
import eu.eventstorm.sql.apt.log.Logger;
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
        writer.write(Mono.class.getName() + ";");
        //writeNewLine(writer);
        //writer.write("import " + SqlQuery.class.getName() + ";");
        writeNewLine(writer);

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
        writer.write("co.elastic.clients.elasticsearch.ElasticsearchAsyncClient client) {");
        writeNewLine(writer);
        writer.write("        super(client);");
        writeNewLine(writer);

        writer.write("    }");
        writeNewLine(writer);

        writer.write("    protected Abstract");
        writer.write(descriptor.simpleName() + "Repository(");
        writer.write("co.elastic.clients.elasticsearch.ElasticsearchAsyncClient client, java.util.function.Function<String, String> indexResolver) {");
        writeNewLine(writer);
        writer.write("        super(client, indexResolver);");
        writeNewLine(writer);

        writer.write("    }");
        writeNewLine(writer);
    }


    private static void writeMethods(Writer writer, ElsQueryDescriptor descriptor) throws IOException {
        generateMethodFindById(writer, descriptor);
        generateMethodFind(writer, descriptor);
        generateMethodInsert(writer, descriptor);
        generateMethodUpdate(writer, descriptor);
        generateMethodPartialUpdate(writer, descriptor);

        /*
        generateMethodFindByIdForUpdate(writer, descriptor);

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

        writer.write("        return super.doFindById(\"" + descriptor.indice().name() + "\", id, " + descriptor.simpleName() + ".class);");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);

    }

    private static void generateMethodFind(Writer writer, ElsQueryDescriptor descriptor) throws IOException {

        writeNewLine(writer);
        writer.write("    public final Mono<" + Page.class.getName() + "<");
        writer.write(descriptor.element().toString());
        writer.write(">> find(" + PageRequest.class.getName() + " pageRequest) {");
        writeNewLine(writer);

        writer.write("        return super.doSelectPage(\"" + descriptor.indice().name() + "\", pageRequest, " + descriptor.simpleName() + ".class);");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);

    }

    private static void generateMethodInsert(Writer writer, ElsQueryDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writer.write("    public final Mono<co.elastic.clients.elasticsearch.core.IndexResponse>");
        writer.write(" insert(String id, " + descriptor.element().toString() + " pojo) {");
        writeNewLine(writer);

        writer.write("        return super.doInsert(\"" + descriptor.indice().name() + "\", id, pojo);");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
    }

    private static void generateMethodUpdate(Writer writer, ElsQueryDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writer.write("    public final Mono<co.elastic.clients.elasticsearch.core.UpdateResponse<" + descriptor.element().toString() + ">>");
        writer.write(" update(String id, " + descriptor.element().toString() + " pojo) {");
        writeNewLine(writer);

        writer.write("        return super.doUpdate(\"" + descriptor.indice().name() + "\", id, pojo, " + descriptor.element().toString() + ".class);");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
    }

    private static void generateMethodPartialUpdate(Writer writer, ElsQueryDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writer.write("    public final Mono<co.elastic.clients.elasticsearch.core.UpdateResponse<" + descriptor.element().toString() + ">>");
        writer.write(" partialUpdate(String id, java.util.Map<String, Object> partialDocument) {");
        writeNewLine(writer);

        writer.write("        return super.doPartialUpdate(\"" + descriptor.indice().name() + "\", id, partialDocument, " + descriptor.element().toString() + ".class);");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
    }

}