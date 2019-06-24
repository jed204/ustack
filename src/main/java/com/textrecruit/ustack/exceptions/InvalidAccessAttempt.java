package com.textrecruit.ustack.exceptions;

import com.textrecruit.ustack.main.Msg;

public class InvalidAccessAttempt extends AuthorizationException {

	private static final long serialVersionUID = 1L;

	public InvalidAccessAttempt() {
		super(Msg.getString("InvalidAccessAttempt"));
	}
	
	public InvalidAccessAttempt(String message) {
		super(Msg.getString("InvalidAccessAttempt") + " - " + message);
	}

}
