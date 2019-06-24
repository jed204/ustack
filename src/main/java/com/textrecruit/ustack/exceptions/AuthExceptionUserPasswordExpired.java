package com.textrecruit.ustack.exceptions;

import com.textrecruit.ustack.main.Msg;

public class AuthExceptionUserPasswordExpired extends AuthenticationException {

	private static final long serialVersionUID = -5014582788733684923L;

	public AuthExceptionUserPasswordExpired()
	{
		super(Msg.getString("Username-Password-Expired"));
	}


}
