package eu.eventstorm.sql;

import static com.google.common.collect.ImmutableMap.of;
import static eu.eventstorm.sql.EventstormRepositoryException.Type.BATCH_ADD;
import static eu.eventstorm.sql.EventstormRepositoryException.Type.BATCH_EXECUTE_QUERY;
import static eu.eventstorm.sql.EventstormRepositoryException.Type.BATCH_RESULT;
import static eu.eventstorm.sql.EventstormRepositoryException.Type.DELETE_EXECUTE_QUERY;
import static eu.eventstorm.sql.EventstormRepositoryException.Type.DELETE_PREPARED_STATEMENT_SETTER;
import static eu.eventstorm.sql.EventstormRepositoryException.Type.INSERT_EXECUTE_QUERY;
import static eu.eventstorm.sql.EventstormRepositoryException.Type.INSERT_GENERATED_KEYS;
import static eu.eventstorm.sql.EventstormRepositoryException.Type.INSERT_MAPPER;
import static eu.eventstorm.sql.EventstormRepositoryException.Type.INSERT_RESULT;
import static eu.eventstorm.sql.EventstormRepositoryException.Type.SELECT_EXECUTE_QUERY;
import static eu.eventstorm.sql.EventstormRepositoryException.Type.SELECT_MAPPER;
import static eu.eventstorm.sql.EventstormRepositoryException.Type.SELECT_NEXT;
import static eu.eventstorm.sql.EventstormRepositoryException.Type.SELECT_PREPARED_STATEMENT_SETTER;
import static eu.eventstorm.sql.EventstormRepositoryException.Type.STREAM_EXECUTE_QUERY;
import static eu.eventstorm.sql.EventstormRepositoryException.Type.STREAM_NEXT;
import static eu.eventstorm.sql.EventstormRepositoryException.Type.STREAM_MAPPER;
import static eu.eventstorm.sql.EventstormRepositoryException.Type.STREAM_PREPARED_STATEMENT_SETTER;
import static eu.eventstorm.sql.EventstormRepositoryException.Type.UPDATE_EXECUTE_QUERY;
import static eu.eventstorm.sql.EventstormRepositoryException.Type.UPDATE_MAPPER;
import static eu.eventstorm.sql.EventstormRepositoryException.Type.UPDATE_RESULT;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.sql.builder.DeleteBuilder;
import eu.eventstorm.sql.builder.InsertBuilder;
import eu.eventstorm.sql.builder.SelectBuilder;
import eu.eventstorm.sql.builder.UpdateBuilder;
import eu.eventstorm.sql.desc.SqlColumn;
import eu.eventstorm.sql.desc.SqlPrimaryKey;
import eu.eventstorm.sql.desc.SqlSingleColumn;
import eu.eventstorm.sql.desc.SqlTable;
import eu.eventstorm.sql.domain.Page;
import eu.eventstorm.sql.domain.PageImpl;
import eu.eventstorm.sql.domain.Pageable;
import eu.eventstorm.sql.expression.AggregateFunction;
import eu.eventstorm.sql.jdbc.InsertMapper;
import eu.eventstorm.sql.jdbc.InsertMapperWithAutoIncrement;
import eu.eventstorm.sql.jdbc.PreparedStatementSetter;
import eu.eventstorm.sql.jdbc.ResultSetMapper;
import eu.eventstorm.sql.jdbc.ResultSetMappers;
import eu.eventstorm.sql.jdbc.UpdateMapper;
import eu.eventstorm.sql.tx.TransactionQueryContext;
import eu.eventstorm.sql.tx.TransactionContext;

