package eu.eventstorm.page;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface Sort {

    boolean isAscending();

    String getProperty();

    static Sort desc(String property){
        return new SortImpl(false, property);
    }

    static Sort asc(String property){
        return new SortImpl(true, property);
    }

}

