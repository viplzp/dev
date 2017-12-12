package io.flysium.framework.jdbc.datasource.lookup;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import io.flysium.framework.jdbc.datasource.support.DynamicDataSourceContextHolder;

/**
 * 动态数据源
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 1.0
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

	/**
	 * 用户返回当且切换到的数据库
	 */
	@Override
	protected Object determineCurrentLookupKey() {
		String jndiName = DynamicDataSourceContextHolder.getDataSource();// DynamicDataSourceContextHolder有获取和设置当前数据库的方法get
																			// &
																			// put
		return jndiName;
	}

}
