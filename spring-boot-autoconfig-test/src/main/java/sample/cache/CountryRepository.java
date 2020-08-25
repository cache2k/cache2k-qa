package sample.cache;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@CacheConfig(cacheNames = CountryRepository.CACHE_NAME)
public class CountryRepository {

  public static final String CACHE_NAME = "contriesUnconfigured";

  @Cacheable
  public Country findByCode(String code) {
    return new Country(code);
  }

}
