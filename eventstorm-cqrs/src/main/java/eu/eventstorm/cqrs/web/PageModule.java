package eu.eventstorm.cqrs.web;

import com.fasterxml.jackson.databind.module.SimpleModule;
import eu.eventstorm.sql.page.Page;

@SuppressWarnings("serial")
public final class PageModule extends SimpleModule {

    public PageModule() {
        super();
        addSerializer(Page.class, new PageStdSerializer());
    }
}