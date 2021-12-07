package org.telosys.tools.dsl.parser.annotations;

import org.junit.Test;
import org.telosys.tools.dsl.parser.annotation.AnnotationDefinition;
import org.telosys.tools.dsl.parser.annotation.AnnotationParamType;
import org.telosys.tools.dsl.parser.exceptions.AnnotationParsingError;
import org.telosys.tools.dsl.parser.exceptions.ParsingError;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MaxLenAnnotationTest {

	private static final String MAX_LEN = "MaxLen";
	
	private AnnotationDefinition getAnnotationDefinition() {
		return new MaxLenAnnotation() ;
	}
	
	@Test
	public void test1() {
		AnnotationDefinition a = getAnnotationDefinition();
		assertEquals( MAX_LEN, a.getName() );
		assertEquals( AnnotationParamType.INTEGER, a.getParamType() );
		// 2 scopes : attribute + link 
		assertTrue( a.hasAttributeScope() );
		assertFalse( a.hasLinkScope() );
		assertFalse( a.hasEntityScope() );
	}

	@Test 
	public void test2() throws ParsingError {
		AnnotationDefinition a = getAnnotationDefinition();
		DomainAnnotation da = a.buildAnnotation("Student", "firstName", "26");
		assertEquals( MAX_LEN, da.getName() );
		assertEquals( 26, da.getParameter()); // Integer
	}

	@Test (expected=AnnotationParsingError.class)
	public void test3() throws ParsingError {
		AnnotationDefinition a = getAnnotationDefinition();
		a.buildAnnotation("Student", "firstName", null);
		// Error : parameter required
	}
	@Test (expected=AnnotationParsingError.class)
	public void test4() throws ParsingError {
		AnnotationDefinition a = getAnnotationDefinition();
		a.buildAnnotation("Student", "firstName", "");
		// Error : parameter required
	}
	@Test (expected=AnnotationParsingError.class)
	public void test5() throws ParsingError {
		AnnotationDefinition a = getAnnotationDefinition();
		a.buildAnnotation("Student", "firstName", "  ");
		// Error : invalid integer parameter
	}
	@Test (expected=AnnotationParsingError.class)
	public void test6() throws ParsingError {
		AnnotationDefinition a = getAnnotationDefinition();
		a.buildAnnotation("Student", "firstName", "12,2");
		// Error : invalid integer parameter
	}
}
