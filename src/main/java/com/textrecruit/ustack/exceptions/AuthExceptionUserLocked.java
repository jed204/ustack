package com.textrecruit.ustack.exceptions;

import com.textrecruit.ustack.main.Msg;

public class AuthExceptionUserLocked extends AuthenticationException {

	private static final long serialVersionUID = -5016582788733684923L;

	public AuthExceptionUserLocked()
	{
		super(Msg.getString("Username-Locked"));
	}

}
