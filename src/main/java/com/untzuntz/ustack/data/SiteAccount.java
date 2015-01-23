package com.untzuntz.ustack.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

import com.Ostermiller.util.CSVParser;
import com.Ostermiller.util.LabeledCSVParser;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.untzuntz.ustack.aaa.ResourceDefinition;
import com.untzuntz.ustack.aaa.ResourceLink;
import com.untzuntz.ustack.exceptions.AccountExistsException;
import com.untzuntz.ustack.exceptions.InvalidSiteAccountName;
import com.untzuntz.ustack.exceptions.PasswordLengthException;
import com.untzuntz.ustack.main.UAppCfg;
import com.untzuntz.ustack.main.UOpts;

/**
 * A 'Site'
 * 
 * @author jdanner
 *
 */
public class SiteAccount extends UntzDBObject {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(SiteAccount.class);
	
	public static final String STATUS_DISABLED = "Disabled";
	public static final String STATUS_ACTIVE = "Active";
	
	public String getCollectionName() { return "sites"; }
	
	public static DBCollection getDBCollection() {
		return new SiteAccount().getCollection();
	}
	
	public static SiteAccount getTestObject() {
		
		SiteAccount u = new SiteAccount();
		
		u.setSiteName("Test Site");
		u.setAddress1("123 Main St.");
		u.setAddress2("Suite 123");
		u.setCity("San Francisco");
		u.setState("California");
		u.setPostalCode("98401");
		u.setCountry("United States");
		u.setTimeZone("Usa/Pacific");
		u.setPrimaryTelephone(new BasicDBObject("countryCode", "1").append("phoneNumber", "333-444-5555"));
		
		return u;
		
	}

	private SiteAccount()
	{
		// setup basic values on account
		put("created", new Date());
	}
	
	public String getSiteId() {
		return get("_id") + "";
	}

	/** Return the name of the database that houses the 'users' collection */
	public static final String getDatabaseName() {
		
		if (UOpts.getString(UAppCfg.DATABASE_SITES_COL) != null)
			return UOpts.getString(UAppCfg.DATABASE_SITES_COL);
		
		return UOpts.getAppName();
		
	}

	/**
	 * Generate a SiteAccount object from the MongoDB object
	 * @param user
	 */
	public SiteAccount(DBObject site) {
		super(site);
	}
	
	/** Rename the site - this runs a check to verify the new name isn't in use */
	public void renameSite(String siteName) throws AccountExistsException,InvalidSiteAccountName
	{
		if (siteName == null || siteName.length() == 0)
			throw new InvalidSiteAccountName("Site");
		
		SiteAccount site = getSite(siteName);
		if (site != null) // already exists
			throw new AccountExistsException("Site");

		put("siteName", siteName);
	}

	/** Internal set site's name */
	private void setSiteName(String siteName)
	{
		put("siteName", siteName);
	}

	/** Return the site name */
	public String getSiteName()
	{
		return (String)get("siteName");
	}
	
	public void setPrimaryTelephone(DBObject phone)
	{
		if (phone == null)
			removeField("primaryTelephone");
		else
			put("primaryTelephone", phone);
	}
	
	public DBObject getPrimaryTelephone() {
		return (DBObject)get("primaryTelephone");
	}
	
	public String getPrimaryTelephoneString()
	{
		DBObject phone = getPrimaryTelephone();
		if (phone == null)
			return "";
		return (String)phone.get("countryCode") + " " + (String)phone.get("phoneNumber");
	}
	
	public void setFaxNumber(DBObject phone)
	{
		if (phone == null)
			removeField("faxNumber");
		else
			put("faxNumber", phone);
	}
	
	public DBObject getFaxNumber() {
		return (DBObject)get("faxNumber");
	}
	
	public String getFaxNumberString()
	{
		DBObject phone = getFaxNumber();
		if (phone == null)
			return "";
		return (String)phone.get("countryCode") + " " + (String)phone.get("phoneNumber");
	}
	
