package eu.eventstorm.sql.apt.util;

import eu.eventstorm.util.ToStringBuilder;

public final class Tuple<X, Y> {

	public final X x;
	public final Y y;

	public Tuple(X x, Y y) {
		this.x = x;
		this.y = y;
	}

	public X getX() {
		return x;
	}

	public Y getY() {
		return y;
	}

    @Override
    public String toString() {
        return new ToStringBuilder(this, true)
            .append("x", x)
            .append("y", y)
            .toString();
    }

}
