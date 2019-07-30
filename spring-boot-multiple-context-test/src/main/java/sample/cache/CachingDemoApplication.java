package sample.cache;

import org.cache2k.extra.spring.SpringCache2kCacheManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

@EnableCaching
@SpringBootApplication
public class CachingDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(CachingDemoApplication.class, args);
	}

	/**
	 *
	 */
	@Bean
	public CacheManager springCacheManager() {
		SpringCache2kCacheManager cacheManager = new SpringCache2kCacheManager("spring-" + hashCode());
		// SpringCache2kCacheManager cacheManager = new CloseableCacheManager("spring-" + hashCode());
		cacheManager.addCaches(b -> b.name("cache"));
		return cacheManager;
	}

	/**
	 * Alternative for {@link SpringCache2kCacheManager} that closes the cache manager on
	 * application shutdown.
	 *
	 * <p/>Since every application context / configuration object has a unique cache manager its
	 * we can close the cache manager on application shutdown. So this way the cache manager lifecycle
	 * is identical to the application lifecycle.
	 *
	 * <p/>It seems that the Spring test runner is shutting down all applications at the end of the complete
	 * run and not after the tests finished for one application. That may cause troubles when caches hog
	 * memory.
	 */
	static class CloseableCacheManager extends SpringCache2kCacheManager implements AutoCloseable {

		public CloseableCacheManager(final String name) {
			super(name);
		}

		@Override
		public void close() {
			getNativeCacheManager().close();
		}
	}

}
