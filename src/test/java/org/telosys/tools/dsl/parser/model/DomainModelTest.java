package org.telosys.tools.dsl.parser.model;

import java.util.List;
import java.util.Properties;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DomainModelTest {
	
	private final static String     MODEL_FILE_NAME = "test.model" ;
	private final static Properties MODEL_PROPERTIES = null ;

	@Test
	public void testEntity() {
		DomainModel model = new DomainModel(MODEL_FILE_NAME, MODEL_PROPERTIES);
		assertTrue( model.getNumberOfEntities() == 0 ) ;
		
		model.addEntity( new DomainEntity("Book") );
		assertTrue( model.getNumberOfEntities() == 1 ) ;

		model.addEntity( new DomainEntity("Author") );
		assertTrue( model.getNumberOfEntities() == 2 ) ;

		model.addEntity( new DomainEntity("String") ); // "string" neutral type is case sensitive => No error
		
		DomainEntity book = model.getEntity("Book");
		assertNotNull(book);

		DomainEntity tmp = model.getEntity("NotDefined");
		assertNull(tmp);
		
		List<String> entityNames = model.getEntityNames();
		assertTrue(entityNames.size() == 3 ) ;		
		System.out.println("Entity names : ");
		for ( String name : entityNames ) {
			System.out.println(" . " + name);
		}
	}

//	@Test ( expected = DslParserException.class )
	@Test
	public void testEntityDuplicated() {
		DomainModel model = new DomainModel(MODEL_FILE_NAME, MODEL_PROPERTIES);
		model.addEntity( new DomainEntity("Book") );
		model.addEntity( new DomainEntity("Book") ); // ERROR expected
	}

	@Test
	public void testEntityWithNeutralTypeName() {
		DomainModel model = new DomainModel(MODEL_FILE_NAME, MODEL_PROPERTIES);
		model.addEntity( new DomainEntity("string") ); // ERROR expected
	}

}
