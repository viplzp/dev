package io.flysium.framework.cache.data.redis.serializer;

import java.nio.charset.Charset;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import io.flysium.framework.cache.data.redis.core.RedisTemplateExpand;

/**
 * 键命名空间-string序列化工具
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 1.0
 */
public class KeyNamespaceStringRedisSerializer extends StringRedisSerializer {

	private final RedisTemplateExpand redisTemplate;

	/**
	 * 构造器
	 * 
	 * @param redisTemplate
	 * @param forName
	 */
	public KeyNamespaceStringRedisSerializer(RedisTemplateExpand redisTemplate, Charset forName) {
		super(forName);
		this.redisTemplate = redisTemplate;
	}

	@Override
	public byte[] serialize(String string) {
		String namespace = redisTemplate.getDefaultNamespace();
		/**
		 * 默认为键定义加上命名空间 namespace:
		 */
		if (StringUtils.isNotEmpty(namespace)) {
			String key = new StringBuilder().append(namespace).append(":").append(string).toString();
			return super.serialize(key);
		}
		return super.serialize(string);
	}

}
