package eu.eventsotrm.core.apt.analyser;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;

import eu.eventsotrm.core.apt.model.EmbeddedCommandDescriptor;
import eu.eventsotrm.core.apt.model.PropertyDescriptor;
import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class CqrsEmbeddedCommandAnalyser implements Function<Element, EmbeddedCommandDescriptor> {

	private final Logger logger;

	public CqrsEmbeddedCommandAnalyser() {
		this.logger = LoggerFactory.getInstance().getLogger(CqrsEmbeddedCommandAnalyser.class);
	}

	@Override
	public EmbeddedCommandDescriptor apply(Element element) {

		try {
			return doApply(element);
		} catch (Exception cause) {
			this.logger.error(cause.getMessage(), cause);
			// throw new AnalyserException(cause);
			return null;
		}

	}

	public EmbeddedCommandDescriptor doApply(Element element) {

		if (ElementKind.INTERFACE != element.getKind()) {
			logger.error("element [" + element + "] should be an interface");
			return null;
		}

		logger.info("Analyse " + element);

		List<PropertyDescriptor> properties = new ArrayList<>();

		for (Element method : element.getEnclosedElements()) {

			if (ElementKind.METHOD != method.getKind()) {
				logger.error("element [" + method + "] in [" + element + "] is not a method, it's [" + element.getKind() + "]");
				return null;
			}

			ExecutableElement executableElement = (ExecutableElement) method;

			if (executableElement.getSimpleName().toString().startsWith("get")) {
				properties.add(new PropertyDescriptor(executableElement));
				continue;
			}

			throw new IllegalStateException("method [" + method + "] doesn't start with 'get'");

		}

		return new EmbeddedCommandDescriptor(element, properties);
	}

}