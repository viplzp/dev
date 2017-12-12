<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<head lang="en">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Classic Demo</title>
</head>
<body>
	<form id="demo_form" method="post">
		<table>
			<tr>
				<td>用户名：</td>
				<td><input type="text" name="username" value="sven"></input></td>
			</tr>
			<tr>
				<td>密码：</td>
				<td><input type="password" name="password" value="password"></input></td>
			</tr>
			<tr>
				<td>姓名：</td>
				<td><input type="text" name="name" value="SvenAugustus"></input></td>
			</tr>
			<tr>
				<td></td>
				<td><input type="submit" value="提交" onclick="onSubmit();"></input></td>
			</tr>
		</table>
	</form>
</body>
<script type="text/javascript">
	function getContextPath() {
	    var pathname = document.location.pathname;
	    var index = pathname.substr(1).indexOf("/");
	    var context_path = "";
	    
	    if (index !== -1) {
		    context_path = pathname.substr(0, index + 1);
	    }
	    
	    if (context_path && context_path.substr(0, 1) != "/") {
		    context_path = "/" + context_path;
	    }
	    
	    return context_path;
    }

    function onSubmit() {
	    document.getElementById("demo_form").action = getContextPath() + "/demo/classic/hi.do";
    }
</script>
</html>
