package org.telosys.tools.dsl.parser;

import org.junit.Test;
import org.telosys.tools.dsl.DslModelError;
import org.telosys.tools.dsl.parser.Element;
import org.telosys.tools.dsl.parser.TagProcessor;
import org.telosys.tools.dsl.parser.model.DomainTag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TagParserTest {

	private TagProcessor getParser() {
		return new TagProcessor("MyEntity", "myField");
	}

	private DomainTag parseTag(String tagString) throws DslModelError {
		Element element = new Element(12, tagString );
		TagProcessor parser = getParser();
		return parser.parseTag(element);
	}
	

	@Test
	public void testParseTag() throws DslModelError {
		DomainTag tag ;
		
		tag = parseTag("#Foo");
		assertEquals("Foo", tag.getName());
		assertFalse(tag.hasParameter());
		
		tag = parseTag("#FooBar(abc)");
		assertEquals("FooBar", tag.getName());
		assertTrue(tag.hasParameter());
		assertEquals("abc", tag.getParameter());

		tag = parseTag("#MyTag(\" abc \")");
		assertEquals("MyTag", tag.getName());
		assertTrue(tag.hasParameter());
		assertEquals(" abc ", tag.getParameter());

		tag = parseTag("#MyTag(\" a(b)c \")");
		assertEquals("MyTag", tag.getName());
		assertTrue(tag.hasParameter());
		assertEquals(" a(b)c ", tag.getParameter());

		tag = parseTag("#Maximum(  123  )");
		assertEquals("Maximum", tag.getName());
		assertTrue(tag.hasParameter());
		assertEquals("123", tag.getParameter());

		parseWithExpectedException("#Fo o(123)");
		parseWithExpectedException("# Foo(123)");
		parseWithExpectedException("#Fo o");
		parseWithExpectedException("#Foo(123");
	}
		
	private void parseWithExpectedException(String s) {
		DslModelError error = null ;
		try {
			parseTag(s);
		} catch (DslModelError e) {
			error = e;
		} 
		assertNotNull(error);
	}
	
	@Test(expected = DslModelError.class)
	public void testParseTagError1() throws DslModelError {
		parseTag("#Fo o(123)");
	}

	@Test(expected = DslModelError.class)
	public void testParseTagError2() throws DslModelError {
		parseTag("# Foo()");
	}

	@Test(expected = DslModelError.class)
	public void testParseTagError3() throws DslModelError {
		parseTag("#Foo(123");
	}

	@Test(expected = DslModelError.class)
	public void testParseTagError4() throws DslModelError {
		parseTag("#Foo 123)");
	}

}
