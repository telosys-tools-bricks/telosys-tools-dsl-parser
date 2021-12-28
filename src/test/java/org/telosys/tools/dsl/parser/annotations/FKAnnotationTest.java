package org.telosys.tools.dsl.parser.annotations;

import org.junit.Test;
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

public class FKAnnotationTest {

	private static final String FK = "FK";
	
	private AnnotationDefinition getAnnotationDefinition() {
		return new FkAnnotation() ;
	}
	
	@Test
	public void test1() {
		AnnotationDefinition a = getAnnotationDefinition();
		assertEquals( FK, a.getName() );
		assertEquals( AnnotationParamType.FK_ELEMENT, a.getParamType() );
		assertTrue( a.hasAttributeScope() );
		assertFalse( a.hasLinkScope() );
		assertFalse( a.hasEntityScope() );
	}

	@Test 
	public void test2() throws ParsingError {
		AnnotationDefinition a = getAnnotationDefinition();
		DomainAnnotation da = a.buildAnnotation("Student", "firstName", "FK_PER_SUBGRP, SubGroup.groupCode");
		assertEquals( FK, da.getName() );
		assertNotNull( da.getParameter());
		FkElement fke = da.getParameterAsFKElement();
		assertEquals("FK_PER_SUBGRP", fke.getFkName());
		assertEquals("SubGroup", fke.getReferencedEntityName());
		assertEquals("groupCode", fke.getReferencedFieldName());
	}

	private FkElement getFkElement(String paramValue) throws ParsingError {
		AnnotationDefinition a = getAnnotationDefinition();
		DomainAnnotation da = a.buildAnnotation("MyEntity", "myField", paramValue);
		return da.getParameterAsFKElement();
	}
	
	@Test
    public void testFkElement1WithoutFkName() throws ParsingError { 
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
    public void testFkElement1WithFkName() throws ParsingError { 
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
	
	@Test (expected=AnnotationParsingError.class)
	public void test3() throws ParsingError {
		AnnotationDefinition a = getAnnotationDefinition();
		a.buildAnnotation("Student", "firstName", null);
		// Error : parameter required
	}
	@Test (expected=AnnotationParsingError.class)
	public void test4() throws ParsingError {
		AnnotationDefinition a = getAnnotationDefinition();
		a.buildAnnotation("Student", "firstName", "");
		// Error : parameter required
	}
	@Test (expected=AnnotationParsingError.class)
	public void test5() throws ParsingError {
		AnnotationDefinition a = getAnnotationDefinition();
		a.buildAnnotation("Student", "firstName", "  ");
		// Error : parameter expected
	}
}
