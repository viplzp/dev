package com.cloud.sso.server.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloud.sso.server.JVMCache;

public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = -3170191388656385924L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       this.doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String service = request.getParameter("service");
        HashMap<String,Object>userMap=new HashMap<String,Object>();
        userMap.put("username", username);
        userMap.put("password", password);
        System.out.println("服务器接受参数："+userMap.toString());
        PrintWriter writer = response.getWriter();

        if ("cloud".equals(username) && "cloud".equals(password)) {
        	String jesessionid = request.getSession().getId();
        	System.out.println("sso 登录成功！！！！");
            Cookie cookie = new Cookie("sso", jesessionid);
            cookie.setPath("/");
            cookie.setMaxAge(60);
            response.addCookie(cookie);

            long time = System.currentTimeMillis();
            String timeString = username + time;
            JVMCache.TICKET_AND_NAME.put(timeString, userMap.toString());
            JVMCache.TICKET_AND_NAME.put(jesessionid, userMap.toString());
            writer.write(timeString);

        } else {
        	writer.write("false");
        }
    }

}