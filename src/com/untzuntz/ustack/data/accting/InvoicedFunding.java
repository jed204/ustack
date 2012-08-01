package com.untzuntz.ustack.data.accting;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import nextapp.echo.app.Column;
import nextapp.echo.app.Component;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;

import org.apache.log4j.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.untzuntz.ustack.main.Msg;
import com.untzuntz.ustack.main.UNotificationSvc;
import com.untzuntz.ustack.uisupport.UButton;
import com.untzuntz.ustack.uisupport.UColumn;
import com.untzuntz.ustack.uisupport.UEntryError;
import com.untzuntz.ustack.uisupport.UErrorColumn;
import com.untzuntz.ustack.uisupport.UGrid;
import com.untzuntz.ustack.uisupport.ULabel;
import com.untzuntz.ustack.uisupport.URow;
import com.untzuntz.ustack.uisupport.UStackStatics;
import com.untzuntz.ustack.uisupport.UTextField;
import com.untzuntz.ustack.uisupport.widgets.AddressBlock;

import echopoint.Strut;

public class InvoicedFunding implements FundingInt,ActionListener {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(InvoicedFunding.class);
	CreditAccount acct;
	FundingConfig cfg;
	private boolean testMode;
	private ActionListener parent;
	private String actionCommand;
	
	public void setAPIName(String a) {}

	public void setActionCommand(String cmd) {
		actionCommand = cmd;
	}
	
	public void setActionListener(ActionListener al) {
		parent = al;
	}
	
	public void setTestMode(boolean tm)
	{
		testMode = tm;
	}
	
	public String getDescription()
	{
		return "Invoice Funding";
	}
	
	public void setCreditAccount(CreditAccount a)
	{
		acct = a;
	}
	
	public void setFundingConfig(FundingConfig c) 
	{
		cfg = c;
	}
	public void cancelSubscription() throws Exception
	{}

	public void subscribeTo(String planId, boolean prorate) throws Exception
	{}

	public List<String> getErrors()
	{
		List<String> errors = new Vector<String>();
		return errors;
	}
	
	public DBObject getFundingData()
	{
		DBObject ret = new BasicDBObject();
		ret.putAll((Map)cfg);
		return ret;
	}
	
	private String uiActor;
	private UTextField name;
	private UTextField companyName;
	private UTextField email;
	private UTextField firstName;
	private UTextField lastName;
	private AddressBlock addrBlock;
	private UErrorColumn errors;
	public Component getFundingSetupUI(String actor, boolean test)
	{
		testMode = test;
		
		uiActor = actor;
		Column leftCol = new UColumn();

		leftCol.add(new ULabel("Invoice Information", UStackStatics.FONT_BOLD_LARGE));

		/** Basic Info UI */
		leftCol.add(new Strut(0, 5));
		leftCol.add(new ULabel(Msg.getString("BasicInfo"), UStackStatics.FONT_BOLD_MED));

		UGrid grid = new UGrid(2, UStackStatics.IN_5);
		leftCol.add(grid);
		
		grid.add("Funding Name");
		grid.add(name = new UTextField(""));
		
		/** Basic Info UI */
		leftCol.add(new Strut(0, 5));
		leftCol.add(new ULabel(Msg.getString("InvoiceInfo"), UStackStatics.FONT_BOLD_MED));
		
		UGrid grid2 = new UGrid(2, UStackStatics.IN_5);
		leftCol.add(grid2);
		grid2.add("Company Name");
		grid2.add(companyName = new UTextField(""));
		grid2.add("User Email");
		grid2.add(email = new UTextField(""));
		grid2.add("First Name");
		grid2.add(firstName = new UTextField(""));
		grid2.add("Last Name");
		grid2.add(lastName = new UTextField(""));

		/** Address Info UI */
		leftCol.add(new Strut(0, 5));
		leftCol.add(new ULabel(Msg.getString("AddressInfo"), UStackStatics.FONT_BOLD_MED));
		
		addrBlock = new AddressBlock(true);
		addrBlock.setShowFax(false);
		addrBlock.setShowTimezone(false);
		addrBlock.setup();
		addrBlock.setColumnWidth(0, UStackStatics.EX_150);
		leftCol.add(addrBlock);

		URow acts = new URow();
		acts.add(new UButton("Save", UStackStatics.WEB_BUTTON, this, "save"));
		leftCol.add(acts);
		
		leftCol.add(errors = new UErrorColumn());
		
		if (cfg != null)
		{
			name.setText(cfg.getName());
			companyName.setText(cfg.getString("companyName"));
			email.setText(cfg.getString("userEmail"));
			firstName.setText(cfg.getString("firstName"));
			lastName.setText(cfg.getString("lastName"));
			addrBlock.setAddress(cfg);
			addrBlock.getTelephoneNumber().setPhoneNumber(cfg.getString("phone"));
		}
		
		return leftCol;
	}

