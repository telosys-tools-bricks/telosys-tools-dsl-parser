package org.telosys.tools.dsl.parser.commons;

import java.math.BigDecimal;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class ParamValueTest {
	
	public ParamValue buildParamValue(String annotationOrTagName, String rawParameterValue) {
		return new ParamValue("MyEntity", "myField", 
				annotationOrTagName, rawParameterValue, 
				ParamValueOrigin.FIELD_ANNOTATION );
	}
	
	@Test
	public void test1() throws ParamError {

		assertEquals("'abc'",   buildParamValue("DefaultValue", "  'abc'  ").getAsString() );
		assertEquals("'a'b'c'", buildParamValue("DefaultValue", "  'a'b'c'  ").getAsString() );
		assertEquals("a", buildParamValue("DefaultValue", "  a ").getAsString() );
		assertEquals("a 1 e", buildParamValue("DefaultValue", "  a 1 e   ").getAsString() );
		assertEquals("say \"Hello\"", buildParamValue("DefaultValue", "say \"Hello\"").getAsString() );
		
		// with double quotes
		assertEquals(" abc ", buildParamValue("DefaultValue", "\" abc \"").getAsString() );
		assertEquals(" a\"bc ", buildParamValue("DefaultValue", "\" a\\\"bc \"").getAsString() );

		assertEquals(Integer.valueOf(123), buildParamValue("XxxYyyy", "123").getAsInteger() );

		assertEquals(new BigDecimal(123), buildParamValue("XxxYyyy", "123").getAsBigDecimal() );

		assertEquals(true, buildParamValue("XxxYyyy", "true").getAsBoolean() );
		assertEquals(true, buildParamValue("XxxYyyy", "True").getAsBoolean() );
		assertEquals(true, buildParamValue("XxxYyyy", "TRUE").getAsBoolean() );
		assertEquals(false, buildParamValue("XxxYyyy", "false").getAsBoolean() );
		assertEquals(false, buildParamValue("XxxYyyy", "False").getAsBoolean() );
	}

	@Test ( expected=ParamError.class)
	public void testErr1() throws ParamError {
		buildParamValue("XxxYyyy", "xyz").getAsBoolean();
	}
	@Test ( expected=ParamError.class)
	public void testErr2() throws ParamError {
		buildParamValue("XxxYyyy", "").getAsBoolean();
	}
	@Test ( expected=ParamError.class)
	public void testErr3() throws ParamError {
		buildParamValue("XxxYyyy", "12AB34").getAsInteger() ;
	}
	@Test ( expected=ParamError.class)
	public void testErr4() throws ParamError {
		buildParamValue("XxxYyyy", "12.34").getAsInteger() ;
	}
}
