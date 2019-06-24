package com.textrecruit.ustack.exceptions;

abstract public class AuthorizationException extends Exception {

	private static final long serialVersionUID = -501651211511634923L;

	protected AuthorizationException(String msg) {
		super(msg);
	}

}
