package org.telosys.tools.dsl.parser;

import java.util.List;

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
		
		EntityFileParser entityFileParser = new EntityFileParser("src/test/resources/entity_test/valid/Employee.entity");
		//entityFileParser.parse();
		List<Field> fields = entityFileParser.parseFile();
		System.out.println("\nFIELDS PARSED :");
		for ( Field field : fields ) {
			System.out.println(" . " + field);
		}
	}

}
