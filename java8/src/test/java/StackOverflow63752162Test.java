import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.cache2k.CacheEntry;
import org.cache2k.expiry.ExpiryTimeValues;
import org.cache2k.integration.AdvancedCacheLoader;
import org.cache2k.processor.EntryProcessor;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Two ideas how to switch the cache on bypass during a database update.
 *
 * @see <a href="https://stackoverflow.com/questions/63752162"/>Stackoverflow question</a>
 * @author Jens Wilke
 */
public class StackOverflow63752162Test {

  @Test
  public void test() {
    CacheWrapper<Long, String> cw = new CacheWrapper<>(
      new Cache2kBuilder<Long, ValueAndState<String>>() {
      }
        .loader(new AdvancedCacheLoader<Long, ValueAndState<String>>() {
          @Override
          public ValueAndState<String> load(Long key, long now,
                                            CacheEntry<Long, ValueAndState<String>> oldEntry) {
            return new ValueAndState<>(
              realLoad(key),
              oldEntry != null && oldEntry.getValue().updating);
          }
        })
        .keepDataAfterExpired(true)
        .eternal(true)
        .expiryPolicy(
          (key, value, loadTime, oldEntry) ->
            value.updating ? ExpiryTimeValues.NOW : ExpiryTimeValues.ETERNAL)
        .build()
    );
    cw.startUpdate(123L);
    assertEquals(0, loaderCalled.get());
    cw.cache.get(123L);
    cw.cache.get(123L);
    cw.cache.get(4711L);
    cw.cache.get(4711L);
    assertEquals(3, loaderCalled.get());
  }

  AtomicLong loaderCalled = new AtomicLong();

  String realLoad(long key) {
    loaderCalled.incrementAndGet();
    return key * 2 + "";
  }

  static class ValueAndState<V> {
    V value;
    boolean updating;

    public ValueAndState(boolean value) {
      this.updating = value;
    }

    public ValueAndState(V value, boolean updating) {
      this.value = value;
      this.updating = updating;
    }
  }

  static class CacheWrapper<K, V> {

    Cache<K, ValueAndState<V>> cache;

    public CacheWrapper(Cache<K, ValueAndState<V>> cache) {
      this.cache = cache;
    }

    V read (K key) {
      return cache.get(key).value;
    }

    /**
     * @return {@code false} if update is already in progress.
     */
    boolean startUpdate(K key) {
      return cache.invoke(key, (EntryProcessor<K, ValueAndState<V>, Boolean>) entry -> {
        // check whether entry is existing to avoid trigger a load
        // we only need to load, if the data is actually read
        ValueAndState<V> v = entry.exists() ? entry.getValue() : null;
        if (v != null) {
          if (v.updating) {
            return false;
          }
          entry.setValue(new ValueAndState<>(v.value, true));
        } else {
          entry.setValue(new ValueAndState<>(true));
        }
        return true;
      });
    }
  }

  abstract class ValueOrUpdating<V> {  }

  class Value<V> extends ValueOrUpdating<V> {
    V value;
  }

  class Updating extends ValueOrUpdating<Void> {
  }

  class SimpleCacheWrapper<K, V> {

    Cache<K, ValueOrUpdating<V>> cache;

    V read(K key) {
      ValueOrUpdating<V> v = cache.get(key);
      if (v instanceof Updating) {
        return load(key);
      }
      return ((Value<V>) v).value;
    }

    V load(K key) {
      return null;
    }

  }

}
