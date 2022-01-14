package org.telosys.tools.dsl.parser.annotations;

import org.junit.Test;
import org.telosys.tools.dsl.DslModelError;
import org.telosys.tools.dsl.parser.annotation.AnnotationDefinition;
import org.telosys.tools.dsl.parser.annotation.AnnotationParamType;
import org.telosys.tools.dsl.parser.annotations.tools.AnnotationTool;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MaxLenAnnotationTest {

	private static final String ANNOTATION_NAME = "MaxLen";
	
	private DomainAnnotation buildAnnotation() throws DslModelError {
		return AnnotationTool.parseAnnotationInAttribute("@" + ANNOTATION_NAME );
	}
	private DomainAnnotation buildAnnotationWithParam(String annotationParam) throws DslModelError {
		return AnnotationTool.parseAnnotationInAttribute("@" + ANNOTATION_NAME + "(" + annotationParam + ")");
	}	

	@Test
	public void test1() {
		AnnotationDefinition a = new MaxLenAnnotation();
		assertEquals( ANNOTATION_NAME, a.getName() );
		assertEquals( AnnotationParamType.INTEGER, a.getParamType() );
		// 2 scopes : attribute + link 
		assertTrue( a.hasAttributeScope() );
		assertFalse( a.hasLinkScope() );
		assertFalse( a.hasEntityScope() );
	}

	@Test 
	public void test2() throws DslModelError {
		DomainAnnotation da = buildAnnotationWithParam("26");
		assertEquals( ANNOTATION_NAME, da.getName() );
		assertEquals( 26, da.getParameter()); // Integer
	}

	@Test (expected=DslModelError.class)
	public void test3() throws DslModelError {
		buildAnnotation();
		// Error : parameter required
	}
	@Test (expected=DslModelError.class)
	public void test4() throws DslModelError {
		buildAnnotationWithParam("");
		// Error : parameter required
	}
	@Test (expected=DslModelError.class)
	public void test5() throws DslModelError {
		buildAnnotationWithParam("  ");
		// Error : invalid integer parameter
	}
	@Test (expected=DslModelError.class)
	public void test6() throws DslModelError {
		buildAnnotationWithParam("12,2");
		// Error : invalid integer parameter
	}
}
