package org.telosys.tools.dsl.converter;

import java.io.File;

import org.junit.Test;
import org.telosys.tools.dsl.DslModelErrors;
import org.telosys.tools.dsl.model.DslModel;
import org.telosys.tools.dsl.parser.ParserV2;
import org.telosys.tools.dsl.parser.ParsingResult;
import org.telosys.tools.dsl.parser.model.DomainModel;
import org.telosys.tools.generic.model.Attribute;
import org.telosys.tools.generic.model.Entity;
import org.telosys.tools.junit.utils.PrintUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ConverterStep1Test {

	private static final String EMPLOYEE = "Employee";
	private static final String COUNTRY  = "Country";
	
	private DomainModel parseModel(String filePath) {
		File modelFile = new File(filePath);		
		ParserV2 parser = new ParserV2();
		ParsingResult result = parser.parseModel(modelFile);
		PrintUtil.printErrors(result.getErrors());
		
		return result.getModel() ;
	}
	
	private DslModel convertModel(DomainModel domainModel) {
		// Create a new void DSL model 
		DslModel dslModel = new DslModel("test-model");
		// Create a new void DSL model 
		DslModelErrors errors = new DslModelErrors();
		ModelConverter converter = new ModelConverter(errors);
		converter.step1CreateAllVoidEntities(domainModel, dslModel);
		converter.step2CreateAllAttributes(domainModel, dslModel);
		// stop just after attributes conversion
		return dslModel;
	}
	
	@Test
	public void testOneEntityModel() {
		DomainModel domainModel = parseModel("src/test/resources/model_test/valid/OneEntity.model");
		assertEquals(1, domainModel.getNumberOfEntities());
		
		DslModel dslModel = convertModel(domainModel);
		
		assertEquals(1, dslModel.getEntities().size() ) ;
		
		Entity entity = dslModel.getEntityByClassName(EMPLOYEE);
		assertNotNull(entity);
		assertEquals(EMPLOYEE, entity.getClassName() );
		assertNotNull(entity.getAttributes() );
		assertEquals(3, entity.getAttributes().size() );
		
		int i = 0;
		for ( Attribute a : entity.getAttributes() ) {
			i++;
			if ( i == 1 ) {
				assertEquals("id", a.getName() );
				assertTrue(a.isKeyElement());
				assertEquals("int", a.getNeutralType() );
				// Annotations with string parameter
				assertEquals("Identifier", a.getLabel());
				assertEquals("", a.getDefaultValue()); // not defined => ""
				assertEquals("", a.getInitialValue()); // not defined => "" 
				assertEquals("", a.getPattern()); // not defined => "" 
				assertEquals("", a.getInputType()); // not defined => "" 
			}
			if ( i == 2 ) {
				assertEquals("firstName", a.getName() );
				assertFalse(a.isKeyElement());
				assertEquals("string", a.getNeutralType() );
				// Annotations with string parameter
				assertEquals("firstName", a.getLabel());
				assertEquals("Bart", a.getDefaultValue());
				assertEquals("xxx", a.getInitialValue());
				assertEquals("", a.getPattern()); // not defined => "" 
				assertEquals("", a.getInputType()); // not defined => "" 
			}
			if ( i == 3 ) {
				assertEquals("birthDate", a.getName() );
				assertFalse(a.isKeyElement());
				assertEquals("date", a.getNeutralType() );
				// Annotations with string parameter
				assertEquals("birthDate", a.getLabel());
				assertEquals("", a.getDefaultValue());
				assertEquals("", a.getInitialValue());
				assertEquals("date", a.getInputType()); 
				assertEquals("", a.getPattern()); // not defined => "" 
			}
		}
	}
	
	private void printAttribute(Entity e, Attribute a) {
		String type = "???" ;
		if ( a.getNeutralType() != null && a.getNeutralType().trim().length() > 0 ) {
			type = a.getNeutralType() ;
		}
		else if ( a.getReferencedEntityClassName() != null && a.getReferencedEntityClassName().trim().length() > 0 ) {
			type = a.getReferencedEntityClassName() ;
		}
		System.out.println(e.getClassName() + " : - " + a.getName() + " : " + type );
	}
	
	@Test
	public void testTwoEntitiesModel()  {
		DomainModel domainModel = parseModel("src/test/resources/model_test/valid/TwoEntities.model");
		assertEquals(2, domainModel.getNumberOfEntities());
		
		DslModel dslModel = convertModel(domainModel);
		
		assertEquals(2, dslModel.getEntities().size() ) ;
		
		Entity entity ;
		
		entity = dslModel.getEntityByClassName(EMPLOYEE);
		assertNotNull(entity);
		assertEquals(EMPLOYEE, entity.getClassName() );
		// ATTRIBUTES
		assertNotNull(entity.getAttributes() );
		assertEquals(3, entity.getAttributes().size() );
		for ( Attribute a : entity.getAttributes() ) {
			printAttribute(entity, a);
		}
		
		entity = dslModel.getEntityByClassName(COUNTRY);
		assertNotNull(entity);
		assertEquals(COUNTRY, entity.getClassName() );
		assertNotNull(entity.getAttributes() );
		assertEquals(2, entity.getAttributes().size() );
		for ( Attribute a : entity.getAttributes() ) {
			printAttribute(entity, a);
		}
	}
}
