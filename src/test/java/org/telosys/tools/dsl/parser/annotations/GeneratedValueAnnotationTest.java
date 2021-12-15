package org.telosys.tools.dsl.parser.annotations;

import java.util.List;

import org.junit.Test;
import org.telosys.tools.dsl.AnnotationName;
import org.telosys.tools.dsl.parser.annotation.AnnotationDefinition;
import org.telosys.tools.dsl.parser.annotation.AnnotationParamType;
import org.telosys.tools.dsl.parser.commons.FkElement;
import org.telosys.tools.dsl.parser.exceptions.AnnotationParsingError;
import org.telosys.tools.dsl.parser.exceptions.ParsingError;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class GeneratedValueAnnotationTest {

	private static final String ANNOTATION_NAME = AnnotationName.GENERATED_VALUE ;
	
	private AnnotationDefinition getAnnotationDefinition() {
		return new GeneratedValueAnnotation() ;
	}
	
	@Test
	public void testTypes() {
		AnnotationDefinition a = getAnnotationDefinition();
		assertEquals( ANNOTATION_NAME, a.getName() );
		assertEquals( AnnotationParamType.LIST, a.getParamType() );
		assertTrue( a.hasAttributeScope() );
		assertFalse( a.hasLinkScope() );
		assertFalse( a.hasEntityScope() );
	}

	@Test (expected=AnnotationParsingError.class)
	public void testNoParam() throws ParsingError {
		AnnotationDefinition a = getAnnotationDefinition();
		a.buildAnnotation("Student", "firstName", null);
		// Error : parameter required
	}

	@Test 
	public void testAUTO() throws ParsingError {
		AnnotationDefinition a = getAnnotationDefinition();
		DomainAnnotation da = a.buildAnnotation("Student", "id", "  AUTO  ");
		assertEquals( ANNOTATION_NAME, da.getName() );
		assertNotNull( da.getParameter());
		List<?> list = da.getParameterAsList();
		assertEquals(1, list.size());
		assertEquals("AUTO", list.get(0));
	}

	@Test 
	public void testSEQUENCE() throws ParsingError {
		AnnotationDefinition a = getAnnotationDefinition();
		DomainAnnotation da = a.buildAnnotation("Student", "id", 
				" SEQUENCE  , seq1Generator  , MYSEQ1 , 1   ");
		assertEquals( ANNOTATION_NAME, da.getName() );
		assertNotNull( da.getParameter());
		List<?> list = da.getParameterAsList();
		assertEquals(4, list.size());
		assertEquals("SEQUENCE", list.get(0));
		assertEquals("seq1Generator", list.get(1));
		assertEquals("MYSEQ1", list.get(2));
		assertEquals("1", list.get(3));
	}

	@Test (expected=Exception.class)
	public void testInvalidSEQUENCE() throws ParsingError {
		AnnotationDefinition a = getAnnotationDefinition();
		a.buildAnnotation("Student", "id", 
				" SEQUENCE  , GeneratorName   "); // invalid number of parameters 
	}

	@Test (expected=Exception.class)
	public void testInvalidSEQUENCE2() throws ParsingError {
		AnnotationDefinition a = getAnnotationDefinition();
		a.buildAnnotation("Student", "id", 
				" SEQUENCE  , GeneratorName, MYSEQ1 , 1 , foo   "); // invalid number of parameters 
	}

	@Test (expected=Exception.class)
	public void testInvalidTABLE() throws ParsingError {
		AnnotationDefinition a = getAnnotationDefinition();
		a.buildAnnotation("Student", "id", 
				" TABLE  , GeneratorName   "); // invalid number of parameters 
	}

	@Test (expected=Exception.class)
	public void testInvalidTABLE2() throws ParsingError {
		AnnotationDefinition a = getAnnotationDefinition();
		a.buildAnnotation("Student", "id", 
				" TABLE  , GeneratorName, tableName, foo  "); // invalid number of parameters 
	}

}
