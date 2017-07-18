package com.cloud.sso.pro.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloud.sso.pro.util.HttpUtil;

public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = -3170191388656385924L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       super.doGet(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String service = request.getParameter("service");
        Map<String,String>userMap=new HashMap<String,String>();
        userMap.put("username", username);
        userMap.put("password", password);
        userMap.put("service", service);
        String url = "http://localhost:8081/sso/user/login";
        System.out.println("pro 请求参数："+userMap.toString());
		String result = HttpUtil.sendPost(url, userMap, "UTF-8", "2");
		System.out.println("结果："+result);
        if ("false".equals(result)) {
        	request.setAttribute("service", service);
        	request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
        } else {
        	System.out.println("=="+service);
        	 StringBuilder jumpurl = new StringBuilder();
        	 jumpurl.append(service);
             if (0 <= service.indexOf("?")) {
            	 jumpurl.append("&");
             } else {
            	 jumpurl.append("?");
             }
             jumpurl.append("ticket=").append(result);
             response.sendRedirect(jumpurl.toString());
        }
    }

}