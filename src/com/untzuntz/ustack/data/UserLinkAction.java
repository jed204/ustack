package com.untzuntz.ustack.data;

import java.util.List;

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
 * Links a user to an object
 * 
 * @author jdanner
 *
 */
public class UserLinkAction extends LinkUIComponent implements LinkActionInterface,ActionListener {

	private static Logger logger = Logger.getLogger(UserLinkAction.class);
	
	private static final long serialVersionUID = 1L;
	protected UTable searchTable;
	private DBObject additionalSearch;
		
	public LinkUIComponent getLinkSelectUI() {

		searchTable = new UTable( "userName", "firstName", "lastName", "city", "state", "postalCode", "country" );
		searchTable.setHeader(
				new USearchTableHeader(this, "searchUsers")
							.buildHeaders("userName", "firstName", "lastName", "city", "state", "postalCode", "country")
							.setSizes(150, 150, 150, 150, 150, 75, 150)
		);
		searchTable.setWidth(UStackStatics.EX_100P);
		searchTable.addActionListener(this);
		searchTable.setActionCommand("selectUser");
		searchTable.setPager(new UTablePager(this, "searchUsers", 10));
		searchTable.getHeader().setSortField("userName");
		searchTable.getHeader().setSortDirection("ASC");
		
		// add the search area
		add(new ULabel(Msg.getString("SelectUser"), UStackStatics.FONT_BOLD_MED));
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
		List<DBObject> objects = UserAccount.search(searchTable.getHeader(), searchTable.getPager(), UserAccount.getDBCollection(), getSearchExtra());
		searchTable.loadData(objects);
	}
	
	public void actionPerformed(ActionEvent e) {
		
		if ("selectUser".equalsIgnoreCase(e.getActionCommand()))
			runActions();
		else if ("searchUsers".equalsIgnoreCase(e.getActionCommand()))
			search();
		
	}

	public DBObject getResourceLinkExtras(ResourceLink link) {
		DBObject ret = new BasicDBObject();
		ret.put("userName", searchTable.getSelectedObject().get("userName"));
		ret.put("linkText", searchTable.getSelectedObject().get("userName"));
		return ret;
	}

	public void linkCreated(UntzDBObject user, ResourceLink link) {
	}

	public void linkRemoved(UntzDBObject user, ResourceLink link) {
	}
	
}
