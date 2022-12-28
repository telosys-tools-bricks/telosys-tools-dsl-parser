package org.telosys.tools.dsl.model.dbmodel;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AttributeUtilsTest {
	
	@Test
	public void test1() {

		assertEquals ("ab cd ef ", AttributeUtils.removeSpecialCharacters("ab cd ef "));
		assertEquals ("abcd", AttributeUtils.removeSpecialCharacters("a\nb\fc\rd"));
		assertEquals ("", AttributeUtils.removeSpecialCharacters("\n"));
		assertEquals ("", AttributeUtils.removeSpecialCharacters("\r"));
	}

	@Test
	public void test2() {
		assertEquals ("abcd", AttributeUtils.cleanDefaultValue(" 'abcd'  ") );
		assertEquals ("'abcd", AttributeUtils.cleanDefaultValue(" 'abcd  ") );
		assertEquals ("  abcd  ", AttributeUtils.cleanDefaultValue(" '  abcd  '  ") );
		assertEquals ("  abcd  ", AttributeUtils.cleanDefaultValue(" \"  abcd  \"  ") );
		assertEquals ("abcd", AttributeUtils.cleanDefaultValue("'abcd'  \n") );
		assertEquals ("abcd", AttributeUtils.cleanDefaultValue("abcd\n    ") );
	}

}
