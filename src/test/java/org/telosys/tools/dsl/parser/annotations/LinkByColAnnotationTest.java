package org.telosys.tools.dsl.parser.annotations;

import java.util.List;

import org.junit.Test;
import org.telosys.tools.dsl.commons.ReferenceDefinitions;
import org.telosys.tools.generic.model.JoinColumn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LinkByColAnnotationTest {

	private List<JoinColumn> getJoinColumns(String paramValue) {
		LinkByColAnnotation a = new LinkByColAnnotation();
		return a.getJoinColumns(paramValue);
	}
	
	@Test (expected=RuntimeException.class)
	public void testErr01() {
		
		getJoinColumns((String)null) ;
//		assertTrue(jc.isEmpty());
		
//		rd = buildReferenceDefinitions("");
//		assertEquals(0, rd.count());
//
//		rd = buildReferenceDefinitions("    ");
//		assertEquals(0, rd.count());
//
//		rd = buildReferenceDefinitions(" , ,,,   ");
//		assertEquals(0, rd.count());
	}

	@Test (expected=RuntimeException.class)
	public void testErr02() {
		getJoinColumns("") ;
	}

	@Test
	public void test1JoinColumn() {
		// @LinkByCol(firstName)
		List<JoinColumn> jc = getJoinColumns("firstName") ;
		assertEquals(1, jc.size());
		assertEquals("firstName", jc.get(0).getName());
		assertEquals("", jc.get(0).getReferencedColumnName());
	}

	@Test
	public void test2JoinColumns() {
		// @LinkByCol(aaa, bbb)
		List<JoinColumn> jc = getJoinColumns("aaa,bbb") ; 
		assertEquals(2, jc.size());
		assertEquals("aaa", jc.get(0).getName());
		assertEquals("", jc.get(0).getReferencedColumnName());
		assertEquals("bbb", jc.get(1).getName());
		assertEquals("", jc.get(1).getReferencedColumnName());
	}
	
	@Test
	public void test2JoinColumnsWithSpaces() {
		// @LinkByCol(aaa, bbb)
		List<JoinColumn> jc = getJoinColumns("   aaa,  bbb ") ; 
		assertEquals(2, jc.size());
		assertEquals("aaa", jc.get(0).getName());
		assertEquals("", jc.get(0).getReferencedColumnName());
		assertEquals("bbb", jc.get(1).getName());
		assertEquals("", jc.get(1).getReferencedColumnName());
	}
	
	@Test
	public void test03() {
		// @LinkByCol(aaa, bbb)
		List<JoinColumn> jc = getJoinColumns("aaa ,    bbb  ,  ccc ") ; 
		assertEquals(3, jc.size());
		assertEquals("aaa", jc.get(0).getName());
		assertEquals("", jc.get(0).getReferencedColumnName());
		assertEquals("bbb", jc.get(1).getName());
		assertEquals("", jc.get(1).getReferencedColumnName());
		assertEquals("ccc", jc.get(2).getName());
		assertEquals("", jc.get(2).getReferencedColumnName());
	}
	
}
