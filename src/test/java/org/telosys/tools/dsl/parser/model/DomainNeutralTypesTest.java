package org.telosys.tools.dsl.parser.model;

import java.util.List;

import org.junit.Test;
import org.telosys.tools.generic.model.types.NeutralType;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DomainNeutralTypesTest {

	@Test
	public void testExist() {
		assertTrue(DomainNeutralTypes.exists("string"));
		assertTrue(DomainNeutralTypes.exists("int"));

		// ver 4.3.0
		assertTrue(DomainNeutralTypes.exists("datetime"));
		assertTrue(DomainNeutralTypes.exists("datetimetz"));
		assertTrue(DomainNeutralTypes.exists("timetz"));
		assertTrue(DomainNeutralTypes.exists("uuid"));
	}

	@Test
	public void testNotExist() {
		assertFalse(DomainNeutralTypes.exists("xxx"));
		assertFalse(DomainNeutralTypes.exists("zzz"));
	}

	@Test
	public void testNames() {
		List<String> allNeutralTypes = NeutralType.getAllNeutralTypes();
		assertEquals( 17, allNeutralTypes.size());
		assertEquals( allNeutralTypes.size(), DomainNeutralTypes.size());
		
		// Check DomainNeutralType existence for each neutral type
		for ( String name : NeutralType.getAllNeutralTypes() ) {
			DomainNeutralType type = DomainNeutralTypes.getType(name);
			assertNotNull( type );
			assertTrue ( name.equals( type.getName() ) );
		}
	}

}
