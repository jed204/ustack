package com.untzuntz.ustack.testcases;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import com.untzuntz.ustack.uisupport.UEntryValidation;

public class EntryValidationTests extends TestCase {

	protected static Logger logger = Logger.getLogger(AuthenticationTests.class);

	public void testEmailAddress()
	{
		assertEquals(0, UEntryValidation.validateEmailAddress("Test", "jdanner@untzuntz.com").size());
		assertEquals(0, UEntryValidation.validateEmailAddress("Test", "jdan-ner@untzuntz.com").size());
		assertEquals(0, UEntryValidation.validateEmailAddress("Test", "jdan-ner@untzu-ntz.com").size());
		assertEquals(1, UEntryValidation.validateEmailAddress("Test", "jdanneruntzuntz.com").size());
		assertEquals(1, UEntryValidation.validateEmailAddress("Test", "jdanner@untzuntz.c").size());
		assertEquals(1, UEntryValidation.validateEmailAddress("Test", "jdanner@untzuntzc").size());
		assertEquals(1, UEntryValidation.validateEmailAddress("Test", "jdanner@untzuntz.co9").size());
		
		assertEquals(1, UEntryValidation.validateCode("Text", "abca-def2-abc3").size());
		assertEquals(1, UEntryValidation.validateCode("Text", "abc1-defc-abc3").size());
		assertEquals(1, UEntryValidation.validateCode("Text", "abc1-def2-abcd").size());
		assertEquals(1, UEntryValidation.validateCode("Text", "abg1-def2-abc3").size());
		assertEquals(1, UEntryValidation.validateCode("Text", "abc1-deg2-abc3").size());
		assertEquals(1, UEntryValidation.validateCode("Text", "abc1-deg2-abg3").size());
		assertEquals(1, UEntryValidation.validateCode("Text", "abc1deg2-abg3").size());
		assertEquals(1, UEntryValidation.validateCode("Text", "abc1-deg2abg3").size());
		assertEquals(1, UEntryValidation.validateCode("Text", "abc1deg2abg3").size());
		assertEquals(0, UEntryValidation.validateCode("Text", "abc1-def2-abc3").size());
	}

}
