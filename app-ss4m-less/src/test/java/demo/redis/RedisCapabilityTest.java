package demo.redis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;

import demo.util.TimeTaskHelper;

/**
 * Redis性能测试
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 1.0
 */
public class RedisCapabilityTest extends BaseJunitForSpringRedis {

	@Autowired
	private RedisTemplate redisTemplate;

	/**
	 * 并发测试
	 */
	@Test
	public void multiTest() throws Exception {
		System.out.println("-------------------并发测试-------------------");
		int threads = 1000;

		long time = TimeTaskHelper.timeTasks(threads, new Runnable() {

			@Override
			public void run() {
				redisTemplate.opsForValue().set("hi", "SvenAugustus");
			}
		});
		redisTemplate.delete("hi");
		System.out.println("平均耗时：" + time / (threads * 1.0) + "ms");
	}

	/**
	 * hash 性能测试
	 */
	@Test
	public void hashOperationsCapabilityTest() throws InterruptedException {
		System.out.println("-------------------hash 性能测试-------------------");
		final BoundHashOperations ops = redisTemplate.boundHashOps("testHashCapability");
		ops.delete();

		Map m = new HashMap(20000);
		for (int i = 0; i < 3000000; i++) {/// 300w
			m.put(i, i);
			if (i > 0 && i % 10000 == 0) {
				ops.putAll(m);
				m.clear();
			}
		}
		ops.putAll(m);
		System.out.println(ops.size());

		/**
		 * 因为HGETALL取数据比较大的情况，会导致其他操作阻塞，因此分开执行
		 */
		Thread t1 = new Thread(new Runnable() {
			@SuppressWarnings("unused")
			public void run() {
				// System.out.println("entries开始时间：" + new
				// Date().toLocaleString());
				long start = System.currentTimeMillis();
				try {
					Map m = ops.entries();
					Iterator<Map.Entry> cursor = m.entrySet().iterator();
					while (cursor.hasNext()) {
						Map.Entry entry = cursor.next();
						// System.out.println(entry.getKey() + "," +
						// entry.getValue());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				// System.out.println(m);
				long end = System.currentTimeMillis();
				// System.out.println("entries结束时间：" + new
				// Date().toLocaleString());
				System.out.println("entries：" + (end - start) + "ms");
			}
		});

		Thread t2 = new Thread(new Runnable() {
			@SuppressWarnings("unused")
			public void run() {
				// System.out.println("entriesIterator开始时间：" + new
				// Date().toLocaleString());
				long start = System.currentTimeMillis();
				ScanOptions scanOptions = ScanOptions.scanOptions().match("*").count(10000).build();
				Iterator<Map.Entry> cursor = ops.scan(scanOptions);
				while (cursor.hasNext()) {
					Map.Entry entry = cursor.next();
					// System.out.println(entry.getKey() + "," +
					// entry.getValue());
				}
				long end = System.currentTimeMillis();
				// System.out.println("entriesIterator结束时间：" + new
				// Date().toLocaleString());
				System.out.println("entriesIterator 10000：" + (end - start) + "ms");
			}
		});

		t1.start();

		t1.join();

		t2.start();

		t2.join();

		// 删除
		long start = System.currentTimeMillis();
		System.out.println(ops.delete());
		long end = System.currentTimeMillis();
		System.out.println("delete：" + (end - start) + "ms");

		/**
		 * 测试版本：redis 3.2.0
		 * 
		 * 很清晰地看出来 HSCAN（当然count注意不要设置过大） 比 HGETALL命令更优
		 * 
		 * 400w的数据 entries：12015ms entriesIterator 10000：7326ms true
		 * delete：1827ms
		 * 
		 * 删除DEL操作，在100w内 对于Hash来说还是可以接受的。 但上百万乃至1kw以上的海量数据，就需要考虑HSCAN来删除。
		 * 如此，不建议使用Hash存储巨海量的数据。
		 */
	}

	/**
	 * list 性能测试
	 */
	@Test
	public void listOperationsCapabilityTest() throws InterruptedException {
		System.out.println("-------------------list 性能测试-------------------");
		final BoundListOperations ops = redisTemplate.boundListOps("testListCapability");
		redisTemplate.delete(ops.getKey());

		List list = new ArrayList(10000);
		for (int i = 0; i < 10000000; i++) {/// 1kw
			list.add(i);
			if (i > 0 && i % 10000 == 0) {
				ops.leftPushAll(list.toArray());
				list.clear();
			}
		}
		ops.leftPushAll(list.toArray());
		System.out.println(ops.size());

		/**
		 * 因为LRANGE取数据区间比较大的情况，会导致其他操作阻塞，因此分开执行
		 */
		Thread t1 = new Thread(new Runnable() {
			@SuppressWarnings("unused")
			public void run() {
				// System.out.println("elements开始时间：" + new
				// Date().toLocaleString());
				long start = System.currentTimeMillis();
				try {
					List list = ops.range(0L, -1L);// all
					Iterator cursor = list.iterator();
					while (cursor.hasNext()) {
						Object o = cursor.next();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				// System.out.println(list);
				long end = System.currentTimeMillis();
				// System.out.println("elements结束时间：" + new
				// Date().toLocaleString());
				System.out.println("elements：" + (end - start) + "ms");
			}
		});

		Thread t2 = new Thread(new Runnable() {
			@SuppressWarnings("unused")
			public void run() {
				// System.out.println("hashScan开始时间：" + new
				// Date().toLocaleString());
				long start = System.currentTimeMillis();

				long pageSize = 10000;
				long startIndex = 0;

				while ((startIndex + pageSize) <= ops.size()) {
					Iterator cursor = ops.range(startIndex, startIndex + pageSize).iterator();
					while (cursor.hasNext()) {
						Object o = cursor.next();
						// System.out.println(entry.getKey() + "," +
						// entry.getValue());
					}
					startIndex += pageSize;
				}
				long end = System.currentTimeMillis();
				// System.out.println("hashScan结束时间：" + new
				// Date().toLocaleString());
				System.out.println("range by 10000 each time：" + (end - start) + "ms");
			}
		});

		t1.start();

		t1.join();

		t2.start();

		t2.join();

		// 删除
		long start = System.currentTimeMillis();
		redisTemplate.delete(ops.getKey());
		long end = System.currentTimeMillis();
		System.out.println("delete：" + (end - start) + "ms");

		/**
		 * 测试版本：redis 3.2.0 很清晰地看出来 LRANGE 少量多次（当然区间注意不要设置过大） 比 LRANGE全部区间 命令更优
		 * 
		 * 1kw的数据
		 * 
		 * elements：21075ms range by 10000 each time：7531ms true delete：12ms
		 * 
		 * 删除DEL操作，在1kw内 对于List来说还是可以接受的。
		 */
	}

}
