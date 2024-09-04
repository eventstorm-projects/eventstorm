package eu.eventstorm.page;

public interface FilterVisitor {

    default void visit(SinglePropertyFilter filter) {
    }

    default void visitBegin(AndFilter filter) {}
    default void visitEnd(AndFilter filter ){};

    default void visitBegin(OrFilter filter) {}
    default void visitEnd(OrFilter filter ){};

    default void visitBegin(MultiAndFilter filter) {}
    default void visitEnd(MultiAndFilter filter ){};
}