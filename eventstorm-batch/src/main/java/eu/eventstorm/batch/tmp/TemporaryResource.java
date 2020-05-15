package eu.eventstorm.batch.tmp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public final class TemporaryResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(TemporaryResource.class);
	
	private final TemporaryResourceProperties properties;
	
	public TemporaryResource(TemporaryResourceProperties properties) {
		this.properties = properties;
	}

	public Path touch(String uuid) throws IOException {
		
		Path file = Paths.get(properties.getBaseDirectory(), uuid);
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("touch({})", file);
		}
		
		return file;
	}

	public void delete(String uuid) throws IOException {
		
		Path file = Paths.get(properties.getBaseDirectory(), uuid);
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("delete({})", file);
		}
		
		Files.delete(file);
		
	}

}