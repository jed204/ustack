package com.untzuntz.ustack.uisupport;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EventListener;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import nextapp.echo.app.Color;
import nextapp.echo.app.Component;
import nextapp.echo.app.Label;
import nextapp.echo.app.Table;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;
import nextapp.echo.app.layout.TableLayoutData;
import nextapp.echo.app.table.AbstractTableModel;
import nextapp.echo.app.table.TableCellRenderer;

import org.apache.log4j.Logger;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.untzuntz.ustack.main.Msg;

public class UTable extends Table {

	protected static Logger logger = Logger.getLogger(UTable.class);
	private static final long serialVersionUID = 1144575294702663267L;

	private UTableModel model;
	private List<DBObject> dataList;
	private String[] columnHeaders;
	private DBObject selectedObject;
	private List<ActionListener> actions;
	private SimpleDateFormat dateOnly;
	private TableLayoutData evenRowLayout;
	private TableLayoutData oddRowLayout;
	private Hashtable<Integer,Integer> maxLens;

	@SuppressWarnings("unused")
	private UTable() {
		super();
	}
	
	public void addActionListener(ActionListener al) {
		actions.add(al);
	}

	private ActionListener tableActionListener = new ActionListener() {
		final static long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			fireItemSelection();
		}
	};

	/**
	 * Return selected object (may be null)
	 * 
	 * @return
	 */
	public DBObject getSelectedObject() {
		return selectedObject;
	}

	/**
	 * Table Header Renderer
	 */
	private TableCellRenderer tblHeaderRenderer = new TableCellRenderer() {

		final static long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(Table table,
				Object value, int column, int row) {
			return new ULabel(Msg.getString((String) value),
					UStackStatics.FONT_BOLD_MED);
		}

	};

	/**
	 * Table Cell Renderer
	 */
	private TableCellRenderer tblCellRenderer = new TableCellRenderer() {

		final static long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(Table table,
				Object value, int column, int row) {

			if (value instanceof String) {
				
				Label label = new Label("");
				
				String val = value == null ? "" : value.toString();
				if (maxLens.get(column) != null && val.length() > maxLens.get(column))
				{
					label.setToolTipText(val);
					val = val.substring(0, maxLens.get(column)) + "...";
					label.setText(val);
				}
				else
					label.setText(val);

				if (row % 2 == 0)
					label.setLayoutData(evenRowLayout);
				else
					label.setLayoutData(oddRowLayout);

				return label;
			} else if (value instanceof Component) {
				Component comp = (Component) value;

				if (comp.getStyleName() == null) {
					if (row % 2 == 0)
						comp.setLayoutData(evenRowLayout);
					else
						comp.setLayoutData(oddRowLayout);
				}
				
				return comp;
			} else if (value != null)
				logger
						.warn("Unknown value type: "
								+ value.getClass().getName());

			Label ret = new Label("");
			if (row % 2 == 0)
				ret.setLayoutData(evenRowLayout);
			else
				ret.setLayoutData(oddRowLayout);

			return ret;
		}
	};

	/**
	 * An event describing a LDAP List selection.
	 */
	public class ItemSelectionEvent extends EventObject {

		final static long serialVersionUID = 1L;

		public ItemSelectionEvent(Object source) {
			super(source);
		}

	}

	/**
	 * A listener interface for receiving notification of Item selections.
	 */
	public static interface ItemSelectionListener extends EventListener,
			Serializable {

		/**
		 * Invoked when an Item is selected.
		 * 
		 * @param e
		 *            an event describing the selection
		 */
		public void ItemSelected(ItemSelectionEvent e);
	}

	/**
     * 
     */
	public class UItemSelected implements ItemSelectionListener {
		private static final long serialVersionUID = 8385875347145452031L;

		public void ItemSelected(ItemSelectionEvent e) {
			fireItemSelection();
		}

	}
	
	public void setSelectedItem(DBObject obj)
	{
		selectedObject = obj;
		
		if (dataList.contains(obj))
		{
			getSelectionModel().setSelectedIndex(dataList.indexOf(obj), true);
			fireItemSelection();
		}
	}

	/**
	 * 
	 */
	private void fireItemSelection() {
		selectedObject = null;
		int selectedRow = getSelectionModel().getMinSelectedIndex();
		if (selectedRow > -1)
			selectedObject = dataList.get(selectedRow);

		String actName = getActionCommand();
		if (actName == null)
			actName = "ItemSelected";

		ActionEvent ae = new ActionEvent(this, actName);
		for (ActionListener al : actions)
			al.actionPerformed(ae);
	}

	public UTable(String... ch) {
		super();
		setup(ch, null);
	}

	private UTablePager pager;

	public void setPager(UTablePager pgr) {
		pager = pgr;
	}

	public UTablePager getPager() {
		return pager;
	}

	private USearchTableHeader sth;

	public void setHeader(USearchTableHeader hdr) {
		sth = hdr;
		setDefaultHeaderRenderer(hdr);
	}

	public USearchTableHeader getHeader() {
		return sth;
	}

	public void setColumnTextMax(int col, int max)
	{
		if (max == 0)
			maxLens.remove(col);
		else
			maxLens.put(col, max);
	}
	
	private void setup(String[] ch, String timeZone) {
		super.addActionListener(tableActionListener);
		setStyleName("Default.Table");

		actions = new Vector<ActionListener>();
		getEventListenerList().addListener(ItemSelectionListener.class,
				new UItemSelected());

		columnHeaders = ch;
		
		maxLens = new Hashtable<Integer,Integer>();

		evenRowLayout = new TableLayoutData();
		evenRowLayout.setBackground(UStackStatics.VLIGHT_GRAY);
		oddRowLayout = new TableLayoutData();

		dateOnly = new SimpleDateFormat("MM/dd/yyyy");
		
		model = new UTableModel(timeZone);
		setModel(model);
		setDefaultRenderer(Object.class, tblCellRenderer);
		setDefaultHeaderRenderer(tblHeaderRenderer);
		setInsets(UStackStatics.IN_5);
		setRolloverBackground(UStackStatics.DARK_BLUE);
		setRolloverForeground(Color.WHITE);
		setRolloverEnabled(true);
		setSelectionEnabled(true);
	}
	
	public TableLayoutData getEvenRowLayout() {
		return evenRowLayout;
	}
	
	public TableLayoutData getOddRowLayout() {
		return oddRowLayout;
	}

	public List<DBObject> getData() {
		return dataList;
	}

	public void loadData(BasicDBList list) {
		if (list == null) {
			loadData(new Vector<DBObject>());
			return;
		}
		List<DBObject> nList = new Vector<DBObject>();

		for (int i = 0; i < list.size(); i++)
			nList.add((DBObject) list.get(i));

		loadData(nList);
	}

	public void loadData(List<DBObject> newData) {
		dataList = newData;
		model.fireTableDataChanged();
	}

	public void clear() {
		dataList = new Vector<DBObject>();
		model.fireTableDataChanged();

		if (getPager() != null)
			getPager().clear();
	}

	public class UTableModel extends AbstractTableModel {

		private static final long serialVersionUID = -1861128616396287265L;

		@SuppressWarnings("unused")
		private String tzInfo;

		public UTableModel() {
			super();
		}

		public UTableModel(String timeZone) {
			super();

			tzInfo = timeZone;
		}

		public int getColumnCount() {
			return columnHeaders.length;
		}

		public void fireTableDataChanged() {
			super.fireTableDataChanged();
			getSelectionModel().clearSelection();
		}

		public int getRowCount() {

			if (dataList == null)
				return 0;

			return dataList.size();
		}

		public String getColumnName(int column) {
			return columnHeaders[column];
		}

		public Object getValueAt(int col, int row) {

			DBObject data = dataList.get(row);

			String colName = columnHeaders[col];
			String dat = "";
			
			if (colName.indexOf(".") > -1)
			{
				String[] colSplit = colName.split("\\.");
				
				colName = colSplit[1];
				
				if (data.get(colSplit[0]) instanceof BasicDBList)
				{
					BasicDBList list = (BasicDBList)data.get(colSplit[0]);
					
					if (list.size() > 0)
						data = (DBObject)list.get(0);
				}
				else if (data.get(colSplit[0]) instanceof BasicDBObject)
					data = (BasicDBObject)data.get(colSplit[0]);
				else if (data.get(colSplit[0]) instanceof DBObject)
					data = (DBObject)data.get(colSplit[0]);

				if (data.get(colName) == null && data.get("_id") != null)
				{
					// TODO: handle lookup
					//String id = data.get("_id") + "";
				}
			}
			

			if (data != null) {
				if (data.get(colName) instanceof Date) {
					try {
						Date dt = (Date) data.get(colName);
						if (dt != null)
							dat = dateOnly.format(dt);
					} catch (Exception e) {
						logger.warn("format error", e);
					}
				} else if (data.get(colName) instanceof String)
					dat = (String) data.get(colName);
				else if (data.get(colName) instanceof BasicDBList)
					dat = "";
				else if (data.get(colName) instanceof Double)
					dat = ((Double) data.get(colName)).intValue() + "";
				else if (data.get(colName) instanceof Integer)
					dat = ((Integer) data.get(colName)) + "";
				else if (data.get(colName) instanceof Long)
					dat = ((Long) data.get(colName)) + "";
				else
					dat = (String) data.get(colName);
			}

			return dat;
		}

	}

}
