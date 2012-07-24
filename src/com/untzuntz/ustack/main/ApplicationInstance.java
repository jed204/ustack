package com.untzuntz.ustack.main;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import nextapp.echo.app.Component;
import nextapp.echo.app.ContentPane;
import nextapp.echo.app.SplitPane;
import nextapp.echo.app.Window;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;
import nextapp.echo.webcontainer.ClientProperties;
import nextapp.echo.webcontainer.Connection;
import nextapp.echo.webcontainer.ContainerContext;
import nextapp.echo.webcontainer.WebContainerServlet;

import org.apache.log4j.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.untzuntz.components.app.OnLoad;
import com.untzuntz.components.app.PageMask;
import com.untzuntz.ustack.aaa.Authentication;
import com.untzuntz.ustack.aaa.ResourceDefinition;
import com.untzuntz.ustack.aaa.ResourceLink;
import com.untzuntz.ustack.aaa.RoleDefinition;
import com.untzuntz.ustack.aaa.UBasePermission;
import com.untzuntz.ustack.aaa.UStackPermissionEnum;
import com.untzuntz.ustack.data.Country;
import com.untzuntz.ustack.data.UserAccount;
import com.untzuntz.ustack.data.WebAppSession;
import com.untzuntz.ustack.exceptions.AuthExceptionUserPasswordMismatch;
import com.untzuntz.ustack.exceptions.AuthenticationException;
import com.untzuntz.ustack.exceptions.AuthorizationException;
import com.untzuntz.ustack.exceptions.ForgotPasswordLinkExpired;
import com.untzuntz.ustack.exceptions.InvalidAccessAttempt;
import com.untzuntz.ustack.uisupport.UEntryError;
import com.untzuntz.ustack.uisupport.UStackStatics;
import com.untzuntz.ustack.uisupport.widgets.interf.UserPassEntryInt;

abstract public class ApplicationInstance extends nextapp.echo.app.ApplicationInstance implements UControllerInt {

	private static final long serialVersionUID = 2865734405614972072L;
	private static Logger logger = Logger.getLogger(ApplicationInstance.class);

	public static final String SUBSYS_SETUP = "SubSys.Setup";
	/** Authentication Subsystem Text */
	public static final String SUBSYS_AUTH = "SubSys.Authentication";
	/** The name of the host the application is running on  */
	private static final String hostName;

	abstract public String getResourceName();
	
	public static String getCurrentResourceName()
	{
		return ((ApplicationInstance)nextapp.echo.app.ApplicationInstance.getActive()).getResourceName();
	}
	
	/** Returns the current user */
	public static UserAccount getCurrentUser()
	{
		return ((ApplicationInstance)nextapp.echo.app.ApplicationInstance.getActive()).getUser();
	}
	
	public static PageMask getLightBox()
	{
		return ((ApplicationInstance)nextapp.echo.app.ApplicationInstance.getActive()).lightBox;
	}
	
	public static ApplicationInstance getUStackActive() {
		return (ApplicationInstance)nextapp.echo.app.ApplicationInstance.getActive();
	}
	
	/**
	 * List of text resources, can be added by extending application
	 */
	private static final List<String> messageBundles = new Vector<String>();

	/**
	 * List of type resources, can be added by extending application
	 */
	private static final List<String> resourceTypes = new Vector<String>();

	/**
	 * List of configuration properties to use as defaults values
	 */
	private static final List<String> configurations = new Vector<String>();
	
	static
	{
		addMessageBundle("com.untzuntz.ustack.resources.Messages");
		addConfigurationFile("com.untzuntz.ustack.resources.Config");
		addResourceType(ResourceDefinition.TYPE_USERACCESS);
		addResourceType(ResourceDefinition.TYPE_SITEPROFILE);
		
		String tHost = null;
		try { tHost = InetAddress.getLocalHost().getHostName(); } catch (Exception err) { tHost = "Unknown"; }
		hostName = tHost;
		
		if (System.getProperty("UAppCfg.CacheHost") != null)
			ApplicationInstance.setCacheEnabled(true);
	}

	/**
	 * Adds a message bundle path. The message bundle will be used in converting text parameters to localized text
	 * 
	 * @param bundlePath
	 */
	public static void addMessageBundle(String bundlePath)
	{
		logger.info("Message Bundle Added [" + bundlePath + "]");
		messageBundles.add(bundlePath);
	}
	
