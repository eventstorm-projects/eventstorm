package eu.eventstorm.page;

public interface AndFilter extends Filter {

    Filter getLeft();

    Filter getRight();

}
