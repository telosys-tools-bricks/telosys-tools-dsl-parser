package org.telosys.tools.dsl.parser;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.telosys.tools.dsl.EntityParserException;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.model.DomainEntityField;
import org.telosys.tools.dsl.parser.model.DomainEntityFieldAnnotation;
import org.telosys.tools.dsl.parser.model.DomainModel;
import org.telosys.tools.dsl.parser.model.DomainNeutralTypes;

public class FieldParserTest {
	
	private final static String ENTITY_NAME = "EntityForTest" ;
	
	private DomainModel getDomainModel(String... entityNames) {
		DomainModel model = new DomainModel("model");
		for ( String entityName : entityNames ) {
	        model.addEntity(new DomainEntity(entityName)); // eg "Country", "Book", ...
		}
		return model ;
	}
	private DomainEntityField parseField(String fieldDescription) {
        FieldParser fieldParser = new FieldParser(getDomainModel());
        return fieldParser.parseField(ENTITY_NAME, fieldDescription) ;
	}
	private void checkFieldSyntax(String fieldDescription) {
        FieldParser fieldParser = new FieldParser(getDomainModel());
        fieldParser.checkSyntax(ENTITY_NAME, fieldDescription) ;
	}
	
	@Test
    public void testCheckSyntax()  {
    	FieldParser fieldParser = new FieldParser(getDomainModel());
    	fieldParser.checkSyntax(ENTITY_NAME, "aa:string");
    	fieldParser.checkSyntax(ENTITY_NAME, "aa:string[2]");
    	fieldParser.checkSyntax(ENTITY_NAME, "aa:string[]");
    	fieldParser.checkSyntax(ENTITY_NAME, "aa:string { @Id }");
    	fieldParser.checkSyntax(ENTITY_NAME, "aa:string[2] { @Id }");
    	fieldParser.checkSyntax(ENTITY_NAME, "aa:string[] { @Id }");
	}
	
	@Test
    public void testGetFieldType() throws Exception {
    	FieldParser fieldParser = new FieldParser(getDomainModel());
    	Assert.assertEquals("string",fieldParser.getFieldType(ENTITY_NAME, "aa:string"));
    	Assert.assertEquals("string",fieldParser.getFieldType(ENTITY_NAME, "aa: string"));
    	Assert.assertEquals("string",fieldParser.getFieldType(ENTITY_NAME, "aa:string "));
    	Assert.assertEquals("str in g",fieldParser.getFieldType(ENTITY_NAME, "aa:str in g "));
//    	Assert.assertEquals("string [ 2 ]",fieldParser.getFieldType(ENTITY_NAME, "aa:string [ 2 ] "));
//    	Assert.assertEquals("string []",fieldParser.getFieldType(ENTITY_NAME, "aa:string [] "));
    	Assert.assertEquals("string",fieldParser.getFieldType(ENTITY_NAME, "aa:string [ 2 ] "));
    	Assert.assertEquals("string",fieldParser.getFieldType(ENTITY_NAME, "aa:string [] "));
    	Assert.assertEquals("string",fieldParser.getFieldType(ENTITY_NAME, "aa:string { @Id }"));
    	Assert.assertEquals("string # lqksjd",fieldParser.getFieldType(ENTITY_NAME, "aa:string # lqksjd "));
    	Assert.assertEquals("string $ lqksjd",fieldParser.getFieldType(ENTITY_NAME, "aa:string $ lqksjd "));
    	Assert.assertEquals("string",fieldParser.getFieldType(ENTITY_NAME, "aa:string[12] # lqksjd "));
    	Assert.assertEquals("string",fieldParser.getFieldType(ENTITY_NAME, "aa:string[] $ lqksjd "));
    	Assert.assertEquals("string",fieldParser.getFieldType(ENTITY_NAME, "aa:string[$] $ lqksjd "));
    	Assert.assertEquals("string",fieldParser.getFieldType(ENTITY_NAME, "aa:string [] { @Id }"));
    	Assert.assertEquals("string",fieldParser.getFieldType(ENTITY_NAME, "aa:string{ @Id }"));
    	Assert.assertEquals("string",fieldParser.getFieldType(ENTITY_NAME, "aa:string\t{ @Id }"));
    	Assert.assertEquals("string]5]",fieldParser.getFieldType(ENTITY_NAME, "aa:string]5] { @Id }"));
    }
	
