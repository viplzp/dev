package io.flysium.framework.session;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.RedisFlushMode;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

import io.flysium.framework.Consts;
import io.flysium.framework.app.AppConfig;
import io.flysium.framework.cache.CacheConfig;
import io.flysium.framework.cache.data.redis.serializer.FastJsonStringRedisSeriaziler;

/**
 * Spring Session分布式会话解决方案
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 1.0
 */
@Configuration
@EnableScheduling
public class SpringSessionConfig extends RedisHttpSessionConfiguration {

	private String getApplicationName() {
		return AppConfig.getInst().getApplicationName();
	}

	private int getSessionCacheTimeout() {
		return CacheConfig.getInst().getSessionCacheTimeout(TimeUnit.SECONDS);
	}

	/**
	 * Spring Data Redis 的连接工厂配置，必选
	 */
	@Bean(name = "connectionFactory")
	public RedisConnectionFactory connectionFactory() {
		return CacheConfig.getRedisConnectionFactory();
	}

	/**
	 * Redis session操作模板
	 */
	@Override
	@Bean(name = "sessionRedisTemplate")
	public RedisTemplate sessionRedisTemplate(
			@Qualifier("connectionFactory") RedisConnectionFactory connectionFactory) {
		return super.sessionRedisTemplate(connectionFactory);
	}

	/**
	 * Spring Data Redis 的会话存储仓库配置，可选
	 */
	@Override
	@Bean(name = "sessionRepository")
	public RedisOperationsSessionRepository sessionRepository(RedisOperations<Object, Object> sessionRedisTemplate,
			ApplicationEventPublisher applicationEventPublisher) {
		this.setMaxInactiveIntervalInSeconds(getSessionCacheTimeout()); // 单位：秒
		this.setRedisNamespace(getApplicationName());
		this.setRedisFlushMode(RedisFlushMode.ON_SAVE);
		return super.sessionRepository(sessionRedisTemplate, applicationEventPublisher);
	}

	/**
	 * Spring Data Redis 的默认序列化工具，可选
	 */
	@Bean(name = "springSessionDefaultRedisSerializer")
	public RedisSerializer springSessionDefaultRedisSerializer() {
		return new FastJsonStringRedisSeriaziler(Charset.forName(Consts.CHARSET.UTF_8));
	}

	/**
	 * Session会话策略配置，可选
	 * 
	 * 1、Spring Session 默认支持Cookie存储当前session的id， 即CookieHttpSessionStrategy。
	 * 2、Spring Session 支持RESTFul APIS，响应头回返回x-auth-token，来标识当前session的token，
	 * 即HeaderHttpSessionStrategy。
	 */
	/**
	 * @Bean(name = "httpSessionStrategy") public HttpSessionStrategy
	 *            httpSessionStrategy() { HeaderHttpSessionStrategy
	 *            headerHttpSessionStrategy = new HeaderHttpSessionStrategy();
	 *            headerHttpSessionStrategy.setHeaderName("X-Auth-Token");
	 *            return headerHttpSessionStrategy; }
	 */

	/**
	 * Session会话策略为 CookieHttpSessionStrategy 情况下配置的 Cookie序列化工具，可选
	 */
	@Bean(name = "cookieSerializer")
	public CookieSerializer cookieSerializer() {
		DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
		// Cookie名称
		cookieSerializer.setCookieName(new StringBuilder(getApplicationName()).append("SESSION").toString());
		// HttpOnly
		cookieSerializer.setUseHttpOnlyCookie(true);
		// HTTPS定义
		cookieSerializer.setUseSecureCookie(true);
		/**
		 * 解决子域问题：把cookiePath的返回值设置为统一的根路径就能让session id从根域获取，
		 * 这样同根下的所有web应用就可以轻松实现单点登录共享session
		 */
		cookieSerializer.setCookiePath("/");
		return cookieSerializer;
	}

}
