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

public class SizeAnnotationTest {

	private static final String ANNOTATION_NAME = "Size";
	
	private DomainAnnotation buildAnnotation() throws DslModelError {
		return AnnotationTool.parseAnnotation("@" + ANNOTATION_NAME );
	}
	private DomainAnnotation buildAnnotationWithParam(String annotationParam) throws DslModelError {
		return AnnotationTool.parseAnnotation("@" + ANNOTATION_NAME + "(" + annotationParam + ")");
	}	
	
	@Test
	public void test0() {
		AnnotationDefinition a = new SizeAnnotation();
		assertEquals( ANNOTATION_NAME, a.getName() );
		assertEquals( AnnotationParamType.SIZE, a.getParamType() );
		// 2 scopes : attribute + link 
		assertTrue( a.hasAttributeScope() );
		assertFalse( a.hasLinkScope() );
		assertFalse( a.hasEntityScope() );
	}

	@Test 
	public void test1() throws DslModelError {
		DomainAnnotation da = buildAnnotationWithParam("40");
		assertEquals( ANNOTATION_NAME, da.getName() );
		assertEquals( "40", da.getParameter());
	}

	@Test 
	public void test2() throws DslModelError {
		DomainAnnotation da = buildAnnotationWithParam("12,2");
		assertEquals( ANNOTATION_NAME, da.getName() );
		assertEquals( "12,2", da.getParameter());
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
		// Error : invalid parameter (not a number)
	}
}
