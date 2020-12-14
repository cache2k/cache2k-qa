import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.cache2k.CacheEntry;
import org.cache2k.event.CacheEntryExpiredListener;
import org.cache2k.integration.AdvancedCacheLoader;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Dynamic cache creation
 *
 * @author Jens Wilke
 * @see <a href="https://github.com/cache2k/cache2k/issues/124"/>
 */
public class DynamicCacheGh124 {


	Cache<String, Cache<String, String>> type2Cache =
		new Cache2kBuilder<String, Cache<String, String>>() {}
			.name(DynamicCacheGh124.class, "type2Cache")
			// TTI emulation: expires an entry after 5-10 minutes after last access
			.refreshAhead(true)
			.expireAfterWrite(5, TimeUnit.MINUTES)
			// remove the cache, after no access comes in.
			// Attention: In cache2k 1.2 the expiry listener is always called asynchronous.
			// This creates a race condition with creating a new cache of the same type
			// Use this after cache2k 1.4 is released.
			.addListener((CacheEntryExpiredListener<String, Cache<String, String>>)
				(cache, entry) -> entry.getValue().close())
			.loader(new AdvancedCacheLoader<String, Cache<String, String>>() {
				@Override
				public Cache<String, String> load(
					final String type, long currentTime,
					CacheEntry<String, Cache<String, String>> currentEntry) {
					// TTI emulation: on expire we just return the same entry.
					// this will stay in the cache for 5 minutes more (probation period of refresh ahead)
					// and then expire, if no more requests go to this entry
					if (currentEntry != null) { return currentEntry.getValue(); }
					return buildCacheForType(type);
				}
			})
			.build();


	public String getResource(String type, String id) {
		return type2Cache.get(type).get(id);
	}

	private Map<String, Cache<String, String>> map = new ConcurrentHashMap<>();

	public String getResource2(String type, String id) {
		return map.computeIfAbsent(type, this::buildCacheForType).get(id);
	}

	private Cache<String, String> buildCacheForType(final String type) {
		return new Cache2kBuilder<String, String>() {}
			.loader(key -> getResourceInternal(type, key))
			.name(DynamicCacheGh124.class, "type-" + type)
			// more cache configuration
			.build();
	}


	private String generateCacheNameFromType(String type) {
		return "type-" + type;
	}

	private String getResourceInternal(String type, String id) {
		return "xyz";
	}

	@Test
	public void testCompletableFutureGet() throws InterruptedException {
		Executor executor = Executors.newCachedThreadPool();
		Thread t = new Thread(() -> {
			while (!Thread.interrupted()) {
				CompletableFuture<Void> future = new CompletableFuture<>();
				executor.execute(() -> future.complete(null));
				try {
					future.get();
				} catch (InterruptedException e) {
					return;
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
		});
		t.start();
		Thread.sleep(1000);
		t.interrupt();
		t.join();
	}

}
