package com.cloud.sso.server.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloud.sso.server.JVMCache;

public class TicketServlet extends HttpServlet {
    private static final long serialVersionUID = 5964206637772848290L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String ticket = request.getParameter("ticket");
        System.out.println("ticket:"+ticket);
        String username = JVMCache.TICKET_AND_NAME.get(ticket);
        JVMCache.TICKET_AND_NAME.remove(ticket);
		/*for (Map.Entry<String, String> map: JVMCache.TICKET_AND_NAME.entrySet()) {
			JVMCache.TICKET_AND_NAME.remove(map.getKey());
		}*/
        System.out.println("JVMCache:"+JVMCache.TICKET_AND_NAME.toString());
        if(null!=username&&!"".equals(username)){
        	PrintWriter writer = response.getWriter();
        	writer.write(username);       	
        }
    }

}