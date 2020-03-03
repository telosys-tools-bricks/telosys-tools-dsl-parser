package org.telosys.tools.dsl.parsing.entitylevel;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.telosys.tools.dsl.parser.Parser;
import org.telosys.tools.dsl.parser.exceptions.EntityParsingError;
import org.telosys.tools.dsl.parser.model.DomainEntity;

import static org.junit.Assert.assertEquals;

public class StudentEntityParsingTest {
	
	@Before
	public void setUp() throws Exception {}

	@Test
	public void testParseEntity() throws EntityParsingError {
		
		List<String> entitiesNames = new LinkedList<>();
		entitiesNames.add("Car");
		Parser parser = new Parser();
		DomainEntity entity = parser.parseEntity(new File("src/test/resources/entity_test_v_3_2/Student.entity"), entitiesNames );
		EntityReport.print(entity);

		assertEquals("Student", entity.getName());
		assertEquals(7, entity.getNumberOfFields());
		
	}

}
