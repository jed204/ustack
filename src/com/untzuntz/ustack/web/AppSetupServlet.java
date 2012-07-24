package com.untzuntz.ustack.web;

import com.untzuntz.ustack.main.ApplicationInstance;
import com.untzuntz.ustack.main.setup.SetupApp;

public class AppSetupServlet extends UAppServlet {
	
	private static final long serialVersionUID = 1L;

	public ApplicationInstance newApplicationInstance() {
        return new SetupApp(appName);
    }

}
