package org.telosys.tools.dsl.parser;

import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.telosys.tools.dsl.DslParserException;
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
    public void testCheckFieldNameAndType()  {
		FieldParts field = new FieldParts(12, "", "");
		FieldNameAndTypeParser fieldParser = getParser();

		fieldParser.checkFieldNameAndType(field, "aa:string");
    	fieldParser.checkFieldNameAndType(field, "aa:string[2]");
    	fieldParser.checkFieldNameAndType(field, "aa:string[]");
    	fieldParser.checkFieldNameAndType(field, "aa  :  string ");
    	fieldParser.checkFieldNameAndType(field, "  aa :string[2] ");
    	fieldParser.checkFieldNameAndType(field, "aa : string[  ] ");
	}
	
	//----------------------------------------------------------------------
	// check field name
	//----------------------------------------------------------------------
	@Test
    public void testCheckFieldName()  {
		FieldParts field = new FieldParts(12, "", "");
		FieldNameAndTypeParser fieldParser = getParser();

		fieldParser.checkFieldName(field, "aaa");
		fieldParser.checkFieldName(field, "aaa$");
		fieldParser.checkFieldName(field, "aaa%");
	}

	@Test(expected=DslParserException.class)
	public void testCheckFieldNameWithError1()  {
		FieldParts field = new FieldParts(12, "", "");
		FieldNameAndTypeParser fieldParser = getParser();
		fieldParser.checkFieldName(field, "");
	}
	@Test(expected=DslParserException.class)
	public void testCheckFieldNameWithError2()  {
		FieldParts field = new FieldParts(12, "", "");
		FieldNameAndTypeParser fieldParser = getParser();
		fieldParser.checkFieldName(field, "   ");
	}
	@Test(expected=DslParserException.class)
	public void testCheckFieldNameWithError3()  {
		FieldParts field = new FieldParts(12, "", "");
		FieldNameAndTypeParser fieldParser = getParser();
		fieldParser.checkFieldName(field, "aa bb");
	}
	
	//----------------------------------------------------------------------
	// check field type
	//----------------------------------------------------------------------
	@Test
    public void testCheckFieldType()  {
		FieldParts field = new FieldParts(12, "", "");
		FieldNameAndTypeParser fieldParser = getParser();

		fieldParser.checkFieldTypeWithCardinality(field, "aaa");
		fieldParser.checkFieldTypeWithCardinality(field, "aaa$");
		fieldParser.checkFieldTypeWithCardinality(field, "aaa%");
		fieldParser.checkFieldTypeWithCardinality(field, "aaa[]");
		fieldParser.checkFieldTypeWithCardinality(field, "aaa []");
		fieldParser.checkFieldTypeWithCardinality(field, "aaa$[2]");
		fieldParser.checkFieldTypeWithCardinality(field, "aaa% ");
	}
	@Test(expected=DslParserException.class)
	public void testCheckFieldTypeError1()  {
		FieldParts field = new FieldParts(12, "", "");
		FieldNameAndTypeParser fieldParser = getParser();
		fieldParser.checkFieldTypeWithCardinality(field, "");
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
	@Test
    public void testGetFieldType()  {
		FieldParts field = new FieldParts(12, "", "");
		FieldNameAndTypeParser fieldParser = getParser();
		
    	Assert.assertEquals("string",     fieldParser.getFieldTypeWithoutCardinality(field, "aa:string"));
    	Assert.assertEquals("string",     fieldParser.getFieldTypeWithoutCardinality(field, "aa: string"));
    	Assert.assertEquals("string",     fieldParser.getFieldTypeWithoutCardinality(field, "  aa  :  string "));
    	Assert.assertEquals("str in g",   fieldParser.getFieldTypeWithoutCardinality(field, "aa : str in g "));
//    	Assert.assertEquals("string [ 2 ]",fieldParser.getFieldTypeWithoutCardinality(ENTITY_NAME, "aa:string [ 2 ] "));
//    	Assert.assertEquals("string []",fieldParser.getFieldTypeWithoutCardinality(ENTITY_NAME, "aa:string [] "));
    	Assert.assertEquals("string",     fieldParser.getFieldTypeWithoutCardinality(field, "aa:string [ 2 ] "));
    	Assert.assertEquals("string",     fieldParser.getFieldTypeWithoutCardinality(field, "aa:string [] "));
    	Assert.assertEquals("string",     fieldParser.getFieldTypeWithoutCardinality(field, "aa:string    "));
    	Assert.assertEquals("str # lqk",  fieldParser.getFieldTypeWithoutCardinality(field, "aa:str # lqk "));
    	Assert.assertEquals("str $ lqk",  fieldParser.getFieldTypeWithoutCardinality(field, "aa:str $ lqk [2] "));
    	Assert.assertEquals("string",     fieldParser.getFieldTypeWithoutCardinality(field, "aa:string[12] # lqksjd "));
    	Assert.assertEquals("string",     fieldParser.getFieldTypeWithoutCardinality(field, "aa:string[] $ lqksjd "));
    	Assert.assertEquals("string",     fieldParser.getFieldTypeWithoutCardinality(field, "aa:string[$] $ lqksjd "));
    	Assert.assertEquals("string",     fieldParser.getFieldTypeWithoutCardinality(field, "aa:string [] "));
    	Assert.assertEquals("string",     fieldParser.getFieldTypeWithoutCardinality(field, "aa:string  [ 123 ]  "));
    	Assert.assertEquals("string",     fieldParser.getFieldTypeWithoutCardinality(field, "aa:string\t  "));
    	Assert.assertEquals("string]5]",  fieldParser.getFieldTypeWithoutCardinality(field, "aa:string]5]  "));
    }
	
	//----------------------------------------------------------------------
	// get field type cardinality
	//----------------------------------------------------------------------
	@Test
    public void testGetCardinality() {
		FieldParts field = new FieldParts(12, "", "");
		FieldNameAndTypeParser fieldParser = getParser();
		
    	Assert.assertEquals( 1, fieldParser.getFieldTypeCardinality(field, "aa:string") );
    	Assert.assertEquals( 2, fieldParser.getFieldTypeCardinality(field, "aa:string [2]") );
    	Assert.assertEquals( 2, fieldParser.getFieldTypeCardinality(field, "aa:string [ 2 ]") );
    	Assert.assertEquals( 2, fieldParser.getFieldTypeCardinality(field, "aa:string \t [ \t 2 ]") );
    	Assert.assertEquals(-1, fieldParser.getFieldTypeCardinality(field, "aa:string []") );
    	Assert.assertEquals(20, fieldParser.getFieldTypeCardinality(field, "aa:Car [20]") );
	}

	//----------------------------------------------------------------------
	// test entry point 
	//----------------------------------------------------------------------
	@Test
    public void testParseFieldNameAndType() {
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
