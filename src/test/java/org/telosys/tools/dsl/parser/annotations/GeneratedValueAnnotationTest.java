package org.telosys.tools.dsl.parser.annotations;

import java.util.List;

import org.junit.Test;
import org.telosys.tools.dsl.DslModelError;
import org.telosys.tools.dsl.parser.annotation.AnnotationDefinition;
import org.telosys.tools.dsl.parser.annotation.AnnotationName;
import org.telosys.tools.dsl.parser.annotation.AnnotationParamType;
import org.telosys.tools.dsl.parser.annotations.tools.AnnotationTool;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class GeneratedValueAnnotationTest {

	private static final String ANNOTATION_NAME = AnnotationName.GENERATED_VALUE ;
	
	private DomainAnnotation buildAnnotation() throws DslModelError {
		return AnnotationTool.parseAnnotationInAttribute("@" + ANNOTATION_NAME );
	}
	private DomainAnnotation buildAnnotationWithParam(String annotationParam) throws DslModelError {
		return AnnotationTool.parseAnnotationInAttribute("@" + ANNOTATION_NAME + "(" + annotationParam + ")");
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

	//------------------------------------------------------------
	// Strategy = AUTO
	//------------------------------------------------------------
	@Test 
	public void testAUTO() throws DslModelError {
		DomainAnnotation da = buildAnnotationWithParam("  AUTO  ");
		assertEquals( ANNOTATION_NAME, da.getName() );
		assertNotNull( da.getParameter());
		List<?> list = da.getParameterAsList();
		assertEquals(1, list.size());
		assertEquals("AUTO", list.get(0));
	}

	@Test (expected=DslModelError.class)
	public void testInvalidAUTO() throws DslModelError {
		// invalid number of parameters 
		buildAnnotationWithParam("  AUTO, foo  ");
	}

	//------------------------------------------------------------
	// Strategy = IDENTITY
	//------------------------------------------------------------
	@Test 
	public void testIDENTITY() throws DslModelError {
		DomainAnnotation da = buildAnnotationWithParam("  IDENTITY  ");
		assertEquals( ANNOTATION_NAME, da.getName() );
		assertNotNull( da.getParameter());
		List<?> list = da.getParameterAsList();
		assertEquals(1, list.size());
		assertEquals("IDENTITY", list.get(0));
	}

	@Test (expected=DslModelError.class)
	public void testInvalidIDENTITY() throws DslModelError {
		// invalid number of parameters 
		buildAnnotationWithParam("  IDENTITY, foo, bar ");
	}

	//------------------------------------------------------------
	// Strategy = SEQUENCE
	//------------------------------------------------------------
	@Test 
	public void testSEQUENCE() throws DslModelError {
		DomainAnnotation annotation ;
		annotation = buildAnnotationWithParam(" SEQUENCE  , sequenceName  ");
		assertEquals(2, annotation.getParameterAsList().size());
		annotation = buildAnnotationWithParam(" SEQUENCE  , sequenceName  , 10  ");
		assertEquals(3, annotation.getParameterAsList().size());
		annotation = buildAnnotationWithParam(" SEQUENCE  , sequenceName  , 10 , 1  ");
		assertEquals( ANNOTATION_NAME, annotation.getName() );
		assertNotNull( annotation.getParameter());
		List<?> list = annotation.getParameterAsList();
		assertEquals(4, list.size());
		assertEquals("SEQUENCE", list.get(0));
		assertEquals("sequenceName", list.get(1));
		assertEquals("10", list.get(2));
		assertEquals("1", list.get(3));
	}

	@Test (expected=DslModelError.class)
	public void testInvalidSEQUENCE() throws DslModelError {
		// invalid number of parameters 
//		buildAnnotationWithParam(" SEQUENCE  , GeneratorName   "); 
		buildAnnotationWithParam(" SEQUENCE "); 
	}

	@Test (expected=DslModelError.class)
	public void testInvalidSEQUENCE2() throws DslModelError {
		// invalid number of parameters 
		buildAnnotationWithParam(" SEQUENCE , MYSEQUENCE , 10 , 1 , foo "); 
	}

	//------------------------------------------------------------
	// Strategy = TABLE
	//------------------------------------------------------------
	@Test 
	public void testTABLE() throws DslModelError {
		DomainAnnotation annotation ;
		annotation = buildAnnotationWithParam(" TABLE  , pkValue  ");
		assertEquals(2, annotation.getParameterAsList().size());
		annotation = buildAnnotationWithParam(" TABLE  , pkValue  , 10  ");
		assertEquals(3, annotation.getParameterAsList().size());
		annotation = buildAnnotationWithParam(" TABLE  , pkValue  , 10 , 1  ");
		assertEquals(4, annotation.getParameterAsList().size());
		assertEquals( ANNOTATION_NAME, annotation.getName() );
		assertNotNull( annotation.getParameter());
		List<?> list = annotation.getParameterAsList();
		assertEquals("TABLE", list.get(0));
		assertEquals("pkValue", list.get(1));
		assertEquals("10", list.get(2));
		assertEquals("1", list.get(3));
	}

	@Test (expected=DslModelError.class)
	public void testInvalidTABLE() throws DslModelError {
		// invalid number of parameters 
		buildAnnotationWithParam(" TABLE   "); 
	}

	@Test (expected=DslModelError.class)
	public void testInvalidTABLE2() throws DslModelError {
		// invalid number of parameters 
		buildAnnotationWithParam(" TABLE  , GeneratorName, 10, 1, foo  "); 
	}

}
