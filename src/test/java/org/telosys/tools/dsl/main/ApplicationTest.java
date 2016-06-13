package org.telosys.tools.dsl.main;

//import static org.fest.assertions.api.Assertions.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.telosys.tools.dsl.DslModelManager;
import org.telosys.tools.generic.model.Attribute;
import org.telosys.tools.generic.model.Entity;
import org.telosys.tools.generic.model.Model;

public class ApplicationTest {

    @Test
    public void test() {
        // Given
        String dslFolder = "src/test/resources/model_test/valid/OneEntity.model";
        DslModelManager modelLoader = new DslModelManager();

        // When
        Model model = modelLoader.loadModel(dslFolder);

        // Then
        //assertThat(model).isNotNull();
        assertNotNull(model);
        
        //assertThat(model.getEntities()).hasSize(1);
        assertEquals(1, model.getEntities().size() );
        
        //assertThat(model.getEntities().get(0).getClassName()).isEqualTo("Employee");
        assertEquals("Employee", model.getEntities().get(0).getClassName() );
        
        Entity employeeEntity = model.getEntityByClassName("Employee");
        assertNotNull(employeeEntity);
        
//        assertThat(model.getEntities().get(0).getAttributes()).hasSize(3);
//        assertThat(model.getEntities().get(0).getAttributes().get(0).getName()).isEqualTo("birthDate");
//        assertThat(model.getEntities().get(0).getAttributes().get(1).getName()).isEqualTo("firstName");
//        assertThat(model.getEntities().get(0).getAttributes().get(2).getName()).isEqualTo("id");

        assertEquals(3, employeeEntity.getAttributes().size());    
        int i = 0 ;
        Attribute attrib = null ;
        
        // Attributes in their original order :
        
        attrib = employeeEntity.getAttributes().get(i++);
        assertEquals("id", attrib.getName() ) ;
        assertEquals("Integer", attrib.getSimpleType() ) ;
        assertEquals("java.lang.Integer", attrib.getFullType() ) ;
        assertTrue(attrib.isKeyElement());
        assertFalse(attrib.isNotNull());
        
        attrib = employeeEntity.getAttributes().get(i++);
        assertEquals("firstName", attrib.getName() ) ;
        assertEquals("String", attrib.getSimpleType() ) ;
        assertEquals("java.lang.String", attrib.getFullType() ) ;
        assertFalse(attrib.isKeyElement());
        assertFalse(attrib.isNotNull());

        attrib = employeeEntity.getAttributes().get(i++);
        assertEquals("birthDate", attrib.getName() ) ;
        assertEquals("Date", attrib.getSimpleType() ) ;
        assertEquals("java.util.Date", attrib.getFullType() ) ;
        assertFalse(attrib.isKeyElement());
        assertFalse(attrib.isNotNull());

    }

}
