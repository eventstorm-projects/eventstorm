package eu.eventstorm.sql.tracer;

import io.micrometer.tracing.SpanCustomizer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.Arrays;

class MicrometerStatement implements Statement {

	public static final String TAG_SQL = "sql";
	public static final String TAG_AUTO_GENERATE_KEYS = "autoGenerateKeys";
	public static final String TAG_COLUMN_INDEXES = "columnIndexes";
	public static final String TAG_COLUMN_NAMES = "columnNames";

	public static final String SPAN_EXECUTE_QUERY = "executeQuery";
	public static final String SPAN_EXECUTE = "execute";

	private final Statement ps;

	private final MicrometerTracer tracer;
	private final SpanCustomizer spanCustomizer;

	MicrometerStatement(Statement ps, MicrometerTracer tracer) {
		this.ps = ps;
		this.tracer = tracer;
		this.spanCustomizer = tracer.getTracer().currentSpanCustomizer();;
	}

	protected void event(String value) {
		this.spanCustomizer.event(value);
	}
	
	protected TransactionSpan span(String name) {
		return this.tracer.span(name);
	}

	@Override
	public final ResultSet executeQuery(String sql) throws SQLException {
		try (TransactionSpan span = tracer.span(SPAN_EXECUTE_QUERY)) {
			span.tag(TAG_SQL, sql);
			try {
				return this.ps.executeQuery(sql);
			} catch (SQLException cause) {
				span.exception(cause);
				throw cause;
			}
		}
	}

	@Override
	public final int executeUpdate(String sql) throws SQLException {
		try (TransactionSpan span = tracer.span(SPAN_EXECUTE_QUERY)) {
			span.tag(TAG_SQL, sql);
			try {
				return this.ps.executeUpdate(sql);
			} catch (SQLException cause) {
				span.exception(cause);
				throw cause;
			}
		}
	}

	@Override
	public final void close() throws SQLException {
		this.ps.close();
	}

	@Override
	public final int getMaxFieldSize() throws SQLException {
		return this.ps.getMaxFieldSize();
	}

	@Override
	public final void setMaxFieldSize(int max) throws SQLException {
		this.ps.setMaxFieldSize(max);
	}

	@Override
	public final int getMaxRows() throws SQLException {
		return this.ps.getMaxRows();
	}

	@Override
	public final void setMaxRows(int max) throws SQLException {
		this.ps.setMaxRows(max);
	}

	@Override
	public final void setEscapeProcessing(boolean enable) throws SQLException {
		this.ps.setEscapeProcessing(enable);
	}

	@Override
	public final int getQueryTimeout() throws SQLException {
		return this.ps.getQueryTimeout();
	}

	@Override
	public final void setQueryTimeout(int seconds) throws SQLException {
		this.ps.setQueryTimeout(seconds);
	}

	@Override
	public final void cancel() throws SQLException {
		try (TransactionSpan span = tracer.span("cancel")) {
			try {
				this.ps.cancel();
			} catch (SQLException cause) {
				span.exception(cause);
				throw cause;
			}
		}
	}

	@Override
	public final SQLWarning getWarnings() throws SQLException {
		return this.ps.getWarnings();
	}

	@Override
	public final void clearWarnings() throws SQLException {
		this.ps.clearWarnings();
	}

	@Override
	public final void setCursorName(String name) throws SQLException {
		this.ps.setCursorName(name);
	}

	@Override
	public final boolean execute(String sql) throws SQLException {
		return this.ps.execute(sql);
	}

	@Override
	public final ResultSet getResultSet() throws SQLException {
		return this.ps.getResultSet();
	}

	@Override
	public final int getUpdateCount() throws SQLException {
		return this.ps.getUpdateCount();
	}

	@Override
	public final boolean getMoreResults() throws SQLException {
		return this.ps.getMoreResults();
	}

	@Override
	public final void setFetchDirection(int direction) throws SQLException {
		this.ps.setFetchDirection(direction);
	}

