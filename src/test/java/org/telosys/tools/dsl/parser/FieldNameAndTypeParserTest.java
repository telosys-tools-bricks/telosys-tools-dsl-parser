package org.telosys.tools.dsl.parser;

import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.telosys.tools.dsl.parser.exceptions.FieldParsingError;
import org.telosys.tools.dsl.parser.model.DomainTypeNature;

public class FieldNameAndTypeParserTest {

	private final static String ENTITY_NAME = "EntityForTest" ;

	public FieldNameAndTypeParserTest() {
	}
	
    private List<String> getEntitiesNames() {
    	List<String> list = new LinkedList<>();
    	list.add("Car");  
    	list.add("Driver");  
    	return list;
    }

    private FieldNameAndTypeParser getParser() {
        return new FieldNameAndTypeParser(ENTITY_NAME, getEntitiesNames());
    }
    
	@Test
    public void testCheckFieldNameAndType() throws FieldParsingError {
		FieldNameAndTypeParser fieldParser = getParser();

		fieldParser.checkFieldNameAndType("aa:string");
    	fieldParser.checkFieldNameAndType("aa:string[2]");
    	fieldParser.checkFieldNameAndType("aa:string[]");
    	fieldParser.checkFieldNameAndType("aa  :  string ");
    	fieldParser.checkFieldNameAndType("  aa :string[2] ");
    	fieldParser.checkFieldNameAndType("aa : string[  ] ");
	}
	
	//----------------------------------------------------------------------
	// check field name
	//----------------------------------------------------------------------
	@Test
    public void testCheckFieldName() throws FieldParsingError {
		FieldNameAndTypeParser fieldParser = getParser();

		fieldParser.checkFieldName("", "aaa");
		fieldParser.checkFieldName("", "aaa$");
		fieldParser.checkFieldName("", "aaa%");
	}

	@Test(expected=FieldParsingError.class)
	public void testCheckFieldNameWithError1() throws FieldParsingError {
		FieldNameAndTypeParser fieldParser = getParser();
		fieldParser.checkFieldName("", "");
	}
	@Test(expected=FieldParsingError.class)
	public void testCheckFieldNameWithError2() throws FieldParsingError {
		FieldNameAndTypeParser fieldParser = getParser();
		fieldParser.checkFieldName("", "   ");
	}
	@Test(expected=FieldParsingError.class)
	public void testCheckFieldNameWithError3() throws FieldParsingError {
		FieldNameAndTypeParser fieldParser = getParser();
		fieldParser.checkFieldName("", "aa bb");
	}
	
	//----------------------------------------------------------------------
	// check field type
	//----------------------------------------------------------------------
	@Test
    public void testCheckFieldType() throws FieldParsingError {
		FieldNameAndTypeParser fieldParser = getParser();

		fieldParser.checkFieldTypeWithCardinality("", "aaa");
		fieldParser.checkFieldTypeWithCardinality("", "aaa$");
		fieldParser.checkFieldTypeWithCardinality("", "aaa%");
		fieldParser.checkFieldTypeWithCardinality("", "aaa[]");
		fieldParser.checkFieldTypeWithCardinality("", "aaa []");
		fieldParser.checkFieldTypeWithCardinality("", "aaa$[2]");
		fieldParser.checkFieldTypeWithCardinality("", "aaa% ");
	}
	@Test(expected=FieldParsingError.class) 
	public void testCheckFieldTypeError1() throws FieldParsingError  {
		FieldNameAndTypeParser fieldParser = getParser();
		fieldParser.checkFieldTypeWithCardinality("", "");
	}
//	@Test(expected=DslParserException.class)
//	public void testCheckFieldTypeError2()  {
//		Field field = new Field(12, "", "");
//		FieldNameAndTypeParser fieldParser = getParser();
//		fieldParser.checkFieldTypeWithCardinality(field, "   ");
//	}
//	@Test(expected=DslParserException.class)
//	public void testCheckFieldTypeError3()  {
//		Field field = new Field(12, "", "");
//		FieldNameAndTypeParser fieldParser = getParser();
//		fieldParser.checkFieldTypeWithCardinality(field, "aa bb");
//	}

	//----------------------------------------------------------------------
	// get field type
	//----------------------------------------------------------------------
	private String getFieldTypeWithoutCardinality(String fieldTypeWithCardinality) throws FieldParsingError {
		FieldNameAndTypeParser fieldParser = getParser();
		String s = "myField : " + fieldTypeWithCardinality ;
		return fieldParser.getFieldTypeWithoutCardinality(s, fieldTypeWithCardinality);
	}
	