/**
 * Repository pattern, subclasses will be generated by apt.
 *
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public abstract class Repository {

	private static final Logger LOGGER = LoggerFactory.getLogger(Repository.class);

	private final Database database;

	public Repository(Database database) {
		this.database = database;
	}

	protected Database database() {
		return this.database;
	}

	protected final SelectBuilder select(ImmutableList<SqlColumn> columns) {
		return new SelectBuilder(this.database, columns);
	}

	protected final SelectBuilder select(SqlColumn... columns) {
		return new SelectBuilder(this.database, ImmutableList.copyOf(columns));
	}

	protected final SelectBuilder select(AggregateFunction aggregateFunction) {
		return new SelectBuilder(this.database, aggregateFunction);
	}

	protected final InsertBuilder insert(SqlTable table, ImmutableList<SqlPrimaryKey> keys, ImmutableList<SqlSingleColumn> columns) {
		return new InsertBuilder(this.database, table, keys, columns);
	}

	protected final UpdateBuilder update(SqlTable table, ImmutableList<SqlSingleColumn> columns, ImmutableList<SqlPrimaryKey> keys) {
		return new UpdateBuilder(this.database, table, columns, keys);
	}

	protected final DeleteBuilder delete(SqlTable table) {
		return new DeleteBuilder(this.database, table);
	}

	protected final <T> T executeSelect(String sql, PreparedStatementSetter pss, ResultSetMapper<T> mapper) {
		try (TransactionQueryContext tqc = database.transactionManager().context().read(sql)) {
			try {
				pss.set(tqc.preparedStatement());
			} catch (SQLException cause) {
				throw new EventstormRepositoryException(SELECT_PREPARED_STATEMENT_SETTER, of("sql", sql), cause);
			}
			try (ResultSet rs = tqc.preparedStatement().executeQuery()) {
				return map(rs, mapper, this.database.dialect());
			} catch (SQLException cause) {
				throw new EventstormRepositoryException(SELECT_EXECUTE_QUERY, of("sql", sql), cause);
			}
		}
	}

	protected final <E> void executeInsert(String sql, InsertMapper<E> im, E pojo) {
		try (TransactionQueryContext tqc = database.transactionManager().context().write(sql)) {
			doInsert(sql, im, pojo, tqc);
		}
	}

	private <E> void doInsert(String sql, InsertMapper<E> im, E pojo, TransactionQueryContext tqc) {
		try {
			im.insert(tqc.preparedStatement(), pojo);
		} catch (SQLException cause) {
			throw tqc.exception(new EventstormRepositoryException(INSERT_MAPPER, of("sql", sql, "pojo", pojo), cause));
		}
		int val;
		try {
			val = tqc.preparedStatement().executeUpdate();
		} catch (SQLException cause) {
			throw new EventstormRepositoryException(INSERT_EXECUTE_QUERY, of("sql", sql, "pojo", pojo), cause);
		}
		if (val != 1) {
			throw new EventstormRepositoryException(INSERT_RESULT, of("sql", sql, "pojo", pojo));
		}
	}


	protected final <E> void executeInsertAutoIncrement(String sql, InsertMapperWithAutoIncrement<E> im, E pojo) {

		try (TransactionQueryContext tqc = database.transactionManager().context().writeAutoIncrement(sql)) {

			doInsert(sql, im, pojo, tqc);

			try (ResultSet rs = tqc.preparedStatement().getGeneratedKeys()) {
				if (rs.next()) {
					im.setId(pojo, rs);
				}
			} catch (SQLException cause) {
				throw tqc.exception(new EventstormRepositoryException(INSERT_GENERATED_KEYS, of(), cause));
			}
		}
	}


	protected final <E> void executeUpdate(String sql, UpdateMapper<E> um, E pojo) {

		try (TransactionQueryContext tqc = database.transactionManager().context().write(sql)) {
			try {
				um.update(tqc.preparedStatement(), pojo);
			} catch (SQLException cause) {
				throw tqc.exception(new EventstormRepositoryException(UPDATE_MAPPER, of("sql", sql, "pojo", pojo), cause));
			}

			int val;

			try {
				val = tqc.preparedStatement().executeUpdate();
			} catch (SQLException cause) {
				throw tqc.exception(new EventstormRepositoryException(UPDATE_EXECUTE_QUERY, of("sql", sql, "pojo", pojo), cause));
			}

			if (val != 1) {
				throw tqc.exception(new EventstormRepositoryException(UPDATE_RESULT, of("sql", sql, "pojo", pojo)));
			}
		}

	}

	protected final int executeDelete(String sql, PreparedStatementSetter pss) {

		try (TransactionQueryContext tqc = database.transactionManager().context().write(sql)) {
			try {
				pss.set(tqc.preparedStatement());
			} catch (SQLException cause) {
				throw tqc.exception(new EventstormRepositoryException(DELETE_PREPARED_STATEMENT_SETTER, of("sql", sql), cause));
			}

			try {
				return tqc.preparedStatement().executeUpdate();
			} catch (SQLException cause) {
				throw tqc.exception(new EventstormRepositoryException(DELETE_EXECUTE_QUERY, of("sql", sql), cause));
			}

		}

	}

	private static <T> T map(ResultSet rs, ResultSetMapper<T> mapper, Dialect dialect) {
		boolean value;

		try {
			value = rs.next();
		} catch (SQLException cause) {
			throw new EventstormRepositoryException(SELECT_NEXT, of(), cause);
		}

		if (value) {
			try {
				return mapper.map(dialect, rs);
			} catch (SQLException cause) {
				throw new EventstormRepositoryException(SELECT_MAPPER, of(), cause);
			}
		} else {
			return null;
		}
	}

	protected final <E> void executeBatchInsert(String sql, InsertMapper<E> im, Iterable<E> pojos) {

		try (TransactionQueryContext tqc = database.transactionManager().context().write(sql)) {
			int count = 0;
	        for (E pojo : pojos) {
				try {
					im.insert(tqc.preparedStatement(), pojo);
				} catch (SQLException cause) {
					throw tqc.exception(new EventstormRepositoryException(INSERT_MAPPER, of("sql", sql, "pojo", pojo), cause));
				}
				try {
					tqc.preparedStatement().addBatch();
				} catch (SQLException cause) {
					throw tqc.exception(new EventstormRepositoryException(BATCH_ADD, of("sql", sql, "pojo", pojo), cause));
	            }
	            count++;
			}

			int[] vals;

			try {
				vals = tqc.preparedStatement().executeBatch();
			} catch (SQLException cause) {
				throw tqc.exception(new EventstormRepositoryException(BATCH_EXECUTE_QUERY, of("sql", sql, "pojos", pojos), cause));
			}

			if (vals.length != count) {
				throw tqc.exception(new EventstormRepositoryException(BATCH_RESULT, of("sql", sql, "pojos", pojos, "size", vals.length)));
			}
			for (int i = 0; i < vals.length; i++) {
				if (vals[i] != 1) {
					throw tqc.exception(new EventstormRepositoryException(BATCH_RESULT, of("sql", sql, "pojos", pojos, "item", i, "return", vals[i])));
				}
			}
		}


	}

	protected final <T> Stream<T> stream(String sql, PreparedStatementSetter pss, ResultSetMapper<T> mapper) {

		TransactionContext tc = database.transactionManager().context();

		TransactionQueryContext tqc = tc.read(sql);

		try {
			pss.set(tqc.preparedStatement());
		} catch (SQLException cause) {
			throw tqc.exception(new EventstormRepositoryException(STREAM_PREPARED_STATEMENT_SETTER, of("sql", sql), cause));
		}

		ResultSet rs;
		try {
			rs = tqc.preparedStatement().executeQuery();
		} catch (SQLException cause) {
			throw tqc.exception(new EventstormRepositoryException(STREAM_EXECUTE_QUERY, of(), cause));
		}

		tc.addHook(() -> {

			try {
				rs.close();
			} catch (SQLException cause) {
				LOGGER.warn("Hook -> failed to closed resultset for stream({})", sql);
			} finally {
				tqc.close();
			}
		});

		return StreamSupport.stream(new Spliterator<T>() {
			@Override
			public boolean tryAdvance(Consumer<? super T> action) {
				try {
					if (!rs.next()) {
						return false;
					}
				} catch (SQLException cause) {
					throw tqc.exception(new EventstormRepositoryException(STREAM_NEXT, of(), cause));
				}
				try {
					action.accept(mapper.map(database.dialect(), rs));
				} catch (SQLException cause) {
					throw tqc.exception(new EventstormRepositoryException(STREAM_MAPPER, of(), cause));
				}
				return true;
			}

			@Override
			public Spliterator<T> trySplit() {
				return null;
			}

			@Override
			public long estimateSize() {
				return 0;
			}

			@Override
			public int characteristics() {
				return 0;
			}
		}, false);

	}




	protected final <T> Page<T> executeSelectPage(String countSql, String sql, ResultSetMapper<T> mapper, Pageable pageable) {

		Long count = executeSelect(countSql, ps -> {

		}, ResultSetMappers.SINGLE_LONG);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("find count=[{}]", count);
		}

		if (count == null || count == 0) {
			return Page.empty();
		}

		return new PageImpl<>(stream(sql, ps -> {

		}, mapper), count, pageable);

	}

}
