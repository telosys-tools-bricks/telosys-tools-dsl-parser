package org.telosys.tools.dsl.parser;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.telosys.tools.dsl.parser.Parser;
import org.telosys.tools.dsl.parser.exceptions.EntityParsingError;
import org.telosys.tools.dsl.parser.reporting.ErrorsReport;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class ParseEntityInvalidTest {

	private File getEntityFile(String entityName) {
		return new File("src/test/resources/entity_test/invalid/" + entityName + ".entity");
	}

	private void parseWithErrorExpected(String entityName, List<String> entitiesNames) {
		EntityParsingError error = null;
		Parser parser = new Parser();
		try {
			parser.parseEntity(getEntityFile(entityName), entitiesNames);
			fail(); // Exception expected 
		} catch (EntityParsingError e) {
			error = e;
//			System.out.println("ERROR : " + error.getMessage());
			ErrorsReport.print(e);
		}
		assertNotNull(error);
	}

	@Test
	public void testParseEmployee0FileNotFound() throws EntityParsingError {
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
	public void testParseEmployee3Bis() {
		parseWithErrorExpected("Employee3Bis", new LinkedList<String>());
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
	public void testParseEmployee6() {
		parseWithErrorExpected("Employee6", new LinkedList<String>());
	}

	@Test
	public void testParseEmployee7() {
		parseWithErrorExpected("Employee7", new LinkedList<String>());
	}

	@Test
	public void testParseEmployee8() {
		parseWithErrorExpected("Employee8", new LinkedList<String>());		
	}

	@Test
	public void testParseEmployee9() {
		parseWithErrorExpected("Employee9", new LinkedList<String>());
	}

	@Test
	public void testParseCountry1()  {
		parseWithErrorExpected("Country1", new LinkedList<String>());
	}

}
