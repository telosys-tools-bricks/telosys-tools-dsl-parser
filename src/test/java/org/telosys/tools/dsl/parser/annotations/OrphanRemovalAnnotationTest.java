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

public class OrphanRemovalAnnotationTest {
	
	private static final String ANNOTATION_NAME = "OrphanRemoval";

	private DomainAnnotation buildAnnotation() throws DslModelError {
		return AnnotationTool.parseAnnotationInLink("@" + ANNOTATION_NAME );
	}
	private DomainAnnotation buildAnnotationWithParam(String annotationParam) throws DslModelError {
		return AnnotationTool.parseAnnotationInLink("@" + ANNOTATION_NAME + "(" + annotationParam + ")");
	}		
	
	@Test
	public void testAnnotationDefinition() {
		AnnotationDefinition a = new OrphanRemovalAnnotation();
		assertEquals( ANNOTATION_NAME, a.getName() );
		assertEquals( AnnotationParamType.NONE, a.getParamType() );
		// 2 scopes : attribute + link 
		assertFalse( a.hasAttributeScope() );
		assertTrue( a.hasLinkScope() );
		assertFalse( a.hasEntityScope() );
	}

	@Test 
	public void testAnnotationName() throws DslModelError {
		DomainAnnotation da = buildAnnotation();
		assertEquals( ANNOTATION_NAME, da.getName() );
	}

	@Test (expected=DslModelError.class)
	public void testAnnotationParamValue() throws DslModelError{
		buildAnnotationWithParam("paramValue");
		// Error : unexpected parameter
	}
}
