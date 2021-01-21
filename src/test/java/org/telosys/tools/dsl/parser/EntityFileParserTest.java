package org.telosys.tools.dsl.parser;

import org.junit.Before;
import org.junit.Test;
import org.telosys.tools.dsl.parser.exceptions.EntityParsingError;

public class EntityFileParserTest {
	
	@Before
	public void setUp() throws Exception {}

//	@Test(expected = DslParserException.class)
//	public void testParseFileWithAFileWhichDoesntExist() {
//		File file = new File("entity_test/nul.entity");
//		EntityParser parser = new EntityParser(new DomainModel("model"));
//		parser.parse(file);
//	}

	private void log(String msg) {
		System.out.println(msg);
	}
	
	private void parseEntityFile(String entityFile) throws EntityParsingError {
		log("\nENTITY FILE : " + entityFile);
		EntityFileParser entityFileParser = new EntityFileParser(entityFile);
		EntityFileParsingResult result = entityFileParser.parse();
		log("PARSING RESULT :");
		log(" Entity name : " + result.getEntityNameParsed() );
		log(" Fields :");
		for ( FieldParts field : result.getFields() ) {
			log(" . " + field);
		}
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

	@Test
	public void testEntityFileParser_invalidEntity() throws EntityParsingError {
		
		EntityFileParser entityFileParser = new EntityFileParser(
		"src/test/resources/model_test/invalid/FourEntities_model/Gender.entity");
		EntityFileParsingResult result = entityFileParser.parse();
		System.out.println("\nPARSING RESULT :");
		System.out.println(" Entity name : " + result.getEntityNameParsed() );
		System.out.println("\nFIELDS PARSED :");
		for ( FieldParts field : result.getFields() ) {
			System.out.println(" . " + field);
		}
	}

}
