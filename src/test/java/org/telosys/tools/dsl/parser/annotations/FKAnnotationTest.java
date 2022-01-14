package org.telosys.tools.dsl.parser.annotations;

import org.junit.Test;
import org.telosys.tools.dsl.DslModelError;
import org.telosys.tools.dsl.parser.annotation.AnnotationDefinition;
import org.telosys.tools.dsl.parser.annotation.AnnotationParamType;
import org.telosys.tools.dsl.parser.annotations.tools.AnnotationTool;
import org.telosys.tools.dsl.parser.commons.FkElement;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class FKAnnotationTest {

	private static final String ANNOTATION_NAME = "FK";
	
	private DomainAnnotation buildAnnotation() throws DslModelError {
		return AnnotationTool.parseAnnotationInAttribute("@" + ANNOTATION_NAME );
	}
	private DomainAnnotation buildAnnotationWithParam(String annotationParam) throws DslModelError {
		return AnnotationTool.parseAnnotationInAttribute("@" + ANNOTATION_NAME + "(" + annotationParam + ")");
	}
	
	@Test
	public void test1() {
		AnnotationDefinition a = new FkAnnotation();
		assertEquals( ANNOTATION_NAME, a.getName() );
		assertEquals( AnnotationParamType.FK_ELEMENT, a.getParamType() );
		assertTrue( a.hasAttributeScope() );
		assertFalse( a.hasLinkScope() );
		assertFalse( a.hasEntityScope() );
	}

	@Test 
	public void test2() throws DslModelError {
		DomainAnnotation da = buildAnnotationWithParam("FK_PER_SUBGRP, SubGroup.groupCode");
		assertEquals( ANNOTATION_NAME, da.getName() );
		assertNotNull( da.getParameter());
		FkElement fke = da.getParameterAsFKElement();
		assertEquals("FK_PER_SUBGRP", fke.getFkName());
		assertEquals("SubGroup", fke.getReferencedEntityName());
		assertEquals("groupCode", fke.getReferencedFieldName());
	}

	private FkElement getFkElement(String paramValue) throws DslModelError {
		DomainAnnotation da = buildAnnotationWithParam(paramValue);
		return da.getParameterAsFKElement();
	}
	
	@Test
    public void testFkElement1WithoutFkName() throws DslModelError { 
		FkElement fk ;
		fk = getFkElement("Book");
		assertEquals("FK_MyEntity_Book", fk.getFkName() );
		assertEquals("Book", fk.getReferencedEntityName() );
		assertEquals("", fk.getReferencedFieldName() );
		
		fk = getFkElement("  Book.id  ");
		assertEquals("FK_MyEntity_Book", fk.getFkName() );
		assertEquals("Book", fk.getReferencedEntityName() );
		assertEquals("id", fk.getReferencedFieldName() );
		
		fk = getFkElement("  Book  .  id  ");
		assertEquals("FK_MyEntity_Book", fk.getFkName() );
		assertEquals("Book", fk.getReferencedEntityName() );
		assertEquals("id", fk.getReferencedFieldName() );
		
		fk = getFkElement("  ,  Book  .  id  ");
		assertEquals("FK_MyEntity_Book", fk.getFkName() );
		assertEquals("Book", fk.getReferencedEntityName() );
		assertEquals("id", fk.getReferencedFieldName() );
		
	}

	@Test
    public void testFkElement1WithFkName() throws DslModelError { 
		FkElement fk ;
		fk = getFkElement("MY_FK,Book");
		assertEquals("MY_FK", fk.getFkName() );
		assertEquals("Book", fk.getReferencedEntityName() );
		assertEquals("", fk.getReferencedFieldName() );
		
		fk = getFkElement("MY_FK,Book.id");
		assertEquals("MY_FK", fk.getFkName() );
		assertEquals("Book", fk.getReferencedEntityName() );
		assertEquals("id", fk.getReferencedFieldName() );
		
		fk = getFkElement(" MY_FK, Book  .  id  ");
		assertEquals("MY_FK", fk.getFkName() );
		assertEquals("Book", fk.getReferencedEntityName() );
		assertEquals("id", fk.getReferencedFieldName() );
		
		fk = getFkElement("  MY_FK  ,  Book  .  id  ");
		assertEquals("MY_FK", fk.getFkName() );
		assertEquals("Book", fk.getReferencedEntityName() );
		assertEquals("id", fk.getReferencedFieldName() );
		
		fk = getFkElement("  MY_FK  ,  Book   ");
		assertEquals("MY_FK", fk.getFkName() );
		assertEquals("Book", fk.getReferencedEntityName() );
		assertEquals("", fk.getReferencedFieldName() );
		
		fk = getFkElement("  MY_FK  ,  Book.   ");
		assertEquals("MY_FK", fk.getFkName() );
		assertEquals("Book", fk.getReferencedEntityName() );
		assertEquals("", fk.getReferencedFieldName() );
		
		fk = getFkElement("   MY_FK,  Book   .   ");
		assertEquals("MY_FK", fk.getFkName() );
		assertEquals("Book", fk.getReferencedEntityName() );
		assertEquals("", fk.getReferencedFieldName() );
		
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
		// Error : parameter expected
	}
}
