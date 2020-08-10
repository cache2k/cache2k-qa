import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import static org.junit.Assert.*;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * @author Jens Wilke
 */
public class SimpleTest {

  static {
    System.setProperty("log4j2.contextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
  }

  /*
  void checkThreads() {
    System.err.println("THREADS");
    for (Thread t : Thread.getAllStackTraces().keySet()) {
      System.err.println(t);
    }
    System.err.println(ThreadDump.generateThreadDump());
  }
  */

  @Test
  public void testCache() {
    Cache<String,String> c = new Cache2kBuilder<String, String>() {}
      .expireAfterWrite(5, TimeUnit.MINUTES)
      .resilienceDuration(30, TimeUnit.SECONDS)
      .refreshAhead(true)
      .loader(this::expensiveOperation)
      .build();
    assertEquals("3", c.get("abc"));
    c.close();
    assertTrue("Log framework created the debug logger",
      org.apache.logging.log4j.LogManager.getContext().hasLogger("org.cache2k.core.util.Log"));
  }

  private String expensiveOperation(String s) {
    return s.length() + "";
  }

}
