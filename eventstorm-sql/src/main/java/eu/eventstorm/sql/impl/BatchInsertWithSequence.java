package eu.eventstorm.sql.impl;

import java.util.function.BiConsumer;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.SqlQuery;
import eu.eventstorm.sql.id.Identifier;
import eu.eventstorm.sql.jdbc.InsertMapper;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class BatchInsertWithSequence<E,T> extends BatchInsert<E> {

    private final Identifier<T> identifier;
    private final BiConsumer<E, T> identifierSetter;

	public BatchInsertWithSequence(Database database, SqlQuery query, InsertMapper<E> im, Identifier<T> identifier, BiConsumer<E, T> identifierSetter) {
		super(database, query, im);
		this.identifier = identifier;
	    this.identifierSetter = identifierSetter;
	}

	@Override
	public void add(E pojo) {
		this.identifierSetter.accept(pojo, this.identifier.next());
		super.add(pojo);
	}

}
