package eu.eventstorm.sql.page;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static eu.eventstorm.sql.expression.Expressions.eq;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.URI;
import java.nio.channels.FileChannel;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.Transaction;
import eu.eventstorm.sql.TransactionManager;
import eu.eventstorm.sql.builder.Order;
import eu.eventstorm.sql.csv.CsvColumnConverters;
import eu.eventstorm.sql.csv.CsvLine;
import eu.eventstorm.sql.csv.CsvReader;
import eu.eventstorm.sql.csv.CsvReaders;
import eu.eventstorm.sql.impl.DatabaseBuilder;
import eu.eventstorm.sql.impl.TransactionException;
import eu.eventstorm.sql.impl.TransactionManagerImpl;
import eu.eventstorm.sql.model.airport.Airport;
import eu.eventstorm.sql.model.airport.AirportDescriptor;
import eu.eventstorm.sql.model.airport.AirportImpl;
import eu.eventstorm.sql.model.airport.AirportRepository;
import eu.eventstorm.sql.util.TransactionTemplate;
import eu.eventstorm.test.LoggerInstancePostProcessor;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@ExtendWith(LoggerInstancePostProcessor.class)
class PageTest {

	private JdbcConnectionPool ds;
	private Database db;
	private AirportRepository repo;
	private TransactionTemplate transactionTemplate;
	
	@BeforeEach
	void before() {
		ds = JdbcConnectionPool.create("jdbc:h2:mem:test;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1;INIT=RUNSCRIPT FROM 'classpath:sql/airport.sql'", "sa", "");
		TransactionManager transactionManager = new TransactionManagerImpl(ds);
		db = DatabaseBuilder.from(Dialect.Name.H2)
				.withTransactionManager(transactionManager)
				.withModule(new eu.eventstorm.sql.model.airport.Module("test", null))
				.build();  
		repo = new AirportRepository(db);
		transactionTemplate = new TransactionTemplate(transactionManager);
	}
	
	@Test
	void test01() throws Exception {
		URI uri = URI.create("jar:" + PageTest.class.getResource("/data/airport-codes.zip").toURI().toASCIIString());
		  try (FileSystem zipfs = FileSystems.newFileSystem(uri, new HashMap<>())) {
			  Path path = zipfs.getPath("data", "airport-codes_csv.csv");
			 
			  try (FileChannel fc = FileChannel.open(path, StandardOpenOption.READ)) {
				  Path temp = Paths.get(System.getProperty("java.io.tmpdir"), "airport.csv");
				  Files.copy(path, temp, StandardCopyOption.REPLACE_EXISTING);
				  try (FileChannel tmp = FileChannel.open(temp, StandardOpenOption.READ)) {
					  try (Transaction tx = db.transactionManager().newTransactionReadWrite()) {
						  try (CsvReader reader = CsvReaders.newReader(tmp)) {
							  // header
							  reader.line();
							  CsvLine line;
							  while ((line = reader.line()) != null) {
								  Airport airport = new AirportImpl();
								  airport.setId(line.get(0, CsvColumnConverters.RAW_STRING));
								  airport.setType(line.get(1, CsvColumnConverters.RAW_STRING));
								  airport.setName(line.get(2, CsvColumnConverters.RAW_STRING));
								  airport.setElevation(line.get(3, CsvColumnConverters.RAW_INTEGER));
								  airport.setContinent(line.get(4, CsvColumnConverters.RAW_STRING));
								  airport.setCountry(line.get(5, CsvColumnConverters.RAW_STRING));
								  airport.setRegion(line.get(6, CsvColumnConverters.RAW_STRING));
								  this.repo.insert(airport);
							  }
						  }
						  tx.commit();
					  }
				  }
			  }
		  }
		  
		  try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
			  assertEquals(56495, this.repo.count());
			  tx.rollback();
		  }
		  
		  
		  try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
			  
			  PageRequest pageable = PageRequest.of(0, 10).build();
			  
			  Page<Airport> page = this.repo.findAll(pageable);
			  List<Airport> content = page.getContent().collect(ImmutableList.toImmutableList());
			  
			  assertEquals(56495, page.getTotalElements());
			  assertEquals(0, page.getRange().getStart());
			  assertEquals(9, page.getRange().getEnd());
			  
			  assertEquals("00A", content.get(0).getId());
			  assertEquals("small_airport", content.get(1).getType());
			  assertEquals("00CN", content.get(9).getId());
			  			  
			  page = this.repo.findAll(pageable.next());
			  content = page.getContent().collect(ImmutableList.toImmutableList());
			  assertEquals("00CO", content.get(0).getId());
			  assertEquals("00ID", content.get(7).getId());
			  assertEquals("00IG", content.get(8).getId());
			  assertEquals("00II", content.get(9).getId());
			  
			  System.out.println(page);

