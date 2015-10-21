package org.telosys.tools.dsl.parser;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.telosys.tools.dsl.EntityParserException;
import org.telosys.tools.dsl.parser.model.DomainEntityFieldAnnotation;

public class AnnotationParserTest {
	
	private final static String ENTITY_NAME = "EntityForTest" ;
	private final static String FIELD_NAME  = "testFieldName" ;

    @Test
    public void testSpit() throws Exception {    	
        Assert.assertEquals(1,  "".split(",").length);
        Assert.assertEquals("", "".split(",")[0]);
        
        Assert.assertEquals(0, ",".split(",").length);
        Assert.assertEquals(0, ",,".split(",").length);
        
        Assert.assertEquals(3,       "aa,, ccc ".split(",").length);
        Assert.assertEquals("aa",    "aa,, ccc ".split(",")[0]);
        Assert.assertEquals("",      "aa,, ccc ".split(",")[1]);
        Assert.assertEquals(" ccc ", "aa,, ccc ".split(",")[2]);
    }
    
	private String getAnnotationName(String s) {
        AnnotationParser annotationParser = new AnnotationParser();
        return annotationParser.getAnnotationName(s) ;
	}
	private String getParameterValue(String s, char c1, char c2) throws Exception {
        AnnotationParser annotationParser = new AnnotationParser();
        return annotationParser.getParameterValue(s, c1, c2 ) ;
	}
	private List<DomainEntityFieldAnnotation> parseAnnotations(String annotationsString) {
        AnnotationParser annotationParser = new AnnotationParser();
        return annotationParser.parseAnnotations(ENTITY_NAME, FIELD_NAME, annotationsString) ;
	}

	//-------------------------------------------------------------------------------------------
    @Test
    public void testGetAnnotationName() throws Exception {
        Assert.assertEquals("",   getAnnotationName(""));
        Assert.assertEquals("Id", getAnnotationName("@Id"));
        Assert.assertEquals("Id", getAnnotationName("@Id  aaa"));
        Assert.assertEquals("",   getAnnotationName("@ Id"));
        Assert.assertEquals("Id", getAnnotationName("@Id@Max(3)"));
        Assert.assertEquals("Max", getAnnotationName("@Max(12)"));
        Assert.assertEquals("Max", getAnnotationName("@Max)))"));
        Assert.assertEquals("Max", getAnnotationName("@Max\t(4)"));
    }
	//-------------------------------------------------------------------------------------------
    @Test
    public void testGetParamValue() throws Exception {
        Assert.assertEquals(null, getParameterValue("", '(', ')' ));
        Assert.assertEquals("aa", getParameterValue("(aa)", '(', ')' ));
        Assert.assertEquals("aa", getParameterValue(" (aa)  ", '(', ')' ));
        Assert.assertEquals("a a", getParameterValue(" (a a)  ", '(', ')' ));
    }
    @Test(expected = Exception.class)
    public void testGetParamValueInvalid1() throws Exception {
    	getParameterValue(" (a  ", '(', ')' );
    }
    @Test(expected = Exception.class)
    public void testGetParamValueInvalid2() throws Exception {
    	getParameterValue(" a)  ", '(', ')' );
    }
    @Test(expected = Exception.class)
    public void testGetParamValueInvalid3() throws Exception {
    	getParameterValue(" )a(  ", '(', ')' );
    }
    @Test(expected = Exception.class)
    public void testGetParamValueInvalid4() throws Exception {
    	getParameterValue(" a))  ", '(', ')' );
    }
    @Test(expected = Exception.class)
    public void testGetParamValueInvalid5() throws Exception {
    	getParameterValue(" (a(  ", '(', ')' );
    }

    //-------------------------------------------------------------------------------------------
    // Parsing valid annotations
    //-------------------------------------------------------------------------------------------
    @Test
    public void testParseAnnotationsValid() {
        List<DomainEntityFieldAnnotation> expectedAnnotations = new ArrayList<DomainEntityFieldAnnotation>();
        expectedAnnotations.add(new DomainEntityFieldAnnotation("Id"));
        Assert.assertEquals(expectedAnnotations, parseAnnotations("@Id") );
    }

