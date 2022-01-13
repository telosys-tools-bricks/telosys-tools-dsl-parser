package org.telosys.tools.dsl.parser;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.telosys.tools.dsl.DslModelErrors;
import org.telosys.tools.dsl.parser.ParserV2;
import org.telosys.tools.dsl.parser.commons.FkElement;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.model.DomainField;
import org.telosys.tools.dsl.parser.reporting.ResultReport;

import static org.junit.Assert.assertEquals;

public class ParserTest {
	
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
	
//	private DomainEntity parseEntityFile(String entityFile, List<String> entitiesNames)  {
//		log("\nENTITY FILE : " + entityFile);
//		Parser parser = new Parser();
//		try {
//			DomainEntity entity = parser.parseEntity(entityFile, entitiesNames);
//			log("PARSING RESULT :");
//			log(" Entity name : " + entity.getName() );
//			return entity ;
//		} catch (EntityParsingError e) {
//			ErrorsReport.print(e);
//			throw e;
//		}
//	}
	private DomainEntity parseEntityFile(String entityFile, List<String> entitiesNames, DslModelErrors errors) {
		ParserV2 parser = new ParserV2();
		DomainEntity entity = parser.parseEntity(entityFile, entitiesNames, errors);
		ResultReport.print(entity, errors);
		return entity;
	}
	
	@Test
	public void testEntityFileParserV33()  {
		List<String> entitiesNames = Arrays.asList("Country", "Employee");
//		entitiesNames.add("Country");
//		entitiesNames.add("Employee");
		DomainEntity entity ;
//		List<FieldParsingError> errors;
//		List<ParsingError> errors;
		DslModelErrors errors = new DslModelErrors();
		entity = parseEntityFile("src/test/resources/entity_test_v_3_3/Country.entity", entitiesNames, errors);
		assertEquals("Country", entity.getName());
		assertEquals(3, entity.getNumberOfFields());
		assertEquals(0, errors.getNumberOfErrors());
		assertEquals(0, errors.getErrors().size());
		
		errors = new DslModelErrors();
		entity = parseEntityFile("src/test/resources/entity_test_v_3_3/Employee.entity", entitiesNames, errors);
		assertEquals("Employee", entity.getName());
		assertEquals(0, errors.getNumberOfErrors());
		
		logFieldFK(entity.getField("badgeId"));
		logFieldFK(entity.getField("countryId1"));
		logFieldFK(entity.getField("countryId2"));
	}

	@Test
	public void testEntityFileParserV32()  {
		List<String> entitiesNames = Arrays.asList("Employee");
		DomainEntity entity ;
//		entity = parseEntityFile("src/test/resources/entity_test_v_3_2/Employee.entity", entitiesNames);
		DslModelErrors errors = new DslModelErrors();
		entity = parseEntityFile("src/test/resources/entity_test_v_3_2/Employee.entity", entitiesNames, errors);
		assertEquals("Employee", entity.getName());
	}

}
