package org.telosys.tools.dsl;

import java.io.File;
import java.util.Map;

import org.junit.Test;
import org.telosys.tools.dsl.parser.Parser;
import org.telosys.tools.dsl.parser.exceptions.EntityParsingError;
import org.telosys.tools.dsl.parser.exceptions.ModelParsingError;
import org.telosys.tools.dsl.parser.exceptions.ParsingError;
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
    
	private void println(String s) {
		System.out.println(s);
	}

	private Model loadValidModel(String modelFileName) {
		println("----- " );
		println("Loading valid model : " + modelFileName );
        DslModelManager dslModelManager = new DslModelManager();
        Model model = dslModelManager.loadModel(modelFileName);
		printErrors(dslModelManager);
		if ( model == null ) {
			println("ERROR : cannot load model");
			println(dslModelManager.getErrorMessage());
			throw new IllegalStateException("Cannot load model : " + dslModelManager.getErrorMessage());
		}
		// No error expected
		assertEquals(0, dslModelManager.getErrorMessage().length() );
		assertEquals(0, dslModelManager.getErrors().getAllErrorsCount() );
		return model ;
    }
    
    @Test
    public void testValidModelOneEntityModel() {
        Model model = loadValidModel("src/test/resources/model_test/valid/OneEntity.model");
        
        assertNotNull(model);
        
        assertEquals(1, model.getEntities().size() );
        
        assertEquals("Employee", model.getEntities().get(0).getClassName() );
        
        Entity employeeEntity = model.getEntityByClassName("Employee");
        assertNotNull(employeeEntity);
        
        assertEquals(3, employeeEntity.getAttributes().size());    
        int i = 0 ;
        Attribute attrib = null ;
        Map<String,String> tagsMap ;
        
        // Attributes in their original order :
        
        //--- "id" ATTRIBUTE 
        attrib = employeeEntity.getAttributes().get(i++);
        assertEquals("id", attrib.getName() ) ;
		assertEquals("int", attrib.getNeutralType() ); 
        assertTrue(attrib.isKeyElement());
        assertTrue(attrib.isNotNull()); // If "@Id" => "@NotNull"
        assertTrue(attrib.isDatabaseNotNull() ); // If "@Id" => "@NotNull"
        // Tags defined for this attribute
        tagsMap = attrib.getTagsMap();
        assertNotNull(tagsMap);
        assertFalse(tagsMap.isEmpty());
        assertTrue(tagsMap.containsKey("Id"));
        assertTrue(tagsMap.containsKey("Foo"));
        assertEquals("abc",tagsMap.get("Foo"));

        //--- "firstName" ATTRIBUTE 
        attrib = employeeEntity.getAttributes().get(i++);
        assertEquals("firstName", attrib.getName() ) ;
		assertEquals("string", attrib.getNeutralType() ); 
        assertFalse(attrib.isKeyElement());
        assertFalse(attrib.isNotNull());
        assertFalse(attrib.isDatabaseNotNull());
        tagsMap = attrib.getTagsMap();
        assertNotNull(tagsMap);
        assertFalse(tagsMap.isEmpty());
        assertTrue(tagsMap.containsKey("tag"));

        //--- "birthDate" ATTRIBUTE 
        attrib = employeeEntity.getAttributes().get(i++);
        assertEquals("birthDate", attrib.getName() ) ;
		assertEquals("date", attrib.getNeutralType() ); 
        assertFalse(attrib.isKeyElement());
        assertFalse(attrib.isNotNull());
        assertFalse(attrib.isDatabaseNotNull());
    }

    @Test
    public void testValidModelTypesModel()  {
        Model model = loadValidModel("src/test/resources/model_test/valid/types.model");
        
        assertNotNull(model);
        assertEquals(1, model.getEntities().size() );
        
        Entity entity = model.getEntityByClassName("Person");
        assertNotNull(entity);
        println("Entity 'Person' found");
        assertEquals("Person", entity.getClassName() );
        assertEquals("Person", entity.getFullName() ); 
        
        for ( Attribute attribute : entity.getAttributes() ) {
        	println(" attribute " + attribute.getName() );
    		println("  " + attribute.getNeutralType() + " --> " + attribute.getNeutralType());
        	if ( attribute.getName().equals("id") ) {
        		assertEquals("int", attribute.getNeutralType() ); 
        		assertTrue(attribute.isKeyElement());
        		assertFalse(attribute.isAutoIncremented());
        		
        		assertFalse(attribute.isPrimitiveTypeExpected());
        		assertFalse(attribute.isUnsignedTypeExpected());
        		assertFalse(attribute.isObjectTypeExpected());
        	}
        	else if ( attribute.getName().equals("firstName") ) {
        		println(attribute.getNeutralType() + " --> " + attribute.getNeutralType());
        		assertTrue(attribute.isNotEmpty());
        		assertFalse(attribute.isKeyElement());
        		assertFalse(attribute.isAutoIncremented());
        	}
        	else if ( attribute.getName().equals("lastName") ) {
        		println(attribute.getNeutralType() + " --> " + attribute.getNeutralType());
        		assertTrue(attribute.isNotBlank());
        		assertFalse(attribute.isKeyElement());
        		assertFalse(attribute.isAutoIncremented());
        	}
        	else if ( attribute.getName().equals("counter") ) {
        		println(attribute.getNeutralType() + " --> " + attribute.getNeutralType());
        		assertEquals("short", attribute.getNeutralType()); 
        		assertTrue(attribute.isAutoIncremented());
        	}
        	else if ( attribute.getName().equals("counter2") ) {
        		println(attribute.getNeutralType() + " --> " + attribute.getNeutralType());
        		assertEquals("short", attribute.getNeutralType()); 
        		assertTrue(attribute.isAutoIncremented());
        	}
        	else if ( attribute.getName().equals("counter3") ) {
        		println(attribute.getNeutralType() + " --> " + attribute.getNeutralType());
        		assertEquals("short", attribute.getNeutralType()); 

        		assertTrue(attribute.isPrimitiveTypeExpected());
        		assertFalse(attribute.isUnsignedTypeExpected());
        		assertFalse(attribute.isObjectTypeExpected());
        	}
        	else if ( attribute.getName().equals("counter4") ) {
        		println(attribute.getNeutralType() + " --> " + attribute.getNeutralType());
        		assertEquals("short", attribute.getNeutralType()); 

        		assertFalse(attribute.isNotNull());
        		assertFalse(attribute.isPrimitiveTypeExpected());
        		assertFalse(attribute.isUnsignedTypeExpected());
        		assertTrue(attribute.isObjectTypeExpected());
        	}
        	else if ( attribute.getName().equals("counter5") ) {
        		println(attribute.getNeutralType() + " --> " + attribute.getNeutralType());
        		assertEquals("short", attribute.getNeutralType()); 

        		assertFalse(attribute.isNotNull());
        		assertFalse(attribute.isPrimitiveTypeExpected());
        		assertFalse(attribute.isUnsignedTypeExpected());
        	}
        	else if ( attribute.getName().equals("counter6") ) {
        		println(attribute.getNeutralType() + " --> " + attribute.getNeutralType());
        		assertEquals("short", attribute.getNeutralType()); 

        		assertTrue(attribute.isNotNull());
        		assertTrue(attribute.isDatabaseNotNull());
        		assertFalse(attribute.isPrimitiveTypeExpected());
        		assertFalse(attribute.isUnsignedTypeExpected());
        		assertTrue(attribute.isObjectTypeExpected());
        	}
        	else if ( attribute.getName().equals("counter7") ) {
        		println(attribute.getNeutralType() + " --> " + attribute.getNeutralType());
        		assertEquals("short", attribute.getNeutralType()); 

        		assertTrue(attribute.isNotNull());
        		assertTrue(attribute.isDatabaseNotNull());

        		assertFalse(attribute.isPrimitiveTypeExpected());
        		assertFalse(attribute.isUnsignedTypeExpected());
        		assertFalse(attribute.isObjectTypeExpected());
        	}
        }
    }
    
    @Test
    public void testValidModelTwoEntityModel() {
        Model model = loadValidModel("src/test/resources/model_test/valid/TwoEntities.model");
        
        assertNotNull(model);
        
        assertEquals(2, model.getEntities().size() ); // 2 entities
        
        //----- ENTITY "Employee"
        Entity employeeEntity = model.getEntityByClassName("Employee");
        assertNotNull(employeeEntity);
        
        assertEquals(3, employeeEntity.getAttributes().size());    
        assertEquals(1, employeeEntity.getLinks().size());    
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

    }

    @Test
    public void testValidModelFourEntityModel() {
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
        assertEquals(4, personEntity.getAttributes().size());
        assertEquals(2, personEntity.getLinks().size());
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
		println("ModelParsingError message : " + modelParsingError.getMessage() );
		for ( EntityParsingError entityError : modelParsingError.getEntitiesErrors() ) {
			println(" . EntityParsingError : " + entityError.getMessage() );
//			for ( FieldParsingError fieldError : entityError.getFieldsErrors() ) {
//				println(" . . FieldParsingError : " + fieldError.getMessage() );
//			}
			for ( ParsingError fieldError : entityError.getErrors() ) {
				println(" . . ParsingError : " + fieldError.getMessage() );
			}
		}
    }

    @Test
    public void testParserInvalidModelFourEntityModel() { 
    	testParserWithInvalidModel("src/test/resources/model_test/invalid/FourEntities.model") ;
    }
    
    @Test
    public void testInvalidModelTwoEntityModel() { 
    	String modelFile = "src/test/resources/model_test/invalid/TwoEntities.model" ;
		println("Loading model : " + modelFile );
        DslModelManager dslModelManager = new DslModelManager();
        Model model = dslModelManager.loadModel(modelFile);
        printErrors(dslModelManager);

        assertNull(model);
        assertNotNull(dslModelManager.getErrorMessage());
        assertNotNull(dslModelManager.getErrors());
        assertTrue( dslModelManager.getErrors().getAllErrorsCount() > 0 );
    }
    
    @Test
    public void testInvalidModelFourEntityModel() { 
    	String modelFile = "src/test/resources/model_test/invalid/FourEntities.model" ;
		println("Loading model : " + modelFile );
        DslModelManager dslModelManager = new DslModelManager();
        Model model = dslModelManager.loadModel(modelFile);
        printErrors(dslModelManager);
        
        assertNull(model);
        assertNotNull(dslModelManager.getErrorMessage());
        assertNotNull(dslModelManager.getErrors());
        assertTrue( dslModelManager.getErrors().getAllErrorsCount() > 0 );
    }
    
    private void printErrors(DslModelManager dslModelManager) {
    	println("DslModelManager errors : " );
    	println(" Error message : " + dslModelManager.getErrorMessage() );
    	DslModelErrors errors = dslModelManager.getErrors();
    	if ( errors != null ) {
        	println(" All Errors Count : " + errors.getAllErrorsCount() );
//    		for ( String entityName : errors.getEntities() ) {
//            	println(" --> Entity '" + entityName + "' : " );
//        		for ( FieldParsingError fpe : errors.getErrors(entityName) ) {
//                	// println("   . [" + fpe.getEntityName() + "] "+ fpe.getMessage() );
//                	println("   . [" + fpe.getEntityName() + "] field '"+ fpe.getFieldName() + "' : " + fpe.getError() );
//        		}
//    		}
        	println(" All Errors : " );
    		for ( String err : errors.getAllErrorsList() ) {
            	println(" . " + err );
    		}
        	println(" Errors for each entity : " );
    		for ( String entityName : errors.getEntities() ) {
    			println(" --> Entity '" + entityName + "' : " );
    			for ( String err : errors.getErrors(entityName) ) {
                	println(" . " + err );
    			}
    		}
    	}
    }
}
