package eu.eventstorm.cqrs.impl;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

import eu.eventstorm.cqrs.Command;
import eu.eventstorm.cqrs.EventLoop;
import reactor.core.scheduler.Scheduler;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class MultipleEventLoop implements EventLoop {

	private static final Logger LOGGER = LoggerFactory.getLogger(MultipleEventLoop.class);
			
	private final ConcurrentHashMap<String, Scheduler> cache = new ConcurrentHashMap<>();

	private final ImmutableMap<String, Scheduler> others;
	
	private final Scheduler defaultScheduler; 
	
	private final Scheduler postScheduler;
	
	public MultipleEventLoop(Scheduler defaultScheduler, ImmutableMap<String, Scheduler> others, Scheduler postScheduler) {
		this.others = others;
		this.defaultScheduler = defaultScheduler;
		this.postScheduler = postScheduler;
	}

	@Override
	public Scheduler get(Command command) {
		Class<?> clazz = command.getClass();
		return getFromCache(clazz.getName(), clazz);
	}

	
	private Scheduler getFromCache(String classname, Class<?> clazz) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("getFromCache({},{})", classname, clazz);
		}
		
		Scheduler def = this.cache.get(clazz.getName());
		
		if (def != null) {
			return def;
		}
		
		def = this.others.get(clazz.getName());
		
		if (def != null) {
			this.cache.putIfAbsent(classname, def);
			return def;
		}
		
		for (Class<?> item : clazz.getInterfaces()) {
			def = getFromCache(classname, item);
			if (def != null) {
				return def;
			}
		}
		
		this.cache.putIfAbsent(classname, defaultScheduler);
		
		return defaultScheduler;
	}

	@Override
	public Scheduler post() {
		return postScheduler;
	}
	
}
