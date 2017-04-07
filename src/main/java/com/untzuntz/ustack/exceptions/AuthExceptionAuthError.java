package com.untzuntz.ustack.exceptions;


public class AuthExceptionAuthError extends AuthenticationException {

	private static final long serialVersionUID = -5016582788733684923L;

	public AuthExceptionAuthError()
	{
		super("Unknown Error During Authentication");
	}

}
