package com.untzuntz.ustack.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LogoutServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		ServletOutputStream out = resp.getOutputStream();
		
		PrintWriter w = new PrintWriter(out);
		// output head
		w.println("<html><head><title>Logout Complete</title><style type=\"text/css\">");
		w.println("/*<![CDATA[*/");
		// output css
		w.println("body { background-color: #fff; color: #000; font-size: 0.9em; font-family: sans-serif,helvetica; margin: 0; padding: 0; }");
		w.println("h1 { text-align: center; margin: 0; padding: 0.6em 2em 0.4em; background-color: #294172; color: #fff; font-weight: bold; font-size: 1.75em; border-bottom: 2px solid #000; }");
		w.println("h3 { text-align: center; background-color: #ddd; padding: 0.5em; color: #000; }");
		w.println("</style></head>");

		// output body
		w.println("<body>");
		w.println("<h1><strong>Logout Complete</strong></h1><div class=\"content\"><h3><p>You have been logged out of the system</p></h3></div>");
		w.println("</body></html>");
		
		w.flush();
		w.close();

		req.getSession().invalidate();
	}

	
	
}
