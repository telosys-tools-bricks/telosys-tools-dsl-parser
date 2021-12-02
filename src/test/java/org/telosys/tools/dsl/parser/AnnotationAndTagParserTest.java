package org.telosys.tools.dsl.parser;

import org.junit.Test;
import org.telosys.tools.dsl.parser.exceptions.ParsingError;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class AnnotationAndTagParserTest {

	private AnnotationAndTagParser getParser(int i) {
		if ( i == 1 ) {
			// use "AnnotationParser" as concrete class
			return new AnnotationParser("MyEntity", "myField");
		}
		else {
			// use "TagParser" as concrete class
			return new TagParser("MyEntity", "myField");
		}
	}
	
	@Test
	public void testGetName() throws ParsingError {
		for ( int i = 1 ; i <= 2 ; i++ ) {
			AnnotationAndTagParser parser = getParser(i);
			assertEquals("NotBlank", parser.getName("@NotBlank"));
			assertEquals("Foo", parser.getName("@Foo"));
			assertEquals("MyTag", parser.getName("#MyTag"));
			assertEquals("Foo", parser.getName("@Foo()"));
			assertEquals("Foo", parser.getName("@Foo(azer)"));
			assertEquals("Foo", parser.getName("@Foo('azer')"));
			assertEquals("Foo", parser.getName("@Foo(\"a(z)er\")"));
			assertEquals("Foo", parser.getName("@Foo(123)"));
			assertEquals("Foo", parser.getName("@Foo(123.45)"));
			assertEquals("Foo", parser.getName("@Foo ( 123.45 ) "));
			assertEquals("Foo", parser.getName("?Foo ( 123.45 ) "));
		}
	}

	@Test(expected = ParsingError.class)
	public void testGetNameError1() throws ParsingError {
		AnnotationAndTagParser parser = getParser(1);
		parser.getName("@Fo o ( 123.45  )");
	}

	@Test(expected = ParsingError.class)
	public void testGetNameError2() throws ParsingError {
		AnnotationAndTagParser parser = getParser(2);
		parser.getName("@ Foo ( 123.45  )");
	}

	@Test
	public void testGetParameterValue() throws ParsingError {
		for ( int i = 1 ; i <= 2 ; i++ ) {	
			AnnotationAndTagParser parser = getParser(i);
	
			assertNull(parser.getParameterValue("@Foo"));
			assertNull(parser.getParameterValue("#Tag"));
	
			assertEquals("", parser.getParameterValue("@Foo()"));
			assertEquals("", parser.getParameterValue("@Foo(   )"));
			assertEquals("azer", parser.getParameterValue("@Foo(azer)"));
			assertEquals("'azer'", parser.getParameterValue("@Foo('azer')"));
			assertEquals("\"azer\"", parser.getParameterValue("@Foo(\"azer\")"));
			assertEquals("\"a(z)er\"", parser.getParameterValue("@Foo(\"a(z)er\")"));
			assertEquals("123", parser.getParameterValue("@Foo(123)"));
			assertEquals("123.45", parser.getParameterValue("@Foo(123.45)"));
			assertEquals("123.45", parser.getParameterValue("@Foo ( 123.45 ) "));
			assertEquals("123.45", parser.getParameterValue("?Foo ( 123.45 ) "));
			assertEquals("(123)", parser.getParameterValue("@Foo((123))"));
			assertEquals("(123)", parser.getParameterValue("@Foo( (123)  )"));
	
			assertEquals("", parser.getParameterValue("#Tag()"));
			assertEquals("", parser.getParameterValue("#Tag(   )"));
			assertEquals("123.45", parser.getParameterValue("#Tag ( 123.45 ) "));
		}
	}

}
