package org.telosys.tools.dsl.parser;

import org.junit.Test;
import org.telosys.tools.dsl.parser.exceptions.ParsingError;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;
import org.telosys.tools.dsl.parser.model.DomainFK;

import static org.junit.Assert.assertEquals;

public class FieldFKAnnotationParserTest {

	private DomainFK process(String param) throws ParsingError { // AnnotationOrTagError {
		FieldFKAnnotationParser parser = new FieldFKAnnotationParser("MyEntity", "myField");
		DomainAnnotation annotation = new DomainAnnotation("FK",param);
		return parser.parse(annotation);
	}
	
	@Test
    public void test1() throws ParsingError { // AnnotationOrTagError {
		String expectedFKName = "FK_MyEntity_Book";
		DomainFK fk = process("Book");
		assertEquals(expectedFKName, fk.getFkName() );
		assertEquals("Book", fk.getReferencedEntityName() );
		assertEquals("", fk.getReferencedFieldName() );

		fk = process("  Book  ");
		assertEquals(expectedFKName, fk.getFkName() );
		assertEquals("Book", fk.getReferencedEntityName() );
		assertEquals("", fk.getReferencedFieldName() );

		fk = process("Book.");
		assertEquals(expectedFKName, fk.getFkName() );
		assertEquals("Book", fk.getReferencedEntityName() );
		assertEquals("", fk.getReferencedFieldName() );

		fk = process("Book  .  ");
		assertEquals(expectedFKName, fk.getFkName() );
		assertEquals("Book", fk.getReferencedEntityName() );
		assertEquals("", fk.getReferencedFieldName() );

		fk = process("   ,  Book.");
		assertEquals(expectedFKName, fk.getFkName() );
		assertEquals("Book", fk.getReferencedEntityName() );
		assertEquals("", fk.getReferencedFieldName() );

	}
	
	@Test
    public void test2() throws ParsingError { // AnnotationOrTagError {
		String expectedFKName = "FK_MyEntity_Book";
		DomainFK fk = process("Book.id");
		assertEquals(expectedFKName, fk.getFkName() );
		assertEquals("Book", fk.getReferencedEntityName() );
		assertEquals("id", fk.getReferencedFieldName() );

		fk = process("  Book.id  ");
		assertEquals(expectedFKName, fk.getFkName() );
		assertEquals("Book", fk.getReferencedEntityName() );
		assertEquals("id", fk.getReferencedFieldName() );

		fk = process("  Book.  id  ");
		assertEquals(expectedFKName, fk.getFkName() );
		assertEquals("Book", fk.getReferencedEntityName() );
		assertEquals("id", fk.getReferencedFieldName() );

		fk = process("  Book  .  id  ");
		assertEquals(expectedFKName, fk.getFkName() );
		assertEquals("Book", fk.getReferencedEntityName() );
		assertEquals("id", fk.getReferencedFieldName() );

		fk = process("  ,  Book  .  id  ");
		assertEquals(expectedFKName, fk.getFkName() );
		assertEquals("Book", fk.getReferencedEntityName() );
		assertEquals("id", fk.getReferencedFieldName() );
	}
	
	@Test
    public void test3() throws ParsingError { // AnnotationOrTagError {
		DomainFK fk = process("MY_FK,Book");
		assertEquals("MY_FK", fk.getFkName() );
		assertEquals("Book", fk.getReferencedEntityName() );
		assertEquals("", fk.getReferencedFieldName() );

		fk = process("  MY_FK  ,   Book    ");
		assertEquals("MY_FK", fk.getFkName() );
		assertEquals("Book", fk.getReferencedEntityName() );
		assertEquals("", fk.getReferencedFieldName() );

		fk = process("  MY_FK  ,   Book.    ");
		assertEquals("MY_FK", fk.getFkName() );
		assertEquals("Book", fk.getReferencedEntityName() );
		assertEquals("", fk.getReferencedFieldName() );

		fk = process("  MY_FK  ,   Book  .    ");
		assertEquals("MY_FK", fk.getFkName() );
		assertEquals("Book", fk.getReferencedEntityName() );
		assertEquals("", fk.getReferencedFieldName() );
	}
	
	@Test
    public void test4() throws ParsingError { // AnnotationOrTagError {
		DomainFK fk = process("MY_FK,Book.foo");
		assertEquals("MY_FK", fk.getFkName() );
		assertEquals("Book", fk.getReferencedEntityName() );
		assertEquals("foo", fk.getReferencedFieldName() );

		fk = process("  MY_FK  ,   Book.foo    ");
		assertEquals("MY_FK", fk.getFkName() );
		assertEquals("Book", fk.getReferencedEntityName() );
		assertEquals("foo", fk.getReferencedFieldName() );

		fk = process("  MY_FK  ,   Book . foo   ");
		assertEquals("MY_FK", fk.getFkName() );
		assertEquals("Book", fk.getReferencedEntityName() );
		assertEquals("foo", fk.getReferencedFieldName() );
	}
	
	//@Test (expected = AnnotationOrTagError.class)
//	@Test (expected = ParsingError.class)
	@Test (expected = Exception.class)
    public void testErr0() throws ParsingError { // AnnotationOrTagError {
		process(null);
	}
	//@Test (expected = AnnotationOrTagError.class)
	@Test (expected = ParsingError.class)
    public void testErr1() throws ParsingError { // AnnotationOrTagError {
		process("");
	}
	//@Test (expected = AnnotationOrTagError.class)
	@Test (expected = ParsingError.class)
    public void testErr2() throws ParsingError { // AnnotationOrTagError {
		process("MY_FK,Book,foo");
	}
	//@Test (expected = AnnotationOrTagError.class)
	@Test (expected = ParsingError.class)
    public void testErr3() throws ParsingError { // AnnotationOrTagError {
		process("Book.foo.bar");
	}
	//@Test (expected = AnnotationOrTagError.class)
	@Test (expected = ParsingError.class)
    public void testErr4() throws ParsingError { // AnnotationOrTagError {
		FieldFKAnnotationParser parser = new FieldFKAnnotationParser("MyEntity", "myField");
		DomainAnnotation annotation = new DomainAnnotation("FOO","Book.id"); // bad annotation name
		parser.parse(annotation);
	}

}