	/**
	 * Sets the address book value for this object
	 * @param book
	 */
	public void setAddressBook(AddressBook book)
	{
		if (book == null)
			removeField("addrBookId");
		else
			put("addrBookId", book.getAddressBookId());
	}

	/**
	 * Returns the address book for this object
	 * @return
	 */
	public AddressBook getAddressBook()
	{
		AddressBook ret = null;
		String id = getString("addrBookId");
		
		if (id == null)
		{
			try {
				ret = AddressBook.getBySiteId(getSiteId());
				ret.save();
				setAddressBook(ret);
				save();
			} catch (Exception er) {
				logger.error("General failure while trying to create an address book", er);
			}
		}
		else
			ret = AddressBook.getById( id );
		
		return ret;
	}
	
	public String getSuffix()
	{
		return getString("suffix");
	}
	
	public String getTimeZone()
	{
		return getString("timeZone");
	}
	
	public void setTimeZone(String tz)
	{
		if (tz != null && tz.length() > 0)
			put("timeZone", tz);
		else
			removeField("timeZone");
	}
	
	public boolean isPayOnReceive()
	{
		if ("true".equalsIgnoreCase(getString("payOnReceive")))
			return true;
		
		return false;
	}
	
	public void setPayOnReceive(boolean pay)
	{
		put("payOnReceive", pay);
	}
	
	public String getCreditAccount()
	{
		return getString("creditAccountId");
	}
	
	public void setCreditAccount(String c)
	{
		if (c != null && c.length() > 0)
			put("creditAccountId", c);
		else
			removeField("creditAccountId");
	}

	public String getCountry()
	{
		return getString("country");
	}
	
	public void setCountry(String c)
	{
		if (c != null && c.length() > 0)
			put("country", c);
		else
			removeField("country");
	}
	
	public String getAddressString() {
		
		String a1 = getAddress1();
		if (getAddress2() != null && getAddress2().length() > 0)
			a1 += " - " + getAddress2();
		
		return a1;
		
	}
	
	public String getAddress1()
	{
		return getString("address1");
	}
	
	public void setAddress1(String data)
	{
		if (data != null && data.length() > 0)
			put("address1", data);
		else
			removeField("address1");
	}
	
	public String getAddress2()
	{
		return getString("address2");
	}
	
	public void setAddress2(String data)
	{
		if (data != null && data.length() > 0)
			put("address2", data);
		else
			removeField("address2");
	}
	
	public String getCity()
	{
		return getString("city");
	}
	
	public void setCity(String data)
	{
		if (data != null && data.length() > 0)
			put("city", data);
		else
			removeField("city");
	}
	
	public String getState()
	{
		return getString("state");
	}
	
	public void setState(String data)
	{
		if (data != null && data.length() > 0)
			put("state", data);
		else
			removeField("state");
	}

	public String getPostalCode()
	{
		return getString("postalCode");
	}
	
	public void setPostalCode(String data)
	{
		if (data != null && data.length() > 0)
			put("postalCode", data);
		else
			removeField("postalCode");
	}

	/**
	 * Returns the total number of user accounts
	 * 
	 * @return
	 */
	public static long getAccountCount()
	{
		return MongoDB.getCollection(getDatabaseName(), "sites").count();
	}
	
	/**
	 * Determines if the site account is disabled by the site account status
	 * @return
	 */
	public boolean isDisabled()
	{
		String status = getStatus();
		
		if (STATUS_DISABLED.equalsIgnoreCase(status))
			return true;
		
		return false;
	}

	/**
	 * Returns the current site status
	 * 
	 * @return
	 */
	public String getStatus()
	{
		String status = (String)get("status");
		if (status == null)
			return STATUS_ACTIVE;
		
		return status;
	}

