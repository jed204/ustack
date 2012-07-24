package com.untzuntz.ustack.uisupport;

import nextapp.echo.app.Alignment;
import nextapp.echo.app.Border;
import nextapp.echo.app.Column;
import nextapp.echo.app.Component;
import nextapp.echo.app.Extent;
import nextapp.echo.app.IllegalChildException;
import nextapp.echo.app.Insets;
import nextapp.echo.app.Row;

/**
 * Extends the column class to provide a centered column. Includes a left aligned row for IE compatibility.
 * 
 * @author jdanner
 *
 */
public class UCenterCol extends Column {

	private static final long serialVersionUID = 6044020317963575725L;
	
	private Column inner;

	public UCenterCol()
	{
		Row mainRow = new Row();
		mainRow.setAlignment(Alignment.ALIGN_CENTER);
		super.add(mainRow, 0);
		
		Column mid = new Column();
		mainRow.add(mid);
		
		Row lRow = new Row();
		((Row)lRow).setAlignment(Alignment.ALIGN_LEFT);
		mid.add(lRow);
		
		inner = new Column();
		lRow.add(inner);
	}

	/**
	 * Returns the internal column that all items are added to
	 * 
	 * @return
	 */
	public Column getInner() { 
		return inner;
	}
	
	@Override
	public void setBorder(Border newValue) {
		inner.setBorder(newValue);
	}

	@Override
	public void setCellSpacing(Extent newValue) {
		inner.setCellSpacing(newValue);
	}

	@Override
	public void setInsets(Insets newValue) {
		inner.setInsets(newValue);
	}

	@Override
	public void add(Component comp) {
		inner.add(comp);
	}

	@Override
	public void add(Component c, int n) throws IllegalChildException {
		inner.add(c, n);
	}

	@Override
	public void remove(Component c) {
		inner.remove(c);
	}

	@Override
	public void remove(int n) {
		inner.remove(n);
	}

	@Override
	public void removeAll() {
		inner.removeAll();
	}
	
	
}
