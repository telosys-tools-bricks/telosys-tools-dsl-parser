package org.telosys.tools.dsl.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DslModelForeignKeyTest {

	@Test
	public void testExplicitFK() {
		DslModelForeignKey fk = new DslModelForeignKey("FK_FOO", "Car", "Type");
		assertTrue(fk.isExplicit());
		assertEquals("FK_FOO", fk.getName());
		assertEquals("Car", fk.getOriginEntityName());
		assertEquals("Type", fk.getReferencedEntityName());
		
		assertEquals(0, fk.getAttributes().size());
		assertEquals(0, fk.getLinkAttributes().size());
		
		DslModelForeignKeyAttribute fkAttr = new DslModelForeignKeyAttribute(1, "typeId", "id");
		assertEquals("typeId", fkAttr.getOriginAttributeName());
		assertEquals("id", fkAttr.getReferencedAttributeName());
		
		fk.addAttribute(fkAttr);
		assertEquals(1, fk.getAttributes().size());
		assertEquals(1, fk.getLinkAttributes().size());			
	}
	
	@Test
	public void testImplicitFK() {
		DslModelForeignKey fk = new DslModelForeignKey("Car", "Type");
		assertFalse(fk.isExplicit());
		assertTrue( fk.getName().startsWith("FK_IMPLICIT") );
		assertTrue( fk.getName().endsWith("_Car_Type") );
		assertEquals("Car", fk.getOriginEntityName());
		assertEquals("Type", fk.getReferencedEntityName());
		assertEquals(0, fk.getAttributes().size());
		assertEquals(0, fk.getLinkAttributes().size());
	}
	
}
