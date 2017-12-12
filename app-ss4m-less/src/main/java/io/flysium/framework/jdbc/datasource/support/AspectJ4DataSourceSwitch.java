package io.flysium.framework.jdbc.datasource.support;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.annotation.Transactional;

import io.flysium.framework.Consts;
import io.flysium.framework.exception.ErrorHelper;
import io.flysium.framework.transaction.annotation.LogTransactional;

/**
 * 容灾情况，主数据库自动切换到后备数据库
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 1.0
 */
@Aspect
public class AspectJ4DataSourceSwitch {

	private Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 前置
	 * 
	 * @param joinPoint
	 * @throws Throwable
	 */
	public void before(JoinPoint joinPoint) throws NoSuchMethodException {
		Object target = joinPoint.getTarget();
		String method = joinPoint.getSignature().getName();
		Class<?> classz = target.getClass();
		Class<?>[] parameterTypes = ((MethodSignature) joinPoint.getSignature()).getMethod().getParameterTypes();
		try {
			Method m = classz.getMethod(method, parameterTypes);
			if (setDataSource(m)) {
				return;
			}
			Class<?>[] classzInterfaces = target.getClass().getInterfaces();
			for (int i = 0; i < classzInterfaces.length; i++) {
				m = classzInterfaces[i].getMethod(method, parameterTypes);
				if (setDataSource(m)) {
					break;
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	private boolean setDataSource(Method m) {
		/** 如果使用事务注解@LogTransactional，则使用日志数据库 */
		if (m != null && m.isAnnotationPresent(LogTransactional.class)) {
			DynamicDataSourceContextHolder.setDataSource(Consts.JNDI.LOG_JNDI_NAME);
			return true;
		} else if (m != null && m.isAnnotationPresent(Transactional.class)) {
			DynamicDataSourceContextHolder.setDataSource(Consts.JNDI.DEFAULT_JNDI_NAME);
			return true;
		}
		return false;
	}

	/**
	 * 环绕增强
	 * 
	 * @param joinPoint
	 * @throws Throwable
	 */
	public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
		Object obj = null;
		try {
			obj = joinPoint.proceed();// 调用真实的方法
		} catch (CannotCreateTransactionException e) {
			String jndiName = DynamicDataSourceContextHolder.getDataSource();
			if (Consts.JNDI.DEFAULT_JNDI_NAME.equals(jndiName)) {
				String reserveJndiName = Consts.JNDI.RESERVE_JNDI_NAME;
				DataSourceSwitchReference.putSwitchReference(jndiName, reserveJndiName);
				logger.error("系统检测到数据源 " + jndiName + " 无法连接，可能宕机，准备切换后备数据源 " + reserveJndiName + " ，容灾处理。", e);
			} else {
				logger.error("系统检测到数据源 " + jndiName + " 无法连接，可能宕机。", e);
			}
			ErrorHelper.throwError(Consts.CodeInfoSet.CODE_99999);
		}
		return obj;
	}

}