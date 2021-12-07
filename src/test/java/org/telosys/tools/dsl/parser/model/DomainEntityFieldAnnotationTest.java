package org.telosys.tools.dsl.parser.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

public class DomainEntityFieldAnnotationTest {

	@Test
	public void testId() {
		DomainAnnotationOrTag annotation = new DomainAnnotation("Id") ;
		assertFalse ( annotation.hasParameter() ) ;
	}
	@Test (expected = Exception.class)
	public void testIdErr() {
		DomainAnnotationOrTag annotation = new DomainAnnotation("Id") ;
		assertNull ( annotation.getParameterAsString() ) ;
	}
	@Test (expected = Exception.class)
	public void testIdErr2() {
		DomainAnnotationOrTag annotation = new DomainAnnotation("Id") ;
		assertNull ( annotation.getParameterAsInteger() ) ;
	}
	@Test (expected = Exception.class)
	public void testIdErr3() {
		DomainAnnotationOrTag annotation = new DomainAnnotation("Id") ;
		assertNull ( annotation.getParameterAsBigDecimal() ) ;
	}
	
	@Test
	public void testMax() {
		DomainAnnotationOrTag annotation = new DomainAnnotation("Max", new BigDecimal("123.45")) ;
		assertTrue ( annotation.hasParameter() ) ;
		assertEquals(new BigDecimal("123.45"), annotation.getParameterAsBigDecimal() );
	}
	
	@Test
	public void testMaxLen() {
		DomainAnnotationOrTag annotation = new DomainAnnotation("MaxLen", new Integer("12")) ;
		assertTrue ( annotation.hasParameter() ) ;
		assertEquals(new Integer("12"), annotation.getParameterAsInteger() );
	}
	
	@Test
	public void testDbColumn() {
		DomainAnnotationOrTag annotation = new DomainAnnotation("DbColumn", "FIRST_NAME") ;
		assertTrue ( annotation.hasParameter() ) ;
		assertNotNull ( annotation.getParameterAsString() ) ;
		assertEquals("FIRST_NAME", annotation.getParameterAsString() );
	}
	
	@Test
	public void testSize() {
		DomainAnnotationOrTag annotation = new DomainAnnotation("Size", "12,4") ;
		assertTrue ( annotation.hasParameter() ) ;
		assertNotNull ( annotation.getParameterAsString() ) ;
		assertEquals("12,4", annotation.getParameterAsString() );
	}

}
