package org.telosys.tools.dsl.parser2;

import java.util.List;

import org.junit.Test;
import org.telosys.tools.dsl.parser.EntityFileParser;
import org.telosys.tools.dsl.parser.EntityFileParsingResult;
import org.telosys.tools.dsl.parser.exceptions.EntityParsingError;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class EntityElementsParserTest {
	
	private void log(String msg) {
		System.out.println(msg);
	}
	
	private List<Element> parseEntityFile(String entityFile) throws EntityParsingError {
		log("\nENTITY FILE : " + entityFile);
		EntityElementsParser parser = new EntityElementsParser(entityFile);
		List<Element> elements = parser.parseElements();
		log("ELEMENTS :");
		for ( Element e : elements) {
			log(" . " + e);
		}
		return elements;
	}

	@Test
	public void testEntityElementsParserEmployee() throws EntityParsingError {
		List<Element> elements = parseEntityFile("src/test/resources/entity_test_v_3_4/Employee.entity");
		assertEquals(83, elements.size());
	}

	@Test
	public void testEntityElementsParserBadge() throws EntityParsingError {
		List<Element> elements = parseEntityFile("src/test/resources/entity_test_v_3_4/Badge.entity");
		assertEquals(16, elements.size());
	}

	@Test
	public void testEntityFileParserV33() throws EntityParsingError {
		parseEntityFile("src/test/resources/entity_test_v_3_3/Country.entity");
		parseEntityFile("src/test/resources/entity_test_v_3_3/Employee.entity");
	}

	@Test
	public void testEntityFileParserV32() throws EntityParsingError {
		parseEntityFile("src/test/resources/entity_test_v_3_2/Employee.entity");
	}

//	@Test
//	public void testEntityFileParserWithInvalidEntity() throws EntityParsingError {
//		
//		EntityFileParser entityFileParser = new EntityFileParser(
//		"src/test/resources/model_test/invalid/FourEntities_model/Gender.entity");
//		EntityFileParsingResult result = entityFileParser.parse();
//		System.out.println("\nPARSING RESULT :");
//		System.out.println(" Entity name : " + result.getEntityNameParsed() );
//		System.out.println("\nFIELDS PARSED :");
//		for ( FieldParts field : result.getFields() ) {
//			System.out.println(" . " + field);
//		}
//	}

}
