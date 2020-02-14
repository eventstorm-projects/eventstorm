package eu.eventstorm.util.tuple;

import static java.util.Objects.requireNonNull;

import eu.eventstorm.util.ToStringBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class Tuple3Impl<T1, T2, T3> implements Tuple3<T1, T2, T3> {

	private final T1 t1;
	private final T2 t2;
	private final T3 t3;

	Tuple3Impl(T1 t1, T2 t2, T3 t3) {
		this.t1 = requireNonNull(t1, "t1 is null");
		this.t2 = requireNonNull(t2, "t2 is null");
		this.t3 = requireNonNull(t3, "t3 is null");
	}

	/** {@inheritDoc} */
	@Override
	public T1 getT1() {
		return this.t1;
	}

	/** {@inheritDoc} */
	@Override
	public T2 getT2() {
		return this.t2;
	}
	
	/** {@inheritDoc} */
	@Override
	public T3 getT3() {
		return this.t3;
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return ((31 * t1.hashCode() + t2.hashCode()) *31)+t3.hashCode();
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Tuple3)) {
			return false;
		}

		Tuple3<?, ?, ?> other = (Tuple3<?, ?, ?>) obj;

		return t1.equals(other.getT1()) && t2.equals(other.getT2()) && t3.equals(other.getT3());
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return new ToStringBuilder(false)
				.append("t1", t1)
				.append("t2", t2)
				.append("t3", t3)
				.toString();
	}

}