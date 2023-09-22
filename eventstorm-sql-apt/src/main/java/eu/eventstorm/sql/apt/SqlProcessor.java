package eu.eventstorm.sql.apt;

import eu.eventstorm.sql.annotation.GlobalConfiguration;
import eu.eventstorm.sql.annotation.JoinTable;
import eu.eventstorm.sql.annotation.Table;
import eu.eventstorm.sql.annotation.View;
import eu.eventstorm.sql.apt.analyser.GlobalConfigurationAnalyser;
import eu.eventstorm.sql.apt.analyser.JoinTableAnalyser;
import eu.eventstorm.sql.apt.analyser.TableAnalyser;
import eu.eventstorm.sql.apt.analyser.ViewAnalyser;
import eu.eventstorm.sql.apt.liquibase.LiquibaseGenerator;
import eu.eventstorm.sql.apt.log.Logger;
import eu.eventstorm.sql.apt.model.GlobalConfigurationDescriptor;
import eu.eventstorm.sql.apt.model.PojoDescriptor;
import eu.eventstorm.sql.apt.model.ViewDescriptor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SupportedSourceVersion(SourceVersion.RELEASE_11)
@SupportedAnnotationTypes({"eu.eventstorm.sql.annotation.GlobalConfiguration","eu.eventstorm.sql.annotation.Table","eu.eventstorm.sql.annotation.View"})
public final class SqlProcessor extends AbstractProcessor {

    private boolean firstTime = false;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        this.processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,"Start SqlProcessor");

        if (firstTime) {
            this.processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "SqlProcessor done");
            return true;
        }

        try (Logger logger = Logger.getLogger(processingEnv, "eu.eventstorm.sql", "SqlProcessor")) {
            Logger.setMainLogger(logger);
            logger.info("SqlProcessor start");

            doProcess(roundEnv);

            logger.info("SqlProcessor end");
            this.firstTime = true;
        }

        return true;
    }

    private void doProcess(RoundEnvironment roundEnvironment) {

        List<PojoDescriptor> descriptors;
        List<PojoDescriptor> joinTableDescriptors;
        List<ViewDescriptor> viewDescriptors;

        try (TableAnalyser tableAnalyser =  new TableAnalyser(this.processingEnv)) {
            descriptors = roundEnvironment.getElementsAnnotatedWith(Table.class)
                    .stream()
                    .map(tableAnalyser)
                    .collect(Collectors.toList());

        }

        try (JoinTableAnalyser analyser =  new JoinTableAnalyser(this.processingEnv, descriptors)) {
            joinTableDescriptors = roundEnvironment.getElementsAnnotatedWith(JoinTable.class)
                    .stream()
                    .map(analyser)
                    .collect(Collectors.toList());

        }


        try (ViewAnalyser analyser =  new ViewAnalyser(this.processingEnv)) {
            viewDescriptors = roundEnvironment.getElementsAnnotatedWith(View.class)
                    .stream()
                    .map(analyser)
                    .collect(Collectors.toList());
        }


        SourceCode sourceCode = new SourceCodeBuilder(this.processingEnv)
                .withTableDescriptors(descriptors)
                .withJoinTableDescriptors(joinTableDescriptors)
                .withViewDescriptors(viewDescriptors)
                .build();

        sourceCode.dump();

        new PojoImplementationGenerator().generate(this.processingEnv, sourceCode);
        new PojoFactoryGenerator().generate(this.processingEnv, sourceCode);
        new PojoDescriptorGenerator().generate(this.processingEnv, sourceCode);
        new PojoBuilderGenerator().generate(this.processingEnv, sourceCode);
        new RepositoryGenerator().generate(this.processingEnv, sourceCode);
        new MapperGenerator().generate(this.processingEnv, sourceCode);
        new PojoMapperFactoryGenerator().generate(this.processingEnv, sourceCode);
        new ModuleGenerator().generate(this.processingEnv, sourceCode);


        new ViewImplementationGenerator().generate(processingEnv, sourceCode);
        new ViewDescriptorGenerator().generate(processingEnv, sourceCode);
        new ViewRepositoryGenerator().generate(processingEnv, sourceCode);
        new ViewMapperGenerator().generate(processingEnv, sourceCode);
        new ViewMapperFactoryGenerator().generate(processingEnv, sourceCode);

        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(GlobalConfiguration.class);

        List<GlobalConfigurationDescriptor> configs = roundEnvironment.getElementsAnnotatedWith(GlobalConfiguration.class)
                .stream()
                .map(new GlobalConfigurationAnalyser(this.processingEnv, sourceCode))
                .collect(Collectors.toList());

        new LiquibaseGenerator(sourceCode).generate(this.processingEnv, configs);

    }

}