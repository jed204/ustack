package com.untzuntz.ustack.exceptions;

abstract public class AuthenticationException extends Exception {

	private static final long serialVersionUID = -5016582788733684923L;

	protected AuthenticationException(String msg) {
		super(msg);
	}
	
}
