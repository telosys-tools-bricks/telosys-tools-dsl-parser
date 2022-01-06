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

public class MaxLenAnnotationTest {

	private static final String ANNOTATION_NAME = "MaxLen";
	
	private DomainAnnotation parseAnnotation(String annotation) throws DslModelError {
		Element element = new Element(2, annotation);
		AnnotationProcessor annotationProcessor = new AnnotationProcessor("Student", "firstName");
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
		DomainAnnotation da = buildAnnotation("26");
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
		buildAnnotation("");
		// Error : parameter required
	}
	@Test (expected=DslModelError.class)
	public void test5() throws DslModelError {
		buildAnnotation("  ");
		// Error : invalid integer parameter
	}
	@Test (expected=DslModelError.class)
	public void test6() throws DslModelError {
		buildAnnotation("12,2");
		// Error : invalid integer parameter
	}
}
