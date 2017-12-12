package org.jasig.cas.client.validation;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.util.CommonUtils;

/**
 * 远程认证过滤器. 由于AuthenticationFilter的doFilter方法被声明为final，
 * 只好重新实现一个认证过滤器，支持localLoginUrl设置.
 * 
 * @author GuoLin
 * 
 */
public class RemoteAuthenticationFilter extends AbstractCasFilter {
    public static final String CONST_CAS_GATEWAY = "_const_cas_gateway_";
    /**
     * 本地登陆页面URL.  
     */
    private String localLoginUrl;
    /**
     * CAS服务器登录的URL.  
     */
    private String casServerLoginUrl;
    /**
     * 是否发送更新请求。 
     */
    private boolean renew = false;
    /**
     *  * 是否发送网关请求。  
     */
    private boolean gateway = false;

    protected void initInternal(final FilterConfig filterConfig)
            throws ServletException {
        super.initInternal(filterConfig);
        setCasServerLoginUrl(getPropertyFromInitParams(filterConfig,
                "casServerLoginUrl", null));
        log.trace("加载CasServerLoginUrl参数: " + this.casServerLoginUrl);
        setLocalLoginUrl(getPropertyFromInitParams(filterConfig,
                "localLoginUrl", null));
        log.trace("加载LocalLoginUrl参数: " + this.localLoginUrl);
        setRenew(Boolean.parseBoolean(getPropertyFromInitParams(filterConfig,
                "renew", "false")));
        log.trace("加载更新参数: " + this.renew);
        setGateway(Boolean.parseBoolean(getPropertyFromInitParams(filterConfig,
                "gateway", "false")));
        log.trace("加载网关参数: " + this.gateway);
    }

    public void init() {
        super.init();
        CommonUtils.assertNotNull(this.localLoginUrl, "localLoginUrl不能为空.");
        CommonUtils.assertNotNull(this.casServerLoginUrl,
                "casServerLoginUrl不能为空.");
    }

    public final void doFilter(final ServletRequest servletRequest,
            final ServletResponse servletResponse, final FilterChain filterChain)
            throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        final HttpSession session = request.getSession(false);
        final String ticket = request.getParameter(getArtifactParameterName());
        final Assertion assertion = session != null ? (Assertion) session
                .getAttribute(CONST_CAS_ASSERTION) : null;
        final boolean wasGatewayed = session != null
                && session.getAttribute(CONST_CAS_GATEWAY) != null;
        // 如果访问路径为localLoginUrl且带有validated参数则跳过
        URL url = new URL(localLoginUrl);
        final boolean isValidatedLocalLoginUrl = request.getRequestURI()
                .endsWith(url.getPath())
                && CommonUtils.isNotBlank(request.getParameter("validated"));

        if (!isValidatedLocalLoginUrl && CommonUtils.isBlank(ticket)
                && assertion == null && !wasGatewayed) {
            log.debug("no ticket and no assertion found");
            if (this.gateway) {
                log.debug("在session设置网关属性");
                request.getSession(true).setAttribute(CONST_CAS_GATEWAY, "yes");
            }
            final String serviceUrl = constructServiceUrl(request, response);
            if (log.isDebugEnabled()) {
                log.debug("构建服务的url: " + serviceUrl);
            }
            String urlToRedirectTo = CommonUtils.constructRedirectUrl(
                    this.casServerLoginUrl, getServiceParameterName(),
                    serviceUrl, this.renew, this.gateway);
            // 加入localLoginUrl
            urlToRedirectTo += (urlToRedirectTo.contains("?") ? "&" : "?")
                    + "loginUrl=" + URLEncoder.encode(localLoginUrl, "utf-8");
            if (log.isDebugEnabled()) {
                log.debug("redirecting to \"" + urlToRedirectTo + "\"");
            }

            response.sendRedirect(urlToRedirectTo);
            return;
        }
        if (session != null) {
            log.debug("removing gateway attribute from session");
            session.setAttribute(CONST_CAS_GATEWAY, null);
        }
        filterChain.doFilter(request, response);
    }

    public final void setRenew(final boolean renew) {
        this.renew = renew;
    }

    public final void setGateway(final boolean gateway) {
        this.gateway = gateway;
    }

    public final void setCasServerLoginUrl(final String casServerLoginUrl) {
        this.casServerLoginUrl = casServerLoginUrl;
    }

    public final void setLocalLoginUrl(String localLoginUrl) {
        this.localLoginUrl = localLoginUrl;
    }

}
