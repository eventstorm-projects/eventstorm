package eu.eventstorm.util.tuple;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface Tuple3<T1, T2, T3> extends Tuple2<T1,T2> {

	/**
	 * To get the third object of this {@link Tuples}.
	 *
	 * @return The third object
	 */
	T3 getT3();
	
}