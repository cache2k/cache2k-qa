package org.hibernate.cache.jcache.access;

import org.hibernate.cache.jcache.JCacheTransactionalDataRegion;

/**
 * @author Alex Snaps
 */
public class ItemValueExtractor extends AbstractReadWriteRegionAccessStrategy<JCacheTransactionalDataRegion> {

  /**
   * Creates a read/write cache access strategy around the given cache region.
   */
  public ItemValueExtractor(JCacheTransactionalDataRegion region) {
    super(region);
  }


  public static <T> T getValue(final Object entry) {
    if(!(entry instanceof AbstractReadWriteRegionAccessStrategy.Item)) {
      throw new IllegalArgumentException("Entry needs to be of type " + AbstractReadWriteRegionAccessStrategy.Item.class.getName());
    }
    return (T)((AbstractReadWriteRegionAccessStrategy.Item)entry).getValue();
  }

}
