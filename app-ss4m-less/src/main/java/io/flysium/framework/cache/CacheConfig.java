package io.flysium.framework.cache;

import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisClusterNode;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import io.flysium.framework.Consts;
import io.flysium.framework.cache.data.redis.core.RedisTemplateExpand;
import io.flysium.framework.cache.data.redis.serializer.FastJsonStringRedisSeriaziler;
import io.flysium.framework.cache.data.redis.serializer.KeyNamespaceStringRedisSerializer;
import io.flysium.framework.util.SpringContextUtils;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 缓存配置
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 1.0
 */
@Configuration
public class CacheConfig {

	@Value("${cache.default.type}")
	private String defaultCacheType;

	@Value("${cache.session.timeout}")
	private Integer sessionCacheTimeout;

	// redis通用配置

	@Value("${redis.client.type}")
	private String redisClientType;

	@Value("${redis.default.namespace}")
	private String redisDefaultNamespace;

	@Value("${redis.password}")
	private String redisPassword;

	@Value("${redis.maxIdle}")
	private Integer redisMaxIdle;

	@Value("${redis.minIdle}")
	private Integer redisMinIdle;

	@Value("${redis.maxTotal}")
	private Integer redisMaxTotal;

	@Value("${redis.maxWaitMillis}")
	private Integer redisMaxWaitMillis;

	@Value("${redis.testOnBorrow}")
	private String redisTestOnBorrow;

	@Value("${redis.testOnReturn}")
	private String redisTestOnReturn;

	// redis单机配置

	@Value("${redis.standalone.hostName}")
	private String redisHostName;

	@Value("${redis.standalone.port}")
	private Integer redisPort;

	@Value("${redis.standalone.useSSL}")
	private String redisUseSSL;

	// redis主从复制配置

	@Value("${redis.sentinel.master}")
	private String redisMaster;

	@Value("${redis.sentinel.sentinelList}")
	private String redisSentinelList;

	// redis集群配置

	@Value("${redis.cluster.serverList}")
	private String redisClusterServerList;

	/**
	 * 默认存取的缓存类型，如 redis.cluster
	 */
	public String getDefaultCacheType() {
		return defaultCacheType;
	}

	/**
	 * Session会话的超时时间，单位为秒，如900表示15分钟超时
	 */
	public int getSessionCacheTimeout(TimeUnit timeUnit) {
		Long timeout = timeUnit.convert(sessionCacheTimeout, TimeUnit.SECONDS);
		return timeout.intValue();
	}

	/**
	 * Redis客户端类型，如jedis、lettuce
	 */
	public String getRedisClientType() {
		return redisClientType;
	}

	/**
	 * Redis缓存连接池配置--jedis
	 */
	@Bean(name = "JedisPoolConfig")
	public JedisPoolConfig jedisPoolConfig() {
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		// 连接耗尽时是否阻塞, false报异常,ture阻塞直到超时, 默认true
		poolConfig.setBlockWhenExhausted(true);
		// 设置的逐出策略类名, 默认DefaultEvictionPolicy(当连接超过最大空闲时间,或连接数超过最大空闲连接数)
		poolConfig.setEvictionPolicyClassName("org.apache.commons.pool2.impl.DefaultEvictionPolicy");
		// 是否启用pool的jmx管理功能, 默认true
		poolConfig.setJmxEnabled(true);
		// JMX命名前缀：MBean ObjectName = new
		// ObjectName("org.apache.commons.pool2:type=GenericObjectPool,name=" +
		// "pool" + i); 默 认为"pool"
		poolConfig.setJmxNamePrefix("jedis-pool");
		// 是否启用后进先出, 默认true
		poolConfig.setLifo(true);
		// 最大空闲连接数, 默认8个
		poolConfig.setMaxIdle(redisMaxIdle);
		// 最小空闲连接数, 默认0个
		poolConfig.setMinIdle(redisMinIdle);
		// 最大连接数, 默认8个
		poolConfig.setMaxTotal(redisMaxTotal);
		// 获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间,
		// 默认-1
		poolConfig.setMaxWaitMillis(redisMaxWaitMillis);
		// 逐出连接的最小空闲时间 默认1800000毫秒(30分钟)
		poolConfig.setMinEvictableIdleTimeMillis(1800000);
		// 每次逐出检查时 逐出的最大数目 如果为负数就是 : 1/abs(n), 默认3
		poolConfig.setNumTestsPerEvictionRun(3);
		// 对象空闲多久后逐出, 当空闲时间>该值 且 空闲连接>最大空闲数
		// 时直接逐出,不再根据MinEvictableIdleTimeMillis判断 (默认逐出策略)
		poolConfig.setSoftMinEvictableIdleTimeMillis(1800000);
		// 在获取连接的时候检查有效性, 默认false
		poolConfig.setTestOnBorrow(Consts.PropertiesValue.PROPERTIEVALUE_TRUE.equals(redisTestOnBorrow));
		// 在return给pool时，是否提前进行validate操作；默认false
		poolConfig.setTestOnReturn(Consts.PropertiesValue.PROPERTIEVALUE_TRUE.equals(redisTestOnReturn));
		// 在空闲时检查有效性, 默认false
		poolConfig.setTestWhileIdle(false);
		// 逐出扫描的时间间隔(毫秒) 如果为负数,则不运行逐出线程, 默认-1
		poolConfig.setTimeBetweenEvictionRunsMillis(-1);
		return poolConfig;
	}

