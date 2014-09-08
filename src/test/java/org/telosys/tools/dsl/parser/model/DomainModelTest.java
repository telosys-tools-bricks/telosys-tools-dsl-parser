package org.telosys.tools.dsl.parser.model;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.telosys.tools.dsl.EntityParserException;

public class DomainModelTest {

	@Test
	public void testEntity() {
		DomainModel model = new DomainModel("mymodel");
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

	@Test ( expected = EntityParserException.class )
	public void testEntityDuplicated() {
		DomainModel model = new DomainModel("mymodel");
		model.addEntity( new DomainEntity("Book") );
		model.addEntity( new DomainEntity("Book") ); // ERROR expected
	}

	@Test ( expected = EntityParserException.class )
	public void testEntityWithNeutralTypeName() {
		DomainModel model = new DomainModel("mymodel");
		model.addEntity( new DomainEntity("string") ); // ERROR expected
	}

	@Test
	public void testEnumeration() {
		DomainModel model = new DomainModel("mymodel");
		assertTrue( model.getNumberOfEnumerations() == 0 ) ;
		
		model.addEnumeration( new DomainEnumerationForInteger("BookType") );
		assertTrue( model.getNumberOfEnumerations() == 1 ) ;

		model.addEnumeration( new DomainEnumerationForString("Country") );
		assertTrue( model.getNumberOfEnumerations() == 2 ) ;

		model.addEnumeration( new DomainEnumerationForString("String") ); // "string" neutral type is case sensitive => No error

		DomainEnumeration<?> country = model.getEnumeration("Country");
		assertNotNull(country);

		DomainEnumeration<?> bookType = model.getEnumeration("BookType");
		assertNotNull(bookType);

		DomainEnumeration<?> tmp = model.getEnumeration("NotDefined");
		assertNull(tmp);
		
		List<String> enumerationNames = model.getEnumerationNames();
		assertTrue(enumerationNames.size() == 3 ) ;		
		System.out.println("Enumeration names : ");
		for ( String name : enumerationNames ) {
			System.out.println(" . " + name);
		}
		
	}

	@Test ( expected = EntityParserException.class )
	public void testEnumerationDuplicated() {
		DomainModel model = new DomainModel("mymodel");		
		model.addEnumeration( new DomainEnumerationForInteger("BookType") );
		model.addEnumeration( new DomainEnumerationForInteger("BookType") ); // ERROR expected
	}

	@Test ( expected = EntityParserException.class )
	public void testEnumerationWithNeutralTypeName() {
		DomainModel model = new DomainModel("mymodel");		
		model.addEnumeration( new DomainEnumerationForString("string") ); // ERROR expected
	}
	
	@Test ( expected = EntityParserException.class )
	public void testEntityAndEnumerationDuplicated() {
		DomainModel model = new DomainModel("mymodel");
		model.addEntity( new DomainEntity("Book") );
		model.addEnumeration( new DomainEnumerationForString("Book") ); // ERROR expected
	}

}
