package org.telosys.tools.dsl.parser.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

public class DomainNeutralTypesTest {

	@Test
	public void testExist() {
		assertTrue(DomainNeutralTypes.exists("string"));
		assertTrue(DomainNeutralTypes.exists("integer"));
	}

	@Test
	public void testNotExist() {
		assertFalse(DomainNeutralTypes.exists("xxx"));
		assertFalse(DomainNeutralTypes.exists("zzz"));
	}

	@Test
	public void testList() {
		List<String> list = DomainNeutralTypes.getSortedNames();
		assertTrue( list.size() > 0 );
		System.out.println("All neutral type names ( " + list.size() + " ) : ");
		for ( String s : list ) {
			System.out.println(" . " + s );
		}
		String name0 = list.get(0);
		assertTrue( DomainNeutralTypes.exists(name0) );
	}

	@Test
	public void testNames() {
		
		List<String> list = DomainNeutralTypes.getSortedNames();
		System.out.println("Checking " + list.size() + " neutral type names : ");
		for ( String name : list ) {
			System.out.println(" test type '" + name + "'" );
			DomainNeutralType type = DomainNeutralTypes.getType(name);
			assertNotNull( type );
			assertTrue ( name.equals( type.getName() ) );
		}
		
	}

}
