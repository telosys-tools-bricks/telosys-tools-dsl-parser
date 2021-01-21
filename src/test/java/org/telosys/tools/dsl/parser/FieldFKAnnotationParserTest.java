package org.telosys.tools.dsl.parser;

import org.junit.Test;
import org.telosys.tools.dsl.parser.exceptions.AnnotationOrTagError;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;
import org.telosys.tools.dsl.parser.model.DomainFK;

import static org.junit.Assert.assertEquals;

public class FieldFKAnnotationParserTest {

	private DomainFK process(String param) throws AnnotationOrTagError {
		FieldFKAnnotationParser parser = new FieldFKAnnotationParser("MyEntity", "myField");
		DomainAnnotation annotation = new DomainAnnotation("FK",param);
		return parser.parse(annotation);
	}
	
	@Test
    public void test1() throws AnnotationOrTagError {
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
    public void test2() throws AnnotationOrTagError {
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
    public void test3() throws AnnotationOrTagError {
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
    public void test4() throws AnnotationOrTagError {
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
	
	@Test (expected = AnnotationOrTagError.class)
    public void testErr0() throws AnnotationOrTagError {
		process(null);
	}
	@Test (expected = AnnotationOrTagError.class)
    public void testErr1() throws AnnotationOrTagError {
		process("");
	}
	@Test (expected = AnnotationOrTagError.class)
    public void testErr2() throws AnnotationOrTagError {
		process("MY_FK,Book,foo");
	}
	@Test (expected = AnnotationOrTagError.class)
    public void testErr3() throws AnnotationOrTagError {
		process("Book.foo.bar");
	}
	@Test (expected = AnnotationOrTagError.class)
    public void testErr4() throws AnnotationOrTagError {
		FieldFKAnnotationParser parser = new FieldFKAnnotationParser("MyEntity", "myField");
		DomainAnnotation annotation = new DomainAnnotation("FOO","Book.id"); // bad annotation name
		parser.parse(annotation);
	}

}
