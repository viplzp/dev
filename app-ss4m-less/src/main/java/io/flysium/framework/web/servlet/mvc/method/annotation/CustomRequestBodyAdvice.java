package io.flysium.framework.web.servlet.mvc.method.annotation;

import java.io.IOException;
import java.lang.reflect.Type;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

/**
 * 自定义请求参数通知器
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 1.0
 */
@ControllerAdvice // Spring 4.2新特性，加之注解会自动注入
public class CustomRequestBodyAdvice implements RequestBodyAdvice {

	@Override
	public boolean supports(MethodParameter methodparameter, Type type,
			Class<? extends HttpMessageConverter<?>> class1) {
		return true;
	}

	@Override
	public Object handleEmptyBody(Object obj, HttpInputMessage httpinputmessage, MethodParameter methodparameter,
			Type type, Class<? extends HttpMessageConverter<?>> class1) {
		/**
		 * 自定义如何处理空的方法参数
		 */
		return obj;
	}

	@Override
	public HttpInputMessage beforeBodyRead(HttpInputMessage httpinputmessage, MethodParameter methodparameter,
			Type type, Class<? extends HttpMessageConverter<?>> class1) throws IOException {
		/**
		 * 如果你需要在参数前做加密处理，请自定义此方法
		 */
		return httpinputmessage;
	}

	@Override
	public Object afterBodyRead(Object obj, HttpInputMessage httpinputmessage, MethodParameter methodparameter,
			Type type, Class<? extends HttpMessageConverter<?>> class1) {
		/**
		 * 在方法参数已经被解析后，在进入Controller实际方法之前
		 */
		return obj;
	}

}
