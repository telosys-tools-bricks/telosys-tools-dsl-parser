package org.telosys.tools.dsl.parsing.entitylevel;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.telosys.tools.dsl.parser.Parser;
import org.telosys.tools.dsl.parser.exceptions.EntityParsingError;
import org.telosys.tools.dsl.parser.model.DomainEntity;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class InvalidEntitiesParsingTest {
	
	private File getEntityFile(String entityName) {
		return new File("src/test/resources/entity_test/invalid/" + entityName + ".entity");
	}

	private void parseWithErrorExpected(String entityName, List<String> entitiesNames) {
		EntityParsingError error = null;
		Parser parser = new Parser();
		try {
			parser.parseEntity(getEntityFile(entityName), entitiesNames );
		} catch (EntityParsingError e) {
			error = e ;
			System.out.println("ERROR : " + error.getMessage());
		}
		assertNotNull(error);
	}
	
	
	@Test
	public void testParseEmployee0FileNotFound() throws EntityParsingError {		
//		List<String> entitiesNames = new LinkedList<>();
//		Parser parser = new Parser();
//		// supposed to throw EntityParsingError ( File not found )
//		parser.parseEntity(getEntityFile("Employee0"), entitiesNames );
		parseWithErrorExpected("Employee0", new LinkedList<String>());
	}
	
	@Test
	public void testParseEmployee1() throws EntityParsingError {
		parseWithErrorExpected("Employee1", new LinkedList<String>());
	}

	@Test
	public void testParseEmployee2() {
		parseWithErrorExpected("Employee2", new LinkedList<String>());
	}

	@Test
	public void testParseEmployee3() {
		parseWithErrorExpected("Employee3", new LinkedList<String>());
	}

	@Test
	public void testParseEmployee4() {
		parseWithErrorExpected("Employee4", new LinkedList<String>());
	}

	@Test
	public void testParseEmployee5() {
		parseWithErrorExpected("Employee5", new LinkedList<String>());
	}

	@Test
	public void testParseEmployee8() throws EntityParsingError {
		
		List<String> entitiesNames = new LinkedList<>();
		entitiesNames.add("Car");
		Parser parser = new Parser();
		// fields errors
		DomainEntity entity = parser.parseEntity(getEntityFile("Employee8"), entitiesNames );
		
		EntityReport.print(entity);
		
		assertTrue(entity.hasError());
	}

}
