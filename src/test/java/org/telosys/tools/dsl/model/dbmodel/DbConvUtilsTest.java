package org.telosys.tools.dsl.model.dbmodel;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DbConvUtilsTest {
	
	@Test
	public void testRemoveSpecialCharacters() {

		assertEquals ("ab cd ef ", DbConvUtils.removeSpecialCharacters("ab cd ef "));
		assertEquals ("a b c d", DbConvUtils.removeSpecialCharacters("a\nb\fc\rd"));
		assertEquals (" ", DbConvUtils.removeSpecialCharacters("\n"));
		assertEquals (" ", DbConvUtils.removeSpecialCharacters("\r"));
	}

	@Test
	public void testCleanDefaultValue() {
		assertEquals ("abcd", DbConvUtils.cleanDefaultValue(" 'abcd'  ") );
		assertEquals ("'abcd", DbConvUtils.cleanDefaultValue(" 'abcd  ") );
		assertEquals ("  abcd  ", DbConvUtils.cleanDefaultValue(" '  abcd  '  ") );
		assertEquals ("  abcd  ", DbConvUtils.cleanDefaultValue(" \"  abcd  \"  ") );
		assertEquals ("abcd", DbConvUtils.cleanDefaultValue("'abcd'  \n") );
		assertEquals ("abcd", DbConvUtils.cleanDefaultValue("abcd\n    ") );
	}

	@Test
	public void testCleanComment() {
		assertEquals (" abcd  ", DbConvUtils.cleanComment(" abcd  ") );
		assertEquals (" 'abcd  ", DbConvUtils.cleanComment(" 'abcd  ") );
		assertEquals ("abcd efg", DbConvUtils.cleanComment("abcd\nefg") );
		assertEquals (" abcd efg ", DbConvUtils.cleanComment(" abcd\refg ") );
	}
}
