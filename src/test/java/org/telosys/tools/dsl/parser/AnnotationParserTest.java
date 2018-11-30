package org.telosys.tools.dsl.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.telosys.tools.dsl.DslParserException;
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
    
	//-------------------------------------------------------------------------------------------
	private String getAnnotationName(String s) throws Exception {
        AnnotationParser annotationParser = new AnnotationParser();
        System.out.println("getAnnotationName('" + s + "')");
        return annotationParser.getAnnotationName(s) ;
	}
	private String getParameterValue(String s, char c1, char c2) throws Exception {
        AnnotationParser annotationParser = new AnnotationParser();
        System.out.println("getParameterValue('" + s + "')");
        return annotationParser.getParameterValue(s, c1, c2 ) ;
	}
	private Number getParameterValueAsInteger(String value) throws Exception {
        AnnotationParser annotationParser = new AnnotationParser();
        System.out.println("getParameterValueAsInteger('" + value + "')");
        Number r = annotationParser.getParameterValueAsInteger(value) ;
        return r ;
	}
	private Number getParameterValueAsBigDecimal(String value) throws Exception {
        AnnotationParser annotationParser = new AnnotationParser();
        System.out.println("getParameterValueAsBigDecimal('" + value + "')");
        Number r = annotationParser.getParameterValueAsBigDecimal(value) ;
        return r ;
	}
	private List<DomainEntityFieldAnnotation> parseAnnotations(String annotationsString) {
        AnnotationParser annotationParser = new AnnotationParser();
        return annotationParser.parseAnnotations(ENTITY_NAME, FIELD_NAME, annotationsString) ;
	}

	//-------------------------------------------------------------------------------------------
    @Test
    public void testGetAnnotationName() throws Exception {
        Assert.assertEquals("",    getAnnotationName(""));
        Assert.assertEquals("Id",  getAnnotationName("@Id"));
        Assert.assertEquals("Max", getAnnotationName("@Max(12)"));
        Assert.assertEquals("Max", getAnnotationName("@Max\t(4)"));
        Assert.assertEquals("Foo", getAnnotationName("@Foo(4)"));
        Assert.assertEquals("Min", getAnnotationName("@Min\r \n (4)"));
    }

    //-------------------------------------------------------------------------------------------
    @Test (expected=Exception.class)
    public void testGetAnnotationNameWithError1() throws Exception {
        getAnnotationName("@Id  aaa") ;
    }
    @Test (expected=Exception.class)
    public void testGetAnnotationNameWithError2() throws Exception {
        getAnnotationName("@ Id");
    }
    @Test (expected=Exception.class)
    public void testGetAnnotationNameWithError3() throws Exception {

        getAnnotationName("@Id@Max(3)");
    }
    @Test (expected=Exception.class)
    public void testGetAnnotationNameWithError4() throws Exception {
        getAnnotationName("@Max)))" );
    }
    @Test (expected=Exception.class)
    public void testGetAnnotationNameWithError5() throws Exception {
        getAnnotationName("@Foo#" );
    }
    @Test (expected=Exception.class)
    public void testGetAnnotationNameWithError6() throws Exception {
        getAnnotationName("@Foo!" );
    }
    @Test (expected=Exception.class)
    public void testGetAnnotationNameWithError7() throws Exception {
        getAnnotationName("@Max8)" );
    }
    @Test (expected=Exception.class)
    public void testGetAnnotationNameWithError8() throws Exception {
        getAnnotationName("@Max 5  )" );
    } 
	//-------------------------------------------------------------------------------------------
    @Test
    public void testGetParamValue() throws Exception {
        Assert.assertEquals(null, getParameterValue("", '(', ')' ));
        Assert.assertEquals(null, getParameterValue("@Foo", '(', ')' ));
        Assert.assertEquals(null, getParameterValue(" @Foo  ", '(', ')' ));
        Assert.assertEquals("aa", getParameterValue("@Foo(aa)", '(', ')' ));
        Assert.assertEquals("aa", getParameterValue("@Foo  (aa)  ", '(', ')' ));
        Assert.assertEquals("a a", getParameterValue("  @Foo  (a a)  ", '(', ')' ));
        Assert.assertEquals("aa", getParameterValue("@Foo  (  aa  )  ", '(', ')' ));
    }
    //-------------------------------------------------------------------------------------------
    @Test (expected=Exception.class)
    public void testGetParamValueWithError1() throws Exception {
    	getParameterValue(" @Foo (a  ", '(', ')' );
    }
    @Test (expected=Exception.class)
    public void testGetParamValueWithError2() throws Exception {
    	getParameterValue(" @Foo a)  ", '(', ')' );
    }
    @Test (expected=Exception.class)
    public void testGetParamValueWithError3() throws Exception {
    	getParameterValue(" @Foo )a(  ", '(', ')' );
    }
    @Test (expected=Exception.class)
    public void testGetParamValueWithError4() throws Exception {
    	getParameterValue(" @Foo a))  ", '(', ')' );
    }
    @Test(expected = Exception.class)
    public void testGetParamValueWithError5() throws Exception {
    	getParameterValue(" @Bar(a(  ", '(', ')' );
    }
	//-------------------------------------------------------------------------------------------
    @Test
    public void checkParameterValueAsInteger() throws Exception {
    	// Valid integer values
    	assertTrue(getParameterValueAsInteger("12" ) instanceof Integer ) ;
    	assertTrue(getParameterValueAsInteger("123") instanceof Integer ) ;
    	
//    	// Valid decimal values    	
//    	assertTrue(getParameterValueAsNumber("12.34") instanceof BigDecimal ) ;
//    	assertTrue(getParameterValueAsNumber(".34") instanceof BigDecimal ) ;
//    	assertTrue(getParameterValueAsNumber("123.") instanceof BigDecimal ) ;
    }
	//-------------------------------------------------------------------------------------------
    @Test
    public void checkParameterValueDecimal() throws Exception {
    	// Valid integer values
    	assertTrue(getParameterValueAsBigDecimal("12" ) instanceof BigDecimal ) ;
    	assertTrue(getParameterValueAsBigDecimal("123") instanceof BigDecimal ) ;
    	
    	// Valid decimal values    	
    	assertTrue(getParameterValueAsBigDecimal("12.34") instanceof BigDecimal ) ;
    	assertTrue(getParameterValueAsBigDecimal(".34") instanceof BigDecimal ) ;
    	assertTrue(getParameterValueAsBigDecimal("123.") instanceof BigDecimal ) ;
    }
    @Test(expected = Exception.class)
    public void checkParameterValueDecimalWithError1() throws Exception {
    	getParameterValueAsBigDecimal("");
    }
    @Test(expected = Exception.class)
    public void checkParameterValueDecimalWithError2() throws Exception {
    	getParameterValueAsBigDecimal("   ");
    }
    @Test(expected = Exception.class)
    public void checkParameterValueDecimalWithError3() throws Exception {
    	getParameterValueAsBigDecimal("  123");
    }
    @Test(expected = Exception.class)
    public void checkParameterValueDecimalWithError4() throws Exception {
    	getParameterValueAsBigDecimal("123   ");
    }
    @Test(expected = Exception.class)
    public void checkParameterValueDecimalWithError5() throws Exception {
    	getParameterValueAsBigDecimal("12.3.4");
    }

    //-------------------------------------------------------------------------------------------
    // Parsing valid annotations
    //-------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------
    
    private void checkExpectedResultVoid(List<DomainEntityFieldAnnotation> annotations ) {
    	assertEquals(0, annotations.size());
        //DomainEntityFieldAnnotation annotation =  annotations.get(0);
    }
    
