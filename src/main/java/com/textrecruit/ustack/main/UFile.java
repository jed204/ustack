package com.textrecruit.ustack.main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * File extension to support Amazon S3, MogileFS, etc.
 * 
 * @author jdanner
 *
 */
public class UFile extends File {

	private static final long serialVersionUID = 1085215642263863084L;
	private boolean localFile;

	public UFile(String pathname, boolean lcl) {
		super(pathname);
		localFile = lcl;
	}
	
	public UFile(UFile directory, String fileName, boolean lcl)
	{
		super(directory, fileName);
		localFile = lcl;
	}
	
	public boolean isLocalFile()
	{
		return localFile;
	}
	
	public void setLocalFile(boolean lcl)
	{
		localFile = lcl;
	}
	
	public static UFile getTempFile()
	{
		UFile directory = UOpts.getDirectory( UAppCfg.DIRECTORY_SCRATCH );
		directory.mkdirs();
		String fileName = "tmpfile." + System.currentTimeMillis() + "-" + Thread.currentThread().getName() + ".tmp";
		return new UFile(directory, fileName, true);
	}
	
	public OutputStream getOutputStream() throws FileNotFoundException
	{
		if (localFile)
			return new BufferedOutputStream(new FileOutputStream(this));
			
		return null;
	}
	
	public InputStream getInputStream() throws FileNotFoundException
	{
		if (localFile)
			return new BufferedInputStream(new FileInputStream(this));
		
		return null;
	}
	
	public static UFile getTempFile(InputStream in)
	{
    	OutputStream out = null;
        UFile tmpFile = null;
        try {
        	
        	tmpFile = UFile.getTempFile();
        	out = new BufferedOutputStream(new FileOutputStream(tmpFile));
            
        	byte[] buffer = new byte[4096];
            int n = 0;
            while (-1 != (n = in.read(buffer))) {
            	out.write(buffer, 0, n);
            }
            out.flush();
            
        } catch (IOException ex) {
        	
        } finally {
            try { if (in != null) in.close(); } catch (Exception ex) {}
            try { if (out != null) out.close(); } catch (Exception ex) {}
        }
        return tmpFile;
	}

}
