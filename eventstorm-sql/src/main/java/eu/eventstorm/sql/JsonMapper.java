package eu.eventstorm.sql;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface JsonMapper {

	Map<String, Object> readMap(byte[] content) throws IOException;

	<T> List<T> readList(byte[] buf, Class<T> type) throws IOException;
	
	byte[] write(Map<String, Object> map) throws IOException;

	byte[] write(List<?> list) throws IOException;


}
