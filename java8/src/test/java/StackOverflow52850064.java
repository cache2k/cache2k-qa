import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.cache2k.CacheEntry;
import org.cache2k.event.CacheEntryExpiredListener;
import org.cache2k.processor.EntryProcessor;
import org.cache2k.processor.MutableCacheEntry;

import java.util.concurrent.TimeUnit;

/**
 * No test, example for Stackoverflow question: How to create a write-behind cache from inmemory to database?

 *
 * @see <a href="https://stackoverflow.com/questions/52850064/how-to-create-a-write-behind-cache-from-inmemory-to-database">Stackoverflow question</a>
 * @author Jens Wilke
 */
public class StackOverflow52850064 {

  public static class WriteBehindCache {

      Cache<String, Object> cache = Cache2kBuilder.of(String.class, Object.class)
        .addListener((CacheEntryExpiredListener<String, Object>) (cache, entry)
          -> persist(entry.getKey(), entry.getValue()))
        .expireAfterWrite(60, TimeUnit.SECONDS)
        .build();

      public void add(String key, Object data) {
        cache.put(key, data);
      }

      public Object get(String key) {
        return cache.invoke(key, e -> {
          if (e.exists()) {
            Object v = e.getValue();
            e.remove();
            return v;
          }
          return loadAndRemove(e.getKey());
        });
      }


      // stubs
      protected void persist(String key, Object value) {
      }

      protected Object loadAndRemove(String key) {
        return null;
      }

    }

}

