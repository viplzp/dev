package com.cloud.sso.oa.filter;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

import com.cloud.sso.oa.util.HttpUtil;

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
    	String path = request.getContextPath();
    	String basePath = request.getScheme() + "://"
    			+ request.getServerName() + ":" + request.getServerPort()
    			+ path + "/";
        System.out.println("oa filter:username>"+username);
        System.out.println("ticket->"+ticket+";url->"+url);
        if (null == username&&url.indexOf("login")<0) {
            if (null != ticket && !"".equals(ticket)) {
                PostMethod postMethod = new PostMethod("http://localhost:8081/sso/ticket");
                postMethod.addParameter("ticket", ticket);
                HttpClient httpClient = new HttpClient();
                try {
                    httpClient.executeMethod(postMethod);
                    username = postMethod.getResponseBodyAsString();
                    System.out.println("oa 获取用户名："+username);
                    postMethod.releaseConnection();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (null != username && !"".equals(username)) {
                    session.setAttribute("username", username);
                    filterChain.doFilter(request, response);
                } else {
					HttpUtil.sendPost(serverLogin, null, "UTF-8", "1");
					String jumpUrl=basePath+"loginPage";
					if(url.indexOf("loginPage")>0){
			            filterChain.doFilter(request, response);
					}else{
						response.sendRedirect(jumpUrl+"?service=" + url);					
					}
                }
            } else {
            	HttpUtil.sendPost(serverLogin, null, "UTF-8", "1");
				String jumpUrl=basePath+"loginPage";
				if(url.indexOf("loginPage")>0){
		            filterChain.doFilter(request, response);
				}else{
					response.sendRedirect(jumpUrl+"?service=" + url);					
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