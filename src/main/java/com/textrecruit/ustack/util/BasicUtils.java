package com.textrecruit.ustack.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.http.HttpEntity;

public class BasicUtils {

	public static Writer getResponseString(HttpEntity e) throws Exception
	{
		char[] buffer = new char[1024];
		Writer writer = new StringWriter();
		Reader reader = new BufferedReader(new InputStreamReader(e.getContent(), "UTF-8"));
		int n;
		while ((n = reader.read(buffer)) != -1) {
			writer.write(buffer, 0, n);
		}
		
		return writer;
	}


}
