package com.cloud.sso.server.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloud.sso.server.JVMCache;

public class SSOServerFilter implements Filter {

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String service = request.getParameter("service");
        String ticket = request.getParameter("ticket");
        Cookie[] cookies = request.getCookies();
        System.out.println("sso server is called:service->"+service);
        System.out.println("ticket->"+ticket);
        System.out.println("url->"+request.getRequestURL());
        System.out.println("cookies:"+cookies.length);
        String jsessionid = "";
        if (null != cookies) {
            for (Cookie cookie : cookies) {
            	System.out.println("cookie:"+cookie.getName()+"="+cookie.getValue());
                if ("sso".equals(cookie.getName())) {
                	jsessionid = cookie.getValue();
                    break;
                }
            }
        }

        if (null == service && null != ticket) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        if (null != jsessionid && !"".equals(jsessionid)) {
            long time = System.currentTimeMillis();
            String timeString = jsessionid + time;
            JVMCache.TICKET_AND_NAME.put(timeString, JVMCache.TICKET_AND_NAME.get(jsessionid));
    		System.out.println( timeString);
    		System.out.println( JVMCache.TICKET_AND_NAME.size());

            StringBuilder url = new StringBuilder();
            url.append(service);
            if (0 <= service.indexOf("?")) {
                url.append("&");
            } else {
                url.append("?");
            }
            url.append("ticket=").append(timeString);
            response.sendRedirect(url.toString());
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
    }

}