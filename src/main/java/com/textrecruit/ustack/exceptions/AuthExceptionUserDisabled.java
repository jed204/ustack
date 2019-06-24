package com.textrecruit.ustack.exceptions;

import com.textrecruit.ustack.main.Msg;

public class AuthExceptionUserDisabled extends AuthenticationException {

	private static final long serialVersionUID = -5016582788733684923L;

	public AuthExceptionUserDisabled()
	{
		super(Msg.getString("Username-Disabled"));
	}

}
