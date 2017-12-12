<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%><!-- JSP el表达式默认关闭。若isELIgnored设置为true，代表在本页不使用el表达式，当做字符串解析出来显示。isELIgnored设置为false，el表达式正常工作，显示正常。  -->
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head lang="en">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Spring MVC Demo</title>
</head>
<body>
	<p>Hi,${message}.Welcome to Spring MVC.</p>
</body>
</html>
