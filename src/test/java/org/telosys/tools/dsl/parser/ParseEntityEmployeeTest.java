package org.telosys.tools.dsl.parser;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.telosys.tools.dsl.DslModelErrors;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.reporting.ResultReport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class ParseEntityEmployeeTest {
	
	@Test
	public void testParseFileWithAFileWhichDoesntExist() {
		File entityFile = new File("src/test/resources/entity_test/no-file.entity");
		DslModelErrors errors = new DslModelErrors();
		ParserV2 parser = new ParserV2();
		DomainEntity entity = parser.parseEntity(entityFile, new LinkedList<String>(), errors );
		
		assertNull(entity); // cannot parse entity => null
		ResultReport.print(errors); // [nul.entity] : File not found
		assertFalse(errors.isEmpty());
	}

	@Test
	public void testParser() {
		File entityFile = new File("src/test/resources/entity_test_v_3_2/Employee.entity");
		List<String> entitiesNames = new LinkedList<>();
		entitiesNames.add("Car");
		DslModelErrors errors = new DslModelErrors();
		ParserV2 parser = new ParserV2();
		DomainEntity entity = parser.parseEntity(entityFile, entitiesNames, errors );
		
		ResultReport.print(entity, errors);
		assertEquals("Employee", entity.getName());
		assertEquals(11, entity.getNumberOfFields());
	}
}
