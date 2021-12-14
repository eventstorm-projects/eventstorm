package eu.eventstorm.test;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class LoggerInstancePostProcessor implements TestInstancePostProcessor {

    @Override
    public void postProcessTestInstance(Object o, ExtensionContext extensionContext) throws Exception {
        String level = System.getProperty("logback.level");
        if (level != null) {
            configLoggers(Level.valueOf(level));
        }

        // The job exceeded the maximum log length, and has been terminated.
        String travis = System.getProperty("travis");
        if (travis != null) {
            //removeAllAppenders();
        }
    }

    private static void configLoggers(Level level) {
        if (level != null) {
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            for (Logger lc : loggerContext.getLoggerList()) {
                if (lc.getName().startsWith("org") || lc.getName().startsWith("com") || lc.getName().startsWith("io")) {
                    lc.setLevel(Level.INFO);
                } else {
                    lc.setLevel(level);
                }
            }
        }
    }

    private void removeAllAppenders() {
		/*LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		Configuration config = ctx.getConfiguration();
		LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
		loggerConfig.getAppenderRefs().forEach(a -> {
			loggerConfig.removeAppender(a.getRef());
			for (LoggerConfig lc : config.getLoggers().values()) {
				lc.removeAppender(a.getRef());
			}
		});
		ctx.updateLoggers();
		*/
    }

}