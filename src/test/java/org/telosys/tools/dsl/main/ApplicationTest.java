package org.telosys.tools.dsl.main;

//import static org.fest.assertions.api.Assertions.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.telosys.tools.dsl.DslModelManager;
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
        assertEquals("birthDate", employeeEntity.getAttributes().get(0).getName() );
        assertEquals("firstName", employeeEntity.getAttributes().get(1).getName() );
        assertEquals("id",        employeeEntity.getAttributes().get(2).getName() );
        
    }

}
