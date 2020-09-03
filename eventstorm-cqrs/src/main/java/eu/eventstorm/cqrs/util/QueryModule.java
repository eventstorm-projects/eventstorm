package eu.eventstorm.cqrs.util;

import com.fasterxml.jackson.databind.module.SimpleModule;

import eu.eventstorm.sql.type.Json;

@SuppressWarnings("serial")
public final class QueryModule extends SimpleModule {

    public QueryModule() {
        super();
        addSerializer(Json.class, new JsonStdSerializer());
    }
}