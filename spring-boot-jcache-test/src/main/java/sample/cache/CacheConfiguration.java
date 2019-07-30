package sample.cache;

import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.configuration.MutableConfiguration;

/**
 * @author Jens Wilke
 */
@Configuration
@EnableCaching
public class CacheConfiguration {

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
