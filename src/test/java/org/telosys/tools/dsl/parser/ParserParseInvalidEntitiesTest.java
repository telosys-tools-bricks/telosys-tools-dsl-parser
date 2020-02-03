package org.telosys.tools.dsl.parser;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.telosys.tools.dsl.parser.exceptions.EntityParsingError;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.model.DomainField;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ParserParseInvalidEntitiesTest {
	
	@Test
	public void testParseEntity() throws EntityParsingError {
		
		List<String> entitiesNames = new LinkedList<>();
		entitiesNames.add("Car");
		Parser parser = new Parser();
		DomainEntity entity = parser.parseEntity(new File("src/test/resources/entity_test/invalid/Employee8.entity"), entitiesNames );
		
		assertTrue(entity.hasError());
		List<Exception> errors = entity.getErrors();
		for ( Exception e : errors ) {
			System.out.println(" . " + e.getClass().getSimpleName() + " : " + e.getMessage());
		}
		
//		assertEquals("Student", entity.getName());
//		assertEquals(7, entity.getNumberOfFields());
//		
////		//entityFileParser.parse();
////		EntityFileParsingResult result = entityFileParser.parse();
//		System.out.println("\nPARSING RESULT :");
//		System.out.println(" Entity name : " + entity.getName() );
//		System.out.println("\nFIELDS PARSED :");
//		for ( DomainField field : entity.getFields() ) {
//			System.out.println(" . " + field);
//		}
	}

}
