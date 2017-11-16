package com.untzuntz.ustack.main;

/**
 * Application configuration options
 * 
 * If adding to this please make sure to provide a valid and reasonable description of the value, also put a default in the Config.properties if necessary
 * 
 * @author jdanner
 *
 */
public class UAppCfg {

	private UAppCfg() {}

	/** Boolean flag to determine if we should try to pull data from memcache */
	public static final String CACHE_ENABLED_FLAG = "UAppCfg.CacheEnabled";
	
	/** String of the hostname + port information for memcache */
	public static final String CACHE_HOST_STRING = "UAppCfg.CacheHost";
	
	/** Default hostname for the mongodb cluster */
	public static final String MONGO_DB_HOST = "MongoDB.DefaultHost";
	public static final String MONGO_DB_CONN_STRING= "MongoDB.ConnectionURI";
	public static final String MONGO_DB_AUTH_DATABASE = "MongoDB.Auth.Database";
	public static final String MONGO_DB_AUTH_USERNAME = "MongoDB.Auth.UserName";
	public static final String MONGO_DB_AUTH_PASSWORD = "MongoDB.Auth.Password";
	
	/** true/false if reading from slaves is OK */
	public static final String MONGO_DB_READS_OK = "MongoDB.SlaveReadsOKFlag";
	
	/** true/false if the driver should use keepalives to keep the connection open through a firewall */
	public static final String MONGO_DB_KEEPALIVE = "MongoDB.KeepAlives";

	/** true/false if the driver should retry upon connection errors */
	public static final String MONGO_DB_AUTORETRY = "MongoDB.AutoRetry";
	
	/** Tells the driver how many connections to setup per host - default is currently 10 -- see http://api.mongodb.org/java/current/com/mongodb/MongoOptions.html#connectionsPerHost */
	public static final String MONGO_DB_CONNECTIONS_PER_HOST = "MongoDB.ConnectionsPerHost";
	
	/** Password Failure Error Limit - once reached the account will be locked */
	public static final String PASSWORD_ERROR_LIMIT = "UAppCfg.Password.ErrorLimit";
	
	/** Password minimum length */
	public static final String PASSWORD_MIN_LENGTH = "UAppCfg.Password.MinLength";

	/** Time in seconds for how long a user account is locked for when PASSWORD_ERROR_LIMIT is reached */
	public static final String USER_ACCOUNT_LOCKTIME_SEC = "UAppCfg.UserAccount.LockTimeSec";

	/** Number of days until a password is expired - this is set when the password is set */
	public static final String PASSWORD_EXPIRATION_IN_DAYS = "UAppCfg.Password.ExpirationDateInDays";

	/** Number of hours until a forgot password link is expired - this is set when the link is sent */
	public static final String PASSWORD_FORGOT_LINK_IN_HOURS = "UAppCfg.Password.ForgotLinkExpirationInHours";

	/** The name of the machine the application is currently running on */
	public static final String CURRENT_HOSTANME = "UAppCfg.Hostname";

	/** Name of the database in mongo db that stores the 'users' collections */
	public static final String DATABASE_USERS_COL = "Database.Users";
	
	/** Name of the database in mongo db that stores the 'sites' collections */
	public static final String DATABASE_SITES_COL = "Database.Sites";
	
	/** Name of the database in mongo db that stores the 'resources' collections */
	public static final String DATABASE_RESOURCE_COL = "Database.Resources";
	
	/** Name of the database in mongo db that stores the 'tos' collections */
	public static final String DATABASE_TOS_COL = "Database.TOS";
	
	/** Name of the database in mongo db that stores the 'notification' collections */
	public static final String DATABASE_NOTIFICATION_COL = "Database.Notification";
	
	/** Name of the database in mongo db that stores the 'notificationTempl' collections */
	public static final String DATABASE_NOTIFICATION_TEMPL_COL = "Database.NotificationTempl";
	
	/** Name of the database in mongo db that stores the 'addressBook' collections */
	public static final String DATABASE_ADDRBOOK_COL = "Database.AddressBook";
	
	/** Name of the database in mongo db that stores the 'apiLogs' collections */
	public static final String DATABASE_APILOG_COL = "Database.APILog";

	public static final String DATABASE_IN_THE_FUTURE_COL = "Database.InTheFuture";
	
	public static final String EMAIL_MODE = "Email.Mode";
	
	public static final String MAILGUN_KEY = "MailGun.APIKey";
	
	public static final String MAILGUN_DOMAIN = "MailGun.Domain";
	
	/** Enable SSL Connection */
	public static final String EMAIL_SECURE_SSL = "Email.Secure";
	
	/** Enable TLS Email Sending */
	public static final String EMAIL_TLS = "Email.TLS";
	
	/** Hostname or IP of the SMTP server */
	public static final String EMAIL_HOST = "Email.Host";
	
	/** TCP Port to use for the SMTP server */
	public static final String EMAIL_PORT = "Email.TCPPort";
	
	/** SMTP Authentication */
	public static final String EMAIL_AUTH = "Email.Authentication";
	
	/** SMTP Authentication Username */
	public static final String EMAIL_AUTH_USER = "Email.Authentication.Username";
	
	/** SMTP Authentication Password */
	public static final String EMAIL_AUTH_PASS = "Email.Authentication.Password";
	
	/** Location to store temporary file */
	public static final String DIRECTORY_SCRATCH = "Directory.Scratch";

	/** Name of the database in mongo db that stores the 'creditAccounts' collections */
	public static final String DATABASE_CREDITACCT_COL = "Database.CreditAccounts";

	/** Name of the database in mongo db that stores the 'accountTransactions' collections */
	public static final String DATABASE_ACCTRANS_COL = "Database.AccountTransactions";

	/** Name of the database in mongo db that stores the 'products' collections */
	public static final String DATABASE_PRODUCT_COL = "Database.Products";

	/** Name of the database in mongo db that stores the 'products' collections */
	public static final String DATABASE_OBJ_TEMPL_COL = "Database.ObjectTemplates";
	
	/** Encrypted login id for AuthorizeNET Sandbox Testing */
	public static final String AUTHNET_SANDBOX_LOGINID = "AuthorizeNet.Sandbox.LoginId";
	
	/** Encrypted Transaction key for AuthorizeNET Sandbox Testing */
	public static final String AUTHNET_SANDBOX_TRANSACTIONKEY = "AuthorizeNet.Sandbox.TransactionKey";

	/** Encrypted login id for AuthorizeNET Sandbox Testing */
	public static final String AUTHNET_PROD_LOGINID = "AuthorizeNet.Production.LoginId";
	
	/** Encrypted Transaction key for AuthorizeNET Sandbox Testing */
	public static final String AUTHNET_PROD_TRANSACTIONKEY = "AuthorizeNet.Production.TransactionKey";

	public static final String CREDIT_REFRESH_DEF_PRODUCT_ID = "CreditAccounts.Default.RefreshProductId";
	
}