    @Test
    public void testParseWithoutAnotation() {
        List<DomainEntityFieldAnnotation> expectedAnnotations = new ArrayList<DomainEntityFieldAnnotation>();
        Assert.assertEquals(expectedAnnotations, parseAnnotations("") );
    }

    @Test
    public void testParseWithNull() {
        List<DomainEntityFieldAnnotation> expectedAnnotations = new ArrayList<DomainEntityFieldAnnotation>();
        Assert.assertEquals(expectedAnnotations, parseAnnotations(null) );
    }

    @Test
    public void testParseAnnotationWithParam() {
        List<DomainEntityFieldAnnotation> expectedAnnotations = new ArrayList<DomainEntityFieldAnnotation>();
        expectedAnnotations.add(new DomainEntityFieldAnnotation("Max", "3"));

        Assert.assertEquals(expectedAnnotations, parseAnnotations("@Max(3)") );
    }

    @Test
    public void testParseMultipleAnnotations() {
        List<DomainEntityFieldAnnotation> expectedAnnotations = new ArrayList<DomainEntityFieldAnnotation>();
        expectedAnnotations.add(new DomainEntityFieldAnnotation("Id"));
        expectedAnnotations.add(new DomainEntityFieldAnnotation("Max", "3"));

        Assert.assertEquals(expectedAnnotations, parseAnnotations("@Id,@Max(3)") );
    }

    @Test
    public void testParseMultipleAnnotations2() {
        List<DomainEntityFieldAnnotation> expectedAnnotations = new ArrayList<DomainEntityFieldAnnotation>();
        expectedAnnotations.add(new DomainEntityFieldAnnotation("Id"));
        expectedAnnotations.add(new DomainEntityFieldAnnotation("Max", "3"));

        Assert.assertEquals(expectedAnnotations, parseAnnotations(" @Id ,  @Max ( 3 )  ") );
    }

    @Test
    public void testParseMultipleAnnotations3() {
        List<DomainEntityFieldAnnotation> expectedAnnotations = new ArrayList<DomainEntityFieldAnnotation>();
        expectedAnnotations.add(new DomainEntityFieldAnnotation("Id"));
        expectedAnnotations.add(new DomainEntityFieldAnnotation("Max", "3"));

        Assert.assertEquals(expectedAnnotations, parseAnnotations(" \t @Id , \t @Max ( 3 )  ") );
    }

    //-------------------------------------------------------------------------------------------
    // Parsing invalid annotations ( exception expected )
    //-------------------------------------------------------------------------------------------
    @Test(expected = EntityParserException.class)
    public void testParseUnknownAnnotation() {
        parseAnnotations("@FalseAnnotation");
    }

    @Test(expected = EntityParserException.class)
    public void testParseInvalidAnnotation() {
        parseAnnotations("Abcde");
    }

    @Test(expected = EntityParserException.class)
    public void testParseMultipleAnnotationsWithWrongSyntax() {
        parseAnnotations( "@Id;@Max(3)" );
    }

    @Test(expected = EntityParserException.class)
    public void testParseAnnotationWithMissingParameter() {
        parseAnnotations( "@Max" );
    }

    @Test(expected = EntityParserException.class)
    public void testParseAnnotationWithEmptyParameter() {
        parseAnnotations( "@Max()" );
    }

    @Test(expected = EntityParserException.class)
    public void testParseAnnotationWithBadParameter() {
        parseAnnotations( "@Max(aa)" );
    }

    @Test(expected = EntityParserException.class)
    public void testParseAnnotationWithUnexpectedParameter() {
        parseAnnotations( "@Id(12)" );
    }

    @Test(expected = EntityParserException.class)
    public void testParseParameteredAnnotationWithWrongSyntax() {
        parseAnnotations( "@Max(3" );
    }

    @Test(expected = EntityParserException.class)
    public void testParseAnnotationWithMissingStartParenthesis() {
        parseAnnotations( "@Id, @Max3)" );
    }

    @Test(expected = EntityParserException.class)
    public void testParseAnnotationWithMissingEndParenthesis() {
        parseAnnotations( "@Id, @Max(3" );
	}

    @Test(expected = EntityParserException.class)
    public void testParseAnnotationWithoutAt() {
        parseAnnotations( "#Id, @Max(3" );
    }
}
