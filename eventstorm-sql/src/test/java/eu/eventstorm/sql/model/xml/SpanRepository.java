package eu.eventstorm.sql.model.xml;

import static com.google.common.collect.ImmutableList.of;
import static eu.eventstorm.sql.expression.Expressions.eq;
import static eu.eventstorm.sql.model.xml.SpanDescriptor.ALL;
import static eu.eventstorm.sql.model.xml.SpanDescriptor.COLUMNS;
import static eu.eventstorm.sql.model.xml.SpanDescriptor.ID;
import static eu.eventstorm.sql.model.xml.SpanDescriptor.TABLE;

import java.sql.ResultSet;
import java.sql.SQLException;

import eu.eventstorm.sql.jdbc.Batch;


public class SpanRepository extends eu.eventstorm.sql.Repository {

	private static final SpanMapper SPAN = new SpanMapper();

    private final String findById;
    private final String findByIdForUpdate;
    private final String insert;

    protected SpanRepository(eu.eventstorm.sql.Database database){
        super(database);
        this.findById = select(ALL).from(TABLE).where(eq(ID)).build();
        this.findByIdForUpdate = select(ALL).from(TABLE).where(eq(ID)).forUpdate().build();
        this.insert = insert(TABLE, of(), COLUMNS).build();
    }

    public final Span findById(int id) {
        return executeSelect(this.findById, ps -> {
           ps.setInt(1, id);
        }, SPAN);
    }

    public final Span findByIdForUpdate(int id) {
        return executeSelect(this.findByIdForUpdate, ps -> ps.setInt(1, id), SPAN);
    }

    public final void insert(Span pojo) {
        // execute insert
        executeInsertAutoIncrement(this.insert, SPAN, pojo);
    }


    public final Batch<Span> batch() {
        // add to batch
        return super.batch(this.insert, SPAN);
    }

    static final class SpanMapper implements eu.eventstorm.sql.jdbc.MapperWithAutoIncrement<Span> {

        SpanMapper() {
        }

        public Span map(eu.eventstorm.sql.Dialect dialect, java.sql.ResultSet rs) throws java.sql.SQLException {
            Span pojo = new Span();
            pojo.setId(rs.getInt(1));
            pojo.setContent(dialect.fromJdbcXml(rs,2));
            return pojo;
        }

        public void insert(java.sql.PreparedStatement ps, Span pojo) throws java.sql.SQLException {
            ps.setObject(1,  pojo.getContent());
        }

        public void update(java.sql.PreparedStatement ps, Span pojo) throws java.sql.SQLException {
            ps.setObject(1,  pojo.getContent());
            //set primary key
            ps.setInt(2,  pojo.getId());
        }

		@Override
		public void setId(Span pojo, ResultSet rs) throws SQLException {
			pojo.setId(rs.getInt(1));
		}

    }
}