package eu.eventstorm.sql.model.json;

import static eu.eventstorm.sql.expression.Expressions.eq;
import static eu.eventstorm.sql.model.json.SpanDescriptor.ALL;
import static eu.eventstorm.sql.model.json.SpanDescriptor.COLUMNS;
import static eu.eventstorm.sql.model.json.SpanDescriptor.ID;
import static eu.eventstorm.sql.model.json.SpanDescriptor.IDS;
import static eu.eventstorm.sql.model.json.SpanDescriptor.TABLE;

import javax.annotation.Generated;

import eu.eventstorm.sql.jdbc.Batch;


@Generated("eu.eventsotrm.sql.apt.RepositoryGenerator")
public class SpanRepository extends eu.eventstorm.sql.Repository {

	private static final SpanMapper SPAN = new SpanMapper();

    private final String findById;
    private final String findByIdForUpdate;
    private final String insert;
    private final String update;
    private final String delete;

    protected SpanRepository(eu.eventstorm.sql.Database database){
        super(database);
        this.findById = select(ALL).from(TABLE).where(eq(ID)).build();
        this.findByIdForUpdate = select(ALL).from(TABLE).where(eq(ID)).forUpdate().build();
        this.insert = insert(TABLE, IDS, COLUMNS).build();
        this.update = update(TABLE, COLUMNS, IDS).build();
        this.delete = delete(TABLE).where(eq(ID)).build();
    }

    public final eu.eventstorm.sql.model.json.Span findById(int id) {
        return executeSelect(this.findById, ps -> {
           ps.setInt(1, id);
        }, SPAN);
    }

    public final eu.eventstorm.sql.model.json.Span findByIdForUpdate(int id) {
        return executeSelect(this.findByIdForUpdate, ps -> ps.setInt(1, id), SPAN);
    }

    public final void insert(eu.eventstorm.sql.model.json.Span pojo) {
        // execute insert
        executeInsert(this.insert, SPAN, pojo);
    }

    public final void update(eu.eventstorm.sql.model.json.Span pojo) {
        // execute update
        executeUpdate(this.update, SPAN, pojo);
    }

    public final Batch<eu.eventstorm.sql.model.json.Span> batch() {
        // add to batch
        return super.batch(this.insert, SPAN);
    }

    static final class SpanMapper implements eu.eventstorm.sql.jdbc.Mapper<Span> {

        SpanMapper() {
        }

        public Span map(eu.eventstorm.sql.Dialect dialect, java.sql.ResultSet rs) throws java.sql.SQLException {
            Span pojo = new Span();
            pojo.setId(rs.getInt(1));
            pojo.setContent(dialect.fromJdbcJson(rs,2));
            return pojo;
        }

        public void insert(java.sql.PreparedStatement ps, Span pojo) throws java.sql.SQLException {
            ps.setInt(1,  pojo.getId());
            pojo.getContent().flush();
            ps.setObject(2,  pojo.getContent());
        }

        public void update(java.sql.PreparedStatement ps, Span pojo) throws java.sql.SQLException {
            pojo.getContent().flush();
            ps.setObject(1,  pojo.getContent());
            //set primary key
            ps.setInt(2,  pojo.getId());

        }

    }
}