package io.flysium.framework.web.servlet.handler;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import io.flysium.framework.util.HttpRequestUtils;

/**
 * 全局异常处理器
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 1.0
 */
public class CustomHandlerExceptionResolver extends SimpleMappingExceptionResolver {

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		if (HttpRequestUtils.isAjaxRequest(request)) {
			try {
				super.logException(ex, request);

				String message = ex.getMessage();
				if (StringUtils.isEmpty(message)) {
					message = "未知错误";
				}
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw, true);
				ex.printStackTrace(pw);
				String stack = sw.getBuffer().toString();

				// jQuery.ajax响应error方法
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				// 设置编码
				response.setCharacterEncoding("UTF-8");
				response.setContentType("text/html; charset=UTF-8");

				PrintWriter writer = response.getWriter();
				writer.print("{\"message\":\"" + message + "\", \"stack\":\"" + stack + "\"}");
				writer.flush();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		} else {
			return super.resolveException(request, response, handler, ex);
		}
		return null;
	}

}