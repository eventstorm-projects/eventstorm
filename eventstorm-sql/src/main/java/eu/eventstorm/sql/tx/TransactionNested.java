package eu.eventstorm.sql.tx;

import java.sql.PreparedStatement;
import java.util.UUID;

final class TransactionNested implements Transaction, TransactionContext {

    private final AbstractTransaction parent;

    TransactionNested(AbstractTransaction parent) {
        this.parent = parent;
    }

    @Override
    public boolean isReadOnly() {
        return parent.isReadOnly();
    }

    @Override
    public void commit() {
        // skip
    }

    @Override
    public void rollback() {
        // skip
    }

    @Override
    public void close() {
        // skip
    }

    @Override
    public PreparedStatement read(String sql) {
        return this.parent.read(sql);
    }

    @Override
    public PreparedStatement write(String sql) {
        return this.parent.write(sql);
    }

    @Override
    public void addHook(Runnable runnable) {
        parent.addHook(runnable);
    }

	@Override
	public UUID getUuid() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PreparedStatement writeAutoIncrement(String sql) {
		// TODO Auto-generated method stub
		return null;
	}

}