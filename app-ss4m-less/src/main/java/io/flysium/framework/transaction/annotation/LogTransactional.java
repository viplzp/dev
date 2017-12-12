package io.flysium.framework.transaction.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;

/**
 * 日志数据库事务注解</br>
 * 默认配置下，spring只有在抛出的异常为运行时unchecked异常时才回滚该事务，</br>
 * 也就是抛出的异常为RuntimeException的子类(Errors也会导致事务回滚)，而抛出checked异常则不会导致事务回滚。</br>
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 1.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface LogTransactional {

	/**
	 * 可选的限定描述符</br>
	 * 指定使用的事务管理器
	 * 
	 * @return
	 */
	String value() default "";

	/**
	 * 可选的事务传播行为设置</br>
	 * 默认 Propagation.REQUIRED（如果当前存在事务，则加入该事务；如果当前没有事务，则创建一个新的事务）
	 * 
	 * @return
	 */
	Propagation propagation() default Propagation.REQUIRED;

	/**
	 * 可选的事务隔离级别设置</br>
	 * 默认使用底层数据库的隔离级别。
	 * 
	 * @return
	 */
	Isolation isolation() default Isolation.DEFAULT;

	/**
	 * 事务超时时间，单位为秒</br>
	 * 一个事务所允许执行的最长时间，如果超过该时间限制但事务还没有完成，则自动回滚事务。
	 * 
	 * @return
	 */
	int timeout() default TransactionDefinition.TIMEOUT_DEFAULT;

	/**
	 * 事务只读属性，只读事务用于客户代码只读但不修改数据的情形
	 * 
	 * @return
	 */
	boolean readOnly() default false;

	/**
	 * 导致事务回滚的异常类数组
	 * 
	 * @return
	 */
	Class<? extends Throwable>[] rollbackFor() default {};

	/**
	 * 导致事务回滚的异常类名称数组
	 * 
	 * @return
	 */
	String[] rollbackForClassName() default {};

	/**
	 * 不会导致事务回滚的异常类数组
	 * 
	 * @return
	 */
	Class<? extends Throwable>[] noRollbackFor() default {};

	/**
	 * 不会导致事务回滚的异常类名称数组
	 * 
	 * @return
	 */
	String[] noRollbackForClassName() default {};
}