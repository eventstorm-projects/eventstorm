package eu.eventsotrm.sql.apt.analyser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import javax.lang.model.element.Element;

import eu.eventsotrm.sql.apt.SourceCode;
import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;
import eu.eventsotrm.sql.apt.model.GlobalConfigurationDescriptor;
import eu.eventsotrm.sql.apt.model.PojoDescriptor;
import eu.eventstorm.sql.annotation.GlobalConfiguration;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class GlobalConfigurationAnalyser implements Function<Element, GlobalConfigurationDescriptor> {

	private final SourceCode sourceCode;
	private final Logger logger;

	public GlobalConfigurationAnalyser(SourceCode sourceCode) {
		this.sourceCode = sourceCode;
		this.logger = LoggerFactory.getInstance().getLogger(GlobalConfigurationAnalyser.class);
	}

	@Override
	public GlobalConfigurationDescriptor apply(Element element) {

		logger.info("Analyse Global Configuration " + element);

		GlobalConfiguration gc = element.getAnnotation(GlobalConfiguration.class);

		List<PojoDescriptor> found = new ArrayList<>();
        List<PojoDescriptor> all = new ArrayList<>(sourceCode.all());

		for (String pack : gc.flywayConfiguration().packages()) {

			Iterator<PojoDescriptor> it = all.iterator();
			while (it.hasNext()) {
				PojoDescriptor desc = it.next();
				if (desc.element().toString().startsWith(pack)) {
					found.add(desc);
					it.remove();
				}
			}
		}

		all.forEach(desc -> {
			logger.error("No GlogalConfiguration found for " + desc.fullyQualidiedClassName());
		});

		return new GlobalConfigurationDescriptor(element, found, gc);
	}

}