	/**
	 * Adds a resource type.
	 * @param txt
	 */
	public static void addResourceType(String txt)
	{
		logger.info("Resource Type Added [" + txt + "]");
		resourceTypes.add(txt);
	}

	/**
	 * Returns a list of resource types
	 * 
	 * @return
	 */
	public static List<String> getResourceTypes()
	{
		return resourceTypes;
	}

	/**
	 * Gets the list of messages bundles. Used internally.
	 * 
	 * @return
	 */
	public static List<String> getMessageBundles()
	{
		return messageBundles;
	}
	
	/**
	 * Returns the name of the server/host the application is running on
	 * @return
	 */
	public static String getHostName() {
		return hostName;
	}

	/**
	 * Adds a configuration file path. The configuration will be used in pulling default values for use in your application
	 * 
	 * @param filePath
	 */
	public static void addConfigurationFile(String filePath)
	{
		logger.info("Configuration Added [" + filePath + "]");
		configurations.add(filePath);
		UOpts.addConfigurationFile(filePath);
	}

	/**
	 * Gets the list of configuration. Used internally.
	 * 
	 * @return
	 */
	public static List<String> getConfigurationFiles()
	{
		return configurations;
	}
	
	public static boolean getCacheEnabled()
	{
		return cacheFlag;
	}
	
	public static void setCacheEnabled(boolean cf) {
		cacheFlag = cf;
		UOpts.setCacheFlag(cf);
	}

