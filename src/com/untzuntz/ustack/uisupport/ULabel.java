package com.untzuntz.ustack.uisupport;

import nextapp.echo.app.Color;
import nextapp.echo.app.Font;
import nextapp.echo.app.ImageReference;
import nextapp.echo.app.Label;
import nextapp.echo.app.LayoutData;

public class ULabel extends Label {

	private static final long serialVersionUID = 8867167284587432728L;
	
	/**
	 * Displays the label with provided text but elipses the text at a maximum length
	 * @param txt
	 * @param maxToDisplay
	 */
	public ULabel(String txt, int maxToDisplay)
	{
		if (txt != null && txt.length() > maxToDisplay)
		{
			setToolTipText(txt);
			if (maxToDisplay < 10) // for short instances show the beginning only
				txt = txt.substring(0, 7) + "...";
			else // for longer, show the two ends
			{ 
				int half = (int)( (float)maxToDisplay / 2.0f );
				txt = txt.substring(0, half - 3) + " ... " + txt.substring( txt.length() - half + 2 );
			}
		}
		setText(txt);
	}

	/** text plus a font */
	public ULabel(String txt, Font font)
	{
		super(txt);
		
		setFont(font);
	}

	/** text plus a font and color */
	public ULabel(String txt, Font font, Color color)
	{
		super(txt);
		
		setFont(font);
		setForeground(color);
	}

	/** text plus a font, color and layout */
	public ULabel(String txt, Font font, Color color, LayoutData lod)
	{
		super(txt);
		
		setFont(font);
		setForeground(color);
		setLayoutData(lod);
	}

	/** text plus a layout */
	public ULabel(String txt, LayoutData lod)
	{
		super(txt);
		
		setLayoutData(lod);
	}
	
	/** image reference and a a layout */
	public ULabel(ImageReference img, LayoutData lod)
	{
		super(img);
		
		setLayoutData(lod);
	}
	
	
}