			  tx.rollback();
		  }
		  
		  try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
			  
			  PageRequest pageable = PageRequest.of(0, 10).withFilter(eq(AirportDescriptor.TYPE), (ps,index) -> {ps.setString(index,"small_airport"); return index+1;}).build();
			  Page<Airport> page = this.repo.findAll(pageable);
			  assertEquals(34475, page.getTotalElements());
			  
			  List<Airport> content = page.getContent().collect(ImmutableList.toImmutableList());
			  
			  assertEquals("00AA", content.get(0).getId());
			  assertEquals("00AK", content.get(1).getId());
			  assertEquals("00AL", content.get(2).getId());
			  assertEquals("00AS", content.get(3).getId());
			  assertEquals("00AZ", content.get(4).getId());
			  assertEquals("00CA", content.get(5).getId());
			  assertEquals("00CL", content.get(6).getId());
			  assertEquals("00FA", content.get(7).getId());
			  assertEquals("00FL", content.get(8).getId());
			  assertEquals("00GA", content.get(9).getId());
			  for (int i = 0 ; i < 10 ; i++) {
				  assertEquals("small_airport", content.get(i).getType());
			  }
			  tx.rollback();
		  }
		  
		  Page<Airport> page = transactionTemplate.page(() -> this.repo.findAll( PageRequest.of(0, 10).withFilter(eq(AirportDescriptor.TYPE, "small_airport")).build()));
		  assertEquals(34475, page.getTotalElements());
		  List<Airport> content;
		  try (Stream<Airport> stream = page.getContent()) {
			  content = stream.collect(toImmutableList());  
		  }
		  assertEquals("00AA", content.get(0).getId());
		  assertEquals("00AK", content.get(1).getId());
		  assertEquals("00AL", content.get(2).getId());
		  assertEquals("00AS", content.get(3).getId());
		  assertEquals("00AZ", content.get(4).getId());
		  assertEquals("00CA", content.get(5).getId());
		  assertEquals("00CL", content.get(6).getId());
		  assertEquals("00FA", content.get(7).getId());
		  assertEquals("00FL", content.get(8).getId());
		  assertEquals("00GA", content.get(9).getId());
		  for (int i = 0 ; i < 10 ; i++) {
			  assertEquals("small_airport", content.get(i).getType());
		  }
		  
		  try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
			  page = transactionTemplate.page(() -> this.repo.findAll( PageRequest.of(0, 10).withFilter(eq(AirportDescriptor.TYPE, "small_airport")).build()));
			  assertEquals(34475, page.getTotalElements());
			  try (Stream<Airport> stream = page.getContent()) {
				  content = stream.collect(toImmutableList());  
		  }
			  
			  assertEquals("00AA", content.get(0).getId());
			  assertEquals("00AK", content.get(1).getId());
			  assertEquals("00AL", content.get(2).getId());
			  assertEquals("00AS", content.get(3).getId());
			  assertEquals("00AZ", content.get(4).getId());
			  assertEquals("00CA", content.get(5).getId());
			  assertEquals("00CL", content.get(6).getId());
			  assertEquals("00FA", content.get(7).getId());
			  assertEquals("00FL", content.get(8).getId());
			  assertEquals("00GA", content.get(9).getId());
			  for (int i = 0 ; i < 10 ; i++) {
				  assertEquals("small_airport", content.get(i).getType());
			  }
			  tx.rollback();
		  }
		  
		  try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
			  page = transactionTemplate.page(() -> this.repo.findAll( PageRequest.of(0, 10)
					  .withFilter(eq(AirportDescriptor.TYPE, "small_airport"))
					  .withOrder(Order.desc(AirportDescriptor.ID))
					  .build()));
			  
			  assertEquals(34475, page.getTotalElements());
			  try (Stream<Airport> stream = page.getContent()) {
				  content = stream.collect(toImmutableList());  
			  }
			  
			  assertEquals("spgl", content.get(0).getId());
			  assertEquals("mdwo", content.get(1).getId());
			  assertEquals("mdpm", content.get(2).getId());
			  assertEquals("mdma", content.get(3).getId());
			  assertEquals("mdlm", content.get(4).getId());
			  assertEquals("mdll", content.get(5).getId());
			  assertEquals("mdji", content.get(6).getId());
			  assertEquals("mdhn", content.get(7).getId());
			  assertEquals("mdes", content.get(8).getId());
			  assertEquals("mder", content.get(9).getId());
			  for (int i = 0 ; i < 10 ; i++) {
				  assertEquals("small_airport", content.get(i).getType());
			  }
			  tx.rollback();
		  }

		  
		  try (Transaction tx = db.transactionManager().newTransactionReadWrite()) {
			  assertThrows(TransactionException.class, () -> transactionTemplate.page(() -> this.repo.findAll(PageRequest.of(0, 10).withFilter(eq(AirportDescriptor.TYPE, "small_airport")).build())));
			  tx.rollback();
		  }
	}

}
