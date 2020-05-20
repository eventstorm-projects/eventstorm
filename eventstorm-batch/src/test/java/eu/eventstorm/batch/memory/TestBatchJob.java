package eu.eventstorm.batch.memory;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eu.eventstorm.batch.BatchJob;
import eu.eventstorm.batch.BatchJobContext;

@Component("Test")
public class TestBatchJob implements BatchJob {

	private static final Logger LOGGER = LoggerFactory.getLogger(TestBatchJob.class);
	
	public AtomicInteger counter = new AtomicInteger();
	
	@Override
	public void execute(BatchJobContext context) {
		
		LOGGER.info("execute() -> increment");
		
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		counter.incrementAndGet();
		
		
	}

}
