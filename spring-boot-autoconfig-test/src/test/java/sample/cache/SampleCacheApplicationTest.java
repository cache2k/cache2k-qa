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

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SampleCacheApplicationTest {

  @Autowired
  private CacheManager cacheManager;

  @Autowired
  private MeterRegistry meterRegistry;

  @Autowired
  private CountryRepository countries;

  @Test
  public void knownCache() {
    assertTrue(cacheManager.getCacheNames().contains("definedByXml"));
  }

  @Test
  public void checkUnknownCacheIsCreated() {
    assertTrue("not configured",
      !((SpringCache2kCacheManager) cacheManager).getConfiguredCacheNames().contains(CountryRepository.CACHE_NAME));
    String KEY = "de";
    assertTrue("caching, if returned object is identical",
      countries.findByCode(KEY) ==
      countries.findByCode(KEY));
    assertTrue("known by the cache manager",
      cacheManager.getCacheNames().contains(CountryRepository.CACHE_NAME));
  }

}
