package com.textrecruit.ustack.exceptions;

import com.textrecruit.ustack.main.Msg;

public class PasswordLengthException extends PasswordException {

	private static final long serialVersionUID = 1L;

	public PasswordLengthException(int minLength) {
		super(Msg.getString("Password-Length", minLength));
	}

}
