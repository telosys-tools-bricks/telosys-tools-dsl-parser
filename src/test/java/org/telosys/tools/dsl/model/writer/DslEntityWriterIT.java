package org.telosys.tools.dsl.model.writer;

import org.junit.Test;
import org.telosys.tools.dsl.model.DslModel;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.junit.utils.ModelUtil;

import junit.framework.TestCase;

public class DslEntityWriterIT extends TestCase {

	@Test
	public void testParseEntityFileEmployee() { 

		DslModel model = (DslModel) ModelUtil.loadValidModel("src/test/resources/model_test/valid/PeopleModel");
		
		EntityFileWriter writer = new EntityFileWriter("C:\\Temp");
		
		
		writer.writeEntity((DslModelEntity) model.getEntityByClassName("Person"));
		writer.writeEntity((DslModelEntity) model.getEntityByClassName("Country"));
		writer.writeEntity((DslModelEntity) model.getEntityByClassName("Department"));
	}
}