	@Test
    public void testGetCardinality() throws Exception {
    	FieldParser fieldParser = new FieldParser(getDomainModel());
    	Assert.assertEquals(1,fieldParser.getCardinality(ENTITY_NAME, "aa:string") );
    	Assert.assertEquals(2,fieldParser.getCardinality(ENTITY_NAME, "aa:string [2]") );
    	Assert.assertEquals(2,fieldParser.getCardinality(ENTITY_NAME, "aa:string [ 2 ]") );
    	Assert.assertEquals(2,fieldParser.getCardinality(ENTITY_NAME, "aa:string \t [ \t 2 ]") );
    	Assert.assertEquals(-1,fieldParser.getCardinality(ENTITY_NAME, "aa:string []") );
	}

    @Test(expected = EntityParserException.class)
	public void testInvalidCardinality01() throws Exception {
    	FieldParser fieldParser = new FieldParser(getDomainModel());
    	fieldParser.getCardinality(ENTITY_NAME, "aa:string [-2]") ;
	}
    @Test(expected = EntityParserException.class)
	public void testInvalidCardinality02() throws Exception {
    	FieldParser fieldParser = new FieldParser(getDomainModel());
    	fieldParser.getCardinality(ENTITY_NAME, "aa:string [0]") ;
	}
    @Test(expected = EntityParserException.class)
	public void testInvalidCardinality03() throws Exception {
    	FieldParser fieldParser = new FieldParser(getDomainModel());
    	fieldParser.getCardinality(ENTITY_NAME, "aa:string [xx]") ;
	}
	
    @Test
    public void testParseFieldValid() throws Exception {
//        String fieldInfo = "id:integer";
//
//        FieldParser fieldParser = new FieldParser(getDomainModel());
//        DomainEntityField field = fieldParser.parseField(ENTITY_NAME, fieldInfo) ;
//        
//        Assert.assertEquals("id", field.getName());
//        Assert.assertEquals("integer", field.getTypeName());
//        Assert.assertEquals(DomainNeutralTypes.getType("integer"), field.getType());
//        Assert.assertEquals(1, field.getCardinality());
//        Assert.assertEquals(0, field.getAnnotationNames().size());
//
//        DomainEntityField expected = new DomainEntityField("id", DomainNeutralTypes.getType("integer"));
//        Assert.assertEquals(expected, field);
        checkResult1(parseField("id:integer"), 1);
        checkResult1(parseField("  id:integer"), 1);
        checkResult1(parseField("  id  :    integer"), 1);
        checkResult1(parseField("  id  :    integer  "), 1);
        checkResult1(parseField("id : integer"), 1);
        checkResult1(parseField("id : integer[1]"), 1);
        checkResult1(parseField("id : integer[2]"), 2);
        checkResult1(parseField("id : integer[45]"), 45);
        checkResult1(parseField("id : integer[]"), -1);
    }

    private void checkResult1(DomainEntityField field, int cardinality) {
        Assert.assertEquals("id", field.getName());
        Assert.assertEquals("integer", field.getTypeName());
        Assert.assertEquals(DomainNeutralTypes.getType("integer"), field.getType());
        Assert.assertEquals(cardinality, field.getCardinality());
        Assert.assertEquals(0, field.getAnnotationNames().size());

//        DomainEntityField expected = new DomainEntityField("id", DomainNeutralTypes.getType("integer"));
//        Assert.assertEquals(expected, field);
    }
    
    @Test()
    public void testParseFieldWithAnnotation() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        String fieldInfo = "id:integer{@Id}";
        
