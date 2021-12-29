package org.telosys.tools.dsl.parser2;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;
import org.telosys.tools.dsl.parser.model.DomainField;
import org.telosys.tools.dsl.parser.model.DomainTag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class FieldElementsProcessorTest {
	
	private void log(DomainField field) {
		System.out.println(field);
	}
	private void log(ParsingErrors errors) {
		System.out.println(errors);
	}

	private ParsingErrors errors;
	@Before
	public void before() {
		errors = new ParsingErrors();
	}

	private List<Element> buildElements(String... elements) {
		List<Element> list = new LinkedList<>();
		for ( String s : elements ) {
			list.add(new Element(3, s));
		}
		return list;
	}
	
	private DomainField process(String... elements) {
		List<String> entitiesNames = Arrays.asList("Country", "Employee");
		FieldElementsProcessor processor = new FieldElementsProcessor("Country", entitiesNames);

		DomainField field = processor.processFieldElements( buildElements(elements), errors );
		
		log(field);
		log(errors);
		return field;
	}

	@Test
	public void test1() {
		DomainField field = process("firstName", ":", "string");
		assertTrue(errors.isEmpty());
		assertEquals(0, errors.getNumberOfErrors());

		assertEquals("firstName", field.getName());
		assertTrue(field.getType().isNeutralType() );
	}

	@Test
	public void test11() {
		DomainField field = process("firstName", ":", "string", "{", "}");
		assertTrue(errors.isEmpty());
		assertEquals(0, errors.getNumberOfErrors());

		assertEquals("firstName", field.getName());
		assertTrue(field.getType().isNeutralType() );
	}

	@Test
	public void test12() {
		DomainField field = process("firstName", ":", "string", "{", "@MaxLen(20)", "#Foo(12)", "#Bar", "}");
		assertTrue(errors.isEmpty());
		assertEquals(0, errors.getNumberOfErrors());

		assertEquals("firstName", field.getName());
		assertTrue(field.getType().isNeutralType() );
		Map<String, DomainAnnotation> annotations = field.getAnnotations() ;
		assertEquals(1, annotations.size());
		Map<String, DomainTag> tags = field.getTags() ;
		assertEquals(2, tags.size());
	}

	@Test
	public void testErr1() {
		DomainField field = process("firstName", ":", "xxx", "{", "@MaxLen(20)", "#Foo(12)", "#Bar", "}");
		// invalid type 'xxx' 
		assertNull(field);
		assertFalse(errors.isEmpty());
		assertEquals(1, errors.getNumberOfErrors());
	}

	@Test
	public void testErr2() {
		DomainField field = process("firstName", ":", "string", "{", "@FooBar(20)", "}");
		// field OK, but unknown annotation
		assertNotNull(field); 
		assertFalse(errors.isEmpty());
		assertEquals(1, errors.getNumberOfErrors());
	}

	@Test
	public void testCommaError() {
		DomainField field = process("count", ":", "int", 
				"{", "@Max(12)",  ",",  "@NotNull",  "@SizeMax(12)", "}" ); 
		// ERR : ',' invalid element
		assertNotNull(field);
		assertFalse(errors.isEmpty());
		assertEquals(1, errors.getNumberOfErrors());
	}

	@Test 
	public void testCommaAtTheEnd() {
		DomainField field = process("count", ":", "int", 
				"{", "@Max(12),", "@Min(  0 ),", "@NotNull",  "@SizeMax(12)", "}" ); 
		// Ok : ')' => end of annotation or tag => "," is isolated in an element
		assertNotNull(field);
		assertTrue(errors.isEmpty());
		assertEquals(0, errors.getNumberOfErrors());
	}

	@Test
	public void testCommaAtTheEndError() {
		DomainField field = process("count", ":", "int", 
				"{", "@Id,", "@Min(  0 ),", "@NotNull",  "@SizeMax(12)", "}" ); 
		// ERR : "@Id," invalid name
		assertNotNull(field);
		assertFalse(errors.isEmpty());
		assertEquals(1, errors.getNumberOfErrors());
	}

	@Test 
	public void testCommaAtTheBeginning() {
		DomainField field = process("count", ":", "int", 
				"{", "@Max(12),", ",@Min(  0 ),", "@NotNull",  "@SizeMax(12)", "}" ); 
		// ERR : ",@Min" not an annotation or tag 
		assertNotNull(field);
		assertFalse(errors.isEmpty());
		assertEquals(1, errors.getNumberOfErrors());
	}

	@Test
	public void test2() {
		DomainField field = process("country", ":", "Country");
		assertNotNull(field);
		assertTrue(errors.isEmpty());
		assertEquals(0, errors.getNumberOfErrors());

		assertEquals("country", field.getName());
		assertTrue(field.getType().isEntity() );
		assertEquals("Country", field.getType().getName());
	}

	@Test
	public void testErr21() {
		DomainField field = process("country", ":", "Country", "{", "@FooBar(20)", "}");
		// '@FooBar(20)' : unknown annotation
		assertNotNull(field);
		assertFalse(errors.isEmpty());
		assertEquals(1, errors.getNumberOfErrors());
	}

	@Test
	public void testErr22() {
		DomainField field = process("country", ":", "Country", "{", "@Embedded", "}", "aa");
		// unexpected element 'aa'
		assertNotNull(field);
		assertFalse(errors.isEmpty());
		assertEquals(1, errors.getNumberOfErrors());
	}

	@Test
	public void testErr23() {
		DomainField field = process("country", ":", "Country", "zzz", "{", "@Embedded", "}" );
		// unexpected element 'zzz'
		assertNotNull(field);
		assertFalse(errors.isEmpty());
		assertEquals(1, errors.getNumberOfErrors());
	}

	@Test 
	public void testErr24()  { 
		DomainField field = process("country", ":", ":", "Country", "{", "@Embedded", "}", "aa");
		// invalid type ':'
		assertNull(field);
		assertFalse(errors.isEmpty());
		assertEquals(1, errors.getNumberOfErrors());
	}

	@Test 
	public void testErr25()  { 
		DomainField field = process("country", ":", "Nation", "{", "@Embedded", "}");
		// invalid type 'Nation'
		assertNull(field);
		assertFalse(errors.isEmpty());
		assertEquals(1, errors.getNumberOfErrors());
	}
	
	//--------------------------------------------------------------------------
	// extractAdditionalElements()
	//--------------------------------------------------------------------------
	@Test
	public void testExtractAdditionalElements() throws ParserError  {
		List<String> entitiesNames = Arrays.asList("Country", "Employee");
		FieldElementsProcessor processor = new FieldElementsProcessor("Country", entitiesNames);

		List<Element> elements ;
		elements = processor.extractAdditionalElements("foo", 
				buildElements("aaa" ) );
		assertEquals(0, elements.size());

		elements = processor.extractAdditionalElements("foo", 
				buildElements("foo",":","int" ) );
		assertEquals(0, elements.size());

		elements = processor.extractAdditionalElements("foo", 
				buildElements("foo",":","int",  "{","}") );
		assertEquals(0, elements.size());
		
		elements = processor.extractAdditionalElements("foo", 
				buildElements("foo",":","int",  "{","@Max(10)","}") );
		assertEquals(1, elements.size());
		
		elements = processor.extractAdditionalElements("foo", 
				buildElements("foo",":","int",  "{","@Max(10)", "#Foo", "}") );
		assertEquals(2, elements.size());
		
	}

	@Test (expected=ParserError.class)
	public void testExtractAdditionalElementsErr1() throws ParserError  {
		List<String> entitiesNames = Arrays.asList("Country", "Employee");
		FieldElementsProcessor processor = new FieldElementsProcessor("Country", entitiesNames);

		processor.extractAdditionalElements("foo", 
				buildElements("foo",":","int",  "aaa","}") ); // unexpected element 'aaa' out of {...}
	}

	@Test (expected=ParserError.class)
	public void testExtractAdditionalElementsErr2() throws ParserError  {
		List<String> entitiesNames = Arrays.asList("Country", "Employee");
		FieldElementsProcessor processor = new FieldElementsProcessor("Country", entitiesNames);

		processor.extractAdditionalElements("foo", 
				buildElements("foo",":","int",  "{", "aaa",  "{", "}") ); // multiple {
	}

	@Test (expected=ParserError.class)
	public void testExtractAdditionalElementsErr3() throws ParserError  {
		List<String> entitiesNames = Arrays.asList("Country", "Employee");
		FieldElementsProcessor processor = new FieldElementsProcessor("Country", entitiesNames);

		processor.extractAdditionalElements("foo", 
				buildElements("foo",":","int",  "{", "aaa",  "}", "}") ); // multiple }
	}
	
}
