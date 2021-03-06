package sample.cache;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@CacheConfig(cacheNames = CountryRepositoryWithUnconfiguredCache.CACHE_NAME)
public class CountryRepositoryWithUnconfiguredCache {

  public static final String CACHE_NAME = "countriesUnconfigured";

  @Cacheable
  public Country findByCode(String code) {
    return new Country(code);
  }

}
