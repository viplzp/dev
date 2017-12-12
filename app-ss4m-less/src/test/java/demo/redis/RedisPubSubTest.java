package demo.redis;

import java.io.Serializable;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import io.flysium.framework.cache.SpringRedisPubSubConfig;

/**
 * Redis发布/订阅测试</br>
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 1.0
 */
public class RedisPubSubTest extends BaseJunitForSpringRedis {

	@Autowired
	private RedisTemplate redisTemplate;

	private String channelName = "sven";

	public class RedisReceiver {

		// pass the channel/pattern as well
		public void handleMessage(Serializable message, String channel) {
			System.out.println("handleMessage：channel=" + channel + "，body=" + message);
		}

	}

	/**
	 * Redis自身的pub sub消息机制只是作为一个不可靠的消息通知</br>
	 * 
	 * Redis中"pub/sub"的消息,为"即发即失",server不会保存消息,</br>
	 * 如果publish的消息,没有任何client处于"subscribe"状态,消息将会被丢弃.</br>
	 * 如果client在subcribe时,链接断开后重连,那么此期间的消息也将丢失.</br>
	 * Redis server将会"尽力"将消息发送给处于subscribe状态的client,但是仍不会保证每条消息都能被正确接收.</br>
	 * 
	 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
	 * @version 2017年4月7日
	 */
	@Before
	public void beforeTest() {
		RedisMessageListenerContainer rmlc = (RedisMessageListenerContainer) SpringRedisPubSubConfig
				.getRedisMessageListenerContainer();
		/*
		 * rmlc.addMessageListener(new MessageListenerAdapter() {
		 * 
		 * @Override public void onMessage(Message message, byte[] pattern) {
		 * System.out.println("message received:channel=" + message.getChannel()
		 * + "，body=" + message.toString()); } }, new ChannelTopic("sven"));
		 */
		MessageListenerAdapter listenerAdapter = new MessageListenerAdapter(new RedisReceiver(), // 指定Delegate
				"handleMessage"// 指定Delegate执行方法
		);
		listenerAdapter.afterPropertiesSet();
		rmlc.addMessageListener(listenerAdapter, new ChannelTopic(channelName));
	}

	@Test
	public void test() throws InterruptedException {
		System.out.println("-------------------发布/订阅测试-------------------");

		Thread t1 = new Thread(new Runnable() {

			@Override
			public void run() {
				RedisMessageListenerContainer rmlc = (RedisMessageListenerContainer) SpringRedisPubSubConfig
						.getRedisMessageListenerContainer();
				while (true) {
					if (!rmlc.isRunning()) {
						break;
						// System.out.println("RedisMessageListenerContainer is
						// running..");
					}
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});

		Thread t2 = new Thread(new Runnable() {

			@Override
			public void run() {

				for (int i = 0; i < 100; i++) {
					redisTemplate.convertAndSend(channelName, "呵呵哈哈哈-" + i);
					System.out.println("Publish message " + i);
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}
		});

		t1.start();
		t2.start();

		t1.join();
		t2.join();
	}

}
