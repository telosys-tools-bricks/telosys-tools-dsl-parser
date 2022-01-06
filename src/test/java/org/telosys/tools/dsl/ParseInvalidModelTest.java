package org.telosys.tools.dsl;

import org.junit.Test;
import org.telosys.tools.junit.utils.ModelUtil;

import static org.junit.Assert.assertEquals;

public class ParseInvalidModelTest {
    
    @Test
    public void testParserInvalidModelFourEntityModel() { 
    	DslModelErrors errors = ModelUtil.parseInvalidModel("src/test/resources/model_test/invalid/FourEntities.model") ;
    	assertEquals(10, errors.getNumberOfErrors() );
    }
    
    @Test
    public void testInvalidModelTwoEntityModel() { 
    	DslModelErrors errors = ModelUtil.parseInvalidModel("src/test/resources/model_test/invalid/TwoEntities.model");
    	assertEquals(7, errors.getNumberOfErrors() );
    }
    
    @Test
    public void testInvalidModelFourEntityModel() { 
    	DslModelErrors errors = ModelUtil.parseInvalidModel("src/test/resources/model_test/invalid/FourEntities.model");
    	assertEquals(10, errors.getNumberOfErrors() );
    }
    
}
