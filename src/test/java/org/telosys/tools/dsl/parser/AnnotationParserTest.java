package org.telosys.tools.dsl.parser;

import java.math.BigDecimal;

import org.junit.Test;
import org.telosys.tools.dsl.parser.exceptions.ParsingError;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AnnotationParserTest {

	private AnnotationParser getParser() {
		return new AnnotationParser("MyEntity", "myField");
	}

	private DomainAnnotation parseAnnotation(String annotation) throws ParsingError {
		AnnotationParser parser = getParser();
		return parser.parseAnnotation(annotation);
	}
	

	@Test
	public void testParseAnnotations() throws ParsingError {
		AnnotationParser parser = getParser();
		DomainAnnotation annotation ;
		
		annotation = parser.parseAnnotation("@Id");
		assertEquals("Id", annotation.getName());
		assertFalse(annotation.hasParameter());
//		assertNull(annotation.getParameterAsString());
//		assertNull(annotation.getParameterAsBigDecimal());
//		assertNull(annotation.getParameterAsInteger());
		
		annotation = parser.parseAnnotation("@AutoIncremented");
		assertEquals("AutoIncremented", annotation.getName());
		assertFalse(annotation.hasParameter());

		annotation = parser.parseAnnotation("@Embedded");
		assertEquals("Embedded", annotation.getName());
		assertFalse(annotation.hasParameter());

		annotation = parser.parseAnnotation("@NotBlank");
		assertEquals("NotBlank", annotation.getName());
		assertFalse(annotation.hasParameter());
		
		annotation = parser.parseAnnotation("@Max(123)");
		assertEquals("Max", annotation.getName());
		assertTrue(annotation.hasParameter());
//		assertNull(annotation.getParameterAsString());
		assertEquals(new BigDecimal("123"), annotation.getParameterAsBigDecimal());

		annotation = parser.parseAnnotation("@Min(123.45)");
		assertEquals("Min", annotation.getName());
		assertTrue(annotation.hasParameter());
//		assertNull(annotation.getParameterAsString());
		assertEquals(new BigDecimal("123.45"), annotation.getParameterAsBigDecimal());
		
		annotation = parser.parseAnnotation("@SizeMax(22)");
		assertEquals("SizeMax", annotation.getName());
		assertTrue(annotation.hasParameter());
//		assertNull(annotation.getParameterAsString());
//		assertNull(annotation.getParameterAsBigDecimal());
		assertEquals(new Integer("22"), annotation.getParameterAsInteger());

		annotation = parser.parseAnnotation("@DbSize(22)");
		assertEquals("DbSize", annotation.getName());
		assertTrue(annotation.hasParameter());
		assertNotNull(annotation.getParameterAsString());

		annotation = parser.parseAnnotation("@DbSize(22,4)");
		annotation = parser.parseAnnotation("@DbSize(22,0)");
		//annotation = parser.parse("@DbSize(-22,2)"); // ERR : negative size
		//annotation = parser.parse("@DbSize(22,-2)"); // ERR : negative size
		//annotation = parser.parse("@DbSize(22,)"); // ERR
		//annotation = parser.parse("@DbSize(,)"); // ERR
		//annotation = parser.parse("@DbSize()"); // ERR
		//annotation = parser.parse("@DbSize(aa,2)"); // ERR
		//annotation = parser.parse("@DbSize(aa)"); //ERR
		
	}
	
	@Test
	public void testParseAnnotationDbSize() throws ParsingError {
		AnnotationParser parser = getParser();
		DomainAnnotation annotation ;
		
		annotation = parser.parseAnnotation("@DbSize(22)");
		assertEquals("DbSize", annotation.getName());
		assertTrue(annotation.hasParameter());
		assertNotNull(annotation.getParameterAsString());

		annotation = parser.parseAnnotation("@DbSize(22,4)");
		annotation = parser.parseAnnotation("@DbSize(22,0)");
		
		parseWithExpectedException("@DbSize(-22,2)", parser); // ERR : negative size
		parseWithExpectedException("@DbSize(22,-2)", parser); // ERR : negative size
		parseWithExpectedException("@DbSize(22,)", parser); // ERR
		parseWithExpectedException("@DbSize(,)", parser); // ERR
		parseWithExpectedException("@DbSize()", parser); // ERR
		parseWithExpectedException("@DbSize(aa,2)", parser); // ERR
		parseWithExpectedException("@DbSize(aa)", parser); //ERR
	}
	
	@Test
	public void testParseAnnotationSize() throws ParsingError {
		AnnotationParser parser = getParser();
		DomainAnnotation annotation ;
		
		annotation = parser.parseAnnotation("@Size(22 )"); 
		assertNotNull(annotation);
		assertNotNull(annotation.getName());
		assertEquals("Size", annotation.getName());
		assertNotNull(annotation.getParameter());
		assertEquals("22", annotation.getParameter());		
		assertTrue(annotation.hasParameter());
		assertNotNull(annotation.getParameterAsString());

		annotation = parser.parseAnnotation("@Size( 22,4 )");
		assertEquals("Size", annotation.getName());
		assertEquals("22,4", annotation.getParameter());		

		parseWithExpectedException("@Size(22 , 0)", parser); // ERR : not a number
		parseWithExpectedException("@Size(-22,2)", parser); // ERR : negative size
		parseWithExpectedException("@Size(22,-2)", parser); // ERR : negative size
		parseWithExpectedException("@Size(22,)", parser); // ERR
		parseWithExpectedException("@Size(,)", parser); // ERR
		parseWithExpectedException("@Size()", parser); // ERR
		parseWithExpectedException("@Size(aa,2)", parser); // ERR
		parseWithExpectedException("@Size(aa)", parser); //ERR
	}
	
	private void parseWithExpectedException(String s, AnnotationParser parser) {
		ParsingError error = null ;
		try {
			parser.parseAnnotation(s);
		} catch (ParsingError e) {
			error = e;
		} 
		assertNotNull(error);
	}
	
	@Test(expected = ParsingError.class)
	public void testParseAnnotationError1() throws ParsingError {
		AnnotationParser parser = getParser();
		parser.parseAnnotation("@Max(12RRR3)");
	}

	@Test(expected = ParsingError.class)
	public void testParseAnnotationError2() throws ParsingError {
		AnnotationParser parser = getParser();
		parser.parseAnnotation("@Max()");
	}

	@Test(expected = ParsingError.class)
	public void testParseAnnotationError3() throws ParsingError {
		AnnotationParser parser = getParser();
		parser.parseAnnotation("@Max(123");
	}

	@Test(expected = ParsingError.class)
	public void testParseAnnotationError4() throws ParsingError {
		AnnotationParser parser = getParser();
		parser.parseAnnotation("@Max");
	}

	@Test(expected = ParsingError.class)
	public void testParseAnnotationError5() throws ParsingError {
		AnnotationParser parser = getParser();
		parser.parseAnnotation("@Abcdef"); // unhknown annotation
	}

	@Test(expected = ParsingError.class)
	public void testParseAnnotationError6() throws ParsingError {
		AnnotationParser parser = getParser();
		parser.parseAnnotation("@Maxi(123)"); // unhknown annotation
	}

	@Test(expected = ParsingError.class)
	public void testParseAnnotationError7() throws ParsingError {
		AnnotationParser parser = getParser();
		parser.parseAnnotation("@Id(145)"); // Unexpected parameter
	}

	@Test(expected = ParsingError.class)
	public void testParseAnnotationError8() throws ParsingError {
		AnnotationParser parser = getParser();
		parser.parseAnnotation("@SizeMax(44.55)"); // integer parameter required
	}
	
	@Test(expected = ParsingError.class)
	public void testParseAnnotationError9() throws ParsingError {
		AnnotationParser parser = getParser();
		parser.parseAnnotation("@FooBar"); // unhknown annotation
	}


	@Test(expected = ParsingError.class)
	public void testParseDbSizeError1() throws ParsingError {
		AnnotationParser parser = getParser();
		parser.parseAnnotation("@DbSize(10.2)"); 
	}

	@Test(expected = ParsingError.class)
	public void testParseDbSizeError2() throws ParsingError {
		AnnotationParser parser = getParser();
		parser.parseAnnotation("@DbSize(-22,2)"); 
	}

	@Test(expected = ParsingError.class)
	public void testParseDbSizeError3() throws ParsingError {
		AnnotationParser parser = getParser();
		parser.parseAnnotation("@DbSize(22,-4)"); 
	}

	@Test(expected = ParsingError.class)
	public void testParseDbSizeError4() throws ParsingError {
		AnnotationParser parser = getParser();
		parser.parseAnnotation("@DbSize(22,)"); 
	}

	@Test(expected = ParsingError.class)
	public void testParseDbSizeError5() throws ParsingError {
		AnnotationParser parser = getParser();
		parser.parseAnnotation("@DbSize(,4)"); 
	}

	@Test(expected = ParsingError.class)
	public void testParseDbSizeError6() throws ParsingError {
		AnnotationParser parser = getParser();
		parser.parseAnnotation("@DbSize(,)"); 
	}

	@Test(expected = ParsingError.class)
	public void testParseDbSizeError7() throws ParsingError {
		AnnotationParser parser = getParser();
		parser.parseAnnotation("@DbSize()"); 
	}

	@Test(expected = ParsingError.class)
	public void testParseDbSizeError8() throws ParsingError {
		AnnotationParser parser = getParser();
		parser.parseAnnotation("@DbSize(aa,2)"); 
	}
}
