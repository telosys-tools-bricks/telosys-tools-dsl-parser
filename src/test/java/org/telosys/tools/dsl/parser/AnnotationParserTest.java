package org.telosys.tools.dsl.parser;

import java.math.BigDecimal;

import org.junit.Test;
import org.telosys.tools.dsl.DslModelError;
import org.telosys.tools.dsl.parser.AnnotationProcessor;
import org.telosys.tools.dsl.parser.Element;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AnnotationParserTest {

//	private AnnotationParser getParser() {
//		return new AnnotationParser("MyEntity", "myField");
//	}

	private DomainAnnotation parseAnnotation(String annotation) throws DslModelError {
//		AnnotationParser parser = getParser();
//		return parser.parseAnnotation(annotation);
		
		Element element = new Element(2, annotation);
		AnnotationProcessor annotationProcessor = new AnnotationProcessor("MyEntity", "myField");
		return annotationProcessor.parseAnnotation(element);
	}
	
	private void parseWithExpectedException(String s) {
		DslModelError error = null ;
		try {
			parseAnnotation(s);
		} catch (DslModelError e) {
			error = e;
		} 
		assertNotNull(error);
	}
	

	@Test
	public void testParseAnnotations() throws DslModelError {
//		AnnotationParser parser = getParser();
//		DomainAnnotation annotation ;
		
		DomainAnnotation annotation = parseAnnotation("@Id");
		assertEquals("Id", annotation.getName());
		assertFalse(annotation.hasParameter());
//		assertNull(annotation.getParameterAsString());
//		assertNull(annotation.getParameterAsBigDecimal());
//		assertNull(annotation.getParameterAsInteger());
		
		annotation = parseAnnotation("@AutoIncremented");
		assertEquals("AutoIncremented", annotation.getName());
		assertFalse(annotation.hasParameter());

		annotation = parseAnnotation("@Embedded");
		assertEquals("Embedded", annotation.getName());
		assertFalse(annotation.hasParameter());

		annotation = parseAnnotation("@NotBlank");
		assertEquals("NotBlank", annotation.getName());
		assertFalse(annotation.hasParameter());
		
		annotation = parseAnnotation("@Max(123)");
		assertEquals("Max", annotation.getName());
		assertTrue(annotation.hasParameter());
//		assertNull(annotation.getParameterAsString());
		assertEquals(new BigDecimal("123"), annotation.getParameterAsBigDecimal());

		annotation = parseAnnotation("@Min(123.45)");
		assertEquals("Min", annotation.getName());
		assertTrue(annotation.hasParameter());
//		assertNull(annotation.getParameterAsString());
		assertEquals(new BigDecimal("123.45"), annotation.getParameterAsBigDecimal());
		
		annotation = parseAnnotation("@SizeMax(22)");
		assertEquals("SizeMax", annotation.getName());
		assertTrue(annotation.hasParameter());
//		assertNull(annotation.getParameterAsString());
//		assertNull(annotation.getParameterAsBigDecimal());
		assertEquals(new Integer("22"), annotation.getParameterAsInteger());

		annotation = parseAnnotation("@DbSize(22)");
		assertEquals("DbSize", annotation.getName());
		assertTrue(annotation.hasParameter());
		assertNotNull(annotation.getParameterAsString());

		annotation = parseAnnotation("@DbSize(22,4)");
		annotation = parseAnnotation("@DbSize(22,0)");
		//annotation = parser.parse("@DbSize(-22,2)"); // ERR : negative size
		//annotation = parser.parse("@DbSize(22,-2)"); // ERR : negative size
		//annotation = parser.parse("@DbSize(22,)"); // ERR
		//annotation = parser.parse("@DbSize(,)"); // ERR
		//annotation = parser.parse("@DbSize()"); // ERR
		//annotation = parser.parse("@DbSize(aa,2)"); // ERR
		//annotation = parser.parse("@DbSize(aa)"); //ERR
		
	}
	
	@Test
	public void testParseAnnotationDbSize() throws DslModelError {
//		AnnotationParser parser = getParser();
//		DomainAnnotation annotation ;
		
		DomainAnnotation annotation = parseAnnotation("@DbSize(22)");
		assertEquals("DbSize", annotation.getName());
		assertTrue(annotation.hasParameter());
		assertNotNull(annotation.getParameterAsString());

		annotation = parseAnnotation("@DbSize(22,4)");
		annotation = parseAnnotation("@DbSize(22,0)");
		
		parseWithExpectedException("@DbSize(-22,2)"); // ERR : negative size
		parseWithExpectedException("@DbSize(22,-2)"); // ERR : negative size
		parseWithExpectedException("@DbSize(22,)"); // ERR
		parseWithExpectedException("@DbSize(,)"); // ERR
		parseWithExpectedException("@DbSize()"); // ERR
		parseWithExpectedException("@DbSize(aa,2)"); // ERR
		parseWithExpectedException("@DbSize(aa)"); //ERR
	}
	
	@Test
	public void testParseAnnotationSize() throws DslModelError {
		
		DomainAnnotation annotation = parseAnnotation("@Size(22 )"); 
		assertNotNull(annotation);
		assertNotNull(annotation.getName());
		assertEquals("Size", annotation.getName());
		assertNotNull(annotation.getParameter());
		assertEquals("22", annotation.getParameter());		
		assertTrue(annotation.hasParameter());
		assertNotNull(annotation.getParameterAsString());

		annotation = parseAnnotation("@Size( 22,4 )");
		assertEquals("Size", annotation.getName());
		assertEquals("22,4", annotation.getParameter());		

		parseWithExpectedException("@Size(22 , 0)"); // ERR : not a number
		parseWithExpectedException("@Size(-22,2)"); // ERR : negative size
		parseWithExpectedException("@Size(22,-2)"); // ERR : negative size
		parseWithExpectedException("@Size(22,)"); // ERR
		parseWithExpectedException("@Size(,)"); // ERR
		parseWithExpectedException("@Size()"); // ERR
		parseWithExpectedException("@Size(aa,2)"); // ERR
		parseWithExpectedException("@Size(aa)"); //ERR
	}
	
	@Test(expected = DslModelError.class)
	public void testParseAnnotationError1() throws DslModelError {
		parseAnnotation("@Max(12RRR3)");
	}

	@Test(expected = DslModelError.class)
	public void testParseAnnotationError2() throws DslModelError {
		parseAnnotation("@Max()");
	}

	@Test(expected = DslModelError.class)
	public void testParseAnnotationError3() throws DslModelError {
		parseAnnotation("@Max(123");
	}

	@Test(expected = DslModelError.class)
	public void testParseAnnotationError4() throws DslModelError {
		parseAnnotation("@Max");
	}

	@Test(expected = DslModelError.class)
	public void testParseAnnotationError5() throws DslModelError {
		parseAnnotation("@Abcdef"); // unhknown annotation
	}

	@Test(expected = DslModelError.class)
	public void testParseAnnotationError6() throws DslModelError {
		parseAnnotation("@Maxi(123)"); // unhknown annotation
	}

	@Test(expected = DslModelError.class)
	public void testParseAnnotationError7() throws DslModelError {
		parseAnnotation("@Id(145)"); // Unexpected parameter
	}

	@Test(expected = DslModelError.class)
	public void testParseAnnotationError8() throws DslModelError {
		parseAnnotation("@SizeMax(44.55)"); // integer parameter required
	}
	
	@Test(expected = DslModelError.class)
	public void testParseAnnotationError9() throws DslModelError {
		parseAnnotation("@FooBar"); // unhknown annotation
	}


	@Test(expected = DslModelError.class)
	public void testParseDbSizeError1() throws DslModelError {
		parseAnnotation("@DbSize(10.2)"); 
	}

	@Test(expected = DslModelError.class)
	public void testParseDbSizeError2() throws DslModelError {
		parseAnnotation("@DbSize(-22,2)"); 
	}

	@Test(expected = DslModelError.class)
	public void testParseDbSizeError3() throws DslModelError {
		parseAnnotation("@DbSize(22,-4)"); 
	}

	@Test(expected = DslModelError.class)
	public void testParseDbSizeError4() throws DslModelError {
		parseAnnotation("@DbSize(22,)"); 
	}

	@Test(expected = DslModelError.class)
	public void testParseDbSizeError5() throws DslModelError {
		parseAnnotation("@DbSize(,4)"); 
	}

	@Test(expected = DslModelError.class)
	public void testParseDbSizeError6() throws DslModelError {
		parseAnnotation("@DbSize(,)"); 
	}

	@Test(expected = DslModelError.class)
	public void testParseDbSizeError7() throws DslModelError {
		parseAnnotation("@DbSize()"); 
	}

	@Test(expected = DslModelError.class)
	public void testParseDbSizeError8() throws DslModelError {
		parseAnnotation("@DbSize(aa,2)"); 
	}
}
