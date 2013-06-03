package com.untzuntz.ustack.exceptions;

public class PasswordPriorException extends PasswordException {

	private static final long serialVersionUID = -1796894951587161707L;

	public PasswordPriorException()
	{
		super("You cannot use your prior 5 passwords");
	}
	
}
