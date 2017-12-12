package io.flysium.framework.util;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * Spring配置文件工具类
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 1.0
 */
public class SpringPropertyUtils extends PropertyPlaceholderConfigurer {

	private static Map ctxPropertiesMap = new ConcurrentHashMap();

	@Override
	protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) {
		super.processProperties(beanFactoryToProcess, props);
		for (Object key : props.keySet()) {
			String keyStr = key.toString();
			String value = props.getProperty(keyStr);
			ctxPropertiesMap.put(keyStr, value);
		}
	}

	/**
	 * 获取变量值
	 * 
	 * @param name
	 *            变量名称
	 * @return
	 */
	public static Object getContextProperty(String name) {
		Object value = ctxPropertiesMap.get(name);
		if (value != null && (value instanceof String)) {
			return ((String) value).trim();
		}
		return value;
	}

	/**
	 * 获取变量值，如果不存在，则取默认值
	 * 
	 * @param name
	 *            变量名称
	 * @param defaultValue
	 *            默认值
	 * @return
	 */
	public static Object getContextProperty(String name, Object defaultValue) {
		Object value = getContextProperty(name);
		if (value == null || "".equals(value)) {
			return defaultValue;
		}
		return value;
	}

}
