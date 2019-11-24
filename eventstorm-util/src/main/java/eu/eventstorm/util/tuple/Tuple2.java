package eu.eventstorm.util.tuple;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface Tuple2<T1, T2> {

	/**
	 * To get the fist object of this {@link Tuples}.
	 *
	 * @return The first object
	 */
	T1 getT1();
	
	/**
	 * To get the second object of this {@link Tuples}.
	 *
	 * @return The second object
	 */
	T2 getT2();
	
}