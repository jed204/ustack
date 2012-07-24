package com.untzuntz.ustack.uisupport;

import nextapp.echo.app.Color;
import nextapp.echo.app.ContentPane;
import nextapp.echo.app.ImageReference;
import nextapp.echo.extras.app.TabPane;
import nextapp.echo.extras.app.layout.TabPaneLayoutData;

public class UTabPane extends TabPane {

	private static final long serialVersionUID = 1L;

	public ContentPane createTab(String title) {
		return createTab(title, null, false);
	}
	
	public ContentPane createTab(String title, ImageReference icon, boolean cl) {

    	ContentPane cp = new ContentPane();
    	cp.setBackground(Color.WHITE);
    	cp.setInsets(UStackStatics.IN_10);
    	
        TabPaneLayoutData layoutData = new TabPaneLayoutData();
        layoutData.setCloseEnabled(cl);
        if (icon != null)
        	layoutData.setIcon(icon);
        layoutData.setTitle(title);
        cp.setLayoutData(layoutData);
        
        add(cp);

        return cp;
    }

	
	
	
}
