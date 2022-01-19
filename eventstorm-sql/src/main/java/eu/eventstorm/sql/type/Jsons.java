package eu.eventstorm.sql.type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eu.eventstorm.sql.JsonMapper;
import eu.eventstorm.sql.type.common.DefaultJsonList;
import eu.eventstorm.sql.type.common.DefaultJsonMap;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class Jsons {

	private Jsons() {
	}
	
	public static Json createMap() {
		return new DefaultJsonMap();
	}

	public static Json createMap(Map<String, ?> map) {
		return new DefaultJsonMap(map);
	}

	public static Json createMap(Map<String, ?> map, JsonMapper mapper) {
		return new DefaultJsonMap(map, mapper);
	}

	@SuppressWarnings("rawtypes")
	public static Json createList() {
		return new DefaultJsonList();
	}

	@SuppressWarnings("rawtypes")
	public static Json createList(JsonMapper mapper) {
		return new DefaultJsonList(new ArrayList(), mapper);
	}

	@SuppressWarnings("rawtypes")
	public static Json createList(List<?> list) {
		return new DefaultJsonList(list);
	}

	@SuppressWarnings("rawtypes")
	public static Json createList(List<?> list, JsonMapper mapper) {
		return new DefaultJsonList(list, mapper);
	}
	
}
