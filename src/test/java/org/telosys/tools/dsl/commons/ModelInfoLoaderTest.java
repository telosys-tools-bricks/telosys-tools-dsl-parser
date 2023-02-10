package org.telosys.tools.dsl.commons;

import java.io.File;

import org.junit.Test;
import org.telosys.tools.dsl.DslModelError;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ModelInfoLoaderTest {

	@Test
	public void test1() throws DslModelError {
		
		File modelYamlFile = new File("src/test/resources/model-yaml-files/model1.yaml") ;
		ModelInfo modelInfo = ModelInfoLoader.loadModelInformation(modelYamlFile);
		
		// title : "my title  " (keep trailing blanks)
		assertNotNull(modelInfo.getTitle());
		assertEquals("my title  ", modelInfo.getTitle());

		// version :    1.0.0    
		assertNotNull(modelInfo.getVersion());
		assertEquals("1.0.0", modelInfo.getVersion());

		// description :  
		// no value => SnakeYaml set NULL but null value is replaced by "" in ModelInfo
		assertEquals("", modelInfo.getDescription());
	}
	
	@Test
	public void test2() throws DslModelError {
		
		File modelYamlFile = new File("src/test/resources/model-yaml-files/model2.yaml") ;
		ModelInfo modelInfo = ModelInfoLoader.loadModelInformation(modelYamlFile);
		
		assertEquals("", modelInfo.getTitle());
		assertEquals("", modelInfo.getVersion());
		assertEquals("", modelInfo.getDescription());
	}
	
}
