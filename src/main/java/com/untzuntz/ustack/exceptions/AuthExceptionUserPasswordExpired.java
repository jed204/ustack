package com.untzuntz.ustack.exceptions;

import com.untzuntz.ustack.main.Msg;

public class AuthExceptionUserPasswordExpired extends AuthenticationException {

	private static final long serialVersionUID = -5014582788733684923L;

	public AuthExceptionUserPasswordExpired()
	{
		super(Msg.getString("Username-Password-Expired"));
	}


}
