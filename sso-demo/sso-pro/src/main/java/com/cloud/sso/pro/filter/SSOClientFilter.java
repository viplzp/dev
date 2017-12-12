package com.cloud.sso.pro.filter;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.PostMethod;

import com.cloud.sso.pro.util.HttpUtil;

public class SSOClientFilter implements Filter {
	private static String serverLogin="http://localhost:8081/sso/user/loginPage";
    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        String ticket = request.getParameter("ticket");
        String url = URLEncoder.encode(request.getRequestURL().toString(), "UTF-8");
        String key=request.getParameter("key");
        System.out.println("pro filter:username>"+username);
        System.out.println("jessionid>"+request.getSession().getId());
        System.out.println("ticket->"+ticket+";url->"+url);
        if (null == username&&url.indexOf("logout")<0) {
            if (null != ticket && !"".equals(ticket)) {
                PostMethod method = new PostMethod("http://localhost:8081/sso/ticket");
                method.addParameter("ticket", ticket);
                HttpClient httpClient = new HttpClient();
                try {
                	method.addRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:51.0) Gecko/20100101 Firefox/51.0");
                	method.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
                	method.setRequestHeader("Cookie", "special_cookie=value");
                    httpClient.executeMethod(method);
                    username = method.getResponseBodyAsString();
                    System.out.println("pro 获取用户名："+username);
                    method.releaseConnection();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (null != username && !"".equals(username)) {
                    session.setAttribute("username", username);
                    filterChain.doFilter(request, response);
                } else {
    				if("sso".equals(key)||url.indexOf("user")>0){
   					 filterChain.doFilter(request, response);
	   				}else{
	   					response.sendRedirect(serverLogin+"?service=" + url);					
	   				}
                }
            } else {
				if("sso".equals(key)||url.indexOf("user")>0){
					 filterChain.doFilter(request, response);
				}else{
					response.sendRedirect(serverLogin+"?service=" + url);					
				}
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
    }

}