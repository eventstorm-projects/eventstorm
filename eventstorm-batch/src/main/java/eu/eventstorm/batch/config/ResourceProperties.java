package eu.eventstorm.batch.config;

import java.io.File;

import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@ConfigurationProperties(prefix = "eu.eventstorm.batch.resource")
public class ResourceProperties {

	private String contextPath;
	private String temporaryDirectory = System.getProperty("java.io.tmpdir") + File.separator + "batch";

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	public String getTemporaryDirectory() {
		return this.temporaryDirectory;
	}

	public void setTemporaryDirectory(String temporaryDirectory) {
		this.temporaryDirectory = temporaryDirectory;
	}
	
}
