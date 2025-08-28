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
		String notSupposedToBeUsed = "xyz";
		assertEquals (NeutralType.DECIMAL, DbConvUtils.getAttributeType(Types.NUMERIC, 10, 2, notSupposedToBeUsed) );
		assertEquals (NeutralType.DECIMAL, DbConvUtils.getAttributeType(Types.DECIMAL, 10, 2, notSupposedToBeUsed) );
		assertEquals (NeutralType.DECIMAL, DbConvUtils.getAttributeType(Types.NUMERIC,  0, 0, notSupposedToBeUsed) );
		assertEquals (NeutralType.DECIMAL, DbConvUtils.getAttributeType(Types.DECIMAL,  0, 0, notSupposedToBeUsed) );

		assertEquals (NeutralType.INTEGER, DbConvUtils.getAttributeType(Types.NUMERIC,  6, 0, notSupposedToBeUsed) );
		assertEquals (NeutralType.INTEGER, DbConvUtils.getAttributeType(Types.DECIMAL,  6, 0, notSupposedToBeUsed) );
		
		// Ver 4.3.0 - Specific types detection (for UUID)
		assertEquals (NeutralType.UUID,    DbConvUtils.getAttributeType(Types.OTHER,   0, 0, "uuid") ); // PostgreSQL UUID
		assertEquals (NeutralType.UUID,    DbConvUtils.getAttributeType(Types.OTHER,   0, 0, "UUID") ); // PostgreSQL UUID

		assertEquals (NeutralType.UUID,    DbConvUtils.getAttributeType(Types.CHAR,    0, 0, "UNIQUEIDENTIFIER") ); // SQL Server UUID
		assertEquals (NeutralType.UUID,    DbConvUtils.getAttributeType(Types.BINARY,  0, 0, "UNIQUEIDENTIFIER") ); // SQL Server UUID
		assertEquals (NeutralType.UUID,    DbConvUtils.getAttributeType(Types.CHAR,    0, 0, "uniqueidentifier") ); // SQL Server UUID

		assertEquals (NeutralType.STRING,  DbConvUtils.getAttributeType(Types.OTHER,   0, 0, "not-uuid") );         // "string" for default value
		assertEquals (NeutralType.STRING,  DbConvUtils.getAttributeType(Types.OTHER,   0, 0, "uniqueidentifier") ); // "string" for default value
		assertEquals (NeutralType.STRING,  DbConvUtils.getAttributeType(Types.VARCHAR, 0, 0, "uuid") ); 
		assertEquals (NeutralType.STRING,  DbConvUtils.getAttributeType(Types.CHAR,    0, 0, "uuid") );
		assertEquals (NeutralType.BINARY,  DbConvUtils.getAttributeType(Types.BINARY,  0, 0, "uuid") );
		assertEquals (NeutralType.INTEGER, DbConvUtils.getAttributeType(Types.NUMERIC, 6, 0, "uuid") );
		
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

	@Test
	public void testGetNeutralTypeForTimestamp() {
		assertEquals (NeutralType.DATETIME,   DbConvUtils.getNeutralTypeForTimestamp(Types.TIMESTAMP, "timestamp"));
		assertEquals (NeutralType.DATETIME,   DbConvUtils.getNeutralTypeForTimestamp(Types.TIMESTAMP, "foo"));
		assertEquals (NeutralType.DATETIMETZ, DbConvUtils.getNeutralTypeForTimestamp(Types.TIMESTAMP, "timestamptz"));
		assertEquals (NeutralType.DATETIMETZ, DbConvUtils.getNeutralTypeForTimestamp(Types.TIMESTAMP, "TIMESTAMPTZ"));
		assertEquals (NeutralType.DATETIMETZ, DbConvUtils.getNeutralTypeForTimestamp(Types.TIMESTAMP, "timestamp with timezone"));
		assertEquals (NeutralType.DATETIMETZ, DbConvUtils.getNeutralTypeForTimestamp(Types.TIMESTAMP, "datetimeoffset"));
		assertEquals (NeutralType.DATETIMETZ, DbConvUtils.getNeutralTypeForTimestamp(Types.TIMESTAMP, "datetime offset"));
		assertEquals (NeutralType.DATETIMETZ, DbConvUtils.getNeutralTypeForTimestamp(Types.TIMESTAMP_WITH_TIMEZONE, "foo"));
	}
	@Test
	public void testGetNeutralTypeForTime() {
		assertEquals (NeutralType.TIME,   DbConvUtils.getNeutralTypeForTime(Types.TIME, "time"));
		assertEquals (NeutralType.TIME,   DbConvUtils.getNeutralTypeForTime(Types.TIME, "foo"));
		assertEquals (NeutralType.TIMETZ, DbConvUtils.getNeutralTypeForTime(Types.TIME, "timetz"));
		assertEquals (NeutralType.TIMETZ, DbConvUtils.getNeutralTypeForTime(Types.TIME, "TIMETZ"));
		assertEquals (NeutralType.TIMETZ, DbConvUtils.getNeutralTypeForTime(Types.TIME, "time with timezone"));
		assertEquals (NeutralType.TIMETZ, DbConvUtils.getNeutralTypeForTime(Types.TIME, "timeoffset"));
		assertEquals (NeutralType.TIMETZ, DbConvUtils.getNeutralTypeForTime(Types.TIME, "time offset"));
		assertEquals (NeutralType.TIMETZ, DbConvUtils.getNeutralTypeForTime(Types.TIME, "foo offset"));
		assertEquals (NeutralType.TIMETZ, DbConvUtils.getNeutralTypeForTime(Types.TIME_WITH_TIMEZONE, "foo"));
	}
}
