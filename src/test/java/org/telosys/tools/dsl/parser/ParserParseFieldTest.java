package org.telosys.tools.dsl.parser;

import java.util.LinkedList;

import org.junit.Test;
import org.telosys.tools.dsl.parser.exceptions.AnnotationOrTagError;
import org.telosys.tools.dsl.parser.exceptions.FieldParsingError;
import org.telosys.tools.dsl.parser.model.DomainField;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ParserParseFieldTest {

	@Test
    public void test1() throws FieldParsingError {
		
		Parser parser = new Parser();
		FieldParts fieldParts = new FieldParts(1, "firstName : string", "@FooBar  ");
		DomainField field = parser.parseField("Country", fieldParts, new LinkedList<String>());

		assertEquals("firstName", field.getName() );
		assertEquals("string", field.getTypeName() );
		assertEquals(1,field.getCardinality());

		assertEquals(0, field.getAnnotationNames().size() );

		assertEquals(0, field.getTagNames().size() );
		
		assertTrue(field.hasErrors());
		assertEquals(1, field.getErrors().size());
		for ( AnnotationOrTagError err : field.getErrors() ) {
			System.out.println(" . " + err); // field 'firstName' : '@FooBar' (unknown annotation)
		}
	}
	
}
