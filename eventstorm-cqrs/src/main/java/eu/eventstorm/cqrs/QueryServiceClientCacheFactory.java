package eu.eventstorm.cqrs;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.LoadingCache;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface QueryServiceClientCacheFactory<K, V> {

    LoadingCache<K, V> newInstance(CacheLoader<K, V> cacheLoader);

}
