package eu.eventstorm.batch.tmp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Component
public final class TemporaryResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(TemporaryResource.class);
	
	private final TemporaryResourceProperties properties;
	
	public TemporaryResource(TemporaryResourceProperties properties) {
		this.properties = properties;
		
		Path path = Paths.get(properties.getBaseDirectory());
		
		if (!Files.exists(path)) {
			try {
				Files.createDirectories(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public Path touch(String uuid) throws IOException {
		
		Path file = Paths.get(properties.getBaseDirectory(), uuid);
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("touch({})", file);
		}
		
		return file;
	}
	
	public Path get(String uuid) {
		return Paths.get(properties.getBaseDirectory(), uuid);
	}

	public void delete(String uuid) throws IOException {
		
		Path file = Paths.get(properties.getBaseDirectory(), uuid);
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("delete({})", file);
		}
		
		Files.delete(file);
		
	}

}