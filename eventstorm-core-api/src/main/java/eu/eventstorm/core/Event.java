//package eu.eventstorm.core;
//
//import java.time.OffsetDateTime;
//
///**
// * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
// */
//public interface Event<T extends EventPayload> {
//
//	StreamId getStreamId();
//	
//	String getStream();
//
//	T getPayload();
//	
//	OffsetDateTime getTimestamp();
//
//	int getRevision();
//	
//}