	/** Resets and rebuilds the manage and can manage lists */
	protected void calculateManageLists()
	{
		setManagedByList(new BasicDBList());
		
		BasicDBList resourceLinkList = getResourceLinkList();
		for (int q = 0; q < resourceLinkList.size(); q++)
		{
			ResourceLink resourceLink = new ResourceLink( (DBObject)resourceLinkList.get(q) );
			if (resourceLink == null || resourceLink.getResourceDefinition() == null)
				continue;
			
			BasicDBList managedByList = resourceLink.getResourceDefinition().getManagedByList();
			for (int i = 0; i < managedByList.size(); i++)
				addManagedBy( (String)managedByList.get(i) );
			
			BasicDBList resLinkMgBy = (BasicDBList)resourceLink.get("managedBy");
			for (int i = 0; resLinkMgBy != null && i < resLinkMgBy.size(); i++)
			{
				String mgmtBy = (String)resLinkMgBy.get(i);
				addManagedBy( mgmtBy );
			}
		}
	}

	public static SiteAccount createSite(String siteName) throws AccountExistsException,InvalidSiteAccountName
	{
		return createSite(siteName, true);
	}
	
	/**
	 * Create a new site account
	 * 
	 * @param userName
	 * @param password
	 * @return
	 * @throws AccountExistsException
	 * @throws PasswordLengthException
	 */
	public static SiteAccount createSite(String siteName, boolean checkForDupe) throws AccountExistsException,InvalidSiteAccountName
	{
		if (siteName == null || siteName.length() == 0)
			throw new InvalidSiteAccountName("Site");
		
		SiteAccount site = null;
		
		if (checkForDupe)
			site = getSite(siteName);
		
		if (site != null) // already exists
			throw new AccountExistsException("Site");
		
		// create the actual account
		site = new SiteAccount();
		site.setSiteName(siteName);
		logger.info("Creating site account '" + siteName + "'");
		
		return site;
	}
	
	public static List<SiteAccount> getAll()
	{
		List<SiteAccount> ret = new Vector<SiteAccount>();
		
		DBCursor cur = new SiteAccount().getCollection().find();
		while (cur.hasNext())
			ret.add(new SiteAccount(cur.next()));
		
		return ret;
	}

	/**
	 * Get a site account by name
	 * 
	 * @param userName
	 * @return
	 */
	public static SiteAccount getSite(String siteName)
	{
		DBObject site = new SiteAccount().getCollection().findOne(BasicDBObjectBuilder.start("siteName", siteName).get());
		
		if (site == null)
			return null;
		
		return new SiteAccount(site);
	}
	
	/**
	 * Get a site account by id
	 * 
	 * @param id
	 * @return
	 */
	public static SiteAccount getSiteById(String id)
	{
		if (id == null)
			return null;

		try {
			
			DBObject site = new SiteAccount().getCollection().findOne(BasicDBObjectBuilder.start("_id", new ObjectId(id)).get());
			
			if (site == null)
				return null;
			
			return new SiteAccount(site);
			
		} catch (IllegalArgumentException iae) {}
		return null;
	}
	
	public static SiteAccount getByField(String field, String value)
	{
		DBObject search = BasicDBObjectBuilder.start(field, value).get();
		DBObject site = new SiteAccount().getCollection().findOne(search);
		
		if (site == null)
			return null;
		
		return new SiteAccount(site);
	}
	
