package eu.eventstorm.cqrs.context;

import java.util.HashMap;
import java.util.Map;

import eu.eventstorm.cqrs.CommandContext;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public class DefaultCommandContext implements CommandContext {

	private final Map<String, Object> props;
	
	public DefaultCommandContext() {
		this.props = new HashMap<String, Object>();
	}
	
	@Override
	public <T> void put(String key, T value) {
		this.props.put(key, value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(String key) {
		return (T)this.props.get(key);
	}

}
