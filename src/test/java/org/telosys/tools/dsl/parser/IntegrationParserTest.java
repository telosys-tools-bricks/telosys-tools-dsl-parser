package org.telosys.tools.dsl.parser;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.telosys.tools.dsl.EntityParserException;
import org.telosys.tools.dsl.parser.model.DomainModel;


public class IntegrationParserTest {


    @Test
    public void testParseInvalidFiles() throws Exception {
        File folder = new File("src/test/resources/entity_test/invalid/");
        for (final File fileEntry : folder.listFiles()) {
            if (!fileEntry.isDirectory()) {
            	Exception errorDetected = null ;
                try {
                	EntityParser parser = new EntityParser(new DomainModel("model"));
                    parser.parse(fileEntry);
                } catch (Exception e) {
                    errorDetected = e ;
                }
                Assert.assertNotNull(errorDetected);
            }
        }
    }

}
