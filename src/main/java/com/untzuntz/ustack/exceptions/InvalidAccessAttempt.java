package com.untzuntz.ustack.exceptions;

import com.untzuntz.ustack.main.Msg;

public class InvalidAccessAttempt extends AuthorizationException {

	private static final long serialVersionUID = 1L;

	public InvalidAccessAttempt() {
		super(Msg.getString("InvalidAccessAttempt"));
	}

}
