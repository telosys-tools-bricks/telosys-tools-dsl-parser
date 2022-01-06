package org.telosys.tools.dsl.parser;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.telosys.tools.dsl.DslModelErrors;
import org.telosys.tools.dsl.parser.ParserV2;
import org.telosys.tools.dsl.parser.commons.FkElement;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.model.DomainField;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ParserV2Test {
	
//	private void log(DomainField field) {
//		System.out.println(field);
//	}
	
	private void log(String s) {
		System.out.println(s);
	}
	private void log(DomainEntity e) {
		log(e.toString());
	}
	private void log(DslModelErrors errors) {
		System.out.println(errors);
	}

	private DslModelErrors errors;
	@Before
	public void before() {
		errors = new DslModelErrors();
	}

	private DomainEntity parseEntityFile(String entityFile) { // throws ParsingError {
		log("\nENTITY FILE : " + entityFile);
		ParserV2 parser = new ParserV2();
		List<String> entitiesNames = Arrays.asList("Country", "Employee", "Student");
		//DomainEntity entity = fileParser.parse(new File(entityFile));
		DomainEntity entity = parser.parseEntity(entityFile, entitiesNames, errors);
		log(entity);
		log(errors);		
		return entity;
	}
	
	@Test
	public void testParseEntityFileEmployee() { 
		DomainEntity entity = parseEntityFile("src/test/resources/entity_test_v_3_4/Employee.entity");
		
		assertTrue(errors.isEmpty());
		assertEquals(0, errors.getNumberOfErrors());

		assertEquals("Employee", entity.getName() );
		assertEquals(11, entity.getNumberOfFields() );
		assertTrue(entity.getAnnotationNames().isEmpty() );
		assertTrue(entity.getTagNames().isEmpty() );
		
		DomainField badgeId = entity.getField("badgeId");
		assertEquals(2, badgeId.getFkElements().size());
		
		FkElement fke ;
		
		fke = badgeId.getFkElements().get(0);
		assertEquals("FK_Employee_Badge", fke.getFkName()); // Default FK Name
		assertEquals("Badge", fke.getReferencedEntityName());
		assertEquals("", fke.getReferencedFieldName());
		
		fke = badgeId.getFkElements().get(1);
		assertEquals("FK_Employee_Toto", fke.getFkName()); // Default FK Name
		assertEquals("Toto", fke.getReferencedEntityName());
		assertEquals("code", fke.getReferencedFieldName());
	}

	@Test
	public void testParseEntityFileCountry() { 
		DomainEntity entity = parseEntityFile("src/test/resources/entity_test_v_3_4/Country.entity");
		
		assertTrue(errors.isEmpty());
		assertEquals(0, errors.getNumberOfErrors());

		assertEquals("Country", entity.getName() );
		assertEquals(3, entity.getNumberOfFields() );
		assertTrue(entity.getAnnotationNames().isEmpty() );
		assertTrue(entity.getTagNames().isEmpty() );
	}

	@Test
	public void testParseEntityFileBadge() { 
		DomainEntity entity = parseEntityFile("src/test/resources/entity_test_v_3_4/Badge.entity");
		
		assertTrue(errors.isEmpty());
		assertEquals(0, errors.getNumberOfErrors());

		assertEquals("Badge", entity.getName() );
		assertEquals(2, entity.getNumberOfFields() );
		assertFalse(entity.getAnnotationNames().isEmpty() );
		assertFalse(entity.getTagNames().isEmpty() );
	}

//	@Test (expected=EntityParsingError.class)
//	public void testErr3() throws ParsingError  {
//		process(
//				"@Foo",
//				"Student", "{",
//				   "firstName", ":", "string", ";",
//				   "level", ":", "int", "{", "@Max(5)", "}", ";",
//				"}");
//		// '@Foo' : unknown annotation
//	}

}
