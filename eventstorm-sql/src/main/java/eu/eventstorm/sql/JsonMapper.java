package eu.eventstorm.sql;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface JsonMapper {

    Map<String, Object> readMap(byte[] content) throws IOException;

    Map<String, Object> readMap(String content) throws IOException;

    <T> List<T> readList(byte[] buf, Class<T> type) throws IOException;

    <T> List<T> readList(String content, Class<T> type) throws IOException;

    byte[] write(Map<String, Object> map) throws IOException;

    String writeAsString(Map<String, Object> map) throws IOException;

    byte[] write(List<?> list) throws IOException;

    String writeAsString(List<?> list) throws IOException;

}
