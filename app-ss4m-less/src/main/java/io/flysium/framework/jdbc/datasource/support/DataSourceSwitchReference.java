package io.flysium.framework.jdbc.datasource.support;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 全局数据源切换映射表
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 1.0
 */
public final class DataSourceSwitchReference {

	/**
	 * 全局数据源切换映射表，采用ConcurrentHashMap保证读基本不需要锁，不影响高并发场景使用，而写操作仅在数据库宕机情况使用
	 */
	private static final ConcurrentHashMap<String, String> switchReference = new ConcurrentHashMap();

	private DataSourceSwitchReference() {
	}

	/**
	 * 设置全局数据源切换映射
	 * 
	 * @param name
	 *            数据源名称
	 * @param reserveJndiName
	 *            切换的目标数据源名称
	 */
	public static void putSwitchReference(String name, String reserveJndiName) {
		switchReference.put(name, reserveJndiName);
	}

	/**
	 * 获取全局数据源切换映射
	 * 
	 * @param name
	 * @return
	 */
	public static String getSwitchReference(String name) {
		return switchReference.get(name);
	}

}
