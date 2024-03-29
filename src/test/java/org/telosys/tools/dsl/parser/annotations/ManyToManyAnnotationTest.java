package org.telosys.tools.dsl.parser.annotations;

import org.junit.Test;
import org.telosys.tools.dsl.DslModelError;
import org.telosys.tools.dsl.parser.annotation.AnnotationDefinition;
import org.telosys.tools.dsl.parser.annotation.AnnotationParamType;
import org.telosys.tools.dsl.parser.annotations.tools.AnnotationTool;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ManyToManyAnnotationTest {

	private static final String ANNOTATION_NAME = "ManyToMany";
	
	private DomainAnnotation buildAnnotation() throws DslModelError {
		return AnnotationTool.parseAnnotationInLink("@" + ANNOTATION_NAME );
	}
	private DomainAnnotation buildAnnotationWithParam(String annotationParam) throws DslModelError {
		return AnnotationTool.parseAnnotationInLink("@" + ANNOTATION_NAME + "(" + annotationParam + ")");
	}
	
	@Test
	public void test1() {
		AnnotationDefinition a = new ManyToManyAnnotation();
		assertEquals( ANNOTATION_NAME, a.getName() );
		assertEquals( AnnotationParamType.NONE, a.getParamType() );
		// scopes 
		assertFalse( a.hasAttributeScope() );
		assertTrue( a.hasLinkScope() );
		assertFalse( a.hasEntityScope() );
	}

	@Test 
	public void test2() throws DslModelError {
		DomainAnnotation da = buildAnnotation();
		assertEquals( ANNOTATION_NAME, da.getName() );
		assertNull(da.getParameter());
	}

	@Test (expected=DslModelError.class)
	public void test3() throws DslModelError {
		buildAnnotationWithParam("paramValue");
		// Error : unexpected parameter
	}

}
