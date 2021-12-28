package org.telosys.tools.dsl.parser2;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.telosys.tools.dsl.parser.exceptions.FieldParsingError;
import org.telosys.tools.dsl.parser.exceptions.ParsingError;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;
import org.telosys.tools.dsl.parser.model.DomainField;
import org.telosys.tools.dsl.parser.model.DomainTag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

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
	
	private DomainField process(String... elements) { // throws ParsingError {
		List<String> entitiesNames = Arrays.asList("Country", "Employee");
		FieldElementsProcessor processor = new FieldElementsProcessor("Country", entitiesNames);

		DomainField field = processor.processFieldElements( buildElements(elements), errors );
		
		log(field);
		log(errors);
		return field;
	}

	@Test
	public void test1() { // throws ParsingError  {
		DomainField field = process("firstName", ":", "string");
		assertTrue(errors.isEmpty());
		assertEquals(0, errors.getNumberOfErrors());

		assertEquals("firstName", field.getName());
		assertTrue(field.getType().isNeutralType() );
	}

	@Test
	public void test11() { // throws ParsingError  {
		DomainField field = process("firstName", ":", "string", "{", "}");
		assertTrue(errors.isEmpty());
		assertEquals(0, errors.getNumberOfErrors());

		assertEquals("firstName", field.getName());
		assertTrue(field.getType().isNeutralType() );
	}

	@Test
	public void test12() { // throws ParsingError  {
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

	@Test //(expected=FieldParsingError.class)
	public void testErr1() { //  throws ParsingError  {
		DomainField field = process("firstName", ":", "xxx", "{", "@MaxLen(20)", "#Foo(12)", "#Bar", "}");
		// invalid type 'xxx' 
		assertNull(field);
		assertFalse(errors.isEmpty());
		assertEquals(1, errors.getNumberOfErrors());
	}

	@Test //(expected=FieldParsingError.class)
	public void testErr2() { // throws ParsingError  {
		DomainField field = process("firstName", ":", "string", "{", "@FooBar(20)", "}");
		// field OK, but unknown annotation
		assertNotNull(field); 
		assertFalse(errors.isEmpty());
		assertEquals(1, errors.getNumberOfErrors());
	}

	@Test  // (expected=FieldParsingError.class)
	public void testCommaError() throws ParsingError  {
		DomainField field = process("count", ":", "int", 
				"{", "@Max(12)",  ",",  "@NotNull",  "@SizeMax(12)", "}" ); 
		// ERR : ',' invalid element
		assertNotNull(field);
		assertFalse(errors.isEmpty());
		assertEquals(1, errors.getNumberOfErrors());
	}

	@Test 
	public void testCommaAtTheEnd() { // throws ParsingError  {
		DomainField field = process("count", ":", "int", 
				"{", "@Max(12),", "@Min(  0 ),", "@NotNull",  "@SizeMax(12)", "}" ); 
		// Ok : ')' => end of annotation or tag => "," is isolated in an element
		assertNotNull(field);
		assertTrue(errors.isEmpty());
		assertEquals(0, errors.getNumberOfErrors());
	}

	@Test // (expected=FieldParsingError.class)
	public void testCommaAtTheEndError() { // throws ParsingError  {
		DomainField field = process("count", ":", "int", 
				"{", "@Id,", "@Min(  0 ),", "@NotNull",  "@SizeMax(12)", "}" ); 
		// ERR : "@Id," invalid name
		assertNotNull(field);
		assertFalse(errors.isEmpty());
		assertEquals(1, errors.getNumberOfErrors());
	}

	@Test //(expected=FieldParsingError.class)
	public void testCommaAtTheBeginning() throws ParsingError  {
		DomainField field = process("count", ":", "int", 
				"{", "@Max(12),", ",@Min(  0 ),", "@NotNull",  "@SizeMax(12)", "}" ); 
		// ERR : ",@Min" not an annotation or tag 
		assertNotNull(field);
		assertFalse(errors.isEmpty());
		assertEquals(1, errors.getNumberOfErrors());
	}

	@Test
	public void test2() { // throws ParsingError  {
		DomainField field = process("country", ":", "Country");
		assertNotNull(field);
		assertTrue(errors.isEmpty());
		assertEquals(0, errors.getNumberOfErrors());

		assertEquals("country", field.getName());
		assertTrue(field.getType().isEntity() );
		assertEquals("Country", field.getType().getName());
	}

	@Test // (expected=FieldParsingError.class)
	public void testErr21()  { // throws ParsingError  {
		DomainField field = process("country", ":", "Country", "{", "@FooBar(20)", "}");
		// '@FooBar(20)' : unknown annotation
		assertNotNull(field);
		assertFalse(errors.isEmpty());
		assertEquals(1, errors.getNumberOfErrors());
	}

	@Test //(expected=FieldParsingError.class)
	public void testErr22()  { // throws ParsingError  {
		DomainField field = process("country", ":", "Country", "{", "@Embedded", "}", "aa");
		// unexpected element 'aa'
		assertNotNull(field);
		assertFalse(errors.isEmpty());
		assertEquals(1, errors.getNumberOfErrors());
	}

	@Test //(expected=FieldParsingError.class)
	public void testErr23()  { // throws ParsingError  {
		DomainField field = process("country", ":", "Country", "zzz", "{", "@Embedded", "}" );
		// unexpected element 'zzz'
		assertNotNull(field);
		assertFalse(errors.isEmpty());
		assertEquals(1, errors.getNumberOfErrors());
	}

	@Test // (expected=FieldParsingError.class)
	public void testErr24()  { // throws ParsingError  {
		DomainField field = process("country", ":", ":", "Country", "{", "@Embedded", "}", "aa");
		// invalid type ':'
		assertNull(field);
		assertFalse(errors.isEmpty());
		assertEquals(1, errors.getNumberOfErrors());
	}

	@Test // (expected=FieldParsingError.class)
	public void testErr25()  { // throws ParsingError  {
		DomainField field = process("country", ":", "Nation", "{", "@Embedded", "}");
		// invalid type 'Nation'
		assertNull(field);
		assertFalse(errors.isEmpty());
		assertEquals(1, errors.getNumberOfErrors());
	}
	
	//--------------------------------------------------------------------------
	// extractAdditionalElements()
	//--------------------------------------------------------------------------
	@Test //(expected=FieldParsingError.class)
	public void testExtractAdditionalElements() throws ParsingError  {
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

	@Test (expected=FieldParsingError.class)
	public void testExtractAdditionalElementsErr1() throws ParsingError  {
		List<String> entitiesNames = Arrays.asList("Country", "Employee");
		FieldElementsProcessor processor = new FieldElementsProcessor("Country", entitiesNames);

		processor.extractAdditionalElements("foo", 
				buildElements("foo",":","int",  "aaa","}") ); // unexpected element 'aaa' out of {...}
	}

	@Test (expected=FieldParsingError.class)
	public void testExtractAdditionalElementsErr2() throws ParsingError  {
		List<String> entitiesNames = Arrays.asList("Country", "Employee");
		FieldElementsProcessor processor = new FieldElementsProcessor("Country", entitiesNames);

		processor.extractAdditionalElements("foo", 
				buildElements("foo",":","int",  "{", "aaa",  "{", "}") ); // multiple {
	}

	@Test (expected=FieldParsingError.class)
	public void testExtractAdditionalElementsErr3() throws ParsingError  {
		List<String> entitiesNames = Arrays.asList("Country", "Employee");
		FieldElementsProcessor processor = new FieldElementsProcessor("Country", entitiesNames);

		processor.extractAdditionalElements("foo", 
				buildElements("foo",":","int",  "{", "aaa",  "}", "}") ); // multiple }
	}
	
}
