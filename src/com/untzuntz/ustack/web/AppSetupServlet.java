package com.untzuntz.ustack.web;

import javax.servlet.ServletException;

import com.untzuntz.ustack.data.APILog;
import com.untzuntz.ustack.main.ApplicationInstance;
import com.untzuntz.ustack.main.setup.SetupApp;

public class AppSetupServlet extends UAppServlet {
	
	private static final long serialVersionUID = 1L;
	
	@Override
	public void init() throws ServletException {
		super.init();
		APILog.generateCollection();
	}

	public ApplicationInstance newApplicationInstance() {
        return new SetupApp(appName);
    }

}
