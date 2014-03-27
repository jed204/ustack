package com.untzuntz.ustack.testcases;

import org.junit.Test;

import com.untzuntz.ustack.main.UAppCfg;

public class MailGunTest {

	@Test public void testMailGun() throws Exception
	{
		System.setProperty(UAppCfg.EMAIL_MODE, "mailgun");

	}
	
}
