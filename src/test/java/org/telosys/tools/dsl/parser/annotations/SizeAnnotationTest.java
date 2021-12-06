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

public class SizeAnnotationTest {

	private static final String SIZE = "Size";
	
	private AnnotationDefinition getAnnotationDefinition() {
		return new SizeAnnotation() ;
	}
	
	@Test
	public void test1() {
		AnnotationDefinition a = getAnnotationDefinition();
		assertEquals( SIZE, a.getName() );
		assertEquals( AnnotationParamType.SIZE, a.getParamType() );
		// 2 scopes : attribute + link 
		assertTrue( a.hasAttributeScope() );
		assertFalse( a.hasLinkScope() );
		assertFalse( a.hasEntityScope() );
	}

	@Test 
	public void test2() throws ParsingError {
		AnnotationDefinition a = getAnnotationDefinition();
		DomainAnnotation da = a.buildAnnotation("Student", "firstName", "12,2");
		assertEquals( SIZE, da.getName() );
		assertEquals( "12,2", da.getParameter());
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
		// Error : invalid parameter (not a number)
	}
}
