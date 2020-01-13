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
	public void test1() {
		DomainEntityFieldAnnotation annotation = new DomainEntityFieldAnnotation("Id") ;
		System.out.println(annotation);
		assertFalse ( annotation.hasParameter() ) ;
		assertNull ( annotation.getParameterAsString() ) ;
		assertNull ( annotation.getParameterAsInteger() ) ;
		assertNull ( annotation.getParameterAsBigDecimal() ) ;
	}
	
	@Test
	public void test2() {
		DomainEntityFieldAnnotation annotation = new DomainEntityFieldAnnotation("Max", new BigDecimal("123.45")) ;
		System.out.println(annotation);
		assertTrue ( annotation.hasParameter() ) ;
		assertNull ( annotation.getParameterAsString() ) ;
		assertNull ( annotation.getParameterAsInteger() ) ;
		assertNotNull ( annotation.getParameterAsBigDecimal() ) ;
		assertEquals(new BigDecimal("123.45"), annotation.getParameterAsBigDecimal() );
	}
	
	@Test
	public void test3() {
		DomainEntityFieldAnnotation annotation = new DomainEntityFieldAnnotation("SizeMax", new Integer("12")) ;
		System.out.println(annotation);
		assertTrue ( annotation.hasParameter() ) ;
		assertNull ( annotation.getParameterAsString() ) ;
		assertNotNull ( annotation.getParameterAsInteger() ) ;
		assertNull ( annotation.getParameterAsBigDecimal() ) ;
		assertEquals(new Integer("12"), annotation.getParameterAsInteger() );
	}
	
	@Test
	public void test4() {
		DomainEntityFieldAnnotation annotation = new DomainEntityFieldAnnotation("DbColumn", "FIRST_NAME") ;
		System.out.println(annotation);
		assertTrue ( annotation.hasParameter() ) ;
		assertNotNull ( annotation.getParameterAsString() ) ;
		assertNull ( annotation.getParameterAsInteger() ) ;
		assertNull ( annotation.getParameterAsBigDecimal() ) ;
		assertEquals("FIRST_NAME", annotation.getParameterAsString() );
	}
	
}
