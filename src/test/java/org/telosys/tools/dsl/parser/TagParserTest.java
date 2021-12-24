package org.telosys.tools.dsl.parser;

import org.junit.Test;
import org.telosys.tools.dsl.parser.exceptions.ParsingError;
import org.telosys.tools.dsl.parser.model.DomainTag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TagParserTest {

	private TagParser getParser() {
		return new TagParser("MyEntity", "myField");
	}

	private DomainTag parseTag(String tagString) throws ParsingError {
		TagParser parser = getParser();
		return parser.parseTag(tagString);
	}
	

	@Test
	public void testParseTag() throws ParsingError {
		TagParser parser = getParser();
		DomainTag tag ;
		
		tag = parser.parseTag("#Foo");
		assertEquals("Foo", tag.getName());
		assertFalse(tag.hasParameter());
		
		tag = parser.parseTag("#FooBar(abc)");
		assertEquals("FooBar", tag.getName());
		assertTrue(tag.hasParameter());
		assertEquals("abc", tag.getParameter());

		tag = parser.parseTag("#Maximum(  123  )");
		assertEquals("Maximum", tag.getName());
		assertTrue(tag.hasParameter());
		assertEquals("123", tag.getParameter());

		parseWithExpectedException("#Fo o(123)");
		parseWithExpectedException("# Foo(123)");
		parseWithExpectedException("#Fo o");
		parseWithExpectedException("#Foo(123");
	}
		
	private void parseWithExpectedException(String s) {
		TagParser parser = getParser();
		ParsingError error = null ;
		try {
			parser.parseTag(s);
		} catch (ParsingError e) {
			error = e;
		} 
		assertNotNull(error);
	}
	
	@Test(expected = ParsingError.class)
	public void testParseTagError1() throws ParsingError {
		TagParser parser = getParser();
		parser.parseTag("#Fo o(123)");
	}

	@Test(expected = ParsingError.class)
	public void testParseTagError2() throws ParsingError {
		TagParser parser = getParser();
		parser.parseTag("# Foo()");
	}

	@Test(expected = ParsingError.class)
	public void testParseTagError3() throws ParsingError {
		TagParser parser = getParser();
		parser.parseTag("#Foo(123");
	}

	@Test(expected = ParsingError.class)
	public void testParseTagError4() throws ParsingError {
		TagParser parser = getParser();
		parser.parseTag("#Foo 123)");
	}

}
