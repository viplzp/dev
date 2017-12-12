<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%

 String JSESSIONID = request.getSession().getId();//获取当前JSESSIONID （不管是从主域还是二级域访问产生）

 Cookie cookie = new Cookie("JSESSIONID", JSESSIONID);
 cookie.setDomain(".test.com"); //关键在这里，将cookie设成主域名访问，确保不同域之间都能获取到该cookie的值，从而确保session统一
 response.addCookie(cookie);  //将cookie返回到客户端

 request.getRequestDispatcher("indes.jsp").forward(request, response);

%>