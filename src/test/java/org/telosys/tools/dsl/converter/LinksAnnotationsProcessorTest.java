package org.telosys.tools.dsl.converter;

import org.junit.Test;
import org.telosys.tools.dsl.model.DslModel;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;

import static org.junit.Assert.assertEquals;

public class LinksAnnotationsProcessorTest {

	private static final DslModel VOID_MODEL = new DslModel("test.model");
	
	@Test
	public void test0() {
		LinksAnnotationsProcessor p = new LinksAnnotationsProcessor(VOID_MODEL) ;
		ReferenceDefinitions rd;
		
		rd = p.buildReferenceDefinitions((String)null);
		assertEquals(0, rd.count());
		
		rd = p.buildReferenceDefinitions("");
		assertEquals(0, rd.count());

		rd = p.buildReferenceDefinitions("    ");
		assertEquals(0, rd.count());

		rd = p.buildReferenceDefinitions(" , ,,,   ");
		assertEquals(0, rd.count());
	}

	@Test
	public void test1() {
		LinksAnnotationsProcessor p = new LinksAnnotationsProcessor(VOID_MODEL) ;
		ReferenceDefinitions rd = p.buildReferenceDefinitions(" firstName ");
		assertEquals(1, rd.count());
		assertEquals("firstName", rd.get(0).getName());
		assertEquals("", rd.get(0).getReferencedName());
	}

	@Test
	public void test2() {
		LinksAnnotationsProcessor p = new LinksAnnotationsProcessor(VOID_MODEL) ;
		ReferenceDefinitions rd = p.buildReferenceDefinitions(" aa > refAA ,  bb>refBB ");
		assertEquals(2, rd.count());
		assertEquals("aa", rd.get(0).getName());
		assertEquals("refAA", rd.get(0).getReferencedName());
		assertEquals("bb", rd.get(1).getName());
		assertEquals("refBB", rd.get(1).getReferencedName());
	}
	
	@Test
	public void test3() {
		LinksAnnotationsProcessor p = new LinksAnnotationsProcessor(VOID_MODEL) ;
		ReferenceDefinitions rd = p.buildReferenceDefinitions("aa>refAA,bb>refBB,  cc   > refCC  ");
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
		LinksAnnotationsProcessor p = new LinksAnnotationsProcessor(VOID_MODEL) ;
		DomainAnnotation annotation = new DomainAnnotation("LinkByAttr", " aa > refAA ,  bb>refBB ");
		ReferenceDefinitions rd = p.buildReferenceDefinitions(annotation);
		assertEquals(2, rd.count());
		assertEquals("aa", rd.get(0).getName());
		assertEquals("refAA", rd.get(0).getReferencedName());
		assertEquals("bb", rd.get(1).getName());
		assertEquals("refBB", rd.get(1).getReferencedName());
	}
	
	@Test
	public void testAnnotOK1() {
		LinksAnnotationsProcessor p = new LinksAnnotationsProcessor(VOID_MODEL) ;
		DomainAnnotation annotation = new DomainAnnotation("LinkByAttr", "aa ");
		ReferenceDefinitions rd = p.buildReferenceDefinitions(annotation);
		assertEquals(1, rd.count());
		assertEquals("aa", rd.get(0).getName());
		assertEquals("", rd.get(0).getReferencedName());
	}
	
	@Test
	public void testAnnotOK2() {
		LinksAnnotationsProcessor p = new LinksAnnotationsProcessor(VOID_MODEL) ;
		DomainAnnotation annotation = new DomainAnnotation("LinkByAttr", "aa > refAA ,  bb>refBB ");
		ReferenceDefinitions rd = p.buildReferenceDefinitions(annotation);
		assertEquals(2, rd.count());
		assertEquals("aa", rd.get(0).getName());
		assertEquals("refAA", rd.get(0).getReferencedName());
		assertEquals("bb", rd.get(1).getName());
		assertEquals("refBB", rd.get(1).getReferencedName());
	}
	
	@Test (expected=RuntimeException.class)
	public void testAnnotErr1() {
		LinksAnnotationsProcessor p = new LinksAnnotationsProcessor(VOID_MODEL) ;
		DomainAnnotation annotation = new DomainAnnotation("LinkByAttr", " ");
		p.buildReferenceDefinitions(annotation);
	}
	
	@Test (expected=RuntimeException.class)
	public void testAnnotErr2() {
		LinksAnnotationsProcessor p = new LinksAnnotationsProcessor(VOID_MODEL) ;
		DomainAnnotation annotation = new DomainAnnotation("LinkByAttr", " aa  ,  bb ");
		p.buildReferenceDefinitions(annotation);
	}
	
	@Test (expected=RuntimeException.class)
	public void testAnnotErr3() {
		LinksAnnotationsProcessor p = new LinksAnnotationsProcessor(VOID_MODEL) ;
		DomainAnnotation annotation = new DomainAnnotation("LinkByAttr", " aa  ,  bb > RBB ");
		p.buildReferenceDefinitions(annotation);
	}
	
	@Test (expected=RuntimeException.class)
	public void testAnnotErr4() {
		LinksAnnotationsProcessor p = new LinksAnnotationsProcessor(VOID_MODEL) ;
		DomainAnnotation annotation = new DomainAnnotation("LinkByAttr", "  > refAA ");
		p.buildReferenceDefinitions(annotation);
	}
	
}
