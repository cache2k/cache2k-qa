package sample.cache;

import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @author Jens Wilke
 */
@Configuration(
  proxyBeanMethods = false
)
@AutoConfigureAfter({MetricsAutoConfiguration.class, CacheAutoConfiguration.class})
@ConditionalOnBean({CacheManager.class})
public class Cache2kCacheMetricsAutoConfiguration {

  Map<String, CacheManager> allManagers;

}
