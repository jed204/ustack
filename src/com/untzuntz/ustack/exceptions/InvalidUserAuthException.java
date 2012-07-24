package com.untzuntz.ustack.exceptions;

import com.untzuntz.ustack.main.Msg;

public class InvalidUserAuthException extends AuthorizationException {

	private static final long serialVersionUID = 1L;

	public InvalidUserAuthException() {
		super(Msg.getString("UnknownUser"));
	}

}
