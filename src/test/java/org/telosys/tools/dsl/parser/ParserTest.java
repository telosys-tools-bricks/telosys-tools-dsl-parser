package org.telosys.tools.dsl.parser;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

public class ParserTest {
	
	@Before
	public void setUp() throws Exception {}

//	@Test(expected = DslParserException.class)
//	public void testParseFileWithAFileWhichDoesntExist() {
//		File file = new File("entity_test/nul.entity");
//		EntityParser parser = new EntityParser(new DomainModel("model"));
//		parser.parse(file);
//	}

	@Test
	public void testParser() {
		
		Parser parser = new Parser();
//		parser.parse(new File("src/test/resources/entity_test_v_3_2/Employee.entity") );
		
//		//entityFileParser.parse();
//		EntityFileParsingResult result = entityFileParser.parse();
//		System.out.println("\nPARSING RESULT :");
//		System.out.println(" Entity name : " + result.getEntityNameParsed() );
//		System.out.println("\nFIELDS PARSED :");
//		for ( Field field : result.getFields() ) {
//			System.out.println(" . " + field);
//		}
	}

}
