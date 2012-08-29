package com.untzuntz.ustack.exceptions;

import com.untzuntz.ustack.main.Msg;

public class InvalidSiteAccountName extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidSiteAccountName(String reason) {
		super(Msg.getString("Invalid-Account-Name", reason));
	}

}
