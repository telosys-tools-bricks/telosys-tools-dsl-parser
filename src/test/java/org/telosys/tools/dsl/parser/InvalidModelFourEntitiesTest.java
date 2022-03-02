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

public class InvalidModelFourEntitiesTest {

	private DslModelErrors parseEntityFile(String entityFileName) {
		File file = new File(entityFileName);
		List<String> entitiesNames = new LinkedList<>();
		entitiesNames.add("Country");
		entitiesNames.add("Employee");
		entitiesNames.add("Gender");
		entitiesNames.add("Person");
		
		DslModelErrors errors = new DslModelErrors();
		ParserV2 parser = new ParserV2();
		DomainEntity entity = parser.parseEntity(file, entitiesNames, errors);
		ResultReport.print(entity, errors);
		return errors;
	}
	
//	private void print(EntityParsingError exception) {
//		if ( exception != null ) {
//			System.out.println("EntityParsingError : " + exception.getMessage() );
//			for ( ParsingError error : exception.getErrors() ) {
//				System.out.println(" . FieldParsingError : " + error.getMessage() + " [" + error.getEntityName() + "]");
//			}
//		}
//		else {
//			System.out.println("EntityParsingError = null (no exception) "  );
//		}
//	}

	@Test
	public void parseEntityEmployeeERR() {
		DslModelErrors errors = parseEntityFile("src/test/resources/model_test/invalid/FourEntitiesModel/Employee.entity");
		assertEquals(1, errors.getNumberOfErrors() );
	}

	@Test
	public void parseEntityCountryERR() {
		DslModelErrors errors = parseEntityFile("src/test/resources/model_test/invalid/FourEntitiesModel/Country.entity");
		assertEquals(1, errors.getNumberOfErrors() );
	}

	@Test
	public void parseEntityGenderERR() {
		DslModelErrors errors = parseEntityFile("src/test/resources/model_test/invalid/FourEntitiesModel/Gender.entity");
		assertEquals(2, errors.getNumberOfErrors() );
	}

	@Test
	public void parseEntityPersonERR() {
		DslModelErrors errors = parseEntityFile("src/test/resources/model_test/invalid/FourEntitiesModel/Person.entity");
		assertEquals(6, errors.getNumberOfErrors() );
	}

}
