package eu.eventstorm.sql.model.airport;

import static eu.eventstorm.sql.jdbc.PreparedStatementSetters.noParameter;
import static eu.eventstorm.sql.jdbc.ResultSetMappers.LONG;
import static eu.eventstorm.sql.model.airport.AirportDescriptor.COLUMNS;
import static eu.eventstorm.sql.model.airport.AirportDescriptor.ALL;
import static eu.eventstorm.sql.model.airport.AirportDescriptor.IDS;
import static eu.eventstorm.sql.model.airport.AirportDescriptor.ID;
import static eu.eventstorm.sql.model.airport.AirportDescriptor.TABLE;

import eu.eventstorm.sql.SqlQuery;
import eu.eventstorm.sql.SqlQueryPageable;
import eu.eventstorm.sql.expression.AggregateFunctions;
import eu.eventstorm.sql.page.Page;
import eu.eventstorm.sql.page.PageRequest;

public class AirportRepository extends eu.eventstorm.sql.Repository {

	public static final eu.eventstorm.sql.jdbc.Mapper<Airport> MAPPER = new AirportMapper();
	
	private final SqlQuery insert;
	private final SqlQuery count;
	
	private final SqlQueryPageable findAll;

	public AirportRepository(eu.eventstorm.sql.Database database) {
		super(database);
		this.insert = insert(TABLE, IDS, COLUMNS).build();
		this.count = select(AggregateFunctions.count(ID)).from(TABLE).build();
		
		this.findAll = select(ALL).from(TABLE).pageable().build();
//		this.countAll = select(AggregateFunctions.count(ID)).from(TABLE).build();
//		
//		this.findAllByType = select(ALL).from(TABLE).where(eq(TYPE)).pageable().orderBy(Order.asc(ID)).build();
//		this.countByType = select(AggregateFunctions.count(ID)).from(TABLE).where(eq(TYPE)).build();
	}

	public void insert(Airport pojo) {
		// execute insert
		executeInsert(this.insert, MAPPER, pojo);
	}
	
	public long count() {
		return executeSelect(count, noParameter(), LONG);
	}
	
	public Page<Airport> findAll(PageRequest pageable) {
		return executeSelectPage(findAll, MAPPER, pageable);
	}
	
//	public Page<Airport> findAllByType(String type, Pageable pageable) {
//		return executeSelectPage(countByType, findAllByType, ps -> ps.setString(1, type), MAPPER, pageable);
//	}

}