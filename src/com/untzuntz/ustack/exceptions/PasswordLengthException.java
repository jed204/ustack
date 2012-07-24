package com.untzuntz.ustack.exceptions;

import com.untzuntz.ustack.main.Msg;

public class PasswordLengthException extends PasswordException {

	private static final long serialVersionUID = 1L;

	public PasswordLengthException(int minLength) {
		super(Msg.getString("Password-Length", minLength));
	}

}
