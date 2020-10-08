package sample.cache;

import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.cache2k.extra.micrometer.Cache2kCacheMetrics;
import org.cache2k.extra.spring.SpringCache2kCache;
import org.springframework.boot.actuate.metrics.cache.CacheMeterBinderProvider;

/**
 * Mechanism used within Spring boot to add all caches. However, this adds only the
 * caches known by a cache manager and not the ones created automatically.
 * cache2k bind internally to the global micrometer registry, so this is not needed.
 *
 * {@link CacheMeterBinderProvider} implementation for cache2k.
 *
 * @author Jens Wilke
 */
public class Cache2kCacheMeterBinderProvider implements CacheMeterBinderProvider<SpringCache2kCache> {

	@Override
	public MeterBinder getMeterBinder(SpringCache2kCache cache, Iterable<Tag> tags) {
		// System.err.println("STATS REQUESTED " + toString() + " " + cache.getName());
		// Thread.dumpStack();
		return new Cache2kCacheMetrics(cache.getNativeCache(), tags);
	}

}
