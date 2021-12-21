package org.telosys.tools.dsl.parser;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.telosys.tools.dsl.parser.Parser;
import org.telosys.tools.dsl.parser.exceptions.EntityParsingError;
import org.telosys.tools.dsl.parser.exceptions.ModelParsingError;
import org.telosys.tools.dsl.parser.exceptions.ParsingError;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.model.DomainModel;
import org.telosys.tools.dsl.parser.reporting.EntityReport;

import static org.junit.Assert.assertEquals;

public class ParseEntityStudentTest {
	
	@Before
	public void setUp() throws Exception {}
	
	private DomainEntity parseEntity(String filePath, List<String> entitiesNames) {
		File file = new File(filePath);
		Parser parser = new Parser();
		try {
			return parser.parseEntity(file, entitiesNames);
		} catch (EntityParsingError err) {
			System.out.println( err.getReportMessage() );
			for ( ParsingError pe : err.getErrors() ) {
				System.out.println( pe.getReportMessage());
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	@Test
	public void testParseEntity() {
		
		List<String> entitiesNames = new LinkedList<>();
		entitiesNames.add("Car");
		DomainEntity entity = parseEntity("src/test/resources/entity_test_v_3_2/Student.entity", entitiesNames);
		EntityReport.print(entity);

		assertEquals("Student", entity.getName());
		assertEquals(7, entity.getNumberOfFields());
	}

}
