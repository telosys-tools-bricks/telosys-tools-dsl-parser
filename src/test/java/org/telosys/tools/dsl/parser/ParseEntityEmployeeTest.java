package org.telosys.tools.dsl.parser;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.telosys.tools.dsl.DslModelErrors;
import org.telosys.tools.dsl.parser.ParserV2;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.reporting.EntityReport;

import static org.junit.Assert.assertEquals;

public class ParseEntityEmployeeTest {
	
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
		
		File entityFile = new File("src/test/resources/entity_test_v_3_2/Employee.entity");
		
		List<String> entitiesNames = new LinkedList<>();
		entitiesNames.add("Car");
		DslModelErrors errors = new DslModelErrors();
		ParserV2 parser = new ParserV2();
		DomainEntity entity = parser.parseEntity(entityFile, entitiesNames, errors );
		
		EntityReport.print(entity, errors);
		
		assertEquals("Employee", entity.getName());
		assertEquals(11, entity.getNumberOfFields());

	}

}
