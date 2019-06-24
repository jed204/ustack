package com.textrecruit.ustack.exceptions;

import com.textrecruit.ustack.main.Msg;

public class ObjectExistsException extends Exception {

	private static final long serialVersionUID = 1L;

	public ObjectExistsException() {
		super(Msg.getString("Object-Exists"));
	}
	
}
