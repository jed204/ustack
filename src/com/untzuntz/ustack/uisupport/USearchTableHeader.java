package com.untzuntz.ustack.uisupport;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import nextapp.echo.app.Alignment;
import nextapp.echo.app.ApplicationInstance;
import nextapp.echo.app.Button;
import nextapp.echo.app.Column;
import nextapp.echo.app.Component;
import nextapp.echo.app.Extent;
import nextapp.echo.app.FillImage;
import nextapp.echo.app.Font;
import nextapp.echo.app.ImageReference;
import nextapp.echo.app.Style;
import nextapp.echo.app.Table;
import nextapp.echo.app.TextField;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;
import nextapp.echo.app.layout.TableLayoutData;
import nextapp.echo.app.table.TableCellRenderer;

import org.apache.log4j.Logger;

import com.untzuntz.ustack.main.Msg;

import echopoint.Strut;

public class USearchTableHeader implements TableCellRenderer,ActionListener,SearchTableHeaderInt {

	static Logger           		logger                  = Logger.getLogger(USearchTableHeader.class);

	final static long serialVersionUID = 1L;
	private Hashtable<String,FieldValueMap> fields;
	private Hashtable<String,String> defaultValues;
	private List<ActionListener> actions;
	private String actionCommand;
	private int[] fieldSizes;
	private Component lastActionHero;
	private String sortField;
	private String sortDirection;
	private Style entryStyle;
	private Style headerStyle;
	private boolean changedFlag;
	private boolean convertName;
	private boolean allowHdrTextBreak;
	private TableLayoutData tld;
	private boolean forceUpperCaseOutput;
	
	public void setUpperCase(boolean flag) {
		forceUpperCaseOutput = flag;
	}
	
	public void setHeaderStyle(Style s) {
		headerStyle = s;
	}
	
	public void setEntryStyle(Style s) {
		entryStyle = s;
	}
	
	public void setActionCommand(String cmd) {
		actionCommand = cmd;
	}

	private void prepLayout()
	{
		tld = new TableLayoutData();
		tld.setAlignment(Alignment.ALIGN_TOP);
		tld.setBackground(UStackStatics.LIGHT_GRAY);
	}
	
	public TableLayoutData getLayoutData() {
		return tld;
	}
	
	public void setConvertName(boolean b) { convertName = b; }
	
	public USearchTableHeader(ActionListener al, String actionName)
	{
		prepLayout();
		convertName = true;
		actions = new Vector<ActionListener>();
		actions.add(al);
		
		allowHdrTextBreak = true;
		changedFlag = false;
		
		actionCommand = actionName;
	}

	public USearchTableHeader()
	{
		prepLayout();
		convertName = true;
		actions = new Vector<ActionListener>();
		changedFlag = false;
		allowHdrTextBreak = true;
		actionCommand = "NoAction";
	}
	
	public USearchTableHeader setAllowHeaderTextBreak(boolean b) { 
		allowHdrTextBreak = b;
		return this;
	}
	
	public void setSortDirection(String sd) {
		sortDirection = sd;
	}
	
	public int getSortDirectionInt()
	{
		if (sortDirection == null)
			sortDirection = "DESC";

		if ("DESC".equalsIgnoreCase(sortDirection))
			return -1;
		
		return 1;
	}
	
	public String getSortDirection()
	{
		if (sortDirection == null)
			sortDirection = "DESC";
		
		return sortDirection;
	}
	
	public void setSortField(String sf)
	{
		sortField = sf;
	}
	
	public String getSortField()
	{
		return sortField;
	}
	
	public boolean getChangedFlag() { return changedFlag; }

	public USearchTableHeader setSizes(int ... sizes)
	{
		fieldSizes = sizes;
		return this;
	}
	
	public USearchTableHeader buildHeaders(String ... hdrs)
	{
		for (int i = 0; i < hdrs.length; i++)
		{
			String hdr = hdrs[i];
			getTableCellRendererComponent(null, hdr, i, 0);
		}
		
		return this;
	}
	