	@Test
    public void testGetFieldTypeWithoutCardinality() throws FieldParsingError {
    	Assert.assertEquals("string",     getFieldTypeWithoutCardinality("aa:string"));
    	Assert.assertEquals("string",     getFieldTypeWithoutCardinality("aa: string"));
    	Assert.assertEquals("string",     getFieldTypeWithoutCardinality("  aa  :  string "));
    	Assert.assertEquals("str in g",   getFieldTypeWithoutCardinality("aa : str in g "));
    	Assert.assertEquals("string",     getFieldTypeWithoutCardinality("aa:string [ 2 ] "));
    	Assert.assertEquals("string",     getFieldTypeWithoutCardinality("aa:string [] "));
    	Assert.assertEquals("string",     getFieldTypeWithoutCardinality("aa:string    "));
    	Assert.assertEquals("str # lqk",  getFieldTypeWithoutCardinality("aa:str # lqk "));
    	Assert.assertEquals("str $ lqk",  getFieldTypeWithoutCardinality("aa:str $ lqk [2] "));
    	Assert.assertEquals("string",     getFieldTypeWithoutCardinality("aa:string[12] # lqksjd "));
    	Assert.assertEquals("string",     getFieldTypeWithoutCardinality("aa:string[] $ lqksjd "));
    	Assert.assertEquals("string",     getFieldTypeWithoutCardinality("aa:string[$] $ lqksjd "));
    	Assert.assertEquals("string",     getFieldTypeWithoutCardinality("aa:string [] "));
    	Assert.assertEquals("string",     getFieldTypeWithoutCardinality("aa:string  [ 123 ]  "));
    	Assert.assertEquals("string",     getFieldTypeWithoutCardinality("aa:string\t  "));
    	Assert.assertEquals("string]5]",  getFieldTypeWithoutCardinality("aa:string]5]  "));
    }
	
	//----------------------------------------------------------------------
	// get field type cardinality
	//----------------------------------------------------------------------
	private int parseFieldTypeCardinality(String fieldTypeWithCardinality) throws FieldParsingError {
		String fieldNameAndType = "myField : " + fieldTypeWithCardinality ;
		FieldNameAndTypeParser fieldParser = getParser();
		return fieldParser.getFieldTypeCardinality(fieldNameAndType, fieldTypeWithCardinality);
	}

	@Test
    public void testGetCardinality() throws FieldParsingError {
    	Assert.assertEquals( 1, parseFieldTypeCardinality("string") );
    	Assert.assertEquals( 2, parseFieldTypeCardinality("string [2]") );
    	Assert.assertEquals( 2, parseFieldTypeCardinality("string [ 2 ]") );
    	Assert.assertEquals( 2, parseFieldTypeCardinality("string \t [ \t 2 ]") );
    	Assert.assertEquals(-1, parseFieldTypeCardinality("string []") );
    	Assert.assertEquals(20, parseFieldTypeCardinality("Car [20]") );
	}

	//----------------------------------------------------------------------
	// test entry point 
	//----------------------------------------------------------------------
	@Test
    public void testParseFieldNameAndType() throws FieldParsingError {
		FieldNameAndTypeParser fieldParser = getParser();
		
		FieldNameAndType r ;
		r = fieldParser.parseFieldNameAndType( new FieldParts(12, "aa:string [2]", ""));
		Assert.assertEquals( "aa", r.getName() );
		Assert.assertEquals( "string", r.getType() );
		Assert.assertEquals( 2, r.getCardinality() );
		Assert.assertEquals( "string", r.getDomainType().getName() );
		Assert.assertEquals( DomainTypeNature.NEUTRAL_TYPE, r.getDomainType().getNature() );

		r = fieldParser.parseFieldNameAndType( new FieldParts(12, "  foo : long ", ""));
		Assert.assertEquals( "foo", r.getName() );
		Assert.assertEquals( "long", r.getType() );
		Assert.assertEquals( 1, r.getCardinality() );
		Assert.assertEquals( "long", r.getDomainType().getName() );
		Assert.assertEquals( DomainTypeNature.NEUTRAL_TYPE, r.getDomainType().getNature() );

		r = fieldParser.parseFieldNameAndType( new FieldParts(12, "  bar : int [  ] ", ""));
		Assert.assertEquals( "bar", r.getName() );
		Assert.assertEquals( "int", r.getType() );
		Assert.assertEquals( -1, r.getCardinality() );
		Assert.assertEquals( "int", r.getDomainType().getName() );
		Assert.assertEquals( DomainTypeNature.NEUTRAL_TYPE, r.getDomainType().getNature() );
	}

}
