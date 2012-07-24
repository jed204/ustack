package com.untzuntz.ustack.exceptions;

import com.untzuntz.ustack.main.Msg;

public class InvalidUserAccountName extends PasswordException {

	private static final long serialVersionUID = 1L;

	public InvalidUserAccountName(String reason) {
		super(Msg.getString("Invalid-Account-Name", reason));
	}

}
