package org.telosys.tools.dsl.parser;

import org.junit.Before;
import org.junit.Test;

public class EntityFileParserTest {
	
	@Before
	public void setUp() throws Exception {}

//	@Test(expected = DslParserException.class)
//	public void testParseFileWithAFileWhichDoesntExist() {
//		File file = new File("entity_test/nul.entity");
//		EntityParser parser = new EntityParser(new DomainModel("model"));
//		parser.parse(file);
//	}

	@Test
	public void testEntityFileParser() {
		
		EntityFileParser entityFileParser = new EntityFileParser("src/test/resources/entity_test_v_3_2/Employee.entity");
		//entityFileParser.parse();
		EntityFileParsingResult result = entityFileParser.parse();
		System.out.println("\nPARSING RESULT :");
		System.out.println(" Entity name : " + result.getEntityNameParsed() );
		System.out.println("\nFIELDS PARSED :");
		for ( FieldParts field : result.getFields() ) {
			System.out.println(" . " + field);
		}
	}

}