	@Override
	public final int getFetchDirection() throws SQLException {
		return this.ps.getFetchDirection();
	}

	@Override
	public final void setFetchSize(int rows) throws SQLException {
		this.ps.setFetchSize(rows);
	}

	@Override
	public final int getFetchSize() throws SQLException {
		return this.ps.getFetchSize();
	}

	@Override
	public final int getResultSetConcurrency() throws SQLException {
		return this.ps.getResultSetConcurrency();
	}

	@Override
	public final int getResultSetType() throws SQLException {
		return this.ps.getResultSetType();
	}

	@Override
	public final void addBatch(String sql) throws SQLException {
		this.ps.addBatch(sql);
	}

	@Override
	public final void clearBatch() throws SQLException {
		this.ps.clearBatch();
	}

	@Override
	public final int[] executeBatch() throws SQLException {
		return this.ps.executeBatch();
	}

	@Override
	public final Connection getConnection() throws SQLException {
		return this.ps.getConnection();
	}

	@Override
	public final boolean getMoreResults(int current) throws SQLException {
		return this.ps.getMoreResults(current);
	}

	@Override
	public final ResultSet getGeneratedKeys() throws SQLException {
		return this.ps.getGeneratedKeys();
	}

	@Override
	public final int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
		return this.ps.executeUpdate(sql, autoGeneratedKeys);
	}

	@Override
	public final int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
		return this.ps.executeUpdate(sql, columnIndexes);
	}

	@Override
	public final int executeUpdate(String sql, String[] columnNames) throws SQLException {
		return this.ps.executeUpdate(sql, columnNames);
	}

	@Override
	public final boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
		try (TransactionSpan span = tracer.span(SPAN_EXECUTE)) {
			span.tag(TAG_SQL, sql);
			span.tag(TAG_AUTO_GENERATE_KEYS, String.valueOf(autoGeneratedKeys));
			try {
				return this.ps.execute(sql, autoGeneratedKeys);
			} catch (SQLException cause) {
				span.exception(cause);
				throw cause;
			}
		}

	}

	@Override
	public final boolean execute(String sql, int[] columnIndexes) throws SQLException {
		try (TransactionSpan span = tracer.span(SPAN_EXECUTE)) {
			span.tag(TAG_SQL, sql);
			span.tag(TAG_COLUMN_INDEXES, Arrays.toString(columnIndexes));
			try {
				return this.ps.execute(sql, columnIndexes);
			} catch (SQLException cause) {
				span.exception(cause);
				throw cause;
			}
		}
	}

	@Override
	public final boolean execute(String sql, String[] columnNames) throws SQLException {
		try (TransactionSpan span = tracer.span(SPAN_EXECUTE)) {
			span.tag(TAG_SQL, sql);
			span.tag(TAG_COLUMN_NAMES, Arrays.toString(columnNames));
			try {
				return this.ps.execute(sql, columnNames);
			} catch (SQLException cause) {
				span.exception(cause);
				throw cause;
			}
		}
	}

	@Override
	public final int getResultSetHoldability() throws SQLException {
		return this.ps.getResultSetHoldability();
	}

	@Override
	public final boolean isClosed() throws SQLException {
		return this.ps.isClosed();
	}

	@Override
	public final void setPoolable(boolean poolable) throws SQLException {
		this.ps.setPoolable(poolable);
	}

	@Override
	public final boolean isPoolable() throws SQLException {
		return this.ps.isPoolable();
	}

	@Override
	public final void closeOnCompletion() throws SQLException {
		this.ps.closeOnCompletion();
	}

	@Override
	public final boolean isCloseOnCompletion() throws SQLException {
		return this.ps.isCloseOnCompletion();
	}

	@Override
	public final <T> T unwrap(Class<T> iface) throws SQLException {
		return this.ps.unwrap(iface);
	}

	@Override
	public final boolean isWrapperFor(Class<?> iface) throws SQLException {
		return this.ps.isWrapperFor(iface);
	}

}
