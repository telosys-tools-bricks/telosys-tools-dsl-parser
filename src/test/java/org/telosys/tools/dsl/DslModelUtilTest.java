package org.telosys.tools.dsl;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class DslModelUtilTest {
	
    @Test
    public void testGetModelName() {
    	String modelName = DslModelUtil.getModelName(new File("C:/foo/bar/toto.model"));
    	Assert.assertEquals("toto", modelName);
    }
    
    @Test(expected=RuntimeException.class)
    public void testGetModelNameInvalid() {
    	DslModelUtil.getModelName(new File("C:/foo/bar/toto.foo"));
    }
    
    @Test
    public void testGetEntityName() {
    	String modelName = DslModelUtil.getEntityName(new File("C:/foo/bar/Toto.entity"));
    	Assert.assertEquals("Toto", modelName);
    }
    
    @Test(expected=RuntimeException.class)
    public void testGetEntityNameInvalid() {
    	DslModelUtil.getEntityName(new File("C:/foo/bar/Toto.txt"));
    }
    
    @Test
    public void testGetEntitiesAbsoluteFileNames() {
    	File modelFile = new File("src/test/resources/model_test/valid/FourEntities.model");
    	List<String> list = DslModelUtil.getEntitiesAbsoluteFileNames(modelFile);
    	for ( String s : list ) {
    		System.out.println(" . " + s );
    	}
    	Assert.assertEquals(4, list.size());
    }
    
}
