package org.telosys.tools.dsl.model.dbmodel;

import java.sql.Types;

import org.junit.Test;
import org.telosys.tools.generic.model.types.NeutralType;

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
	
	@Test
	public void testGetAttributeType() {
		
		assertEquals (NeutralType.DECIMAL, DbConvUtils.getAttributeType(Types.NUMERIC, 10, 2) );
		assertEquals (NeutralType.DECIMAL, DbConvUtils.getAttributeType(Types.DECIMAL, 10, 2) );
		assertEquals (NeutralType.DECIMAL, DbConvUtils.getAttributeType(Types.NUMERIC, 0, 0) );
		assertEquals (NeutralType.DECIMAL, DbConvUtils.getAttributeType(Types.DECIMAL, 0, 0) );

		assertEquals (NeutralType.INTEGER, DbConvUtils.getAttributeType(Types.NUMERIC, 6, 0) );
		assertEquals (NeutralType.INTEGER, DbConvUtils.getAttributeType(Types.DECIMAL, 6, 0) );
	}
	
	@Test
	public void testGetNumericOrDecimalAttributeType() {
		// Test for SQL type "NUMERIC(precision, scale)", "NUMERIC(precision)", "NUMERIC"
		// with precision and scale 
		assertEquals (NeutralType.DECIMAL, DbConvUtils.getNumericOrDecimalAttributeType(10, 2) );
		// with no precision and no scale 
		assertEquals (NeutralType.DECIMAL, DbConvUtils.getNumericOrDecimalAttributeType(0, 0) );

		// without scale (scale=0) => integer
		// 1 to 4 => short
		assertEquals (NeutralType.SHORT, DbConvUtils.getNumericOrDecimalAttributeType(1, 0) );
		assertEquals (NeutralType.SHORT, DbConvUtils.getNumericOrDecimalAttributeType(4, 0) );
		// 5 to 9 => int
		assertEquals (NeutralType.INTEGER, DbConvUtils.getNumericOrDecimalAttributeType(5, 0) );
		assertEquals (NeutralType.INTEGER, DbConvUtils.getNumericOrDecimalAttributeType(9, 0) );
		// 10 to 18 => long
		assertEquals (NeutralType.LONG, DbConvUtils.getNumericOrDecimalAttributeType(10, 0) );
		assertEquals (NeutralType.LONG, DbConvUtils.getNumericOrDecimalAttributeType(18, 0) );
		// more than 18 => decimal
		assertEquals (NeutralType.DECIMAL, DbConvUtils.getNumericOrDecimalAttributeType(19, 0) );
	}
}
