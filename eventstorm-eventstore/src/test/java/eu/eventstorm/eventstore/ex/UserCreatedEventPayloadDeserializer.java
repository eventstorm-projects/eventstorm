package eu.eventstorm.eventstore.ex;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import eu.eventstorm.core.json.Deserializer;

public final class UserCreatedEventPayloadDeserializer implements Deserializer<UserCreatedEventPayload> {
	@Override
	public UserCreatedEventPayload deserialize(byte[] bytes) {
		ObjectMapper mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();
	//	module.addDeserializer(UserCreatedEventPayload.class, new UserCreatedEventPayloadStdDeserializer());
		mapper.registerModule(module);
		try {
			return mapper.readValue(bytes, UserCreatedEventPayload.class);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
