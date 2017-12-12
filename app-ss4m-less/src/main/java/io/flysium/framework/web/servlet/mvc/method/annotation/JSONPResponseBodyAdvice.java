package io.flysium.framework.web.servlet.mvc.method.annotation;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.AbstractJsonpResponseBodyAdvice;

import io.flysium.framework.Consts;

/**
 * JSONP响应参数通知器
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 1.0
 */
@ControllerAdvice(annotations = RestController.class) // Spring 4.2新特性，加之注解会自动注入
public class JSONPResponseBodyAdvice extends AbstractJsonpResponseBodyAdvice {

	/**
	 * 默认无参构造器
	 * 
	 * spring mvc就会为我们所有的json类型的请求提供jsonp数据的支持，只需要在请求的时候携带callbackparam参数，
	 * spring mvc就会返回jsonp类型数据，如果没有callback参数，spring mvc会返回正常的json数据。
	 * 控制器的原有方法不做任何修改，也无需添加任何配置，轻松支持jsonp请求。
	 */
	public JSONPResponseBodyAdvice() {
		super(Consts.FrontEndParamSet.JSONP_CALLBACK);
	}

}
