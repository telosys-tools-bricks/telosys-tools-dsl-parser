package org.telosys.tools.dsl.commons;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class StringUtilTest {

	public void quoteUnquote(String s1) {
		String s2 = StringUtil.quote(s1) ;
		String s3 = StringUtil.unquote( s2);
		System.out.println("[" + s1 + "] ==> [" + s2 + "] ==> [" + s3 + "]" );
		assertEquals(s1, s3);
	}
	
	@Test
	public void testQuote() {
		assertNull(StringUtil.quote(null));
		assertEquals("\"abc\"", StringUtil.quote("abc"));
		assertEquals("\" abc \"", StringUtil.quote(" abc "));
		assertEquals("\" a\\bc \"", StringUtil.quote(" a\\bc "));
		assertEquals("\" a\\\"bc \"", StringUtil.quote(" a\"bc "));
	}
	
	@Test
	public void testUnquote() {
		assertNull(StringUtil.unquote(null));
		assertEquals("", StringUtil.unquote(""));
		assertEquals(" ", StringUtil.unquote(" "));
		assertEquals("abc", StringUtil.unquote("abc"));
		
		assertEquals("abc", StringUtil.unquote("\"abc\""));
		assertEquals(" abc ", StringUtil.unquote("\" abc \""));
		assertEquals(" a\\bc ", StringUtil.unquote("\" a\\bc \""));
		assertEquals(" a\"bc ", StringUtil.unquote("\" a\\\"bc \""));
		assertEquals("abc\\", StringUtil.unquote("\"abc\\\""));
	}
	
	@Test
	public void testQuoteUnquote() {
		quoteUnquote("");
		quoteUnquote("a");
		quoteUnquote("abc");
		quoteUnquote("a\\b\\c");
		quoteUnquote("  ab  ");
		quoteUnquote("a : \"bc\"");
		quoteUnquote("a\\\\b");
		quoteUnquote("abc\\");
	}
}
