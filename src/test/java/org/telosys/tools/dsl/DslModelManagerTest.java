package org.telosys.tools.dsl;

import java.io.File;

import org.junit.Test;
import org.telosys.tools.dsl.parser.Parser;
import org.telosys.tools.dsl.parser.exceptions.EntityParsingError;
import org.telosys.tools.dsl.parser.exceptions.FieldParsingError;
import org.telosys.tools.dsl.parser.exceptions.ModelParsingError;
import org.telosys.tools.dsl.parser.model.DomainModel;
import org.telosys.tools.generic.model.Attribute;
import org.telosys.tools.generic.model.Entity;
import org.telosys.tools.generic.model.Model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DslModelManagerTest {
    
	private Model loadValidModel(String modelFileName) {
		System.out.println("----- " );
		System.out.println("Loading valid model : " + modelFileName );
        DslModelManager dslModelManager = new DslModelManager();
        Model model = dslModelManager.loadModel(modelFileName);
		printErrors(dslModelManager);
		if ( model == null ) {
			System.out.println("ERROR : cannot load model");
			System.out.println(dslModelManager.getErrorMessage());
//			for ( Map.Entry<String, String> entry : modelLoader.getParsingErrors().entrySet() ) {
//				System.out.println(" . " + entry.getKey() + " : " + entry.getValue() );
//			}
			throw new RuntimeException("Cannot load model : " + dslModelManager.getErrorMessage());
		}
		// No error expected
		assertEquals(0, dslModelManager.getErrorMessage().length() );
		assertEquals(0, dslModelManager.getErrors().getAllErrorsCount() );
		return model ;
    }
    
    @Test
    public void test_ValidModel_OneEntity_Model() {
        Model model = loadValidModel("src/test/resources/model_test/valid/OneEntity.model");
        
        assertNotNull(model);
        
        assertEquals(1, model.getEntities().size() );
        
        assertEquals("Employee", model.getEntities().get(0).getClassName() );
        
        Entity employeeEntity = model.getEntityByClassName("Employee");
        assertNotNull(employeeEntity);
        
        assertEquals(3, employeeEntity.getAttributes().size());    
        int i = 0 ;
        Attribute attrib = null ;
        
        // Attributes in their original order :
        
        attrib = employeeEntity.getAttributes().get(i++);
        assertEquals("id", attrib.getName() ) ;
		assertEquals("int", attrib.getNeutralType() ); 
//        assertEquals("Integer", attrib.getSimpleType() ) ;
//        assertEquals("java.lang.Integer", attrib.getFullType() ) ;
        assertTrue(attrib.isKeyElement());
        assertTrue(attrib.isNotNull()); // If "@Id" => "@NotNull"
        assertTrue(attrib.isDatabaseNotNull() ); // If "@Id" => "@NotNull"
        
        attrib = employeeEntity.getAttributes().get(i++);
        assertEquals("firstName", attrib.getName() ) ;
		assertEquals("string", attrib.getNeutralType() ); 
//        assertEquals("String", attrib.getSimpleType() ) ;
//        assertEquals("java.lang.String", attrib.getFullType() ) ;
        assertFalse(attrib.isKeyElement());
        assertFalse(attrib.isNotNull());
        assertFalse(attrib.isDatabaseNotNull());

        attrib = employeeEntity.getAttributes().get(i++);
        assertEquals("birthDate", attrib.getName() ) ;
		assertEquals("date", attrib.getNeutralType() ); 
//        assertEquals("Date", attrib.getSimpleType() ) ;
//        assertEquals("java.util.Date", attrib.getFullType() ) ;
        assertFalse(attrib.isKeyElement());
        assertFalse(attrib.isNotNull());
        assertFalse(attrib.isDatabaseNotNull());

    }

    @Test
    public void test_ValidModel_Types_Model()  {
        Model model = loadValidModel("src/test/resources/model_test/valid/types.model");
        
        assertNotNull(model);
        assertEquals(1, model.getEntities().size() );
        
        Entity entity = model.getEntityByClassName("Person");
        assertNotNull(entity);
        System.out.println("Entity 'Person' found");
        assertEquals("Person", entity.getClassName() );
        assertEquals("Person", entity.getFullName() ); 
        
        for ( Attribute attribute : entity.getAttributes() ) {
        	System.out.println(" attribute " + attribute.getName() );
    		System.out.println("  " + attribute.getNeutralType() + " --> " + attribute.getNeutralType());
        	if ( attribute.getName().equals("id") ) {
//        		assertEquals("int", attribute.getSimpleType() ); 
//        		assertEquals("int", attribute.getFullType() ); 
        		assertEquals("int", attribute.getNeutralType() ); 
        		assertTrue(attribute.isKeyElement());
        		assertFalse(attribute.isAutoIncremented());
        		
        		assertFalse(attribute.isPrimitiveTypeExpected());
        		assertFalse(attribute.isUnsignedTypeExpected());
        		assertFalse(attribute.isObjectTypeExpected());
        		assertFalse(attribute.isSqlTypeExpected());
        	}
        	else if ( attribute.getName().equals("firstName") ) {
        		System.out.println(attribute.getNeutralType() + " --> " + attribute.getNeutralType());
        		assertTrue(attribute.isNotEmpty());
        		assertFalse(attribute.isKeyElement());
        		assertFalse(attribute.isAutoIncremented());
        	}
        	else if ( attribute.getName().equals("lastName") ) {
        		System.out.println(attribute.getNeutralType() + " --> " + attribute.getNeutralType());
        		assertTrue(attribute.isNotBlank());
        		assertFalse(attribute.isKeyElement());
        		assertFalse(attribute.isAutoIncremented());
        	}
        	else if ( attribute.getName().equals("counter") ) {
        		System.out.println(attribute.getNeutralType() + " --> " + attribute.getNeutralType());
        		assertEquals("short", attribute.getNeutralType()); 
        		
        		assertTrue(attribute.isAutoIncremented());

//        		assertEquals("Short", attribute.getSimpleType() ); 
//        		assertEquals("java.lang.Short", attribute.getFullType() ); 
        	}
        	else if ( attribute.getName().equals("counter2") ) {
        		System.out.println(attribute.getNeutralType() + " --> " + attribute.getNeutralType());
        		assertEquals("short", attribute.getNeutralType()); 

        		assertTrue(attribute.isAutoIncremented());
        		
//        		assertEquals("short", attribute.getSimpleType() ); 
//        		assertEquals("short", attribute.getFullType() ); 
        	}
        	else if ( attribute.getName().equals("counter3") ) {
        		System.out.println(attribute.getNeutralType() + " --> " + attribute.getNeutralType());
        		assertEquals("short", attribute.getNeutralType()); 

        		assertTrue(attribute.isPrimitiveTypeExpected());
        		assertFalse(attribute.isUnsignedTypeExpected());
        		assertFalse(attribute.isObjectTypeExpected());
        		assertFalse(attribute.isSqlTypeExpected());

//        		assertEquals("short", attribute.getSimpleType() ); 
//        		assertEquals("short", attribute.getFullType() ); 
        	}
        	else if ( attribute.getName().equals("counter4") ) {
        		System.out.println(attribute.getNeutralType() + " --> " + attribute.getNeutralType());
        		assertEquals("short", attribute.getNeutralType()); 

        		assertFalse(attribute.isNotNull());
        		assertFalse(attribute.isPrimitiveTypeExpected());
        		assertFalse(attribute.isUnsignedTypeExpected());
        		assertTrue(attribute.isObjectTypeExpected());
        		assertFalse(attribute.isSqlTypeExpected());

//        		assertEquals("Short", attribute.getSimpleType() ); 
//        		assertEquals("java.lang.Short", attribute.getFullType() ); 

        	}
        	else if ( attribute.getName().equals("counter5") ) {
        		System.out.println(attribute.getNeutralType() + " --> " + attribute.getNeutralType());
        		assertEquals("short", attribute.getNeutralType()); 

        		assertFalse(attribute.isNotNull());
        		assertFalse(attribute.isPrimitiveTypeExpected());
        		assertFalse(attribute.isUnsignedTypeExpected());
        		//assertTrue(attribute.isObjectTypeExpected());
        		assertTrue(attribute.isSqlTypeExpected());

//        		assertEquals("Short", attribute.getSimpleType() ); 
//        		assertEquals("java.lang.Short", attribute.getFullType() ); 

        	}
        	else if ( attribute.getName().equals("counter6") ) {
        		System.out.println(attribute.getNeutralType() + " --> " + attribute.getNeutralType());
        		assertEquals("short", attribute.getNeutralType()); 

        		assertTrue(attribute.isNotNull());
        		assertTrue(attribute.isDatabaseNotNull());
        		assertFalse(attribute.isPrimitiveTypeExpected());
        		assertFalse(attribute.isUnsignedTypeExpected());
        		assertTrue(attribute.isObjectTypeExpected());
        		assertFalse(attribute.isSqlTypeExpected());

//        		assertEquals("Short", attribute.getSimpleType() ); 
//        		assertEquals("java.lang.Short", attribute.getFullType() ); 

        	}
        	else if ( attribute.getName().equals("counter7") ) {
        		System.out.println(attribute.getNeutralType() + " --> " + attribute.getNeutralType());
        		assertEquals("short", attribute.getNeutralType()); 

        		assertTrue(attribute.isNotNull());
        		assertTrue(attribute.isDatabaseNotNull());

        		assertFalse(attribute.isPrimitiveTypeExpected());
        		assertFalse(attribute.isUnsignedTypeExpected());
        		assertFalse(attribute.isObjectTypeExpected());
        		assertTrue(attribute.isSqlTypeExpected());

//        		assertEquals("Short", attribute.getSimpleType() ); 
//        		assertEquals("java.lang.Short", attribute.getFullType() ); 

        	}
        }
    }
    
    @Test
    public void test_ValidModel_TwoEntity_Model() throws EntityParsingError {
        Model model = loadValidModel("src/test/resources/model_test/valid/TwoEntities.model");
        
        assertNotNull(model);
        
        assertEquals(2, model.getEntities().size() ); // 2 entities
        
        //----- ENTITY "Employee"
        Entity employeeEntity = model.getEntityByClassName("Employee");
        assertNotNull(employeeEntity);
        
        assertEquals(4, employeeEntity.getAttributes().size());    
        int i = 0 ;
        Attribute attrib = null ;
        
        // Attributes in their original order :
        
        attrib = employeeEntity.getAttributes().get(i++);
        assertEquals("id", attrib.getName() ) ;
		assertEquals("int", attrib.getNeutralType() ); 
        assertTrue(attrib.isKeyElement());
        assertTrue(attrib.isNotNull()); // If "@Id" => "@NotNull"
        assertTrue(attrib.isDatabaseNotNull() ); // If "@Id" => "@NotNull"
        assertFalse(attrib.isFK());
        assertFalse(attrib.isFKSimple());
        assertFalse(attrib.isFKComposite());
        // Annotations with string paramater : "" if not set
        assertEquals("", attrib.getInitialValue() );
        assertEquals("", attrib.getPattern() );
        assertEquals("", attrib.getInputType() );
        assertEquals("", attrib.getDefaultValue() );
        
        attrib = employeeEntity.getAttributes().get(i++);
        assertEquals("firstName", attrib.getName() ) ;
		assertEquals("string", attrib.getNeutralType() ); 
        assertFalse(attrib.isKeyElement());
        assertFalse(attrib.isNotNull());
        assertFalse(attrib.isDatabaseNotNull());
        assertFalse(attrib.isFK());
        assertFalse(attrib.isFKSimple());
        assertFalse(attrib.isFKComposite());

        attrib = employeeEntity.getAttributes().get(i++);
        assertEquals("birthDate", attrib.getName() ) ;
		assertEquals("date", attrib.getNeutralType() ); 
        assertFalse(attrib.isKeyElement());
        assertFalse(attrib.isNotNull());
        assertFalse(attrib.isDatabaseNotNull());
        assertFalse(attrib.isFK());
        assertFalse(attrib.isFKSimple());
        assertFalse(attrib.isFKComposite());

        attrib = employeeEntity.getAttributes().get(i++);
        assertEquals("country", attrib.getName() ) ;
        assertEquals("int", attrib.getNeutralType() ); // country PK is "id : int" 
        assertFalse(attrib.isKeyElement());
        assertFalse(attrib.isNotNull());
        assertTrue(attrib.isDatabaseNotNull()); // country PK has "@Id" => not null 
        assertTrue(attrib.isFK());
        assertTrue(attrib.isFKSimple());
        assertFalse(attrib.isFKComposite());

    }

    @Test
    public void test_ValidModel_FourEntity_Model() throws EntityParsingError {
        Model model = loadValidModel("src/test/resources/model_test/valid/FourEntities.model");
        
        assertNotNull(model);
        
        assertEquals(4, model.getEntities().size() ); // 2 entities
        
        //----- ENTITY "Employee"
        Entity employeeEntity = model.getEntityByClassName("Employee");
        assertNotNull(employeeEntity);
        
        //----- ENTITY "Country"
        Entity countryEntity = model.getEntityByClassName("Country");
        assertNotNull(countryEntity);
        
        //----- ENTITY "Gender"
        Entity genderEntity = model.getEntityByClassName("Gender");
        assertNotNull(genderEntity);
        
        //----- ENTITY "Person"
        Entity personEntity = model.getEntityByClassName("Person");
        assertNotNull(personEntity);
        assertEquals(5, personEntity.getAttributes().size());
        Attribute attrib ;
        int i = 0 ;
        
        attrib = personEntity.getAttributes().get(i++);
        assertEquals("id", attrib.getName() ) ;

        attrib = personEntity.getAttributes().get(i++);
        assertEquals("firstName", attrib.getName() ) ;

        attrib = personEntity.getAttributes().get(i++);
        assertEquals("lastName", attrib.getName() ) ;

        attrib = personEntity.getAttributes().get(i++);
        assertEquals("birthDate", attrib.getName() ) ;

        // country is a OneToMay links => not in the attributes 
        
        attrib = personEntity.getAttributes().get(i++);
        assertEquals("gender", attrib.getName() ) ;
        assertEquals("string", attrib.getNeutralType() ); // gender PK is "id : string" 
        // Annotations come from "Gender.id" (instead original field)
        assertFalse(attrib.isKeyElement());
        assertFalse(attrib.isNotNull());
        assertTrue(attrib.isDatabaseNotNull()); // gender PK has "@Id" => not null 
        assertEquals(Integer.valueOf(2), attrib.getMaxLength()); // gender PK has "@SizeMax(2)" 
        // Foreign Key (simple, not composite)
        assertTrue(attrib.isFK());
        assertTrue(attrib.isFKSimple());
        assertFalse(attrib.isFKComposite());

    }    
    
    private void testParserWithInvalidModel(String modelFile) {
        Parser dslParser = new Parser();
        DomainModel domainModel = null ;
        ModelParsingError modelParsingError = null ;
		try {
			domainModel = dslParser.parseModel(new File(modelFile));
		} catch (ModelParsingError e) {
			modelParsingError = e ;
		}    	
        assertNull(domainModel);
        assertNotNull(modelParsingError);
		System.out.println("ModelParsingError message : " + modelParsingError.getMessage() );
		for ( EntityParsingError entityError : modelParsingError.getEntitiesErrors() ) {
			//System.out.println(" . EntityParsingError : " + entityError.getEntityName() + " : " + entityError.getMessage() );
			System.out.println(" . EntityParsingError : " + entityError.getMessage() );
			for ( FieldParsingError fieldError : entityError.getFieldsErrors() ) {
				//System.out.println(" . . FieldParsingError : " + fieldError.getEntityName() + " : " + fieldError.getMessage() );
				System.out.println(" . . FieldParsingError : " + fieldError.getMessage() );
			}
		}
    }

    @Test
    public void testParser_InvalidModel_FourEntity_Model() { 
    	testParserWithInvalidModel("src/test/resources/model_test/invalid/FourEntities.model") ;
//		System.out.println("Loading model : " + modelFile );
    }
    
    @Test
    public void test_InvalidModel_FourEntity_Model() { 
    	String modelFile = "src/test/resources/model_test/invalid/FourEntities.model" ;
		System.out.println("Loading model : " + modelFile );
        DslModelManager dslModelManager = new DslModelManager();
        Model model = dslModelManager.loadModel(modelFile);
        assertNull(model);
        assertNotNull(dslModelManager.getErrorMessage());
        assertNotNull(dslModelManager.getErrors());
        assertTrue( dslModelManager.getErrors().getAllErrorsCount() > 0 );
//        Map<String,String> errors = dslModelManager.getParsingErrors();
//        for ( Map.Entry<String,String> entry : errors.entrySet() ) {
//        	System.out.println(" . " + entry.getKey() + " : " + entry.getValue() );
//        }
        printErrors(dslModelManager);
    }
    
    private void printErrors(DslModelManager dslModelManager) {
    	System.out.println("DslModelManager errors : " );
    	System.out.println(" . Error message : " + dslModelManager.getErrorMessage() );
    	DslModelErrors errors = dslModelManager.getErrors();
    	if ( errors != null ) {
        	System.out.println(" . All Errors Count : " + errors.getAllErrorsCount() );
    		for ( String entityName : errors.getEntities() ) {
            	System.out.println(" --> Entity '" + entityName + "' : " );
        		for ( FieldParsingError fpe : errors.getErrors(entityName) ) {
                	System.out.println("   . [" + fpe.getEntityName() + "] "+ fpe.getMessage() );
        		}
    		}
    	}
    }
}