	private RedisSentinelConfiguration getRedisSentinelConfiguration() {
		RedisSentinelConfiguration sentinelConfiguration = new RedisSentinelConfiguration();
		sentinelConfiguration.setMaster(redisMaster);
		if (StringUtils.isEmpty(redisSentinelList)) {
			return sentinelConfiguration;
		}
		// redis主从配置哨兵IP及端口用英文逗号隔开，如：127.0.0.1:26379,127.0.0.1:26380
		String[] nodelist = redisSentinelList.split(",");
		if (nodelist == null) {
			return sentinelConfiguration;
		}
		Set set = new HashSet();
		for (String node : nodelist) {
			String[] ipport = node.split(":");
			if (ipport == null || ipport.length < 2) {
				continue;
			}
			set.add(new RedisClusterNode(ipport[0], Integer.parseInt(ipport[1])));
		}
		sentinelConfiguration.setSentinels(set);
		return sentinelConfiguration;
	}

	private RedisClusterConfiguration getRedisClusterConfiguration() {
		RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
		if (StringUtils.isEmpty(redisClusterServerList)) {
			return redisClusterConfiguration;
		}
		// redis集群配置用英文逗号隔开，如：127.0.0.1:7000,127.0.0.1:7001,127.0.0.1:7002
		String[] nodelist = redisClusterServerList.split(",");
		if (nodelist == null) {
			return redisClusterConfiguration;
		}
		Set set = new HashSet();
		for (String node : nodelist) {
			String[] ipport = node.split(":");
			if (ipport == null || ipport.length < 2) {
				continue;
			}
			set.add(new RedisClusterNode(ipport[0], Integer.parseInt(ipport[1])));
		}
		redisClusterConfiguration.setClusterNodes(set);
		return redisClusterConfiguration;
	}

	private JedisConnectionFactory redisConnectionFactoryForJedis(JedisPoolConfig jedisPoolConfig) {
		JedisConnectionFactory jedisConnectionFactory = null;
		if (Consts.CacheSet.CACHE_TYPE_REDIS_STANDALONE.equals(defaultCacheType)) {// 单机
			jedisConnectionFactory = new JedisConnectionFactory();
			jedisConnectionFactory.setHostName(redisHostName);
			jedisConnectionFactory.setPort(redisPort);
		} else if (Consts.CacheSet.CACHE_TYPE_REDIS_SENTINEL.equals(defaultCacheType)) {// 主从复制
			jedisConnectionFactory = new JedisConnectionFactory(getRedisSentinelConfiguration());
		} else if (Consts.CacheSet.CACHE_TYPE_REDIS_CLUSTER.equals(defaultCacheType)) {// 集群
			jedisConnectionFactory = new JedisConnectionFactory(getRedisClusterConfiguration());
		}
		if (jedisConnectionFactory != null && StringUtils.isNotEmpty(redisPassword)) {
			jedisConnectionFactory.setPassword(redisPassword);
		}
		if (jedisConnectionFactory != null) {
			jedisConnectionFactory.setPoolConfig(jedisPoolConfig);
		}
		return jedisConnectionFactory;
	}

