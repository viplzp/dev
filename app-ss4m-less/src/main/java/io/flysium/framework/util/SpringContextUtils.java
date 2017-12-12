package io.flysium.framework.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Spring框架上下文工具类
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 1.0
 */
@Component
public final class SpringContextUtils implements ApplicationContextAware {

	private static Logger logger = LoggerFactory.getLogger(SpringContextUtils.class);

	private static ApplicationContext springApplicationContext;

	private SpringContextUtils() {
	}

	private static void setSpringApplicationContext(ApplicationContext springApplicationContext) {
		SpringContextUtils.springApplicationContext = springApplicationContext;
	}

	/**
	 * 设置上下文
	 * 
	 * @param springApplicationContext
	 * @throws BeansException
	 */
	@Override
	public void setApplicationContext(ApplicationContext context) {
		setSpringApplicationContext(context);
	}

	/**
	 * 返回上下文
	 * 
	 * @param <T>
	 * @return
	 */
	public static <T extends ApplicationContext> T getApplicationContext() {
		return (T) springApplicationContext;
	}

	/**
	 * 根据bean的name返回bean实例
	 * 
	 * @param <T>
	 * @param name
	 * @return
	 * @throws BeansException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		return (T) springApplicationContext.getBean(name);
	}

	/**
	 * 根据bean的name和class类型返回bean实例
	 * 
	 * @param <T>
	 * @param name
	 * @param requiredType
	 * @return
	 * @throws BeansException
	 */
	public static <T> T getBean(String name, Class<T> requiredType) {
		return springApplicationContext.getBean(name, requiredType);
	}

	/**
	 * 根据class类型返回bean实例
	 * 
	 * @param <T>
	 * @param requiredType
	 * @return
	 * @throws BeansException
	 */
	public static <T> T getBean(Class<T> requiredType) {
		return springApplicationContext.getBean(requiredType);
	}

	/**
	 * BeanFactory是否包含此name的实例
	 * 
	 * @param name
	 * @return
	 */
	public static boolean containsBean(String name) {
		return springApplicationContext.containsBean(name);
	}

	/**
	 * 根据bean的name判断该bean是否单例
	 * 
	 * @param name
	 * @return
	 * @throws NoSuchBeanDefinitionException
	 */
	public static boolean isSingleton(String name) {
		return springApplicationContext.isSingleton(name);
	}

	/**
	 * 根据bean的name判断该bean是否多例
	 * 
	 * @param name
	 * @return
	 * @throws NoSuchBeanDefinitionException
	 */
	public static boolean isPrototype(String name) {
		return springApplicationContext.isPrototype(name);
	}

	/**
	 * 根据bean的name判断该bean是否实现了class类型接口
	 * 
	 * @param name
	 * @param targetType
	 * @return
	 * @throws NoSuchBeanDefinitionException
	 */
	public static boolean isTypeMatch(String name, Class<?> targetType) {
		return springApplicationContext.isTypeMatch(name, targetType);
	}

	/**
	 * 根据bean的name返回该bean的class类型
	 * 
	 * @param name
	 * @return
	 * @throws NoSuchBeanDefinitionException
	 */
	public static Class<?> getType(String name) {
		return springApplicationContext.getType(name);
	}

	/**
	 * 根据bean的name返回该bean的别名
	 * 
	 * @param name
	 * @return
	 */
	public static String[] getAliases(String name) {
		return springApplicationContext.getAliases(name);
	}

	/**
	 * 从spring中取bean，没有就注册一个
	 * 
	 * @param <T>
	 * @param name
	 * @param requiredType
	 * @return
	 */
	public static <T> T getOrRegisterBean(String name, Class<T> requiredType) {
		if (requiredType == null) {
			return null;
		}

		ConfigurableApplicationContext appContext = getApplicationContext();
		T bean = null;
		try {
			bean = appContext.getBean(name, requiredType);
		} catch (NoSuchBeanDefinitionException e) {
			logger.error(e.getMessage(), e);
		}

		if (bean == null) {
			DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) appContext.getBeanFactory();
			BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(requiredType);
			beanFactory.registerBeanDefinition(name, beanDefinitionBuilder.getRawBeanDefinition());

			bean = appContext.getBean(name, requiredType);
		}

		return bean;
	}

	/**
	 * 从spring中取bean，没有就注册一个，bean的name是类名(首字母小写)
	 * 
	 * @param <T>
	 * @param requiredType
	 * @return
	 */
	public static <T> T getOrRegisterBean(Class<T> requiredType) {
		if (requiredType == null) {
			return null;
		}

		String name = StringUtils.uncapitalize(requiredType.getSimpleName());// 首字母小写
		return getOrRegisterBean(name, requiredType);
	}

}
