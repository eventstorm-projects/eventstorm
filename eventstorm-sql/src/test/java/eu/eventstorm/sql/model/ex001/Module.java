package eu.eventstorm.sql.model.ex001;

public final class Module extends eu.eventstorm.sql.Module {

     public Module(String name, String catalog) {
         super(name, catalog, StudentDescriptor.INSTANCE);
    }
}