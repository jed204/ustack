package com.textrecruit.ustack.main;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import org.jasypt.util.text.StrongTextEncryptor;

public class ConfigEnc {
	
	private String passwd;

	public ConfigEnc(String passwd) {
		this.passwd = passwd;
	}
	
	public String decrypt(InputStream in) throws IOException
	{
		// read data
		if (in.skip(293) != 293) {
			throw new IOException("Short Read");
		}
		
		int hLen = in.read() << 8;
		int lLen = in.read();
		
		byte[] reader = new byte[hLen + lLen];
		for (int idx = 0; idx < reader.length; idx++)
		{
			int val = in.read();
			in.read(); // skip
			in.read(); // skip
			reader[idx] = (byte)val;
		}

		// un-encrypt
		StrongTextEncryptor textEncryptor = getEncryptor();
		return textEncryptor.decrypt(new String(reader));
	}

	public void encrypt(String src, OutputStream out) throws IOException
	{
		// encrypt
		StrongTextEncryptor textEncryptor = getEncryptor();
		String enc = textEncryptor.encrypt(src);
		
		Random rand = new Random(System.currentTimeMillis());
		
		for (int i = 0; i < 293; i++)
			out.write(rand.nextInt(255));
		
		// save object to stream
		byte[] data = enc.getBytes();
		int dLen = data.length;
		out.write(dLen >> 8);
		out.write(dLen & 0xff);
		for (int i = 0; i < data.length; i++)
		{
			out.write(data[i]);
			out.write(rand.nextInt(255));
			out.write(rand.nextInt(255));
		}
		
		int pad2 = rand.nextInt(512) + 137;
		for (int i = 0; i < pad2; i++)
			out.write(rand.nextInt(255));

		out.flush();

	}
	
	private StrongTextEncryptor getEncryptor()
	{
		StrongTextEncryptor textEncryptor = new StrongTextEncryptor();
		textEncryptor.setPassword(passwd);
		return textEncryptor;
	}
	

	
}
