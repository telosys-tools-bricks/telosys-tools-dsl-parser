package org.telosys.tools.dsl.parser.annotations;

import java.util.List;

import org.junit.Test;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.model.DslModelLink;
import org.telosys.tools.dsl.parser.commons.ParamError;
import org.telosys.tools.generic.model.JoinColumn;

import static org.junit.Assert.assertEquals;

public class LinkByColAnnotationTest {

	private List<JoinColumn> getJoinColumns(String paramValue) throws ParamError {
		DslModelEntity entity = new DslModelEntity("Student");
		DslModelLink link = new DslModelLink("teacher");
		LinkByColAnnotation a = new LinkByColAnnotation();
		return a.getJoinColumns(entity, link, paramValue);
	}
	
	@Test (expected=ParamError.class)
	public void testErr01() throws ParamError {
		
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

	@Test (expected=ParamError.class)
	public void testErr02() throws ParamError {
		getJoinColumns("") ;
	}

	@Test
	public void test1JoinColumn() throws ParamError {
		// @LinkByCol(firstName)
		List<JoinColumn> jc = getJoinColumns("firstName") ;
		assertEquals(1, jc.size());
		assertEquals("firstName", jc.get(0).getName());
		assertEquals("", jc.get(0).getReferencedColumnName());
	}

	@Test
	public void test2JoinColumns() throws ParamError {
		// @LinkByCol(aaa, bbb)
		List<JoinColumn> jc = getJoinColumns("aaa,bbb") ; 
		assertEquals(2, jc.size());
		assertEquals("aaa", jc.get(0).getName());
		assertEquals("", jc.get(0).getReferencedColumnName());
		assertEquals("bbb", jc.get(1).getName());
		assertEquals("", jc.get(1).getReferencedColumnName());
	}
	
	@Test
	public void test2JoinColumnsWithSpaces() throws ParamError {
		// @LinkByCol(aaa, bbb)
		List<JoinColumn> jc = getJoinColumns("   aaa,  bbb ") ; 
		assertEquals(2, jc.size());
		assertEquals("aaa", jc.get(0).getName());
		assertEquals("", jc.get(0).getReferencedColumnName());
		assertEquals("bbb", jc.get(1).getName());
		assertEquals("", jc.get(1).getReferencedColumnName());
	}
	
	@Test
	public void test03() throws ParamError {
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
