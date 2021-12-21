package org.telosys.tools.dsl.parser;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.telosys.tools.dsl.parser.Parser;
import org.telosys.tools.dsl.parser.exceptions.EntityParsingError;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.reporting.EntityReport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ValidEntitiesParsingTest {
	
	private File getEntityFile(String entityName) {
		return new File("src/test/resources/entity_test/valid/" + entityName + ".entity");
	}

	private DomainEntity parseEntity(String entityName, List<String> entitiesNames) throws EntityParsingError {
		Parser parser = new Parser();
		File entityFile = getEntityFile(entityName);
		return parser.parseEntity(entityFile, entitiesNames );
	}
	
	
	@Test
	public void testParsePerson1() throws EntityParsingError {
		DomainEntity entity = parseEntity("Person1", new LinkedList<String>());
		EntityReport.print(entity);
		assertFalse(entity.hasError());
		assertEquals(2, entity.getNumberOfFields());
	}

}
