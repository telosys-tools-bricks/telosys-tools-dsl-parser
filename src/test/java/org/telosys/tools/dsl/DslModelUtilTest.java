package org.telosys.tools.dsl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Test;
import org.telosys.tools.junit.utils.TestFileProvider;

public class DslModelUtilTest {
	
    @Test
    public void testGetModelName() {
    	String modelName = DslModelUtil.getModelName(new File("C:/foo/bar/toto.model"));
    	assertEquals("toto", modelName);
    }
    
    @Test(expected=RuntimeException.class)
    public void testGetModelNameInvalid() {
    	DslModelUtil.getModelName(new File("C:/foo/bar/toto.foo"));
    }
    
    @Test
    public void testGetEntityName() {
    	String modelName = DslModelUtil.getEntityName(new File("C:/foo/bar/Toto.entity"));
    	assertEquals("Toto", modelName);
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
    	assertEquals(4, list.size());
    }
    
    @Test
    public void testRenameEntity() {
    	String newName = "Country2" ;
    	File entityFile = TestFileProvider.copyAndGetTargetTmpFile("model_test/valid/TwoEntities_model/Country.entity");
    	assertTrue(entityFile.exists()) ;
    	assertTrue(entityFile.isFile()) ;

    	TestFileProvider.removeTargetTmpFileIfExists("model_test/valid/TwoEntities_model/Country2.entity");
    	File newFile = TestFileProvider.getTargetTmpFile("model_test/valid/TwoEntities_model/Country2.entity");
    	
    	System.out.println("Rename " + entityFile.getAbsolutePath() + " to " + newName );
    	DslModelUtil.renameEntity(entityFile, newName);
    	
    	assertTrue( newFile.exists() );
    }
    
}
