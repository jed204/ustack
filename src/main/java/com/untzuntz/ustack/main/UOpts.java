package com.untzuntz.ustack.main;

import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.untzuntz.ustack.data.UDBConfigItem;
import com.untzuntz.ustack.data.UDataCache;

/**
 * Manages configuration data and the cache (if enabled)
 * 
 * @author jdanner
 *
 */
public class UOpts {

	private static Logger logger = Logger.getLogger(UOpts.class);

	public static final String SUBSYS_SETUP = "SubSys.Setup";
	/** Authentication Subsystem Text */
	public static final String SUBSYS_AUTH = "SubSys.Authentication";

	/**
	 * List of text resources, can be added by extending application
	 */
	private static final List<String> messageBundles = new Vector<String>();
	
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
	 * Gets the list of messages bundles. Used internally.
	 * 
	 * @return
	 */
	public static List<String> getMessageBundles()
	{
		return messageBundles;
	}
	

	/** The appName configured via web.xml */
	private static String appName;

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
	}

	/** The cacheFlag configured via web.xml */
	private static boolean cacheFlag;
	
	/**
	 * List of configuration properties to use as defaults values
	 */
	private static final List<String> configurations = new Vector<String>();

	public static void setCacheFlag(boolean cf) {
		cacheFlag = cf;
	}
	
	public static boolean getCacheEnabled() {
		return cacheFlag;
	}
	
	public static void addConfigurationFile(String filePath)
	{
		logger.info("Configuration Added [" + filePath + "]");
		configurations.add(filePath);
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
	

	/**
	 * Returns the values as a long, defaults to 0 if not found
	 * 
	 * @param propertyName
	 * @return
	 */
	public static long getLong(String propertyName)
	{
		long retVal = 0;

		String tmpStr = getProperty(propertyName);

		try {
			retVal = Long.valueOf(tmpStr).longValue();
		} catch (Exception err) { }

		return retVal;
	}
	
	/**
	 * Returns the value as an int, defaults to 0 if not found
	 * 
	 * @param propertyName
	 * @return
	 */
	public static int getInt(String propertyName)
	{
		int retVal = 0;
		String tmpStr = getProperty(propertyName);

		try {
			retVal = Integer.valueOf(tmpStr).intValue();
		} catch (Exception err) { }

		return retVal;
	}
	
	/**
	 * Returns a boolean value of the property. If the property does not exist, false is returned.
	 * 
	 * @param propertyName
	 * @return
	 */
	public static boolean getBool(String propertyName)
	{
		String retVal = getProperty(propertyName);
		
		if (retVal != null && retVal.toLowerCase().startsWith("t"))
			return true;

		return false;
	}

	/**
	 * Returns a directory path
	 * 
	 * @param propertyName
	 * @return
	 */
	public static UFile getDirectory(String propertyName)
	{
		String retVal = getProperty(propertyName);
		
		if (retVal != null && !retVal.endsWith("/"))
			retVal += "/";
		
		return new UFile(retVal, true);
	}

	/**
	 * Returns the property value. If it cannot find a value it will return null;
	 * 
	 * @param propertyName
	 * @return
	 */
	public static String getString(String propertyName)
	{
		String retVal = getProperty(propertyName);
		return retVal;
	}

	/**
	 * Returns the property value. If it cannot find a system configured value it will return the default (if available)
	 * 
	 * @param propertyName
	 * @return property value
	 */
	protected static String getProperty(String propertyName)
	{
		// Get current system property
		String retVal = System.getProperty(propertyName);
		if (retVal != null)
			return retVal;

		// if the cache is enabled, check there
		if (cacheFlag)
		{
			retVal = getCacheValue(propertyName);
			if (retVal != null)
				return retVal;
		}
		
		// Check database for override
		if (!propertyName.startsWith("MongoDB."))
		{
			retVal = getMongoProperty(propertyName);
			if (retVal != null)
			{
				if (cacheFlag)
					updateCacheValue(propertyName, retVal);
				
				return retVal;
			}
		}
		
		// Check for a default value in the application
		List<String> configs = getConfigurationFiles();
		for (String cfg : configs)
		{
			retVal = getProperty(propertyName, ResourceBundle.getBundle(cfg));
			if (retVal != null)
			{
				if (cacheFlag)
					updateCacheValue(propertyName, retVal);
				
				logger.debug("Lookup value [" + propertyName + "] in '" + cfg + "' ==> '" + retVal + "'");
				return retVal;
			}
		}
		
		return null;
	}

	/**
	 * Updates the cache with the provided value
	 * 
	 * @param propertyName
	 * @param value
	 */
	protected static void updateCacheValue(String propertyName, String value)
	{
		UDataCache.getInstance().set(propertyName, 300, value);
	}

	/**
	 * Attempts to get the value from the cache
	 * 
	 * @param propertyName
	 * @return
	 */
	protected static String getCacheValue(String propertyName)
	{
		return (String)UDataCache.getInstance().get(propertyName);
	}
	
	/**
	 * Attempts to retrieve the value from MongoDB
	 * 
	 * @param propertyName
	 * @return
	 */
	protected static String getMongoProperty(String propertyName)
	{
		UDBConfigItem cfgItem = UDBConfigItem.getByPropertyName(propertyName);
		if (cfgItem == null)
			return null;
		
		return cfgItem.getString("value");
	}

	/**
	 * Gets a property from a resource bundle
	 * 
	 * @param propertyName
	 * @param resource
	 * @return
	 */
	protected static String getProperty(String propertyName, ResourceBundle resource)
	{
		String retVal = null;
		
        try {
        	retVal = resource.getString(propertyName);
        } catch (MissingResourceException e) {}
        
        return retVal;
	}
	

}
