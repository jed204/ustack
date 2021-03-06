package com.untzuntz.ustack.exceptions;

import com.untzuntz.ustack.main.Msg;

public class RequiredAccountDataMissingException extends Exception {

	private static final long serialVersionUID = -5013582788733224923L;

	public RequiredAccountDataMissingException(String fieldName) {
		super(Msg.getString("Missing-ReqAcctData", fieldName));
	}

}
