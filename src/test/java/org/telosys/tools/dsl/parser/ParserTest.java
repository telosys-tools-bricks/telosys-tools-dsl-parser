package org.telosys.tools.dsl.parser;

import java.io.File;

import org.junit.Test;
import org.telosys.tools.dsl.parser.exceptions.ModelParsingError;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.model.DomainModel;

import static org.junit.Assert.assertEquals;

public class ParserTest {
	
	@Test
	public void test1() throws ModelParsingError {
		File modelFile = new File("src/test/resources/model_test/valid/OneEntity.model");
		
		Parser parser = new Parser();
		DomainModel model = parser.parseModel(modelFile);
		
		assertEquals(1, model.getNumberOfEntities());
		assertEquals(1, model.getEntityNames().size());
		DomainEntity entity = model.getEntity("Employee");
		
		assertEquals("Employee", entity.getName());
		assertEquals(3, entity.getNumberOfFields());
		
////		//entityFileParser.parse();
////		EntityFileParsingResult result = entityFileParser.parse();
//		System.out.println("\nPARSING RESULT :");
//		System.out.println(" Entity name : " + entity.getName() );
//		System.out.println("\nFIELDS PARSED :");
//		for ( DomainField field : entity.getFields() ) {
//			System.out.println(" . " + field);
//		}
	}

}
