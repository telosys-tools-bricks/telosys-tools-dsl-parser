package org.telosys.tools.dsl.parser;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.telosys.tools.dsl.DslModelError;
import org.telosys.tools.dsl.parser.Element;
import org.telosys.tools.dsl.parser.EntityElementsParser;

import static org.junit.Assert.assertEquals;

public class EntityElementsParserTest {
	
	private void log(String msg) {
		System.out.println(msg);
	}
	
	private List<Element> parseEntityFile(String entityFile) throws DslModelError {
		log("\nENTITY FILE : " + entityFile);
		EntityElementsParser parser = new EntityElementsParser();
		List<Element> elements = parser.parseEntityFile(new File(entityFile));
		log("ELEMENTS : " );
		for ( Element e : elements) {
			log(" . " + e);
		}
		log(" " + elements.size() + " element(s)");
		return elements;
	}

	@Test
	public void testEntityElementsParserEmployee() throws DslModelError {
		List<Element> elements = parseEntityFile("src/test/resources/entity_test_v_3_4/Employee.entity");
		assertEquals(85, elements.size());
	}

	@Test
	public void testEntityElementsParserBadge() throws DslModelError {
		List<Element> elements = parseEntityFile("src/test/resources/entity_test_v_3_4/Badge.entity");
		assertEquals(18, elements.size());
	}

	@Test
	public void testEntityFileParserV33() throws DslModelError {
		parseEntityFile("src/test/resources/entity_test_v_3_3/Country.entity");
		parseEntityFile("src/test/resources/entity_test_v_3_3/Employee.entity");
	}

	@Test
	public void testEntityFileParserV32() throws DslModelError {
		parseEntityFile("src/test/resources/entity_test_v_3_2/Employee.entity");
	}

	private List<Element> parseLine(String line) throws DslModelError {
		EntityElementsParser parser = new EntityElementsParser();
		List<Element> elements = new LinkedList<>();
		parser.processLine("MyEntity", line, 1, elements);
		return elements;
	}
	
	@Test
	public void testLine1() throws DslModelError {
		List<Element> elements = parseLine("  @DefaultValue(aa)    #MyTag  #Foo(12) ");
		assertEquals(3, elements.size());
		assertEquals("@DefaultValue(aa)", elements.get(0).getContent());
		assertEquals("#MyTag", elements.get(1).getContent());
		assertEquals("#Foo(12)", elements.get(2).getContent());
	}

	@Test
	public void testLine2() throws DslModelError {
		List<Element> elements = parseLine(
				"  @DefaultValue( \"a \\\"xx\\\"a\" )    #MyTag  "
				+ "#Foo(1\\\"2)  @OneToOne ");
		assertEquals("@DefaultValue( \"a \\\"xx\\\"a\" )", elements.get(0).getContent());
		assertEquals("#MyTag", elements.get(1).getContent());
		assertEquals("#Foo(1\\\"2)  @OneToOne ", elements.get(2).getContent());
	}

	@Test
	public void testLine3() throws DslModelError {
		List<Element> elements = parseLine(
				" @Min(12) @Label( Identifier )   #MyTag  " );
		assertEquals("@Min(12)", elements.get(0).getContent());
		assertEquals("@Label( Identifier )", elements.get(1).getContent());
		assertEquals("#MyTag", elements.get(2).getContent());
	}

	@Test
	public void testLine4() throws DslModelError {
		List<Element> elements = parseLine(
				" myField  :  Person[]  " );
		assertEquals(3, elements.size());
		assertEquals("myField", elements.get(0).getContent());
		assertEquals(":", elements.get(1).getContent());
		assertEquals("Person[]", elements.get(2).getContent());
	}

	@Test
	public void testLine5() throws DslModelError {
		List<Element> elements = parseLine(
				" myField  :  Person []  " );
		assertEquals(4, elements.size());
		assertEquals("myField", elements.get(0).getContent());
		assertEquals(":", elements.get(1).getContent());
		assertEquals("Person", elements.get(2).getContent());
		assertEquals("[]", elements.get(3).getContent());
	}

	@Test
	public void testLine6() throws DslModelError {
		List<Element> elements = parseLine(
				" salary : aa decimal { } " );
		assertEquals(6, elements.size());
		int i = 0 ;
		assertEquals("salary", elements.get(i++).getContent());
		assertEquals(":", elements.get(i++).getContent());
		assertEquals("aa", elements.get(i++).getContent());
		assertEquals("decimal", elements.get(i++).getContent());
		assertEquals("{", elements.get(i++).getContent());
		assertEquals("}", elements.get(i).getContent());
	}

	@Test ( expected=DslModelError.class)
	public void testLineErr1() throws DslModelError {
		parseLine(" @Min(12) @Label ( Identifier )   #MyTag  " );
		// [MyEntity](1) : Unexpected character [(] after element [@Label]
	}

	@Test ( expected=DslModelError.class)
	public void testLineErr11() throws DslModelError {
		parseLine("(  salary : decimal " );
		// [MyEntity](1) : Unexpected character [(] 
	}

	@Test ( expected=DslModelError.class)
	public void testLineErr21() throws DslModelError {
		parseLine("  salary : \" decimal { }  " );
		// [MyEntity](1) :  Unexpected character ["] after element [salary]
	}
}
