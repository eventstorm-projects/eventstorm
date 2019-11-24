package eu.eventstorm.util.tuple;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class Tuples {

	private Tuples() {
	}

	/**
	 * Create a {@link Tuple2} with the given objects.
	 *
	 * @param t1   The first value in the tuple. Not null.
	 * @param t2   The second value in the tuple. Not null.
	 * 
	 * @param <T1> The type of the first value.
	 * @param <T2> The type of the second value.
	 * 
	 * @return The new {@link Tuple2}.
	 */
	public static <T1, T2> Tuple2<T1, T2> of(T1 t1, T2 t2) {
		return new Tuple2Impl<>(t1, t2);
	}
}
