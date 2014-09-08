package org.telosys.tools.dsl.parser;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.telosys.tools.dsl.EntityParserException;
import org.telosys.tools.dsl.parser.model.DomainEnumerationItem;
import org.telosys.tools.dsl.parser.model.DomainEnumerationType;

public class FieldEnumParserTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testParseValidString() {
        String testValid = "TEST=\"string1\"";
        FieldEnumParser parser = new FieldEnumParser();
        DomainEnumerationItem toCompare = new DomainEnumerationItem("TEST", "string1");
        Assert.assertEquals(toCompare, parser.parseField(testValid, DomainEnumerationType.STRING));
	}
	
	@Test
	public void testParseValidDecimal() {
        String testValid = "TEST=4.3";
        FieldEnumParser parser = new FieldEnumParser();
        DomainEnumerationItem toCompare = new DomainEnumerationItem("TEST", new BigDecimal("4.3"));
        Assert.assertEquals(toCompare, parser.parseField(testValid, DomainEnumerationType.DECIMAL));
	}
	
	@Test
	public void testParseValidInteger() {
        String testValid = "TEST=4";
        FieldEnumParser parser = new FieldEnumParser();
        DomainEnumerationItem toCompare = new DomainEnumerationItem("TEST", new BigInteger("4"));
        Assert.assertEquals(toCompare, parser.parseField(testValid, DomainEnumerationType.INTEGER));
	}
	
	@Test(expected=EntityParserException.class)
	public void testParseWithAnEmptyName() {
        String testInvalid = "=4";
        FieldEnumParser parser = new FieldEnumParser();
        parser.parseField(testInvalid, DomainEnumerationType.INTEGER);
	}
	
	@Test(expected=EntityParserException.class)
	public void testParseWithAnEmptyValue() {
        String testInvalid = "TEST=";
        FieldEnumParser parser = new FieldEnumParser();
        parser.parseField(testInvalid, DomainEnumerationType.INTEGER);
	}

    @Test(expected=EntityParserException.class)
    public void testParseWithoutValue() {
        String testInvalid = "TEST";
        FieldEnumParser parser = new FieldEnumParser();
        parser.parseField(testInvalid, DomainEnumerationType.INTEGER);
    }
	
	@Test(expected=EntityParserException.class)
	public void testParseWithAnEmptyValueString() {
        String testInvalid = "TEST=\"\"";
        FieldEnumParser parser = new FieldEnumParser();
        parser.parseField(testInvalid, DomainEnumerationType.STRING);
	}
	
	@Test(expected=EntityParserException.class)
	public void testParseWithSimpleQuoteString() {
        String testInvalid = "TEST=\'abc\'";
        FieldEnumParser parser = new FieldEnumParser();
        parser.parseField(testInvalid, DomainEnumerationType.STRING);
	}
	
	@Test(expected=EntityParserException.class)
	public void testParseWithoutQuoteString() {
        String testInvalid = "TEST=3";
        FieldEnumParser parser = new FieldEnumParser();
        parser.parseField(testInvalid, DomainEnumerationType.STRING);
	}

    @Test
    public void testIsItemWithoutValue() {
        FieldEnumParser parser = new FieldEnumParser();

        String testWithValue = "TEST=3";
        Assert.assertFalse(parser.isItemWithoutValue(testWithValue));

        String testWithoutValue = "TEST";
        Assert.assertTrue(parser.isItemWithoutValue(testWithoutValue));
    }

    @Test
    public void testParseFieldIncrementation() {
        String fieldInfo = "TEST";

        DomainEnumerationItem compareTo = new DomainEnumerationItem("TEST", new BigInteger("2"));

        FieldEnumParser parser = new FieldEnumParser();
        Assert.assertEquals(compareTo, parser.parseField(fieldInfo, new BigInteger("1")));
    }

    @Test(expected = EntityParserException.class)
    public void testParseFieldIncrementationInvalid() {
        String fieldInfo = "TEST=2";

        FieldEnumParser parser = new FieldEnumParser();
        parser.parseField(fieldInfo, new BigInteger("1"));
    }

    @Test(expected = EntityParserException.class)
    public void testParseFieldIncrementationWithInvalidName() {
        String fieldInfo = "T_#EST";

        FieldEnumParser parser = new FieldEnumParser();
        parser.parseField(fieldInfo, new BigInteger("1"));
    }

    @Test(expected = EntityParserException.class)
    public void testParseFieldWithInvalidName() {
        String fieldInfo = "T_#^EST=3";

        FieldEnumParser parser = new FieldEnumParser();
        parser.parseField(fieldInfo, DomainEnumerationType.INTEGER);
    }
}
