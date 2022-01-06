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

public class SizeAnnotationTest {

	private static final String ANNOTATION_NAME = "Size";
	
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
		DomainAnnotation da = buildAnnotation("40");
		assertEquals( ANNOTATION_NAME, da.getName() );
		assertEquals( "40", da.getParameter());
	}

	@Test 
	public void test2() throws DslModelError {
		DomainAnnotation da = buildAnnotation("12,2");
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
		buildAnnotation("");
		// Error : parameter required
	}
	@Test (expected=DslModelError.class)
	public void test5() throws DslModelError {
		buildAnnotation("  ");
		// Error : invalid parameter (not a number)
	}
}
