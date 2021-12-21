package org.telosys.tools.dsl.parser;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.telosys.tools.dsl.parser.Parser;
import org.telosys.tools.dsl.parser.exceptions.EntityParsingError;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.model.DomainField;
import org.telosys.tools.dsl.parser.reporting.EntityReport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class ParseEntityCountryTest {
	
	@Test
	public void testEntityParser() throws EntityParsingError {
		
		File entityFile = new File("src/test/resources/entity_test_v_3_2/Country.entity") ;
//		File entityFile = new File("src/test/resources/model_test/valid/TwoEntities_model/Country.entity") ;
		
		List<String> entitiesNames = new LinkedList<>();
		Parser parser = new Parser();
		DomainEntity entity = parser.parseEntity(entityFile, entitiesNames );
		
		EntityReport.print(entity);
		
		assertEquals("Country", entity.getName());
		assertEquals(2, entity.getNumberOfFields());
		assertEquals(2, entity.getFields().size());
		
		List<DomainField> fields = entity.getFields();
		
		DomainField field1 = fields.get(0);
		assertEquals("id", field1.getName() );
		assertTrue( field1.isNeutralType());
		assertFalse( field1.isEntity());
		assertEquals(1,field1.getCardinality());
	}

}
