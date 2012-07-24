package com.untzuntz.ustack.uisupport;

import nextapp.echo.app.ContentPane;
import nextapp.echo.app.Extent;
import nextapp.echo.app.WindowPane;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;

import com.untzuntz.ustack.main.ApplicationInstance;

public class UStaticWindow extends WindowPane implements ActionListener {

	private static final long serialVersionUID = 1L;
	private ContentPane topPane;
	
	public UStaticWindow()
	{
		setTitleHeight(UStackStatics.EX_0);
		setClosable( false );
		setHeight( new Extent( 310 ) );
		setWidth( new Extent( 500 ) );
		setInsets(UStackStatics.IN_10);
		setModal(true);
		setResizable(false);
		setZIndex( 32767 );

		add(topPane = new ContentPane());
		topPane.setInsets(UStackStatics.IN_5);

		ApplicationInstance.getActive().getDefaultWindow().getContent().add( this );
	}
	
	public void remove()
	{
		ApplicationInstance.getActive().getDefaultWindow().getContent().remove( this );		
	}
	
	public ContentPane getContentPane() {
		return topPane;
	}
	
	public void actionPerformed(ActionEvent e) {
		
		if ("remove".equalsIgnoreCase(e.getActionCommand()))
			remove();
		
	}
	
}
