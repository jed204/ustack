package com.untzuntz.ustack.exceptions;

import com.untzuntz.ustack.main.Msg;

public class PasswordMismatchException extends PasswordException {

	private static final long serialVersionUID = 1L;

	public PasswordMismatchException() {
		super(Msg.getString("Password-Mismatch"));
	}

}
