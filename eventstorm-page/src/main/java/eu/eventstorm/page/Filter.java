package eu.eventstorm.page;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface Filter {

    void accept(FilterVisitor visitor);

}