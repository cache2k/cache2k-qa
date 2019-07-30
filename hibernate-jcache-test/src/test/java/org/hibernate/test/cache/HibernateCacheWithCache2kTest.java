/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.cache;

import org.cache2k.CacheManager;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cache.jcache.access.ItemValueExtractor;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.stat.QueryStatistics;
import org.hibernate.stat.SecondLevelCacheStatistics;
import org.hibernate.stat.Statistics;
import org.hibernate.test.domain.Event;
import org.hibernate.test.domain.EventManager;
import org.hibernate.test.domain.Item;
import org.hibernate.test.domain.Person;
import org.hibernate.test.domain.PhoneNumber;
import org.hibernate.test.domain.VersionedItem;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Test;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

/**
 * Some experiments.
 *
 * @author Jens Wilke
 */
public class HibernateCacheWithCache2kTest extends BaseNonConfigCoreFunctionalTestCase {

  private static final String REGION_PREFIX = "hibernate.test.";

  public HibernateCacheWithCache2kTest() {
    System.setProperty( "derby.system.home", "target/derby" );
  }

  @Override
  @SuppressWarnings("unchecked")
  protected void addSettings(Map settings) {
    super.addSettings( settings );
    settings.put( AvailableSettings.GENERATE_STATISTICS, "true" );
  }

  @Override
  protected void configureStandardServiceRegistryBuilder(StandardServiceRegistryBuilder ssrb) {
    super.configureStandardServiceRegistryBuilder( ssrb );
    ssrb.configure( "hibernate-config/hibernate.cfg.xml" );
  }



  @Test
  public void testRequestNonExistentEvent() {
    EventManager mgr = new EventManager( sessionFactory() );
    Statistics stats = sessionFactory().getStatistics();
    Session s = sessionFactory().getCurrentSession();
    Transaction tx = s.beginTransaction();
    Event data = s.get( Event.class, 1234L );
    data = s.get( Event.class, 1234L );
    tx.rollback();
    s = sessionFactory().getCurrentSession();
    tx = s.beginTransaction();
    data = s.get( Event.class, 1234L );
    data = s.get( Event.class, 1234L );
    tx.rollback();
    CacheManager c2kmgr = CacheManager.getInstance();
    for ( org.cache2k.Cache c : c2kmgr.getActiveCaches()) {
      System.err.println(c);
    }
  }

  @Test
  public void testStoreSomething() {
    EventManager mgr = new EventManager( sessionFactory() );
    Statistics stats = sessionFactory().getStatistics();
    Long pid = mgr.createAndStorePerson( "Jens" , "Wilke" );
    Session s = sessionFactory().getCurrentSession();
    s.beginTransaction();
    Person p = s.byId(Person.class).load( pid );
    s.getTransaction().commit();
    Long eventId = mgr.createAndStoreEvent( "intro" , p , new Date() );
    s = sessionFactory().getCurrentSession();
    s.beginTransaction();
    Event event = s.byId( Event.class ).load( eventId );
    s.getTransaction().commit();
    s = sessionFactory().getCurrentSession();
    s.beginTransaction();
    event = s.byId( Event.class ).load( eventId );
    s.getTransaction().commit();

    s = sessionFactory().getCurrentSession();
    s.beginTransaction();
    event = s.byId( Event.class ).load( 123L );
    s.getTransaction().commit();

    CacheManager c2kmgr = CacheManager.getInstance();
    for ( org.cache2k.Cache c : c2kmgr.getActiveCaches()) {
      System.err.println(c);
    }
  }

}