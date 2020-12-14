import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import static org.junit.Assert.*;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * Tests with Java 8 lambda expressions
 *
 * @author Jens Wilke
 */
public class LambdaInvokeTest {

  @Test
  public void remove_return_old() {
    Cache<String,String> cache = new Cache2kBuilder<String, String>() {}
      .build();
    cache.put("ABC", "123");
    String s = cache.invoke("ABC", e -> e.remove().getValue());
    assertEquals("123", s);
    assertFalse(cache.containsKey("ABC"));
    cache.close();
  }

  @Test
  public void remove_existing() {
    Cache<String,String> cache = new Cache2kBuilder<String, String>() {}
      .build();
    cache.put("ABC", "123");
    boolean exists = cache.invoke("ABC", e -> e.remove().exists());
    assertTrue(exists);
    assertFalse(cache.containsKey("ABC"));
    cache.close();
  }

  @Test
  public void put_with_expiry() {
    Cache<String,String> cache = new Cache2kBuilder<String, String>() {}
      .expireAfterWrite(3, TimeUnit.HOURS)
      .build();
    cache.invoke("ABC",
      e -> e.setValue("hello")
            .setExpiryTime(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(120)));
    cache.close();
  }

  @Test
  public void get_refreshed_time() {
    Cache<String,String> cache = new Cache2kBuilder<String, String>() {}
      .expireAfterWrite(3, TimeUnit.HOURS)
      .build();
    final String key = "abc";
    cache.put(key, "123");
    long refreshedTime = cache.invoke(key, e -> e.getModificationTime());
  }

}
