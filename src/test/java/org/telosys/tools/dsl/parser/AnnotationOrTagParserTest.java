package org.telosys.tools.dsl.parser;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Test;
import org.telosys.tools.dsl.DslParserException;
import org.telosys.tools.dsl.parser.model.DomainEntityFieldAnnotation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class AnnotationOrTagParserTest {

	private void print(List<String> list) {
		System.out.println("\n-----");
		for (String s : list) {
			System.out.println(" . [" + s + "]");
		}
	}

	private void print(Exception e) {
		System.out.println("\n----- EXCEPTION ");
		System.out.println(e.getClass().getName() + " : " + e.getMessage());
	}

	@Test
	public void testGetName() {

		AnnotationOrTagParser parser = new AnnotationOrTagParser("MyEntity", "myField");

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

	@Test(expected = DslParserException.class)
	public void testGetNameError1() {
		AnnotationOrTagParser parser = new AnnotationOrTagParser("MyEntity", "myField");
		parser.getName("@Fo o ( 123.45  )");
	}

	@Test(expected = DslParserException.class)
	public void testGetNameError2() {
		AnnotationOrTagParser parser = new AnnotationOrTagParser("MyEntity", "myField");
		parser.getName("@ Foo ( 123.45  )");
	}

	@Test
	public void testGetParameterValue() {

		AnnotationOrTagParser parser = new AnnotationOrTagParser("MyEntity", "myField");

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

	@Test
	public void testParseAnnotation() {

		AnnotationOrTagParser parser = new AnnotationOrTagParser("MyEntity", "myField");

		DomainEntityFieldAnnotation annotation ;
		
		annotation = parser.parseAnnotation("@Id");
		assertEquals("Id", annotation.getName());
		assertFalse(annotation.hasParameter());
		assertNull(annotation.getParameterAsString());
		assertNull(annotation.getParameterAsBigDecimal());
		assertNull(annotation.getParameterAsInteger());
		
		annotation = parser.parseAnnotation("@AutoIncremented");
		assertEquals("AutoIncremented", annotation.getName());
		assertFalse(annotation.hasParameter());

		annotation = parser.parse("@Embedded");
		assertEquals("Embedded", annotation.getName());
		assertFalse(annotation.hasParameter());

		annotation = parser.parse("@NotBlank");
		assertEquals("NotBlank", annotation.getName());
		assertFalse(annotation.hasParameter());
		
		annotation = parser.parse("@Max(123)");
		assertEquals("Max", annotation.getName());
		assertTrue(annotation.hasParameter());
		assertNull(annotation.getParameterAsString());
		assertEquals(new BigDecimal("123"), annotation.getParameterAsBigDecimal());

		annotation = parser.parse("@Min(123.45)");
		assertEquals("Min", annotation.getName());
		assertTrue(annotation.hasParameter());
		assertNull(annotation.getParameterAsString());
		assertEquals(new BigDecimal("123.45"), annotation.getParameterAsBigDecimal());
		
		annotation = parser.parse("@SizeMax(22)");
		assertEquals("SizeMax", annotation.getName());
		assertTrue(annotation.hasParameter());
		assertNull(annotation.getParameterAsString());
		assertNull(annotation.getParameterAsBigDecimal());
		assertEquals(new Integer("22"), annotation.getParameterAsInteger());

	}

	@Test(expected = DslParserException.class)
	public void testParseAnnotationError1() {
		AnnotationOrTagParser parser = new AnnotationOrTagParser("MyEntity", "myField");
		parser.parse("@Max(12RRR3)");
	}

	@Test(expected = DslParserException.class)
	public void testParseAnnotationError2() {
		AnnotationOrTagParser parser = new AnnotationOrTagParser("MyEntity", "myField");
		parser.parse("@Max(12RRR3)");
	}

	@Test(expected = DslParserException.class)
	public void testParseAnnotationError3() {
		AnnotationOrTagParser parser = new AnnotationOrTagParser("MyEntity", "myField");
		parser.parse("@Max(123");
	}

	@Test(expected = DslParserException.class)
	public void testParseAnnotationError4() {
		AnnotationOrTagParser parser = new AnnotationOrTagParser("MyEntity", "myField");
		parser.parse("@Max");
	}

	@Test(expected = DslParserException.class)
	public void testParseAnnotationError5() {
		AnnotationOrTagParser parser = new AnnotationOrTagParser("MyEntity", "myField");
		parser.parse("@Abcdef"); // unhknown annotation
	}

	@Test(expected = DslParserException.class)
	public void testParseAnnotationError6() {
		AnnotationOrTagParser parser = new AnnotationOrTagParser("MyEntity", "myField");
		//parser.parse("@Maximum(123)"); // unhknown annotation
		parser.parse("@Maxi(123)"); // unhknown annotation
	}

	@Test(expected = DslParserException.class)
	public void testParseAnnotationError7() {
		AnnotationOrTagParser parser = new AnnotationOrTagParser("MyEntity", "myField");
		parser.parse("@Id(145)"); // Unexpected parameter
	}

	@Test(expected = DslParserException.class)
	public void testParseAnnotationError8() {
		AnnotationOrTagParser parser = new AnnotationOrTagParser("MyEntity", "myField");
		parser.parse("@SizeMax(44.55)"); // integer parameter required
	}
}
