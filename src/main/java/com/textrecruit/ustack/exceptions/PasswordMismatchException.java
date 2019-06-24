package com.textrecruit.ustack.exceptions;

import com.textrecruit.ustack.main.Msg;

public class PasswordMismatchException extends PasswordException {

	private static final long serialVersionUID = 1L;

	public PasswordMismatchException() {
		super(Msg.getString("Password-Mismatch"));
	}

}
