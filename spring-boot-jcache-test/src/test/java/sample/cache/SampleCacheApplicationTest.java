package sample.cache;

import static org.junit.Assert.*;

import org.cache2k.jcache.provider.JCacheAdapter;
import org.cache2k.jcache.provider.generic.storeByValueSimulation.CopyCacheProxy;
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
  private CountryRepository countryRepository;

  @Autowired
  private OtherCountryRepository otherCountryRepository;

  @Test
  public void cachedCountry() {
    Country be = countryRepository.findByCode("BE");
    Country be2 = countryRepository.findByCode("BE");
    assertEquals(be.getSerialNumber(), (be2.getSerialNumber()));
  }

  @Test
  public void validateCache() {
    Cache countries = this.cacheManager.getCache(CountryRepository.CACHE_NAME);
    assertThat(countries).isNotNull();
    countries.clear(); // Simple test assuming the cache is empty
    assertThat(countries.get("BE")).isNull();
    Country be = this.countryRepository.findByCode("BE");
    assertThat((Country) countries.get("BE").get()).isEqualTo(be);
  }

  @Test
  public void checkForCache2kUsageDifferentDefault() {
    Cache countries = this.cacheManager.getCache(CountryRepository.CACHE_NAME);
    assertThat(countries.getNativeCache()).isInstanceOf(JCacheAdapter.class);
  }

  @Test
  public void checkForCache2kUsageXmlConfigApplies() {
    Cache countries = this.cacheManager.getCache(OtherCountryRepository.CACHE_NAME);
    assertThat(countries.getNativeCache()).isInstanceOf(CopyCacheProxy.class);
  }

  @Test
  public void cachedOtherCountry() {
    Country be = otherCountryRepository.findByCode("BE");
    Country be2 = otherCountryRepository.findByCode("BE");
    assertEquals(be.getSerialNumber(), (be2.getSerialNumber()));
  }

  @Test
  public void nullIfCacheIsUnknown() {
    assertNull(cacheManager.getCache("unknownCache"));
  }

}
