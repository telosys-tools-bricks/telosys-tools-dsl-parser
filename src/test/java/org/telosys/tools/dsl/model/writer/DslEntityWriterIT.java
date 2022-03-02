package org.telosys.tools.dsl.model.writer;

import org.junit.Test;
import org.telosys.tools.dsl.DslModelManager;
import org.telosys.tools.dsl.model.DslModel;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.model.writer.EntityFileWriter;
import org.telosys.tools.generic.model.Model;
import org.telosys.tools.junit.utils.ModelUtil;
import org.telosys.tools.junit.utils.PrintUtil;

import junit.framework.TestCase;

public class DslEntityWriterIT extends TestCase {

//	private void print(String s) {
//		System.out.println(s);
//	}
//	
//	private Model loadModel(String modelFileName) {
//		
//		ModelUtil.loadValidModel(modelFileName);
//		
//        DslModelManager dslModelManager = new DslModelManager();
//        Model model = dslModelManager.loadModel(modelFileName);
//        PrintUtil.printErrors(dslModelManager.getErrors());
//		assertEquals(0, dslModelManager.getErrors().getNumberOfErrors());
//		return model;
//	}
	
	@Test
	public void testParseEntityFileEmployee() { 

		//DslModel model = (DslModel) loadModel("src/test/resources/model_test_v_3_3/People.model");
		DslModel model = (DslModel) ModelUtil.loadValidModel("src/test/resources/model_test/valid/PeopleModel");
		
		EntityFileWriter writer = new EntityFileWriter("C:\\Temp");
		
		
		writer.writeEntity((DslModelEntity) model.getEntityByClassName("Person"));
		writer.writeEntity((DslModelEntity) model.getEntityByClassName("Country"));
		writer.writeEntity((DslModelEntity) model.getEntityByClassName("Department"));
	}
}
