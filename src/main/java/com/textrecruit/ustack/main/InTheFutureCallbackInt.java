package com.textrecruit.ustack.main;

import com.textrecruit.ustack.data.InTheFuture;

public interface InTheFutureCallbackInt {

	public void execute(InTheFuture theFutureIsNow) throws InTheFutureProcessException;
	
}
