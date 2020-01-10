package eu.eventsotrm.core.apt.analyser;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;

import eu.eventsotrm.core.apt.model.CommandDescriptor;
import eu.eventsotrm.core.apt.model.CommandPropertyDescriptor;
import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class CqrsCommandAnalyser implements Function<Element, CommandDescriptor> {

	private final Logger logger;

	public CqrsCommandAnalyser() {
		this.logger = LoggerFactory.getInstance().getLogger(CqrsCommandAnalyser.class);
	}

	@Override
	public CommandDescriptor apply(Element element) {

		try {
			return doApply(element);
		} catch (Exception cause) {
			this.logger.error(cause.getMessage(), cause);
			// throw new AnalyserException(cause);
			return null;
		}

	}

	public CommandDescriptor doApply(Element element) {

		if (ElementKind.INTERFACE != element.getKind()) {
			logger.error("element [" + element + "] should be an interface");
			return null;
		}

		logger.info("Analyse " + element);

		List<CommandPropertyDescriptor> properties = new ArrayList<>();

		for (Element method : element.getEnclosedElements()) {

			if (ElementKind.METHOD != method.getKind()) {
				logger.error("element [" + method + "] in [" + element + "] is not a method, it's [" + element.getKind() + "]");
				return null;
			}

			ExecutableElement executableElement = (ExecutableElement) method;

			if (executableElement.getSimpleName().toString().startsWith("get")) {
				properties.add(new CommandPropertyDescriptor(executableElement));
				continue;
			}

			throw new IllegalStateException("method [" + method + "] doesn't start with 'get'");

		}

		return new CommandDescriptor(element, properties);
	}

}