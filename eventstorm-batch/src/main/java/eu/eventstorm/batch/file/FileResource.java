package eu.eventstorm.batch.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eu.eventstorm.batch.config.ResourceProperties;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Component
public final class FileResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileResource.class);
	
	private final ResourceProperties properties;
	
	public FileResource(ResourceProperties properties) {
		this.properties = properties;
		
		Path path = Paths.get(properties.getTemporaryDirectory());
		
		if (!Files.exists(path)) {
			try {
				Files.createDirectories(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public Path touch(String uuid) throws IOException {
		
		Path file = Paths.get(properties.getTemporaryDirectory(), uuid);
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("touch({})", file);
		}
		
		return file;
	}
	
	public Path get(String uuid) {
		return Paths.get(properties.getTemporaryDirectory(), uuid);
	}

	public void delete(String uuid) throws IOException {
		
		Path file = Paths.get(properties.getTemporaryDirectory(), uuid);
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("delete({})", file);
		}
		
		Files.delete(file);
		
	}

}