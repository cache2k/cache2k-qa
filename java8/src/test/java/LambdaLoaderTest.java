import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import static org.junit.Assert.*;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * @author Jens Wilke
 */
public class LambdaLoaderTest {

  @Test
  public void test() {
    Cache<String,String> c = new Cache2kBuilder<String, String>() {}
      .expireAfterWrite(5, TimeUnit.MINUTES)
      .resilienceDuration(30, TimeUnit.SECONDS) // cope with at most 30 seconds
                                                // outage before propagating exceptions
      .refreshAhead(true)                       // make sure a fresh value is always there
      .loader(this::expensiveOperation)         // auto populating function
      .build();
    assertEquals("3", c.get("abc"));
    c.close();
  }

  String expensiveOperation(String s) {
    return s.length() + "";
  }

}
