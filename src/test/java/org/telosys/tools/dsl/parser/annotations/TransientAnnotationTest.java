package org.telosys.tools.dsl.parser.annotations;

import org.junit.Test;
import org.telosys.tools.dsl.DslModelError;
import org.telosys.tools.dsl.parser.AnnotationProcessor;
import org.telosys.tools.dsl.parser.Element;
import org.telosys.tools.dsl.parser.annotation.AnnotationDefinition;
import org.telosys.tools.dsl.parser.annotation.AnnotationParamType;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TransientAnnotationTest {
	
	private static final String ANNOTATION_NAME = "Transient";

	private DomainAnnotation parseAnnotation(String annotation) throws DslModelError {
		Element element = new Element(2, annotation);
		AnnotationProcessor annotationProcessor = new AnnotationProcessor("MyEntity", "myField");
		return annotationProcessor.parseAnnotation(element);
	}
	private DomainAnnotation buildAnnotation() throws DslModelError {
		return parseAnnotation("@" + ANNOTATION_NAME );
	}
	private DomainAnnotation buildAnnotation(String annotationParam) throws DslModelError {
		return parseAnnotation("@" + ANNOTATION_NAME + "(" + annotationParam + ")");
	}
	
	
	@Test
	public void test1() {
		AnnotationDefinition a = new TransientAnnotation();
		assertEquals( ANNOTATION_NAME, a.getName() );
		assertEquals( AnnotationParamType.NONE, a.getParamType() );
		// 2 scopes : attribute + link 
		assertTrue( a.hasAttributeScope() );
		assertTrue( a.hasLinkScope() );
		assertFalse( a.hasEntityScope() );
	}

	@Test 
	public void test2() throws DslModelError {
		DomainAnnotation da = buildAnnotation();
		assertEquals( ANNOTATION_NAME, da.getName() );
	}

	@Test (expected=DslModelError.class)
	public void test3() throws DslModelError{
		buildAnnotation("paramValue");
		// Error : unexpected parameter
	}
}
