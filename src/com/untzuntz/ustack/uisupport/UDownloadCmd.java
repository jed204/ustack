package com.untzuntz.ustack.uisupport;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.untzuntz.ustack.main.ApplicationInstance;
import com.untzuntz.ustack.main.UFile;

import nextapp.echo.filetransfer.app.DownloadCommand;
import nextapp.echo.filetransfer.app.DownloadProvider;

public class UDownloadCmd implements DownloadProvider {

	public static void sendFileToUser(UFile file, String fileType, String fileName)
	{
		sendFileToUser(file, fileType, fileName);
	}
	
	public static void sendFileToUser(UFile file, String fileType, String fileName, boolean deleteAfter)
	{
		ApplicationInstance.getActive().enqueueCommand(new DownloadCommand(new UDownloadCmd(file, fileType, fileName, deleteAfter)));
	}

	private UFile file;
	private String fileType;
	private String fileName;
	private boolean deleteAfter;
	private UDownloadCmd(UFile file, String fileType, String fileName, boolean deleteAfter)
	{
		this.file = file;
		this.fileType = fileType;
		this.fileName = fileName;
		this.deleteAfter = deleteAfter;
	}

    public String getContentType() {
        return fileType == null ? "application/binary" : fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public long getSize() {
        return file.length();
    }

    public void writeFile(OutputStream out) throws IOException {
    	
    	InputStream in = null;
    	
    	try {
    		in = file.getInputStream();
	    	byte[] buf = new byte[8192];
	    	int read = 0;
	    	while ((read = in.read(buf)) != -1)
	    		out.write(buf, 0, read);
	    	out.flush();
    	} finally {
    		if (in != null)
    			try { in.close(); } catch (Exception er) {}
    	}
    	
    	if (deleteAfter)
    		file.delete();
    }

}
