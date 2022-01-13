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

public class ParseEntityStudentTest {
	
//	@Before
//	public void setUp() throws Exception {}
//	
//	private DomainEntity parseEntity(String filePath, List<String> entitiesNames) {
//		File file = new File(filePath);
//		Parser parser = new Parser();
//		try {
//			return parser.parseEntity(file, entitiesNames);
//		} catch (EntityParsingError err) {
//			System.out.println( err.getReportMessage() );
//			for ( ParsingError pe : err.getErrors() ) {
//				System.out.println( pe.getReportMessage());
//			}
//			return null;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
	@Test
	public void testParseEntity() {
		
//		List<String> entitiesNames = new LinkedList<>();
//		entitiesNames.add("Car");
//		DomainEntity entity = parseEntity("src/test/resources/entity_test_v_3_2/Student.entity", entitiesNames);
//		EntityReport.print(entity);

		File entityFile = new File("src/test/resources/entity_test_v_3_2/Student.entity");
		List<String> entitiesNames = new LinkedList<>();
		entitiesNames.add("Car");
		DslModelErrors errors = new DslModelErrors();
		ParserV2 parser = new ParserV2();
		DomainEntity entity = parser.parseEntity(entityFile, entitiesNames, errors );
		
		ResultReport.print(entity, errors);

		assertEquals("Student", entity.getName());
		assertEquals(7, entity.getNumberOfFields());
	}

}
