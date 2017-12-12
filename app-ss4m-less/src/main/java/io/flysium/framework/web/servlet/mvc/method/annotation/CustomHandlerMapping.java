package io.flysium.framework.web.servlet.mvc.method.annotation;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.condition.ConsumesRequestCondition;
import org.springframework.web.servlet.mvc.condition.HeadersRequestCondition;
import org.springframework.web.servlet.mvc.condition.ParamsRequestCondition;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.ProducesRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * 自定义处理器映射器（HandlerMapping）
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 1.0
 */
public class CustomHandlerMapping extends RequestMappingHandlerMapping {

	private boolean useSuffixPatternMatch = true;

	private boolean useTrailingSlashMatch = true;

	private ContentNegotiationManager contentNegotiationManager = new ContentNegotiationManager();

	private final List fileExtensions = new ArrayList();

	@Override
	protected RequestMappingInfo getMappingForMethod(Method method, Class handlerType) {
		RequestMappingInfo info = createRequestMappingInfoDefault(method);
		if (info != null) {
			RequestMappingInfo typeInfo = createRequestMappingInfoDefault(handlerType);
			if (typeInfo != null)
				info = typeInfo.combine(info);
		}
		return info;
	}

	private RequestMappingInfo createRequestMappingInfoDefault(AnnotatedElement element) {
		RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(element, RequestMapping.class);
		RequestCondition condition = (element instanceof Class)
				? getCustomTypeCondition((Class) element)
				: getCustomMethodCondition((Method) element);
		/**
		 * 以类名和方法名映射请求，参照@RequestMapping 默认不需要添加任何参数(如：/className/methodName.do)
		 */
		String defaultName = (element instanceof Class)
				? ((Class) element).getSimpleName()
				: ((Method) element).getName();
		return requestMapping == null ? null : createRequestMappingInfo(requestMapping, condition, defaultName);
	}

	protected RequestMappingInfo createRequestMappingInfo(RequestMapping annotation,
			RequestCondition<?> customCondition, String defaultName) {
		String[] patterns = resolveEmbeddedValuesInPatterns(annotation.value());
		if (patterns != null && (patterns.length == 0)) {
			patterns = new String[]{defaultName};
		}
		return new RequestMappingInfo(
				new PatternsRequestCondition(patterns, getUrlPathHelper(), getPathMatcher(), this.useSuffixPatternMatch,
						this.useTrailingSlashMatch, this.fileExtensions),
				new RequestMethodsRequestCondition(annotation.method()),
				new ParamsRequestCondition(annotation.params()), new HeadersRequestCondition(annotation.headers()),
				new ConsumesRequestCondition(annotation.consumes(), annotation.headers()), new ProducesRequestCondition(
						annotation.produces(), annotation.headers(), this.contentNegotiationManager),
				customCondition);
	}

}
