package sample.cache;

import org.cache2k.Cache;
import org.cache2k.extra.spring.SpringCache2kCacheManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author Jens Wilke
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ Cache.class, SpringCache2kCacheManager.class })
@ConditionalOnMissingBean(CacheManager.class)
public class Cache2kCacheManagerProvider {

  @Bean
  public CacheManager cacheManager() {
    SpringCache2kCacheManager mgm = new SpringCache2kCacheManager("springDefault");
    return mgm;
  }

}
