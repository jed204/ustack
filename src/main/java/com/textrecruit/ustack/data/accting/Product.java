package com.textrecruit.ustack.data.accting;

import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.textrecruit.ustack.data.UntzDBObject;
import com.textrecruit.ustack.exceptions.AccountExistsException;
import com.textrecruit.ustack.exceptions.InvalidUserAccountName;
import com.textrecruit.ustack.exceptions.PasswordException;
import com.textrecruit.ustack.exceptions.PasswordLengthException;
import com.textrecruit.ustack.main.Msg;
import com.textrecruit.ustack.main.UAppCfg;
import com.textrecruit.ustack.main.UOpts;
import org.apache.log4j.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class Product extends UntzDBObject {
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(Product.class);

	public String getCollectionName() { return "products"; }
	
	private Product()
	{
		// setup basic values on account
		put("created", new Date());
	}
	
	private void setProductId(String id)
	{
		put("productId", id);
	}

	public String getProductUid() {
		return get("_id") + "";
	}
	
	public String toString()
	{
		return getString("productId") + " - " + getDescription();
	}

	/** Gets the DB Collection for the Product object */
	public static DBCollection getDBCollection() {
		return new Product().getCollection();
	}

	/** Return the name of the database that houses the 'products' collection */
	public static final String getDatabaseName() {
		
		if (UOpts.getString(UAppCfg.DATABASE_PRODUCT_COL) != null)
			return UOpts.getString(UAppCfg.DATABASE_PRODUCT_COL);
		
		return UOpts.getAppName();
		
	}

	/**
	 * Generate a Product object from the MongoDB object
	 * @param user
	 */
	public Product(DBObject acct) {
		super(acct);
	}
	
	public String getProductId()
	{
		return getString("productId");
	}
	
	public void setDescription(String desc)
	{
		put("description", desc);
	}
	
	public String getDescription()
	{
		return getString("description");
	}
	
	public boolean isGeneric()
	{
		return "true".equalsIgnoreCase(getString("genericProduct"));
	}
	
	public void setGeneric(boolean flag)
	{
		if (flag)
			put("genericProduct", "true");
		else
			removeField("genericProduct");
	}

	/** Sets the cost of the item in credits */
	public void setCreditAmount(Integer credits)
	{
		if (credits != null)
			put("creditAmount", credits);
		else
			removeField("creditAmount");
	}
	
	public int getCreditAmount()
	{
		if (get("creditAmount") != null)
			return getInt("creditAmount");
		
		return 0;
	}
	

	/** Sets the cost of the item in credits */
	public void setDefaultPrice(Integer price)
	{
		if (price != null)
			put("defaultPrice", price);
		else
			removeField("defaultPrice");
	}
	
	public int getDefaultPrice()
	{
		if (get("defaultPrice") != null)
			return getInt("defaultPrice");
		
		return 0;
	}
	
	/**
	 * Get a product by productId
	 * 
	 * @param name
	 * @return
	 */
	public static Product getByProductId(String productId)
	{
		if (productId == null || productId.length() == 0)
			return null;
		
		productId = productId.toUpperCase();
		
		DBObject prod = null;
		try {
			prod = new Product().getCollection().findOne(BasicDBObjectBuilder.start("productId", productId).get());
		} catch (Exception exp) { 
			return null;
		}
		
		if (prod == null)
			return null;
		
		return new Product(prod);
	}
	
	/** Returns a list of product that either cost money or credits */
	public static List<Product> getCostProductList()
	{
		List<Product> prods = getProductList();
		for (int i = 0; i < prods.size(); i++)
		{
			Product prod  = prods.get(i);
			if (prod.getCreditAmount() > 0 || prod.getDefaultPrice() < 0)
			{
				prods.remove(i);
				i--;
			}
		}
		return prods;
	}

	/** Returns a list of product that either cost money or credits */
	public static List<Product> getAddCreditProductList()
	{
		List<Product> prods = getProductList();
		for (int i = 0; i < prods.size(); i++)
		{
			Product prod  = prods.get(i);
			if (prod.getCreditAmount() <= 0)
			{
				prods.remove(i);
				i--;
			}
		}
		return prods;
	}

	/** Returns a list of products */
	public static List<Product> getProductList()
	{
		List<Product> ret = new Vector<Product>();
		
		BasicDBObject sorter = new BasicDBObject();
		
		sorter.put("creditAmount", 1);

		DBCollection col = new Product().getCollection();
		DBCursor cur = col.find().sort(sorter);
		
		while (cur.hasNext())
			ret.add(new Product(cur.next()));
		
		return ret;
	}

	/** Returns a list of 'generic' products */
	public static List<Product> getGenericProductList()
	{
		List<Product> ret = new Vector<Product>();
		
		BasicDBObject sorter = new BasicDBObject();
		
		sorter.put("creditAmount", 1);

		DBCollection col = new Product().getCollection();
		DBCursor cur = col.find(new BasicDBObject("genericProduct", "true")).sort(sorter);
		
		while (cur.hasNext())
			ret.add(new Product(cur.next()));
		
		return ret;
	}

	/**
	 * Create a new product
	 * 
	 * @param productId
	 * @return
	 * @throws AccountExistsException
	 * @throws PasswordLengthException
	 */
	public static Product createProduct(String productId, String desc) throws AccountExistsException, PasswordException
	{
		if (productId == null || productId.length() == 0)
			throw new InvalidUserAccountName(Msg.getString("Invalid-ProductId"));
		
		Product prod = getByProductId(productId);
		if (prod != null) // already exists
			throw new AccountExistsException("ProductId");
		
		// create the actual product
		prod = new Product();
		prod.setProductId(productId);
		prod.setDescription(desc);
		logger.info("Creating new product '" + productId + "'");
		
		return prod;
	}

}
