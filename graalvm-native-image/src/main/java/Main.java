import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.cache2k.CacheEntry;
import org.cache2k.core.InternalCache;
import org.cache2k.event.CacheEntryCreatedListener;
import org.cache2k.expiry.ExpiryPolicy;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Minimal test suite that triggers different feature sets within cache2k.
 * These are: integer and long key type variation, listeners (triggers WiredCache implementation),
 * expiry/timing
 */
public class Main {

  public static void main(String[] args) throws Exception {
    new Main().runAll();
  }

  /**
   * We don't use the JUnit test harness which requires configuration for reflection.
   *
   */
  public void runAll() throws Exception {
    testIntegerCache();
    testLongCache();
    testStringCache();
    testWithListener();
    testWithExpiry();
    System.out.println("No worries! All good :)");
  }

  public void testIntegerCache() {
    Cache<Integer, Integer> cache = Cache2kBuilder.of(Integer.class, Integer.class)
      .entryCapacity(100)
      .build();
    cache.put(1, 1);
    assertTrue(cache.containsKey(1));
    cache.close();
  }

  public void testLongCache() {
    Cache<Long, Integer> cache = Cache2kBuilder.of(Long.class, Integer.class)
      .entryCapacity(100)
      .build();
    cache.put(1L, 1);
    assertTrue(cache.containsKey(1L));
    cache.close();
  }

  public void testStringCache() {
    Cache<String, Integer> cache = Cache2kBuilder.of(String.class, Integer.class)
      .entryCapacity(100)
      .build();
    cache.put("abc", 1);
    assertTrue(cache.containsKey("abc"));
    cache.close();
  }

  public void testWithListener() {
    final AtomicBoolean listenerCalled = new AtomicBoolean();
    Cache<String, Integer> cache = Cache2kBuilder.of(String.class, Integer.class)
      .entryCapacity(100)
      .addListener(new CacheEntryCreatedListener<String, Integer>() {
        @Override
        public void onEntryCreated(Cache<String, Integer> cache, CacheEntry<String, Integer> entry) {
          listenerCalled.set(true);
        }
      })
      .build();
    cache.put("abc", 1);
    assertTrue(cache.containsKey("abc"));
    assertTrue(listenerCalled.get());
    cache.close();
  }

  /**
   * Test with expiry policy since {@link Cache2kBuilder#expireAfterWrite(long, TimeUnit)} may lag.
   */
  public void testWithExpiry() throws InterruptedException {
    long expireAfterWriteMillis = 100;
    Cache<String, Integer> cache = Cache2kBuilder.of(String.class, Integer.class)
      .entryCapacity(100)
      .sharpExpiry(true)
      .expiryPolicy(new ExpiryPolicy<String, Integer>() {
        @Override
        public long calculateExpiryTime(String key, Integer value, long loadTime, CacheEntry<String, Integer> oldEntry) {
          return loadTime + expireAfterWriteMillis;
        }
      })
      .build();
    long t0 = System.currentTimeMillis();
    cache.put("abc", 1);
    boolean there = cache.containsKey("abc");
    assertTrue("entry is visible, or too much time has passed",
      System.currentTimeMillis() - t0 >= expireAfterWriteMillis || there);
    Thread.sleep(expireAfterWriteMillis);
    if (System.currentTimeMillis() - t0 < expireAfterWriteMillis) {
      System.err.println("sleep was too fast");
    }
    assertFalse("entry not visible, since expired", cache.containsKey("abc"));
    // removal of the cache entry happens a bit after expiry
    // this check whether the timer event is processed
    long waitTime = 1000;
    t0 = System.currentTimeMillis();
    while (cache.asMap().size() > 0 && System.currentTimeMillis() - t0 < waitTime) { }
    assertTrue("entry is removed after expiry", cache.asMap().size() == 0);
    cache.close();
  }

}
