package org.telosys.tools.dsl.parser;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.telosys.tools.dsl.DslModelErrors;
import org.telosys.tools.dsl.parser.ParserV2;
import org.telosys.tools.dsl.parser.model.DomainEntity;

import static org.junit.Assert.assertTrue;

public class ParseEntityInvalidTest {

	private File getEntityFile(String entityName) {
		return new File("src/test/resources/entity_test/invalid/" + entityName + ".entity");
	}

	private void parseWithErrorExpected(String entityName) {
		List<String> entitiesNames = new LinkedList<>();
		DslModelErrors errors = new DslModelErrors();
		ParserV2 parser = new ParserV2();
		DomainEntity entity = parser.parseEntity(getEntityFile(entityName), entitiesNames, errors );
		assertTrue( errors.getNumberOfErrors() > 0 ) ;
		
//		try {
//			parser.parseEntity(getEntityFile(entityName), new LinkedList<String>());
//			fail(); // Exception expected 
//		} catch (EntityParsingError e) {
//			error = e;
////			System.out.println("ERROR : " + error.getMessage());
//			ErrorsReport.print(e);
//		}
//		assertNotNull(error);
	}

	@Test
	public void testParseEmployee0FileNotFound() {
		parseWithErrorExpected("Employee0");
	}

	@Test
	public void testParseEmployee1() {
		parseWithErrorExpected("Employee1");
	}

	@Test
	public void testParseEmployee2() {
		parseWithErrorExpected("Employee2");
	}

	@Test
	public void testParseEmployee3() {
		parseWithErrorExpected("Employee3");
	}

	@Test
	public void testParseEmployee3Bis() {
		parseWithErrorExpected("Employee3Bis");
	}

	@Test
	public void testParseEmployee4() {
		parseWithErrorExpected("Employee4");
	}

	@Test
	public void testParseEmployee5() {
		parseWithErrorExpected("Employee5");
	}

	@Test
	public void testParseEmployee6() {
		parseWithErrorExpected("Employee6");
	}

	@Test
	public void testParseEmployee7() {
		parseWithErrorExpected("Employee7");
	}

	@Test
	public void testParseEmployee8() {
		parseWithErrorExpected("Employee8");		
	}

	@Test
	public void testParseEmployee9() {
		parseWithErrorExpected("Employee9");
	}

	@Test
	public void testParseCountry1()  {
		parseWithErrorExpected("Country1");
	}

}
