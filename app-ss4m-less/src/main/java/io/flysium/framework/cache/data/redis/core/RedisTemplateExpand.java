package io.flysium.framework.cache.data.redis.core;

import org.springframework.data.redis.core.RedisTemplate;

/**
 * Redis 操作模板扩展
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 1.0
 */
public class RedisTemplateExpand<K, V> extends RedisTemplate<K, V> {

	private String defaultNamespace;

	/**
	 * 构造器
	 */
	public RedisTemplateExpand() {
		super();
	}

	/**
	 * 构造器
	 * 
	 * @param defaultNamespace
	 */
	public RedisTemplateExpand(String defaultNamespace) {
		super();
		this.defaultNamespace = defaultNamespace;
	}

	public String getDefaultNamespace() {
		return defaultNamespace;
	}

	public void setDefaultNamespace(String defaultNamespace) {
		this.defaultNamespace = defaultNamespace;
	}

}
