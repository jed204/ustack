package com.untzuntz.ustack.data;

import java.util.List;

import nextapp.echo.app.button.ButtonGroup;
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
import com.untzuntz.ustack.uisupport.UColumn;
import com.untzuntz.ustack.uisupport.ULabel;
import com.untzuntz.ustack.uisupport.URadioButton;
import com.untzuntz.ustack.uisupport.URow;
import com.untzuntz.ustack.uisupport.USearchTableHeader;
import com.untzuntz.ustack.uisupport.UStackStatics;
import com.untzuntz.ustack.uisupport.UTable;
import com.untzuntz.ustack.uisupport.UTablePager;

/**
 * Links a user or a site to an object
 * 
 * @author jdanner
 *
 */
public class SiteOrUserLinkAction extends LinkUIComponent implements LinkActionInterface,ActionListener {

	private static Logger logger = Logger.getLogger(SiteOrUserLinkAction.class);
	
	private static final long serialVersionUID = 1L;
	private UTable searchTable;
	private DBObject additionalSearch;
	private boolean site;
	private ButtonGroup siteOrUser;
	private UColumn main;
		
	public LinkUIComponent getLinkSelectUI() {

		main = new UColumn();
		
		siteOrUser = new ButtonGroup();
		URadioButton siteBtn = new URadioButton("Site", siteOrUser, "site", true);
		URadioButton userBtn = new URadioButton("User", siteOrUser, "user", false);
		siteBtn.addActionListener(this);
		siteBtn.setActionCommand("loadSiteUI");
		userBtn.addActionListener(this);
		userBtn.setActionCommand("loadUserUI");
		
		URow acts = new URow();
		acts.add(siteBtn);
		acts.add(userBtn);
		add(acts);
		
		loadSiteUI();
				
		add(main);
		
		return this;
	}

	public void loadSiteUI()
	{
		searchTable = new UTable( "siteName", "city", "state", "postalCode", "country" );
		searchTable.setHeader(
				new USearchTableHeader(this, "search")
							.buildHeaders("siteName", "city", "state", "postalCode", "country")
							.setSizes(200, 150, 150, 75, 150)
		);
		searchTable.setWidth(UStackStatics.EX_100P);
		searchTable.addActionListener(this);
		searchTable.setActionCommand("select");
		searchTable.setPager(new UTablePager(this, "search", 10));
		searchTable.getHeader().setSortField("siteName");
		searchTable.getHeader().setSortDirection("ASC");
		
		// add the search area
		main.removeAll();
		main.add(new ULabel(Msg.getString("SelectSite"), UStackStatics.FONT_BOLD_MED));
		main.add(searchTable);
		main.add(searchTable.getPager());
		site = true;
		search();
	}
	
	public void loadUserUI()
	{
		searchTable = new UTable( "userName", "firstName", "lastName", "city", "state", "postalCode", "country" );
		searchTable.setHeader(
				new USearchTableHeader(this, "search")
							.buildHeaders("userName", "firstName", "lastName", "city", "state", "postalCode", "country")
							.setSizes(150, 150, 150, 150, 150, 75, 150)
		);
		searchTable.setWidth(UStackStatics.EX_100P);
		searchTable.addActionListener(this);
		searchTable.setActionCommand("select");
		searchTable.setPager(new UTablePager(this, "search", 10));
		searchTable.getHeader().setSortField("userName");
		searchTable.getHeader().setSortDirection("ASC");
		
		// add the search area
		main.removeAll();
		main.add(new ULabel(Msg.getString("SelectUser"), UStackStatics.FONT_BOLD_MED));
		main.add(searchTable);
		main.add(searchTable.getPager());
		site = false;
		search();
		
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
		List<DBObject> objects = null;
		
		if (site)
			objects = SiteAccount.search(searchTable.getHeader(), searchTable.getPager(), SiteAccount.getDBCollection(), getSearchExtra());
		else
			objects = UserAccount.search(searchTable.getHeader(), searchTable.getPager(), UserAccount.getDBCollection(), getSearchExtra());
		
		searchTable.loadData(objects);
	}
	
	public void actionPerformed(ActionEvent e) {
		
		if ("select".equalsIgnoreCase(e.getActionCommand()))
			runActions();
		else if ("search".equalsIgnoreCase(e.getActionCommand()))
			search();
		else if ("loadSiteUI".equalsIgnoreCase(e.getActionCommand()))
			loadSiteUI();
		else if ("loadUserUI".equalsIgnoreCase(e.getActionCommand()))
			loadUserUI();
		
	}

	public DBObject getResourceLinkExtras(ResourceLink link) {
		DBObject ret = new BasicDBObject();
		if (site)
		{
			ret.put("siteId", searchTable.getSelectedObject().get("_id") + "");
			ret.put("linkText", searchTable.getSelectedObject().get("siteName"));
		}
		else
		{
			ret.put("userName", searchTable.getSelectedObject().get("userName"));
			ret.put("linkText", searchTable.getSelectedObject().get("userName"));
		}
		return ret;
	}

	public void linkCreated(UntzDBObject user, ResourceLink link) {

		if (site)
		{	
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
	}

	public void linkRemoved(UntzDBObject user, ResourceLink link) {
	}
	
}
