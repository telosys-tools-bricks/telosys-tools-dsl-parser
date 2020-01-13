package org.telosys.tools.dsl.parser;

import org.junit.Assert;
import org.junit.Test;

public class ParserUtilTest {
	
    @Test
    public void testTrim() {
        Assert.assertEquals("ab", "ab ".trim() );
        Assert.assertEquals("a b", " a b ".trim() );
        
        Assert.assertEquals("xx", "xx\t".trim() );
        Assert.assertEquals("xx", "\txx".trim() );
        Assert.assertEquals("xx", "\t xx ".trim() );
        Assert.assertEquals("x x", "\t x x ".trim() );
        Assert.assertEquals("x\tx", "\t x\tx ".trim() );

        Assert.assertEquals("xxx", "\n xxx \t \n".trim() );
        Assert.assertEquals("x  x  x", " \n\r\n x  x  x \t \n".trim() );
        Assert.assertEquals("a \tb  c", " \n\r\n a \tb  c \t \n".trim() );
    }
    
    @Test
    public void testFlatten() {
        String formattedContent = "Employee {\n" +
                "\tid : id {@test}; // the id\n" +
                "\tfirstName : string ;\n" +
                "\tbirthDate : date ;\n" +
                "}\n";
        
        String expectedResult = "Employee {id : id {@test};firstName : string ;birthDate : date ;}";
        
        //EntityParser parser = new EntityParser(formattedContent, new DomainModel("model"));
        String flattenContent = ParserUtil.preprocessText(formattedContent);

        Assert.assertEquals(expectedResult, flattenContent);
    }

    @Test
    public void testFlattenCR() {
        String formattedContent = "Employee {\r" +
                "\tid : id {@test}; // the id\r" +
                "\tfirstName : string ;\r" +
                "\tbirthDate : date ;\r" +
                "}\r";
        
        String expectedResult = "Employee {id : id {@test};firstName : string ;birthDate : date ;}";
        
        //EntityParser parser = new EntityParser(formattedContent, new DomainModel("model"));
        String flattenContent = ParserUtil.preprocessText(formattedContent);

        Assert.assertEquals(expectedResult, flattenContent);
    }

    @Test
    public void testFlattenCRandLF() {
        String formattedContent = "Employee {\r" +
                "\tid : id {@test}; // the id\n" +
                "\tfirstName : string ;\r" +
                "\tbirthDate : date ;\n" +
                "}\r";
        
        String expectedResult = "Employee {id : id {@test};firstName : string ;birthDate : date ;}";
        
        //EntityParser parser = new EntityParser(formattedContent, new DomainModel("model"));
        String flatContent = ParserUtil.preprocessText(formattedContent);

        Assert.assertEquals(expectedResult, flatContent);
        Assert.assertFalse(flatContent.contains("\t"));
    }

    @Test
    public void testFlattenCRLF() {
        String formattedContent = "Employee {\r\n" +
                "\tid : id {@test}; // the id\n\r" +
                "\tfirstName : string ;\r\n" +
                "\tbirthDate : date ;\n\r" +
                "}\r";
        
        String expectedResult = "Employee {id : id {@test};firstName : string ;birthDate : date ;}";
        
        //EntityParser parser = new EntityParser(formattedContent, new DomainModel("model"));
        String flattenContent = ParserUtil.preprocessText(formattedContent);

        Assert.assertEquals(expectedResult, flattenContent);
    }

    @Test
    public void testFlattenWithSpaces() {
        String formattedContent = "Employee {\n" +
                "\tid : id {@te st}; // the id\n" +
                "\tfirst Name : string ;\n" +
                "\tbirthDate : da te ;\n" +
                "}\n";
        String expectedResult = "Employee {id : id {@te st};first Name : string ;birthDate : da te ;}";

//        EntityParser parser = new EntityParser(formattedContent, new DomainModel("model"));
//        String flattenContent = parser.computeFlattenContent();
        String flattenContent = ParserUtil.preprocessText(formattedContent);

        Assert.assertEquals(expectedResult, flattenContent);
    }

    private final static String COMMENT_REGEXP = "(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)" ;
    
    @Test
    public void testCommentsRemoving1() {
    	String text = " aa \n bbb \n /* cccc \n ddd \n eee */ fff \n" ;
    	System.out.println("Initial text : \n" + text );
    	String filteredText = text.replaceAll(COMMENT_REGEXP,"");
    	System.out.println("Result : \n" + filteredText );
    }

    @Test
    public void testCommentsRemoving2() {
    	String text = " aa \n // bbb \n /* cccc \n ddd // DDD \n eee */ fff \n ggg // GGGG \n hhh" ;
    	System.out.println("Initial text : \n" + text );
    	String filteredText = text.replaceAll(COMMENT_REGEXP,"");
    	System.out.println("Result : \n" + filteredText );
    }

    @Test
    public void testCommentsRemoving3() {
    	String text = " aa \n // bbb \n cccc \n ddd // DDD \n /* eee */ fff \n ggg // GGGG \n hhh" ;
    	System.out.println("Initial text : \n" + text );
    	String filteredText = text.replaceAll(COMMENT_REGEXP,"");
    	System.out.println("Result : \n" + filteredText );
    }
    
    public void checkUnquote(char quote) {
        Assert.assertEquals("abc",      ParserUtil.unquote("abc",          quote) );
        Assert.assertEquals("abc",      ParserUtil.unquote("\"abc\"",      quote) );
        Assert.assertEquals("a b c",    ParserUtil.unquote("\"a b c\"",    quote) );
        Assert.assertEquals("a\"b 'c'", ParserUtil.unquote("\"a\"b 'c'\"", quote) );
    }
    
    @Test
    public void testUnquote1() {
    	char quote = '"';
        Assert.assertNull(ParserUtil.unquote(null, quote) );
        Assert.assertEquals("",         ParserUtil.unquote("",          quote) );
        Assert.assertEquals("abc",      ParserUtil.unquote("abc",          quote) );
        Assert.assertEquals("abc",      ParserUtil.unquote("\"abc\"",      quote) );
        Assert.assertEquals("a b c",    ParserUtil.unquote("\"a b c\"",    quote) );
        Assert.assertEquals("a \"b\" 'c'", ParserUtil.unquote("\"a \"b\" 'c'\"", quote) );
    }
    
    @Test
    public void testUnquote2() {
    	char quote = '\'';
        Assert.assertNull(ParserUtil.unquote(null, quote) );
        Assert.assertEquals("",         ParserUtil.unquote("",          quote) );
        Assert.assertEquals("abc",      ParserUtil.unquote("abc",          quote) );
        Assert.assertEquals("abc",      ParserUtil.unquote("'abc'",      quote) );
        Assert.assertEquals("a b c",    ParserUtil.unquote("'a b c'",    quote) );
        Assert.assertEquals("a \"b\" 'c'", ParserUtil.unquote("'a \"b\" 'c''", quote) );
    }
    
}
