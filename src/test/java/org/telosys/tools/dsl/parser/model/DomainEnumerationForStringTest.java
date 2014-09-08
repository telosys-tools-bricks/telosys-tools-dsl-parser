package org.telosys.tools.dsl.parser.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.telosys.tools.dsl.EntityParserException;

public class DomainEnumerationForStringTest {

	@Test
	public void test() {
		DomainEnumerationForString en = new DomainEnumerationForString("StudentType");
		
		assertTrue ( en.isEnumeration() );
		assertFalse ( en.isEntity() );
		assertFalse ( en.isNeutralType() );
		assertTrue( en.getNumberOfItems() == 0 ) ;
		
		en.addItem("FR", "fr");
		assertTrue( en.getNumberOfItems() == 1 ) ;
		
		en.addItem("UK", "uk");
		assertTrue( en.getNumberOfItems() == 2 ) ;
		
		assertEquals( en.getItemValue("FR"), "fr" ) ;
		assertEquals( en.getItemValue("UK"), "uk" ) ;
		
	}
	
	@Test ( expected = EntityParserException.class )
	public void testItemDuplicated1() {
		DomainEnumerationForString en = new DomainEnumerationForString("Country");
		en.addItem( "FR", "fr" );
		en.addItem( "FR", "fr" );
	}

}
