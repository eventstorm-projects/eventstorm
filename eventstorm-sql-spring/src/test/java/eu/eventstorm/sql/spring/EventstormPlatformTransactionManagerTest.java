package eu.eventstorm.sql.spring;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.spring.ex001.AbstractStudentRepository;
import eu.eventstorm.sql.spring.ex001.Student;
import eu.eventstorm.sql.spring.ex001.StudentImpl;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = EventstormPlatformTransactionManagerConfigurationTest.class)
class EventstormPlatformTransactionManagerTest {

	@Autowired
	private PlatformTransactionManager transactionManager;
	
	@Autowired
	private Database db;

	private AbstractStudentRepository repository;

	@BeforeEach
	void beforeEach() {
		repository = new AbstractStudentRepository(db) {
		};
	}

	@Test
	void testNormalFlow() {

		TransactionTemplate template = new TransactionTemplate(transactionManager);
		template.execute((status) -> {
			Student student = new StudentImpl();
			student.setId(1);
			student.setAge(37);
			student.setCode("Code1");
			repository.insert(student);
			return null;
		});

		template.setPropagationBehavior(TransactionDefinition.PROPAGATION_SUPPORTS);
		template.setReadOnly(true);
		Student student = template.execute((status) -> repository.findById(1));
		
		assertEquals(37, student.getAge());
		assertEquals("Code1", student.getCode());
	}
	
	@Test
	void testRequiredNew() {
		
		TransactionTemplate template = new TransactionTemplate(transactionManager);
		try {
			template.execute((status) -> {
				Student student = new StudentImpl();
				student.setId(1);
				student.setAge(37);
				student.setCode("Code1");
				repository.insert(student);
				
				TransactionTemplate templateRequiredNew = new TransactionTemplate(transactionManager);
				templateRequiredNew.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
				templateRequiredNew.execute((status2) -> {
					Student student2 = new StudentImpl();
					student2.setId(2);
					student2.setAge(39);
					student2.setCode("Code2");
					repository.insert(student2);
					return null;
				});
				
				throw new RuntimeException();
			});	
		} catch (RuntimeException cause) {
			template.setPropagationBehavior(TransactionDefinition.PROPAGATION_SUPPORTS);
			template.setReadOnly(true);
			assertNull(template.execute((status) -> repository.findById(1)));
			assertNotNull(template.execute((status) -> repository.findById(2)));
			return;
		}
		fail();
		
	}
}
