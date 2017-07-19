/*************************************************************************************
 * Copyright (C) 2015 Shenzhen Zhubaodai Internet Financial Services Co.,Ltd
 * All Rights Reserved.
 * 本软件为深圳市珠宝贷互联网金融服务股份有限公司开发研制。未经本公司正式书面同意，其他任何个人、团体不得使用、复制、修改或发布本软件。
 *************************************************************************************/
 /*************************************************************************************
 * Copyright (C) 2015 Shenzhen Zhubaodai Internet Financial Services Co.,Ltd
 * All Rights Reserved.
 * 本软件为深圳市珠宝贷互联网金融服务股份有限公司开发研制。未经本公司正式书面同意，其他任何个人、团体不得使用、复制、修改或发布本软件。
 *************************************************************************************/
	

 package com.cloud.sso.oa.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloud.sso.oa.util.HttpUtil;
 /**
 * 类名: Login
 * 类描述: TODO (用一句话描述这个类做什么).
 * 创建日期: 2017-7-18 上午9:09:21
 * @author lzp
 * @version
 * @since JDK 1.6
 * @see
 */
/**
 * 类名: Login
 * 类描述: TODO (用一句话描述这个类做什么).
 * 创建日期: 2017-7-18 上午9:09:21
 *
 * @author lzp
 * @version 
 * @since JDK 1.6
 */

public class Login extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public Login() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<HTML>");
		out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
		out.println("  <BODY>");
		out.print("    This is ");
		out.print(this.getClass());
		out.println(", using the GET method");
		out.println("  </BODY>");
		out.println("</HTML>");
		out.flush();
		out.close();
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

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

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}

	