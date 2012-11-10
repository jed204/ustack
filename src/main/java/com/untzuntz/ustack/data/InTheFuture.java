package com.untzuntz.ustack.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.untzuntz.ustack.main.Duration;
import com.untzuntz.ustack.main.InTheFutureCallbackInt;
import com.untzuntz.ustack.main.UAppCfg;
import com.untzuntz.ustack.main.UOpts;

public class InTheFuture extends UntzDBObject {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(InTheFuture.class);

	@Override
	public String getCollectionName() { return "inTheFuture"; }

	public InTheFuture(boolean testMode) {
		put("created", new Date());
	}
	
	private InTheFuture()
	{
		// setup basic values on account
		put("status", "pending");
		put("created", new Date());
	}
	
	public String toString() {
		return getInTheFutureId();
	}

	public String getInTheFutureId() {
		return get("_id") + "";
	}

	/** Gets the DB Collection for the InTheFuture object */
	public static DBCollection getDBCollection() {
		return new InTheFuture().getCollection();
	}

	/** Return the name of the database that houses the 'inTheFuture' collection */
	public static final String getDatabaseName() {
		
		if (UOpts.getString(UAppCfg.DATABASE_IN_THE_FUTURE_COL) != null)
			return UOpts.getString(UAppCfg.DATABASE_IN_THE_FUTURE_COL);
		
		return UOpts.getAppName();
	}

	/**
	 * Generate a InTheFuture object from the MongoDB object
	 * @param user
	 */
	public InTheFuture(DBObject not) {
		super(not);
	}
	
	public int getRetryCount() {
		return getInt("retryCount", 5);
	}
	
	public void setRetryCount(int r) {
		put("retryCount", r);
	}
	
	public String getRetryInterval() {
		String ri = getString("retryInterval");
		if (ri == null)
			ri = "1 hour";
		
		return ri;
	}
	
	public void setRetryInterval(String r) {
		put("retryInterval", r);
	}
	
	public String getErrorAction() {
		return getString("errorAction");
	}
	
	public void setErrorAction(String a) {
		put("errorAction", a);
	}

	public String getLastError() {
		return getString("lastError");
	}
	
	public Date getLastErrorTime() {
		return (Date)get("lastErrorTime");
	}
	
	public void setLastError(String e) {
		put("lastError", e);
		put("lastErrorTime", new Date());
	}
	
	public void failed()
	{
		put("status", "failed");
	}
	
	public void requeue()
	{
		String retryInt = getRetryInterval();
		
		Duration d = new Duration(retryInt);
		setActionTime(d.getDate(new Date()));
	}
	
	public void setActionClass(String ac) {
		put("actionClass", ac);
	}
	
	public String getActionClass() {
		return getString("actionClass");
	}
	
	public void setActionTime(Date d) {
		put("actionTime", d);
	}
	
	public Date getActionTime() {
		return (Date)get("actionTime");
	}
	
	public void success() 
	{
		put("successTime", new Date());
		
		if (isRepeatAction())
		{
			put("status", "pending");
			setActionTime(new Duration(getWhen()).getDate(new Date()));
		}
		else
			put("status", "success");
	}
	
	public void setWhen(String when) {
		put("when", when);
	}
	
	public String getWhen() {
		return getString("when");
	}
	
	public void setRepeatAction(boolean f) {
		put("repeat", f);
	}
	
	public boolean isRepeatAction() {
		return getBoolean("repeat", false);
	}

	public static InTheFuture create(String actor, @SuppressWarnings("rawtypes") Class actionClass, String when)
	{
		return create(actor, actionClass, when, false);
	}
	
	public static InTheFuture create(String actor, @SuppressWarnings("rawtypes") Class actionClass, String when, boolean repeat)
	{
		return create(actor, actionClass.getName(), when, repeat);
	}

	public static InTheFuture create(String actor, String actionClass, String when)
	{	
		return create(actor, actionClass, when, false);
	}
	
	public static InTheFuture create(String actor, String actionClass, String when, boolean repeat)
	{	
		InTheFuture itf = new InTheFuture();
		itf.put("createdBy", actor);
		itf.setWhen(when);
		itf.setRepeatAction(repeat);
		itf.setActionClass(actionClass);
		itf.setActionTime(new Duration(when).getDate(new Date()));
		return itf;
	}
	
	public static void cleanUp()
	{
		// delete any older than 7 days
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -7);
		
		DBObject search = new BasicDBObject("actionTime", new BasicDBObject("$lt", cal.getTime()));
		DBCollection col = new InTheFuture().getCollection();
		col.remove(search);
	}
	
	public static void process()
	{
		List<InTheFuture> items = getPending(50);
		logger.info(items.size() + " InTheFuture items to process...");
		for (InTheFuture item : items)
		{
			try {

				logger.info("\t " + item.getInTheFutureId() + " => " +  item.getActionClass() );
				
				InTheFutureCallbackInt callback = (InTheFutureCallbackInt)Class.forName( item.getActionClass() ).newInstance();
				callback.execute(item);
				item.success();
				
			} catch (Exception e) {

				logger.warn("Failed to complete In The Future Call for Item ID : " + item.getInTheFutureId() + " => " + e.getMessage());
				if ("retry".equalsIgnoreCase( item.getErrorAction() ))
				{
					int retryCount = item.getRetryCount();
					retryCount--;
					if (retryCount == 0)
						item.failed();
					else
						item.requeue();
				}
				
				item.setLastError(e.getMessage());
			}

			item.save("In The Future Processor");
		}
	}
	
	public static List<InTheFuture> getPending(int count)
	{
		DBObject search = new BasicDBObject("status", "pending");
		search.put("actionTime", new BasicDBObject("$lt", new Date()));
		DBCollection col = new InTheFuture().getCollection();
		DBCursor cur = col.find(search).limit(count);
		
		List<InTheFuture> ret = new ArrayList<InTheFuture>();
		while (cur.hasNext())
			ret.add(new InTheFuture(cur.next()));
		
		return ret;
	}

}
