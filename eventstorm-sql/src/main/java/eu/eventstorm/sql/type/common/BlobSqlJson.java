package eu.eventstorm.sql.type.common;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;

import eu.eventstorm.sql.type.Json;

public final class BlobSqlJson extends DefaultBlob implements Json {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BlobSqlJson.class);

	private static final ObjectMapper MAPPER = new ObjectMapper();

	private Map<String, Object> map;

	private boolean isModified = false;

	public BlobSqlJson(byte[] buf) {
		super(buf);
	}

	public BlobSqlJson(Map<String, Object> value) throws IOException {
		super(MAPPER.writeValueAsBytes(value));
		this.map = value;
	}

	//@Override
	public ImmutableSet<String> keys() {
		checkInit();
		return ImmutableSet.copyOf(this.map.keySet());
	}

	//@Override
	public Object get(String key) {
		checkInit();
		return this.map.get(key);
	}

	@Override
	public void put(String key, Object value) {
		isModified = true;
		checkInit();
		this.map.put(key, value);
	}

	//@Override
	public void remove(String key) {
		isModified = true;
		checkInit();
		this.map.remove(key);
	}

	@SuppressWarnings("unchecked")
	private void checkInit() {
		if (this.map == null) {
			if (this.length() == 0) {
				this.map = new HashMap<>();
				return;
			}
			try {
				this.map = MAPPER.readValue(super.getBinaryStream(), Map.class);
			} catch (IOException cause) {
				LOGGER.error("Cannot read buffer",cause);
		//		throw new CoreSqlException(CoreSqlExceptionType.CANNOT_READ_BUFFER,
		//		        ImmutableMap.of("cause", Optional.ofNullable(cause.getMessage())));
			}
		}

	}

	//@Override
	public void flush() {
		if (isModified) {
			try {
				setBuf(MAPPER.writeValueAsBytes(this.map));
			} catch (IOException cause) {
				LOGGER.error("Failed to write map [{}] - cause: [{}]", this.map, cause);
			//	throw new CoreSqlException(CoreSqlExceptionType.WRITE_MAP, ImmutableMap.of("map",
			//	        Optional.ofNullable(this.map), "cause", Optional.ofNullable(cause.getMessage())));
			}
		}
	}

	@Override
	public <T> T get(String key, Class<T> clazz) {
		checkInit();
		return clazz.cast(this.map.get(key));
	}
}