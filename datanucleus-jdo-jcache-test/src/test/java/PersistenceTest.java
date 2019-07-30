import org.cache2k.Cache;
import org.cache2k.CacheManager;
import org.datanucleus.PropertyNames;
import org.datanucleus.samples.jdo.tutorial.Book;
import org.datanucleus.samples.jdo.tutorial.Inventory;
import org.datanucleus.samples.jdo.tutorial.Product;
import static org.junit.Assert.*;
import org.junit.Test;

import javax.jdo.Constants;
import javax.jdo.Extent;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Jens Wilke
 */
public class PersistenceTest {

  @Test
  public void initial() {
    Map<String,String> props = new HashMap<>();
    props.put(Constants.PROPERTY_PERSISTENCE_UNIT_NAME, "Tutorial");
    props.put(PropertyNames.PROPERTY_CACHE_L2_TYPE, "javax.cache");
    // Create a PersistenceManagerFactory for this datastore
    PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory(props);

    System.out.println("DataNucleus AccessPlatform with JDO");
    System.out.println("===================================");

    // Persistence of a Product and a Book.
    PersistenceManager pm = pmf.getPersistenceManager();
    Transaction tx = pm.currentTransaction();
    Object inventoryId = null;
    try {
      tx.begin();
      System.out.println("Persisting Inventory of products");
      Inventory inv = new Inventory("My Inventory");
      Product product = new Product("Sony Discman", "A standard discman from Sony", 200.00);
      Book book = new Book("Lord of the Rings by Tolkien", "The classic story", 49.99, "JRR Tolkien", "12345678", "MyBooks Factory");
      inv.getProducts().add(product);
      inv.getProducts().add(book);
      pm.makePersistent(inv);

      tx.commit();
      inventoryId = pm.getObjectId(inv);
      System.out.println("Inventory, Product and Book have been persisted");
    } catch (Exception e) {
      System.out.println("Exception persisting data : " + e.getMessage());
    } finally {
      if (tx.isActive()) {
        tx.rollback();
      }
      listActiveCaches();
      pm.close();
    }

    // Basic Extent of all Products
    pm = pmf.getPersistenceManager();
    tx = pm.currentTransaction();
    try {
      tx.begin();
      System.out.println("Retrieving Extent for Products");
      Extent e = pm.getExtent(Product.class, true);
      Iterator iter = e.iterator();
      int count = 0;
      while (iter.hasNext()) {
        Object obj = iter.next();
        System.out.println(">  " + obj);
        count++;
      }
      tx.commit();
      assertEquals(2, count);
    } catch (Exception e) {
      System.out.println("Exception thrown during retrieval of Extent : " + e.getMessage());
    } finally {
      if (tx.isActive()) {
        tx.rollback();
      }
      pm.close();
    }
    System.out.println("");

    // Perform some query operations
    pm = pmf.getPersistenceManager();
    tx = pm.currentTransaction();
    try {
      tx.begin();
      System.out.println("Executing Query for Products with price below 150.00");
      Query q = pm.newQuery("SELECT FROM " + Product.class.getName() +
        " WHERE price < 150.00 ORDER BY price ASC");
      List<Product> products = (List<Product>) q.execute();
      Iterator<Product> iter = products.iterator();
      while (iter.hasNext()) {
        Product p = iter.next();
        System.out.println(">  " + p);

        // Give an example of an update
        if (p instanceof Book) {
          Book b = (Book) p;
          b.setDescription("This book has been reduced in price!");
        }
      }

      tx.commit();
    } catch (Exception e) {
      System.out.println("Exception performing queries : " + e.getMessage());
    } finally {
      if (tx.isActive()) {
        tx.rollback();
      }
      listActiveCaches();
      pm.close();
    }
    System.out.println("");

    // Clean out the database
    pm = pmf.getPersistenceManager();
    tx = pm.currentTransaction();
    try {
      tx.begin();

      System.out.println("Retrieving Inventory using its id");
      Inventory inv = (Inventory) pm.getObjectById(inventoryId);

      System.out.println("Clearing out Inventory");
      inv.getProducts().clear();

      System.out.println("Deleting Inventory");
      pm.deletePersistent(inv);

      System.out.println("Deleting all products from persistence");
      Query q = pm.newQuery(Product.class);
      long numberInstancesDeleted = q.deletePersistentAll();
      System.out.println("Deleted " + numberInstancesDeleted + " products");

      tx.commit();
    } catch (Exception e) {
      System.out.println("Exception cleaning out the database : " + e.getMessage());
    } finally {
      if (tx.isActive()) {
        tx.rollback();
      }
      pm.close();
    }


  }

  public void listActiveCaches() {
    for (Cache c : CacheManager.getInstance().getActiveCaches()) {
      System.out.println(c.toString());
    }
  }

}
