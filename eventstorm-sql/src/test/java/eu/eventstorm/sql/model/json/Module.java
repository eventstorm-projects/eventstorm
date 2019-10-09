package eu.eventstorm.sql.model.json;

public final class Module extends eu.eventstorm.sql.Module { 

     public Module(String name, String catalog) {
         super(name, catalog, SpanDescriptor.INSTANCE);
    }
}