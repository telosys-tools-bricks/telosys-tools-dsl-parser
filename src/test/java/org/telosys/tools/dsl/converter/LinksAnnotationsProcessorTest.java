package org.telosys.tools.dsl.converter;

import java.util.List;

import org.junit.Test;
import org.telosys.tools.generic.model.JoinColumn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LinksAnnotationsProcessorTest {

	@Test
	public void test0() {
		LinksAnnotationsProcessor p = new LinksAnnotationsProcessor() ;
		List<JoinColumn> list ;
		
		list = p.buildJoinColumnsFromString(null);
		assertEquals(0, list.size());
		
		list = p.buildJoinColumnsFromString("");
		assertEquals(0, list.size());

		list = p.buildJoinColumnsFromString("    ");
		assertEquals(0, list.size());

		list = p.buildJoinColumnsFromString(" , ,,,   ");
		assertEquals(0, list.size());
	}

	@Test
	public void test1() {
		LinksAnnotationsProcessor p = new LinksAnnotationsProcessor() ;
		List<JoinColumn> list = p.buildJoinColumnsFromString(" firstName ");
		assertEquals(1, list.size());
		
		JoinColumn jc = list.get(0);
		assertEquals("firstName", jc.getName());
		// Default values :
		assertEquals("", jc.getReferencedColumnName());
		assertFalse(jc.isUnique());
		assertTrue(jc.isNullable());
		assertTrue(jc.isInsertable());
		assertTrue(jc.isUpdatable());
	}

	@Test
	public void test2() {
		LinksAnnotationsProcessor p = new LinksAnnotationsProcessor() ;
		List<JoinColumn> list = p.buildJoinColumnsFromString(" aaa, bbb ");
		assertEquals(2, list.size());
		assertEquals("aaa", list.get(0).getName());
		assertEquals("bbb", list.get(1).getName());
	}
	
}
