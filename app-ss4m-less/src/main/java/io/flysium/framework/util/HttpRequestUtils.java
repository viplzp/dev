package io.flysium.framework.util;

import javax.servlet.http.HttpServletRequest;

/**
 * HTTP请求工具类
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 1.0
 */
public final class HttpRequestUtils {

	private HttpRequestUtils() {
	}

	/**
	 * 判断是否为Ajax请求
	 * 
	 * @param request
	 * @return
	 */
	public static boolean isAjaxRequest(HttpServletRequest request) {
		String requestType = request.getHeader("X-Requested-With");
		return requestType != null && "XMLHttpRequest".equals(requestType);
	}

}
