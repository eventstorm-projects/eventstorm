package eu.eventstorm.util;

import javax.annotation.Nonnull;
import java.util.concurrent.ThreadFactory;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class NamedThreadFactory implements ThreadFactory {

    private final String name;
    
    private final ThreadGroup threadGroup;
    
    private int count = 1;
    
    public NamedThreadFactory(String name) {
        this.name = name;
        this.threadGroup = Thread.currentThread().getThreadGroup();
    }

    @Override
    public Thread newThread(@Nonnull Runnable runnable) {
        return new Thread(threadGroup, runnable , name + '-' +count++);
    }

}