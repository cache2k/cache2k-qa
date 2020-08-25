package sample.cache;

import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCache;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.Duration;
import javax.cache.expiry.EternalExpiryPolicy;
import javax.cache.expiry.ModifiedExpiryPolicy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * @author Jens Wilke
 */
@Configuration
@EnableCaching
public class CacheConfiguration {

  /**
   * Standard Spring
   */
  /*-
  @Bean
  public JCacheCacheManager cacheManager() {
    return new JCacheCacheManager() {
      @Override
      protected Collection<Cache> loadCaches() {
        Collection<Cache> caches = new ArrayList<>();
        caches.add(new JCacheCache(
          getCacheManager().createCache(CountryRepository.CACHE_NAME,
          new MutableConfiguration()
            .setExpiryPolicyFactory(ModifiedExpiryPolicy.factoryOf(new Duration(TimeUnit.MINUTES, 30)))),
          false));
        return caches;
      }
    };
  }
  -*/

  /**
   * Spring Boot
   */
  @Bean
  public JCacheManagerCustomizer cacheConfigurationCustomizer() {
    return cm -> {
      cm.createCache(CountryRepository.CACHE_NAME, cacheConfiguration());
    };
  }

  private javax.cache.configuration.Configuration<Object, Object> cacheConfiguration() {
    // Create a cache using infinite heap. A real application will want to use a more fine-grained configuration,
    // possibly using an implementation-dependent API
    return new MutableConfiguration<>().setStatisticsEnabled(true);
  }

}
