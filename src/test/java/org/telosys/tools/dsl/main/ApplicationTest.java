package org.telosys.tools.dsl.main;

import static org.fest.assertions.api.Assertions.*;

import org.junit.Test;
import org.telosys.tools.generic.model.Model;


public class ApplicationTest {

    @Test
    public void test() {
        // Given
        String dslFolder = "src/test/resources/model_test/valid/model_withAnEntity";
        Application application = new Application();

        // When
        Model model = application.parseAndConvert(dslFolder);

        // Then
        assertThat(model).isNotNull();
        assertThat(model.getEntities()).hasSize(1);
        assertThat(model.getEntities().get(0).getClassName()).isEqualTo("Employee");
        assertThat(model.getEntities().get(0).getAttributes()).hasSize(3);
        assertThat(model.getEntities().get(0).getAttributes().get(0).getName()).isEqualTo("birthDate");
        assertThat(model.getEntities().get(0).getAttributes().get(1).getName()).isEqualTo("firstName");
        assertThat(model.getEntities().get(0).getAttributes().get(2).getName()).isEqualTo("id");
    }

}
