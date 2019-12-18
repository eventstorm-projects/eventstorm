package eu.eventsotrm.sql.apt;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import eu.eventsotrm.sql.apt.analyser.GlobalConfigurationAnalyser;
import eu.eventsotrm.sql.apt.analyser.JoinTableAnalyser;
import eu.eventsotrm.sql.apt.analyser.SqlInterfaceAnalyser;
import eu.eventsotrm.sql.apt.analyser.ViewAnalyser;
import eu.eventsotrm.sql.apt.flyway.FlywayGenerator;
import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;
import eu.eventsotrm.sql.apt.model.GlobalConfigurationDescriptor;
import eu.eventsotrm.sql.apt.model.PojoDescriptor;
import eu.eventsotrm.sql.apt.model.ViewDescriptor;
import eu.eventstorm.sql.annotation.GlobalConfiguration;
import eu.eventstorm.sql.annotation.JoinTable;
import eu.eventstorm.sql.annotation.Table;
import eu.eventstorm.sql.annotation.View;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"eu.eventstorm.sql.annotation.Table","eu.eventstorm.sql.annotation.View"})
public final class SqlProcessor extends AbstractProcessor {

    private ProcessingEnvironment processingEnvironment;

    private boolean firstTime = false;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.processingEnvironment = processingEnv;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        if (firstTime) {
            this.processingEnvironment.getMessager().printMessage(Diagnostic.Kind.NOTE, "SqlProcessor done");
            return false;
        }

    	LoggerFactory.getInstance().init(processingEnv, "eu.eventstorm.report","sql-output.txt");

    	Logger logger = LoggerFactory.getInstance().getLogger(SqlProcessor.class);
    	logger.info("SqlProcessor start");

        doProcess(roundEnv);

    	logger.info("SqlProcessor end");
        this.firstTime = true;

        LoggerFactory.getInstance().close();

        return true;
    }

    private void doProcess(RoundEnvironment roundEnvironment) {

    	Logger logger = LoggerFactory.getInstance().getLogger(SqlProcessor.class);

    	
        List<PojoDescriptor> descriptors = roundEnvironment.getElementsAnnotatedWith(Table.class)
                .stream()
                .map(new SqlInterfaceAnalyser())
                .collect(Collectors.toList());

        List<PojoDescriptor> joinTableDescriptors = roundEnvironment.getElementsAnnotatedWith(JoinTable.class)
                .stream()
                .map(new JoinTableAnalyser(descriptors))
                .collect(Collectors.toList());
        
        List<ViewDescriptor> viewDescriptors = roundEnvironment.getElementsAnnotatedWith(View.class)
                .stream()
                .map(new ViewAnalyser())
                .collect(Collectors.toList());

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

        logger.info("Global configuration found : " + elements);

        List<GlobalConfigurationDescriptor> configs = roundEnvironment.getElementsAnnotatedWith(GlobalConfiguration.class)
            	.stream()
            	.map(new GlobalConfigurationAnalyser(sourceCode))
            	.collect(Collectors.toList());

        new FlywayGenerator().generate(this.processingEnv, configs);

    }

}