	/** Setup app */
	@SuppressWarnings("unchecked")
	public Window init() 
	{
        window = new Window();
        
        /*
         * Information
         */
		ContainerContext containerContext = (ContainerContext) ApplicationInstance.getActive().getContextProperty(ContainerContext.CONTEXT_PROPERTY_NAME);
		initParams = (Map<String,String[]>)containerContext.getInitialRequestParameterMap();
		if (initParams != null)
		{
			logger.info("Application Initial Params ... " + initParams.size() + " items to process");
			Iterator<String> it = initParams.keySet().iterator();
			while (it.hasNext())
			{
				String key = it.next();
				Object val = initParams.get(key);
				if (val instanceof String[])
					val = (String)initParams.get(key)[0];
				
				logger.info("  -> '" + key + "' = '" + val + "'");
			}
		}
		
    	HttpServletRequest req = WebContainerServlet.getActiveConnection().getRequest();
    	clientIP = req.getHeader("X-Real-IP");
		if (clientIP == null)
			clientIP = req.getRemoteAddr();
		clientCountry = req.getHeader("X-Country-Code");
		if (clientCountry == null)
			clientCountry = "UNK";
		
		logger.info("IP: " + clientIP + " -> " + clientCountry);

		/*
		 * UI
		 */
		SplitPane sp = new SplitPane(SplitPane.ORIENTATION_HORIZONTAL_RIGHT_LEFT, UStackStatics.EX_0);
		window.getContent().add(sp);

		
		SplitPane isp = new SplitPane(SplitPane.ORIENTATION_HORIZONTAL_RIGHT_LEFT, UStackStatics.EX_0);

		//extEventMon = new ExternalEventMonitor();
		//Column c = new Column();
		//c.add(extEventMon);
		//isp.add(c);
		isp.add(lightBox = new PageMask());
		isp.add(onLoad = new OnLoad());
		onLoad.setActionCommand("pageLoaded");
		onLoad.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {
				if ("pageLoaded".equalsIgnoreCase(e.getActionCommand()))
					pageLoaded();
			}
		});
		lightBox.setVisible(false);
		
		sp.add(isp);
		
		targetPane = new ContentPane();
		sp.add(targetPane);
	
		setupApp();
		
		if (Country.getCountryCount() == 0)
			Country.loadCountriesToDB();
		
		if (UserAccount.getAccountCount() == 0)
		{
			logger.info("No user accounts - creating default user admin account");
			try { 
				
				// Basic Resource Definition for the Setup App
				ResourceDefinition res = ResourceDefinition.getByName("Setup App");
				if (res == null)
					res = ResourceDefinition.createResource("Setup App", ResourceDefinition.TYPE_USERACCESS);
				
				if (!res.hasRole("General"))
				{
					RoleDefinition role = new RoleDefinition("General");
					role.addPermission(UBasePermission.Login.getPermission());
					role.addPermission(UBasePermission.ManageRoles.getPermission());
					res.addRole(role);
					res.addCanManage("app");
					res.addManagedBy("app");
				}
				res.save(ApplicationInstance.SUBSYS_SETUP);

				UserAccount user = UserAccount.createUser("app setup", "admin", "$$$" + getAppName() + "$$$"); 
				logger.info("Saving default user admin account");
				user.addResourceLink(new ResourceLink(res, "General"));
				user.save(ApplicationInstance.SUBSYS_SETUP);

			} catch (Exception err) { logger.error("Failed to create default user account", err); }
		}

        return window;
    }
	
	abstract public void setupApp();

	/** The appName configured via web.xml */
	private static String appName;

	/** The cacheFlag configured via web.xml */
	private static boolean cacheFlag;

	private ContentPane targetPane;
	private Map<String,String[]> initParams;
	private String clientIP;
	private String clientCountry;
	private PageMask lightBox;
	private OnLoad onLoad;
	
	protected ContentPane getTargetPane() {
		return targetPane;
	}
	
	public DBObject getClientIPObject() {
		DBObject ret = new BasicDBObject();
		ret.put("requestIP", clientIP);
		ret.put("requestCountry", clientCountry);
		return ret;
	}
	
	public String getClientIP() { 
		return clientIP;
	}
	
	public String getClientCountry() { 
		return clientCountry;
	}
	
	public String getUserName() {
		
		if (getUser() != null)
			return getUser().getUserName();
		
		return null;
		
	}
	
	public String getHTTPSessionID() { 
		
		try {
			Connection conn = WebContainerServlet.getActiveConnection();
			return conn.getRequest().getSession().getId();
		} catch (Exception err) { 
			return null;
		}
	}
	
	public URL getURL() {

		URL myURL = null;
		
		try {
			Connection conn = WebContainerServlet.getActiveConnection();
        	myURL = new URL( conn.getRequest().getRequestURL().toString() );
		} catch (Exception err) { 
			return null;
		}

		return myURL;
	}
	
	public String getBaseURL()
	{
		URL myURL = getURL();
		if (myURL == null)
			return null;
		
		String port = "";
		if (myURL != null && myURL.getPort() > -1)
			port = ":" + myURL.getPort();

		return myURL.getProtocol() + "://" + myURL.getHost() + port;
	}

	public boolean isInternetExplorer()
	{
		if (WebContainerServlet.getActiveConnection().getUserInstance().getClientProperties().get(ClientProperties.BROWSER_INTERNET_EXPLORER) != null)
			return true;
		
		return false;
	}
	
	public boolean isMicrosoftWindows()
	{
		String agent = (String)WebContainerServlet.getActiveConnection().getUserInstance().getClientProperties().get(ClientProperties.NAVIGATOR_USER_AGENT);
		if (agent != null && agent.toLowerCase().indexOf("windows") > -1)
			return true;
		
		return false;
	}
	
	public String getBrowserVerMajor()
	{
		return (String)WebContainerServlet.getActiveConnection().getUserInstance().getClientProperties().get(ClientProperties.BROWSER_VERSION_MAJOR);
	}
	
	public String getBrowserVerMinor()
	{
		return (String)WebContainerServlet.getActiveConnection().getUserInstance().getClientProperties().get(ClientProperties.BROWSER_VERSION_MINOR);
	}
	
	public String getInitParam(String name)
	{
		if (initParams == null)
			return null;
		
		if (initParams.get(name) == null || initParams.get(name).length == 0)
			return null;
		
		return initParams.get(name)[0];
	}
	
	/** Returns the name of the app as configured in web.xml */
	public static String getAppName()
	{
		if (appName == null && "true".equalsIgnoreCase(System.getProperty("TestCase")))
			appName = "testCase";
		return appName;
	}

	/** Set the application name */
	public static void setAppName(String an)
	{
		appName = an;
		UOpts.setAppName(appName);
	}
	
	/**
	 * Initiate application with app name
	 * @param appName
	 */
	public ApplicationInstance(String appName)
	{
		super();
		setAppName(appName);

		String cacheHost = System.getProperty(UAppCfg.CACHE_HOST_STRING);
		if (cacheHost != null && cacheHost.length() != 0)
			ApplicationInstance.setCacheEnabled(true);
	}

	/** The main application window */
	protected Window window;
	/** The user who is logged in */
	protected UserAccount user;
	/** Current controller */
	private UControllerInt ctrl;
	
	public UControllerInt getController() { return ctrl; }
	
	/**
	 * Load the a view
	 * @param comp
	 */
	public void loadView(Component comp)
	{
		targetPane.removeAll();
		targetPane.add(comp);
	}

	/**
	 * Loads a controller with its default view
	 * @param ctrl
	 */
	public void loadController(UControllerInt ctrl)
	{
		this.ctrl = ctrl;
		loadView(ctrl.getDefaultView());
	}
	
	/**
	 * Setup the application
	 * 
	 * @param name
	 */
	protected void setupApp(String name)
	{
        window.setTitle(name);
	}
	
	/**
	 * Get currently logged in user
	 * @return
	 */
	public UserAccount getUser() {
		return user;
	}
	
	public void setUser(UserAccount usr) {
		user = usr;
	}

	/**
	 * Login the user
	 * @param usr
	 */
	public void userLogin(UserAccount usr)
	{
		user = usr;
		user.setLastLoginHost(ApplicationInstance.getHostName());
		
		if (user.isPasswordExpired())
			handleExpiredPassword();
	}
	
	abstract public void authorizeUser(UserAccount usr, UStackPermissionEnum perm) throws AuthorizationException;
	
	protected void handleForgotPassword(String userName, String uid)
	{
		try {
			UserAccount user = UserAccount.getUser(userName);
			if (user == null)
				throw new InvalidAccessAttempt();
			
			user.isValidForgotPassword(uid);
			userLogin(user);
			handleForgotPassword();
			
		} catch (ForgotPasswordLinkExpired linkErr) {
			handleForgotPasswordError("Expired");
		} catch (InvalidAccessAttempt linkErr) {
			handleForgotPasswordError("Invalid");
		}		
	}
	
	abstract public void handleExpiredPassword();
	abstract public void handleForgotPasswordError(String reason);
	abstract public void handleForgotPassword();

	private String webAppSessionId;
	public void setupSession(String appName) {
		
		WebAppSession session = WebAppSession.createSession(appName, this);
		if (session != null)
			webAppSessionId = session.getId();
	}
	
	/**
	 * Logout the user
	 */
	public void userLogout()
	{
		if (webAppSessionId != null)
			WebAppSession.endSessionById(webAppSessionId, "User Logged Out", getUserName());
		else
			WebAppSession.endSession(this, "User Logged Out", getUserName());

		user = null;
	}

	/**
	 * Attempt to login the user
	 * @param src
	 */
	public void login(Component src)
	{
		UserPassEntryInt upe = (UserPassEntryInt)src;
		
		try {
			
			if (upe.getUserName().length() == 0)
				throw new AuthExceptionUserPasswordMismatch();
			
			UserAccount user = Authentication.authenticateUser(upe.getUserName(), upe.getPassword());
			authorizeUser(user, UBasePermission.Login);
			userLogin(user);

		} catch (AuthenticationException err) {
			upe.setErrorList(UEntryError.getTopLevelError(err.getMessage()));
			logger.warn("User failed to login : " + upe.getUserName() + " => " + err);
		} catch (AuthorizationException err) {
			upe.setErrorList(UEntryError.getTopLevelError(err.getMessage()));
			logger.error("User failed to login : " + upe.getUserName() + " => " + err);
		}
		
	}
	
	public void pageLoaded() {}
	
	/**
	 * Reflection Action Performeds
	 * 
	 * @param e
	 */
	@SuppressWarnings("rawtypes")
	public void actionPerformed(ActionEvent e) {

		boolean found = false;
		try {
			
			Class[] partypes = new Class[]{Component.class};
			Method m = getClass().getMethod(e.getActionCommand(), partypes);
			
			Object[] arglist = new Object[]{ (Component)e.getSource() };
			m.invoke(this, arglist);
			found = true;
				
		} catch (NoSuchMethodException err) {
			//logger.warn(getClass().getName() + " -> No Method for '" + e.getActionCommand() + "(Component)'");
		} catch (Exception err) {
			logger.warn(getClass().getName() + " -> Error while executing '" + e.getActionCommand() + "(Component)'", err);
			found = true;
		}
		
		if (!found)
		{
			try {
				Class[] partypes = new Class[]{};
				Method m = getClass().getMethod(e.getActionCommand(), partypes);
				m.invoke(this);
				found = true;
			} catch (NoSuchMethodException err) {
				logger.warn(getClass().getName() + " -> No Method for '" + e.getActionCommand() + "()'", err);
			} catch (Exception err) {
				logger.warn(getClass().getName() + " -> Error while executing '" + e.getActionCommand() + "()'", err);
			}
		}		
		
	}
	
	public void registerPlugin(UPluginInt plugin) {} // not supported
	public Hashtable<String, Object> getIPC() { return null; } // not supported

}
