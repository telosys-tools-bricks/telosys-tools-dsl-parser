package org.telosys.tools.dsl.parser;

import org.junit.Test;
import org.telosys.tools.dsl.DslModelError;
import org.telosys.tools.dsl.parser.model.DomainField;
import org.telosys.tools.dsl.parser.model.DomainNeutralType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class AnnotationAndTagProcessorTest {

	private AnnotationAndTagProcessor getParser(int i) {
		if ( i == 1 ) {
			// use "AnnotationParser" as concrete class
			DomainField field = new DomainField(12, "myField", new DomainNeutralType("string") );
			return new AnnotationProcessor("MyEntity", field);
		}
		else {
			// use "TagParser" as concrete class
			return new TagProcessor("MyEntity", "myField");
		}
	}
	private String getName(String s, int i) throws DslModelError {
		Element e = new Element(10, s);
		AnnotationAndTagProcessor parser = getParser(i);
		return parser.getName(e);
	}
	private String getParameterValue(String s, int i) throws DslModelError {
		Element e = new Element(10, s);
		AnnotationAndTagProcessor parser = getParser(i);
		return parser.getParameterValue(e);
	}
	
	@Test
	public void testGetName() throws DslModelError {
		for ( int i = 1 ; i <= 2 ; i++ ) {
			assertEquals("NotBlank", getName("@NotBlank", i));
			assertEquals("Foo", getName("@Foo", i));
			assertEquals("MyTag", getName("#MyTag", i));
			assertEquals("Foo", getName("@Foo()", i));
			assertEquals("Foo", getName("@Foo(azer)", i));
			assertEquals("Foo", getName("@Foo('azer')", i));
			assertEquals("Foo", getName("@Foo(\"a(z)er\")", i));
			assertEquals("Foo", getName("@Foo(123)", i));
			assertEquals("Foo", getName("@Foo(123.45)", i));
			assertEquals("Foo", getName("@Foo ( 123.45 ) ", i));
			assertEquals("Foo", getName("?Foo ( 123.45 ) ", i));
		}
	}

	@Test(expected = DslModelError.class)
	public void testGetNameError1() throws DslModelError {
		getName("@Fo o ( 123.45  )", 1);
	}

	@Test(expected = DslModelError.class)
	public void testGetNameError2() throws DslModelError {
		getName("@ Foo ( 123.45  )", 1);
	}

	@Test
	public void testGetParameterValue() throws DslModelError {
		for ( int i = 1 ; i <= 2 ; i++ ) {	
	
			assertNull(getParameterValue("@Foo", i));
			assertNull(getParameterValue("#Tag", i));
	
			assertEquals("", getParameterValue("@Foo()", i));
			assertEquals("", getParameterValue("@Foo(   )", i));
			assertEquals("azer", getParameterValue("@Foo(azer)", i));
			assertEquals("'azer'", getParameterValue("@Foo('azer')", i));
			assertEquals("\"azer\"", getParameterValue("@Foo(\"azer\")", i));
			assertEquals("\"a(z)er\"", getParameterValue("@Foo(\"a(z)er\")", i));
			assertEquals("123", getParameterValue("@Foo(123)", i));
			assertEquals("123.45", getParameterValue("@Foo(123.45)", i));
			assertEquals("123.45", getParameterValue("@Foo ( 123.45 ) ", i));
			assertEquals("123.45", getParameterValue("?Foo ( 123.45 ) ", i));
			assertEquals("(123)", getParameterValue("@Foo((123))", i));
			assertEquals("(123)", getParameterValue("@Foo( (123)  )", i));
	
			assertEquals("", getParameterValue("#Tag()", i));
			assertEquals("", getParameterValue("#Tag(   )", i));
			assertEquals("123.45", getParameterValue("#Tag ( 123.45 ) ", i));
		}
	}

}