	private void stylizeHeader(Button btn)
	{
    	if (headerStyle != null)
    		btn.setStyle(headerStyle);
    	if (headerBackgroundImage != null)
    		btn.setBackgroundImage(new FillImage(headerBackgroundImage, UStackStatics.EX_0, UStackStatics.EX_0, FillImage.REPEAT_HORIZONTAL));
    	if (headerRolloverBackgroundImage != null)
    	{
    		btn.setRolloverEnabled(true);
    		btn.setRolloverBackgroundImage(new FillImage(headerRolloverBackgroundImage, UStackStatics.EX_0, UStackStatics.EX_0, FillImage.REPEAT_HORIZONTAL));
    	}
		
		btn.setLineWrap(allowHdrTextBreak);
	}
	
	public void setHeaderRolloverBackgroundImage(ImageReference ref) {
		headerRolloverBackgroundImage = ref;
	}
	
	public void setHeaderBackgroundImage(ImageReference ref) {
		headerBackgroundImage = ref;
	}
	
	private ImageReference headerRolloverBackgroundImage;
	private ImageReference headerBackgroundImage;

    /**
     * @see nextapp.echo.app.table.TableCellRenderer#getTableCellRendererComponent(
     *      nextapp.echo.app.Table, java.lang.Object, int, int)
     */
    public Component getTableCellRendererComponent(Table table, Object value, int column, int row) {
    	
    	if (fields == null)
    		fields = new Hashtable<String,FieldValueMap>();
    	if (defaultValues == null)
    		defaultValues = new Hashtable<String,String>();
    	
    	Column col = new UColumn(UStackStatics.EX_2);
    	col.setLayoutData(tld);
    	
    	if (convertName)
    	{
    		String msg = Msg.getString((String)value);
    		if (forceUpperCaseOutput)
    			msg = msg.toUpperCase();
    		
    		Button btn = null;
	    	if (msg.length() > 20)
	        	btn = new UButton(msg, UStackStatics.BOLD_BUTTON, this, "Sort");
	    	else
	    		btn = new UButton(msg, UStackStatics.BOLD_BUTTON, this, "Sort");

	    	btn.set("hdr", value);
	    	
	    	col.add(btn);

	    	stylizeHeader(btn);
    	}
    	else
    	{
    		String msg = (String)value;
    		if (forceUpperCaseOutput)
    			msg = msg.toUpperCase();
    		
    		Button btn = null;
	    	if (msg.length() > 20)
	        	btn = new UButton(msg, UStackStatics.BOLD_BUTTON, this, "Sort");
	    	else
	        	btn = new UButton(msg, UStackStatics.BOLD_BUTTON, this, "Sort");

	    	col.add(btn);
	    	
	    	stylizeHeader(btn);
    	}
    	
    	String valStr = (String)value;
    	if (convertName)
    		valStr = Msg.getString(valStr);

    	if (fieldSizes == null)
    		return col;
    	
        if (fields.get((String)value) != null)
        {
        	Component comp = (Component)fields.get((String)value).component;
        	col.add(comp);
        	
	        if (lastActionHero != null && lastActionHero.getRenderId().equalsIgnoreCase(comp.getRenderId()))
        		ApplicationInstance.getActive().setFocusedComponent(comp);
        }
        else
        {
        	boolean added = false;
        	
	        Component comp = null;
        	if (fieldSizes != null && fieldSizes[column] == 0)
        	{
        		comp = new Strut(25, 25);
        	}
        	else if (valStr.equalsIgnoreCase("DOB") || valStr.indexOf("Date") > -1 || valStr.indexOf("Age") > -1)
	        {
		        TextField tf = new TextField();
		        tf.setLayoutData(UStackStatics.COLUMN_BOTTOM);
		        tf.addActionListener(this);
		        tf.setId((String)value);
	        	tf.set("Changed", new Integer(0));
		        tf.setActionCommand("Search");
		        if (fieldSizes != null && fieldSizes.length > column)
		        	tf.setWidth(new Extent(fieldSizes[column]));
		        if (defaultValues.get((String)value) != null)
		        	tf.setText(defaultValues.get((String)value));
		        
		        if (entryStyle != null)
		        	tf.setStyle(entryStyle);
		        
		        comp = (Component)tf;
	        }
	        else
	        {
		        TextField tf = new TextField();
		        tf.setLayoutData(UStackStatics.COLUMN_BOTTOM);
		        tf.addActionListener(this);
		        tf.setId((String)value);
	        	tf.set("Changed", new Integer(0));
		        tf.setActionCommand("Search");
		        if (fieldSizes != null && fieldSizes.length > column)
		        	tf.setWidth(new Extent(fieldSizes[column]));
		        if (defaultValues.get((String)value) != null)
		        	tf.setText(defaultValues.get((String)value));

		        if (entryStyle != null)
		        	tf.setStyle(entryStyle);

		        comp = (Component)tf;
	        }        
	        
        	if (!added)
        		col.add(comp);
        	
        	FieldValueMap fvm = new FieldValueMap();
        	fvm.value = "";
	        if (defaultValues.get((String)value) != null)
	        	fvm.value = defaultValues.get((String)value);
        	fvm.component = comp;
        	fvm.id = (String)value;
	        if (defaultValues.get((String)value) != null)
	        	fvm.value = defaultValues.get((String)value);
	        
	        fields.put((String)value, fvm);
        }
        
        return col;
    }

