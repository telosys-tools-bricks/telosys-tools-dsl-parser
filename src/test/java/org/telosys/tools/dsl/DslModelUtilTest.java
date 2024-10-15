package org.telosys.tools.dsl;

import java.io.File;
import java.util.List;

import org.junit.Test;
import org.telosys.tools.junit.utils.TestFileProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DslModelUtilTest {
	
    @Test
    public void testGetModelName() {
    	assertEquals("mymodel", DslModelUtil.getModelNameFromFolderName("foo/bar/mymodel") );
    	assertEquals("toto", DslModelUtil.getModelNameFromFolderName("C:/foo/bar/toto") );
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
    public void testGetEntityFullFileNames() {
    	File modelFolder = new File("src/test/resources/model_test/valid/FourEntitiesModel");
    	List<String> list = DslModelUtil.getEntityFullFileNames(modelFolder);
    	for ( String s : list ) {
        	assertTrue(s.contains("FourEntitiesModel"));
        	assertTrue(s.endsWith(".entity"));
    	}
    	assertEquals(4, list.size());
    }
    
    @Test
    public void testGetEntityShortFileNames() {
    	File modelFolder = new File("src/test/resources/model_test/valid/FourEntitiesModel");
    	List<String> list = DslModelUtil.getEntityShortFileNames(modelFolder);
    	for ( String s : list ) {
        	assertTrue(s.endsWith(".entity"));
    	}
    	assertEquals(4, list.size());
    	assertTrue(list.contains("Employee.entity"));
    	assertTrue(list.contains("Country.entity"));
    }
    
    @Test
    public void testGetEntityNames() {
    	File modelFolder = new File("src/test/resources/model_test/valid/FourEntitiesModel");
    	List<String> list = DslModelUtil.getEntityNames(modelFolder);
    	assertEquals(4, list.size());
    	assertTrue(list.contains("Employee"));
    	assertTrue(list.contains("Country"));
    }
    
    @Test
    public void testRenameEntity() {
    	String newName = "Country2" ;
    	File entityFile = TestFileProvider.copyAndGetTargetTmpFile("model_test/valid/TwoEntitiesModel/Country.entity");
    	assertTrue(entityFile.exists()) ;
    	assertTrue(entityFile.isFile()) ;

    	TestFileProvider.removeTargetTmpFileIfExists("model_test/valid/TwoEntitiesModel/Country2.entity");
    	File newFile = TestFileProvider.getTargetTmpFile("model_test/valid/TwoEntitiesModel/Country2.entity");
    	
    	File renamedFile = DslModelUtil.renameEntity(entityFile, newName);
    	assertTrue( newFile.exists() );
    	assertTrue( renamedFile.exists() );
    	assertEquals("Country2.entity", renamedFile.getName());
    }
    
    private File getFile(String fileFullPath) {
    	return new File(fileFullPath) ;
    }
    
    @Test
    public void testIsValidModelFile() {
		// check only file path (without folder existence)
    	assertTrue(DslModelUtil.isValidModelFile( getFile("/foo/bar/TelosysTools/aaaa/model.yaml") )) ;
    	
//    	assertFalse(DslModelUtil.isValidModelFile( getFile("/foo/bar/aaaa.model"), false )) ;
//    	assertFalse(DslModelUtil.isValidModelFile( getFile("/foo/bar/TelosysTools/aaaa.txt"), false )) ;
//    	assertFalse(DslModelUtil.isValidModelFile( getFile("/aaaa.model"), false )) ;
//    	assertFalse(DslModelUtil.isValidModelFile( getFile("aaaa.model"), false )) ;

//		// check folder existence
//    	assertFalse(DslModelUtil.isValidModelFile( getFile("/foo/bar/TelosysTools/aaaa.model"), true )) ; // No parent folder
    }

    @Test
    public void testIsValidEntityFile() {		
		// check only file path (without folder existence)
    	assertTrue(DslModelUtil.isValidEntityFile( getFile("/foo/bar/TelosysTools/foo_model/aaaa.entity") )) ;
    	assertTrue(DslModelUtil.isValidEntityFile( getFile("/foo/bar/TelosysTools/foo_model/Book.entity") )) ;
    	assertTrue(DslModelUtil.isValidEntityFile( getFile("/foo/bar/TelosysTools/demo_model/Book.entity") )) ;
    	
    	assertFalse(DslModelUtil.isValidEntityFile( getFile("/foo/bar/TelosysTools/demo_model/Book.txt") )) ;
    	assertFalse(DslModelUtil.isValidEntityFile( getFile("/foo/bar/TelosysTools/demo_model/Book") )) ;
    	assertTrue(DslModelUtil.isValidEntityFile( getFile("/foo/bar/aaaa.entity") )) ;
    	assertTrue(DslModelUtil.isValidEntityFile( getFile("/foo/bar/TelosysTools/aaaa.entity") )) ;
    	assertTrue(DslModelUtil.isValidEntityFile( getFile("/aaaa.entity") )) ;
    	assertTrue(DslModelUtil.isValidEntityFile( getFile("aaaa.entity") )) ;
    	assertTrue(DslModelUtil.isValidEntityFile( getFile("demo_model/aaaa.entity") )) ;
    }
    
    @Test
    public void testGetModelFileForEntityFile() {
    	File file ;
		file = DslModelUtil.getModelFileFromEntityFile( getFile("/a/b/c/mymodel/aaaa.entity") ) ;
		assertEquals("model.yaml", file.getName());
		assertEquals("mymodel", file.getParentFile().getName());
    }
}
