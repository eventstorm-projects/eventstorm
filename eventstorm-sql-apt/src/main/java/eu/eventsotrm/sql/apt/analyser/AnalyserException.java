package eu.eventsotrm.sql.apt.analyser;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SuppressWarnings("serial")
public class AnalyserException extends RuntimeException {

    public AnalyserException(String message) {
		super(message);
	}

	public AnalyserException(Throwable cause) {
        super(cause);
	}

}