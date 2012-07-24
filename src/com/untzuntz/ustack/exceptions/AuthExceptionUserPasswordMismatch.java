package com.untzuntz.ustack.exceptions;


public class AuthExceptionUserPasswordMismatch extends AuthenticationException {

	private static final long serialVersionUID = -5016582788733684923L;
	
	public AuthExceptionUserPasswordMismatch()
	{
		super("Username/Password Mismatch");
	}
}
