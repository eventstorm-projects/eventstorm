package eu.eventstorm.cqrs.els.page;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface ElsPageRequestDescriptor {

    ElsField get(String property);

}
