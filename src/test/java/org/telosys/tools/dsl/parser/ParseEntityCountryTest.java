package org.telosys.tools.dsl.parser;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.telosys.tools.dsl.DslModelErrors;
import org.telosys.tools.dsl.parser.ParserV2;
import org.telosys.tools.dsl.parser.model.DomainCardinality;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.model.DomainField;
import org.telosys.tools.dsl.parser.reporting.ResultReport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ParseEntityCountryTest {
	
	@Test
	public void testEntityParser() {
		
		File entityFile = new File("src/test/resources/entity_test_v_3_2/Country.entity") ;
		
		List<String> entitiesNames = new LinkedList<>();
		DslModelErrors errors = new DslModelErrors();
		ParserV2 parser = new ParserV2();
		DomainEntity entity = parser.parseEntity(entityFile, entitiesNames, errors );
		
		ResultReport.print(entity, errors);
		
		assertEquals("Country", entity.getName());
		assertEquals(2, entity.getNumberOfFields());
		assertEquals(2, entity.getFields().size());
		
		List<DomainField> fields = entity.getFields();
		
		DomainField field1 = fields.get(0);
		assertEquals("id", field1.getName() );
		assertTrue( field1.isAttribute());
		assertFalse( field1.isLink());
		assertEquals(DomainCardinality.ONE, field1.getCardinality());

		DomainField field2 = fields.get(1);
		assertEquals("name", field2.getName() );
	}

}
