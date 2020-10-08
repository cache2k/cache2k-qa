package sample.cache;

import io.micrometer.core.instrument.MeterRegistry;
import org.cache2k.extra.spring.SpringCache2kCacheManager;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SampleCacheApplicationTest {

  @Autowired
  private CacheManager dummyCacheManager;

  @Autowired
  private CacheManager cacheManager;

  @Autowired
  private LoadingBySetupCountryRepository countryRepository;

  @Autowired
  private MeterRegistry meterRegistry;

  @Autowired
  private CountryRepositoryWithUnconfiguredCache countriesWithUnconfiguredCache;

  @Test
  public void checkMetersBoundForConfiguredCaches() {
    meterRegistry.get("cache.puts")
      .tag("cache", LoadingBySetupCountryRepository.CACHE_NAME).functionCounter();
  }

  @Test
  public void checkUnknownCacheIsCreated() {
    assertTrue("not configured",
      !((SpringCache2kCacheManager) cacheManager).getConfiguredCacheNames().contains(CountryRepositoryWithUnconfiguredCache.CACHE_NAME));
    String KEY = "de";
    assertTrue("caching, if returned object is identical",
      countriesWithUnconfiguredCache.findByCode(KEY) ==
      countriesWithUnconfiguredCache.findByCode(KEY));
    assertTrue("known by the cache manager",
      cacheManager.getCacheNames().contains(CountryRepositoryWithUnconfiguredCache.CACHE_NAME));
  }

  @Test
  public void checkMetersBoundUnconfiguredCache() {
    Country c1 = countriesWithUnconfiguredCache.findByCode("DE");
    Country c2 = countriesWithUnconfiguredCache.findByCode("DE");
    assertTrue(c1 == c2);
    meterRegistry.get("cache.puts")
      .tag("cache", countriesWithUnconfiguredCache.CACHE_NAME).functionCounter();
  }

}
