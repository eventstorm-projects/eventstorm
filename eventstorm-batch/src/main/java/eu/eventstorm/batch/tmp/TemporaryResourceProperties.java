package eu.eventstorm.batch.tmp;

import java.io.File;

import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@ConfigurationProperties(prefix = "eu.eventstorm.batch.temporary")
public class TemporaryResourceProperties {

	private String contextPath;
	private String baseDirectory = System.getProperty("java.io.tmpdir") + File.separator + "batch";

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	public String getBaseDirectory() {
		return this.baseDirectory;
	}

	public void setBaseDirectory(String baseDirectory) {
		this.baseDirectory = baseDirectory;
	}
	
}
