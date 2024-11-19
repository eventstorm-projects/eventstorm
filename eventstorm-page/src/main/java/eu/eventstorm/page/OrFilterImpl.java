package eu.eventstorm.page;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class OrFilterImpl implements OrFilter {

    private final Filter left;
    private final Filter right;

    OrFilterImpl(Filter left, Filter right) {
        this.left = left;
        this.right = right;
    }

    public Filter getLeft() {
        return left;
    }

    public Filter getRight() {
        return right;
    }

    @Override
    public void accept(FilterVisitor visitor) {
        visitor.visitBegin(this);
        left.accept(visitor);
        right.accept(visitor);
        visitor.visitEnd(this);
    }
}
