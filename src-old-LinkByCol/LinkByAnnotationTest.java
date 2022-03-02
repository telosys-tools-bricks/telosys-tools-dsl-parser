package org.telosys.tools.dsl.parser.annotations;

import org.junit.Test;
import org.telosys.tools.dsl.commons.ReferenceDefinitions;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.model.DslModelLink;
import org.telosys.tools.dsl.parser.commons.ParamError;

import static org.junit.Assert.assertEquals;

public class LinkByAnnotationTest {

	
	private LinkByAnnotation getLinkByAnnotation() {
		// abstract class => use any class extending LinkByAnnotation
		return new LinkByAttrAnnotation() ;
	}
	
	/**
	 * Just build ReferenceDefinitions (no consistency check, no exception)
	 * @param s
	 * @return
	 */
	private ReferenceDefinitions buildReferenceDefinitions(String s) {
		LinkByAnnotation a = getLinkByAnnotation() ;
		return a.buildReferenceDefinitions(s);
	}

	/**
	 * Get and check ReferenceDefinitions (exception if void)
	 * @param s
	 * @return
	 * @throws ParamError 
	 */
	private ReferenceDefinitions getReferenceDefinitionsNotVoid(String s) throws ParamError {
		DslModelEntity entity = new DslModelEntity("Student");
		DslModelLink link = new DslModelLink("teacher");
		LinkByAnnotation a = getLinkByAnnotation() ;
		ReferenceDefinitions rd = a.buildReferenceDefinitions(s);
		a.checkNotVoid(entity, link, rd);
		return rd ;
	}

	/**
	 * Get and check ReferenceDefinitions (exception if invalid)
	 * @param s
	 * @return
	 */
	private ReferenceDefinitions getReferenceDefinitionsMultiRef(String s) throws ParamError  {
		DslModelEntity entity = new DslModelEntity("Student");
		DslModelLink link = new DslModelLink("teacher");
		LinkByAnnotation a = getLinkByAnnotation() ;
		ReferenceDefinitions rd = a.buildReferenceDefinitions(s);
		a.checkNotVoid(entity, link, rd);
		a.checkReferencedNames(entity, link, rd);
		return rd ;
	}

	@Test
	public void test0() {
		ReferenceDefinitions rd ;
		
		rd = buildReferenceDefinitions((String)null);
		assertEquals(0, rd.count());
		
		rd = buildReferenceDefinitions("");
		assertEquals(0, rd.count());

		rd = buildReferenceDefinitions("    ");
		assertEquals(0, rd.count());

		rd = buildReferenceDefinitions(" , ,,,   ");
		assertEquals(0, rd.count());
	}

	@Test
	public void test1() {
		ReferenceDefinitions rd = buildReferenceDefinitions(" firstName ");
		assertEquals(1, rd.count());
		assertEquals("firstName", rd.get(0).getName());
		assertEquals("", rd.get(0).getReferencedName());
	}

	@Test
	public void test2() {
		ReferenceDefinitions rd = buildReferenceDefinitions(" aa > refAA ,  bb>refBB ");
		assertEquals(2, rd.count());
		assertEquals("aa", rd.get(0).getName());
		assertEquals("refAA", rd.get(0).getReferencedName());
		assertEquals("bb", rd.get(1).getName());
		assertEquals("refBB", rd.get(1).getReferencedName());
	}
	
	@Test
	public void test3() {
		ReferenceDefinitions rd = buildReferenceDefinitions("aa>refAA,bb>refBB,  cc   > refCC  ");
		assertEquals(3, rd.count());
		assertEquals("aa", rd.get(0).getName());
		assertEquals("refAA", rd.get(0).getReferencedName());
		assertEquals("bb", rd.get(1).getName());
		assertEquals("refBB", rd.get(1).getReferencedName());
		assertEquals("cc", rd.get(2).getName());
		assertEquals("refCC", rd.get(2).getReferencedName());
	}
	
	@Test
	public void test4() {
		ReferenceDefinitions rd = buildReferenceDefinitions(" aa > refAA ,  bb>refBB ");
		assertEquals(2, rd.count());
		assertEquals("aa", rd.get(0).getName());
		assertEquals("refAA", rd.get(0).getReferencedName());
		assertEquals("bb", rd.get(1).getName());
		assertEquals("refBB", rd.get(1).getReferencedName());
	}
	
	@Test
	public void test5() throws ParamError {
//		DomainAnnotation annotation = new DomainAnnotation("LinkByAttr", "aa ");
//		ReferenceDefinitions rd = p.buildReferenceDefinitions(annotation);
		ReferenceDefinitions rd = getReferenceDefinitionsNotVoid("aa ");

		assertEquals(1, rd.count());
		assertEquals("aa", rd.get(0).getName());
		assertEquals("", rd.get(0).getReferencedName());
	}
	
	@Test
	public void test6() throws ParamError {
//		DomainAnnotation annotation = new DomainAnnotation("LinkByAttr", "aa > refAA ,  bb>refBB ");
//		ReferenceDefinitions rd = p.buildReferenceDefinitions(annotation);
		ReferenceDefinitions rd = getReferenceDefinitionsNotVoid("aa > refAA ,  bb>refBB ");

		assertEquals(2, rd.count());
		assertEquals("aa", rd.get(0).getName());
		assertEquals("refAA", rd.get(0).getReferencedName());
		assertEquals("bb", rd.get(1).getName());
		assertEquals("refBB", rd.get(1).getReferencedName());
	}
	
	//----------------------------------------------------------------------

	@Test (expected=ParamError.class)
	public void testErrNotVoid1() throws ParamError {
//		LinkByAnnotation p = getLinkByAnnotation();
//		DomainAnnotation annotation = new DomainAnnotation("LinkByAttr", " ");
//		p.buildReferenceDefinitions(annotation);
		getReferenceDefinitionsNotVoid(" ");
	}
	
	@Test (expected=ParamError.class)
	public void testErrNotVoid2() throws ParamError {
//		LinksAnnotationsProcessor p = new LinksAnnotationsProcessor(VOID_MODEL) ;
//		DomainAnnotation annotation = new DomainAnnotation("LinkByAttr", "  > refAA ");
//		p.buildReferenceDefinitions(annotation);
		getReferenceDefinitionsNotVoid("  > refAA "); // "no reference definition"
	}
	
	@Test (expected=ParamError.class)
	public void testErrNotVoid3() throws ParamError {
		getReferenceDefinitionsNotVoid(null);
	}

	//----------------------------------------------------------------------
	
	@Test (expected=ParamError.class)
	public void testErrMultiRef1() throws ParamError {
		getReferenceDefinitionsMultiRef(" aa  ,  bb ");
	}
	
	@Test (expected=ParamError.class)
	public void testErrMultiRef2() throws ParamError {
		getReferenceDefinitionsMultiRef(" aa  ,  bb > RBB ");
	}
	
}
