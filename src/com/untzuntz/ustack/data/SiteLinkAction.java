package com.untzuntz.ustack.data;

import java.util.List;
import java.util.Vector;

import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;

import org.apache.log4j.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import com.untzuntz.ustack.aaa.LinkActionInterface;
import com.untzuntz.ustack.aaa.ResourceLink;
import com.untzuntz.ustack.main.Msg;
import com.untzuntz.ustack.uisupport.LinkUIComponent;
import com.untzuntz.ustack.uisupport.ULabel;
import com.untzuntz.ustack.uisupport.USearchTableHeader;
import com.untzuntz.ustack.uisupport.UStackStatics;
import com.untzuntz.ustack.uisupport.UTable;
import com.untzuntz.ustack.uisupport.UTablePager;

/**
 * Links a user to a site
 * @author jdanner
 *
 */
public class SiteLinkAction extends LinkUIComponent implements LinkActionInterface,ActionListener {

	private static Logger logger = Logger.getLogger(SiteLinkAction.class);
	
	private static final long serialVersionUID = 1L;
	private UTable searchTable;
	private DBObject additionalSearch;
		
	public LinkUIComponent getLinkSelectUI() {

		searchTable = new UTable( "siteName", "city", "state", "postalCode", "country" );
		searchTable.setHeader(
				new USearchTableHeader(this, "searchSites")
							.buildHeaders("siteName", "city", "state", "postalCode", "country")
							.setSizes(200, 150, 150, 75, 150)
		);
		searchTable.setWidth(UStackStatics.EX_100P);
		searchTable.addActionListener(this);
		searchTable.setActionCommand("selectSite");
		searchTable.setPager(new UTablePager(this, "searchSites", 10));
		searchTable.getHeader().setSortField("siteName");
		searchTable.getHeader().setSortDirection("ASC");
		
		// add the search area
		add(new ULabel(Msg.getString("SelectSite"), UStackStatics.FONT_BOLD_MED));
		add(searchTable);
		add(searchTable.getPager());
		search();
		
		return this;
	}
	
	public void setAdditionalSearch(DBObject as)
	{
		additionalSearch = as;
	}

	/** Match on internal name */
	private DBObject getSearchExtra()
	{
		DBObject extra = getUser().getCanManageSearch();

		if (getType() != null && getRole() != null)
		{
			BasicDBObject foo = new BasicDBObject();
			foo.put("$elemMatch", BasicDBObjectBuilder.start("internalName", getType()).append("role", getRole()).get());
			extra.put("resourceLinkList", foo);
		}
		else if (getType() != null)
		{
			BasicDBObject foo = new BasicDBObject();
			foo.put("$elemMatch", BasicDBObjectBuilder.start("internalName", getType()).get());
			extra.put("resourceLinkList", foo);
		}
		
		if (additionalSearch != null)
			extra.putAll(additionalSearch);

		logger.info("Search Extra: " + extra);
		
		return extra;
	}

	private void search()
	{
		List<DBObject> objects = SiteAccount.search(searchTable.getHeader(), searchTable.getPager(), SiteAccount.getDBCollection(), getSearchExtra());
		searchTable.loadData(objects);
	}
	
	public void setSelectedSite(String siteId)
	{
		SiteAccount sa = SiteAccount.getSiteById(siteId);
		if (sa != null)
		{
			List<DBObject> objects = new Vector<DBObject>();
			objects.add(sa);
			searchTable.loadData(objects);
			searchTable.getSelectionModel().setSelectedIndex(0, true);
			searchTable.setSelectedItem(objects.get(0));
		}
	}
	
	public DBObject getSelectedSite()
	{
		return searchTable.getSelectedObject();
	}

	public void actionPerformed(ActionEvent e) {
		
		if ("selectSite".equalsIgnoreCase(e.getActionCommand()))
			runActions();
		else if ("searchSites".equalsIgnoreCase(e.getActionCommand()))
			search();
		
	}

	public DBObject getResourceLinkExtras(ResourceLink link) {
		DBObject ret = new BasicDBObject();
		ret.put("siteId", searchTable.getSelectedObject().get("_id") + "");
		ret.put("linkText", searchTable.getSelectedObject().get("siteName"));
		return ret;
	}

	public void linkCreated(UntzDBObject user, ResourceLink link) {
		
		if (link.getString("siteId") != null)
		{
			SiteAccount site = SiteAccount.getSiteById( link.getString("siteId") );
			if (site != null)
			{
				link.put("linkText", site.getSiteName());
				if (site.getString("ldapCompanyUID") != null)
					link.put("ldapCompanyUID", site.getString("ldapCompanyUID"));
				if (site.getString("ldapGroupUID") != null)
					link.put("ldapGroupUID", site.getString("ldapGroupUID"));
				
				if (user instanceof UserAccount)
				{
					UserAccount acct = (UserAccount)user;
					AddressBook siteAddrBook = site.getAddressBook();
					if (siteAddrBook != null)
						siteAddrBook.addEntry(new AddressBookEntry(acct));
				}
			}
			else
				logger.error("Failed to locate site by id '" + link.getString("siteId") + "'");
		}
		
	}

	public void linkRemoved(UntzDBObject user, ResourceLink link) {
	}
	
}
