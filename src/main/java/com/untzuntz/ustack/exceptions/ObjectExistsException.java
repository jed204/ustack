package com.untzuntz.ustack.exceptions;

import com.untzuntz.ustack.main.Msg;

public class ObjectExistsException extends Exception {

	private static final long serialVersionUID = 1L;

	public ObjectExistsException() {
		super(Msg.getString("Object-Exists"));
	}
	
}
