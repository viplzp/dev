<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<%
String service = request.getParameter("service");
%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>pro系统-登录页面</title>
</head>
<body>
  <form action="/pro/user/login" method="post">
  userName: <input type="text" name="username" value="cloud"/> <br>
  password: <input type="text" name="password" value="cloud"/> <br>
  service: <input type="text" name="service" value="<%=service%>"/> <br>
  rememberMe: <input type="checkbox" value="on" name="rememberMe" >
  <input type="submit" value="submit"/>
  </form>
</body>
</html>