package com.untzuntz.ustack.uisupport;

import org.jasypt.salt.RandomSaltGenerator;

import echopoint.DirectHtml;

import nextapp.echo.app.Row;

public class UDownloadLink extends Row {

	private static final long serialVersionUID = 1L;
	public static final String DEFAULT_STYLE = "font-family: Helvetica, sans-serif; font-size: 15px; color: #294172; a:link: { color: #294172 }";
	
	public static final String LOCAL = "LOCAL:";
	public static final String STORAGE = "STORAGE:";
	

	private String uri;
	private String storageLocation;
	private String fileLocation;
	private String fileType;
	private String linkText;
	private String fileName;
	private String style;
	private String uid;
	
	public UDownloadLink(String uri, String storageLocation, String fileLocation, String fileType, String fileName, String linkText)
	{
		this.uri = uri;
		this.storageLocation = storageLocation;
		this.fileLocation = fileLocation;
		this.fileType = fileType;
		this.linkText = linkText;
		this.fileName = fileName;
		this.style = DEFAULT_STYLE; 
		setup();
	}
	
	public UDownloadLink(String uri, String storageLocation, String fileLocation, String fileType, String fileName, String linkText, String style)
	{
		this.uri = uri;
		this.storageLocation = storageLocation;
		this.fileLocation = fileLocation;
		this.fileType = fileType;
		this.linkText = linkText;
		this.fileName = fileName;
		this.style = style; 
		setup();
	}
	
	@Override
	public void dispose() {
		super.dispose();
		
		nextapp.echo.webcontainer.ContainerContext context = (nextapp.echo.webcontainer.ContainerContext) nextapp.echo.app.ApplicationInstance.getActive().getContextProperty(nextapp.echo.webcontainer.ContainerContext.CONTEXT_PROPERTY_NAME);
		context.getSession().removeAttribute("FD." + uid);
		context.getSession().removeAttribute("FDType." + uid);
		context.getSession().removeAttribute("FDName." + uid);
	}

	public void setup()
	{
		if (storageLocation == null)
			storageLocation = STORAGE;
		
		RandomSaltGenerator rsg = new RandomSaltGenerator();
		uid = encode(rsg.generateSalt(10));
		
		nextapp.echo.webcontainer.ContainerContext context = (nextapp.echo.webcontainer.ContainerContext) nextapp.echo.app.ApplicationInstance.getActive().getContextProperty(nextapp.echo.webcontainer.ContainerContext.CONTEXT_PROPERTY_NAME);
		context.getSession().setAttribute("FD." + uid, storageLocation + fileLocation);
		context.getSession().setAttribute("FDType." + uid, fileType);
		context.getSession().setAttribute("FDName." + uid, fileName);

		StringBuffer html = new StringBuffer();
		html.append("<a href=\"");
		html.append(uri);
		html.append("?fid=");
		html.append(uid);
		html.append("\"");
		if (style != null)
		{
			html.append(" style=\"");
			html.append(style);
			html.append("\"");
		}
		
		html.append(">");
		html.append(linkText);
		html.append("</a>");
		
		removeAll();
		add(new DirectHtml(html.toString()));
	}

	protected static final byte[] Hexhars = {
		'0', '1', '2', '3', '4', '5',
		'6', '7', '8', '9', 'a', 'b',
		'c', 'd', 'e', 'f' 
	};

	public static String encode(byte[] b) {

		StringBuilder s = new StringBuilder(2 * b.length);

		for (int i = 0; i < b.length; i++) {
			int v = b[i] & 0xff;
			s.append((char)Hexhars[v >> 4]);
			s.append((char)Hexhars[v & 0xf]);
		}

		return s.toString();
	}
}
