package com.untzuntz.ustack.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nextapp.echo.webcontainer.WebContainerServlet;

import org.apache.log4j.Logger;

import com.untzuntz.ustack.main.ApplicationInstance;

abstract public class UAppServlet extends WebContainerServlet {

	private static Logger logger = Logger.getLogger(UAppServlet.class);
	private static final long serialVersionUID = -1845885128725500085L;
	protected String appName;

	public void init() throws ServletException {
		logger.info("Initialized Servlet...");
		appName = getServletConfig().getInitParameter("appName");
		ApplicationInstance.setAppName(appName);
	}

	protected void process(HttpServletRequest request, HttpServletResponse resp)
			throws IOException, ServletException {
		
		if (request.getParameter("uid") != null)
			request.getSession().setAttribute("uid", request.getParameter("uid"));
		if (request.getParameter("ref") != null)
			request.getSession().setAttribute("ref", request.getParameter("ref"));

		super.process(request, resp);
	}

}
