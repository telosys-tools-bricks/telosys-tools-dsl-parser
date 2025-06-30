package org.telosys.tools.dsl.parser;

import java.math.BigDecimal;

import org.junit.Test;
import org.telosys.tools.dsl.DslModelError;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;
import org.telosys.tools.dsl.parser.model.DomainCardinality;
import org.telosys.tools.dsl.parser.model.DomainEntityType;
import org.telosys.tools.dsl.parser.model.DomainField;
import org.telosys.tools.dsl.parser.model.DomainNeutralType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AnnotationParserTest {

	private DomainAnnotation parseAnnotationInAttribute(String annotation) throws DslModelError {
		Element element = new Element(2, annotation);
		// Field : Attribute
		DomainField field = new DomainField(12, "name", new DomainNeutralType("string") );
		AnnotationProcessor annotationProcessor =  new AnnotationProcessor("MyEntity", field);
		
		return annotationProcessor.parseAnnotation(element);
	}
	
	private DomainAnnotation parseAnnotationInLink(String annotation) throws DslModelError {
		Element element = new Element(2, annotation);
		// Field : Link
		DomainField field = new DomainField(12, "country", new DomainEntityType("Country", DomainCardinality.ONE ) );
		AnnotationProcessor annotationProcessor =  new AnnotationProcessor("MyEntity", field);
		
		return annotationProcessor.parseAnnotation(element);
	}
	
	private void parseWithExpectedException(String s) {
		DslModelError error = null ;
		try {
			parseAnnotationInAttribute(s);
		} catch (DslModelError e) {
			error = e;
		} 
		assertNotNull(error);
	}
	

	@Test
	public void testParseAttributeAnnotations() throws DslModelError {
		DomainAnnotation annotation = parseAnnotationInAttribute("@Id");
		assertEquals("Id", annotation.getName());
		assertFalse(annotation.hasParameter());
		
		annotation = parseAnnotationInAttribute("@AutoIncremented");
		assertEquals("AutoIncremented", annotation.getName());
		assertFalse(annotation.hasParameter());

		annotation = parseAnnotationInAttribute("@NotBlank");
		assertEquals("NotBlank", annotation.getName());
		assertFalse(annotation.hasParameter());
		
		annotation = parseAnnotationInAttribute("@Max(123)");
		assertEquals("Max", annotation.getName());
		assertTrue(annotation.hasParameter());
		assertEquals(new BigDecimal("123"), annotation.getParameterAsBigDecimal());

		annotation = parseAnnotationInAttribute("@Min(123.45)");
		assertEquals("Min", annotation.getName());
		assertTrue(annotation.hasParameter());
		assertEquals(new BigDecimal("123.45"), annotation.getParameterAsBigDecimal());
		
		annotation = parseAnnotationInAttribute("@SizeMax(22)");
		assertEquals("SizeMax", annotation.getName());
		assertTrue(annotation.hasParameter());
		assertEquals(new Integer("22"), annotation.getParameterAsInteger());

		annotation = parseAnnotationInAttribute("@DbSize(22)");
		assertEquals("DbSize", annotation.getName());
		assertTrue(annotation.hasParameter());
		assertNotNull(annotation.getParameterAsString());

		annotation = parseAnnotationInAttribute("@DbSize(22,4)");
		annotation = parseAnnotationInAttribute("@DbSize(22,0)");
	}
	@Test
	public void testParseLinkAnnotations() throws DslModelError {
		DomainAnnotation annotation = parseAnnotationInLink("@OneToOne");
		assertEquals("OneToOne", annotation.getName());
		assertFalse(annotation.hasParameter());
		
		annotation = parseAnnotationInLink("@Embedded");
		assertEquals("Embedded", annotation.getName());
		assertFalse(annotation.hasParameter());
	}

	@Test
	public void testParseAnnotationDbSize() throws DslModelError {
		DomainAnnotation annotation = parseAnnotationInAttribute("@DbSize(22)");
		assertEquals("DbSize", annotation.getName());
		assertTrue(annotation.hasParameter());
		assertNotNull(annotation.getParameterAsString());

		annotation = parseAnnotationInAttribute("@DbSize(22,4)");
		assertEquals("DbSize", annotation.getName());
		assertEquals("22,4", annotation.getParameterAsString());
		annotation = parseAnnotationInAttribute("@DbSize(22,0)");
		assertEquals("22,0", annotation.getParameterAsString());
		
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
		
		DomainAnnotation annotation = parseAnnotationInAttribute("@Size(22 )"); 
		assertNotNull(annotation);
		assertNotNull(annotation.getName());
		assertEquals("Size", annotation.getName());
		assertNotNull(annotation.getParameter());
		assertEquals("22", annotation.getParameter());		
		assertTrue(annotation.hasParameter());
		assertNotNull(annotation.getParameterAsString());

		annotation = parseAnnotationInAttribute("@Size( 22,4 )");
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
		parseAnnotationInAttribute("@Max(12RRR3)");
	}

	@Test(expected = DslModelError.class)
	public void testParseAnnotationError2() throws DslModelError {
		parseAnnotationInAttribute("@Max()");
	}

	@Test(expected = DslModelError.class)
	public void testParseAnnotationError3() throws DslModelError {
		parseAnnotationInAttribute("@Max(123");
	}

	@Test(expected = DslModelError.class)
	public void testParseAnnotationError4() throws DslModelError {
		parseAnnotationInAttribute("@Max");
	}

	@Test(expected = DslModelError.class)
	public void testParseAnnotationError5() throws DslModelError {
		parseAnnotationInAttribute("@Abcdef"); // unhknown annotation
	}

	@Test(expected = DslModelError.class)
	public void testParseAnnotationError6() throws DslModelError {
		parseAnnotationInAttribute("@Maxi(123)"); // unhknown annotation
	}

	@Test(expected = DslModelError.class)
	public void testParseAnnotationError7() throws DslModelError {
		parseAnnotationInAttribute("@Id(145)"); // Unexpected parameter
	}

	@Test(expected = DslModelError.class)
	public void testParseAnnotationError8() throws DslModelError {
		parseAnnotationInAttribute("@SizeMax(44.55)"); // integer parameter required
	}
	
	@Test(expected = DslModelError.class)
	public void testParseAnnotationError9() throws DslModelError {
		parseAnnotationInAttribute("@FooBar"); // unhknown annotation
	}
	
	@Test (expected = DslModelError.class)
	public void testParseAttributeAnnotationError1() throws DslModelError {
		parseAnnotationInAttribute("@Embedded"); 
		// annotation not usable in an attribute
	}
	
	@Test (expected = DslModelError.class)
	public void testParseLinkAnnotationError1() throws DslModelError {
		parseAnnotationInLink("@Id");
		// annotation not usable in an link
	}

	@Test(expected = DslModelError.class)
	public void testParseDbSizeError1() throws DslModelError {
		parseAnnotationInAttribute("@DbSize(10.2)"); 
	}

	@Test(expected = DslModelError.class)
	public void testParseDbSizeError2() throws DslModelError {
		parseAnnotationInAttribute("@DbSize(-22,2)"); 
	}

	@Test(expected = DslModelError.class)
	public void testParseDbSizeError3() throws DslModelError {
		parseAnnotationInAttribute("@DbSize(22,-4)"); 
	}

	@Test(expected = DslModelError.class)
	public void testParseDbSizeError4() throws DslModelError {
		parseAnnotationInAttribute("@DbSize(22,)"); 
	}

	@Test(expected = DslModelError.class)
	public void testParseDbSizeError5() throws DslModelError {
		parseAnnotationInAttribute("@DbSize(,4)"); 
	}

	@Test(expected = DslModelError.class)
	public void testParseDbSizeError6() throws DslModelError {
		parseAnnotationInAttribute("@DbSize(,)"); 
	}

	@Test(expected = DslModelError.class)
	public void testParseDbSizeError7() throws DslModelError {
		parseAnnotationInAttribute("@DbSize()"); 
	}

	@Test(expected = DslModelError.class)
	public void testParseDbSizeError8() throws DslModelError {
		parseAnnotationInAttribute("@DbSize(aa,2)"); 
	}
}
