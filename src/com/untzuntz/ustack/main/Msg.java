package com.untzuntz.ustack.main;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;


public class Msg {

	private static final long serialVersionUID = 2865734405614972072L;
	
    /**
     * A map which contains <code>DateFormat</code> objects for various 
     * locales.
     */
    private static final Map DATE_FORMAT_MEDIUM_MAP = new HashMap();
    private static final Map DATE_FORMAT_SHORT_MAP = new HashMap();
    
    /**
     * Formats a date with the specified locale. (MEDIUM format)
     * 
     * @param date the date to be formatted.
     * @return a localized String representation of the date
     */
    public static final String formatDateTimeMedium(Date date) {

    	Locale locale = ApplicationInstance.getActive().getLocale();
        DateFormat df = (DateFormat) DATE_FORMAT_MEDIUM_MAP.get(locale);
        if (df == null) {
            df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, locale);
            DATE_FORMAT_MEDIUM_MAP.put(locale, df);
        }
        return date == null ? Msg.getString("NotSet") : df.format(date);
        
    }

    /**
     * Formats a date with the specified locale. (SHORT format)
     * 
     * @param date the date to be formatted
     * @return a localized String representation of the date
     */
    public static final String formatDateTimeShort(Date date) {

    	Locale locale = ApplicationInstance.getActive().getLocale();
        DateFormat df = (DateFormat) DATE_FORMAT_SHORT_MAP.get(locale);
        if (df == null) {
            df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, locale);
            DATE_FORMAT_SHORT_MAP.put(locale, df);
        }
        return date == null ? Msg.getString("NotSet") : df.format(date);
        
    }
    
    /**
     * Returns a localized formatted message.  This method conveniently wraps
     * a call to a MessageFormat object.
     * 
     * @param key the key of the message to be returned
     * @param arguments an array of arguments to be inserted into the message
     */
    public static String getString(String key, Object ... arguments) {
    	
        Locale locale = null;
        try { locale = ApplicationInstance.getActive().getLocale(); } catch (Exception err) { locale = Locale.US; }
        
        String template = getString(key);
        
        MessageFormat messageFormat = new MessageFormat(template);
        messageFormat.setLocale(locale);
        return messageFormat.format(arguments, new StringBuffer(), null).toString();
        
    }
    
    /**
     * Returns localized text.
     * 
     * @param key the key of the text to be returned
     * @return the appropriate localized text (if the key is not defined, 
     *         the string "!key!" is returned)
     */
    public static String getString(String key) {

        Locale locale = null;
        try { locale = ApplicationInstance.getActive().getLocale(); } catch (Exception err) { locale = Locale.US; }

        List<String> bundles = ApplicationInstance.getMessageBundles();
        
        for (String bundleName : bundles)
        {
            try {
                ResourceBundle resource = ResourceBundle.getBundle(bundleName, locale);
                return resource.getString(key);
            } catch (MissingResourceException e) {}
        }
        
        return "!" + key + "!";
    }

    /** Non-instantiable class. */
    private Msg() { }

}
