package org.telosys.tools.dsl;

import org.junit.Test;
import org.telosys.tools.generic.model.Attribute;
import org.telosys.tools.generic.model.Entity;
import org.telosys.tools.generic.model.Model;
import org.telosys.tools.generic.model.TagContainer;
import org.telosys.tools.junit.utils.ModelUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DslModelManagerTest {
    
	private void println(String s) {
		System.out.println(s);
	}

//	private Model loadValidModel(String modelFileName) {
//		println("----- " );
//		println("Loading valid model : " + modelFileName );
//        DslModelManager dslModelManager = new DslModelManager();
//        Model model = dslModelManager.loadModel(modelFileName);
//		printErrors(dslModelManager);
//		if ( model == null ) {
//			println("ERROR : cannot load model");
//			println(dslModelManager.getErrorMessage());
//			throw new IllegalStateException("Cannot load model : " + dslModelManager.getErrorMessage());
//		}
//		// No error expected
//		assertEquals(0, dslModelManager.getErrorMessage().length() );
//		assertEquals(0, dslModelManager.getErrors().getAllErrorsCount() );
//		return model ;
//    }
//	private Model loadValidModel(String modelFileName) {
//		println("----- " );
//		println("Loading valid model : " + modelFileName );
//        DslModelManager dslModelManager = new DslModelManager();
//        Model model = dslModelManager.loadModel(modelFileName);
//        PrintUtil.printErrors(dslModelManager.getErrors());
//		if ( model == null || dslModelManager.getErrors().getNumberOfErrors() > 0 ) {
//			println("ERROR : cannot load model");
//			throw new RuntimeException("Cannot load model : " + dslModelManager.getErrorMessage());
//		}
//		return model ;
//    }
    
    @Test
    public void testValidModelOneEntityModel() {
        Model model = ModelUtil.loadValidModel("src/test/resources/model_test/valid/OneEntityModel");
        
        assertNotNull(model);
        
        assertEquals(1, model.getEntities().size() );
        
        assertEquals("Employee", model.getEntities().get(0).getClassName() );
        
        Entity employeeEntity = model.getEntityByClassName("Employee");
        assertNotNull(employeeEntity);
        // TODO
        // employeeEntity.isAggregateRoot() ;
        
        TagContainer tagContainer = employeeEntity.getTagContainer();
        // TODO 
        // assertFalse(tagContainer.isEmpty());
        // assertEquals(1, tagContainer.size());
        // assertTrue(tagContainer.containsTag("MyEntityTag"));
        
        assertEquals(3, employeeEntity.getAttributes().size());    
        int i = 0 ;
        Attribute attrib = null ;
        TagContainer tags ;
        
        // Attributes in their original order :
        
        //--- "id" ATTRIBUTE 
        attrib = employeeEntity.getAttributes().get(i++);
        assertEquals("id", attrib.getName() ) ;
		assertEquals("int", attrib.getNeutralType() ); 
        assertTrue(attrib.isKeyElement());
        assertTrue(attrib.isNotNull()); // If "@Id" => "@NotNull"
        assertTrue(attrib.isDatabaseNotNull() ); // If "@Id" => "@NotNull"
        // Tags defined for this attribute
        tags = attrib.getTagContainer();
        assertNotNull(tags);
//        assertFalse(tags.isEmpty());
        assertTrue(tags.containsTag("Id"));
        assertTrue(tags.containsTag("Foo"));
        assertEquals("abc",tags.getTagValue("Foo"));

        //--- "firstName" ATTRIBUTE 
        attrib = employeeEntity.getAttributes().get(i++);
        assertEquals("firstName", attrib.getName() ) ;
		assertEquals("string", attrib.getNeutralType() ); 
        assertFalse(attrib.isKeyElement());
        assertFalse(attrib.isNotNull());
        assertFalse(attrib.isDatabaseNotNull());
        tags = attrib.getTagContainer();
        assertNotNull(tags);
//        assertFalse(tags.isEmpty());
        assertTrue(tags.containsTag("tag"));

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
        Model model = ModelUtil.loadValidModel("src/test/resources/model_test/valid/typesModel");
        
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
        Model model = ModelUtil.loadValidModel("src/test/resources/model_test/valid/TwoEntitiesModel");
        
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
        Model model = ModelUtil.loadValidModel("src/test/resources/model_test/valid/FourEntitiesModel");
        
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
    
}
