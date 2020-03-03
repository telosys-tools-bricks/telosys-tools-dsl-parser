package org.telosys.tools.dsl.parsing.modellevel;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.telosys.tools.dsl.parser.Parser;
import org.telosys.tools.dsl.parser.exceptions.EntityParsingError;
import org.telosys.tools.dsl.parser.exceptions.FieldParsingError;
import org.telosys.tools.dsl.parser.model.DomainEntity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;

public class ParserEntityLevelTest {
	
	private DomainEntity parseEntityFile(String entityFileName) throws EntityParsingError {
		File file = new File(entityFileName);
		List<String> entitiesNames = new LinkedList<>();
		entitiesNames.add("Country");
		entitiesNames.add("Employee");
		entitiesNames.add("Gender");
		entitiesNames.add("Person");
		
		Parser parser = new Parser();
		return parser.parseEntity(file, entitiesNames);
	}
	
	private void print(EntityParsingError exception) {
		System.out.println("EntityParsingError : " + exception.getMessage() );
		for ( FieldParsingError error : exception.getFieldsErrors() ) {
			System.out.println(" . FieldParsingError : " + error.getMessage() + " [" + error.getEntityName() + "]");
		}
	}

	@Test
	public void parseEntity_Employee_OK() {
		DomainEntity entity = null ;
		EntityParsingError exception = null;
		try {
			entity = parseEntityFile("src/test/resources/model_test/invalid/FourEntities_model/Employee.entity");
		} catch (EntityParsingError e) {
			exception = e;
		}
		// "Entity OK" expected 
		assertNull(exception);
		assertNotNull(entity);
		assertFalse(entity.hasError());
	}

	@Test
	public void parseEntity_Country_ERR() {
		DomainEntity entity = null ;
		EntityParsingError exception = null;
		try {
			entity = parseEntityFile("src/test/resources/model_test/invalid/FourEntities_model/Country.entity");
		} catch (EntityParsingError e) {
			exception = e;
		}
		assertNotNull(exception);
		assertNull(entity);
		print(exception);
		assertEquals(1, exception.getFieldsErrors().size() );		
	}


	@Test
	public void parseEntity_Gender_ERR() {
		DomainEntity entity = null ;
		EntityParsingError exception = null;
		try {
			entity = parseEntityFile("src/test/resources/model_test/invalid/FourEntities_model/Gender.entity");
		} catch (EntityParsingError e) {
			exception = e;
		}
		assertNull(entity);
		assertNotNull(exception);
		print(exception);
		assertEquals(2, exception.getFieldsErrors().size() );		
	}

	@Test
	public void parseEntity_Person_ERR() {
		DomainEntity entity = null ;
		EntityParsingError exception = null;
		try {
			entity = parseEntityFile("src/test/resources/model_test/invalid/FourEntities_model/Person.entity");
		} catch (EntityParsingError e) {
			exception = e;
		}
		assertNull(entity);
		assertNotNull(exception);
		print(exception);
		assertEquals(4, exception.getFieldsErrors().size() );		
	}

}
