package org.telosys.tools.dsl.parser;

import java.util.List;

import org.junit.Test;
import org.telosys.tools.dsl.parser.exceptions.FieldParsingError;
import org.telosys.tools.dsl.parser.exceptions.ParsingError;

import static org.junit.Assert.assertEquals;

public class SplitterTest {
	
//	@Test(expected = DslParserException.class)
//	public void testParseFileWithAFileWhichDoesntExist() {
//		File file = new File("entity_test/nul.entity");
//		EntityParser parser = new EntityParser(new DomainModel("model"));
//		parser.parse(file);
//	}

//	private List<String> split(String s) throws AnnotationOrTagError {
	private List<String> split(String s) throws FieldParsingError {
		System.out.println("-----");
		System.out.println("split(\""+s+"\")");
		FieldAnnotationsAndTagsSplitter splitter = new FieldAnnotationsAndTagsSplitter("MyEntity","myField");
		List<String> list;
		try {
			list = splitter.split(s);
			print(list);
			return list;
//		} catch (AnnotationOrTagError e) {
		} catch (ParsingError e) {
			print(e);
			throw e;
		} catch (Exception e) {
			print(e);
			throw e;
		}
	}
	
	private void print(List<String> list) {
		for ( String s : list ) {
			System.out.println(" . [" + s + "]");
		}
	}
	private void print(Exception e) {
		System.out.println("--> EXCEPTION " + e.getClass().getSimpleName() + " : " + e.getMessage() );
	}

	@Test
	public void test1() throws FieldParsingError { // AnnotationOrTagError  {
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
	public void test2() throws FieldParsingError { // AnnotationOrTagError  {
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

	@Test
	public void test3() throws FieldParsingError { // AnnotationOrTagError {
		int i = 0 ;
		List<String> list ;

		list = split("@NotBlank,#Test @Foo #MyTag,, #Bar");
		i = 0 ;
		assertEquals(5, list.size());
		assertEquals("@NotBlank", list.get(i++));
		assertEquals("#Test", list.get(i++));
		assertEquals("@Foo", list.get(i++));
		assertEquals("#MyTag", list.get(i++));
		assertEquals("#Bar", list.get(i++));
	}

	@Test
	public void test4() throws FieldParsingError { // AnnotationOrTagError {
		int i = 0 ;
		List<String> list ;

		list = split("@NotBlank, #Test @Foo1( ab cd ), @Foo2( ab,cd ) @Foo3(  'ab,cd') ");
		i = 0 ;
		//assertEquals(4, list.size());
		assertEquals("@NotBlank", list.get(i++));
		assertEquals("#Test", list.get(i++));
		assertEquals("@Foo1( ab cd )", list.get(i++));
		assertEquals("@Foo2( ab,cd )", list.get(i++));
		assertEquals("@Foo3(  'ab,cd')", list.get(i++));
	}

//	@Test(expected = AnnotationOrTagError.class)
	@Test(expected = FieldParsingError.class)
	public void testError1() throws FieldParsingError { // AnnotationOrTagError  {
		// unexpected '(' 
		split("@NotBlank (()   @Test(\"  a z'e''r}zz\")");
	}

//	@Test(expected = AnnotationOrTagError.class)
	@Test(expected = FieldParsingError.class)
	public void testError2() throws FieldParsingError { // AnnotationOrTagError  {
		// unexpected ')' 
		split("@NotBlank (  ) )   @Foo(\"  a z'e''r}zz\")  @Bar");
	}

//	@Test(expected = AnnotationOrTagError.class)
	@Test(expected = FieldParsingError.class)
	public void testError3() throws FieldParsingError { // AnnotationOrTagError {
		// unexpected '@' 
		split("@NotBlank (  @ 'aa' )   @Foo  @Bar");
	}

//	@Test(expected = AnnotationOrTagError.class)
	@Test(expected = FieldParsingError.class)
	public void testError4() throws FieldParsingError { // AnnotationOrTagError {
		// unexpected '"' 
		split("@NotBlank \" ('aa')   @Foo  @Bar");
	}

//	@Test(expected = AnnotationOrTagError.class)
	@Test(expected = FieldParsingError.class)
	public void testError5() throws FieldParsingError { // AnnotationOrTagError {
		// unexpected single quote
		split("@NotBlank ' ('aa')   @Foo  @Bar");
	}

//	@Test(expected = AnnotationOrTagError.class)
	@Test(expected = FieldParsingError.class)
	public void testError6() throws FieldParsingError { // AnnotationOrTagError {
		// unexpected single quote
		split("@NotBlank('aa')   @Foo ' @Bar");
	}

//	@Test(expected = AnnotationOrTagError.class)
	@Test(expected = FieldParsingError.class)
	public void testError7() throws FieldParsingError { // AnnotationOrTagError {
		// unexpected '"' 
		split("@NotBlank  ('aa )   @Foo  @Bar");
	}
//	@Test(expected = AnnotationOrTagError.class)
	@Test(expected = FieldParsingError.class)
	public void testError8() throws FieldParsingError { // AnnotationOrTagError {
		// unexpected '"' 
		split("@NotBlank  ('aa '   @Foo  @Bar");
	}
//	@Test(expected = AnnotationOrTagError.class)
	@Test(expected = FieldParsingError.class)
	public void testError9() throws FieldParsingError { // AnnotationOrTagError {
		// unexpected '"' 
		split("@NotBlank  ('aa ' ");
	}

	@Test
	public void testTrim() {
		assertEquals("@NotBlank (  )", "  @NotBlank (  )  ".trim() ) ;
	}

	
}
