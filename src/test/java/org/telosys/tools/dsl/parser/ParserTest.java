package org.telosys.tools.dsl.parser;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.telosys.tools.dsl.parser.commons.FkElement;
import org.telosys.tools.dsl.parser.exceptions.EntityParsingError;
import org.telosys.tools.dsl.parser.exceptions.ParsingError;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.model.DomainField;
import org.telosys.tools.dsl.parser.reporting.ErrorsReport;

import static org.junit.Assert.assertEquals;

public class ParserTest {
	
	@Before
	public void setUp() throws Exception {
		// init here
	}

	private void log(String msg) {
		System.out.println(msg);
	}
	private void logFieldFK(DomainField field) {
		log(" Field : '" + field.getName() + "' ("+ field.getTypeName() +")");
		log("  Declared Foreign Keys : ");
//		for ( DomainFK fk : field.getFKDeclarations() ) {
//			log ( "  - '" + fk.getFkName() + "' : " + fk.getReferencedEntityName() + " / " + fk.getReferencedFieldName() );
//		}
		for ( FkElement fke : field.getFkElements() ) {
			log ( "  - '" + fke.getFkName() + "' : " + fke.getReferencedEntityName() + " / " + fke.getReferencedFieldName() );
		}
	}
	
	private DomainEntity parseEntityFile(String entityFile, List<String> entitiesNames) throws EntityParsingError {
		log("\nENTITY FILE : " + entityFile);
		Parser parser = new Parser();
		try {
			DomainEntity entity = parser.parseEntity(entityFile, entitiesNames);
			log("PARSING RESULT :");
			log(" Entity name : " + entity.getName() );
			return entity ;
		} catch (EntityParsingError e) {
			ErrorsReport.print(e);
			throw e;
		}
	}

	@Test
	public void testEntityFileParserV33() throws EntityParsingError {
		List<String> entitiesNames = new LinkedList<>();
		entitiesNames.add("Country");
		entitiesNames.add("Employee");
		DomainEntity entity ;
//		List<FieldParsingError> errors;
		List<ParsingError> errors;
		
		entity = parseEntityFile("src/test/resources/entity_test_v_3_3/Country.entity", entitiesNames);
		assertEquals("Country", entity.getName());
		assertEquals(3, entity.getNumberOfFields());
		assertEquals(0, entity.getNumberOfErrors());
		errors = entity.getErrors();
		assertEquals(0, errors.size());
		
		entity = parseEntityFile("src/test/resources/entity_test_v_3_3/Employee.entity", entitiesNames);
		assertEquals("Employee", entity.getName());
		assertEquals(0, entity.getNumberOfErrors());
		
		logFieldFK(entity.getField("badgeId"));
		logFieldFK(entity.getField("countryId1"));
		logFieldFK(entity.getField("countryId2"));
	}

	@Test
	public void testEntityFileParserV32() throws EntityParsingError {
		List<String> entitiesNames = new LinkedList<>();
		entitiesNames.add("Employee");
		DomainEntity entity ;
		entity = parseEntityFile("src/test/resources/entity_test_v_3_2/Employee.entity", entitiesNames);
		assertEquals("Employee", entity.getName());
	}

}
