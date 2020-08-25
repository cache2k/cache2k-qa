package sample.cache;


import org.cache2k.extra.spring.SpringCache2kCacheManager;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.boot.actuate.metrics.cache.CaffeineCacheMeterBinderProvider;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Defines the cache manager explicitly. There is no auto configure support for cache2k in
 * spring boot yet.
 *
 * @author Jens Wilke
 */
@Configuration
@EnableCaching
public class CachingConfig extends CachingConfigurerSupport  {

  @Bean
  public CacheManager dummyCacheManager() {
    // Thread.dumpStack();
    return new DummyCacheManager();
  }

  @Bean
  public Cache2kCacheMeterBinderProvider cache2kCacheMeterBinderProvider() {
    return new Cache2kCacheMeterBinderProvider();
  }

  @Bean
  public CacheManager cacheManager() {
    SpringCache2kCacheManager mgm = new SpringCache2kCacheManager();
    mgm.addCaches(
      b->b.name("loadingBySetupCountries").keyType(String.class).valueType(Country.class).loader(k -> new Country(k)),
      b->b.name("test1").expireAfterWrite(30, TimeUnit.SECONDS).entryCapacity(10000),
      b->b.name("anotherCache").entryCapacity(1000)
    );
    mgm.setAllowUnknownCache(true);
    return mgm;
  }

  @Override
  public CacheErrorHandler errorHandler() {
    return new CacheErrorHandler() {
      @Override
      public void handleCacheGetError(final RuntimeException exception, final Cache cache, final Object key) {
        throw new SpecialWrapException(exception);
      }

      @Override
      public void handleCachePutError(final RuntimeException exception, final Cache cache, final Object key, final Object value) {
        throw new SpecialWrapException(exception);
      }

      @Override
      public void handleCacheEvictError(final RuntimeException exception, final Cache cache, final Object key) {
        throw new SpecialWrapException(exception);
      }

      @Override
      public void handleCacheClearError(final RuntimeException exception, final Cache cache) {
        throw new SpecialWrapException(exception);
      }
    };
  }

  static class SpecialWrapException extends RuntimeException {

		public SpecialWrapException(final Throwable cause) {
			super(cause);
		}

	}
}
