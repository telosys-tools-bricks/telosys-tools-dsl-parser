package org.telosys.tools.dsl.parser;

import java.math.BigDecimal;

import org.junit.Test;
import org.telosys.tools.dsl.parser.exceptions.AnnotationOrTagError;
import org.telosys.tools.dsl.parser.model.DomainAnnotationOrTag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class AnnotationOrTagParserTest {

	@Test
	public void testGetName() throws AnnotationOrTagError {

		FieldAnnotationOrTagParser parser = new FieldAnnotationOrTagParser("MyEntity", "myField");

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

	@Test(expected = AnnotationOrTagError.class)
	public void testGetNameError1() throws AnnotationOrTagError {
		FieldAnnotationOrTagParser parser = new FieldAnnotationOrTagParser("MyEntity", "myField");
		parser.getName("@Fo o ( 123.45  )");
	}

	@Test(expected = AnnotationOrTagError.class)
	public void testGetNameError2() throws AnnotationOrTagError {
		FieldAnnotationOrTagParser parser = new FieldAnnotationOrTagParser("MyEntity", "myField");
		parser.getName("@ Foo ( 123.45  )");
	}

	@Test
	public void testGetParameterValue() throws AnnotationOrTagError {

		FieldAnnotationOrTagParser parser = new FieldAnnotationOrTagParser("MyEntity", "myField");

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
	public void testParseAnnotations() throws AnnotationOrTagError {

		FieldAnnotationOrTagParser parser = new FieldAnnotationOrTagParser("MyEntity", "myField");

		DomainAnnotationOrTag annotation ;
		
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

		annotation = parser.parse("@DbSize(22)");
		assertEquals("DbSize", annotation.getName());
		assertTrue(annotation.hasParameter());
		assertNotNull(annotation.getParameterAsString());

		annotation = parser.parse("@DbSize(22,4)");
		annotation = parser.parse("@DbSize(22,0)");
		//annotation = parser.parse("@DbSize(-22,2)"); // ERR : negative size
		//annotation = parser.parse("@DbSize(22,-2)"); // ERR : negative size
		//annotation = parser.parse("@DbSize(22,)"); // ERR
		//annotation = parser.parse("@DbSize(,)"); // ERR
		//annotation = parser.parse("@DbSize()"); // ERR
		//annotation = parser.parse("@DbSize(aa,2)"); // ERR
		//annotation = parser.parse("@DbSize(aa)"); //ERR
		
	}

	@Test(expected = AnnotationOrTagError.class)
	public void testParseAnnotationError1() throws AnnotationOrTagError {
		FieldAnnotationOrTagParser parser = new FieldAnnotationOrTagParser("MyEntity", "myField");
		parser.parse("@Max(12RRR3)");
	}

	@Test(expected = AnnotationOrTagError.class)
	public void testParseAnnotationError2() throws AnnotationOrTagError {
		FieldAnnotationOrTagParser parser = new FieldAnnotationOrTagParser("MyEntity", "myField");
		parser.parse("@Max(12RRR3)");
	}

	@Test(expected = AnnotationOrTagError.class)
	public void testParseAnnotationError3() throws AnnotationOrTagError {
		FieldAnnotationOrTagParser parser = new FieldAnnotationOrTagParser("MyEntity", "myField");
		parser.parse("@Max(123");
	}

	@Test(expected = AnnotationOrTagError.class)
	public void testParseAnnotationError4() throws AnnotationOrTagError {
		FieldAnnotationOrTagParser parser = new FieldAnnotationOrTagParser("MyEntity", "myField");
		parser.parse("@Max");
	}

	@Test(expected = AnnotationOrTagError.class)
	public void testParseAnnotationError5() throws AnnotationOrTagError {
		FieldAnnotationOrTagParser parser = new FieldAnnotationOrTagParser("MyEntity", "myField");
		parser.parse("@Abcdef"); // unhknown annotation
	}

	@Test(expected = AnnotationOrTagError.class)
	public void testParseAnnotationError6() throws AnnotationOrTagError {
		FieldAnnotationOrTagParser parser = new FieldAnnotationOrTagParser("MyEntity", "myField");
		//parser.parse("@Maximum(123)"); // unhknown annotation
		parser.parse("@Maxi(123)"); // unhknown annotation
	}

	@Test(expected = AnnotationOrTagError.class)
	public void testParseAnnotationError7() throws AnnotationOrTagError {
		FieldAnnotationOrTagParser parser = new FieldAnnotationOrTagParser("MyEntity", "myField");
		parser.parse("@Id(145)"); // Unexpected parameter
	}

	@Test(expected = AnnotationOrTagError.class)
	public void testParseAnnotationError8() throws AnnotationOrTagError {
		FieldAnnotationOrTagParser parser = new FieldAnnotationOrTagParser("MyEntity", "myField");
		parser.parse("@SizeMax(44.55)"); // integer parameter required
	}
	
	@Test(expected = AnnotationOrTagError.class)
	public void testParseAnnotationError9() throws AnnotationOrTagError {
		FieldAnnotationOrTagParser parser = new FieldAnnotationOrTagParser("MyEntity", "myField");
		parser.parse("@FooBar"); // unhknown annotation
	}


	@Test(expected = AnnotationOrTagError.class)
	public void testParseDbSizeError1() throws AnnotationOrTagError {
		FieldAnnotationOrTagParser parser = new FieldAnnotationOrTagParser("MyEntity", "myField");
		parser.parse("@DbSize(10.2)"); 
	}

	@Test(expected = AnnotationOrTagError.class)
	public void testParseDbSizeError2() throws AnnotationOrTagError {
		FieldAnnotationOrTagParser parser = new FieldAnnotationOrTagParser("MyEntity", "myField");
		parser.parse("@DbSize(-22,2)"); 
	}

	@Test(expected = AnnotationOrTagError.class)
	public void testParseDbSizeError3() throws AnnotationOrTagError {
		FieldAnnotationOrTagParser parser = new FieldAnnotationOrTagParser("MyEntity", "myField");
		parser.parse("@DbSize(22,-4)"); 
	}

	@Test(expected = AnnotationOrTagError.class)
	public void testParseDbSizeError4() throws AnnotationOrTagError {
		FieldAnnotationOrTagParser parser = new FieldAnnotationOrTagParser("MyEntity", "myField");
		parser.parse("@DbSize(22,)"); 
	}

	@Test(expected = AnnotationOrTagError.class)
	public void testParseDbSizeError5() throws AnnotationOrTagError {
		FieldAnnotationOrTagParser parser = new FieldAnnotationOrTagParser("MyEntity", "myField");
		parser.parse("@DbSize(,4)"); 
	}

	@Test(expected = AnnotationOrTagError.class)
	public void testParseDbSizeError6() throws AnnotationOrTagError {
		FieldAnnotationOrTagParser parser = new FieldAnnotationOrTagParser("MyEntity", "myField");
		parser.parse("@DbSize(,)"); 
	}

	@Test(expected = AnnotationOrTagError.class)
	public void testParseDbSizeError7() throws AnnotationOrTagError {
		FieldAnnotationOrTagParser parser = new FieldAnnotationOrTagParser("MyEntity", "myField");
		parser.parse("@DbSize()"); 
	}

	@Test(expected = AnnotationOrTagError.class)
	public void testParseDbSizeError8() throws AnnotationOrTagError {
		FieldAnnotationOrTagParser parser = new FieldAnnotationOrTagParser("MyEntity", "myField");
		parser.parse("@DbSize(aa,2)"); 
	}
}
