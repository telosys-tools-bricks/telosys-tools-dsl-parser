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
import static org.junit.Assert.assertNull;

public class IdAnnotationTest {

	private static final String ID = "Id";
	
	private AnnotationDefinition getAnnotationDefinition() {
		return new IdAnnotation() ;
	}
	
	@Test
	public void test1() {
		AnnotationDefinition a = getAnnotationDefinition();
		assertEquals( ID, a.getName() );
		assertEquals( AnnotationParamType.NONE, a.getParamType() );
		// 2 scopes : attribute + link 
		assertTrue( a.hasAttributeScope() );
		assertFalse( a.hasLinkScope() );
		assertFalse( a.hasEntityScope() );
	}

	@Test 
	public void test2() throws ParsingError {
		AnnotationDefinition a = getAnnotationDefinition();
		DomainAnnotation da = a.buildAnnotation("Student", "firstName", null);
		assertEquals( ID, da.getName() );
		assertNull(da.getParameter());
	}

	@Test (expected=AnnotationParsingError.class)
	public void test3() throws ParsingError {
		AnnotationDefinition a = getAnnotationDefinition();
		a.buildAnnotation("Student", "firstName", "paramValue");
		// Error : unexpected parameter
	}
}