package org.telosys.tools.dsl.parser;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.telosys.tools.dsl.DslModelErrors;
import org.telosys.tools.dsl.parser.ParserV2;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.reporting.ResultReport;

import static org.junit.Assert.assertEquals;

public class ValidEntitiesParsingTest {
	
	private File getEntityFile(String entityName) {
		return new File("src/test/resources/entity_test/valid/" + entityName + ".entity");
	}

//	private DomainEntity parseEntity(String entityName, List<String> entitiesNames) throws EntityParsingError {
//		Parser parser = new Parser();
//		File entityFile = getEntityFile(entityName);
//		return parser.parseEntity(entityFile, entitiesNames );
//	}
	
	private DomainEntity parseEntity(String entityName) {
		File file = getEntityFile(entityName);
		List<String> entitiesNames = new LinkedList<>();
//		entitiesNames.add("Country");
//		entitiesNames.add("Employee");
//		entitiesNames.add("Gender");
//		entitiesNames.add("Person");
		
		DslModelErrors errors = new DslModelErrors();
		ParserV2 parser = new ParserV2();
		DomainEntity entity = parser.parseEntity(file, entitiesNames, errors);
		ResultReport.print(entity, errors);
		assertEquals(0, errors.getNumberOfErrors());
		return entity;
		
	}
	
	@Test
	public void testParsePerson1()  {
		DomainEntity entity = parseEntity("Person1");
//		assertFalse(entity.hasError());
		assertEquals(2, entity.getNumberOfFields());
	}

}
