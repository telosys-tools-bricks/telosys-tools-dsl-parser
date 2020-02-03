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

public class ParserParseEntityEmployeeTest {
	
	@Before
	public void setUp() throws Exception {}

//	@Test(expected = DslParserException.class)
//	public void testParseFileWithAFileWhichDoesntExist() {
//		File file = new File("entity_test/nul.entity");
//		EntityParser parser = new EntityParser(new DomainModel("model"));
//		parser.parse(file);
//	}

	@Test
	public void testParser() throws EntityParsingError {
		
		List<String> entitiesNames = new LinkedList<>();
		entitiesNames.add("Car");
		Parser parser = new Parser();
		DomainEntity entity = parser.parseEntity(new File("src/test/resources/entity_test_v_3_2/Employee.entity"), entitiesNames );
		
		System.out.println("\nPARSING RESULT :");
		System.out.println(" Entity name : " + entity.getName() );
		System.out.println("\nFIELDS PARSED :");
		for ( DomainField field : entity.getFields() ) {
			System.out.println(" . " + field);
		}
		
		assertEquals("Employee", entity.getName());
		assertEquals(14, entity.getNumberOfFields());

	}

}
