package eu.eventstorm.eventstore.io;

import java.io.IOException;

import org.springframework.core.io.buffer.DataBuffer;

import com.google.protobuf.AbstractMessage;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface Deserializer<T extends AbstractMessage> {

	T deserialize(DataBuffer buffer) throws IOException;
	
}
