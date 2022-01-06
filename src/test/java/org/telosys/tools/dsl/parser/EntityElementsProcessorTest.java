package org.telosys.tools.dsl.parser;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.telosys.tools.dsl.DslModelErrors;
import org.telosys.tools.dsl.parser.Element;
import org.telosys.tools.dsl.parser.EntityElementsProcessor;
import org.telosys.tools.dsl.parser.model.DomainEntity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class EntityElementsProcessorTest {
	
	private void log(DslModelErrors errors) {
		System.out.println(errors);
	}	
	private void log(DomainEntity e) {
		System.out.println(e);
	}

	private DslModelErrors errors;
	@Before
	public void before() {
		errors = new DslModelErrors();
	}
	
	private DomainEntity process(String... elements) { //throws ParsingError {
		List<String> entitiesNames = Arrays.asList("Country", "Employee", "Student");
		EntityElementsProcessor processor = new EntityElementsProcessor("Student", entitiesNames);

		List<Element> list = new LinkedList<>();
		for ( String s : elements ) {
			list.add(new Element(3, s));
		}
		DomainEntity entity = processor.processEntityElements(list, errors);
		log(entity);
		return entity;
	}

	@Test
	public void test1() { // throws ParsingError  {
		DomainEntity e = process(
				"Student", "{",
				   "firstName", ":", "string", ";",
				"}");
		assertTrue(errors.isEmpty());
		assertEquals(0, errors.getNumberOfErrors());
		assertEquals("Student", e.getName());
		assertEquals(1, e.getNumberOfFields());
		assertEquals("firstName", e.getField("firstName").getName());
		assertTrue( e.getField("firstName").getType().isNeutralType() ) ;
	}

	@Test
	public void test2() { // throws ParsingError  {
		DomainEntity e = process(
				"Student", "{",
				   "firstName", ":", "string", ";",
				   "level", ":", "int", "{", "@Max(5)", "}", ";",
				"}");
		assertTrue(errors.isEmpty());
		assertEquals(0, errors.getNumberOfErrors());
		assertEquals("Student", e.getName());
		assertEquals(2, e.getNumberOfFields());
		assertEquals("firstName", e.getField("firstName").getName());
		assertTrue( e.getField("firstName").getType().isNeutralType() ) ;
		assertTrue( e.getField("level").getType().isNeutralType() ) ;
	}

	@Test
	public void test3() { // throws ParsingError  {
		DomainEntity e = process(
				"#Foo(23)",
				"Student", "{",
				   "firstName", ":", "string", ";",
				   "level", ":", "int", "{", "@Max(5)", "}", ";",
				"}");
		assertTrue(errors.isEmpty());
		assertEquals(0, errors.getNumberOfErrors());
		assertEquals("Student", e.getName());
		assertEquals(2, e.getNumberOfFields());
		assertEquals("firstName", e.getField("firstName").getName());
		assertTrue( e.getField("firstName").getType().isNeutralType() ) ;
		assertTrue( e.getField("level").getType().isNeutralType() ) ;
		assertEquals(1, e.getTagNames().size() );
	}

	@Test //(expected=EntityParsingError.class)
	public void testErr3() { // throws ParsingError  {
		process(
				"@Foo",
				"Student", "{",
				   "firstName", ":", "string", ";",
				   "level", ":", "int", "{", "@Max(5)", "}", ";",
				"}");
		// '@Foo' : unknown annotation
		log(errors);
		assertFalse(errors.isEmpty());
		assertEquals(1, errors.getNumberOfErrors());
	}

	@Test //(expected=EntityParsingError.class)
	public void testErr1() { // throws ParsingError  {
		process(
				"Foo", "{",
				   "firstName", ":", "string", ";",
				"}");
		// Entity name 'Foo' different from file name 'Student' 
		log(errors);
		assertFalse(errors.isEmpty());
		assertEquals(1, errors.getNumberOfErrors());
	}

	@Test //(expected=EntityParsingError.class)
	public void testErr2() { // throws ParsingError  {
		process("azerty",
				"Foo", "{",
				   "firstName", ":", "string", ";",
				"}");
		// Entity name 'azerty' different from file name 'Student' 
		log(errors);
		assertFalse(errors.isEmpty());
		assertEquals(2, errors.getNumberOfErrors());
	}

}
