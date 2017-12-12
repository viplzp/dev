package io.flysium.framework.cache.data.redis.serializer;

import java.nio.charset.Charset;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;

import io.flysium.framework.Consts;

/**
 * fastjson-string序列化工具
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 1.0
 */
public class FastJsonStringRedisSeriaziler<T> implements RedisSerializer {

	public static final String EMPTY_JSON = "{}";

	private final Charset charset;
	private Class<T> clazz;

	/**
	 * 构造器
	 */
	public FastJsonStringRedisSeriaziler() {
		this(Charset.forName(Consts.CHARSET.UTF_8), Object.class);
	}

	/**
	 * 构造器
	 * 
	 * @param charset
	 */
	public FastJsonStringRedisSeriaziler(Charset charset) {
		this(charset, Object.class);
	}

	/**
	 * 构造器
	 * 
	 * @param charset
	 * @param clazz
	 */
	public FastJsonStringRedisSeriaziler(Charset charset, Class clazz) {
		Assert.notNull(charset, "charset must not be null!");
		this.charset = charset;
		this.clazz = clazz;
		this.init();
	}

	private void init() {
		/**
		 * fastjson升级之后报错autotype is not support
		 * 安全升级包禁用了部分autotype的功能，也就是"@type"这种指定类型的功能会被限制在一定范围内使用。
		 * 如果你使用场景中包括了这个功能，https://github.com/alibaba/fastjson/wiki/enable_autotype
		 * 这里有一个介绍如何添加白名单或者打开autotype功能。
		 * 
		 * ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
		 */
		/** autotype功能白名单 */
		ParserConfig.getGlobalInstance().addAccept("io.flysium.framework.message.");
		ParserConfig.getGlobalInstance().addAccept("io.flysium.framework.vo.");
	}

	@Override
	public T deserialize(byte[] bytes) {
		if (bytes == null || bytes.length <= 0) {
			return null;
		}
		String str = new String(bytes, charset);

		return JSON.parseObject(str, clazz);
	}

	@Override
	public byte[] serialize(Object object) {
		if (object == null) {
			return new byte[0];
		}

		return JSON.toJSONString(object, SerializerFeature.WriteClassName).getBytes(charset);
	}

}