	/**
	 * Import Data
	 * 
	 * @param col
	 * @param in
	 * @throws IOException
	 */
	public static int importData(DBCollection col, InputStream in, String userName) throws IOException
	{
		LabeledCSVParser myParser = new LabeledCSVParser( new CSVParser(in) );

		Country usa = Country.getCountryByName("United States");
		Hashtable<String,String> found = new Hashtable<String,String>();
		Hashtable<String,ResourceDefinition> defs = new Hashtable<String,ResourceDefinition>();
		
		int cnt = 0;
		SiteAccount site = null;
		while(myParser.getLine() != null)
		{
			logger.info("Importing " +  cnt + "...");
			
			String pk = myParser.getValueByLabel("primaryKey");
			if (pk == null || pk.length() == 0)
				continue;

			if (found.get(pk) != null)
				continue;

			String siteName = myParser.getValueByLabel("siteName");
			site = null;
			DBObject obj = col.findOne(BasicDBObjectBuilder.start("primaryKey", pk).get());
			if (obj != null)
				site = new SiteAccount(obj);
			if (site == null)
			{
				try {
					site = SiteAccount.createSite(siteName); 
				} catch (Exception er) { }
				
				if (site == null)
				{
					// try to create the site with a - Street Name in it
					String addr1 = myParser.getValueByLabel("address1");
					if (addr1 != null && addr1.length() > 0)
					{
						if (addr1.indexOf(" ") > -1)
							addr1 = addr1.substring( addr1.indexOf(" ") + 1 );
	
						try{
							siteName = siteName + " - " + addr1;
							site = SiteAccount.createSite(siteName); 
						} catch (Exception er) { 
							logger.warn("Could not create site: '" + siteName + "'"); 
						}
					}
				}
				
				// we failed --- add an index
				if (site == null)
				{
					int idx = 1;
					while (site == null && idx < 20)
					{
						try { 
							if (idx > 1)
								site = SiteAccount.createSite(siteName + " " + idx); 
							else
								site = SiteAccount.createSite(siteName); 
						} catch (Exception er) { 
							logger.warn("Could not create site: '" + siteName + "'"); 
							idx++;
						}
					}
				}
			}
			
			if (site == null)
				continue;
			
			String[] labels = myParser.getLabels();
			for (String hdr : labels)
			{
				String val = myParser.getValueByLabel(hdr);
				if (val != null)
				{
					if ("primaryTelephone".equalsIgnoreCase(hdr))
					{
						if (val.length() > 0)
							site.setPrimaryTelephone(UntzDBObject.getPhoneObject("1", val));
					}
					else if ("faxNumber".equalsIgnoreCase(hdr))
					{
						if (val.length() > 0)
							site.setFaxNumber(UntzDBObject.getPhoneObject("1", val));
					}
					else if ("resourceRole".equalsIgnoreCase(hdr)) {}
					else if ("siteName".equalsIgnoreCase(hdr)) {}
					else if ("resourceName".equalsIgnoreCase(hdr))
					{
						ResourceDefinition def = defs.get(val);
						if (def == null)
							def = ResourceDefinition.getByName(val);

						ResourceLink link = new ResourceLink(def, myParser.getValueByLabel("resourceRole"));
						site.addResourceLink(userName, link);
					}
					else if ("stateAbbrev".equalsIgnoreCase(hdr))
					{
						if (site.get("state") == null)
						{
							if ("United States".equalsIgnoreCase(myParser.getValueByLabel("country")))
							{
								DBObject state = usa.getStateByAbbrev(val);
								if (state != null)
									site.put("state", (String)state.get("state"));
							}
						}
					}
					else if ("managedBy".equalsIgnoreCase(hdr))
						site.addManagedBy(val);
					else
						site.put(hdr, val);
				}
			}

			UDataMgr.calculateLatLong(site);
			SiteAccount.save(site, userName);
			found.put(pk, "T");
			
			cnt++;
		}		

		in.close();

		return cnt;
	}

	/**
	 * Returns a list of users that are part of this site and match the resource/role
	 * 
	 * @param resource
	 * @param role
	 * @return
	 */
	public List<DBObject> getUsersByResourceRole(String resource, String role)
	{
		ResourceDefinition def = ResourceDefinition.getByName(resource);
		if (def == null)
			def = ResourceDefinition.getByInternalName(resource);
		
		DBObject elem = new BasicDBObject();
		elem.put("resDefId", def.get("_id") + "");
		elem.put("role", role);
		elem.put("siteId", "" + get("_id"));
		
		DBObject extras = BasicDBObjectBuilder.start("resourceLinkList", BasicDBObjectBuilder.start("$elemMatch", elem).get()).get();
		
		return UserAccount.search(null, null, UserAccount.getDBCollection(), extras);
	}

	/**
	 * Returns the first user found that is part of this site and matches the resource/role
	 * 
	 * @param resource
	 * @param role
	 * @return
	 */
	public UserAccount getUserByResourceRole(String resource, String role)
	{
		List<DBObject> objects = getUsersByResourceRole(resource, role);
		if (objects.size() > 0)
			return new UserAccount(objects.get(0));

		return null;
	}
	
	
}
