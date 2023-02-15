package org.telosys.tools.dsl.parser.commons;

import java.math.BigDecimal;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class ParamValueTest {
	
//	public ParamValue buildParamValue(String annotationOrTagName, String rawParameterValue) {
//		return new ParamValue("MyEntity", "myField", 
//				annotationOrTagName, rawParameterValue, 
//				ParamValueOrigin.FIELD_ANNOTATION );
//	}
	
	public ParamValue buildParamValue(String rawParameterValue) {
		return new ParamValue("MyEntity", rawParameterValue);
	}
	
	@Test
	public void test1() throws ParamError {

		assertEquals("'abc'",   buildParamValue("  'abc'  ").getAsString() );
		assertEquals("'a'b'c'", buildParamValue("  'a'b'c'  ").getAsString() );
		assertEquals("a", buildParamValue("  a ").getAsString() );
		assertEquals("a 1 e", buildParamValue("  a 1 e   ").getAsString() );
		assertEquals("say \"Hello\"", buildParamValue("say \"Hello\"").getAsString() );
		
		// with double quotes
		assertEquals(" abc ", buildParamValue("\" abc \"").getAsString() );
		assertEquals(" a\"bc ", buildParamValue("\" a\\\"bc \"").getAsString() );

		assertEquals(Integer.valueOf(123), buildParamValue("123").getAsInteger() );

		assertEquals(new BigDecimal(123), buildParamValue("123").getAsBigDecimal() );

		assertEquals(true, buildParamValue("true").getAsBoolean() );
		assertEquals(true, buildParamValue("True").getAsBoolean() );
		assertEquals(true, buildParamValue("TRUE").getAsBoolean() );
		assertEquals(false, buildParamValue("false").getAsBoolean() );
		assertEquals(false, buildParamValue("False").getAsBoolean() );
	}

	@Test ( expected=ParamError.class)
	public void testErr1() throws ParamError {
		buildParamValue("xyz").getAsBoolean();
	}
	@Test ( expected=ParamError.class)
	public void testErr2() throws ParamError {
		buildParamValue("").getAsBoolean();
	}
	@Test ( expected=ParamError.class)
	public void testErr3() throws ParamError {
		buildParamValue("12AB34").getAsInteger() ;
	}
	@Test ( expected=ParamError.class)
	public void testErr4() throws ParamError {
		buildParamValue("12.34").getAsInteger() ;
	}
}
