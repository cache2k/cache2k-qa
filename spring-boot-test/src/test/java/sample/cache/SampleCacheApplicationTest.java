package sample.cache;

import static org.junit.Assert.*;

import org.cache2k.integration.CacheLoaderException;
import org.cache2k.processor.EntryProcessor;
import org.cache2k.processor.MutableCacheEntry;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SampleCacheApplicationTest {

  @Autowired
  private CacheManager cacheManager;

  @Autowired
  private LoadingBySetupCountryRepository countryRepository;

  @Autowired
  private CountryRepositoryWithNegativeCaching countryRepositoryWithNegativeCaching;

  @Autowired
  private LoadingBySetupCountryRepository loadingBySetupCountryRepository;

  @Test
  public void retrieveCacheViaCacheManager() {
    Cache countries = this.cacheManager.getCache("countriesViaCacheManager");
    assertThat(countries).isNotNull();
    countries.clear(); // Simple test assuming the cache is empty
    assertThat(countries.get("BE")).isNull();
  }

  /**
   * Check that the cache is active, which means the identical objects are returned.
   */
  @Test
  public void validateCache() {
    Country be1 = this.countryRepository.findByCode("BE");
    Country be2 = this.countryRepository.findByCode("BE");
    assertTrue(be1 == be2);
  }

  @Test
  public void validateSyncCache() {
    Country be1 = this.countryRepository.findByCodeSync("BE");
    Country be2 = this.countryRepository.findByCodeSync("BE");
    assertTrue(be1 == be2);
  }

  @Test
  public void checkForCache2kUsage() {
    Cache countries = this.cacheManager.getCache("countriesViaCacheManager");
    assertTrue(countries.getNativeCache() instanceof org.cache2k.Cache);
  }

  @Test
  public void loaderBySetupWorks() {
    Country be1 = this.loadingBySetupCountryRepository.findByCode("BE");
    Country be2 = this.loadingBySetupCountryRepository.findByCodeSync("BE");
    assertTrue(be1 == be2);
  }

  @Test
  public void exceptionFromLoader() {
    String key = "ER";
    Cache countries = this.cacheManager.getCache(LoadingBySetupCountryRepository.CACHE_NAME);
    org.cache2k.Cache c2kCache = (org.cache2k.Cache) countries.getNativeCache();
    c2kCache.invoke(key, new EntryProcessor() {
      @Override
      public Object process(final MutableCacheEntry e) throws Exception {
        e.setException(new IllegalAccessException());
        return null;
      }
    });
    try {
      Country be1 = this.loadingBySetupCountryRepository.findByCode(key);
      fail("exception expected");
    } catch (CachingConfig.SpecialWrapException ex) {
      assertThat(ex.getCause()).isInstanceOf(CacheLoaderException.class);
    }
  }

  @Test
  public void negativeCaching_nullSupport() {
    Country country = countryRepositoryWithNegativeCaching.findByCode("123");
    assertNull(country);
  }

}
