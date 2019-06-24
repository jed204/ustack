package com.textrecruit.ustack.exceptions;

import com.textrecruit.ustack.main.Msg;

public class InvalidUserAuthException extends AuthorizationException {

	private static final long serialVersionUID = 1L;

	public InvalidUserAuthException() {
		super(Msg.getString("UnknownUser"));
	}

}
