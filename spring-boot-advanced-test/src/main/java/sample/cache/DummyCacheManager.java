package sample.cache;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Collection;
import java.util.Collections;

/**
 * Another cache manager for testing.
 *
 * @author Jens Wilke
 */
public class DummyCacheManager implements CacheManager, BeanNameAware {

  private String beanName;

  @Override
  public void setBeanName(final String name) {
    beanName = name;
  }

  @Override
  public Cache getCache(final String name) {
    return null;
  }

  @Override
  public Collection<String> getCacheNames() {
    return Collections.EMPTY_LIST;
  }

}
