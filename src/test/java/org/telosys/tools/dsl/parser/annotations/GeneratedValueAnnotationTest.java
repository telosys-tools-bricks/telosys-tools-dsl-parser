package org.telosys.tools.dsl.parser.annotations;

import java.util.List;

import org.junit.Test;
import org.telosys.tools.dsl.DslModelError;
import org.telosys.tools.dsl.parser.AnnotationProcessor;
import org.telosys.tools.dsl.parser.Element;
import org.telosys.tools.dsl.parser.annotation.AnnotationDefinition;
import org.telosys.tools.dsl.parser.annotation.AnnotationName;
import org.telosys.tools.dsl.parser.annotation.AnnotationParamType;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class GeneratedValueAnnotationTest {

	private static final String ANNOTATION_NAME = AnnotationName.GENERATED_VALUE ;
	
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
	public void testTypes() {
		AnnotationDefinition a = new GeneratedValueAnnotation();
		assertEquals( ANNOTATION_NAME, a.getName() );
		assertEquals( AnnotationParamType.LIST, a.getParamType() );
		assertTrue( a.hasAttributeScope() );
		assertFalse( a.hasLinkScope() );
		assertFalse( a.hasEntityScope() );
	}

	@Test (expected=DslModelError.class)
	public void testNoParam() throws DslModelError {
		buildAnnotation();
		// Error : parameter required
	}

	@Test 
	public void testAUTO() throws DslModelError {
		DomainAnnotation da = buildAnnotation("  AUTO  ");
		assertEquals( ANNOTATION_NAME, da.getName() );
		assertNotNull( da.getParameter());
		List<?> list = da.getParameterAsList();
		assertEquals(1, list.size());
		assertEquals("AUTO", list.get(0));
	}

	@Test 
	public void testSEQUENCE() throws DslModelError {
		DomainAnnotation da = buildAnnotation(" SEQUENCE  , seq1Generator  , MYSEQ1 , 1  ");
		assertEquals( ANNOTATION_NAME, da.getName() );
		assertNotNull( da.getParameter());
		List<?> list = da.getParameterAsList();
		assertEquals(4, list.size());
		assertEquals("SEQUENCE", list.get(0));
		assertEquals("seq1Generator", list.get(1));
		assertEquals("MYSEQ1", list.get(2));
		assertEquals("1", list.get(3));
	}

	@Test (expected=DslModelError.class)
	public void testInvalidSEQUENCE() throws DslModelError {
		buildAnnotation(" SEQUENCE  , GeneratorName   "); 
		// invalid number of parameters 
	}

	@Test (expected=DslModelError.class)
	public void testInvalidSEQUENCE2() throws DslModelError {
		buildAnnotation(" SEQUENCE  , GeneratorName, MYSEQ1 , 1 , foo   "); 
		// invalid number of parameters 
	}

	@Test (expected=DslModelError.class)
	public void testInvalidTABLE() throws DslModelError {
		buildAnnotation(" TABLE  , GeneratorName   "); 
		// invalid number of parameters 
	}

	@Test (expected=DslModelError.class)
	public void testInvalidTABLE2() throws DslModelError {
		buildAnnotation(" TABLE  , GeneratorName, tableName, foo  "); 
		// invalid number of parameters 
	}

}
