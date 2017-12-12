package io.flysium.framework;

import io.flysium.framework.message.CodeInfo;

/**
 * 系统常量
 */
public class Consts {

	private Consts() {
	}

	/** 环境参数 */
	public static class JVMName {
		/** 配置文件目录 */
		public static final String VM_CONFIG_PATH = "CONFIG_PATH";

		private JVMName() {
		}
	}

	/** 配置值定义 */
	public static class PropertiesValue {
		public static final String PROPERTIEVALUE_TRUE = "true";
		public static final String PROPERTIEVALUE_FALSE = "false";

		private PropertiesValue() {
		}
	}

	/** 数据源 */
	public static class JNDI {
		/** 主数据库 */
		public static final String DEFAULT_JNDI_NAME = "DEFAULT_JNDI";
		/** 后备数据库（容灾情况，主数据库自动切换到后备数据库） */
		public static final String RESERVE_JNDI_NAME = "RESERVE_JNDI";
		/** 日志数据库 */
		public static final String LOG_JNDI_NAME = "LOG_JNDI";

		private JNDI() {
		}
	}

	/** 数据库类型 */
	public static class DBTYPE {
		/** Oracle */
		public static final String DBTYPE_ORACLE = "oracle";
		/** MySQL */
		public static final String DBTYPE_MYSQL = "mysql";

		private DBTYPE() {
		}
	}

	/** 缓存 */
	public static class CacheSet {

		/** 缓存类型：redis_s 单机 */
		public static final String CACHE_TYPE_REDIS_STANDALONE = "redis.standalone";
		/** 缓存类型：redis_s 主从复制 */
		public static final String CACHE_TYPE_REDIS_SENTINEL = "redis.sentinel";
		/** 缓存类型：Redis 集群 */
		public static final String CACHE_TYPE_REDIS_CLUSTER = "redis.cluster";
		/** 缓存类型：memcached */
		public static final String CACHE_TYPE_MEMCACHED = "memcached";

		private CacheSet() {
		}

		/** redis客户端类型 */
		public static class RedisClient {

			public static final String REDIS_CLIENT_JEDIS = "jedis";
			public static final String REDIS_CLIENT_LETTUCE = "lettuce";

			private RedisClient() {
			}
		}

	}

	/** 编码 */
	public static class CHARSET {

		public static final String UTF_8 = "UTF8";

		private CHARSET() {
		}
	}

	/** 应用编码规范 */
	public static class CodeInfoSet {

		/** 服务调用成功 */
		public static final CodeInfo CODE_00000 = new CodeInfo("00000", "Success", "服务调用成功");
		/** 该请求必须用GET方法 */
		public static final CodeInfo CODE_10001 = new CodeInfo("10001", "该请求必须用GET方法", "request method must be get");
		/** 该请求必须用POST方法 */
		public static final CodeInfo CODE_10002 = new CodeInfo("10002", "该请求必须用POST方法", "request method must be post");
		/** 通用警告，内容自定义 **/
		public static final CodeInfo CODE_11000 = new CodeInfo("11000", "${message}", "message:${message}");
		/** 系统内部错误 */
		public static final CodeInfo CODE_90001 = new CodeInfo("90001", "系统错误:${message}", "Server Error:${message}");
		/** 未知错误 */
		public static final CodeInfo CODE_90002 = new CodeInfo("90002", "未知错误", "Unknown error");
		/** 系统错误 */
		public static final CodeInfo CODE_99999 = new CodeInfo("99999", "系统错误!", "Server Error!");

		private CodeInfoSet() {
		}
	}

	/** 前端参数 */
	public static class FrontEndParamSet {
		/** 参数：第几页 */
		public static final String PARAM_PAGE_NUMBER = "pageNumber";
		/** 参数：每页记录数 */
		public static final String PARAM_PAGE_SIZE = "pageSize";
		/** JSONP Header参数 :时间戳 */
		public static final String JSONP_TIMESTAMP = "timestamp";
		/** JSONP Header参数 :应用标识 */
		public static final String JSONP_APPID = "appid";
		/** JSONP Header参数 :数据签名 */
		public static final String JSONP_SIGN = "sign";
		/** JSONP参数 :传递给请求处理程序或页面的，用以获得jsonp回调函数名的参数名 */
		public static final String JSONP_CALLBACK = "callbackparam";

		private FrontEndParamSet() {
		}
	}

	/** 时区 */
	public static class TimeZone {
		/** 东八区 */
		public static final String TIME_ZONE_CHINA = "GMT+8";

		private TimeZone() {
		}
	}

	/** 时间格式 */
	public static class DatePattern {
		/** 日期格式 */
		public static final String DATE_PATTERN_DEFAULT = "yyyy-MM-dd";
		/** 日期格式 */
		public static final String DATE_PATTERN_OBLIQUE = "yyyy/MM/dd";
		/** 时间格式 */
		public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
		/** 时间格式 */
		public static final String DATETIME_PATTERN_OBLIQUE = "yyyy/MM/dd HH:mm:ss";

		private DatePattern() {
		}
	}

}