package io.flysium.framework.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Http请求上下文工具类，仅限于Spring
 * MVC中使用，在Servlet中请使用HttpServletRequest或者HttpServletResponse直接获取
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 1.0
 */
public final class HttpRequestContextUtils {// 需要在web.xml中注册Spring监听器RequestContextListener

	private HttpRequestContextUtils() {
	}

	/**
	 * 获取HttpSession
	 * 
	 * @return
	 */
	public static final HttpSession getSession() {
		return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession();
	}

	/**
	 * 获取HttpServletRequest
	 * 
	 * @return
	 */
	public static final HttpServletRequest getRequest() {
		return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
	}

	/**
	 * 获取HttpServletResponse
	 * 
	 * @return
	 */
	public static final HttpServletResponse getResponse() {
		return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
	}
}
