package org.telosys.tools.dsl.parser;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.telosys.tools.dsl.parser.exceptions.ParsingError;
import org.telosys.tools.dsl.parser.model.DomainField;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ParserParseFieldTest {

	@Test
    public void testParseFieldOK() throws ParsingError {
		List<String> entitiesNames = new LinkedList<>();
		Parser parser = new Parser();
		FieldParts fieldParts = new FieldParts(1, "firstName : string", "@NotNull  #FooBar");
		DomainField field = parser.parseField("Country", fieldParts, entitiesNames);

		assertEquals("firstName", field.getName() );
		assertEquals("string", field.getTypeName() );
		assertEquals(1, field.getCardinality());

		assertEquals(1, field.getAnnotationNames().size() );

		assertEquals(1, field.getTagNames().size() );
		
		assertFalse(field.hasErrors());
		assertEquals(0, field.getErrors().size());
	}
	
	@Test
    public void testParseFieldOk2() throws ParsingError {
		List<String> entitiesNames = new LinkedList<>();
		Parser parser = new Parser();
		FieldParts fieldParts = new FieldParts(1, "firstName : string", "@Label(abc)  ");
		DomainField field = parser.parseField("Country", fieldParts, entitiesNames);

		assertEquals("firstName", field.getName() );
		assertEquals("string", field.getTypeName() );
		assertEquals(1,field.getCardinality());

		assertEquals(1, field.getAnnotationNames().size() );

		assertEquals(0, field.getTagNames().size() );
		
		assertFalse(field.hasErrors());
		assertEquals(0, field.getErrors().size());
//		for ( AnnotationOrTagError err : field.getErrors() ) {
		for ( ParsingError err : field.getErrors() ) {
			System.out.println(" . " + err); // field 'firstName' : '@FooBar' (unknown annotation)
		}
	}
	
	@Test
    public void testParseFieldWithError() throws ParsingError {
		List<String> entitiesNames = new LinkedList<>();
		Parser parser = new Parser();
		FieldParts fieldParts = new FieldParts(1, "firstName : string", "@FooBar  ");
		DomainField field = parser.parseField("Country", fieldParts, entitiesNames);

		assertEquals("firstName", field.getName() );
		assertEquals("string", field.getTypeName() );
		assertEquals(1,field.getCardinality());

		assertEquals(0, field.getAnnotationNames().size() );
		assertEquals(0, field.getTagNames().size() );
		
		assertTrue(field.hasErrors());
		assertEquals(1, field.getErrors().size());
		for ( ParsingError err : field.getErrors() ) {
			System.out.println(" . " + err); // field 'firstName' : '@FooBar' (unknown annotation)
		}
	}
	
}
