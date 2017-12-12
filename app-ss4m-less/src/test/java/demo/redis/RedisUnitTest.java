package demo.redis;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import io.flysium.framework.message.ResponseResult;

/**
 * Redis基本单元测试
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 1.0
 */
public class RedisUnitTest extends BaseJunitForSpringRedis {

	@Autowired
	private RedisTemplate redisTemplate;

	/**
	 * 基本api测试
	 */
	@Test
	public void baseTest() throws Exception {
		System.out.println("-------------------基本api测试-------------------");
		redisTemplate.opsForValue().set("h", 100);
		Object result = redisTemplate.opsForValue().get("h");
		System.out.println("h 取值：" + result);
		Assert.assertTrue(Integer.valueOf(100).equals(result));

		redisTemplate.opsForValue().set("hi", "SvenAugustus", 1000, TimeUnit.MILLISECONDS);
		result = redisTemplate.opsForValue().get("hi");
		System.out.println("hi 取值：" + result);
		Assert.assertTrue("SvenAugustus".equals(result));
		try {
			Thread.sleep(2000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("2秒后再取 hi ,已失效");
		result = redisTemplate.opsForValue().get("hi");
		System.out.println(result);
		Assert.assertNull(result);

		redisTemplate.opsForValue().set("hi2", "SvenAugustus2");
		redisTemplate.delete("hi2");
		System.out.println("删除后再取 hi2 ,已不存在");
		Assert.assertNull(redisTemplate.opsForValue().get("hi2"));
	}

	/**
	 * 读写测试
	 */
	@Test
	public void rwTest() throws Exception {
		System.out.println("-------------------读写测试-------------------");
		redisTemplate.opsForValue().set("h3", 100);
		Object result = redisTemplate.opsForValue().get("h3");
		System.out.println("读写基本类型：" + result);
		System.out.println("读写基本类型：" + result.getClass());
		Assert.assertTrue(result != null && Integer.class.isInstance(result));

		redisTemplate.opsForValue().set("h4", "您好啊SvenAugustus,欢迎来到Redis");
		result = redisTemplate.opsForValue().get("h4");
		System.out.println("读写String：" + result);
		Assert.assertTrue(result != null && String.class.isInstance(result));

		Map m = new HashMap();
		m.put("key1", "您好");
		m.put("key2", "sven");
		redisTemplate.opsForValue().set("h5", m);
		result = redisTemplate.opsForValue().get("h5");
		System.out.println("读写HashMap（仅作测试，您可以考虑redis hash存储）：" + result);
		Assert.assertTrue(result != null && Map.class.isInstance(result));

		ResponseResult vo = new ResponseResult("0000", "message");
		vo.setResult(m);
		redisTemplate.opsForValue().set("h6", vo);
		result = redisTemplate.opsForValue().get("h6");
		System.out.println("读写VO：" + result);
		Assert.assertTrue(result != null && ResponseResult.class.isInstance(result));
	}

	/**
	 * value api测试
	 */
	@Test
	public void valueOperationsTest() throws Exception {
		System.out.println("-------------------value api测试-------------------");
		BoundValueOperations ops = redisTemplate.boundValueOps("testValue");
		redisTemplate.delete(ops.getKey());
		redisTemplate.delete(Arrays.asList("testValue", "testValue2", "testValueNil"));

		System.out.println("increment 后：" + ops.increment(1L));
		System.out.println("decrement 后：" + ops.increment(-1L));
		System.out.println("incrementBy 后：" + ops.increment(100));

		ops.set("1234567890您好");
		/**
		 * 以下 APPEND GETRANGE 命令 在使用FastJsonRedisTemplate情况下有问题 后续再修复
		 */
		// System.out.println("appendString前：" + ops.get());
		// System.out.println("appendString：" + ops.append("SvenAugustus"));
		// System.out.println("appendString后：" + ops.get());
		// System.out.println("getRange（0，2）：" + ops.get(0, 2));
		// System.out.println("getRange（0，100）：" + ops.get(0, 100));

		ops.set(0.01, 2000);
		System.out.println("getExpire失效毫秒：" + ops.getExpire());
		ops.persist();
		System.out.println("persist持久化后 getExpire失效毫秒：" + ops.getExpire());

		ops.set(0.01);
		System.out.println("set 0.01 后：" + ops.get());
		System.out.println("size:" + ops.size());
		ops.set("1234567890您好");
		System.out.println("size:" + ops.size());

		System.out.println("getAndSet操作：" + ops.getAndSet("hi"));
		System.out.println("getAndSet后：" + ops.get());

		System.out.println("setIfAbsent前：" + ops.get());
		System.out.println(ops.setIfAbsent("hi2"));
		System.out.println("setIfAbsent后：" + ops.get());

		Map keyValues = new HashMap();
		keyValues.put("testValue", 0.02);
		keyValues.put("testValue2", "SvenAugustus2");
		keyValues.put("testValueNil", "nil");

		System.out.println(
				"multiGet结果：" + redisTemplate.opsForValue().multiGet(Arrays.asList("testValue", "testValue2")));;
		redisTemplate.delete("testValueNil");
		System.out.println("删除testValueNil后 multiGet结果："
				+ redisTemplate.opsForValue().multiGet(Arrays.asList("testValue", "testValue2", "testValueNil")));;

		System.out.println("multiSet前：" + redisTemplate.opsForValue().multiGet(keyValues.keySet()));
		redisTemplate.opsForValue().multiSet(keyValues);
		System.out.println("multiSet后：" + redisTemplate.opsForValue().multiGet(keyValues.keySet()));
		redisTemplate.delete(Arrays.asList("testValue", "testValue2", "testValueNil"));

		keyValues.clear();
		keyValues.put("testValue", 0.03);
		keyValues.put("testValue2", "SvenAugustus3");
		keyValues.put("testValueNil2", "nil3");
		redisTemplate.opsForValue().set("testValue", 0.01);
		redisTemplate.delete("testValueNil2");
		System.out.println("multiSetIfAbsent前：" + redisTemplate.opsForValue().multiGet(keyValues.keySet()));
		System.out.println("multiSetIfAbsent操作结果：" + redisTemplate.opsForValue().multiSetIfAbsent(keyValues));
		System.out.println("multiSetIfAbsent后：" + redisTemplate.opsForValue().multiGet(keyValues.keySet()));

		keyValues.clear();
		keyValues.put("testValue", 0.03);
		keyValues.put("testValue2", "SvenAugustus3");
		keyValues.put("testValueNil2", "nil3");
		redisTemplate.delete(keyValues.keySet());
		System.out.println("multiSetIfAbsent前：" + redisTemplate.opsForValue().multiGet(keyValues.keySet()));
		System.out.println("multiSetIfAbsent操作结果：" + redisTemplate.opsForValue().multiSetIfAbsent(keyValues));
		System.out.println("multiSetIfAbsent后：" + redisTemplate.opsForValue().multiGet(keyValues.keySet()));

		System.out.println("匹配 * ：" + redisTemplate.keys("*"));
		System.out.println("匹配 *Value* ：" + redisTemplate.keys("*Value*"));
	}

	/**
	 * 管道（Pipelining）操作测试
	 * 
	 * 
	 * @throws Exception
	 */
	@Test
	public void pipeliningTest() throws Exception {
		System.out.println("-------------------管道（Pipelining）测试-------------------");
		List<Object> pResults = null;
		pResults = redisTemplate.executePipelined(new RedisCallback() {

			@Override
			public Object doInRedis(RedisConnection redisconnection) throws DataAccessException {
				for (int i = 0; i < 100; i++) {
					redisTemplate.opsForValue().set(String.valueOf(i), i);
				}
				return null;
			}
		});
		System.out.println("pResults：" + pResults);
		pResults = redisTemplate.executePipelined(new SessionCallback() {

			@Override
			public Object execute(RedisOperations redisoperations) {
				for (int i = 0; i < 100; i++) {
					redisoperations.opsForValue().get(String.valueOf(i));
				}
				return null;
			}
		});
		System.out.println("pResults：" + pResults);
		pResults = redisTemplate.executePipelined(new SessionCallback() {

			@Override
			public Object execute(RedisOperations redisoperations) {
				for (int i = 0; i < 100; i++) {
					redisoperations.delete(String.valueOf(i));
				}
				return null;
			}
		});
		System.out.println("pResults：" + pResults);
	}

	/**
	 * hash api测试
	 */
	@Test
	public void hashOperationsTest() {
		System.out.println("-------------------hash api测试-------------------");
		BoundHashOperations ops = redisTemplate.boundHashOps("testHash");
		redisTemplate.delete(ops.getKey());

		Map keyValues = new HashMap();
		keyValues.put(1, 0.02);
		keyValues.put(2, "SvenAugustus2");
		keyValues.put(3, "哈哈233");
		ops.putAll(keyValues);
		System.out.println("putAll后 entries：" + ops.entries());;
		System.out.println("get 2：" + ops.get(2));;
		System.out.println("getElements ：" + ops.multiGet(Arrays.asList(1, 2, 3)));;

		System.out.println("keySet ：" + ops.keys());;
		System.out.println("values ：" + ops.values());;
		System.out.println("size：" + ops.size());;

		System.out.println("containsKey 1：" + ops.hasKey(1));;
		System.out.println("removeElements：" + (ops.delete(1, 2) == 2L));
		System.out.println("entries：" + ops.entries());;
		System.out.println("size：" + ops.size());;
		System.out.println("containsKey 1：" + ops.hasKey(1));;

		System.out.println("increment 1：" + ops.increment(1, 1));;
		System.out.println("incrementBy 1 100：" + ops.increment(1, 100));;
	}

	/**
	 * list api测试
	 */
	@Test
	@SuppressWarnings("deprecation")
	public void listOperationsTest() throws InterruptedException {
		System.out.println("-------------------list api测试-------------------");
		final BoundListOperations ops = redisTemplate.boundListOps("testList");
		final BoundListOperations ops2 = redisTemplate.boundListOps("testList2");

		redisTemplate.delete(ops.getKey());
		redisTemplate.delete(ops2.getKey());

		ops.leftPushIfPresent("0");
		ops.leftPush("1");
		ops.leftPush("2");
		ops.leftPushIfPresent("3");
		ops.leftPush("1");
		ops.leftPush("2");

		System.out.println("size：" + ops.size());
		System.out.println("getFirst：" + ops.index(0));
		System.out.println("getLast：" + ops.index(-1));

		ops.leftPush("1", "9");
		System.out.println("addBeforePivot 1 9 后 elements：" + ops.range(0L, -1L));
		ops.rightPop();
		System.out.println("removeLast后 elements：" + ops.range(0L, -1L));
		// ops.clear();
		ops.trim(-1L, 0L);// clear all
		System.out.println("clear后 elements：" + ops.range(0L, -1L));

		new Thread(new Runnable() {

			@Override
			public void run() {
				System.out.println("准备addFirst：" + new Date().toLocaleString());
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				ops.leftPush("1");
				System.out.println("结束addFirst：" + new Date().toLocaleString());
			}
		}).start();
		System.out.println(
				"阻塞removeFirst：" + new Date().toLocaleString() + "，结果: " + ops.leftPop(1000, TimeUnit.MILLISECONDS));

		ops.leftPushAll(1, 2, 3, 4, 5, 5, 6, 7, 8, 6, 9);
		System.out.println("elements：" + ops.range(0L, -1L));
		ops.remove(1, 5);// reomve first 1 element which equals 5
		System.out.println("reomve first 1 element which equals 5 后：elements：" + ops.range(0L, -1L));
		ops.set(0, 77);// set first
		System.out.println("setFirst后： elements：" + ops.range(0L, -1L));
		ops.remove(100, 6);// reomve first 100 elements which equals 6
		System.out.println("reomve first 100 elements which equals 6 后： elements：" + ops.range(0L, -1L));
		System.out.println("get 3：" + ops.index(3));
		ops.set(3, 55);
		System.out.println("get 3：" + ops.index(3));
		ops.trim(0, 6);
		System.out.println("trim 0,6 后：elements：" + ops.range(0L, -1L));

		redisTemplate.opsForList().rightPopAndLeftPush("testList", "testList");// 自旋
		System.out.println("rotation 后：elements：" + ops.range(0L, -1L));
		redisTemplate.opsForList().rightPopAndLeftPush("testList", "testList2");
		System.out.println("shiftLastToZFirst 后：ops elements：" + ops.range(0L, -1L));
		System.out.println("shiftLastToZFirst 后：ops2 elements：" + ops2.range(0L, -1L));
	}

	/**
	 * set api 测试
	*/
	@Test
	public void setOperationsTest() {
		System.out.println("-------------------set api 测试-------------------");
		final BoundSetOperations ops = redisTemplate.boundSetOps("testSet");
		final BoundSetOperations ops2 = redisTemplate.boundSetOps("testSet2");
		final BoundSetOperations ops3 = redisTemplate.boundSetOps("testSet3");
		final BoundSetOperations opsR = redisTemplate.boundSetOps("testSetResult");

		redisTemplate.delete(ops.getKey());
		redisTemplate.delete(ops2.getKey());
		redisTemplate.delete(ops3.getKey());
		redisTemplate.delete(opsR.getKey());

		Assert.assertTrue(ops.add(1) == 1L);
		Assert.assertTrue(ops.add(1) != 1L);
		Assert.assertTrue(ops.add(1, 2, 3, 4, 5, 6, 7, 8, 9, 10) == 9L);
		Assert.assertTrue(ops.remove(1) == 1L);
		Assert.assertTrue(ops.remove(2, 3, 4, 5, 6, 7, 8, 9) == 8L);
		ops.add(1, 2, 3, 4, 5, 6, 7, 8, 9, 9, 10);

		System.out.println("size：" + ops.size());
		Assert.assertTrue(ops.isMember(1));
		Assert.assertTrue(!ops.isMember(11));

		System.out.println("members：" + ops.members());
		System.out.println("randomMember：" + ops.randomMember());
		System.out.println("randomMembers：" + ops.randomMembers(5));
		System.out.println("randomDistinctMembers：" + ops.distinctRandomMembers(5));
		System.out.println("pop：" + ops.pop());
		System.out.println("members：" + ops.members());

		redisTemplate.delete(ops.getKey());
		redisTemplate.delete(ops2.getKey());
		ops.add(1, 2, 3, 4, 5, 6, 7, 8, 9, 9, 10);
		ops2.add(11, 12, 13, 14, 15, 16, 17, 18, 19, 20);
		Assert.assertTrue(ops.move("testSet2", 2));
		System.out.println();
		System.out.println("shiftTo ops2后 ops members：" + ops.members());
		System.out.println("shiftTo ops2后 ops2 members：" + ops2.members());

		Assert.assertTrue(!ops2.move("testSet", 23));
		System.out.println();
		System.out.println("shiftFrom ops2后 ops members：" + ops.members());
		System.out.println("shiftFrom ops2后 ops2 members：" + ops2.members());

		Assert.assertTrue(ops2.move("testSet", 12));
		System.out.println();
		System.out.println("shiftFrom ops2后 ops members：" + ops.members());
		System.out.println("shiftFrom ops2后 ops2 members：" + ops2.members());

		redisTemplate.delete(ops.getKey());
		redisTemplate.delete(ops2.getKey());
		redisTemplate.delete(ops3.getKey());
		redisTemplate.delete(opsR.getKey());
		ops.add(1, 2, 3, 4);
		ops2.add(1, 2, 5, 6);
		ops3.add(0, 1, 3, 4, 34);
		System.out.println();
		System.out.println("ops members：" + ops.members());
		System.out.println("ops2 members：" + ops2.members());
		System.out.println("ops3 members：" + ops3.members());
		Set expectResult = new HashSet();
		expectResult.add(3);
		expectResult.add(4);////// 差集结果：3,4
		Set result = ops.diff(ops2.getKey());
		System.out.println("difference：" + result);
		Assert.assertTrue(expectResult.equals(result));
		ops.diffAndStore(ops2.getKey(), opsR.getKey());
		System.out.println("differenceAndStore后 opsR members：" + opsR.members());

		redisTemplate.delete(opsR.getKey());
		expectResult.clear();////// 差集结果：空
		result = ops.diff(Arrays.asList(ops2.getKey(), ops3.getKey()));
		System.out.println("differenceAll：" + result);
		Assert.assertTrue(expectResult.equals(result));
		ops.diffAndStore(Arrays.asList(ops2.getKey(), ops3.getKey()), opsR.getKey());
		System.out.println("differenceAllAndStore后 opsR members：" + opsR.members());

		redisTemplate.delete(ops.getKey());
		redisTemplate.delete(ops2.getKey());
		redisTemplate.delete(ops3.getKey());
		redisTemplate.delete(opsR.getKey());
		ops.add(1, 2, 3, 4);
		ops2.add(1, 2, 5, 6);
		ops3.add(0, 1, 3, 4, 34);
		expectResult.clear();
		expectResult.add(1);
		expectResult.add(2);
		////// 交集结果：1 2
		System.out.println();
		result = ops.intersect(ops2.getKey());
		System.out.println("intersect：" + result);
		Assert.assertTrue(expectResult.equals(result));
		ops.intersectAndStore(ops2.getKey(), opsR.getKey());
		System.out.println("intersectAndStore后 opsR members：" + opsR.members());

		redisTemplate.delete(opsR.getKey());
		expectResult.clear();
		expectResult.add(1);
		////// 交集结果：1
		System.out.println();
		result = ops.intersect(Arrays.asList(ops2.getKey(), ops3.getKey()));
		System.out.println("intersectAll：" + result);
		Assert.assertTrue(expectResult.equals(result));
		ops.intersectAndStore(Arrays.asList(ops2.getKey(), ops3.getKey()), opsR.getKey());
		System.out.println("intersectAllAndStore后 opsR members：" + opsR.members());

		redisTemplate.delete(ops.getKey());
		redisTemplate.delete(ops2.getKey());
		redisTemplate.delete(ops3.getKey());
		redisTemplate.delete(opsR.getKey());
		ops.add(1, 2, 3, 4);
		ops2.add(1, 2, 5, 6);
		ops3.add(0, 1, 3, 4, 34);
		expectResult.clear();
		expectResult.addAll(ops.members());
		expectResult.addAll(ops2.members());
		System.out.println(expectResult);
		////// 并集结果：1, 2, 3, 4, 5, 6
		result = ops.union(ops2.getKey());
		System.out.println();
		System.out.println("union：" + result);
		Assert.assertTrue(expectResult.equals(result));
		ops.unionAndStore(ops2.getKey(), opsR.getKey());
		System.out.println("unionAndStore后 opsR members：" + opsR.members());

		redisTemplate.delete(opsR.getKey());
		expectResult.clear();
		expectResult.addAll(ops.members());
		expectResult.addAll(ops2.members());
		expectResult.addAll(ops3.members());
		////// 并集结果：0, 1, 2, 3, 4, 5, 6, 34
		result = ops.union(Arrays.asList(ops2.getKey(), ops3.getKey()));
		System.out.println("unionAll：" + result);
		Assert.assertTrue(expectResult.equals(result));
		ops.unionAndStore(Arrays.asList(ops2.getKey(), ops3.getKey()), opsR.getKey());
		System.out.println("unionAllAndStore后 opsR members：" + opsR.members());

		redisTemplate.delete(ops.getKey());
		redisTemplate.delete(ops2.getKey());
		redisTemplate.delete(ops3.getKey());
		redisTemplate.delete(opsR.getKey());
	}

	/**
	 * zset api 测试
	 */
	@Test
	public void zSetOperationsTest() {
		System.out.println("-------------------zset api 测试-------------------");
		final BoundZSetOperations ops = redisTemplate.boundZSetOps("testZSet");
		final BoundZSetOperations ops2 = redisTemplate.boundZSetOps("testZSet2");
		final BoundZSetOperations ops3 = redisTemplate.boundZSetOps("testZSet3");
		final BoundZSetOperations opsR = redisTemplate.boundZSetOps("testZSetResult");
		redisTemplate.delete(ops.getKey());
		redisTemplate.delete(ops2.getKey());
		redisTemplate.delete(ops3.getKey());
		redisTemplate.delete(opsR.getKey());

		ops.add("SvenAugustus", 0);
		ops.add("Jack", 44);

		Set s0 = new HashSet();
		s0.add(new DefaultTypedTuple("SvenAugustus", 99.0));
		s0.add(new DefaultTypedTuple("Bob", 2.5));
		s0.add(new DefaultTypedTuple("Li haiXia", 999.0));
		ops.add(s0);
		System.out.println(ops.range(0L, -1L));// members
		System.out.println(ops.rangeWithScores(0L, -1L));
		Assert.assertTrue(ops.rank("SvenAugustus") != null);
		Assert.assertTrue(ops.rank("SvenAugustus2") == null);
		System.out.println(ops.rank("SvenAugustus"));
		Assert.assertTrue(ops.rank("SvenAugustus") == 2L);
		Assert.assertTrue(ops.rank("SvenAugustus2") == null);
		Assert.assertTrue(ops.reverseRank("Li haiXia") == 0L);
		Assert.assertTrue(ops.score("SvenAugustus") == 99);
		Assert.assertTrue(ops.score("SvenAugustus2") == null);
		Assert.assertTrue(ops.count(0, 99) == 3L);

		System.out.println(ops.range(0, 1));
		System.out.println(ops.rangeByScore(2.0, 45));
		System.out.println(redisTemplate.opsForZSet().rangeByScore(ops.getKey(), 2.0, 45, 0, 1));
		System.out.println(redisTemplate.opsForZSet().rangeByScore(ops.getKey(), 2.0, 45, 1, 1));
		System.out.println(ops.rangeWithScores(0, 1));
		System.out.println(ops.rangeByScoreWithScores(2.0, 45));
		Set s = redisTemplate.opsForZSet().rangeByScoreWithScores(ops.getKey(), 2.0, 45, 0, 1);
		System.out.println(s);
		Assert.assertTrue(s.size() == 1L && "Bob".equals(((DefaultTypedTuple) s.toArray()[0]).getValue()));
		s = redisTemplate.opsForZSet().rangeByScoreWithScores(ops.getKey(), 2.0, 45, 1, 1);
		System.out.println(s);
		Assert.assertTrue(s.size() == 1L && "Jack".equals(((DefaultTypedTuple) s.toArray()[0]).getValue()));

		System.out.println(ops.reverseRange(0, 1));
		System.out.println(ops.reverseRangeByScore(2.0, 45));
		System.out.println(redisTemplate.opsForZSet().reverseRangeByScore(ops.getKey(), 2.0, 45, 0, 1));
		System.out.println(redisTemplate.opsForZSet().reverseRangeByScore(ops.getKey(), 2.0, 45, 1, 1));
		System.out.println(ops.reverseRangeWithScores(0, 1));
		System.out.println(ops.reverseRangeByScoreWithScores(2.0, 45));
		s = redisTemplate.opsForZSet().reverseRangeByScoreWithScores(ops.getKey(), 2.0, 45, 0, 1);
		System.out.println(s);
		Assert.assertTrue(s.size() == 1L && "Jack".equals(((DefaultTypedTuple) s.toArray()[0]).getValue()));
		s = redisTemplate.opsForZSet().reverseRangeByScoreWithScores(ops.getKey(), 2.0, 45, 1, 1);
		System.out.println(s);
		Assert.assertTrue(s.size() == 1L && "Bob".equals(((DefaultTypedTuple) s.toArray()[0]).getValue()));

		ops.add("Lily", 888);
		System.out.println("remove前：" + ops.range(0L, -1L));// members
		ops.remove("Lily");
		System.out.println("remove前：" + ops.range(0L, -1L));// members
		ops.add("Lily", 888);
		ops.incrementScore("Lily", 1);
		Assert.assertTrue(ops.score("Lily") == 889L);

		ops.add("Geo", 66);
		System.out.println("removeAll前：" + ops.range(0L, -1L));// members
		ops.remove("Lily", "Geo");
		System.out.println("removeAll后：" + ops.range(0L, -1L));// members

		ops.add("Geo", 66);
		ops.add("Lily", 888);
		System.out.println("rank从小到大：" + ops.range(0L, -1L));// members
		ops.removeRange(0, 1);
		System.out.println("删除后 rank从小到大：" + ops.range(0, 1000));

		System.out.println("score从小到大：" + ops.rangeWithScores(0L, -1L));
		ops.removeRangeByScore(0, 100);
		System.out.println("score从小到大：" + ops.rangeWithScores(0L, -1L));

		try {
			redisTemplate.delete(ops.getKey());
			redisTemplate.delete(ops2.getKey());
			redisTemplate.delete(ops3.getKey());
			redisTemplate.delete(opsR.getKey());
			Set expectResult = new HashSet();

			Set s1 = new HashSet();
			s1.add(new DefaultTypedTuple(1, 99.0));
			s1.add(new DefaultTypedTuple(2, 2.5));
			s1.add(new DefaultTypedTuple(3, 56.0));
			s1.add(new DefaultTypedTuple(4, 999.0));
			ops.add(s1);

			s1.clear();
			s1.add(new DefaultTypedTuple(1, 55.0));
			s1.add(new DefaultTypedTuple(2, 2.8));
			s1.add(new DefaultTypedTuple(5, 88.0));
			s1.add(new DefaultTypedTuple(6, 8.9));
			ops2.add(s1);

			s1.clear();
			s1.add(new DefaultTypedTuple(0, 0.0));
			s1.add(new DefaultTypedTuple(1, 7.0));
			s1.add(new DefaultTypedTuple(3, 6.23));
			s1.add(new DefaultTypedTuple(4, 33.9));
			s1.add(new DefaultTypedTuple(34, 880.9));
			ops3.add(s1);

			expectResult.clear();
			expectResult.add(1);
			expectResult.add(2);
			////// 交集结果：1 2
			System.out.println();
			ops.intersectAndStore(ops2.getKey(), opsR.getKey());
			/**
			 * 交集运算，元素的score会被累加
			 */
			System.out.println("membersWithScores：" + opsR.rangeWithScores(0L, -1L));
			Set result = opsR.range(0L, -1L);// members
			System.out.println("intersectAndStore后 opsR members：" + result);
			Assert.assertTrue(expectResult.equals(result));

			redisTemplate.delete(opsR.getKey());
			expectResult.clear();
			expectResult.add(1);
			////// 交集结果：1
			ops.intersectAndStore(Arrays.asList(ops2.getKey(), ops3.getKey()), opsR.getKey());
			result = opsR.range(0L, -1L);// members
			System.out.println("intersectAllAndStore后 opsR members：" + result);
			Assert.assertTrue(expectResult.equals(result));

			redisTemplate.delete(ops.getKey());
			redisTemplate.delete(ops2.getKey());
			redisTemplate.delete(ops3.getKey());
			redisTemplate.delete(opsR.getKey());

			s1.clear();
			s1.add(new DefaultTypedTuple(1, 99.0));
			s1.add(new DefaultTypedTuple(2, 2.5));
			s1.add(new DefaultTypedTuple(3, 56.0));
			s1.add(new DefaultTypedTuple(4, 999.0));
			ops.add(s1);

			s1.clear();
			s1.add(new DefaultTypedTuple(1, 55.0));
			s1.add(new DefaultTypedTuple(2, 2.8));
			s1.add(new DefaultTypedTuple(5, 88.0));
			s1.add(new DefaultTypedTuple(6, 8.9));
			ops2.add(s1);

			s1.clear();
			s1.add(new DefaultTypedTuple(0, 0.0));
			s1.add(new DefaultTypedTuple(1, 7.0));
			s1.add(new DefaultTypedTuple(3, 6.23));
			s1.add(new DefaultTypedTuple(4, 33.9));
			s1.add(new DefaultTypedTuple(34, 880.9));
			ops3.add(s1);

			expectResult.clear();
			expectResult.addAll(ops.range(0L, -1L));// members
			expectResult.addAll(ops2.range(0L, -1L));// members
			System.out.println(expectResult);
			////// 并集结果：1, 2, 3, 4, 5, 6
			ops.unionAndStore(ops2.getKey(), opsR.getKey());
			result = opsR.range(0L, -1L);// members
			System.out.println("unionAndStore后 opsR members：" + result);
			Assert.assertTrue(expectResult.equals(result));
			System.out.println();

			redisTemplate.delete(opsR.getKey());
			expectResult.clear();
			expectResult.addAll(ops.range(0L, -1L));// members
			expectResult.addAll(ops2.range(0L, -1L));// members
			expectResult.addAll(ops3.range(0L, -1L));// members
			////// 并集结果：0, 1, 2, 3, 4, 5, 6, 34
			ops.unionAndStore(Arrays.asList(ops2.getKey(), ops3.getKey()), opsR.getKey());
			System.out.println("unionAllAndStore后 opsR members：" + result);
			result = opsR.range(0L, -1L);// members
			Assert.assertTrue(expectResult.equals(result));
		} catch (InvalidDataAccessApiUsageException e) {
			e.printStackTrace();
		}
		redisTemplate.delete(ops.getKey());
		redisTemplate.delete(ops2.getKey());
		redisTemplate.delete(ops3.getKey());
		redisTemplate.delete(opsR.getKey());
	}

	/**
	 * hyperLogLog api 测试
	 */
	@Test
	public void hyperLogLogOperationsTest() {
		System.out.println("-------------------hyperLogLog api 测试-------------------");
		redisTemplate.opsForHyperLogLog().delete("testHyperLogLog");
		redisTemplate.opsForHyperLogLog().delete("testHyperLogLog2");

		for (int i = 0; i < 30; i++) {/// 30
			redisTemplate.opsForHyperLogLog().add("testHyperLogLog", i);
		}
		for (int i = 0; i < 10; i++) {/// 10
			redisTemplate.opsForHyperLogLog().add("testHyperLogLog", i);
		}
		long result = redisTemplate.opsForHyperLogLog().size("testHyperLogLog");
		System.out.println("ops基数统计估计值：" + result);
		Assert.assertTrue(result == 30);

		for (int i = 0; i < 300; i++) {/// 300
			redisTemplate.opsForHyperLogLog().add("testHyperLogLog", i);
		}
		for (int i = 0; i < 10; i++) {/// 10
			redisTemplate.opsForHyperLogLog().add("testHyperLogLog", i);
		}
		result = redisTemplate.opsForHyperLogLog().size("testHyperLogLog");
		System.out.println("ops基数统计估计值：" + result);
		Assert.assertTrue(result >= 290 && result <= 310);

		for (int i = 0; i < 30000; i++) {/// 3w
			redisTemplate.opsForHyperLogLog().add("testHyperLogLog", i);
		}
		for (int i = 0; i < 1000; i++) {/// 1k
			redisTemplate.opsForHyperLogLog().add("testHyperLogLog", i);
		}
		result = redisTemplate.opsForHyperLogLog().size("testHyperLogLog");
		System.out.println("ops基数统计估计值：" + result);
		Assert.assertTrue(result >= 29500 && result <= 30500);

		for (int i = 41000; i < 42000; i++) {/// 1k
			redisTemplate.opsForHyperLogLog().add("testHyperLogLog2", i);
		}
		System.out.println("ops2基数统计估计值：" + redisTemplate.opsForHyperLogLog().size("testHyperLogLog2"));
		redisTemplate.opsForHyperLogLog().union("testHyperLogLog", "testHyperLogLog2");

		result = redisTemplate.opsForHyperLogLog().size("testHyperLogLog");
		System.out.println("ops基数统计估计值：" + result);
		Assert.assertTrue(result >= 30500 && result <= 31500);

		redisTemplate.opsForHyperLogLog().delete("testHyperLogLog");
		redisTemplate.opsForHyperLogLog().delete("testHyperLogLog2");
	}

	/**
	 * 脚本测试
	 * 
	 * lua脚本仅可在单机redis执行，暂不支持集群环境redis
	 * 
	 * @throws Exception
	 */
	@Test
	public void luaScriptTest() throws Exception {
		System.out.println("-------------------脚本测试-------------------");
		String scriptLocation = "classpath:demo/redis/lua/decreaseInventory.lua";
		List keys = Collections.singletonList("test3");
		Object[] args = new Object[]{};
		/**
		 * 初始化库存
		 */
		redisTemplate.opsForValue().set("test3", 100);
		/**
		 * 模拟减库存
		 */
		try {
			DefaultRedisScript script = new DefaultRedisScript();
			script.setLocation(new ClassPathResource(scriptLocation.substring("classpath:".length())));
			script.setResultType(Boolean.class);

			Object result = redisTemplate.execute(script, keys, args);
			System.out.println("脚本结果：" + result);
		} catch (InvalidDataAccessApiUsageException e) {
			e.printStackTrace();
		}
	}

	/**
	 * lua脚本测试，结果为复杂类型
	 * 
	 * @throws Exception
	 */
	@Test
	public void luaScriptTest2() throws Exception {
		System.out.println("-------------------脚本测试，结果为复杂类型-------------------");

		String scriptLocation = "classpath:demo/redis/lua/updateAvailableInventory.lua";
		Class resultType = ResponseResult.class;
		List keys = Collections.singletonList("test4");
		Object[] args = new Object[]{1};

		/**
		 * 初始化库存
		 */
		redisTemplate.opsForValue().set("test4", 100);
		/**
		 * 模拟减库存
		 */
		try {
			DefaultRedisScript script = new DefaultRedisScript();
			script.setLocation(new ClassPathResource(scriptLocation.substring("classpath:".length())));
			script.setResultType(resultType);

			Object result = redisTemplate.execute(script, keys, args);
			System.out.println("脚本结果：" + result);
		} catch (InvalidDataAccessApiUsageException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 事务测试
	 * 
	 * 
	 * @throws Exception
	 */
	@Test
	public void transactionalTest() throws Exception {
		System.out.println("-------------------事务测试-------------------");
		redisTemplate.boundValueOps("name").set("SvenAugustus");
		redisTemplate.boundValueOps("age").set(18);

		List<Object> txResults = null;
		/**
		 * 自定义异常（发生在 EXEC 执行之前的错误）
		 * 
		 * 对于发生在 EXEC 执行之前的错误， 客户端以前的做法是检查命令入队所得的返回值：如果命令入队时返回 QUEUED ，那么入队成功；
		 * 否则，就是入队失败。如果有命令在入队时失败，那么大部分客户端都会停止并取消这个事务。
		 */
		try {
			txResults = (List<Object>) redisTemplate.execute(new SessionCallback() {

				@Override
				public Object execute(RedisOperations redisoperations) throws DataAccessException {
					redisoperations.multi();
					redisoperations.boundValueOps("name").set("SvenAugustus2");
					// 异常代码
					for (int i = 0; i < 5; i++) {
						if (i == 3) {
							throw new RedisSystemException("exception", new Exception("sssss"));
						}
					}
					redisoperations.boundValueOps("age").set(19);
					return redisoperations.exec();
				}

			});
		} catch (Exception e) {
			// e.printStackTrace();
		} finally {
			System.out.println("txResults: " + txResults);
			System.out.println("name：" + redisTemplate.boundValueOps("name").get());
			System.out.println("age：" + redisTemplate.boundValueOps("age").get());
		}
		/**
		 * 事务执行异常(命令可能在 EXEC 调用之后失败。)
		 * 
		 * 至于那些在 EXEC 命令执行之后所产生的错误， 并没有对它们进行特别处理： 即使事务中有某个/某些命令在执行时产生了错误，
		 * 事务中的其他命令仍然会继续执行。
		 */
		try {
			txResults = (List<Object>) redisTemplate.execute(new SessionCallback() {

				@Override
				public Object execute(RedisOperations redisoperations) throws DataAccessException {
					redisoperations.multi();
					redisoperations.boundValueOps("name").set("SvenAugustus1");
					redisoperations.boundZSetOps("age").size();
					/**
					 * 为什么 Redis 不支持回滚（roll back） 如果你有使用关系式数据库的经验， 那么 “Redis
					 * 在事务失败时不进行回滚，而是继续执行余下的命令”这种做法可能会让你觉得有点奇怪。 以下是这种做法的优点：
					 * Redis 命令只会因为错误的语法而失败（并且这些问题不能在入队时发现），或是命令用在了错误类型的键上面：
					 * 这也就是说，从实用性的角度来说，失败的命令是由编程错误造成的，而这些错误应该在开发的过程中被发现，而不应该出现在生产环境中。
					 * 因为不需要对回滚进行支持，所以 Redis 的内部可以保持简单且快速。
					 * 
					 * 有种观点认为 Redis 处理事务的做法会产生 bug ， 然而需要注意的是， 在通常情况下，
					 * 回滚并不能解决编程错误带来的问题。 举个例子， 如果你本来想通过 INCR 命令将键的值加上 1 ，
					 * 却不小心加上了 2 ， 又或者对错误类型的键执行了 INCR ， 回滚是没有办法处理这些情况的。
					 */
					return redisoperations.exec();
				}

			});
		} catch (Exception e) {
			// e.printStackTrace();
		} finally {
			System.out.println("txResults: " + txResults);
			System.out.println("name：" + redisTemplate.boundValueOps("name").get());
		}
		/**
		 * 批量set
		 */
		txResults = (List<Object>) redisTemplate.execute(new SessionCallback() {

			@Override
			public Object execute(RedisOperations redisoperations) throws DataAccessException {
				redisoperations.multi();
				redisoperations.boundValueOps("name").set("SvenAugustus3");
				redisoperations.boundValueOps("age").set(88);
				return redisoperations.exec();
			}

		});
		System.out.println("txResults: " + txResults);
		/**
		 * get set
		 */
		txResults = (List<Object>) redisTemplate.execute(new SessionCallback() {

			@Override
			public Object execute(RedisOperations redisoperations) throws DataAccessException {
				redisoperations.multi();
				redisoperations.boundValueOps("name").get();
				redisoperations.boundValueOps("age").set(20);
				redisoperations.boundValueOps("age").get();
				return redisoperations.exec();
			}

		});
		System.out.println("txResults: " + txResults);
		/**
		 * watch get set
		 */
		txResults = (List<Object>) redisTemplate.execute(new SessionCallback() {

			@Override
			public Object execute(RedisOperations redisoperations) throws DataAccessException {
				redisoperations.watch("name");
				redisoperations.multi();
				redisoperations.boundValueOps("name").set("Watch your value");
				redisoperations.boundValueOps("name").get();
				return redisoperations.exec();
			}

		});
		System.out.println("txResults: " + txResults);
	}

}