//    @Test
//    public void testParseAnnotationsValid() {
//        List<DomainEntityFieldAnnotation> expectedAnnotations = new ArrayList<DomainEntityFieldAnnotation>();
//        expectedAnnotations.add(new DomainEntityFieldAnnotation("Id"));
//        Assert.assertEquals(expectedAnnotations, parseAnnotations("@Id") );
//    }

    @Test
    public void testParseWithoutAnotation() {
//        List<DomainEntityFieldAnnotation> expectedAnnotations = new ArrayList<DomainEntityFieldAnnotation>();
//        Assert.assertEquals(expectedAnnotations, parseAnnotations("") );
    	checkExpectedResultVoid(parseAnnotations("") );
    }

    @Test
    public void testParseWithNull() {
//        List<DomainEntityFieldAnnotation> expectedAnnotations = new ArrayList<DomainEntityFieldAnnotation>();
//        Assert.assertEquals(expectedAnnotations, parseAnnotations(null) );
    	checkExpectedResultVoid(parseAnnotations(null) );
    }

//    @Test
//    public void testParseAnnotationWithParam() {
//        List<DomainEntityFieldAnnotation> expectedAnnotations = new ArrayList<DomainEntityFieldAnnotation>();
//        expectedAnnotations.add(new DomainEntityFieldAnnotation("Max", "3"));
//
//        Assert.assertEquals(expectedAnnotations, parseAnnotations("@Max(3)") );
//    }

    //-------------------------------------------------------------------------------------
    
    private void checkExpectedResultIdMinMax(List<DomainEntityFieldAnnotation> annotations ) {
    	
    	assertEquals(3, annotations.size());
    	
        DomainEntityFieldAnnotation annotation =  annotations.get(0);
        assertEquals("Id", annotation.getName() );
        assertEquals(null,   annotation.getParameter() );

        annotation =  annotations.get(1);
        assertEquals("Min", annotation.getName() );
        assertEquals(new BigDecimal("3"),   annotation.getParameterAsBigDecimal() );
        
        annotation =  annotations.get(2);
        assertEquals("Max", annotation.getName() );
        assertEquals(new BigDecimal("50"),   annotation.getParameterAsBigDecimal() );
    }
    
    @Test
    public void testParseMultipleAnnotations1() {
    	checkExpectedResultIdMinMax(parseAnnotations("@Id,@Min(3),@Max(50)") );
    }

    @Test
    public void testParseMultipleAnnotations2() {
    	checkExpectedResultIdMinMax(parseAnnotations(" @Id,   @Min  (  3 )   ,@Max(50)") );
    }

    @Test
    public void testParseMultipleAnnotations3() {
    	checkExpectedResultIdMinMax(parseAnnotations(" \t @Id , \t \n @Min  (  3 ), \t \r\n @Max ( 50 )  ") );
    }

    //-------------------------------------------------------------------------------------------
    // Parsing invalid annotations ( exception expected )
    //-------------------------------------------------------------------------------------------
    @Test(expected = DslParserException.class)
    public void testParseUnknownAnnotation() {
        parseAnnotations("@BadAnnotation");
    }

    @Test(expected = DslParserException.class)
    public void testParseInvalidAnnotation() {
        parseAnnotations("Abcde");
    }

    @Test(expected = DslParserException.class)
    public void testParseMultipleAnnotationsWithWrongSyntax() {
        parseAnnotations( "@Id;@Max(3)" );
    }

    @Test(expected = DslParserException.class)
    public void testParseMultipleAnnotationsWithWrongSyntax2() {
        parseAnnotations( "@Id @Max(3)" );
    }

    @Test(expected = DslParserException.class)
    public void testParseAnnotationWithMissingParameter() {
        parseAnnotations( "@Max" );
    }

    @Test(expected = DslParserException.class)
    public void testParseAnnotationWithEmptyParameter() {
        parseAnnotations( "@Max()" );
    }

    @Test(expected = DslParserException.class)
    public void testParseAnnotationWithBadParameter() {
        parseAnnotations( "@Max(aa)" );
    }

    @Test(expected = DslParserException.class)
    public void testParseAnnotationWithUnexpectedParameter() {
        parseAnnotations( "@Id(12)" );
    }

    @Test(expected = DslParserException.class)
    public void testParseParameteredAnnotationWithWrongSyntax() {
        parseAnnotations( "@Max(3" );
    }

    @Test(expected = DslParserException.class)
    public void testParseAnnotationWithMissingStartParenthesis() {
        parseAnnotations( "@Id, @Max3)" );
    }

    @Test(expected = DslParserException.class)
    public void testParseAnnotationWithMissingEndParenthesis() {
        parseAnnotations( "@Id, @Max(3" );
	}

    @Test(expected = DslParserException.class)
    public void testParseAnnotationWithoutAt() {
        parseAnnotations( "#Id, @Max(3" );
    }
}
