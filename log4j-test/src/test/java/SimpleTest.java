import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import static org.junit.Assert.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogManager;

/**
 * @author Jens Wilke
 */
public class SimpleTest {

  static {
    System.setProperty("log4j2.contextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
  }

  void checkThreads() {
    System.err.println("THREADS");
    for (Thread t : Thread.getAllStackTraces().keySet()) {
      System.err.println(t);
    }
    System.err.println(ThreadDump.generateThreadDump());
  }

  @Test
  public void testCache() {
    checkThreads();
    Cache<String,String> c = new Cache2kBuilder<String, String>() {}
      .expireAfterWrite(5, TimeUnit.MINUTES)
      .resilienceDuration(30, TimeUnit.SECONDS)
      .refreshAhead(true)
      .loader(this::expensiveOperation)
      .build();
    assertEquals("3", c.get("abc"));
    c.close();
    checkThreads();
  }

  String expensiveOperation(String s) {
    return s.length() + "";
  }

  @Test
  public void logTest1() {
    checkThreads();
    Logger logger = LoggerFactory.getLogger("log1");
    checkThreads();
  }

  @Test
  public void logTest2() {
    checkThreads();
    org.apache.logging.log4j.Logger logger =
      org.apache.logging.log4j.LogManager.getLogger("log2");
    checkThreads();
  }

  @Test
  public void logTest3() throws Exception {
    checkThreads();
    org.apache.logging.log4j.Logger logger =
      org.apache.logging.log4j.LogManager.getLogger("log3");
    logger.debug("hello");
    ((Closeable) logger).close();
    checkThreads();
  }



}
