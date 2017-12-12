package io.flysium.framework.app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import io.flysium.framework.util.SpringContextUtils;

/**
 * 应用全局配置
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 1.0
 */
@Configuration
public class AppConfig {

	@Value("${application.name}")
	private String applicationName;

	@Value("${application.mode}")
	private String applicationMode;

	public String getApplicationName() {
		return applicationName;
	}

	public String getApplicationMode() {
		return applicationMode;
	}

	/**
	 * 获取应用全局配置
	 * 
	 * @return
	 */
	public static AppConfig getInst() {
		return SpringContextUtils.getBean(AppConfig.class);
	}

}
