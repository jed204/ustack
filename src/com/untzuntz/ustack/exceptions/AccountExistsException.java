package com.untzuntz.ustack.exceptions;

import com.untzuntz.ustack.main.Msg;

public class AccountExistsException extends Exception {

	private static final long serialVersionUID = -5013582788733224923L;

	public AccountExistsException(String type) {
		super(Msg.getString(type + "Account-Exists"));
	}

}
