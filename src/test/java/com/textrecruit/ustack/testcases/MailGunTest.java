package com.textrecruit.ustack.testcases;

import com.textrecruit.ustack.main.UAppCfg;
import org.junit.Test;

public class MailGunTest {

	@Test public void testMailGun() throws Exception
	{
		System.setProperty(UAppCfg.EMAIL_MODE, "mailgun");

	}
	
}