	public void actionPerformed(ActionEvent e) {
		
		String action = e.getActionCommand();
		if ("Search".equalsIgnoreCase(action))
		{
			lastActionHero = (Component)e.getSource();
			doActions("Search", e);
		}
		else if ("Sort".equalsIgnoreCase(action))
		{
			Button btn = (Button)e.getSource();
			String txt = btn.getText();
			
			if (convertName)
				txt = (String)btn.get("hdr");
			
			if (sortField == null || !sortField.equalsIgnoreCase(txt))
				sortDirection = "DESC";
			else
			{
				if (sortDirection == null || sortDirection.equalsIgnoreCase("DESC"))
					sortDirection = "ASC";
				else
					sortDirection = "DESC";
			}
			
			sortField = txt;
			
			doActions("Sort", e);
		}
		
	}
	
	public Hashtable<String,FieldValueMap> getFields()
	{
		changedFlag = false;
		if (fields != null)
		{
			Enumeration<String> enu = fields.keys();
			while (enu.hasMoreElements())
			{
				String fieldName = enu.nextElement();
				String newVal = "";
				FieldValueMap fvm = fields.get(fieldName);
				if (fvm.component instanceof TextField)
				{
					newVal = ((TextField)fields.get(fieldName).component).getText();
					newVal = newVal.replaceAll("\\*", "\\\\*");
					newVal = newVal.replaceAll("\\^", "\\\\^");
					fvm.value = newVal;
					
					if (newVal.length() > 0)
						((TextField)fvm.component).setBackground(UStackStatics.VLIGHT_YELLOW);
					else
						((TextField)fvm.component).setBackground(null);
				}
			}
		}		
		return fields;
	}

	private void doActions(String actionName, ActionEvent e)
	{
		if (actions != null)
		{
			if (actionCommand == null)
				actionCommand = actionName;
			
			ActionEvent ae = new ActionEvent(e.getSource(), actionCommand);
			for (ActionListener al : actions)
				al.actionPerformed(ae);
		}
	}

	public void setFieldValue(String key, String value)
	{
		if (defaultValues == null)
			defaultValues = new Hashtable<String,String>();
		defaultValues.put(key, value);
		
		if (fields != null && fields.get(key) != null && fields.get(key).component instanceof TextField)
		{
			lastActionHero = (TextField)fields.get(key).component;
			((TextField)fields.get(key).component).setText(value);
		}
		
	}
	
	public void clearFieldValues()
	{
		if (defaultValues != null)
			defaultValues.clear();
	}

}
