package org.telosys.tools.dsl.parser;

import java.util.List;

import org.junit.Test;
import org.telosys.tools.dsl.DslParserException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class SplitterTest {
	
//	@Test(expected = DslParserException.class)
//	public void testParseFileWithAFileWhichDoesntExist() {
//		File file = new File("entity_test/nul.entity");
//		EntityParser parser = new EntityParser(new DomainModel("model"));
//		parser.parse(file);
//	}

	private List<String> split(String s) {
		System.out.println("\nsplit(\""+s+"\")");
		Splitter splitter = new Splitter();
		List<String> list;
		try {
			list = splitter.split(s);
			print(list);
			return list;
		} catch (Exception e) {
			print(e);
			throw e;
		}
	}
	
	private void print(List<String> list) {
		System.out.println("\n-----");
		for ( String s : list ) {
			System.out.println(" . [" + s + "]");
		}
	}
	private void print(Exception e) {
		System.out.println("\n----- EXCEPTION ");
		System.out.println(e.getClass().getName() + " : " + e.getMessage());
	}

	@Test
	public void test1() {
		int i = 0 ;
		List<String> list ;

		list = split("@NotBlank,@Test(\"  a z'e''r}zz\")");
		i = 0 ;
		assertEquals(2, list.size());
		assertEquals("@NotBlank", list.get(i++));
		assertEquals("@Test(\"  a z'e''r}zz\")", list.get(i++));
		
		
		list = split("@NotBlank@Test@Foo(12)");
		i = 0 ;
		assertEquals(3, list.size());
		assertEquals("@NotBlank", list.get(i++));
		assertEquals("@Test", list.get(i++));
		assertEquals("@Foo(12)", list.get(i++));

		list = split("@NotBlank,@Test,,,@Foo(12)@NotNull");
		i = 0 ;
		assertEquals(4, list.size());
		assertEquals("@NotBlank", list.get(i++));
		assertEquals("@Test", list.get(i++));
		assertEquals("@Foo(12)", list.get(i++));
		assertEquals("@NotNull", list.get(i++));
		
	}
	
	@Test
	public void test2() {
		int i = 0 ;
		List<String> list ;

		list = split("@NotBlankO++-,@Test896@Foo!:/@--ab++??   @Foo @Foo");
		i = 0 ;
		assertEquals(6, list.size());
		assertEquals("@NotBlankO++-", list.get(i++));
		assertEquals("@Test896", list.get(i++));
		assertEquals("@Foo!:/", list.get(i++));
		assertEquals("@--ab++??", list.get(i++));
		assertEquals("@Foo", list.get(i++));
		assertEquals("@Foo", list.get(i++));
		
		
	}

	@Test(expected = DslParserException.class)
	public void testError1() {
		// unexpected '(' 
		split("@NotBlank (()   @Test(\"  a z'e''r}zz\")");
	}

	@Test(expected = DslParserException.class)
	public void testError2() {
		// unexpected ')' 
		split("@NotBlank (  ) )   @Foo(\"  a z'e''r}zz\")  @Bar");
	}

	@Test(expected = DslParserException.class)
	public void testError3() {
		// unexpected '@' 
		split("@NotBlank (  @ 'aa' )   @Foo  @Bar");
	}

	@Test(expected = DslParserException.class)
	public void testError4() {
		// unexpected '"' 
		split("@NotBlank \" ('aa')   @Foo  @Bar");
	}

	@Test(expected = DslParserException.class)
	public void testError5() {
		// unexpected single quote
		split("@NotBlank ' ('aa')   @Foo  @Bar");
	}

	@Test(expected = DslParserException.class)
	public void testError6() {
		// unexpected single quote
		split("@NotBlank('aa')   @Foo ' @Bar");
	}

	@Test(expected = DslParserException.class)
	public void testError7() {
		// unexpected '"' 
		split("@NotBlank  ('aa )   @Foo  @Bar");
	}
	@Test(expected = DslParserException.class)
	public void testError8() {
		// unexpected '"' 
		split("@NotBlank  ('aa '   @Foo  @Bar");
	}
	@Test(expected = DslParserException.class)
	public void testError9() {
		// unexpected '"' 
		split("@NotBlank  ('aa ' ");
	}

	@Test
	public void testTrim() {
		assertEquals("@NotBlank (  )", "  @NotBlank (  )  ".trim() ) ;
	}

	
}
