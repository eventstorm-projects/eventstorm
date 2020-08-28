package eu.eventstorm.sql.type;

import java.util.List;
import java.util.Map;

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
	
	public static Json createList() {
		return new DefaultJsonList();
	}
	
	public static Json createMap(Map<String, ?> map) {
		return new DefaultJsonMap(map);
	}
	
	public static Json createList(List<?> list) {
		return new DefaultJsonList(list);
	}
	
}
