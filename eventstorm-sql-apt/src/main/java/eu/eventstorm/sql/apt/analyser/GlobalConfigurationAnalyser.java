package eu.eventstorm.sql.apt.analyser;

import eu.eventstorm.sql.annotation.GlobalConfiguration;
import eu.eventstorm.sql.apt.SourceCode;
import eu.eventstorm.sql.apt.log.Logger;
import eu.eventstorm.sql.apt.model.GlobalConfigurationDescriptor;
import eu.eventstorm.sql.apt.model.PojoDescriptor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class GlobalConfigurationAnalyser implements Function<Element, GlobalConfigurationDescriptor> {

    private final SourceCode sourceCode;
    private final Logger logger;

    public GlobalConfigurationAnalyser(ProcessingEnvironment processingEnv, SourceCode sourceCode) {
        this.sourceCode = sourceCode;
        this.logger = Logger.getLogger(processingEnv, "eu.eventstorm.sql.analyser", "GlobalConfigurationAnalyser");
    }

    @Override
    public GlobalConfigurationDescriptor apply(Element element) {

        logger.info("Analyse Global Configuration " + element);

        try {
            GlobalConfiguration gc = element.getAnnotation(GlobalConfiguration.class);

            List<PojoDescriptor> found = new ArrayList<>();
            List<PojoDescriptor> all = new ArrayList<>(sourceCode.all());

            for (String pack : gc.packages()) {
                logger.info("\tfound package [" + pack + "]");
                Iterator<PojoDescriptor> it = all.iterator();
                while (it.hasNext()) {
                    PojoDescriptor desc = it.next();
                    if (desc.element().toString().startsWith(pack)) {
                        logger.info("\t\tfound add [" + desc.element() + "]");
                        found.add(desc);
                        it.remove();
                    }
                }
            }

            all.forEach(desc -> {
                logger.error("No GlogalConfiguration found for " + desc.fullyQualidiedClassName());
            });

            return new GlobalConfigurationDescriptor(element, found, gc);


        } finally {
            logger.close();
        }

    }

}
