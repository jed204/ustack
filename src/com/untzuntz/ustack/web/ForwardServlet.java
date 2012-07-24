package com.untzuntz.ustack.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ForwardServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		String uri = request.getRequestURI();
		if (uri.lastIndexOf("/") > -1)
			uri = uri.substring( 0, uri.lastIndexOf("/") );
		if (uri.lastIndexOf("/") > -1)
			uri = uri.substring( uri.lastIndexOf("/") );
		
		if (request.getParameter("uid") != null)
			request.getSession().setAttribute("uid", request.getParameter("uid"));
		if (request.getParameter("ref") != null)
			request.getSession().setAttribute("ref", request.getParameter("ref"));
		
		String fwd = null;
//		if (!request.getSession().isNew())
//		{
//			fwd = uri + "?sid=ExternalEvent";
//			if (!fwd.startsWith("/"))
//				fwd = "/" + fwd;
//			getServletConfig().getServletContext().getRequestDispatcher(fwd).forward(request, response);
//		}
//		else
//		{
			fwd = uri + "?fwd=true";
			if (!fwd.startsWith("/"))
				fwd = "/" + fwd;
			getServletConfig().getServletContext().getRequestDispatcher(fwd).forward(request, response);
//		}
			
	}

}
