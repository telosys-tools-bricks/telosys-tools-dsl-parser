package org.telosys.tools.dsl.parser.annotations;

import java.util.List;

import org.junit.Test;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.model.DslModelLink;
import org.telosys.tools.dsl.parser.commons.ParamError;
import org.telosys.tools.generic.model.JoinAttribute;

import static org.junit.Assert.assertEquals;

public class LinkByColAnnotationTest {

	private List<JoinAttribute> getJoinAttributes(String paramValue) throws ParamError {
		DslModelEntity entity = new DslModelEntity("Student");
		DslModelLink link = new DslModelLink("teacher");
		LinkByColAnnotation a = new LinkByColAnnotation();
		return a.getJoinAttributes(entity, link, paramValue);
	}
	
	@Test (expected=ParamError.class)
	public void testErr01() throws ParamError {
		
		getJoinAttributes((String)null) ;
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
		getJoinAttributes("") ;
	}

	@Test
	public void test1JoinAttribute() throws ParamError {
		// @LinkByCol(firstName)
		List<JoinAttribute> jc = getJoinAttributes("firstName") ;
		assertEquals(1, jc.size());
		assertEquals("firstName", jc.get(0).getOriginAttributeName());
		assertEquals("", jc.get(0).getReferencedAttributeName());
	}

	@Test
	public void test2JoinAttributes() throws ParamError {
		// @LinkByCol(aaa, bbb)
		List<JoinAttribute> jc = getJoinAttributes("aaa,bbb") ; 
		assertEquals(2, jc.size());
		assertEquals("aaa", jc.get(0).getOriginAttributeName());
		assertEquals("", jc.get(0).getReferencedAttributeName());
		assertEquals("bbb", jc.get(1).getOriginAttributeName());
		assertEquals("", jc.get(1).getReferencedAttributeName());
	}
	
	@Test
	public void test2JoinAttributesWithSpaces() throws ParamError {
		// @LinkByCol(aaa, bbb)
		List<JoinAttribute> jc = getJoinAttributes("   aaa,  bbb ") ; 
		assertEquals(2, jc.size());
		assertEquals("aaa", jc.get(0).getOriginAttributeName());
		assertEquals("", jc.get(0).getReferencedAttributeName());
		assertEquals("bbb", jc.get(1).getOriginAttributeName());
		assertEquals("", jc.get(1).getReferencedAttributeName());
	}
	
	@Test
	public void test03() throws ParamError {
		// @LinkByCol(aaa, bbb)
		List<JoinAttribute> jc = getJoinAttributes("aaa ,    bbb  ,  ccc ") ; 
		assertEquals(3, jc.size());
		assertEquals("aaa", jc.get(0).getOriginAttributeName());
		assertEquals("", jc.get(0).getReferencedAttributeName());
		assertEquals("bbb", jc.get(1).getOriginAttributeName());
		assertEquals("", jc.get(1).getReferencedAttributeName());
		assertEquals("ccc", jc.get(2).getOriginAttributeName());
		assertEquals("", jc.get(2).getReferencedAttributeName());
	}
	
}
