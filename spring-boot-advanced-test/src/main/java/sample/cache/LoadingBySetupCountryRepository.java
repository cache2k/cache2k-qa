package sample.cache;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@CacheConfig(cacheNames = LoadingBySetupCountryRepository.CACHE_NAME)
public class LoadingBySetupCountryRepository {

  public static final String CACHE_NAME = "loadingBySetupCountries";

  @Cacheable
  public Country findByCode(String code) {
    return null;
  }

  @Cacheable(sync = true)
  public Country findByCodeSync(String code) {
    return null;
  }

}
