package com.textrecruit.ustack.exceptions;

import com.textrecruit.ustack.main.Msg;

public class InvalidAuthorizationConfig extends AuthorizationException {

	private static final long serialVersionUID = 1L;

	public InvalidAuthorizationConfig(String extraInfo) {
		super(Msg.getString("InvalidAuthorizationConfig", extraInfo));
	}
}