	private LettuceConnectionFactory redisConnectionFactoryForLettuce() {
		LettuceConnectionFactory lettuceConnectionFactory = null;
		if (Consts.CacheSet.CACHE_TYPE_REDIS_STANDALONE.equals(defaultCacheType)) {// 单机
			lettuceConnectionFactory = new LettuceConnectionFactory();
			lettuceConnectionFactory.setHostName(redisHostName);
			lettuceConnectionFactory.setPort(redisPort);
			lettuceConnectionFactory.setUseSsl(Consts.PropertiesValue.PROPERTIEVALUE_TRUE.equals(redisUseSSL));
		} else if (Consts.CacheSet.CACHE_TYPE_REDIS_SENTINEL.equals(defaultCacheType)) { // 主从复制
			lettuceConnectionFactory = new LettuceConnectionFactory(getRedisSentinelConfiguration());
		} else if (Consts.CacheSet.CACHE_TYPE_REDIS_CLUSTER.equals(defaultCacheType)) { // 集群
			lettuceConnectionFactory = new LettuceConnectionFactory(getRedisClusterConfiguration());
		}
		if (lettuceConnectionFactory != null && StringUtils.isNotEmpty(redisPassword)) {
			lettuceConnectionFactory.setPassword(redisPassword);
		}
		return lettuceConnectionFactory;
	}

	/**
	 * Redis缓存连接工厂配置
	 */
	@Bean(name = "redisConnectionFactory")
	public RedisConnectionFactory redisConnectionFactory(
			@Qualifier("JedisPoolConfig") JedisPoolConfig jedisPoolConfig) {
		if (Consts.CacheSet.RedisClient.REDIS_CLIENT_JEDIS.equals(redisClientType)) {
			return redisConnectionFactoryForJedis(jedisPoolConfig);
		} else if (Consts.CacheSet.RedisClient.REDIS_CLIENT_LETTUCE.equals(redisClientType)) {
			return redisConnectionFactoryForLettuce();
		}
		return null;
	}

	/**
	 * Redis缓存连接工厂配置
	 */
	public static RedisConnectionFactory getRedisConnectionFactory() {
		return SpringContextUtils.getBean("redisConnectionFactory");
	}

	/**
	 * Redis操作模板
	 */
	@Bean(name = "redisTemplate")
	public RedisTemplate redisTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplateExpand redisTemplate = new RedisTemplateExpand(redisDefaultNamespace);
		redisTemplate.setConnectionFactory(connectionFactory);
		// 键命名空间-string序列化工具
		RedisSerializer keyNamespaceStringRedisSerializer = new KeyNamespaceStringRedisSerializer(redisTemplate,
				Charset.forName(Consts.CHARSET.UTF_8));
		redisTemplate.setKeySerializer(keyNamespaceStringRedisSerializer);
		RedisSerializer stringRedisSerializer = new StringRedisSerializer(Charset.forName(Consts.CHARSET.UTF_8));
		redisTemplate.setStringSerializer(stringRedisSerializer);
		redisTemplate.setHashKeySerializer(stringRedisSerializer);

		// fastjson-string序列化工具
		RedisSerializer fastJosnRedisSerializer = new FastJsonStringRedisSeriaziler<Object>(
				Charset.forName(Consts.CHARSET.UTF_8));
		redisTemplate.setValueSerializer(fastJosnRedisSerializer);
		redisTemplate.setDefaultSerializer(fastJosnRedisSerializer);
		redisTemplate.setHashKeySerializer(fastJosnRedisSerializer);
		redisTemplate.setHashValueSerializer(fastJosnRedisSerializer);
		return redisTemplate;
	}

	/**
	 * Redis操作模板
	 */
	public static RedisTemplate getRedisTemplate() {
		return SpringContextUtils.getBean("redisTemplate");
	}

	/**
	 * 获取缓存配置
	 * 
	 * @return
	 */
	public static CacheConfig getInst() {
		return SpringContextUtils.getBean(CacheConfig.class);
	}

}
