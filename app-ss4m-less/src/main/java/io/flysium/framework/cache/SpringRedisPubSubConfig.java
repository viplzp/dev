package io.flysium.framework.cache;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import io.flysium.framework.util.SpringContextUtils;

/**
 * Redis发布/订阅配置
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 1.0
 */
@Configuration
public class SpringRedisPubSubConfig {

	/**
	 * Spring Data Redis 的连接工厂配置，必选
	 */
	/**
	 * @Bean(name = "connectionFactory") public RedisConnectionFactory
	 *            connectionFactory() { return
	 *            CacheFactory.getInst().getSessionRedisConnectionFactory(); }
	 */

	/**
	 * Redis消费者容器
	 * 
	 * @param connectionFactory
	 * @return
	 */
	@Bean
	public RedisMessageListenerContainer redisMessageListenerContainer(
			@Qualifier("connectionFactory") RedisConnectionFactory connectionFactory) {
		RedisMessageListenerContainer container = SpringContextUtils.getBean(RedisMessageListenerContainer.class);
		if (container == null) {
			container = new RedisMessageListenerContainer();
			container.setConnectionFactory(connectionFactory);
		}
		return container;
	}

	/**
	 * 获取缓存配置
	 * 
	 * @return
	 */
	public static SpringRedisPubSubConfig getInst() {
		return SpringContextUtils.getBean(SpringRedisPubSubConfig.class);
	}

	/**
	 * 获取Redis消费者容器
	 * 
	 * @return
	 */
	public static RedisMessageListenerContainer getRedisMessageListenerContainer() {
		return SpringContextUtils.getBean(RedisMessageListenerContainer.class);
	}

}