        DomainEntityField expectedField = new DomainEntityField("id", DomainNeutralTypes.getType("integer"));
        List<DomainEntityFieldAnnotation> annotationList = new ArrayList<DomainEntityFieldAnnotation>();
        annotationList.add(new DomainEntityFieldAnnotation("Id"));
        expectedField.setAnnotationList(annotationList);

        FieldParser fieldParser = new FieldParser(getDomainModel());

        //mock annotationParser
        AnnotationParser mockAnnotationParser = EasyMock.createMock(AnnotationParser.class);
        EasyMock.expect(mockAnnotationParser.parseAnnotations("EntityForTest", "id:integer{@Id}")).andReturn(annotationList);
        java.lang.reflect.Field field = fieldParser.getClass().getDeclaredField("annotationParser");
        field.setAccessible(true);
        field.set(fieldParser, mockAnnotationParser);
        EasyMock.replay(mockAnnotationParser);

        Assert.assertEquals(expectedField, fieldParser.parseField("EntityForTest", fieldInfo));
        EasyMock.verify(mockAnnotationParser);
    }

    @Test
    public void testParseFieldWithEntityReferenced() {
        String fieldInfo = "id:Country";

        DomainModel model = getDomainModel();
        DomainEntity country = new DomainEntity("Country");
        model.addEntity(country);

        DomainEntityField compareTo = new DomainEntityField("id", country);

        FieldParser fieldParser = new FieldParser(model);
        Assert.assertEquals(compareTo, fieldParser.parseField(ENTITY_NAME, fieldInfo));
    }

    @Test
    public void testParseFieldWithCardinalityN() throws Exception {
        String fieldInfo = "id:integer[]";

        DomainEntityField compareTo = new DomainEntityField("id", DomainNeutralTypes.getType("integer"), -1);

        FieldParser fieldParser = new FieldParser(getDomainModel());
        Assert.assertEquals(compareTo, fieldParser.parseField(ENTITY_NAME, fieldInfo));
    }

    @Test
    public void testParseFieldWithCardinality3() throws Exception {
        String fieldInfo = "id:integer[3]";

        DomainEntityField compareTo = new DomainEntityField("id", DomainNeutralTypes.getType("integer"), 3);

        FieldParser fieldParser = new FieldParser(getDomainModel());
        Assert.assertEquals(compareTo, fieldParser.parseField(ENTITY_NAME, fieldInfo));
    }

    @Test
    public void testParseFieldWithoutCardinality() throws Exception {
        String fieldInfo = "id:integer";

        DomainEntityField compareTo = new DomainEntityField("id", DomainNeutralTypes.getType("integer"), 1);

        FieldParser fieldParser = new FieldParser(getDomainModel());
        Assert.assertEquals(compareTo, fieldParser.parseField(ENTITY_NAME, fieldInfo));
    }

    //--------------------------------------------------------------------------
    // Invalid field syntax : EntityParserException expected
    //--------------------------------------------------------------------------
    @Test(expected = EntityParserException.class)
    public void testInvalidSyntaxColon01() {
    	checkFieldSyntax( "aa:string : zzz");
    }
    @Test(expected = EntityParserException.class)
    public void testInvalidSyntaxColon02() {
    	checkFieldSyntax( "aa string ");
    }    
    //--------------------------------------------------------------------------------------
    @Test(expected = EntityParserException.class)
    public void testInvalidSyntaxCardinality01() {
    	checkFieldSyntax( "aa:string [[");
    }
    @Test(expected = EntityParserException.class)
    public void testInvalidSyntaxCardinality02() {
    	checkFieldSyntax( "aa:string [3[]");
    }
    @Test(expected = EntityParserException.class)
    public void testInvalidSyntaxCardinality03() {
    	checkFieldSyntax( "aa:string [3]]");
    }
    @Test(expected = EntityParserException.class)
    public void testInvalidSyntaxCardinality04() {
    	checkFieldSyntax( "aa:string ]2[");
    }
    @Test(expected = EntityParserException.class)
    public void testInvalidSyntaxCardinality05() {
    	checkFieldSyntax( "aa : string [6 ");
    }
    @Test(expected = EntityParserException.class)
    public void testInvalidSyntaxCardinality06() {
    	checkFieldSyntax("aa : string 6]] ");
    }
    @Test(expected = EntityParserException.class)
    public void testInvalidSyntaxCardinality07() {
    	checkFieldSyntax( "aa : string 6] ");
    }
    @Test(expected = EntityParserException.class)
    public void testInvalidSyntaxCardinality08() {
    	checkFieldSyntax("aa : string [6 ");
    }

    @Test(expected = EntityParserException.class)
    public void testInvalidSyntaxAnnotations01() {
    	checkFieldSyntax("aa : string { @Id ");
    }
    @Test(expected = EntityParserException.class)
    public void testInvalidSyntaxAnnotations02() {
    	checkFieldSyntax("aa : string { { @Id ");
    }
    @Test(expected = EntityParserException.class)
    public void testInvalidSyntaxAnnotations03() {
    	checkFieldSyntax("aa : string @Id }");
    }
    @Test(expected = EntityParserException.class)
    public void testInvalidSyntaxAnnotations04() {
    	checkFieldSyntax("aa : string { @Id }}");
    }    
    @Test(expected = EntityParserException.class)
    public void testInvalidSyntaxAnnotations05() {
    	checkFieldSyntax("aa { : string @Id }");
    }    
    @Test(expected = EntityParserException.class)
    public void testInvalidSyntaxAnnotations06() {
    	checkFieldSyntax("aa {@Id} : string ");
    }    
    @Test(expected = EntityParserException.class)
    public void testInvalidSyntaxAnnotations07() {
    	checkFieldSyntax("aa }@Id{ : string ");
    }    
    //--------------------------------------------------------------------------------------
    @Test(expected = EntityParserException.class)
    public void testInvalidField_NoFieldDescription01() {
        parseField("");
    }
    @Test(expected = EntityParserException.class)
    public void testInvalidField_NoFieldDescription02() {
        parseField("    ");
    }
    @Test(expected = EntityParserException.class)
    public void testInvalidField_NoFieldDescription03() {
        parseField("  \t \t  ");
    }
    @Test(expected = EntityParserException.class)
    public void testInvalidField_FieldTypeMissing() {
        parseField("id:");
    }
    @Test(expected = EntityParserException.class)
    public void testInvalidField_FieldNameMissing() {
        parseField(" : integer");
    }
    
    //--- Bad cardinality
    @Test(expected = EntityParserException.class)
    public void testInvalidField_BadCardinality01() {
        parseField("code:integer[n]");
    }
    @Test(expected = EntityParserException.class)
    public void testInvalidField_BadCardinality02() {
        parseField("code : string[-2]");
    }
    @Test(expected = EntityParserException.class)
    public void testInvalidField_BadCardinality03() {
        parseField("code : string[0]");
    }
    //--- Bad type
    @Test(expected = EntityParserException.class)
    public void testInvalidField_BadType0() {
        parseField("code : badtype");
    }
    @Test(expected = EntityParserException.class)
    public void testInvalidField_BadType1() {
        parseField("country :  Country");
    }
    @Test(expected = EntityParserException.class)
    public void testInvalidField_BadType2() {
        parseField("country : #Country");
    }
    @Test(expected = EntityParserException.class)
    public void testInvalidField_BadType3() {
        parseField("country : $Country");
    }
    //--- Bad syntax
    @Test(expected = EntityParserException.class)
    public void testInvalidField_BadSyntax01() {
        parseField("id");
    }
    @Test(expected = EntityParserException.class)
    public void testInvalidField_BadSyntax02() {
        parseField("name = string");
    }
    @Test(expected = EntityParserException.class)
    public void testInvalidField_BadSyntax03() {
        parseField("namestring");
    }
    @Test(expected = EntityParserException.class)
    public void testInvalidField_BadSyntax04() {
        parseField("idinteger");
    }
}
