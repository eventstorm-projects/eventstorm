package eu.eventstorm.cqrs.web;

import javax.annotation.Generated;

import com.fasterxml.jackson.databind.module.SimpleModule;

import eu.eventstorm.sql.page.Page;

@Generated("eu.eventsotrm.core.apt.query.QueryJacksonModuleGenerator")
@SuppressWarnings("serial")
public final class PageModule extends SimpleModule {

    public PageModule() {
        super();
        addSerializer(Page.class, new PageStdSerializer());
    }
}