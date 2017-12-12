package io.flysium.framework.jdbc.datasource.support;

import org.springframework.util.StringUtils;

import io.flysium.framework.Consts;

/**
 * 动态数据源上下文
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 1.0
 */
public class DynamicDataSourceContextHolder {

	/**
	 * 线程本地环境
	 */
	private static final ThreadLocal<String> holder = new ThreadLocal<String>() {

		@Override
		protected String initialValue() {
			return Consts.JNDI.DEFAULT_JNDI_NAME;
		}
	};

	private DynamicDataSourceContextHolder() {
	}

	/** 设置数据源 */
	public static void setDataSource(String name) {
		holder.set(name);
	}

	/** 获取数据源，如有可能将做切换 */
	public static String getDataSource() {
		String jndiName = holder.get();
		String reserveJndiName = DataSourceSwitchReference.getSwitchReference(jndiName);
		/** 如果要切换的目标数据源不为空，则采用切换的目标数据源，否则用原数据源 */
		if (!StringUtils.isEmpty(reserveJndiName)) {
			holder.set(reserveJndiName);
			return reserveJndiName;
		}
		return jndiName;
	}

}
