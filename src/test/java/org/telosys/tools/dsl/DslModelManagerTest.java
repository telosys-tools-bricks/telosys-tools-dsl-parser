package org.telosys.tools.dsl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;
import org.telosys.tools.dsl.parser.exceptions.EntityParsingError;
import org.telosys.tools.generic.model.Attribute;
import org.telosys.tools.generic.model.Entity;
import org.telosys.tools.generic.model.Model;

public class DslModelManagerTest {
    
	private Model loadModel(String modelFileName) throws EntityParsingError {
		System.out.println("Loading model : " + modelFileName );
        DslModelManager modelLoader = new DslModelManager();
        Model model = modelLoader.loadModel(modelFileName);
		if ( model == null ) {
			System.out.println("ERROR : cannot load model");
			System.out.println(modelLoader.getErrorMessage());
			//modelLoader.getParsingErrors()
			for ( Map.Entry<String, String> entry : modelLoader.getParsingErrors().entrySet() )
			{
				System.out.println(" . " + entry.getKey() + " : " + entry.getValue() );
			}
			throw new RuntimeException("TEST ERROR");
		}
		System.out.println("Model loaded." );
		return model ;
    }
    
    @Test
    public void testModel1() throws EntityParsingError {
        Model model = loadModel("src/test/resources/model_test/valid/OneEntity.model");
        
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
    public void test2() throws EntityParsingError {
        Model model = loadModel("src/test/resources/model_test/valid/types.model");
        
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
}
