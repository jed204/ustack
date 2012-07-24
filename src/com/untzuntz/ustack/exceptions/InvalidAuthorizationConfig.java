package com.untzuntz.ustack.exceptions;

import com.untzuntz.ustack.main.Msg;

public class InvalidAuthorizationConfig extends AuthorizationException {

	private static final long serialVersionUID = 1L;

	public InvalidAuthorizationConfig(String extraInfo) {
		super(Msg.getString("InvalidAuthorizationConfig", extraInfo));
	}
}
