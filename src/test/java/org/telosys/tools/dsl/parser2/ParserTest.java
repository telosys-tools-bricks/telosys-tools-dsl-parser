package org.telosys.tools.dsl.parser2;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.telosys.tools.dsl.parser.EntityFileParser;
import org.telosys.tools.dsl.parser.EntityFileParsingResult;
import org.telosys.tools.dsl.parser.exceptions.EntityParsingError;
import org.telosys.tools.dsl.parser.exceptions.ParsingError;
import org.telosys.tools.dsl.parser.model.DomainEntity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ParserTest {
	
//	private void log(DomainField field) {
//		System.out.println(field);
//	}
	
	private void log(String s) {
		System.out.println(s);
	}
	private void log(DomainEntity e) {
		log(e.toString());
	}
	private void log(ParsingErrors errors) {
		System.out.println(errors);
	}

	private ParsingErrors errors;
	@Before
	public void before() {
		errors = new ParsingErrors();
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
		assertTrue(entity.getAnnotationNames().isEmpty() );
		assertTrue(entity.getTagNames().isEmpty() );
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
