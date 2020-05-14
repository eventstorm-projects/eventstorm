package eu.eventstorm.batch.memory;

import static java.util.UUID.randomUUID;

import java.time.OffsetDateTime;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.Any;

import eu.eventstorm.batch.Batch;
import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventCandidate;
import eu.eventstorm.core.UUID;

public final class InMemoryBatch implements Batch {

	@Override
	public ImmutableList<Event> push(ImmutableList<EventCandidate> candidates) {
		
		java.util.UUID correlation = randomUUID();
		ImmutableList.Builder<Event> builder = ImmutableList.builder();
		
		for (EventCandidate candidate : candidates) {
			
			Event event = Event.newBuilder()
					.setStreamId(candidate.getStreamId().toStringValue())
					.setStream(candidate.getStream())
					.setCorrelation(UUID.newBuilder().setLeastSigBits(correlation.getLeastSignificantBits()).setMostSigBits(correlation.getMostSignificantBits()))
					.setRevision(1)
					.setTimestamp(OffsetDateTime.now().toString())
					.setData(Any.pack(candidate.getMessage()))
				.build();	
			
//			BatchExecution batchExecution = new BatchExecutionBuilder()
//					.withName(candidate.getMessage().g)
//					.build();
			
		}
		
		return builder.build();
	}


}