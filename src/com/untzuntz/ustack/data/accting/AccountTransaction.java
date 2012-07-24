package com.untzuntz.ustack.data.accting;

import java.util.Date;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.untzuntz.ustack.data.UntzDBObject;
import com.untzuntz.ustack.main.ApplicationInstance;
import com.untzuntz.ustack.main.UAppCfg;
import com.untzuntz.ustack.main.UOpts;

public class AccountTransaction extends UntzDBObject {
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(AccountTransaction.class);

	public String getCollectionName() { return "accountTransactions"; }
	
	public AccountTransaction(String createdBy, String productId)
	{
		put("created", new Date());
		put("createdBy", createdBy);
		put("productId", productId);
		
		loadProduct(Product.getByProductId(productId), productId);
	}
	
	public AccountTransaction(String createdBy, Product prod)
	{
		put("created", new Date());
		put("createdBy", createdBy);
		loadProduct(prod, null);
	}
	
	private void loadProduct(Product prod, String productId)
	{
		if (prod == null)
		{
			logger.error("Invalid Product Setup ::: product id [" + productId + "]");
			put("invalidProduct", true);
			return;
		}
		
		put("productId", prod.getProductId());
		put("productDesc", prod.getDescription());
		put("credits", prod.getCreditAmount());
		
		if (prod.get("customPrice") != null)
			put("price", prod.getInt("customPrice"));
		else
			put("price", prod.getDefaultPrice());
	}

	public int getCredits() {
		if (get("credits") != null)
			return getInt("credits");
		
		return 0;
	}
	
	public int getPrice() {
		if (get("price") != null)
			return getInt("price");
		
		return 0;
	}
	
	public void setName(String name)
	{
		put("name", name);
	}
	
	public String getName() 
	{ 
		return getString("name");
	}
	
	public void setDescription(String description)
	{
		put("description", description);
	}
	
	public String getDescription() 
	{ 
		return getString("description");
	}
	
	private AccountTransaction()
	{
		// setup basic values on account
		put("created", new Date());
	}

	public String getTransactionId() {
		return get("_id") + "";
	}

	/** Gets the DB Collection for the AccountTransaction object */
	public static DBCollection getDBCollection() {
		return new AccountTransaction().getCollection();
	}

	/** Return the name of the database that houses the 'accountTransactions' collection */
	public static final String getDatabaseName() {
		
		if (UOpts.getString(UAppCfg.DATABASE_ACCTRANS_COL) != null)
			return UOpts.getString(UAppCfg.DATABASE_ACCTRANS_COL);
		
		return ApplicationInstance.getAppName();
		
	}

	/**
	 * Generate a AccountTransaction object from the MongoDB object
	 * @param user
	 */
	public AccountTransaction(DBObject acct) {
		super(acct);
	}

	/**
	 * Get a transaction by name
	 * 
	 * @param name
	 * @return
	 */
	public static AccountTransaction getTransaction(String name)
	{
		if (name == null || name.length() == 0)
			return null;
		
		name = name.toLowerCase();
		
		DBObject acct = null;
		try {
			acct = new AccountTransaction().getCollection().findOne(BasicDBObjectBuilder.start("name", name).get());
		} catch (Exception exp) { 
			return null;
		}
		
		if (acct == null)
			return null;
		
		return new AccountTransaction(acct);
	}
	
	/**
	 * Get a transaction by name and since date
	 * 
	 * @param name
	 * @return
	 */
	public static AccountTransaction getTransaction(String name, Date since)
	{
		if (name == null || name.length() == 0)
			return null;
		
		name = name.toLowerCase();
		
		DBObject find = BasicDBObjectBuilder.start("name", name).append("created", new BasicDBObject("$gt", since)).get();
		DBObject acct = null;
		try {
			acct = new AccountTransaction().getCollection().findOne(find);
		} catch (Exception exp) { 
			return null;
		}
		
		if (acct == null)
			return null;
		
		return new AccountTransaction(acct);
	}
	
	/**
	 * Get a transaction by uid
	 * 
	 * @param uid
	 * @return
	 */
	public static AccountTransaction getAccountById(String uid)
	{
		if (uid == null)
			return null;
		
		DBObject acct = new AccountTransaction().getCollection().findOne(BasicDBObjectBuilder.start("_id", new ObjectId(uid)).get());
		
		if (acct == null)
			return null;
		
		return new AccountTransaction(acct);
	}
	

	
}
