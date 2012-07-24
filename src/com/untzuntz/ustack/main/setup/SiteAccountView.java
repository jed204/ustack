package com.untzuntz.ustack.main.setup;

import java.util.List;

import nextapp.echo.app.ApplicationInstance;
import nextapp.echo.app.Button;
import nextapp.echo.app.Grid;
import nextapp.echo.app.Label;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.untzuntz.components.app.AlertLabel;
import com.untzuntz.ustack.aaa.ResourceDefinition;
import com.untzuntz.ustack.data.SiteAccount;
import com.untzuntz.ustack.main.Msg;
import com.untzuntz.ustack.main.UControllerInt;
import com.untzuntz.ustack.uisupport.UButton;
import com.untzuntz.ustack.uisupport.UCenterCol;
import com.untzuntz.ustack.uisupport.UColumn;
import com.untzuntz.ustack.uisupport.UEntryError;
import com.untzuntz.ustack.uisupport.UErrorColumn;
import com.untzuntz.ustack.uisupport.UGrid;
import com.untzuntz.ustack.uisupport.ULabel;
import com.untzuntz.ustack.uisupport.URow;
import com.untzuntz.ustack.uisupport.USearchTableHeader;
import com.untzuntz.ustack.uisupport.UStackStatics;
import com.untzuntz.ustack.uisupport.UTable;
import com.untzuntz.ustack.uisupport.UTablePager;
import com.untzuntz.ustack.uisupport.UTextField;
import com.untzuntz.ustack.uisupport.UViewColumn;
import com.untzuntz.ustack.uisupport.widgets.UResourceRoleSelect;

import echopoint.Strut;

public class SiteAccountView extends UViewColumn {

	private static final long serialVersionUID = 1L;

	public SiteAccountView(UControllerInt ctrl) { super(ctrl); }
	
	private UTextField siteName;
	private UErrorColumn errors;
	private AlertLabel updateInfo;
	private AlertLabel lookupInfo;
	private UColumn siteArea;
	private UTable siteTable;
	
	public void setErrorList(List<UEntryError> errList) { 
		updateInfo.setVisible(false);
		errors.setErrorList(errList); 
	}
	
	@Override
	public void setup() {

		removeAll();
		
		UCenterCol body = new UCenterCol();
		body.setInsets(UStackStatics.IN_15);
		body.setCellSpacing(UStackStatics.EX_5);
		add(body);

		Grid hGrid = new Grid(2);
		hGrid.setWidth(UStackStatics.EX_800);
		body.add(hGrid);
		hGrid.add(new ULabel(Msg.getString("SiteAccounts"), UStackStatics.FONT_BOLD_LARGE));

		lookupInfo = new AlertLabel();
		lookupInfo.setVisible(false);
		lookupInfo.setBackground(UStackStatics.LIGHT_YELLOW);
		lookupInfo.setInsets(UStackStatics.IN_5);
		lookupInfo.setLayoutData(UStackStatics.GRID_RIGHT);
		hGrid.add(lookupInfo);
		
		body.add(new Strut(800, 10));

		siteArea = new UColumn();

		// Search Table
		siteTable = new UTable( "siteName" );
		siteTable.setHeader(
				new USearchTableHeader(this, "searchSites")
							.buildHeaders("siteName")
							.setSizes(800)
		);
		siteTable.addActionListener(this);
		siteTable.setActionCommand("loadSite");
		siteTable.setPager(new UTablePager(this, "searchSites", 10));
		body.add(siteTable);
		body.add(siteTable.getPager());

		body.add(new Strut(800, 10));

		body.add(siteArea);
		
		body.add(new Strut(800, 50));
		body.add(new ULabel(Msg.getString("New-SiteAccount"), UStackStatics.FONT_BOLD_LARGE));

		URow row = new URow();
		body.add(row);
		
		siteName = new UTextField(Msg.getString("SiteName"));
		siteName.addActionListener(this);
		siteName.setActionCommand("newSiteAccount");
		
		row.add(new Label(Msg.getString("New-SiteAccount")));
		row.add(siteName);
		row.add(new UButton(Msg.getString("Save"), UStackStatics.WEB_BUTTON, this, "newSiteAccount"));
		
		body.add(errors = new UErrorColumn());

		updateInfo = new AlertLabel();
		updateInfo.setVisible(false);
		updateInfo.setBackground(UStackStatics.LIGHT_YELLOW);
		updateInfo.setInsets(UStackStatics.IN_5);
		body.add(updateInfo);
	}
	
	public void siteCreatedUpdate(String siteNameStr)
	{
		errors.setErrorList(null);
		updateInfo.setVisible(true);
		updateInfo.setText(Msg.getString("Site-AccountCreated", siteNameStr));
		siteName.setText("");
		ApplicationInstance.getActive().setFocusedComponent(siteName);
	}
	
	public void loadSiteList(List<DBObject> siteList, int totalResults)
	{
		if (siteList != null)
		{
			lookupInfo.setVisible(true);
			lookupInfo.setText(totalResults + " sites found");
		}
		
		siteTable.loadData(siteList);
	}
	
	public void loadSiteAccount(SiteAccount site)
	{
		siteTable.clear();
		lookupInfo.setVisible(false);
		
		siteArea.removeAll();
		
		siteArea.add(new UColumn(new ULabel(site.getSiteName(), UStackStatics.FONT_BOLD_LARGE), UStackStatics.BDR_SIMPLE, UStackStatics.LIGHT_GRAY));
		
		UGrid resGrid = new UGrid(4, UStackStatics.IN_5);
		resGrid.setWidth(UStackStatics.EX_800);
		resGrid.add(new ULabel(Msg.getString("Resource"), UStackStatics.FONT_BOLD_MED));
		resGrid.add(new ULabel(Msg.getString("Role"), UStackStatics.FONT_BOLD_MED));
		resGrid.add(new ULabel(Msg.getString("LinkData"), UStackStatics.FONT_BOLD_MED));
		resGrid.add(new ULabel("", UStackStatics.FONT_BOLD_MED));
		resGrid.setColumnWidth(0, UStackStatics.EX_100);
		resGrid.setColumnWidth(1, UStackStatics.EX_100);
		resGrid.setColumnWidth(3, UStackStatics.EX_25);
		siteArea.add(resGrid);

		BasicDBList resLink = site.getResourceLinkList();
		for (int i = 0; i < resLink.size(); i++)
		{
			DBObject obj = (DBObject)resLink.get(i);
			resGrid.add(new Label( (String)obj.get("name") ));
			resGrid.add(new Label( (String)obj.get("role") ));
			resGrid.add(new Label( (String)obj.get("linkText") ));
			
			Button btn = new UButton("Remove", UStackStatics.WEB_BUTTON, this, "removeSiteResourceLink");
			btn.set("LinkIdx", new Integer(i));
			resGrid.add(btn);
		}
		
		siteArea.add(new Strut(0, 10));
		siteArea.add(new ULabel(Msg.getString("Add-ResourceLink"), UStackStatics.FONT_BOLD_MED));

		UResourceRoleSelect urs = new UResourceRoleSelect(getController().getUser());
		urs.setActionCommand("createSiteResourceLink");
		urs.addActionListener(this);
		urs.loadResources( ResourceDefinition.getAll(ResourceDefinition.TYPE_SITEPROFILE, null) );
		siteArea.add(urs);
	}

	public UTextField getSiteNameField() {
		return siteName;
	}
	
	public void lookupAlert(String alert)
	{
		lookupInfo.setVisible(true);
		lookupInfo.setText(alert);
		siteArea.removeAll();
	}
	
	public UTable getLookupTable() {
		return siteTable;
	}

}
