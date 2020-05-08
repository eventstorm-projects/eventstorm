package eu.eventstorm.sql.util;

@FunctionalInterface
public interface TransactionCallback<T> {

	T doInTransaction();
	
}
