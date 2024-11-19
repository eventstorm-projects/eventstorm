package eu.eventstorm.page;

public final class EmptyFilter implements Filter {

    public static final Filter INSTANCE = new EmptyFilter();

    private EmptyFilter() {
    }

    @Override
    public void accept(FilterVisitor visitor) {
    }
}