	private void saveFunding()
	{
		List<UEntryError> errorList = UEntryError.getEmptyList();
		errorList.addAll(name.validateStringLen(1, 255));
		errorList.addAll(companyName.validateStringLen(0, 255));
		errorList.addAll(email.validateEmailAddress());
		errorList.addAll(firstName.validateStringLen(1, 255));
		errorList.addAll(lastName.validateStringLen(1, 255));
		errorList.addAll(addrBlock.validateEntries());
		
		errors.setErrorList(errorList);
		if (errorList.size() > 0)
			return;
		
		if (cfg == null)
		{
			try {
				createFunding(uiActor, name.getText(), null, acct.getUid(), companyName.getText(), 
						email.getText(), 
						firstName.getText(), lastName.getText(), 
						addrBlock.getAddress1().getText(), addrBlock.getAddress2().getText(), 
						addrBlock.getCity().getText(), addrBlock.getState().getString(), addrBlock.getPostalCode().getText(), 
						addrBlock.getCountry().getString(), 
						addrBlock.getTelephoneNumber().getPhoneString(), null,
						null, null, null, testMode);
				
				acct.addFundingSource(cfg);
				acct.save(uiActor);
				
			} catch (Exception err) {
				logger.error("Failed to create funding object", err);
				errorList.add(UEntryError.getInstance("Create Failed", "Failed to create profile"));
			}
			
		}
		else
		{
			try {
				// do updates
				updateBillingAddress(uiActor, companyName.getText(), 
							firstName.getText(), lastName.getText(), 
							addrBlock.getAddress1().getText(), addrBlock.getAddress2().getText(), 
							addrBlock.getCity().getText(), addrBlock.getState().getString(), addrBlock.getPostalCode().getText(), 
							addrBlock.getCountry().getString(), 
							addrBlock.getTelephoneNumber().getPhoneString(), null, testMode);
				
				acct.save(uiActor);
				
			} catch (Exception err) {
				logger.error("Failed to update funding object", err);
				errorList.add(UEntryError.getInstance("Update Failed", "Failed to update profile"));
			}
		}
		errors.setErrorList(errorList);
		
		if (errorList.size() == 0)
		{
			ActionEvent ae = new ActionEvent(this, actionCommand);
			parent.actionPerformed(ae);
		}
	}

	public String requestFunding(String actor, String userIpAddress, String description, int price, boolean test) throws Exception {

		UNotificationSvc svc = new UNotificationSvc();
		svc.setData("creditAccount", acct);
		svc.notify("ustack.invoiceRefreshNeeded", null);
		acct.save(actor);

		return "Pending";
	}

	public FundingConfig createFunding(String actor, String name, String customerType, String customerId, String company, String email, String firstName, 
			String lastName, String addr1, String addr2, String city, String state, String zip, String country, String phone, String fax, 
			String cardNumber, Date expirationDate, String ccv, boolean test) throws Exception
	{
		cfg = new FundingConfig(name, "com.untzuntz.ustack.data.accting.InvoicedFunding");
		cfg.put("fundingType", "Invoice (Paper)");
		cfg.put("customerId", customerId);
		cfg.put("companyName", company);
		cfg.put("userEmail", email);
		cfg.put("firstName", firstName);
		cfg.put("lastName", lastName);
		cfg.put("address1", addr1);
		cfg.put("address2", addr2);
		cfg.put("city", city);
		cfg.put("state", state);
		cfg.put("postalCode", zip);
		cfg.put("country", country);
		cfg.put("phone", phone);
		
		return cfg;
	}
	
	public void updateBillingAddress(String actor, String company, String firstName, String lastName, String addr1, String addr2, String city, String state, String zip, String country, String phone, String fax, boolean test) throws Exception
	{
		cfg.put("companyName", company);
		cfg.put("firstName", firstName);
		cfg.put("lastName", lastName);
		cfg.put("address1", addr1);
		cfg.put("address2", addr2);
		cfg.put("city", city);
		cfg.put("state", state);
		cfg.put("postalCode", zip);
		cfg.put("country", country);
		cfg.put("phone", phone);
	}
	
	public void actionPerformed(ActionEvent e) {

		if ("save".equalsIgnoreCase(e.getActionCommand()))
			saveFunding();
		
	}

	
	public void updateCreditCardInfo(String actor, String cardNumber, Date expirationDate, String ccv, boolean test) throws Exception
	{
		// stub
	}

	public String oneTime(String actor, String userIpAddress, String description, int price, 
			String cardNum, Date expDate, String cardCode, 
			String firstName, String lastName, 
			String addr1, String addr2, 
			String city, String state, String zip, String country, String customerId, boolean test)  throws Exception 
	{
		return "";
	}
}
